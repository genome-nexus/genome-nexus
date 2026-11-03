"""AlphaGenome scoring pipeline for POST /score.

Wraps ``dna_client.score_variant`` over a parameterized window (default 1 Mb)
with the four recommended scorers, runs ``tidy_scores`` locally, filters rows
to the canonical gene, and assembles the ``alpha_genome`` payload from
``workstreams/contracts/alpha_genome_schema.json``.
"""

import math
import os

import pandas as pd
from alphagenome.data import genome
from alphagenome.models import dna_client
from alphagenome.models.v1 import dna_output
from alphagenome.models.v1 import variant_scorers as variant_scorers_module
from alphagenome.models.v1.variant_scorers import tidy_scores

from app.logger import log_debug
from app.schemas import ScoreRequest

SEQUENCE_LENGTH_1MB = 1048576
DEFAULT_MODEL_VERSION = "alphagenome-v1"
API_KEY_ENV_VAR = "ALPHA_GENOME_API_KEY"

RNA_SEQ = "RNA_SEQ"
SPLICE_SITES = "SPLICE_SITES"
SPLICE_SITE_USAGE = "SPLICE_SITE_USAGE"
SPLICE_JUNCTIONS = "SPLICE_JUNCTIONS"
MERGED_SPLICING_SCORERS = (SPLICE_SITES, SPLICE_SITE_USAGE, SPLICE_JUNCTIONS)


class UpstreamUnavailableError(RuntimeError):
    """AlphaGenome API is unreachable, unconfigured, or returned an error"""


class RateLimitedError(RuntimeError):
    """AlphaGenome API rate limit exceeded"""


def build_variant(request: ScoreRequest) -> genome.Variant:
    return genome.Variant(
        chromosome=request.chromosome,
        position=request.position,
        reference_bases=request.reference_allele,
        alternate_bases=request.alternate_allele,
    )


def build_interval(variant: genome.Variant, sequence_length: int = SEQUENCE_LENGTH_1MB) -> genome.Interval:
    """Window of ``sequence_length`` centered on the variant.

    Uses the SDK's ``reference_interval.resize`` idiom. NOTE: ``resize``
    centers without clamping, so the start<0 right-shift guard is preserved
    here: a telomeric variant with a wide window would otherwise send a
    negative start to the API. Future iteration: replace the manual shift
    with a chromosome-bounds-aware clamp once contig lengths are available
    in the sidecar
    """
    interval = variant.reference_interval.resize(sequence_length)
    if interval.start < 0:
        interval = genome.Interval(
            interval.chromosome, 0, sequence_length
        )
    return interval


def build_scorers(names: list[str] | None = None) -> list:
    if names is None:
        return [
            variant_scorers_module.GeneMaskLFCScorer(
                requested_output=dna_output.OutputType.RNA_SEQ
            ),
            variant_scorers_module.GeneMaskSplicingScorer(
                requested_output=dna_output.OutputType.SPLICE_SITES, width=None
            ),
            variant_scorers_module.GeneMaskSplicingScorer(
                requested_output=dna_output.OutputType.SPLICE_SITE_USAGE, width=None
            ),
            variant_scorers_module.SpliceJunctionScorer(),
        ]
    scorers = []
    for name in names:
        try:
            scorers.append(variant_scorers_module.RECOMMENDED_VARIANT_SCORERS[name])
        except KeyError:
            raise ValueError(f"Unknown scorer: {name}") from None
    return scorers


def create_client(api_key: str | None = None) -> dna_client.DnaClient:
    key = api_key or os.environ.get(API_KEY_ENV_VAR)
    if not key:
        log_debug("PHASE 4: API KEY CHECK", f"{API_KEY_ENV_VAR} is NOT set in environment!")
        raise UpstreamUnavailableError(f"{API_KEY_ENV_VAR} is not configured")
    log_debug("PHASE 4: API KEY CHECK", f"{API_KEY_ENV_VAR} is configured (prefix={key[:8]}..., length={len(key)})")
    return dna_client.create(key)


def score_with_client(
    request: ScoreRequest, client: dna_client.DnaClient
) -> dict:
    """Run the full pipeline with an injected client (no env access)"""
    variant = build_variant(request)
    interval = build_interval(variant, request.sequence_length)
    log_debug(
        "PHASE 4: SIDECAR EXECUTION",
        f"Built genome.Variant(chrom='{variant.chromosome}', pos={variant.position}, ref='{variant.reference_bases}', alt='{variant.alternate_bases}')",
    )
    log_debug(
        "PHASE 4: SIDECAR EXECUTION",
        f"Built interval: {interval.chromosome}:{interval.start}-{interval.end} (width={interval.end - interval.start})",
    )
    scorers = build_scorers(request.scorers)
    scorer_names = (
        ["RNA_SEQ", "SPLICE_SITES", "SPLICE_SITE_USAGE", "SPLICE_JUNCTIONS"]
        if request.scorers is None
        else list(request.scorers)
    )
    log_debug(
        "PHASE 4: SIDECAR EXECUTION",
        f"Executing client.score_variant with {len(scorers)} scorers: {scorer_names}...",
    )

    try:
        scores = client.score_variant(interval, variant, scorers)
        log_debug(
            "PHASE 4: SIDECAR EXECUTION",
            f"client.score_variant returned {len(scores)} AnnData score objects",
        )
    except (RateLimitedError, UpstreamUnavailableError) as exc:
        log_debug("PHASE 4: SIDECAR ERROR", f"Upstream error during score_variant: {exc}")
        raise
    except Exception as exc:
        mapped = _map_client_error(exc)
        log_debug("PHASE 4: SIDECAR ERROR", f"Exception during score_variant: {type(exc).__name__}: {exc} -> mapped to {type(mapped).__name__}")
        raise mapped from exc

    tidy = tidy_scores(scores, match_gene_strand=True)
    total_tidy_rows = len(tidy) if tidy is not None else 0
    log_debug(
        "PHASE 4: SIDECAR EXECUTION",
        f"tidy_scores produced {total_tidy_rows} total rows across all genes in window",
    )

    filtered = _filter_to_canonical_gene(
        tidy,
        request.canonical_gene_id,
        request.canonical_gene,
        request.ontology_curies,
    )
    filtered_rows = len(filtered) if filtered is not None else 0
    log_debug(
        "PHASE 4: SIDECAR EXECUTION",
        f"Filtered to canonical gene (gene_id='{request.canonical_gene_id}', name='{request.canonical_gene}'): "
        f"{filtered_rows} matching rows retained out of {total_tidy_rows}",
    )

    rows = [_row_to_dict(row) for _, row in filtered.iterrows()]
    rows.sort(key=_row_sort_key, reverse=True)
    score_level = _overall_score_level(rows)
    merged_splicing = _merged_splicing_score(filtered)
    log_debug(
        "PHASE 4: SIDECAR EXECUTION",
        f"Computed merged splicing score: {merged_splicing}, score_level: {score_level}",
    )

    return {
        "variant": request.variant,
        "hg38_variant": f"{request.chromosome}:{request.position}"
        f":{request.reference_allele}>{request.alternate_allele}",
        "scored_interval": f"{interval.chromosome}:{interval.start}-{interval.end}",
        "model_version": DEFAULT_MODEL_VERSION,
        "canonical_gene_id": request.canonical_gene_id,
        "canonical_gene": request.canonical_gene,
        "merged_splicing_score": merged_splicing,
        "score_level": score_level,
        "scores": rows,
    }


def _filter_to_canonical_gene(
    tidy: pd.DataFrame | None,
    gene_id: str | None,
    gene_name: str,
    ontology_curies: list[str] | None = None,
) -> pd.DataFrame:
    if tidy is None or tidy.empty:
        return pd.DataFrame()
    matches_name = tidy["gene_name"] == gene_name
    if gene_id:
        gene_id = gene_id.split(".")[0]
        matches = (tidy["gene_id"] == gene_id) | matches_name
    else:
        matches = matches_name
    filtered = tidy[matches]
    if ontology_curies:
        tissue_matched = filtered["ontology_curie"].isin(ontology_curies)
        tissue_agnostic = filtered["output_type"] == SPLICE_SITES
        filtered = filtered[tissue_matched | tissue_agnostic]
    return filtered.reset_index(drop=True)


def _merged_splicing_score(filtered: pd.DataFrame) -> float | None:
    """max(SPLICE_SITES) + max(SPLICE_SITE_USAGE) + max(SPLICE_JUNCTIONS) / 5"""
    if filtered.empty or "output_type" not in filtered.columns:
        return None
    maxima = (
        filtered.groupby("output_type")["raw_score"].max(numeric_only=True).to_dict()
    )
    if not any(key in maxima for key in MERGED_SPLICING_SCORERS):
        return None
    return float(
        maxima.get(SPLICE_SITES, 0.0)
        + maxima.get(SPLICE_SITE_USAGE, 0.0)
        + maxima.get(SPLICE_JUNCTIONS, 0.0) / 5.0
    )


def _row_sort_key(row: dict) -> tuple[float, float]:
    """Order rows from most impacted to least impacted by |raw_score|"""
    raw = row.get("raw_score")
    quantile = row.get("quantile_score")
    return (
        abs(raw) if raw is not None else -1.0,
        abs(quantile) if quantile is not None else -1.0,
    )


def _row_score_level(
    output_type: str | None,
    raw_score: float | None,
    quantile_score: float | None,
) -> str | None:
    """Dual-gate evaluation: |quantile| >= 0.99 plus output-specific raw threshold"""
    if raw_score is None or quantile_score is None:
        return None
    if abs(quantile_score) < 0.99:
        return None
    if output_type == RNA_SEQ:
        if abs(raw_score) >= 0.5:
            return "strong"
        if abs(raw_score) >= 0.1:
            return "moderate"
        return None
    if raw_score >= 0.5:
        return "strong"
    if raw_score >= 0.2:
        return "moderate"
    return None


def _overall_score_level(rows: list[dict]) -> str | None:
    """Highest dual-gate badge level ('strong' > 'moderate' > None) across rows"""
    best: str | None = None
    for row in rows:
        level = _row_score_level(
            row.get("output_type"),
            row.get("raw_score"),
            row.get("quantile_score"),
        )
        if level == "strong":
            return "strong"
        if level == "moderate":
            best = "moderate"
    return best



def _row_to_dict(row: pd.Series) -> dict:
    def clean(value):
        if value is None:
            return None
        if isinstance(value, float) and math.isnan(value):
            return None
        if isinstance(value, str) and value in ("", "nan", "NaN", "None"):
            return None
        return value

    def number(value):
        value = clean(value)
        return None if value is None else float(value)

    def integer(value):
        value = clean(value)
        return None if value is None else int(value)

    raw = row.to_dict()
    return {
        "output_type": raw.get("output_type"),
        "variant_scorer": raw.get("variant_scorer"),
        "gene_id": clean(raw.get("gene_id")),
        "gene_name": clean(raw.get("gene_name")),
        "junction_start": integer(
            raw.get("junction_start", raw.get("junction_Start"))
        ),
        "junction_end": integer(raw.get("junction_end", raw.get("junction_End"))),
        "track_name": clean(raw.get("track_name")),
        "ontology_curie": clean(raw.get("ontology_curie")),
        "biosample_name": clean(raw.get("biosample_name")),
        "raw_score": number(raw.get("raw_score")),
        "quantile_score": number(raw.get("quantile_score")),
    }


def _map_client_error(exc: Exception) -> RuntimeError:
    try:
        import grpc

        if isinstance(exc, grpc.RpcError):
            if exc.code() == grpc.StatusCode.RESOURCE_EXHAUSTED:
                return RateLimitedError(str(exc))
    except ImportError:
        pass
    return UpstreamUnavailableError(str(exc))

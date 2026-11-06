"""Unit tests for POST /score (no network, no API key)."""

import pandas as pd
import pytest
from fastapi.testclient import TestClient

from app import scoring
from app.main import app, get_client


@pytest.fixture
def canned_tidy():
    return pd.DataFrame(
        [
            {
                "gene_id": "ENSG00000157764",
                "gene_name": "BRAF",
                "output_type": "RNA_SEQ",
                "variant_scorer": "GeneMaskLFCScorer",
                "track_name": "pancreas_rna_seq_plus",
                "ontology_curie": "UBERON:0001264",
                "biosample_name": "pancreas",
                "raw_score": 0.612,
                "quantile_score": 0.994,
            },
            {
                "gene_id": "ENSG00000157764",
                "gene_name": "BRAF",
                "output_type": "SPLICE_SITES",
                "variant_scorer": "GeneMaskSplicingScorer",
                "track_name": "donor_plus",
                "ontology_curie": None,
                "biosample_name": None,
                "raw_score": 0.08,
                "quantile_score": 0.62,
            },
            {
                "gene_id": "ENSG00000157764",
                "gene_name": "BRAF",
                "output_type": "SPLICE_SITE_USAGE",
                "variant_scorer": "GeneMaskSplicingScorer",
                "track_name": "acceptor_usage",
                "ontology_curie": "UBERON:0001264",
                "biosample_name": "pancreas",
                "raw_score": 0.05,
                "quantile_score": 0.5,
            },
            {
                "gene_id": "ENSG00000157764",
                "gene_name": "BRAF",
                "output_type": "SPLICE_JUNCTIONS",
                "variant_scorer": "SpliceJunctionScorer",
                "track_name": "junction",
                "ontology_curie": "UBERON:0001264",
                "biosample_name": "pancreas",
                "junction_Start": 140753300,
                "junction_End": 140753400,
                "raw_score": 0.10,
                "quantile_score": 0.7,
            },
            {
                "gene_id": "ENSG00000000000",
                "gene_name": "OTHER",
                "output_type": "RNA_SEQ",
                "variant_scorer": "GeneMaskLFCScorer",
                "track_name": "pancreas_rna_seq_plus",
                "ontology_curie": "UBERON:0001264",
                "biosample_name": "pancreas",
                "raw_score": 9.99,
                "quantile_score": 1.0,
            },
        ]
    )


class FakeClient:
    def __init__(self, error=None):
        self.error = error
        self.calls = []

    def score_variant(self, interval, variant, scorers):
        self.calls.append((interval, variant, scorers))
        if self.error is not None:
            raise self.error
        return ["SCORES"]


def make_client(monkeypatch, canned, error=None):
    fake = FakeClient(error=error)
    monkeypatch.setattr(scoring, "tidy_scores", lambda scores, match_gene_strand=True: canned)
    app.dependency_overrides[get_client] = lambda: fake
    return fake


@pytest.fixture(autouse=True)
def _client_override():
    """Every TestClient test gets a dependency-injected client, so request
    validation (422s) is exercised without needing an API key."""
    app.dependency_overrides[get_client] = FakeClient
    yield
    app.dependency_overrides.clear()


@pytest.fixture
def braf_payload():
    return {
        "variant": "7:g.140453136A>T",
        "chromosome": "chr7",
        "position": 140753336,
        "reference_allele": "A",
        "alternate_allele": "T",
        "canonical_gene_id": "ENSG00000157764",
        "canonical_gene": "BRAF",
    }


def test_score_filters_to_canonical_gene(monkeypatch, canned_tidy, braf_payload):
    make_client(monkeypatch, canned_tidy)
    try:
        response = TestClient(app).post("/score", json=braf_payload)
    finally:
        app.dependency_overrides.clear()
    assert response.status_code == 200
    body = response.json()
    assert body["variant"] == "7:g.140453136A>T"
    assert body["hg38_variant"] == "chr7:140753336:A>T"
    assert body["scored_interval"] == "chr7:140229048-141277624"
    assert body["model_version"] == "alphagenome-v1"
    assert body["canonical_gene_id"] == "ENSG00000157764"
    assert body["canonical_gene"] == "BRAF"
    gene_names = {row["gene_name"] for row in body["scores"]}
    assert gene_names == {"BRAF"}
    by_type = {row["output_type"]: row for row in body["scores"]}
    assert by_type["SPLICE_JUNCTIONS"]["junction_start"] == 140753300
    assert by_type["SPLICE_JUNCTIONS"]["junction_end"] == 140753400
    assert by_type["SPLICE_SITES"]["ontology_curie"] is None
    assert body["merged_splicing_score"] == pytest.approx(0.08 + 0.05 + 0.10 / 5.0)


def test_score_matches_gene_name_without_gene_id(monkeypatch, canned_tidy, braf_payload):
    make_client(monkeypatch, canned_tidy)
    braf_payload.pop("canonical_gene_id")
    try:
        response = TestClient(app).post("/score", json=braf_payload)
    finally:
        app.dependency_overrides.clear()
    assert response.status_code == 200
    assert {row["gene_name"] for row in response.json()["scores"]} == {"BRAF"}


def test_merged_splicing_score_none_without_splicing_rows(monkeypatch, braf_payload):
    tidy = pd.DataFrame(
        [
            {
                "gene_id": "ENSG00000157764",
                "gene_name": "BRAF",
                "output_type": "RNA_SEQ",
                "variant_scorer": "GeneMaskLFCScorer",
                "track_name": "pancreas_rna_seq_plus",
                "raw_score": 0.612,
                "quantile_score": 0.994,
            }
        ]
    )
    make_client(monkeypatch, tidy)
    try:
        response = TestClient(app).post("/score", json=braf_payload)
    finally:
        app.dependency_overrides.clear()
    assert response.status_code == 200
    body = response.json()
    assert body["merged_splicing_score"] is None
    assert len(body["scores"]) == 1


def test_upstream_error_maps_to_503(monkeypatch, canned_tidy, braf_payload):
    make_client(monkeypatch, canned_tidy, error=RuntimeError("boom"))
    try:
        response = TestClient(app).post("/score", json=braf_payload)
    finally:
        app.dependency_overrides.clear()
    assert response.status_code == 503


def test_rate_limit_maps_to_429(monkeypatch, canned_tidy, braf_payload):
    make_client(monkeypatch, canned_tidy, error=scoring.RateLimitedError("slow down"))
    try:
        response = TestClient(app).post("/score", json=braf_payload)
    finally:
        app.dependency_overrides.clear()
    assert response.status_code == 429


@pytest.mark.parametrize(
    "field,value",
    [
        ("chromosome", "7"),
        ("chromosome", "chrM"),
        ("position", 0),
        ("reference_allele", "a"),
        ("reference_allele", "N"),
        ("alternate_allele", "-"),
    ],
)
def test_invalid_payload_returns_422(field, value, braf_payload):
    braf_payload[field] = value
    response = TestClient(app).post("/score", json=braf_payload)
    assert response.status_code == 422


def test_missing_canonical_gene_returns_422(braf_payload):
    braf_payload.pop("canonical_gene")
    response = TestClient(app).post("/score", json=braf_payload)
    assert response.status_code == 422


def test_missing_api_key_fails_fast(monkeypatch):
    monkeypatch.delenv(scoring.API_KEY_ENV_VAR, raising=False)
    with pytest.raises(scoring.UpstreamUnavailableError):
        scoring.create_client()


def test_interval_shifts_when_window_starts_below_zero():
    from alphagenome.data import genome

    variant = genome.Variant(
        chromosome="chr1", position=100, reference_bases="A", alternate_bases="T"
    )
    interval = scoring.build_interval(variant)
    assert interval.start == 0
    assert interval.end == scoring.SEQUENCE_LENGTH_1MB


def test_scorers_cover_four_recommended_types():
    scorers = scoring.build_scorers()
    assert len(scorers) == 4


def test_score_default_backward_compatible(monkeypatch, canned_tidy, braf_payload):
    fake = make_client(monkeypatch, canned_tidy)
    try:
        response = TestClient(app).post("/score", json=braf_payload)
    finally:
        app.dependency_overrides.clear()
    assert response.status_code == 200
    body = response.json()
    assert body["scored_interval"] == "chr7:140229048-141277624"
    gene_names = {row["gene_name"] for row in body["scores"]}
    assert gene_names == {"BRAF"}
    assert {row["output_type"] for row in body["scores"]} == {
        "RNA_SEQ",
        "SPLICE_SITES",
        "SPLICE_SITE_USAGE",
        "SPLICE_JUNCTIONS",
    }
    interval, _, scorers = fake.calls[0]
    assert interval.end - interval.start == 1048576
    assert len(scorers) == 4


def test_score_sequence_length(monkeypatch, canned_tidy, braf_payload):
    fake = make_client(monkeypatch, canned_tidy)
    braf_payload["sequence_length"] = 16384
    try:
        response = TestClient(app).post("/score", json=braf_payload)
    finally:
        app.dependency_overrides.clear()
    assert response.status_code == 200
    interval, _, _ = fake.calls[0]
    assert interval.end - interval.start == 16384
    assert response.json()["scored_interval"] == "chr7:140745144-140761528"


def test_score_rejects_unsupported_sequence_length(braf_payload):
    braf_payload["sequence_length"] = 1000
    response = TestClient(app).post("/score", json=braf_payload)
    assert response.status_code == 422


def test_score_ontology_curie_filter(monkeypatch, canned_tidy, braf_payload):
    make_client(monkeypatch, canned_tidy)
    braf_payload["ontology_curies"] = ["UBERON:0009999"]
    try:
        response = TestClient(app).post("/score", json=braf_payload)
    finally:
        app.dependency_overrides.clear()
    assert response.status_code == 200
    # Only the tissue-agnostic SPLICE_SITES row survives a non-matching curie.
    assert {row["output_type"] for row in response.json()["scores"]} == {"SPLICE_SITES"}


def test_score_ontology_curie_filter_keeps_matching_tissue(
    monkeypatch, canned_tidy, braf_payload
):
    make_client(monkeypatch, canned_tidy)
    braf_payload["ontology_curies"] = ["UBERON:0001264"]
    try:
        response = TestClient(app).post("/score", json=braf_payload)
    finally:
        app.dependency_overrides.clear()
    assert response.status_code == 200
    assert {row["output_type"] for row in response.json()["scores"]} == {
        "RNA_SEQ",
        "SPLICE_SITES",
        "SPLICE_SITE_USAGE",
        "SPLICE_JUNCTIONS",
    }


def test_score_custom_scorers(monkeypatch, canned_tidy, braf_payload):
    fake = make_client(monkeypatch, canned_tidy)
    braf_payload["scorers"] = ["RNA_SEQ"]
    try:
        response = TestClient(app).post("/score", json=braf_payload)
    finally:
        app.dependency_overrides.clear()
    assert response.status_code == 200
    _, _, scorers = fake.calls[0]
    assert len(scorers) == 1


def test_score_rejects_unknown_scorer(braf_payload):
    braf_payload["scorers"] = ["NOT_A_SCORER"]
    response = TestClient(app).post("/score", json=braf_payload)
    assert response.status_code == 422


def test_score_sorts_rows_most_to_least_impacted_and_computes_score_level(
    monkeypatch, braf_payload
):
    tidy = pd.DataFrame(
        [
            {
                "gene_id": "ENSG00000157764",
                "gene_name": "BRAF",
                "output_type": "RNA_SEQ",
                "variant_scorer": "GeneMaskLFCScorer",
                "track_name": "low_track",
                "ontology_curie": "UBERON:0000955",
                "biosample_name": "brain",
                "raw_score": 0.04,
                "quantile_score": 0.4,
            },
            {
                "gene_id": "ENSG00000157764",
                "gene_name": "BRAF",
                "output_type": "RNA_SEQ",
                "variant_scorer": "GeneMaskLFCScorer",
                "track_name": "strong_neg_track",
                "ontology_curie": "UBERON:0001264",
                "biosample_name": "pancreas",
                "raw_score": -0.75,
                "quantile_score": -0.996,
            },
            {
                "gene_id": "ENSG00000157764",
                "gene_name": "BRAF",
                "output_type": "SPLICE_SITE_USAGE",
                "variant_scorer": "GeneMaskSplicingScorer",
                "track_name": "moderate_splice_track",
                "ontology_curie": "UBERON:0002048",
                "biosample_name": "lung",
                "raw_score": 0.30,
                "quantile_score": 0.995,
            },
        ]
    )
    make_client(monkeypatch, tidy)
    try:
        response = TestClient(app).post("/score", json=braf_payload)
    finally:
        app.dependency_overrides.clear()
    assert response.status_code == 200
    body = response.json()
    assert body["score_level"] == "strong"
    assert [row["track_name"] for row in body["scores"]] == [
        "strong_neg_track",
        "moderate_splice_track",
        "low_track",
    ]
    assert "score_level" not in body["scores"][0]


def test_score_level_moderate_and_none(monkeypatch, braf_payload):
    moderate_tidy = pd.DataFrame(
        [
            {
                "gene_id": "ENSG00000157764",
                "gene_name": "BRAF",
                "output_type": "SPLICE_SITES",
                "variant_scorer": "GeneMaskSplicingScorer",
                "track_name": "donor",
                "raw_score": 0.25,
                "quantile_score": 0.992,
            }
        ]
    )
    make_client(monkeypatch, moderate_tidy)
    try:
        mod_resp = TestClient(app).post("/score", json=braf_payload)
    finally:
        app.dependency_overrides.clear()
    assert mod_resp.json()["score_level"] == "moderate"

    below_gate_tidy = pd.DataFrame(
        [
            {
                "gene_id": "ENSG00000157764",
                "gene_name": "BRAF",
                "output_type": "RNA_SEQ",
                "variant_scorer": "GeneMaskLFCScorer",
                "track_name": "pancreas",
                "raw_score": 0.8,
                "quantile_score": 0.95,
            }
        ]
    )
    make_client(monkeypatch, below_gate_tidy)
    try:
        none_resp = TestClient(app).post("/score", json=braf_payload)
    finally:
        app.dependency_overrides.clear()
    assert none_resp.json()["score_level"] is None


import asyncio
from functools import lru_cache

from fastapi import Depends, FastAPI, HTTPException

from app import scoring
from app.logger import log_debug
from app.schemas import ScoreRequest

app = FastAPI(
    title="AlphaGenome Scoring Sidecar",
    description="Scores GRCh38 variants via AlphaGenome and returns gene-filtered summaries for Genome Nexus.",
    version="0.1.0",
)


@app.get("/health")
def health() -> dict[str, str]:
    return {"status": "ok", "service": "alphagenome-sidecar"}


@lru_cache(maxsize=1)
def get_client() -> scoring.dna_client.DnaClient:
    return scoring.create_client()


@app.post("/score")
async def score_variant(
    request: ScoreRequest,
    client: scoring.dna_client.DnaClient = Depends(get_client),
) -> dict:
    log_debug("PHASE 4: SIDECAR REQUEST RECEIVED", f"Incoming POST /score: {request.dict()}")
    try:
        response = await asyncio.to_thread(scoring.score_with_client, request, client)
        log_debug(
            "PHASE 4: SIDECAR RESPONSE RETURNED",
            f"Returning score response for variant '{request.variant}': "
            f"hg38='{response.get('hg38_variant')}', model='{response.get('model_version')}', "
            f"rows count={len(response.get('scores', []))}, merged splicing={response.get('merged_splicing_score')}",
        )
        return response
    except scoring.RateLimitedError as exc:
        log_debug("PHASE 4: SIDECAR RATE LIMITED", f"429 Rate limited for variant '{request.variant}': {exc}")
        raise HTTPException(status_code=429, detail=str(exc)) from exc
    except scoring.UpstreamUnavailableError as exc:
        log_debug("PHASE 4: SIDECAR UPSTREAM UNAVAILABLE", f"503 Upstream error for variant '{request.variant}': {exc}")
        raise HTTPException(status_code=503, detail=str(exc)) from exc
    except Exception as exc:
        log_debug("PHASE 4: SIDECAR UNEXPECTED ERROR", f"500 Unexpected error for variant '{request.variant}': {type(exc).__name__}: {exc}")
        raise

# AlphaGenome Variant Scoring Sidecar (`alphagenome-sidecar`)

FastAPI microservice wrapping the Google DeepMind `alphagenome` Python SDK (`dna_client.score_variant` and `variant_scorers.tidy_scores`) for Genome Nexus (`POST /score`).

## Build & Setup Instructions

### Prerequisites
- Python 3.11–3.13
- `pip` or `uv`

### Install Dependencies
```bash
python3 -m venv .venv
source .venv/bin/activate
pip install -e ".[dev]"
```

### Run Tests
```bash
pytest -q
```

### Start Server
```bash
uvicorn app.main:app --host 0.0.0.0 --port 8000
```

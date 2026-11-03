"""Request and response models for the variant scoring API"""

from pydantic import BaseModel, Field, field_validator

from alphagenome.models.v1 import variant_scorers as variant_scorers_module

CHROMOSOME_PATTERN = r"^chr([1-9]|1[0-9]|2[0-2]|X|Y)$"
ALLELE_PATTERN = r"^[ACGT]+$"

# Reject unsupported sequence lengths
ALLOWED_SEQUENCE_LENGTHS = (16384, 131072, 524288, 1048576)
DEFAULT_SEQUENCE_LENGTH = 1048576
# List of recommended scorers currently implemented
SUPPORTED_SCORER_NAMES = frozenset(variant_scorers_module.RECOMMENDED_VARIANT_SCORERS.keys())


class ScoreRequest(BaseModel):
    variant: str = Field(description="Original Genome Nexus variant ID, echoed in the response")
    chromosome: str = Field(pattern=CHROMOSOME_PATTERN, examples=["chr7"])
    position: int = Field(ge=1, description="1-based GRCh38 position", examples=[140753336])
    reference_allele: str = Field(pattern=ALLELE_PATTERN, examples=["A"])
    alternate_allele: str = Field(pattern=ALLELE_PATTERN, examples=["T"])
    canonical_gene_id: str | None = Field(default=None, examples=["ENSG00000157764"])
    canonical_gene: str = Field(examples=["BRAF"])
    rsid: str | None = None
    hgvsc: str | None = None
    hgvsp: str | None = None
    sequence_length: int = Field(
        default=DEFAULT_SEQUENCE_LENGTH,
        description="Scored window width. One of 16384, 131072, 524288, 1048576 (default).",
        examples=[1048576],
    )
    scorers: list[str] | None = Field(
        default=None,
        description="Scorer names from RECOMMENDED_VARIANT_SCORERS. None keeps the 4 defaults.",
        examples=[["RNA_SEQ", "SPLICE_SITES", "SPLICE_SITE_USAGE", "SPLICE_JUNCTIONS"]],
    )
    ontology_curies: list[str] | None = Field(
        default=None,
        description="Post-output filter for UBERON/CL tissue curies. None keeps all tissues.",
        examples=[["UBERON:0001264"]],
    )

    @field_validator("sequence_length")
    @classmethod
    def check_sequence_length(cls, value: int) -> int:
        if value not in ALLOWED_SEQUENCE_LENGTHS:
            raise ValueError(f"sequence_length must be one of {list(ALLOWED_SEQUENCE_LENGTHS)}")
        return value

    @field_validator("scorers")
    @classmethod
    def check_scorers(cls, value: list[str] | None) -> list[str] | None:
        if value is not None:
            unknown = [name for name in value if name not in SUPPORTED_SCORER_NAMES]
            if unknown:
                raise ValueError(f"Unknown scorers: {unknown}")
        return value

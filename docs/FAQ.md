# Frequently Asked Questions

## What is Genome Nexus?

Genome Nexus is a variant annotation and interpretation service. It takes a genomic variant as input, queries [VEP (Variant Effect Predictor)](https://www.ensembl.org/info/docs/tools/vep/index.html) for functional consequences, optionally enriches the result with data from external sources (gnomAD, ClinVar, COSMIC, signaldb, Mutation Assessor, etc.), and returns structured JSON containing transcript consequences, protein changes, variant classifications, and more.

---

## What genome builds are supported?

GRCh37 (hg19) and GRCh38 (hg38) are both supported, but as **separate deployments** — each instance is backed by a build-specific VEP endpoint. The build is fixed at deployment time in `application.properties`. There is no runtime parameter to switch builds; you must query the instance that matches your variant coordinates.

---

## What variant input formats are accepted?

Genome Nexus accepts two input formats: **genomic HGVS notation** and **genomic location** (chromosome, start, end, ref, alt).

### Genomic HGVS notation

Used with the `/annotation` endpoints. The string is forwarded directly to VEP.

| Variant type | Example |
|---|---|
| SNP | `7:g.140453136A>T` |
| Insertion | `17:g.41242962_41242963insGA` |
| Deletion | `22:g.36689419_36689421del` |
| Deletion-insertion | `19:g.46141892_46141893delinsAA` |
| Multi-nucleotide | `12:g.25398285_25398286delinsAA` |

### Genomic location

Use the `/annotation/genomic` endpoints. Genome Nexus converts the location to HGVS internally before querying VEP.

**GET — comma-delimited string** (format: `chromosome,start,end,referenceAllele,variantAllele`):

```
GET /annotation/genomic/7,140453136,140453136,A,T
```

**POST — JSON array** of genomic location objects:

```json
[
  {
    "chromosome": "7",
    "start": 140453136,
    "end": 140453136,
    "referenceAllele": "A",
    "variantAllele": "T"
  }
]
```

Alleles must contain only the characters `A`, `C`, `G`, `T`, or `-` (deletion placeholder). Chromosome accepts `1`–`22`, `X`, `Y`, and `MT`.

---

## What is the difference between `/annotation` and `/annotation/summary`?

| Endpoint | Returns |
|---|---|
| `GET /annotation/{variant}` | Raw VEP output plus any requested enrichments (my_variant_info, nucleotide context, etc.). All VEP fields are present. |
| `GET /annotation/summary/{variant}` | A processed summary: resolved `variantClassification`, `HGVSp_Short`, protein position, Hugo symbol, RefSeq IDs, and other derived fields. Raw VEP fields are omitted. |

The summary endpoint calls the annotation endpoint internally and then runs the resolver pipeline on top of the result. Use `/annotation` when you need raw VEP fields; use `/annotation/summary` when you want pre-resolved, MAF-compatible fields.

---

## How do I annotate multiple variants at once?

All annotation endpoints have a batch POST variant. Results are returned in the same order as the input. Variants that fail VEP annotation still appear in the response with `successfully_annotated: false` so array indices stay aligned.

### HGVS notation — array of strings

```
POST /annotation
Content-Type: application/json

["7:g.140453136A>T", "17:g.41242962_41242963insGA", "22:g.36689419_36689421del"]
```

### Genomic location — array of objects

```
POST /annotation/genomic
Content-Type: application/json

[
  {
    "chromosome": "7",
    "start": 140453136,
    "end": 140453136,
    "referenceAllele": "A",
    "variantAllele": "T"
  },
  {
    "chromosome": "17",
    "start": 41242962,
    "end": 41242963,
    "referenceAllele": "-",
    "variantAllele": "GA"
  }
]
```

---

## What does the `fields` parameter do?

`fields` is a comma-separated list of enrichment sources to activate for a request. By default only the raw VEP annotation is returned. Each named field triggers an additional data fetch and merges its result into the response.

All valid values (case-insensitive):

| Field | Data added |
|---|---|
| `annotation_summary` | Resolved canonical-transcript summary: `variantClassification`, `HGVSp_Short`, protein position, Hugo symbol, RefSeq transcript IDs, codon change, etc. |
| `clinvar` | ClinVar clinical significance and review status |
| `hotspots` | Whether the variant falls on a known cancer hotspot (from cancerhotspots.org) |
| `mutation_assessor` | Mutation Assessor functional impact score and prediction |
| `my_variant_info` | Aggregated data from MyVariantInfo: gnomAD allele frequencies, ClinVar, COSMIC IDs, dbSNP rsIDs |
| `nucleotide_context` | Trinucleotide context around the variant |
| `oncokb` | OncoKB oncogenicity and therapeutic actionability — **requires a token** (see below) |
| `ptms` | Post-translational modification sites overlapping the variant position |
| `signal` | SIGNAL database mutation significance data |

Example: `GET /annotation/7:g.140453136A>T?fields=annotation_summary,my_variant_info,hotspots`

### OncoKB token

OncoKB requires authentication. Pass your token via the `token` query parameter as a JSON map:

```
token={"oncokb":"YOUR_ONCOKB_TOKEN"}
```

Without a valid token the OncoKB enricher will be called but the API request to OncoKB will fail, and the `oncokb` field in the response will be empty. Tokens can be obtained from [oncokb.org](https://www.oncokb.org).

---

## How does Genome Nexus pick a transcript for a variant?

When a variant overlaps multiple transcripts, Genome Nexus selects one "canonical" transcript through a multi-step pipeline. VEP's own canonical flag is **reset** before the pipeline runs — it is not used as input.

Each step can only shrink the candidate list (or leave it unchanged when the filter finds no matches).

**Table of Contents**

- [Overview](#overview)
- [Step 1 — Gene-level biotype filter](#step-1--gene-level-biotype-filter)
- [Step 2 — OncoKB gene preference](#step-2--oncokb-gene-preference)
- [Step 3 — Per-transcript biotype filter](#step-3--per-transcript-biotype-filter)
- [Step 4 — Isoform override](#step-4--isoform-override)
- [Step 5 — Mark canonical and resolve](#step-5--mark-canonical-and-resolve)
  - [How the prioritizer picks one transcript](#how-the-prioritizer-picks-one-transcript)
    - [Pass A — Match most_severe_consequence](#pass-a--match-most_severe_consequence)
    - [Pass B — Highest-priority consequence term](#pass-b--highest-priority-consequence-term)
    - [Final tiebreaker — VEP list order](#final-tiebreaker--vep-list-order)
- [Source code references](#source-code-references)

---

### Overview

```
VEP transcript_consequences
  │
  ▼
Step 1: Gene-level biotype filter    (keep genes with best biotype)
  │
  ▼
Step 2: OncoKB gene preference       (prefer cancer genes)
  │
  ▼
Step 3: Per-transcript biotype filter (keep transcripts with best biotype)
  │
  ▼
Step 4: Isoform override             (keep transcripts in override set)
  │
  ▼
Step 5: Mark canonical + resolve     (pick exactly one transcript)
  │
  ▼
transcriptConsequenceSummary
```
![Flowchart of transcript picking](https://github.com/user-attachments/assets/f8c3ab90-0c99-4ea7-aa89-04bb7224f47f)
---

### Step 1 — Gene-level biotype filter

Group all transcripts by gene. For each gene, find the best (lowest-number) biotype among its transcripts. Then compare across genes and keep only transcripts from genes whose best biotype equals the global minimum.

This ensures a gene with a `protein_coding` transcript always beats a gene with only `lncRNA` or `pseudogene` transcripts.

**Biotype priority table** (lower number = higher priority):

| Priority | Biotypes |
|---|---|
| 1 | `protein_coding` |
| 2 | `IG_*`, `TR_*` (immunoglobulin / T-cell receptor genes) |
| 3 | `miRNA`, `snRNA`, `snoRNA`, `lncRNA`, `lincRNA` |
| 4 | `Mt_tRNA`, `Mt_rRNA`, `vault_RNA`, `rRNA`, `ribozyme` |
| 5 | `antisense`, `sense_intronic`, `sense_overlapping`, `misc_RNA`, `scRNA` |
| 6 | `processed_transcript`, `TEC` |
| 7 | `retained_intron`, `nonsense_mediated_decay`, `non_stop_decay` |
| 8 | All pseudogene types |
| 9 | `artifact` |
| 10 | Anything not in the above list |

---

### Step 2 — OncoKB gene preference

Among surviving genes, check whether any gene symbol appears in the OncoKB cancer gene list. If at least one gene qualifies, keep only transcripts from OncoKB genes and discard the rest. If no gene is in the list, all candidates pass through unchanged.

This runs at the **gene** level so that an isoform override for an incidental bystander gene cannot outrank a cancer gene's transcript.

---

### Step 3 — Per-transcript biotype filter

Within the surviving genes from Step 2, apply the same biotype priority table again, but now at the **individual transcript** level. Keep only transcripts whose biotype has the lowest (best) priority value. For example, if one transcript is `protein_coding` (1) and another is `nonsense_mediated_decay` (7), only the `protein_coding` transcript survives.

---

### Step 4 — Isoform override

Check the configured isoform override set (controlled by the `isoformOverrideSource` query parameter; default `mskcc`). Among the remaining transcripts, keep only those whose transcript ID appears in the override set. If **no** transcript matches the override set (e.g., the curated transcript is too far from the variant to appear in VEP's output), all Step 3 candidates are kept unchanged.

---

### Step 5 — Highest consequence term

For all transcripts that survived Steps 1–4,
| Scenario | Action |
|---|---|
| Exactly **1** transcript marked canonical | Return it immediately — no further logic needed. |
| **Multiple** transcripts marked canonical | Run the **prioritizer** (described below) on just the survived transcripts. |
| **0** transcripts marked canonical | Run the **prioritizer** on **all** original transcripts as a fallback. |

#### How the prioritizer picks one transcript

The [`TranscriptConsequencePrioritizer`](https://github.com/genome-nexus/genome-nexus/blob/master/component/src/main/java/org/cbioportal/genome_nexus/component/annotation/TranscriptConsequencePrioritizer.java) works in two passes to narrow candidates, then uses list position as a final tiebreaker.

##### Pass A — Match `most_severe_consequence`

VEP returns a top-level `most_severe_consequence` string for the whole variant (e.g., `missense_variant`). This is the single most severe consequence term across **all** transcripts, computed by VEP itself.

The prioritizer scans every candidate transcript and collects those whose `consequence_terms` list contains this exact term (case-insensitive match). This narrows the pool to transcripts that actually carry the variant's most impactful effect.

If one transcript matches, all other transcripts are discarded.
If more than one transcript matches, those transcripts carry forward to Pass B.
If **no** transcript matches (rare — can happen when VEP's term is not in any transcript's list), all candidates carry forward to Pass B unchanged.

##### Pass B — Highest-priority consequence term

Among the transcripts from Pass A, the prioritizer scans every `consequence_terms` entry and looks up its numeric priority in the `EFFECT_PRIORITY` map. It finds the **single lowest value** (= most severe) across all terms of all candidates.

Then it keeps only transcripts that have at least one term at that best priority level, and discards the rest.

**Effect priority table** (lower number = more severe; selected examples):

| Priority | Consequence terms |
|---|---|
| 1 | `transcript_ablation`, `exon_loss_variant` |
| 2 | `splice_donor_variant`, `splice_acceptor_variant` |
| 3 | `stop_gained`, `frameshift_variant`, `stop_lost` |
| 4 | `start_lost`, `initiator_codon_variant` |
| 5 | `transcript_amplification` |
| 7 | `inframe_insertion`, `inframe_deletion`, `disruptive_inframe_insertion`, `disruptive_inframe_deletion` |
| 8 | `protein_altering_variant` |
| 9 | `missense_variant`, `conservative_missense_variant`, `rare_amino_acid_variant` |
| 10 | `splice_region_variant`, `splice_donor_5th_base_variant`, `splice_donor_region_variant` |
| 11 | `synonymous_variant`, `start_retained_variant`, `stop_retained_variant` |
| 13 | `coding_sequence_variant`, `mature_mirna_variant`, `exon_variant` |
| 14 | `5_prime_utr_variant`, `3_prime_utr_variant` |
| 16 | `intron_variant`, `non_coding_transcript_variant` |
| 19 | `upstream_gene_variant`, `downstream_gene_variant` |
| 20 | `regulatory_region_variant`, `tfbs_ablation` |
| 21 | `intergenic_variant`, `sequence_variant` |

(The full map contains ~60 terms. See `EFFECT_PRIORITY` in the [source code](https://github.com/genome-nexus/genome-nexus/blob/master/component/src/main/java/org/cbioportal/genome_nexus/component/annotation/TranscriptConsequencePrioritizer.java) for the complete list.)

##### Final tiebreaker — VEP list order

If multiple transcripts still tie after both passes, the prioritizer returns `bestCandidates.get(0)` — the **first** one in the list. Because the candidate list preserves VEP's original `transcript_consequences` array order throughout the pipeline, VEP's ordering is the ultimate tiebreaker.

As a last-resort fallback (if no transcript had any recognized consequence term at all), the code returns `transcripts.get(0)` — the first transcript in the original input list.

---

### Source code references

| Component | Class | Role |
|---|---|---|
| Pipeline orchestrator | [`IsoformAnnotationEnricher`](https://github.com/genome-nexus/genome-nexus/blob/master/service/src/main/java/org/cbioportal/genome_nexus/service/enricher/IsoformAnnotationEnricher.java) | Steps 1–4, marks canonical |
| Final transcript pick | [`CanonicalTranscriptResolver`](https://github.com/genome-nexus/genome-nexus/blob/master/component/src/main/java/org/cbioportal/genome_nexus/component/annotation/CanonicalTranscriptResolver.java) | Step 5 branching logic |
| Consequence ranking | [`TranscriptConsequencePrioritizer`](https://github.com/genome-nexus/genome-nexus/blob/master/component/src/main/java/org/cbioportal/genome_nexus/component/annotation/TranscriptConsequencePrioritizer.java) | Pass A + Pass B + tiebreaker |
| Detailed walkthrough | [Canonical-Transcript-Selection-Analysis.md](Canonical-Transcript-Selection-Analysis.md) | Full examples and edge cases |

---

## What is `most_severe_consequence`, and how does it differ from `variantClassification`?

`most_severe_consequence` is a field returned directly by VEP. It is the single highest-priority consequence term across **all transcripts** for the variant (e.g. `missense_variant`, `stop_gained`). VEP computes it using its own consequence severity ranking.

`variantClassification` is computed by Genome Nexus. It takes the highest-priority consequence term for the **canonical transcript only**, maps it to a MAF-style label (e.g. `Missense_Mutation`, `Nonsense_Mutation`), and optionally overrides it with a reVUE curation. See [How is `variantClassification` resolved?](#how-is-variantclassification-resolved) for the full logic.

The two fields can disagree when the most severe consequence in any transcript differs from the consequence in the canonical transcript.

---

## What is reVUE, and how does it affect annotations?

reVUE (Revised Variant Understander and Evaluator) is a dataset of manually curated variant reclassifications, maintained at [github.com/knowledgesystems/reVUE-data](https://github.com/knowledgesystems/reVUE-data). It captures cases where VEP's predicted consequence is incorrect — most commonly for splice variants whose true functional impact differs from the sequence-based prediction.

At startup, Genome Nexus downloads the reVUE and replaces the VEP-derived `variantClassification`.

Whether unconfirmed reVUE entries are applied is controlled by `overwrite_by_confirmed_revue_only` in `application.properties`:
- `true` — only entries with `confirmed: true` in the dataset can override VEP.
- `false` — all reVUE entries override VEP regardless of confirmation status.

---

## What is the difference between `hgvsp` and `HGVSp_Short`?

`hgvsp` is the raw protein notation returned by VEP. It uses three-letter amino acid codes and includes the protein/transcript ID prefix, for example: `ENSP00000288602.6:p.Val600Glu`.

`HGVSp_Short` is Genome Nexus's processed version. It strips the prefix, converts three-letter amino acid codes to single-letter codes, and fills in the notation when VEP did not return an `hgvsp` (e.g. for splice variants or frameshifts). Result: `p.V600E`.

See [How is `HGVSp_Short` resolved?](#how-is-hgvsp_short-resolved) for the full resolution logic.

---

## Why can the same variant return different annotations on different Genome Nexus instances?

Several deployment-level factors affect the annotation:

- **VEP version** — newer VEP versions introduce additional consequence terms (e.g. `splice_donor_5th_base_variant` added in VEP 105), which change `variantClassification` and `HGVSp_Short` for splice-region variants.
- **Genome build** — GRCh37 and GRCh38 instances use different VEP endpoints and reference sequences. Coordinates and transcripts differ.
- **reVUE configuration** — `overwrite_by_confirmed_revue_only=true` vs `false` controls whether unconfirmed curations override VEP.
- **Canonical transcript configuration** — `prioritize_cancer_gene_transcripts=true` can select a different transcript than an instance where it is disabled.
- **Cache state** — a cached annotation from an older VEP version will differ from a fresh annotation if VEP was upgraded without clearing the MongoDB cache.

---

## How do I force a re-annotation of a cached variant?

Two options:

1. **Disable caching globally** — set `cache.enabled=false` in `application.properties` and restart. All requests will bypass the cache and call VEP directly. Re-enable afterward to resume caching.

2. **Delete the cached document** — connect to the MongoDB instance and delete the document from the `vep.annotation` collection where the `_id` field matches the variant string. The next request for that variant will call VEP fresh and cache the new result.

There is no per-request cache-bypass parameter in the API.

---

## When is `HGVSp_Short` empty?

`HGVSp_Short` is intentionally left empty in these cases:

- **3′ UTR variants** — if the HGVSc position contains `c.*` (after the stop codon), no protein position can be computed. You can recognize these by their HGVSc notation, for example `c.*31+8A>C` or `c.*18+2T>C`. Splice region variants after the stop codon are labeled `Splice_Region` in `variantClassification`, and `HGVSp_Short` will be blank because these positions are outside the coding sequence.
- **Splice variants** — if the resolved `variantClassification` contains `"splice"`, `p.X{pos}_splice` is returned when HGVSc is available; if HGVSc is also absent, the field is left empty.
- **All resolution tiers fail** — when VEP provides no `hgvsp`, no `hgvsc`, and no `amino_acids`, none of the three resolution tiers can produce a value. See [How is `HGVSp_Short` resolved?](#how-is-hgvsp_short-resolved) for the full resolution chain.

> **Note for developers:** The 3′ UTR early exit is a single `contains("c.*")` check in [`resolveHgvspShortFromHgvsc()` (line 127)](https://github.com/genome-nexus/genome-nexus/blob/master/component/src/main/java/org/cbioportal/genome_nexus/component/annotation/ProteinChangeResolver.java#L127). It runs before any consequence term check, so it applies to all variant types, not just splice variants. The splice suppression is in [`resolveHgvsp()` (lines 251–253)](https://github.com/genome-nexus/genome-nexus/blob/master/component/src/main/java/org/cbioportal/genome_nexus/component/annotation/ProteinChangeResolver.java#L251).

---

## Why is a splice variant labeled `X(pos)_splice` vs `p.*(pos)*`?

The label depends on which consequence term VEP assigns as the **most severe** (first) consequence for the canonical transcript. The exact definitions come from the [Ensembl VEP consequence type table](https://www.ensembl.org/info/genome/variation/prediction/predicted_data.html?redirect=no#consequence_type_table):

| Label | VEP consequence term | Ensembl definition | Introduced |
|---|---|---|---|
| `X(pos)_splice` | `splice_acceptor_variant` | 2 base region at the 3′ end of the intron (the AG dinucleotide) | VEP early versions |
| `X(pos)_splice` | `splice_donor_variant` | 2 base region at the 5′ end of the intron (the GT dinucleotide) | VEP early versions |
| `X(pos)_splice` | `splice_region_variant` | Within 1–3 bases of the exon or 3–8 bases of the intron | VEP early versions |
| `p.*(pos)*` | `splice_donor_5th_base_variant` | 5th base into the intron after the 5′ splice junction | VEP 105 (Dec 2021) |
| `p.*(pos)*` | `splice_donor_region_variant` | 3rd to 6th base into the intron after the 5′ splice junction | VEP 105 (Dec 2021) |
| `p.*(pos)*` | `splice_polypyrimidine_tract_variant` | Acceptor −3 to acceptor −17 (polypyrimidine tract at 3′ end of intron) | VEP 105 (Dec 2021) |
| `p.*(pos)*` | `intron_variant` | Deeper intronic, outside all ranges above | VEP early versions |

The diagram below shows bp positions each term covers, relative to the 5′ donor and 3′ acceptor splice junctions.

![VEP Splice Consequence Terms — Genomic Range](https://github.com/user-attachments/assets/68ac7f45-ab77-45ed-9bf1-e863072d5747)

- **RED** (`splice_donor_variant` / `splice_acceptor_variant`)
- **BLUE** (`splice_region_variant`)
- **ORANGE** (`splice_donor_5th_base_variant`)
- **GREEN** (`splice_donor_region_variant`)
- **PURPLE** (`splice_polypyrimidine_tract_variant`)

Note that `splice_region_variant` overlaps with both `splice_donor_region_variant` (+3 to +6) and `splice_polypyrimidine_tract_variant` (−3 to −8). The three newer terms were built-in only from **VEP 105 (Dec 2021)** onward — previously they required the SpliceRegion plugin, and positions in the overlap zones were annotated as `splice_region_variant` instead. This means the same variant can produce `X(pos)_splice` on a genome nexus instance backed by VEP ≤104 and `p.*(pos)*` on one backed by VEP 105+.

Genome Nexus does not apply its own distance cutoff — it relies entirely on which consequence term VEP returns.

> **Note for developers:** The three terms that trigger `X(pos)_splice` are defined in [`SPLICE_SITE_VARIANTS` (lines 27–29)](https://github.com/genome-nexus/genome-nexus/blob/master/component/src/main/java/org/cbioportal/genome_nexus/component/annotation/ProteinChangeResolver.java#L27):
> ```java
> public static final Set<String> SPLICE_SITE_VARIANTS = new HashSet<>(
>     Arrays.asList("splice_acceptor_variant", "splice_donor_variant", "splice_region_variant")
> );
> ```
> Newer VEP terms — `splice_donor_5th_base_variant`, `splice_donor_region_variant`, and `splice_polypyrimidine_tract_variant` — are absent from this set. They are mapped to the `Splice_Region` variant classification in [`VariantClassificationResolver.java` (lines 213–215)](https://github.com/genome-nexus/genome-nexus/blob/master/component/src/main/java/org/cbioportal/genome_nexus/component/annotation/VariantClassificationResolver.java#L213), but because they are not in `SPLICE_SITE_VARIANTS` they fall through to the `p.*(pos)*` branch.
>
> The full decision tree in [`resolveHgvspShortFromHgvsc()` (lines 113–156)](https://github.com/genome-nexus/genome-nexus/blob/master/component/src/main/java/org/cbioportal/genome_nexus/component/annotation/ProteinChangeResolver.java#L113):
> ```
> HGVSc present?
> ├── contains "c.*"?                    → return null  (3' UTR)
> └── else: extract cPos, compute pPos = (cPos + 2) / 3
>     ├── first consequence in SPLICE_SITE_VARIANTS?  → "p.X{pPos}_splice"
>     └── else if amino_acids == null?
>         ├── frameshift?                → "p.*{pPos}fs*"
>         └── else                      → "p.*{pPos}*"
> ```
> The HGVSp suppression for splice variants is in [`resolveHgvsp()` (lines 251–253)](https://github.com/genome-nexus/genome-nexus/blob/master/component/src/main/java/org/cbioportal/genome_nexus/component/annotation/ProteinChangeResolver.java#L251).

---

## How is the protein position in `X(pos)_splice` and `p.*(pos)*` calculated?

The position is derived from the cDNA position in HGVSc, not from a protein-level annotation. The formula is:

```
protein_position = (cDNA_position + 2) / 3   (integer division)
```

For example, `c.3709-5C>A` → cDNA position 3709 → protein position (3709 + 2) / 3 = **1237** → `p.X1237_splice`.

The intronic offset (the `-5` part) is stripped before this calculation; only the exon-anchored cDNA number is used.

> **Note for developers:** The cDNA position is extracted with the regex [`".*[cn].-?\\*?(\\d+).*"` (line 41)](https://github.com/genome-nexus/genome-nexus/blob/master/component/src/main/java/org/cbioportal/genome_nexus/component/annotation/ProteinChangeResolver.java#L41). It captures the first numeric value after `c.` or `n.`, discarding any `+`/`-` intronic offsets or `*` UTR markers that follow. If the parsed cDNA position is less than 1, it is clamped to 1 before the protein position is computed.

---

## When is `successfully_annotated` false?

`successfully_annotated: false` means Genome Nexus could not obtain a valid annotation from VEP for that variant. The most common causes:

- **Reference allele mismatch** — the reference allele you provided does not match what the genome assembly has at that position. The `errorMessage` field will contain: `1:g.1020385C>A: Reference allele extracted from input (C) does not match reference allele from genome (T)`.
- **Invalid HGVS notation** — the variant string could not be parsed. The `errorMessage` will say: `Invalid HGVS notation '<variant>': could not be parsed.`
- **Chromosome or position not found** — the chromosome doesn't exist in the reference genome or the position is out of range.
- **VEP is down or unreachable** — any HTTP 5xx or connection error results in a placeholder annotation with this flag set to false.
- **Reference allele verification failure** — even if VEP responds successfully, Genome Nexus verifies the reference allele in the response against your input. Two distinct messages depending on what VEP returned:
  - Actual mismatch (VEP returned an allele but it differs): `Reference allele extracted from response (T) does not match reference allele given by input 7,140453136,140453136,A,T (A)`
  - Unknown (VEP returned no allele string — position may not exist in the assembly): `Reference allele for 7,140453136,140453136,A,T could not be determined from VEP response - verify that the position exists in the genome assembly (GRCh37/hg19) and the reference allele (A) is correct`

When `successfully_annotated: true`, it means VEP responded with HTTP 200 and the result was transformed without error, but `transcript_consequences` could be null.

> **Note for developers:** The flag is set by `setSuccessfullyAnnotated(true)` only on the path where `super.fetchAndCache(id)` returns a non-null `VariantAnnotation` without an existing `errorMessage`. All exception paths (`HttpServerErrorException`, `HttpClientErrorException`, and the generic `catch (Exception e)`) create an empty `VariantAnnotation` without calling that setter, leaving the field false. When VEP returns a detailed error for a specific variant in a batch, the error message is propagated through and preserved in the `errorMessage` field.

---

## What error messages can appear in `errorMessage`?

The `errorMessage` field is populated when `successfully_annotated` is false. The message indicates exactly why annotation failed:

| Error type | Example `errorMessage` | Cause |
|---|---|---|
| Reference allele mismatch、| `1:g.1020385C>A: Reference allele extracted from input (C) does not match reference allele from genome (T)` | The ref allele you provided doesn't match the genome assembly. Verify coordinates and allele. |
| Invalid HGVS notation | `Invalid HGVS notation 'xxx': could not be parsed.` | The variant string is malformed. Check format - e.g. `7:g.140453136A>T` for SNVs. |
| Chromosome/position not found | `Chromosome or position not found: variant '25:g.1295228G>A' - chromosome or position does not exist in the genome assembly.` | The chromosome doesn't exist or the position exceeds the chromosome length. |
| Reference allele unknown | `Reference allele for 1:g.170552185C>A could not be determined from VEP response - verify that the position exists in the genome assembly (GRCh37/hg19) and the reference allele (C) is correct` | VEP returned no `alleleString` - the position may not exist in the assembly. |
| Generic VEP error | `Error annotating variant '<variant>': <raw error>` | An unexpected VEP error. The raw error message is preserved for debugging. |
| VEP returned no data | `Error from VEP for: <variant>` | VEP returned no result for this variant (connection failure, timeout, or variant dropped from batch). |

### Batch behavior

In batch requests (POST), a single bad variant does **not** fail the entire batch. VEP uses a retry strategy to isolate bad variants.
Each variant gets its own specific error message — good variants are annotated normally even if others in the same batch fail.

---

## When is `transcript_consequences` null or empty?

| Scenario | Value |
|---|---|
| VEP is down / unreachable | `null` |
| Truly intergenic variant (no nearby gene) | `null` — VEP omits the field and returns `intergenic_consequences` instead |
| Variant outside all transcript boundaries | `null` |
| Variant in unannotated / gap regions | `null` |
| VEP returns valid consequences | populated list |

Note that variants with `upstream_gene_variant` or `downstream_gene_variant` consequences **do** appear in `transcript_consequences` — only variants with no gene context at all produce null.

In practice, VEP omits the field entirely rather than returning an empty array, so `[]` is not a realistic value. The field will be either `null` or a populated list.

> **Note for developers:** The field is populated entirely by Jackson deserialization of VEP's JSON — application code never sets it explicitly. VEP omitting the field maps to `null`; VEP returning `"transcript_consequences": []` would map to `[]`, but this does not occur in practice.

---

## When is `annotation_summary.transcriptConsequenceSummaries` empty?

`transcriptConsequenceSummaries` is `[]` whenever `transcript_consequences` from VEP is null — this covers both the VEP-down case and intergenic variants. It is never null; the field is always initialized to an empty list before population is attempted.

It is only populated when both conditions are met:
1. VEP successfully returned `transcript_consequences` (non-null), **and**
2. The summary endpoint is called (`/annotation` with `fields=annotation_summary`, or `/annotation/summary/{variant}`).

Each entry is an enriched view of a VEP transcript consequence with resolved fields: protein position, codon change, amino acids, Hugo gene symbol, variant classification, RefSeq transcript IDs, and more.

> **Note for developers:** The guard is an `if (transcriptConsequences != null)` check in `VariantAnnotationSummaryServiceImpl`. When it is skipped, an empty `ArrayList` is set on the summary object. This field is not a fallback source — it is derived directly from VEP data and carries no information that VEP did not provide.

---

## How is `HGVSp_Short` resolved?

`HGVSp_Short` is computed by a three-tier fallback chain. Each tier is tried in order; the first one that produces a non-null value wins.

### Tier 1 — Convert VEP's raw `hgvsp`

VEP returns a full protein notation such as `ENSP00000288602.6:p.Val600Glu`. Genome Nexus processes it in two steps:

1. **Normalize** — strip the transcript/protein ID prefix (everything up to and including the first `:`), and translate the URL-encoded synonymous marker `(p.%3D)` to `p.=`.
2. **3-to-1 amino acid conversion** — replace every three-letter amino acid code with its single-letter equivalent using this table:

| 3-letter | 1-letter | 3-letter | 1-letter |
|---|---|---|---|
| `Ala` | `A` | `Met` | `M` |
| `Arg` | `R` | `Phe` | `F` |
| `Asn` | `N` | `Pro` | `P` |
| `Asp` | `D` | `Ser` | `S` |
| `Asx` | `B` | `Thr` | `T` |
| `Cys` | `C` | `Trp` | `W` |
| `Gln` | `Q` | `Tyr` | `Y` |
| `Glu` | `E` | `Val` | `V` |
| `Glx` | `Z` | `Xxx` | `X` |
| `Gly` | `G` | `Ter` | `*` |
| `His` | `H` | | |
| `Ile` | `I` | | |
| `Leu` | `L` | | |
| `Lys` | `K` | | |

**Example:** `ENSP00000288602.6:p.Val600Glu` → `p.V600E`

### Tier 2 — Synthesize from HGVSc

When `hgvsp` is unavailable, Genome Nexus derives a protein notation from the cDNA HGVSc string.

**Compute the protein position from the cDNA position:**

```
cDNA_position    =  first integer captured by regex .*[cn].-?\*?(\d+).* from the HGVSc string
protein_position =  (cDNA_position + 2) / 3   (integer division, minimum 1)
```

The regex captures only the exon-anchored base number; any intronic offset (`+5`, `-3`) and UTR marker (`*`) that follows are discarded.

**Branch on the consequence term:**

| Condition | Output |
|---|---|
| HGVSc contains `c.*` (3′ UTR position) | `null` — no protein position exists |
| First consequence term is `splice_acceptor_variant`, `splice_donor_variant`, or `splice_region_variant` | `p.X{pos}_splice` |
| `amino_acids` is null **and** variant classification contains `"frame_shift"` | `p.*{pos}fs*` |
| `amino_acids` is null **and** no frameshift | `p.*{pos}*` |

**Examples:**
- `c.50-1_50delinsTT` (splice acceptor, cDNA pos 50, pPos 17) → `p.X17_splice`
- `c.340insG` (frameshift, cDNA pos 340, pPos 114) → `p.*114fs*`
- `c.4349A>G` (stop gained, cDNA pos 4349, pPos 1450) → `p.*1450*`

### Tier 3 — Salvage from the `amino_acids` field

When both tiers above produce null, Genome Nexus falls back to building a notation from VEP's `amino_acids` field (format: `Ref/Alt`) combined with `protein_start` and `protein_end`.

| Variant type (from `consequence_terms`) | Condition | Output |
|---|---|---|
| `inframe_insertion` | HGVSc contains `dup`, single AA inserted | `p.{altAA}{proteinStart - 1}dup` |
| `inframe_insertion` | HGVSc contains `dup`, multiple AAs inserted | `p.{altAA[0]}{proteinStart + 1}_{altAA[last]}{proteinStart + len}dup` |
| `inframe_insertion` | Regular insertion | `p.X{proteinStart}_X{proteinEnd}ins{altAA}` |
| `inframe_deletion` | — | `p.{refAA}{proteinStart}del` |
| `frameshift_variant` | — | `p.{refAA}{proteinStart}fs` |
| Other (missense, etc.) | — | `p.{refAA}{proteinStart}{altAA}` |

**Examples:**
- `amino_acids=EA/A`, `inframe_deletion`, pos 1350 → `p.E1350del`
- `amino_acids=L/Vfs`, `frameshift_variant`, pos 431 → `p.L431fs`
- `amino_acids=K/E`, missense, pos 373 → `p.K373E`

> **Note for developers:** The three-tier chain is in [`resolveHgvspShort(VariantAnnotation, TranscriptConsequence)` (lines 67–91)](https://github.com/genome-nexus/genome-nexus/blob/master/component/src/main/java/org/cbioportal/genome_nexus/component/annotation/ProteinChangeResolver.java#L67) in `ProteinChangeResolver.java`. Key subordinate methods:
> - **Tier 1 normalization & conversion:** [`normalizeHgvsp()` (lines 348–362)](https://github.com/genome-nexus/genome-nexus/blob/master/component/src/main/java/org/cbioportal/genome_nexus/component/annotation/ProteinChangeResolver.java#L348) and [`resolveHgvspShortFromHgvsp()` (lines 95–109)](https://github.com/genome-nexus/genome-nexus/blob/master/component/src/main/java/org/cbioportal/genome_nexus/component/annotation/ProteinChangeResolver.java#L95)
> - **Splice suppression:** [`resolveHgvsp()` (lines 244–260)](https://github.com/genome-nexus/genome-nexus/blob/master/component/src/main/java/org/cbioportal/genome_nexus/component/annotation/ProteinChangeResolver.java#L244)
> - **Tier 2 (HGVSc synthesis):** [`resolveHgvspShortFromHgvsc()` (lines 113–156)](https://github.com/genome-nexus/genome-nexus/blob/master/component/src/main/java/org/cbioportal/genome_nexus/component/annotation/ProteinChangeResolver.java#L113); cDNA regex at [line 41](https://github.com/genome-nexus/genome-nexus/blob/master/component/src/main/java/org/cbioportal/genome_nexus/component/annotation/ProteinChangeResolver.java#L41)
> - **Tier 3 (AA salvage):** [`resolveHgvspShortFromAAs()` (lines 159–227)](https://github.com/genome-nexus/genome-nexus/blob/master/component/src/main/java/org/cbioportal/genome_nexus/component/annotation/ProteinChangeResolver.java#L159)
> - **Splice site constant:** [`SPLICE_SITE_VARIANTS` (lines 27–29)](https://github.com/genome-nexus/genome-nexus/blob/master/component/src/main/java/org/cbioportal/genome_nexus/component/annotation/ProteinChangeResolver.java#L27)

---

## How is `variantClassification` resolved?

`variantClassification` is computed in three stages: consequence selection, variant type detection, and consequence-to-classification mapping. An optional fourth stage lets manually curated data override the result.

### Stage 1 — Select the consequence term

VEP returns a list of consequence terms for each transcript (e.g. `["missense_variant", "splice_region_variant"]`). Genome Nexus picks the **highest-priority** term using a ranked list defined in `TranscriptConsequencePrioritizer`. Lower rank number = higher severity:

| Rank | Consequence term(s) |
|---|---|
| 1 | `transcript_ablation`, `exon_loss_variant` |
| 2 | `splice_donor_variant`, `splice_acceptor_variant` |
| 3 | `stop_gained`, `frameshift_variant`, `stop_lost` |
| 4 | `start_lost`, `initiator_codon_variant` |
| 5 | `transcript_amplification` |
| 6 | `feature_elongation`, `feature_truncation` |
| 7 | `disruptive_inframe_insertion`, `disruptive_inframe_deletion`, `inframe_insertion`, `inframe_deletion` |
| 8 | `protein_altering_variant` |
| 9 | `missense_variant`, `conservative_missense_variant`, `rare_amino_acid_variant` |
| 10 | `splice_donor_5th_base_variant`, `splice_region_variant`, `splice_donor_region_variant`, `splice_polypyrimidine_tract_variant` |
| 11 | `synonymous_variant`, `start_retained_variant`, `stop_retained_variant` |
| 12+ | UTR, intronic, intergenic, and other modifier terms |

The consequence source depends on what VEP returned:
- If a specific transcript consequence is supplied → use its `consequence_terms`, pick the highest-priority term.
- Else if the annotation has `intergenic_consequences` (no transcript context) → use those terms.
- Else fall back to `most_severe_consequence` from the top-level VEP annotation.

### Stage 2 — Determine variant type and in-frame status

The variant type is derived by comparing the lengths of the reference and alternate alleles:

| Condition | Variant type |
|---|---|
| `ref.length == alt.length == 1` | `SNP` |
| `ref.length == alt.length == 2` | `DNP` |
| `ref.length == alt.length == 3` | `TNP` |
| `ref.length == alt.length > 3` | `ONP` |
| `ref.length < alt.length` | `INS` |
| `ref.length > alt.length` | `DEL` |

For insertions and deletions, Genome Nexus also checks whether the indel is **in-frame**:

```
in_frame = |ref.length − alt.length| % 3 == 0
```

A difference that is a multiple of 3 keeps the reading frame intact (`In_Frame_Ins` / `In_Frame_Del`); any other difference causes a frameshift (`Frame_Shift_Ins` / `Frame_Shift_Del`).

### Stage 3 — Map consequence term → variantClassification

The selected consequence term is mapped to a MAF style classification. Context dependent entries (marked with *) also require the variant type and in-frame result from Stage 2:

| VEP consequence term | variantClassification |
|---|---|
| `splice_acceptor_variant` | `Splice_Site` |
| `splice_donor_variant` | `Splice_Site` |
| `transcript_ablation` | `Splice_Site` |
| `exon_loss_variant` | `Splice_Site` |
| `splice_region_variant` | `Splice_Region` |
| `splice_donor_5th_base_variant` | `Splice_Region` |
| `splice_donor_region_variant` | `Splice_Region` |
| `splice_polypyrimidine_tract_variant` | `Splice_Region` |
| `stop_gained` | `Nonsense_Mutation` |
| `stop_lost` | `Nonstop_Mutation` |
| `start_lost` | `Translation_Start_Site` |
| `initiator_codon_variant` | `Translation_Start_Site` |
| `missense_variant` | `Missense_Mutation` |
| `conservative_missense_variant` | `Missense_Mutation` |
| `rare_amino_acid_variant` | `Missense_Mutation` |
| `coding_transcript_variant` | `Missense_Mutation` |
| `inframe_insertion`, `disruptive_inframe_insertion` | `In_Frame_Ins` |
| `inframe_deletion`, `disruptive_inframe_deletion` | `In_Frame_Del` |
| `frameshift_variant` + not in-frame + `INS` * | `Frame_Shift_Ins` |
| `frameshift_variant` + not in-frame + `DEL` * | `Frame_Shift_Del` |
| `frameshift_variant` + in-frame + `INS` * | `In_Frame_Ins` |
| `frameshift_variant` + in-frame + `DEL` * | `In_Frame_Del` |
| `protein_altering_variant` + not in-frame + `INS` * | `Frame_Shift_Ins` |
| `protein_altering_variant` + not in-frame + `DEL` * | `Frame_Shift_Del` |
| `protein_altering_variant` + in-frame (or SNP/DNP/TNP/ONP) * | `Missense_Mutation` |
| `coding_sequence_variant` + not in-frame + `INS` * | `Frame_Shift_Ins` |
| `coding_sequence_variant` + not in-frame + `DEL` * | `Frame_Shift_Del` |
| `coding_sequence_variant` + in-frame (or SNP/…) * | `Missense_Mutation` |
| `synonymous_variant`, `incomplete_terminal_codon_variant`, `nmd_transcript_variant` | `Silent` |
| `5_prime_utr_variant`, `5_prime_utr_premature_start_codon_gain_variant` | `5'UTR` |
| `3_prime_utr_variant` | `3'UTR` |
| `intron_variant`, `intragenic_variant` | `Intron` |
| `upstream_gene_variant` | `5'Flank` |
| `downstream_gene_variant` | `3'Flank` |
| `intergenic_variant`, `intergenic_region`, `regulatory_region_variant`, `tf_binding_site_variant` | `IGR` |
| `mature_mirna_variant`, `non_coding_transcript_exon_variant`, `non_coding_transcript_variant`, `nc_transcript_variant`, `exon_variant` | `RNA` |
| Any unmapped term, or variant type cannot be determined | `Targeted_Region` |

> **Note for developers:** The four-stage pipeline lives across three files:
>
> - **Consequence selection & in-frame check:** [`VariantClassificationResolver.java` (lines 37–113)](https://github.com/genome-nexus/genome-nexus/blob/master/component/src/main/java/org/cbioportal/genome_nexus/component/annotation/VariantClassificationResolver.java#L37)
> - **Priority ranking:** [`TranscriptConsequencePrioritizer.java` (lines 80–151)](https://github.com/genome-nexus/genome-nexus/blob/master/component/src/main/java/org/cbioportal/genome_nexus/component/annotation/TranscriptConsequencePrioritizer.java#L80)
> - **Consequence→classification mapping:** [`VariantClassificationResolver.java` (lines 185–256)](https://github.com/genome-nexus/genome-nexus/blob/master/component/src/main/java/org/cbioportal/genome_nexus/component/annotation/VariantClassificationResolver.java#L185)
> - **reVUE override & result storage:** [`VariantAnnotationSummaryServiceImpl.java` (lines 267–345)](https://github.com/genome-nexus/genome-nexus/blob/master/service/src/main/java/org/cbioportal/genome_nexus/service/internal/VariantAnnotationSummaryServiceImpl.java#L267)
> - **Variant type detection:** [`VariantTypeResolver.java` (lines 20–56)](https://github.com/genome-nexus/genome-nexus/blob/master/component/src/main/java/org/cbioportal/genome_nexus/component/annotation/VariantTypeResolver.java#L20)
>
> The resolved `variantClassification` is stored on `TranscriptConsequenceSummary` (for transcript-level results) and `IntergenicConsequenceSummary` (for intergenic variants), and is returned by the `/annotation/summary/{variant}` endpoint.

---

## What gets cached to MongoDB, and what does not?

In `application.properties`, you can configure caching by setting `cache.enabled=`.
- `cache.enabled=true`: Enables or disables caching for annotation sources such as vep.annotation, index, my_variant_info.annotation. Default value is true.
- `cache.enabled=false`: Queries bypass the cache and make direct calls to the web service, not saving any data to the database.

Failed VEP calls are never cached. Successful HTTP 200 responses from VEP are cached, even if `transcript_consequences` is null (e.g. for intergenic variants). Variants that fail with a specific error (e.g. reference allele mismatch) are also not cached — they will be re-queried on the next request.

| Scenario | Cached to MongoDB? |
|---|---|
| VEP returns valid response (any content) | **Yes** |
| VEP is down / returns 5xx | **No** |
| VEP returns 4xx (single variant) | **No** |
| Variant fails in batch (ref mismatch, parse error) | **No** |

A practical consequence: if a variant is intergenic, the empty annotation is cached on first lookup. All subsequent requests will serve that cached result without re-querying VEP. To force a re-fetch, delete the document from the `vep.annotation` MongoDB collection or temporarily set `cache.enabled=false`.

---

## What do the LEVEL values in the annotation pipeline error report mean?

When running `genome-nexus-annotation-pipeline`, records that could not be fully annotated are written to an error report TSV (controlled by the `-e` flag). The `LEVEL` column classifies each entry by severity:

| LEVEL | When it appears | Action needed? |
|---|---|---|
| `ERROR` | True annotation failures: VEP returned an error for the variant; the Genome Nexus request failed entirely; a `Frame_Shift` or `In_Frame` variant has empty HGVSc+HGVSp **and** VEP set an error message; or any `Missense_Mutation`, `Nonsense_Mutation`, `Splice_Site`, or `Splice_Region` variant has empty HGVSc+HGVSp (regardless of whether an error message is set) | Yes — investigate the `FAILURE_REASON` column |
| `WARN` | Ambiguous allele: the record has both an SNP and INDEL change at the same position; the SNP allele was used for annotation | Review whether the chosen allele is correct for your analysis |
| `INFO` | Expected empty HGVSc/HGVSp — either an inherently non-coding classification (3'UTR, 5'UTR, 3'Flank, 5'Flank, IGR, Intron, RNA), or a `Frame_Shift`/`In_Frame` deletion/insertion that spans a coding/UTR boundary **with no VEP error message** | No — this is expected VEP behavior |

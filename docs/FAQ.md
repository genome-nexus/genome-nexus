# Frequently Asked Questions

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

The diagram below shows exactly which bp positions each term covers, relative to the 5′ donor and 3′ acceptor splice junctions. Positions are numbered starting from ±1 immediately adjacent to the junction — there is no position 0 in HGVS notation. The intron interior is compressed (`···`) since its length varies by gene.

![VEP Splice Consequence Terms — Genomic Range](https://github.com/user-attachments/assets/68ac7f45-ab77-45ed-9bf1-e863072d5747)

- **RED** (`splice_donor_variant` / `splice_acceptor_variant`) — the 2 bp GT/AG dinucleotides at the intron ends; HIGH impact.
- **BLUE** (`splice_region_variant`) — appears in four segments: the 3 exon-proximal bases on each side of both junctions, plus intron positions +3 to +8 (donor) and −3 to −8 (acceptor).
- **ORANGE** (`splice_donor_5th_base_variant`) — exactly the +5 intron position after the donor.
- **GREEN** (`splice_donor_region_variant`) — intron positions +3 to +6 after the donor, overlapping with `splice_region_variant`; assigned by newer VEP versions.
- **PURPLE** (`splice_polypyrimidine_tract_variant`) — intron positions −3 to −17 before the acceptor (the polypyrimidine tract), partially overlapping with `splice_region_variant`.

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

- **VEP is down or unreachable** — any HTTP 5xx or connection error results in a placeholder annotation with this flag set to false.
- **VEP returns a 4xx error** — for example, a malformed variant string that VEP rejects.
- **Any unexpected exception** during annotation processing.

When `successfully_annotated: true`, it means VEP responded with HTTP 200 and the result was transformed without error — but note that a true value does **not** guarantee `transcript_consequences` is populated (see below).

> **Note for developers:** The flag is set by `setSuccessfullyAnnotated(true)` only on the path where `super.fetchAndCache(id)` returns a non-null `VariantAnnotation`. All exception paths (`HttpServerErrorException`, `HttpClientErrorException`, and the generic `catch (Exception e)`) create an empty `VariantAnnotation` without calling that setter, leaving the field false. There is no retry logic anywhere in the codebase — on VEP failure, the error is logged at ERROR level under `org.cbioportal.genome_nexus.service.cached.BaseCachedExternalResourceFetcher` and the placeholder is returned immediately.

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

Failed VEP calls are never cached. Successful HTTP 200 responses from VEP are always cached, even if `transcript_consequences` is null (e.g. for intergenic variants).

| Scenario | Cached to MongoDB? |
|---|---|
| VEP returns valid response (any content) | **Yes** |
| VEP is down / returns 5xx | **No** |
| VEP returns 4xx | **No** |

A practical consequence: if a variant is intergenic, the empty annotation is cached on first lookup. All subsequent requests will serve that cached result without re-querying VEP. To force a re-fetch, delete the document from the `vep.annotation` MongoDB collection or temporarily set `cache.enabled=false`.

# How Genome Nexus selects a canonical transcript

## **Background**
When a variant is sent to Genome Nexus, Genome Nexus calls **VEP** (Variant Effect Predictor)to get a range of annotations associated with the variant, including a list of potential transcripts.

Genome Nexus then uses a set of predefined criteria to select the canonical transcript from this list of transcripts. The selection process is guided by the user-selected isoform override source and a prioritization logic.

The canonical transcript is in the `transcriptConsequenceSummary` field, under the `annotation_summary` section of the response. To ensure the canonical transcript is returned, the `annotation_summary` field must be requested in the query sent to Genome Nexus.

Example response structure:
```
"annotation_summary" {
    "transcriptConsequenceSummary" {
        ...
    }
}
```

### Available isoform override sources
The isoform override source can be defined in `isoformOverrideSource` field, options are:
- `mskcc`
- `uniprot` (default)
- `genome_nexus`
- `ensembl`

The list of canonical transcript isoform override for each gene can be found here, which is genereated by this [script](https://github.com/genome-nexus/genome-nexus-importer/blob/master/scripts/make_one_canonical_transcript_per_gene.py): 
- GRCh37: https://github.com/genome-nexus/genome-nexus-importer/blob/master/data/grch37_ensembl92/export/ensembl_biomart_canonical_transcripts_per_hgnc.txt
- GRCh38: https://github.com/genome-nexus/genome-nexus-importer/blob/master/data/grch38_ensembl95/export/ensembl_biomart_canonical_transcripts_per_hgnc.txt
and 
https://github.com/genome-nexus/genome-nexus-importer/blob/master/data/grch38_ensembl92/export/ensembl_biomart_canonical_transcripts_per_hgnc.txt


The original isoform override files can be found here:
- `mskcc`:
    - GRCh37: https://github.com/genome-nexus/genome-nexus-importer/blob/master/data/common_input/isoform_overrides_oncokb_grch37.txt 
    and 
    https://github.com/genome-nexus/genome-nexus-importer/blob/master/data/common_input/isoform_overrides_oncokb_grch37.txt
    - GRCh38: https://github.com/genome-nexus/genome-nexus-importer/blob/master/data/common_input/isoform_overrides_at_mskcc_grch38.txt
    and
    https://github.com/genome-nexus/genome-nexus-importer/blob/master/data/common_input/isoform_overrides_oncokb_grch38.txt
- `uniprot`:
    - https://github.com/genome-nexus/genome-nexus-importer/blob/master/data/common_input/isoform_overrides_uniprot.txt
- `genome_nexus`:
    - GRCh37: https://github.com/genome-nexus/genome-nexus-importer/blob/master/data/common_input/isoform_overrides_genome_nexus_grch37.txt
    - GRCh38: https://github.com/genome-nexus/genome-nexus-importer/blob/master/data/common_input/isoform_overrides_genome_nexus_grch38.txt


---

## Overview
![How Genome Nexus selects canonical transcript (1)](https://github.com/user-attachments/assets/b0df1faa-9819-462a-aa16-0c3e61db08f0)

### **Step 1: Send a variant**
- When a variant is sent to Genome Nexus, there are some other parameters sending in the query as well, such `isoformOverrideSource` and `fields`.
- Check if there is an `isoformOverrideSource` provided in the query.
  - If no isoform override source: use `uniprot` as the default.
- Genome Nexus calls `VEP` to get annotations for the variant.

---
### **Step 2: Retrieve Transcripts**
- Genome Nexus retrieves the isoform override transcript list from the database based on the selected isoform override source. For example, if `mskcc` is selected, the system fetches the mskcc isoform override transcript list, which contains the canonical transcript overrides for each HGNC symbol.
- In the VEP response, a list of potential transcripts is returned.

---

### **Step 3: Select canonical transcript**

#### **3.1 Find in isoform override transcript list**
- Check if any of the transcripts in the VEP transcript list match transcripts in the isoform override list.
    - If there are only 1 transcript in the isoform override list is found, this transcript is picked as canonical transcript.
    - If no transcripts or multiple transcripts found: proceed to step 3.2.

#### **3.2 Prioritize cancer gene transcripts**
- Filter the transcript list by [OncoKB](https://www.oncokb.org/) gene, this ensures transcripts of cancer-associated genes are prioritized.
  - Are there any transcripts left after filtering?
    - If only 1 transcript remains: the process terminates, this transcript is selected as canonical.
    - If no transcripts or multiple transcripts remain: proceed to the step 3.3.

#### **3.3 Find by most severe consequence**
- Find transcripts based on the `most_severe_consequence` of the variant.
  - Are there any transcripts whose `consequence_terms` match the `most_severe_consequence`?
    - If only 1 transcript is found: the process terminates, this transcript is selected as  canonical.
    - If no transcripts or multiple transcripts are found: proceed to the step 3.4.

#### **3.4 Find by highest priority transcript**
- Find transcript with the highest priority, the priority of the transcript is predefined [here](https://github.com/genome-nexus/genome-nexus/blob/master/component/src/main/java/org/cbioportal/genome_nexus/component/annotation/TranscriptConsequencePrioritizer.java#L80-L150).
  - Is there a clear highest priority transcript?
    - Yes: the process terminates, this transcript is selected as canonical.
    - No: proceed to step 3.5.

#### **3.5 Return the first transcript**
- If none of the criteria are met, the first transcript from the list is returned as the canonical transcript.

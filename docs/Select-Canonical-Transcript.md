# How Genome Nexus selects a canonical transcript

## **Background**
When a variant is sent to Genome Nexus, Genome Nexus calls **VEP** (Variant Effect Predictor) to get a range of annotations associated with the variant. Most of these annotations are associated with a particular transcript. For example. [7:g.55249071C>T](https://www.genomenexus.org/variant/7:g.55249071C%3ET) is `c.2369C>T / p.T790M` in `ENST00000275493.2`, but `c.2210C>T / p.T737M` for transcript `ENST00000454757.2`.

Genome Nexus aims to select one transcript (the canonical transcript) from this list of transcripts to have a default most relevant to oncology. The selection process is guided by a curated list of paired gene and transcript preferences (e.g., `EGFR` and `ENST00000275493.2`) and additional prioritization logic that emphasizes mutations with a high impact (e.g., in coding regions and/or targeting cancer-related genes). The curated list of gene and transcripts is called the isoform override list.

### Available isoform override sources
The isoform override source can be defined in `isoformOverrideSource` field, options are:
- `mskcc`
- `uniprot` (default)
- `genome_nexus`
- `ensembl`

The list of canonical transcript isoform override for each gene can be found here, which is genereated by this [script](https://github.com/genome-nexus/genome-nexus-importer/blob/master/scripts/make_one_canonical_transcript_per_gene.py): 
- GRCh37: [canonical_transcripts_per_hgnc.txt](https://github.com/genome-nexus/genome-nexus-importer/blob/master/data/grch37_ensembl92/export/ensembl_biomart_canonical_transcripts_per_hgnc.txt)
- GRCh38: 
  - [canonical_transcripts_per_hgnc.txt (ensembl95)](https://github.com/genome-nexus/genome-nexus-importer/blob/master/data/grch38_ensembl95/export/ensembl_biomart_canonical_transcripts_per_hgnc.txt)
  - [canonical_transcripts_per_hgnc.txt (ensembl92)](https://github.com/genome-nexus/genome-nexus-importer/blob/master/data/grch38_ensembl92/export/ensembl_biomart_canonical_transcripts_per_hgnc.txt)


The isoform override files can be found here:
- `mskcc`:
    - GRCh37: 
      - [isoform_overrides_oncokb_grch37.txt](https://github.com/genome-nexus/genome-nexus-importer/blob/master/data/common_input/isoform_overrides_oncokb_grch37.txt)
        - Source: [OncoKB curated genes](https://www.oncokb.org/api/v1/utils/allCuratedGenes.txt?includeEvidence=false)
        - Description: This file contains the GRCh37 OncoKB isoform canonical transcript list. It is the primary choice when `mskcc` is selected as the isoform override source.
      - [isoform_overrides_at_mskcc_grch37.txt](https://github.com/genome-nexus/genome-nexus-importer/blob/master/data/common_input/isoform_overrides_at_mskcc_grch37.txt)
        - Source: [MSKCC VCF2MAF Isoform Overrides](https://github.com/mskcc/vcf2maf/blob/main/data/isoform_overrides_at_mskcc)
        - Description: This file contains the GRCh37 mskcc isoform canonical transcript list. If a transcript is not present in the OncoKB canonical transcript list, this file is used as a fallback.

    - GRCh38: 
      - [isoform_overrides_oncokb_grch38.txt](https://github.com/genome-nexus/genome-nexus-importer/blob/master/data/common_input/isoform_overrides_at_mskcc_grch38.txt)
        - Source: [OncoKB curated genes](https://www.oncokb.org/api/v1/utils/allCuratedGenes.txt?includeEvidence=false)
        - Description: This file contains the GRCh38 OncoKB isoform canonical transcript list. It is the primary choice when `mskcc` is selected as the isoform override source.
      - [isoform_overrides_at_mskcc_grch38.txt](https://github.com/genome-nexus/genome-nexus-importer/blob/master/data/common_input/isoform_overrides_genome_nexus_grch38.txt)
        - Source: [MSKCC VCF2MAF Isoform Overrides](https://github.com/mskcc/vcf2maf/blob/main/data/isoform_overrides_at_mskcc_grch38)
        - Description: This file contains the GRCh38 MSKCC isoform canonical transcript list. If a transcript is not present in the OncoKB canonical transcript list, this file is used as a fallback.

- `uniprot`:
    - [isoform_overrides_uniprot.txt](https://github.com/genome-nexus/genome-nexus-importer/blob/master/data/common_input/isoform_overrides_uniprot.txt)
      - Source: [Uniprot VCF2MAF Isoform Overrides](https://github.com/mskcc/vcf2maf/blob/main/data/isoform_overrides_uniprot)
      - Description: This file contains the UniProt isoform canonical transcript list. When `uniprot` is selected as the isoform override source, the transcript selection follows this priority order:
        1. isoform_overrides_genome_nexus.txt
        2. isoform_overrides_uniprot.txt
- `genome_nexus`:
    - GRCh37: [isoform_overrides_genome_nexus_grch37.txt](https://github.com/genome-nexus/genome-nexus-importer/blob/master/data/common_input/isoform_overrides_genome_nexus_grch37.txt)
      - Description: This file contains the GRCh37 genome_nexus isoform canonical transcript list. It is the primary choice when `genome_nexus` is selected as the isoform override source.
    - GRCh38: [isoform_overrides_genome_nexus_grch38.txt](https://github.com/genome-nexus/genome-nexus-importer/blob/master/data/common_input/isoform_overrides_genome_nexus_grch38.txt)
      - Description: This file contains the GRCh38 genome_nexus isoform canonical transcript list. It is the primary choice when `genome_nexus` is selected as the isoform override source.


---

## Overview
![How Genome Nexus selects canonical transcript (1)](https://github.com/user-attachments/assets/b0df1faa-9819-462a-aa16-0c3e61db08f0)

### **Step 1: Send a variant**
- When a variant is sent to Genome Nexus, there are some other parameters sent in the query as well, such `isoformOverrideSource` and `fields`.
![step1](https://github.com/user-attachments/assets/3158ec3d-8c6f-4bf7-8fa7-ea5d57455e5c)

---
### **Step 2: Retrieve Transcripts**
- Genome Nexus retrieves the isoform override transcript list from the database based on the selected isoform override source. For example, if `mskcc` is selected, the system fetches the mskcc isoform override transcript list, which contains the canonical transcript overrides for each HGNC symbol.
- In the VEP response, a list of potential transcripts is returned.
![step2](https://github.com/user-attachments/assets/1e6d8e1b-6aa4-417c-9969-434f4feacba2)

---

### **Step 3: Select canonical transcript**

#### **3.1 Find in isoform override transcript list**
- Check if any of the transcripts in the VEP transcript list match transcripts in the isoform override list.
    - If there are only 1 transcript in the isoform override list is found, this transcript is picked as canonical transcript.
    - If no transcripts or multiple transcripts found: proceed to step 3.2.
    ![step3 1](https://github.com/user-attachments/assets/64f768d8-dfd7-42dd-ba6f-887baad6d8ad)

#### **3.2 Prioritize cancer gene transcripts**
- Filter the transcript list by [OncoKB](https://www.oncokb.org/) gene, this ensures transcripts of cancer-associated genes are prioritized.
  - Are there any transcripts left after filtering?
    - If only 1 transcript remains: the process terminates, this transcript is selected as canonical.
    - If no transcripts or multiple transcripts remain: proceed to the step 3.3.
    ![step3 2](https://github.com/user-attachments/assets/a78f9a6a-daec-4489-a5e5-628f7afcddf2)


#### **3.3 Find by most severe consequence**
- Find transcripts based on the `most_severe_consequence` of the variant.
  - Are there any transcripts whose `consequence_terms` match the `most_severe_consequence`?
    - If only 1 transcript is found: the process terminates, this transcript is selected as  canonical.
    - If no transcripts or multiple transcripts are found: proceed to the step 3.4.
    ![step3 3](https://github.com/user-attachments/assets/702b8afa-55de-446d-b82f-025d54f4b84c)

#### **3.4 Find by highest priority transcript**
- Find transcript with the highest priority, the priority of the transcript is predefined [here](https://github.com/genome-nexus/genome-nexus/blob/master/component/src/main/java/org/cbioportal/genome_nexus/component/annotation/TranscriptConsequencePrioritizer.java#L80-L150).
  - Is there a clear highest priority transcript?
    - Yes: the process terminates, this transcript is selected as canonical.
    - No: proceed to step 3.5.
    ![step3 4](https://github.com/user-attachments/assets/87b4409c-5120-476b-81ce-bf43934f55be)


#### **3.5 Return the first transcript**
- If none of the criteria are met, the first transcript from the list is returned as the canonical transcript.
![step3 5](https://github.com/user-attachments/assets/fca2b62d-ed96-465f-90ef-d793cac3d2ed)

## Get canonical transcript programmatically 
Canonical transcript is returned in annotation endpoints, see [doc](https://docs.genomenexus.org/api#canonical-transcript) for more details.
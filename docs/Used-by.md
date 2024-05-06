# Used By

Genome Nexus is used by a number of organizations and projects:

- **[cBioPortal](https://cbioportal.org/)**

- **[AACR GENIE](https://genie.synapse.org/)**

- **[OncoKB](https://www.oncokb.org/)**

- **[Memorial Sloan Kettering Cancer Center](https://mskcc.org/)**
    - **Tempo**
    
    Time-Efficient Mutational Profiling in Oncology (Tempo) is a computational pipeline for processing data of paired-end whole-exome (WES) and whole-genome sequencing (WGS) of human cancer samples with matched normals. Its components are containerized and the pipeline runs on the [Juno high-performance computing cluster](http://mskcchpc.org/display/CLUS/Juno+Cluster+Guide) at Memorial Sloan Kettering Cancer Center and on [Amazon Web Services](https://cmotempo.netlify.app/#:~:text=Amazon%20Web%20Services,opens%20new%20window) (AWS). The pipeline was written by members of the [Center for Molecular Oncology](https://www.mskcc.org/research-programs/molecular-oncology).
    
    - **Argos**
    
    The [ARGOS](https://github.com/mskcc/argos-cwl/) pipeline, developed by the CMO/BIC group at Memorial Sloan Kettering Cancer Center, is an informatics pipeline designed for analysis of cancer genomics data, specifically from the MSK-IMPACT targeted suite of assays. The pipeline operates on tumor/normal pairs and is designed to be reproducible and portable. The inputs are FASTQ files and BWA is used for sequence alignment to the GRCh37 reference genome, followed by ABRA for realignment.
    
    - **MSK-ACCESS**
    
    Developed by scientists in the CMO Technology Innovation Lab and Department of Pathology, this high-sensitivity assay is offered by the CMO to MSK researchers for profiling circulating tumor DNA derived from blood plasma. The inclusion of matched buffy coat DNA enables the identification and elimination of germline variants and mutations associated with clonal hematopoiesis, a significant confounder of most commercial assays. The assay is available for clinical use in the Molecular Diagnostics Service and for research projects in the Integrated Genomics Operations (IGO). CCI supports the data processing and analysis of research projects utilizing MSK-ACCESS in IGO and leads the ongoing development of the MSK-ACCESS pipeline for all applications. The current version of the pipeline is available here: [mskcc/ACCESS-Pipeline: cfDNA Sequencing Pipeline with UMI (github.com)](https://github.com/mskcc/ACCESS-Pipeline), and more details about the assay and analysis are described in this paper below as well as here: [Brannon, A. R. et al. Enhanced specificity of clinical high-sensitivity tumor mutation profiling in cell- free DNA via paired normal sequencing using MSK-ACCESS. Nat Commun 12, 3770 (2021)](https://www.nature.com/articles/s41467-021-24109-5)

    - **MSK-IMPACT**
    
    [MSK-IMPACT](https://www.nature.com/articles/nm.4333) is a custom hybridization capture–based assay encompassing all genes that are druggable by approved therapies or are targets of experimental therapies being investigated in clinical trials at Memorial Sloan Kettering Cancer Center (MSKCC), as well as frequently mutated genes in human cancer (somatic and germline mutations). MSK-IMPACT is capable of detecting sequence mutations, small insertions and deletions, copy number alterations and select structural rearrangements, and it has been validated and approved for clinical use by the New York State Department of Health Clinical Laboratory Evaluation Program. Samples are processed, followed by sequencing and analysis. Sequencers are monitored by an automated data management system, which initiates the analysis pipeline upon the end of the sequencing run. Each alteration identified by the pipeline is manually reviewed to ensure accuracy. Sequencing results are stored in a clinical-grade database and reported back to patients and physicians through the electronic medical record. Between January 2014 and May 2016, 12,670 tumor samples from 11,369 unique patients were submitted for MSK-IMPACT sequencing. Of these, 10,945 cases were successfully sequenced for a final assay success rate of 86%.

    - **CMO-CH assay**
    
    The CMO-CH assay, developed by scientists at the Center for Molecular Oncology (CMO) Technology Innovation Lab in collaboration with CMO cfDNA Informatics (CCI), the Clonal Hematopoiesis (CH) program, and Diagnostic Molecular Pathology, utilizes the same barcoding and ultra-deep sequencing technology as MSK-ACCESS. It is designed to detect Clonal Hematopoiesis (CH) mutations in white blood cells with high sensitivity.
    The CMO-CH assay is specifically offered by the CMO to MSK researchers for profiling white blood cell DNA and identifying mutations in the most commonly altered CH-associated genes. It is available for research projects through the Integrated Genomics Operations (IGO). CCI provides support for the data processing and analysis of these projects.
    To analyze the data generated from the CMO-CH assay, a workflow is utilized. You can find the workflow at the following link: [https://github.com/msk-access/chip-var](https://github.com/msk-access/chip-var).
 

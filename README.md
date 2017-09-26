[![Build Status](https://travis-ci.org/cBioPortal/genome-nexus.svg?branch=master)](https://travis-ci.org/cBioPortal/genome-nexus)
[![codecov](https://codecov.io/gh/cBioPortal/genome-nexus/branch/master/graph/badge.svg)](https://codecov.io/gh/cBioPortal/genome-nexus)
[![codebeat badge](https://codebeat.co/badges/44bc0446-8b33-411d-bc33-f96ef66cb895)](https://codebeat.co/projects/github-com-cbioportal-genome-nexus-master)

[![Deploy](https://www.herokucdn.com/deploy/button.svg)](https://heroku.com/deploy)

# Genome Nexus
Genome Nexus, a comprehensive one-stop resource for fast, automated and
high-throughput annotation and interpretation of genetic variants in cancer.
Genome Nexus will integrate information from a variety of existing resources,
including databases that convert DNA changes to protein changes, predict the
functional effects of protein mutations, and contain information about mutation
frequencies, gene function, variant effects, and clinical actionability.

Three goals:

1. Data collection from various annotation sources
2. Integration of heterogeneous information into a harmonized structure and
programmatic interface
3. Dissemination of the diverse information in a hierarchical digestible way
for interpreting variants and patients.

## Run
```
mvn clean install
java -jar annotation/target/annotation-*.war
```

## Data Download

### Ensemble Biomart

Ensembl Biomart file is required by the PFAM endpoint. In order to download this file
follow these steps:

1. Go to the [Biomart](www.ensembl.org/biomart/martview) page on the Ensemble website.
2. Select `Ensemble Genes` from the `Database` dropdown menu.
3. Select `Human Genes` from the `Dataset` dropdown menu.
4. Click on `Attributes`, and select these ones:
Gene stable ID, Transcript stable Id, Protein stable Id, Gene name, Pfam domain ID, Pfam domain start, Pfam domain end.
5. Click on `Results`, and export all results to a `TSV` file.

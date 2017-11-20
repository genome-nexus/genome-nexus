[![codecov](https://codecov.io/gh/genome-nexus/genome-nexus/branch/master/graph/badge.svg)](https://codecov.io/gh/genome-nexus/genome-nexus)
[![codebeat badge](https://codebeat.co/badges/d599b538-43e3-4828-8f27-820031393196)](https://codebeat.co/projects/github-com-genome-nexus-genome-nexus-master)

[![Deploy](https://www.herokucdn.com/deploy/button.svg)](https://heroku.com/deploy)

| branch | master | rc |
| --- | --- | --- |
| status | [![Build Status](https://travis-ci.org/genome-nexus/genome-nexus.svg?branch=master)](https://travis-ci.org/genome-nexus/genome-nexus/branches) | [![Build Status](https://travis-ci.org/genome-nexus/genome-nexus.svg?branch=rc)](https://travis-ci.org/genome-nexus/genome-nexus/branches) |

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
java -jar web/target/web-*.war
```

## Data Download
If you need to update the included date files see [data/README.md](data/README.md)

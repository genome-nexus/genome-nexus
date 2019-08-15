# Genome Nexus 🧬

Genome Nexus, a comprehensive one-stop resource for fast, automated and
high-throughput annotation and interpretation of genetic variants in cancer.
Genome Nexus integrates information from a variety of existing resources,
including databases that convert DNA changes to protein changes, predict the
functional effects of protein mutations, and contain information about mutation
frequencies, gene function, variant effects, and clinical actionability.

## Architecture 🔧

![Screen Shot 2019-08-15 at 4 21 41 PM](https://user-images.githubusercontent.com/1334004/63124264-ddd1b680-bf78-11e9-9122-514330e8fcd8.png)

### Backend

Genome Nexus aggregates variant annotation from various sources. There are two types:

1. Small sized annotations are stored as static data in the mongo database directly, see the [genome-nexus-importer repo](https://github.com/genome-nexus/genome-nexus-importer) if you want to update/change this data.
2. Larger annotation sources are pulled on the fly from other APIs and cached in the mongo database.

For a list of all supported variant annotation sources see this Google Sheet:

https://docs.google.com/spreadsheets/d/1xML949NWzJGcvltjlquwSRIv79o13C_sfrVPAU5ci9Q/edit#gid=258442188

### REST API

Genome Nexus provides a [REST API](https://www.genomenexus.org/swagger-ui.html) for variant annotation.

### API Clients

Clients in various languages can be generated to access it. For programmatic access with R/Python see the [notebooks directory](notebooks/).

### Genome Nexus Website

The new frontend currently under construction is being developed here: https://github.com/genome-nexus/genome-nexus-frontend.

### Other Websites

The main consumer of the Genome Nexus REST API is [cBioPortal](https://cbioportal.org). cBioPortal provides visualization, analysis, and download of large-scale cancer genomics data sets. Variants in cBioPortal are annotated using Genome Nexus. The other examples in the figure are potential other consumers.

### Command Line Interface

There is a command line tool to annotate a MAF file (VCF not available yet): https://github.com/genome-nexus/genome-nexus-annotation-pipeline

## Run :computer:

### Alternative 1 - run genome-nexus, mongoDB and genome-nexus-vep in docker containers
First, set environment variables for Ensembl Release, VEP Assembly and location of VEP Cache. If these are not, the default values from `.env` will be set.

The reference genome and Ensembl release must be consistent with a version in [genome-nexus-importer/data/](https://github.com/genome-nexus/genome-nexus-importer/tree/rc/data).
For example `grch37_ensembl92`, `grch38_ensembl92` or `grch38_ensembl95`:
```
export REF_ENSEMBL_VERSION=grch38_ensembl92
```

If you would like to do local VEP annotations instead of using the public Ensembl API, please uncomment `# gn_vep.region.url=http://localhost:6060/vep/human/region/VARIANT` in your `application.properties`. This will require you to download the VEP cache files for the preferred Ensembl Release and Reference genome, see our documentation on [downloading the Genome Nexus VEP Cache](https://github.com/genome-nexus/genome-nexus-vep/blob/master/README.md#create-vep-cache). This will take several hours.
```
# Set local cache dir
export VEP_CACHE=<local_vep_cache>

# GRCh38 or GRCh37
export VEP_ASSEMBLY=GRCh38
```

Run docker-compose to create images and containers:
```
docker-compose up --build -d
```

Run without recreating images:
```
docker-compose up -d
```

Run without Genome Nexus VEP:
```
# Start both the Web and DB (dependency of Web) containers
docker-compose up -d web
```

Stop and remove containers:
```
docker-compose down
```

### Alternative 2 - run genome-nexus locally, but mongoDB in docker container
```
# the genomenexus/gn-mongo images comes with all the required tables imported
# change latest to different version if necessary (only need to run this once)
docker run --name=gn-mongo --restart=always -p 27017:27017 -d genomenexus/gn-mongo:latest 
mvn  -DskipTests clean install
java -jar web/target/web-*.war
```

### Alternative 3 - install mongoDB locally and run with local java
Install mongoDB manually. Then follow instructions in
[genome-nexus-importer](https://github.com/genome-nexus/genome-nexus-importer)
to initialize the database.

After that run this:
```
mvn clean install
java -jar web/target/web-*.war
```

## Test Status

| branch | master | rc |
| --- | --- | --- |
| status | [![Build Status](https://travis-ci.org/genome-nexus/genome-nexus.svg?branch=master)](https://travis-ci.org/genome-nexus/genome-nexus/branches) | [![Build Status](https://travis-ci.org/genome-nexus/genome-nexus.svg?branch=rc)](https://travis-ci.org/genome-nexus/genome-nexus/branches) |

## Deploy

[![Deploy](https://www.herokucdn.com/deploy/button.svg)](https://heroku.com/deploy)


## Programmatic access through R/Python
See [notebooks/](notebooks/)

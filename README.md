# Genome Nexus 🧬

Genome Nexus, a comprehensive one-stop resource for fast, automated and
high-throughput annotation and interpretation of genetic variants in cancer.
Genome Nexus integrates information from a variety of existing resources,
including databases that convert DNA changes to protein changes, predict the
functional effects of protein mutations, and contain information about mutation
frequencies, gene function, variant effects, and clinical actionability.

## Documentation 📖
See the [docs](https://docs.genomenexus.org)

## Run 💻

### Alternative 1 - run genome-nexus, mongoDB and genome-nexus-vep in docker containers
First, set environment variables for Ensembl Release, VEP Assembly, location of VEP Cache, and species (since a mouse instalation is supported). If these are not, the default values from `.env` will be set.

The reference genome and Ensembl release must be consistent with a version in [genome-nexus-importer/data/](https://github.com/genome-nexus/genome-nexus-importer/tree/master/data).
For example `grch37_ensembl92`, `grch38_ensembl92` or `grch38_ensembl95`:
```
export REF_ENSEMBL_VERSION=grch38_ensembl92
```
If you want to setup Genome Nexus for mouse, also set the `SPECIES` variable to 'mus_musculus'. Also see the [docs](https://github.com/genome-nexus/genome-nexus-importer/blob/master/docs/setup-genome-nexus-mouse.md) to create a mouse database.
```bash
export SPECIES=mus_musculus
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

## Examples
The Genome Nexus website's variant page uses the HGVS format to describe variants. The Genome Nexus API supports multiple input formats.
- HGVS: For `GET /annotation/{variant}` and `POST /annotation` endpoint, Genome Nexus follows [HGVS](https://hgvs-nomenclature.org/stable/recommendations/general/) format.
- Genomic change: For `GET /annotation/genomic/{genomicLocation}` and  `POST /annotation/genomic` endpoint, Genome Nexus uses a comma separated version of [MAF](https://docs.gdc.cancer.gov/Encyclopedia/pages/Mutation_Annotation_Format_TCGAv2/) from TCGA (chromosome, start_postion, end_postion, reference_allle, variant_allele).

|Type| HGVS |Genomic change|API response|Variant page|
|--|--|--|--|--|
|Substitution| `7:g.140453136A>T`|`7,140453136,140453136,A,T`|[7:g.140453136A>T](https://www.genomenexus.org/annotation/7:g.140453136A%3ET?fields=hotspots,annotation_summary,my_variant_info,clinvar,signal,mutation_assessor)|[7:g.140453136A>T](https://www.genomenexus.org/variant/7:g.140453136A%3ET)
|Deletion| `3:g.52439259del` or `1:g.27105878_27105881del`| `3,52439259,52439259,G,-` or `1,27105878,27105881,AGCT,-`|[3:g.52439259del](https://www.genomenexus.org/variant/3:g.52439259del), [1:g.27105878_27105881del](https://www.genomenexus.org/variant/1:g.27105878_27105881del)|[3:g.52439259del](https://www.genomenexus.org/annotation/3:g.52439259del?fields=hotspots,annotation_summary,my_variant_info,clinvar,signal,mutation_assessor), [1:g.27105878_27105881del](https://www.genomenexus.org/annotation/1:g.27105878_27105881del?fields=hotspots,annotation_summary,my_variant_info,clinvar,signal,mutation_assessor)
|Duplication| `9:g.21970956dup` or `9:g.21970956_21970957dup`| `9,21970956,21970956,C,CC`or`9,21970956,21970957,CG,CGCG`|[9:g.21970956dup](https://www.genomenexus.org/variant/9:g.21970956dup), [9:g.21970956_21970957dup](https://www.genomenexus.org/variant/9:g.21970956_21970957dup) | [9:g.21970956dup](https://www.genomenexus.org/annotation/9:g.21970956dup?fields=hotspots,annotation_summary,my_variant_info,clinvar,signal,mutation_assessor), [9:g.21970956_21970957dup](https://www.genomenexus.org/annotation/9:g.21970956_21970957dup?fields=hotspots,annotation_summary,my_variant_info,clinvar,signal,mutation_assessor)
|Insertion| `17:g.41242962_41242963insGA`|`17,41242962,41242963,-,GA`|[17:g.41242962_41242963insGA](https://www.genomenexus.org/variant/17:g.41242962_41242963insGA) | [17:g.41242962_41242963insGA](https://www.genomenexus.org/annotation/17:g.41242962_41242963insGA?fields=hotspots,annotation_summary,my_variant_info,clinvar,signal,mutation_assessor)
|Inversion| `X:g.66937331_66937332inv`|`X,66937331,66937332,TT,AA`| [X:g.66937331_66937332inv](https://www.genomenexus.org/variant/X:g.66937331_66937332inv)|[X:g.66937331_66937332inv](https://www.genomenexus.org/annotation/X:g.66937331_66937332inv?fields=hotspots,annotation_summary,my_variant_info,clinvar,signal,mutation_assessor)
|Deletion-Insertion| `4:g.1803568_1803569delinsG`|`4,1803568,1803569,CC,G`|[4:g.1803568_1803569delinsG](https://www.genomenexus.org/variant/4:g.1803568_1803569delinsG)|[4:g.1803568_1803569delinsG](https://www.genomenexus.org/annotation/4:g.1803568_1803569delinsG?fields=hotspots,annotation_summary,my_variant_info,clinvar,signal,mutation_assessor)


## Test Status 👷‍♀️

| branch | master | rc |
| --- | --- | --- |
| status | [![Build Status](https://travis-ci.org/genome-nexus/genome-nexus.svg?branch=master)](https://travis-ci.org/genome-nexus/genome-nexus/branches) | [![Build Status](https://travis-ci.org/genome-nexus/genome-nexus.svg?branch=rc)](https://travis-ci.org/genome-nexus/genome-nexus/branches) |

## Deploy 🚀

[![Deploy](https://www.herokucdn.com/deploy/button.svg)](https://heroku.com/deploy)

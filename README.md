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

## Pre-requisites

#### Mongo DB

##### Alternative 1 - install mongoDB
Install mongoDB manually.

##### Alternative 2 - start mongoDB in a docker container (assuming genome-nexus is running locally, development mode)
Start with docker:
```
docker network create gn-net
docker run --name=gn-mongo --restart=always --net=gn-net -p 27017:27017 -d mongo
```
Enable the following line in `application.properties`:
```
spring.data.mongodb.import.command.prefix=docker run --rm --net=gn-net -v ${user.dir}:${user.dir} -v /tmp:/tmp mongo mongoimport --uri mongodb://gn-mongo:27017/annotator
```
##### Alternative 3 - start genome-nexus and mongoDB in docker containers (production mode)
Create common docker network:
```
docker network create gn-net
```
Run your genome-nexus container using the network above (`--net` parameter in docker).

Run the mongoDB container:
```
docker run --name=gn-mongo --restart=always --net=gn-net -d mongo
```
**Only** change the following in `application.properties` (so do **not** enable the property used in Alternative 2 above):
```
spring.data.mongodb.uri=mongodb://gn-mongo:27017/annotator
```

## Run
```
mvn clean install
java -jar web/target/web-*.war
```

## Data Download
If you need to update the included date files see [data/README.md](data/README.md)

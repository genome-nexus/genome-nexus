# Annotation Sources 🗄️
Genome Nexus aggregates variant annotation from various sources. There are two types:

1. Small sized annotations are stored as static data in the mongo database
directly. See the [genome-nexus-importer repo](https://github.com/genome-nexus/genome-nexus-importer) if you want to
update/change this data.
2. For larger annotation services/databases we either run the full annotation
service ourselves or the program pulls on the fly from other APIs and caches
the result in the mongo database.

For a list of all supported variant annotation sources see this Google Sheet:

https://docs.google.com/spreadsheets/d/1xML949NWzJGcvltjlquwSRIv79o13C_sfrVPAU5ci9Q/edit#gid=258442188

## Versions

The mirrored annotation sources are either stored directly in the mongo database
or we run the annotation service ourselves. In both cases the version is
guaranteed. For the external annotion sources an API is called and
responses are cached on a per request basis. Given that the external API is
outside of our control, no particular version is guaranteed, but we provide
links to documentation of those APIs to find the currently live version.

See all versions from `version` [API](https://www.genomenexus.org/version).
# API ⚙️
A full listing of all API endpoints can be found here:

https://www.genomenexus.org/swagger-ui.html.

## API Clients
For access through Python/R see the notebooks here:

https://github.com/genome-nexus/genome-nexus/tree/master/notebooks

## Command Line Clients
There are two Command Line Clients available. One written in NodeJS which provides a simple user-friendly interface:

https://github.com/genome-nexus/genome-nexus-cli

There is also a Java Command Line Client which has been developed with a focus on internal processes at Memorial Sloan Kettering Cancer Center but might work for your use case as well:

https://github.com/genome-nexus/genome-nexus-annotation-pipeline

They are similar in terms of feature completeness

## Annotation API
### Send a variant
- When a variant is sent to Genome Nexus, there are some other parameters sent in the query as well, such `isoformOverrideSource` and `fields`.
- Check if there is an `isoformOverrideSource` provided in the query.
  - If no isoform override source: use `uniprot` as the default.
- Genome Nexus calls `VEP` to get annotations for the variant.

#### Available enrichment fields
- annotation_summary:
This field is essential for most annotation processes. **Canonical transcript** is returned in `annotation_summary`.
- my_variant_info
Provide **gnomAD** and other annotations from [MyVariant.Info](https://myvariant.info/).
- clinvar
- hotspots
- polyphen
- sift
- mutation_assessor
`mutation_assessor` provides V4 version annotation from Mutation Assessor.
- nucleotide_context
- ptm
- signal:
Provide annotation from [signalDB](https://www.signaldb.org/).
- oncokb:
`oncokb` provides annotations of the biological consequences and clinical implications from OncoKB website. OncoKB token is required (see more information from: https://www.oncokb.org/apiAccess). Please also provide your token in -Doncokb.token=abc123 command line parameter, or directly add oncokb.token=abc123in the application.properties. No OncoKB annotation columns will be added if no valid token is provided.

#### Canonical transcript
The canonical transcript is in the `transcriptConsequenceSummary` field, under the `annotation_summary` section of the response. To ensure the canonical transcript is returned, the `annotation_summary` field must be requested in the query sent to Genome Nexus.

Example response structure:
```
"annotation_summary" {
    "transcriptConsequenceSummary" {
        ...
    }
}
```

## Other Languages

The API follows the Swagger/Open API specification, so clients can be generated in most languages: https://openapi-generator.tech/docs/generators

## Applications build on top of the API
See the [tools section](Tools.md)

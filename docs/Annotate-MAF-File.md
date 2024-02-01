# Annotate a MAF File 📄

MAF files can be annotated using either the web interface or the command line client. The [Mutation Mapper](https://www.cbioportal.org/mutation_mapper) is a web-based tool and provides an easy-to-use graphical user interface for annotating MAF files. On the other hand, the command line client offers a more flexible approach for users who prefer command-line interfaces. Both options allow you to add additional information to the MAF file, which can be useful for downstream analysis and interpretation.


## Annotate a MAF file using the command line client

To annotate a specific MAF file using the command line client, we will be using the [genome-nexus-annotation-pipeline](https://github.com/genome-nexus/genome-nexus-annotation-pipeline). This tool provides the options to customize the annotations according to your specific needs.


To use the genome-nexus-annotation-pipeline for annotations, the mutation data files must have at least five columns:
- Chromosome
- Start_Position
- End_Position
- Reference_Allele
- Tumor_Seq_Allele1 or Tumor_Seq_Alele2

These essential columns provide the necessary information for accurate annotation of the variants.

### Minimal Example

Create a MAF file:
```
(echo -e "Chromosome\tStart_Position\tEnd_Position\tReference_Allele\tTumor_Seq_Allele2"; echo -e "7\t55220240\t55220240\tG\tT") > input.txt;
```

Run the annotator using docker:
```
docker run -v ${PWD}:/wd genomenexus/gn-annotation-pipeline:master --filename /wd/input.txt  --output-filename /wd/output.txt --isoform-override uniprot
```

Output can be found in `output.txt`


### Reference Genome
The Genome Nexus Annotation Pipeline supports two versions of the human genome reference assembly: **GRCh37** and **GRCh38**.
By default, the pipeline uses **GRCh37**. 
#### Using GRCh38

If you want to annotate with **GRCh38**, please set the `GENOMENEXUS_BASE` environment variable to `https://grch38.genomenexus.org`. Here's an example of how to do this:

```
docker run -e GENOMENEXUS_BASE=https://grch38.genomenexus.org -v ${PWD}:/wd genomenexus/gn-annotation-pipeline:latest --filename /wd/input.txt  --output-filename /wd/output.txt --isoform-override uniprot
```

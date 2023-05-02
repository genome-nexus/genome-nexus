# Annotate a MAF File 📄

MAF files can be annotated using either the web interface or the command line client. The [Mutation Mapper](https://www.cbioportal.org/mutation_mapper) is a web-based tool and provides an easy-to-use graphical user interface for annotating MAF files. On the other hand, the command line client offers a more flexible approach for users who prefer command-line interfaces. Both options allow you to add additional information to the MAF file, which can be useful for downstream analysis and interpretation.


## Annotate a MAF file using the command line client

To annotate a specific MAF file using the command line client, we recommend using the [genome-nexus-annotation-pipeline](https://github.com/genome-nexus/genome-nexus-annotation-pipeline). This powerful tool adds annotations to the MAF files quickly and easily, with options for customizing the annotations to suit your specific needs.

Annotations using the genome-nexus-annotation-pipeline requires the mutation data files with at least five columns:
- Chromosome
- Start_Position
- End_Position
- Reference_Allele
- Tumor_Seq_Allele2

These essential columns provide the necessary information for accurate annotation of MAF files.

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

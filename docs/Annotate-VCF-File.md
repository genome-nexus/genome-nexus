# Annotate a VCF File 📄

Before annotating a VCF file using Genome Nexus, it must be first converted to a MAF file. This is because the pipeline is specifically designed to annotate MAF files, which provide a standardized format for storing mutation data.

## VCF to MAF conversion

To convert a Variant Call Format (VCF) file to Mutation Annotation Format (MAF), we recommend using a simpler and more user-friendly [vcf2maf](https://github.com/genome-nexus/annotation-tools/blob/master/vcf2maf.py) python tool from the [genome-nexus/annotation-tools](https://github.com/genome-nexus/annotation-tools) GitHub repository.

This tool is designed to support the conversion of VCF files from multiple callers to a standardized MAF format that can be easily used for downstream analysis.

### Requirements

```
python3
click
```
Python `click` module can be installed using `pip install click`.
### Usage

```
python3 vcf2maf.py --help

  -i | --input-data             A list of .vcf files or input data directories, separated by commas [required]
  -o | --output-directory       output data directory [optional]
  -c | --center                 name of the center (standard MAF field = 'Center') [optional]
  -s | --sequence-source        Sequencing source (standard MAF field = 'Sequencing_Source'), e.g., WXS or WGS [optional]
  -t | --tumor-id               The ID of the tumor sample utilized in the genotype columns of the VCF file. [optional]
  -n | --normal-id              The ID of the normal sample utilized in the genotype columns of the VCF file. [optional]
```

### Minimal Example
```
python3 vcf2maf.py --input-data /data/vcfs --output-directory /data/maf/ --center-name CTR --sequence-source WGS --tumor-id Tumor --normal-id Normal
```
This command converts the VCF files in /vcfs folder to MAF format.
- The `--input-data` option is used to specify either a single VCF file or a directory containing multiple VCF files (separated by commas). This option supports passing multiple input files or directories at once.
- The `--output-directory` option allows you to specify the directory where the MAF files will be saved. If no output path is provided, the default output directory `vcf2maf_output` will be used in the current working directory.
- The `--tumor-id` option allows you to specify the ID of the tumor sample used in the genotype columns of the VCF file. If the option is not used, the script will automatically identify the tumor ID from either the `tumor_sample` keyword in the meta data lines or the sample columns from VCF header.
- The `--normal-id` option allows you to specify the ID of the normal sample used in the genotype columns of the VCF file. If the option is not used, the script will automatically identify the normal ID from either the `normal_sample` keyword in the meta data lines or the sample columns from VCF header.

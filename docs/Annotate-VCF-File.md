# Annotate a VCF File 📄

Before annotating a VCF file using Genome Nexus, it must be first converted to a MAF file. This is because the pipeline is specifically designed to annotate MAF files, which provide a standardized format for storing mutation data.

## VCF to MAF conversion

To convert a Variant Call Format (VCF) file to Mutation Annotation Format (MAF), we recommend using the [vcf2maf-lite Python tool](https://github.com/genome-nexus/vcf2maf-lite). It is a lightweight Python adaptation of the [vcf2maf Perl tool](https://github.com/mskcc/vcf2maf), that converts the VCF to MAF format without adding variant annotations.

### Minimal Example
```
python3 vcf2maf.py --input-data /data/vcf --output-directory /data/maf/ --center CTR --sequence-source WGS --tumor-id Tumor --normal-id Normal --retain-info Custom_filters,AC,AF,AC_nfe_seu,AC_afr,AF_afr --retain-fmt alt_count_raw,ref_count_raw,depth_raw
```

This command converts the VCF files in /vcf folder to MAF format. 
- The `--input-data` option is used to specify either a single VCF file or a directory containing multiple VCF files (separated by commas). This option supports passing multiple input files or directories at once.
- The `--output-directory` option allows you to specify the directory where the MAF files will be saved. If no output path is provided, the default output directory `vcf2maf_output` will be used in the current working directory. 
- The `--tumor-id` option allows you to specify the ID of the tumor sample used in the genotype columns of the VCF file. If the option is not used, the script will automatically identify the tumor ID from either the `tumor_sample` keyword in the meta data lines or the sample columns from VCF header.
- The `--normal-id` option allows you to specify the ID of the normal sample used in the genotype columns of the VCF file. If the option is not used, the script will automatically identify the normal ID from either the `normal_sample` keyword in the meta data lines or the sample columns from VCF header.
- The `--retain-info` option allows you to specify the INFO fields to be retained as additional columns in the MAF. If the option is not used, standard MAF columns are included by default.
- The `--retain-fmt` option allows you to specify the FORMAT fields to be retained as additional columns in the MAF. If the option is not used, standard MAF columns are included by default.

### Convert with Docker

vcf2maf-lite is available in DockerHub at https://hub.docker.com/r/genomenexus/vcf2maf-lite

```
docker pull genomenexus/vcf2maf-lite:main
docker run -v ${PWD}:/wd genomenexus/vcf2maf-lite:main python3 vcf2maf_lite.py --input-data /wd/test.vcf --output-directory /wd/maf/ --center CTR --sequence-source WGS --tumor-id Tumor --normal-id Normal --retain-info Custom_filters,AC,AF,AC_nfe_seu,AC_afr,AF_afr --retain-fmt alt_count_raw,ref_count_raw,depth_raw
```
Output can be found in the /maf directory.



## Annotate a MAF file:

Once the VCF file has been converted to Mutation Annotation Format (MAF), the MAF file can be annotated using Genome Nexus. Refer to the [Annotate a MAF File](https://docs.genomenexus.org/annotate-maf-file) section for detailed instructions.

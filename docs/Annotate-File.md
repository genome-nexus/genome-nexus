# Annotate MAF File 📄

You can use either the [web interface](https://www.cbioportal.org/mutation_mapper) or the command line client to annotate a MAF

## Annotate a MAF file using the command line client
We will be using the [genome-nexus-annotation-pipeline](https://github.com/genome-nexus/genome-nexus-annotation-pipeline/pulls) for this.

### Minimal Example
Create a MAF file:
```
(echo -e "Chromosome\tStart_Position\tEnd_Position\tReference_Allele\tTumor_Seq_Allele1"; echo -e "7\t55220240\t55220240\tG\tT") > input.txt;
```

Run the annotator using docker:
```
docker run -v ${PWD}:/wd genomenexus/gn-annotation-pipeline:master --filename /wd/input.txt  --output-filename /wd/output.txt --isoform-override uniprot
```

Output can be found in `output.txt`



# Using the Genome Nexus Docker image

## Relevant Paths

This document outlines details of the container-based implementation and usage of the Genome Nexus annotation tools located at:

	• https://github.com/genome-nexus/annotation-tools
	• https://github.com/genome-nexus/genome-nexus-annotation-pipeline
    
The docker container is located here:

	• https://hub.docker.com/repository/docker/price0416/nexus_bash

The Dockerfile for building this container is located in the Genome-Nexus repository at 

    • /containers/Dockerfile

In addition to the above genome nexus repositories, the container builds the following:

	• Java 8
	• Maven
	• Python 3


## Parameters

The container can be run at the command line by providing the following parameters:

	• /aws/
	    Genome nexus requires two files for authentication, AwsSsl.truststore and AwsSsl.truststore.password.  
        For security reasons, these files are not included in the container and instead,
        a directory containing these files must be provided at runtime.           
        Copies of these files may be obtained by contacting Angelica Ochoa or another member of the genome nexus team.  
        Container bound directory must be called /aws/.
    • /in/
        An input directory containing all vcf and maf files to provide as input for genome nexus annotation.
        Container bound directory must be called /in/.
    • /out/
        An output directory for genome nexus annotation results. 
        Container bound directory must be called /out/.
    • output_prefix
        An output prefix string that will be used for naming various files.  
    • processing_center
        A string representing the processing center these samples were run at.  
        Used to create a column in the final genome nexus .maf file.
    • data_type
        A data type indicating whole genome or whole exome sequencing.  Valid options are WGS or WES.  Case sensitive.

## Notes on Directory Mapping

When providing directory parameters listed above, in both docker and singularity the format is [/local/directory/:/container_directory/], which will map the local directory to the target directory in the container.  The container expects the directory names to be explicit [/aws, /in, /out].  Failure to provide the proper container directory name will result in an error.  

Singularity runs in write only mode, so to address the inability to copy some files to specific locations when running singularity, the genome nexus annotation tools file, /annotation-tools/annotation_suite_wrapper.sh, is overwritten with a custom version that updates path configurations to readable directories.  This means that changes to this file in the GitHub repository will potentially need to be updated in this container when necessary.


## Example execution using docker:

```
docker run 
   --mount type=bind,source=/myAwsDir/,target=/aws 
   --mount type=bind,source=/my/input/dir,target=/in 
   --mount type=bind,source=/my/output/,target=/out 
   price0416/nexus_bash 
   testRunPrefix
   CMO
   WES
```

## Example execution using singularity.

```
singularity run 
    --bind /my/Aws/Dir/:/aws 
    --bind /my/input/dir/:/in 
    --bind /my/out/dir:/out 
    docker://price0416/nexus_bash:latest 
    testRunPrefix 
    CMO 
    WGS
```


# Update Genome Nexus Data files
All the input files for genome nexus are included with the repo. This folder
is to update the data files when necessary.

## Manually download these files

### Ensembl Biomart

#### PFAM endpoint

Ensembl Biomart file is required by the PFAM endpoint. In order to download this file
follow these steps:

1. Go to the [Biomart](www.ensembl.org/biomart/martview) page on the Ensemble website.
2. Select `Ensemble Genes` from the `Database` dropdown menu.
3. Select `Human Genes` from the `Dataset` dropdown menu.
4. Click on `Attributes`, and select these ones:
Gene stable ID, Transcript stable Id, Protein stable Id, Gene name, Pfam domain ID, Pfam domain start, Pfam domain end.
5. Click on `Results`, and export all results to a `TSV` file.
6. Copy over the downoaded file to replace [../annotation/src/main/resources/ensembl_biomart_pfam.txt](../annotation/src/main/resources/ensembl_biomart_pfam.txt).

#### Ensembl endpoint 
1. Go to Biomart ([grch37.ensembl.org/biomart/martview](grch37.ensembl.org/biomart/martview)) page on the Ensemble website.
2. Select `Ensemble Genes` from the `Database` dropdown menu.
3. Select `Human Genes` from the `Dataset` dropdown menu.
4. Click on `Attributes`, and select these ones:
Gene stable ID, Transcript stable Id, HGNC Symbol, HGNC ID
5. Click on `Results`, and export all results to a `TSV` file.
6. Copy over the downoaded file to replace [ensembl_biomart_geneids_grch37.p13.txt](ensembl_biomart_geneids_grch37.p13.is_canonical.txt).

##### Exon information in Ensembl endpoint
1. Go to Ensembl FTP site ([ftp://ftp.ensembl.org/pub/grch37/](ftp://ftp.ensembl.org/pub/grch37/))
2. Click desired Ensembl Release
3. Click `gff3`
4. Click `homo_sapiens`
5. Click Homo_sapiens.GRCh37.[release#].gff3.gz and download will start
6. Unzip file with the following command: `gunzip Homo_sapiens.GRCh37.[release#].gff3.gz`
7. Copy the unzipped file to replace `Homo_sapiens.GRCh37.gff3`

## Download and transform data
```
make all # takes about 1h40m from scratch
```

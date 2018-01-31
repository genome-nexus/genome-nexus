import pandas as pd
import numpy as np
import sys
import gzip

def transform_gff_to_tsv(gff_file):

    # Dataframe to append the exon information
    exon_info = pd.DataFrame(columns=['transcriptId', 'exonId', 'exonStart', 'exonEnd', 'rank', 'strand', 'version'])
    # list to append exon information per row
    rows = []

    # Open gff file and read lines, when line contains exon information, extract this information
    # In case of python 3, use 'rt' instead of 'r'
    with gzip.open(gff_file, 'r') as gff:
        for line in gff:
            list_line = line.split("\t")
            if len(list_line) > 1 and "exon" in list_line[2]:
                data_dict = {}
                line_exon_info = list_line[8].split(";")

                try:
                    data_dict['transcriptId'] = line_exon_info[0].split(":")[1]
                except IndexError:
                    data_dict['transcriptId'] = np.NaN

                try:
                    data_dict['exonId'] = line_exon_info[5].split("=")[1]
                except IndexError:
                    data_dict['exonId'] = np.NaN

                try:
                    data_dict['exonStart'] = list_line[3]
                except IndexError:
                    data_dict['exonStart'] = np.NaN

                try:
                    data_dict['exonEnd'] = list_line[4]
                except IndexError:
                    data_dict['exonEnd'] = np.NaN

                try:
                    data_dict['rank'] = line_exon_info[6].split("=")[1]
                except IndexError:
                    data_dict['rank'] = np.NaN

                try:
                    strand = list_line[6]
                    # Convert plus strand into 1 and minus strand into -1
                    if strand == "+":
                        data_dict['strand'] = 1
                    elif strand == "-":
                        data_dict['strand'] = -1
                    else:
                        data_dict['strand'] = np.NaN
                except IndexError:
                    data_dict['strand'] = np.NaN

                try:
                    data_dict['version'] = line_exon_info[7].split("=")[1].replace("\n","")
                except IndexError:
                    data_dict['version'] = np.NaN

                rows.append(data_dict)

    exon_info = exon_info.append(rows, ignore_index=True)
    return(exon_info)

def main():
    ## Homo_sapiens.GRCH37.gff3 is local copy from: ftp://ftp.ensembl.org/pub/grch37/release-91/gff3/homo_sapiens/
    tsv_exon_info = transform_gff_to_tsv("../data/Homo_sapiens.GRCh37.gff3.gz")
    tsv_exon_info.to_csv(sys.stdout, sep='\t', index=False)
main()

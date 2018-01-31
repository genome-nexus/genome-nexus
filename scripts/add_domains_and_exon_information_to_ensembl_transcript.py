import pandas as pd
import sys
from collections import defaultdict

if __name__ == "__main__":
    # Read in transcript information file
    transcripts = pd.read_csv("../data/ensembl_biomart_transcripts.txt", sep='\t')

    ## Domains
    domains_file = "../data/ensembl_biomart_pfam_grch37.p13.txt"
    # Make a dictionary where every value added will be an empty list
    # With this dictionary no keys have to be added for transcripts without domains
    # This will automatically be empty list when transcript id (key in dict) is requested
    domain_dict = defaultdict(list)

    # Loop through each line of the file with pfam domain info and append information
    # to dictionary, which has transcript id as key
    with open(domains_file, 'r') as domains:
        for line in domains:
            # Split line and remove enter from last value
            list_line = line.split("\t")
            list_line[6] = list_line[6].strip("\n")
            # Append information to dict, ensure correct mapping in json later
            # not necessary to check if transcript id is in transcript file,
            # will be done when dictionary is added to the correct rows of the transcript file
            line_dict = {}
            line_dict['pfam_domain_id'] = list_line[4]
            line_dict['pfam_domain_start'] = list_line[5]
            line_dict['pfam_domain_end'] = list_line[6]
            # Append to dictionary with transcript ids
            # A transcript can have multiple domains,
            # therefore each domain will be appended to a list
            domain_dict[list_line[1]].append(line_dict)

    ## Exons
    exon_file = "../data/ensembl_exon_info.txt"
    # Also create dictionary where transcript ids will become the key
    # and value is a empty list, unless it is filled by information from file
    exon_dict = defaultdict(list)

    # Loop through each line of the file with exon information, append information about
    # exons to dictionary which have transcript id as key
    with open(exon_file, 'r') as exons:
        for line in exons:
            # Split line to retrieve information and
            list_line = line.split("\t")
            list_line[6] = list_line[6].strip("\n")
            # Add information to dictionary, not necessary to check if
            # transcript id is in transcript file, will be done when dictionary
            # is added to the correct rows of the transcript file
            line_dict = {}
            line_dict['exonId'] = list_line[1]
            line_dict['exonStart'] = list_line[2]
            line_dict['exonEnd'] = list_line[3]
            line_dict['rank'] = list_line[4]
            line_dict['strand'] = list_line[5]
            line_dict['version'] = list_line[6]
            # Add information to list in dictionary, because transcript can have
            # multiple exons
            exon_dict[list_line[0]].append(line_dict)

    # Add dictionary values to correct row in transcript file
    rows_domain = []
    rows_exons = []
    # Get transcript ids in order to append to correct row
    transcript_ids = transcripts['transcript_stable_id'].tolist()
    for row in transcript_ids:
        # In case no domain or exon information is found, append None,
        # then the domain or exon line will not be shown in the webservice
        if len(domain_dict[row]) == 0:
            rows_domain.append(None)
        else:
            rows_domain.append(domain_dict[row])

        if len(exon_dict[row]) == 0:
            rows_exons.append(None)
        else:
            rows_exons.append(exon_dict[row])

    # Add to dataframe and write to stdout
    transcripts["domains"] = rows_domain
    transcripts["exons"] = rows_exons
    transcripts.to_json(sys.stdout, orient='records', lines=True)



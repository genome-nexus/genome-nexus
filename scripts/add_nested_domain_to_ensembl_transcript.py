import pandas as pd
import sys


if __name__ == "__main__":
    transcripts = pd.read_csv("../data/ensembl_biomart_transcripts.txt", sep='\t')
    trans_domain =  pd.read_csv("../data/ensembl_biomart_pfam_grch37.p13.txt", sep='\t')
    trans_domain.columns = [c.lower().replace(' ','_') for c in trans_domain.columns]
    transcripts["domains"] = transcripts.transcript_stable_id.apply(lambda x: trans_domain[trans_domain.transcript_stable_id == x]['pfam_domain_id pfam_domain_start pfam_domain_end'.split()])
    transcripts.to_json(sys.stdout, orient='records',lines=True)

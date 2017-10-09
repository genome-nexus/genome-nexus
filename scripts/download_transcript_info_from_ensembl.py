import pandas as pd
import numpy as np
import argparse
import requests, sys


def request_transcript_ids(transcripts):
    server = "http://grch37.rest.ensembl.org"
    ext = "/lookup/id"
    headers={ "Content-Type" : "application/json", "Accept" : "application/json"}
    data = '{"expand": 1, "format":"full", "ids": ['
    for i, transcript in enumerate(transcripts):
        if i != len(transcripts) - 1:
            data += '"{}",'.format(transcript)
        else:
            data += '"{}"'.format(transcript)
    data += '] }'
    r = requests.post(server+ext, headers=headers, data=data)

    if not r.ok:
        r.raise_for_status()
        sys.exit()

    decoded = r.json()

    return decoded

def get_transcript_info(x, ensembl_transcript_response):
    df = ensembl_transcript_response
    is_canonical = df[x]['is_canonical']
    try:
        protein_stable_id = df[x]['Translation']['id']
    except KeyError:
        protein_stable_id = np.nan
    try:
        # store as string to prevent integer -> float
        protein_length = str(df[x]['Translation']['length'])
    except KeyError:
        protein_length = np.nan
    return pd.Series(
        [is_canonical, protein_stable_id, protein_length],
        index='is_canonical protein_stable_id protein_length'.split()
    )


if __name__ == "__main__":
    tsv = pd.read_csv("../data/ensembl_biomart_geneids_grch37.p13.txt", sep='\t')
    tsv.columns = [c.lower().replace(' ','_') for c in tsv.columns]
    tsv['is_canonical'] = [np.nan] * len(tsv)
    tsv['protein_stable_id'] = [np.nan] * len(tsv)
    tsv['protein_length'] = [np.nan] * len(tsv)
    column_indices = [list(tsv.columns).index(x) for x in 'is_canonical protein_stable_id protein_length'.split()]
    for low_index in range(0, len(tsv), 1000):
        high_index = min(low_index + 1000, len(tsv))
        transcripts = tsv.iloc[low_index:high_index, list(tsv.columns).index('transcript_stable_id')]
        decoded = request_transcript_ids(transcripts)
        tsv.iloc[low_index:high_index, column_indices] = transcripts.apply(lambda x: get_transcript_info(x, decoded))
    tsv.to_csv(sys.stdout, sep='\t', index=False)

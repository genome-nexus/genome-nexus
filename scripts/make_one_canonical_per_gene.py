import pandas as pd
import numpy as np
import sys


def get_overrides_transcript(overrides_tables, ensembl_table, hgnc_symbol):
    """Find canonical transcript id for given hugo symbol. Overrides_tables is
    a list of different override tables"""
    for overrides in overrides_tables:
        try:
            # corner case when there are multiple overrides for a given gene symbol
            if overrides.ix[hgnc_symbol].ndim > 1:
                transcript = overrides.ix[hgnc_symbol].isoform_override.values[0]
            else:
                transcript = overrides.ix[hgnc_symbol].isoform_override
            return transcript
        except KeyError:
            pass
    else:
        # get ensembl canonical version otherwise
        return get_ensembl_canonical_transcript_id(ensembl_table, hgnc_symbol)


def get_ensembl_canonical_transcript_id(ensembl_table, hgnc_symbol):
    """Get canonical transcript id with largest protein length or if there is
    no such thing, pick biggest gene id"""
    gene_rows = ensembl_table[ensembl_table.hgnc_symbol == hgnc_symbol]
    if len(gene_rows) == 0:
        return np.nan
    elif len(gene_rows) == 1:
        return gene_rows.gene_stable_id.values[0]
    else:
        return gene_rows.sort_values('is_canonical protein_length gene_stable_id'.split(), ascending=False).transcript_stable_id.values[0]


def main():
    # input files
    tsv = pd.read_csv("../data/ensembl_biomart_geneids_grch37.p13.transcript_info.txt", sep='\t', dtype={'is_canonical':bool})
    tsv = tsv.drop_duplicates()
    mskcc = pd.read_csv("../data/isoform_overrides_at_mskcc.txt", sep='\t')\
        .rename(columns={'enst_id':'isoform_override'})\
        .set_index('gene_name'.split())
    uniprot = pd.read_csv("../data/isoform_overrides_uniprot.txt", sep='\t')\
        .rename(columns={'enst_id':'isoform_override'})\
        .set_index('gene_name'.split())
    custom = pd.read_csv("../data/isoform_overrides_genome_nexus.txt", sep='\t')\
        .rename(columns={'enst_id':'isoform_override'})\
        .set_index('gene_name'.split())
    hugos = pd.read_csv('../data/hgnc_symbols_20170921.txt', sep='\t')['Approved symbol'].unique()

    # only test the cancer genes for oddities (these are very important)
    cgs = set(pd.read_csv('../data/oncokb_cancer_genes_list_20170926.txt',sep='\t')['Hugo Symbol'])
    # each cancer gene stable id should have only one associated cancer gene symbol
    assert(tsv[tsv.hgnc_symbol.isin(cgs)].groupby('gene_stable_id').hgnc_symbol.nunique().sort_values().nunique() == 1)
    # each transcript stable id always belongs to only one gene stable id
    assert(tsv.groupby('transcript_stable_id').gene_stable_id.nunique().sort_values().nunique() == 1)

    # create hgnc_symbol to gene id mapping
    # TODO: run this for all genes instead of just cancer genes
    all_hugo_symbols = [g for g in list(set(tsv.hgnc_symbol.unique()).union(set(cgs)).union(set(hugos))) if not pd.isnull(g)]
    one_transcript_per_hugo_symbol = pd.Series(all_hugo_symbols).apply(lambda x:
        pd.Series(
            [
                get_ensembl_canonical_transcript_id(tsv, x),
                get_overrides_transcript([custom], tsv, x),
                get_overrides_transcript([uniprot, custom], tsv, x),
                get_overrides_transcript([mskcc, uniprot, custom], tsv, x),
            ],
            index="""
            ensembl_canonical_transcript
            genome_nexus_canonical_transcript
            uniprot_canonical_transcript
            mskcc_canonical_trancript
            """.split()
        )
    )
    one_transcript_per_hugo_symbol.index = all_hugo_symbols
    one_transcript_per_hugo_symbol.index.name = 'hgnc_symbol'
    # TODO: add argparse to output this to file instead of stderr
    one_transcript_per_hugo_symbol.to_csv(sys.stdout, sep='\t')

if __name__ == "__main__":
    main()

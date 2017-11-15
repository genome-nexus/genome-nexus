# canonical transcripts per gene id and per hugo symbol

ensembl_biomart_geneids_grch37.p13.transcript_info.txt: ensembl_biomart_geneids_grch37.p13.txt
	cd ../scripts && python download_transcript_info_from_ensembl.py > ../data/$@

ensembl_biomart_transcripts.txt: ensembl_biomart_geneids_grch37.p13.transcript_info.txt
	csvcut -tc transcript_stable_id,gene_stable_id,protein_stable_id,protein_length $< | csvsort -c transcript_stable_id,gene_stable_id,protein_stable_id,protein_length | uniq | csvformat -T > $@

ensembl_biomart_transcripts.json: ensembl_biomart_transcripts.txt ensembl_biomart_pfam_grch37.p13.txt
	cd ../scripts && python add_nested_domain_to_ensembl_transcript.py > ../data/$@

# one script outputting two files (one through stderr, TODO: split up in two
# scripts)
ensembl_biomart_canonical_transcripts_per_hgnc.txt: ensembl_biomart_geneids_grch37.p13.transcript_info.txt isoform_overrides_uniprot.txt isoform_overrides_at_mskcc.txt isoform_overrides_genome_nexus hgnc_symbols_20170921.txt
	cd ../scripts && python make_one_canonical_per_gene.py > ../data/$@

# vcf2maf canonical transcripts
VCF2MAF_RAW_URL=https://raw.githubusercontent.com/mskcc/vcf2maf/v1.6.14
isoform_overrides_uniprot.txt:
	curl '$(VCF2MAF_RAW_URL)/data/isoform_overrides_uniprot' | sed 's/^#//' > $@

isoform_overrides_at_mskcc.txt:
	curl '$(VCF2MAF_RAW_URL)/data/isoform_overrides_at_mskcc' | sed 's/^#//' | sed 's/dmp_refseq_id/refseq_id/' | sed 's/isoform_override/enst_id/g' > $@

export: isoform_overrides_at_mskcc.txt isoform_overrides_uniprot.txt ensembl_biomart_canonical_transcripts_per_hgnc.txt ensembl_biomart_transcripts.json
	cp $^ ../web/src/main/resources/

all: ensembl_biomart_geneids_grch37.p13.transcript_info.txt ensembl_biomart_geneids_grch37.p13.transcript_info.txt ensembl_biomart_canonical_transcripts_per_hgnc.txt isoform_overrides_uniprot.txt isoform_overrides_at_mskcc.txt ensembl_biomart_transcripts.json

.PHONY: all export

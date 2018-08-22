package org.cbioportal.genome_nexus.model.my_variant_info_model;

import org.springframework.data.mongodb.core.mapping.Field;

public class Mutdb {

	private String rsid;
	private String ref;
	private String alt;
	private String uniprotId;
	private Double mutpredScore;
	private String cosmicId;
	private String chrom;
	private Hg19 hg19;


	@Field(value = "rsid")
	public String getRsid()
	{
		return rsid;
	}

	public void setRsid(String rsid)
	{
		this.rsid = rsid;
	}

	@Field(value = "ref")
	public String getRef()
	{
		return ref;
	}

	public void setRef(String ref)
	{
		this.ref = ref;
	}

	@Field(value = "alt")
	public String getAlt()
	{
		return alt;
	}

	public void setAlt(String alt)
	{
		this.alt = alt;
	}

	@Field(value = "uniprot_id")
	public String getUniprotId()
	{
		return uniprotId;
	}

	public void setUniprotId(String uniprotId)
	{
		this.uniprotId = uniprotId;
	}

	@Field(value = "mutpred_score")
	public Double getMutpredScore()
	{
		return mutpredScore;
	}

	public void setMutpredScore(Double mutpredScore)
	{
		this.mutpredScore = mutpredScore;
	}

	@Field(value = "cosmic_id")
	public String getCosmicId()
	{
		return cosmicId;
	}

	public void setCosmicId(String cosmicId)
	{
		this.cosmicId = cosmicId;
	}

	@Field(value = "chrom")
	public String getChrom()
	{
		return chrom;
	}

	public void setChrom(String chrom)
	{
		this.chrom = chrom;
	}

	@Field(value = "hg19")
	public Hg19 getHg19()
	{
		return hg19;
	}

	public void setHg19(Hg19 hg19)
	{
		this.hg19 = hg19;
	}

}
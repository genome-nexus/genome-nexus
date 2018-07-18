package org.cbioportal.genome_nexus.model;

import org.springframework.data.mongodb.core.mapping.Field;

public class ClinVar {

	private String _license;
	private Integer alleleId;
	private String alt;
	private String chrom;
	private String cytogenic;
	private Gene gene;
	private Hg19 hg19;
	private Hg38 hg38;
	private Hgvs hgvs;

   	@Field(value = "license")
	public String get_license()
	{
		return _license;
	}

	public void set_license(String _license)
	{
		this._license = _license;
	}
   	@Field(value = "allele_id")
	public Integer getAlleleId()
	{
		return alleleId;
	}

	public void setAlleleId(Integer alleleId)
	{
		this.alleleId = alleleId;
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
   	@Field(value = "chrom")
	public String getChrom()
	{
		return chrom;
	}

	public void setChrom(String chrom)
	{
		this.chrom = chrom;
	}
   	@Field(value = "cytogenic")
	public String getCytogenic()
	{
		return cytogenic;
	}

	public void setCytogenic(String cytogenic)
	{
		this.cytogenic = cytogenic;
	}
   	@Field(value = "gene")
	public Gene getGene()
	{
		return gene;
	}

	public void setGene(Gene gene)
	{
		this.gene = gene;
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
   	@Field(value = "hg38")
	public Hg38 getHg38()
	{
		return hg38;
	}

	public void setHg38(Hg38 hg38)
	{
		this.hg38 = hg38;
	}
   	@Field(value = "hgvs")
	public Hgvs getHgvs()
	{
		return hgvs;
	}

	public void setHgvs(Hgvs hgvs)
	{
		this.hgvs = hgvs;
	}
}
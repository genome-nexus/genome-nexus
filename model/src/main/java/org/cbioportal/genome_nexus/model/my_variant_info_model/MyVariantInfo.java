package org.cbioportal.genome_nexus.model.my_variant_info_model;

import org.springframework.data.mongodb.core.mapping.Field;

public class MyVariantInfo
{

	@Field(value = "hgvs")
	public String hgvs;

	public String getHgvs()
	{
		return hgvs;
	}

	public void setHgvs(String hgvs)
	{
		this.hgvs = hgvs;
	}
	@Field(value = "version")
	public Integer version;

	public Integer getVersion()
	{
		return version;
	}

	public void setVersion(Integer version)
	{
		this.version = version;
	}
	@Field(value = "snpeff")
	public Snpeff snpeff;

	public Snpeff getSnpeff()
	{
		return snpeff;
	}

	public void setSnpeff(Snpeff snpeff)
	{
		this.snpeff = snpeff;
	}
	@Field(value = "vcf")
	public Vcf vcf;

	public Vcf getVcf()
	{
		return vcf;
	}

	public void setVcf(Vcf vcf)
	{
		this.vcf = vcf;
	}
	@Field(value = "dbsnp")
	public Dbsnp dbsnp;

	public Dbsnp getDbsnp()
	{
		return dbsnp;
	}

	public void setDbsnp(Dbsnp dbsnp)
	{
		this.dbsnp = dbsnp;
	}
	@Field(value = "cosmic")
	public Cosmic cosmic;

	public Cosmic getCosmic()
	{
		return cosmic;
	}

	public void setCosmic(Cosmic cosmic)
	{
		this.cosmic = cosmic;
	}
	@Field(value = "clin_var")
	public ClinVar clinVar;

	public ClinVar getClinVar()
	{
		return clinVar;
	}

	public void setClinVar(ClinVar clinVar)
	{
		this.clinVar = clinVar;
	}
	@Field(value = "mutdb")
	public Mutdb mutdb;

	public Mutdb getMutdb()
	{
		return mutdb;
	}

	public void setMutdb(Mutdb mutdb)
	{
		this.mutdb = mutdb;
	}

}
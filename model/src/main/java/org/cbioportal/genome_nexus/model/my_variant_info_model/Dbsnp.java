package org.cbioportal.genome_nexus.model.my_variant_info_model;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Field;

public class Dbsnp
{

	@Field(value = "license")
	public String license;

	public String getLicense()
	{
		return license;
	}

	public void setLicense(String license)
	{
		this.license = license;
	}
	@Field(value = "allele_origin")
	public String alleleOrigin;

	public String getAlleleOrigin()
	{
		return alleleOrigin;
	}

	public void setAlleleOrigin(String alleleOrigin)
	{
		this.alleleOrigin = alleleOrigin;
	}
	@Field(value = "alleles")
	public List<Alleles> alleles;

	public List<Alleles> getAlleles()
	{
		return alleles;
	}

	public void setAlleles(List<Alleles> alleles)
	{
		this.alleles = alleles;
	}
	@Field(value = "alt")
	public String alt;

	public String getAlt()
	{
		return alt;
	}

	public void setAlt(String alt)
	{
		this.alt = alt;
	}
	@Field(value = "chrom")
	public String chrom;

	public String getChrom()
	{
		return chrom;
	}

	public void setChrom(String chrom)
	{
		this.chrom = chrom;
	}
	@Field(value = "_class")
	public String _class;

	public String get_class()
	{
		return _class;
	}

	public void set_class(String _class)
	{
		this._class = _class;
	}
	@Field(value = "dbsnp_build")
	public Integer dbsnpBuild;

	public Integer getDbsnpBuild()
	{
		return dbsnpBuild;
	}

	public void setDbsnpBuild(Integer dbsnpBuild)
	{
		this.dbsnpBuild = dbsnpBuild;
	}
	@Field(value = "flags")
	public List<String> flags;

	public List<String> getFlags()
	{
		return flags;
	}

	public void setFlags(List<String> flags)
	{
		this.flags = flags;
	}
	@Field(value = "gene")
	public Gene gene;

	public Gene getGene()
	{
		return gene;
	}

	public void setGene(Gene gene)
	{
		this.gene = gene;
	}
	@Field(value = "hg19")
	public Hg19 hg19;

	public Hg19 getHg19()
	{
		return hg19;
	}

	public void setHg19(Hg19 hg19)
	{
		this.hg19 = hg19;
	}
	@Field(value = "ref")
	public String ref;

	public String getRef()
	{
		return ref;
	}

	public void setRef(String ref)
	{
		this.ref = ref;
	}
	@Field(value = "rsid")
	public String rsid;

	public String getRsid()
	{
		return rsid;
	}

	public void setRsid(String rsid)
	{
		this.rsid = rsid;
	}
	@Field(value = "validated")
	public Boolean validated;

	public Boolean getValidated()
	{
		return validated;
	}

	public void setValidated(Boolean validated)
	{
		this.validated = validated;
	}
	@Field(value = "var_subtype")
	public String varSubtype;

	public String getVarSubtype()
	{
		return varSubtype;
	}

	public void setVarSubtype(String varSubtype)
	{
		this.varSubtype = varSubtype;
	}
	@Field(value = "vartype")
	public String vartype;

	public String getVartype()
	{
		return vartype;
	}

	public void setVartype(String vartype)
	{
		this.vartype = vartype;
	}

}
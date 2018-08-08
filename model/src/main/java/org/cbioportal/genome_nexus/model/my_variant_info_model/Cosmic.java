package org.cbioportal.genome_nexus.model.my_variant_info_model;

import org.springframework.data.mongodb.core.mapping.Field;

public class Cosmic
{

	@Field(value = "_license")
	public String _license;

	public String get_license()
	{
		return _license;
	}

	public void set_license(String _license)
	{
		this._license = _license;
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
	@Field(value = "cosmic_id")
	public String cosmicId;

	public String getCosmicId()
	{
		return cosmicId;
	}

	public void setCosmicId(String cosmicId)
	{
		this.cosmicId = cosmicId;
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
	@Field(value = "mut_freq")
	public Double mutFreq;

	public Double getMutFreq()
	{
		return mutFreq;
	}

	public void setMutFreq(Double mutFreq)
	{
		this.mutFreq = mutFreq;
	}
	@Field(value = "mut_nt")
	public String mutNt;

	public String getMutNt()
	{
		return mutNt;
	}

	public void setMutNt(String mutNt)
	{
		this.mutNt = mutNt;
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
	@Field(value = "tumor_site")
	public String tumorSite;

	public String getTumorSite()
	{
		return tumorSite;
	}

	public void setTumorSite(String tumorSite)
	{
		this.tumorSite = tumorSite;
	}

}
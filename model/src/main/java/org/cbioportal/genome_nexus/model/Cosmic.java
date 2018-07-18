package org.cbioportal.genome_nexus.model;

import org.springframework.data.mongodb.core.mapping.Field;

public class Cosmic {

	private  String _license;
	private  String alt;
	private  String chrom;
	private  String cosmicId;
	private Hg19 hg19;
	private Double mutFreq;
	private  String mutNt;
	private  String ref;
	private  String tumorSite;

   	@Field(value = "_license")
	public String get_license()
	{
		return _license;
	}

	public void set_license(String _license)
	{
		this._license = _license;
	}
   	@Field(value = "alt")
	public String getalt()
	{
		return alt;
	}

	public void setAlt(String alt)
	{
		this.alt = alt;
	}
   	@Field(value = "chrom")
	public String getchrom()
	{
		return chrom;
	}

	public void setChrom(String chrom)
	{
		this.chrom = chrom;
	}
   	@Field(value = "cosmic_id")
	public String getcosmicId()
	{
		return cosmicId;
	}

	public void setCosmicid(String cosmicId)
	{
		this.cosmicId = cosmicId;
	}
   	@Field(value = "hg19")
	public Hg19 gethg19()
	{
		return hg19;
	}

	public void setHg19(Hg19 hg19)
	{
		this.hg19 = hg19;
	}
   	@Field(value = "mut_freq")
	public Double getmutFreq()
	{
		return mutFreq;
	}

	public void setMutfreq(Double mutFreq)
	{
		this.mutFreq = mutFreq;
	}
   	@Field(value = "mut_nt")
	public String getmutNt()
	{
		return mutNt;
	}

	public void setMutnt(String mutNt)
	{
		this.mutNt = mutNt;
	}
   	@Field(value = "ref")
	public String getref()
	{
		return ref;
	}

	public void setRef(String ref)
	{
		this.ref = ref;
	}
   	@Field(value = "tumor_site")
	public String gettumorSite()
	{
		return tumorSite;
	}

	public void setTumorsite(String tumorSite)
	{
		this.tumorSite = tumorSite;
	}

}
package org.cbioportal.genome_nexus.model.my_variant_info_model;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Field;

public class Hgvs
{

	@Field(value = "coding")
	public List<String> coding;

	public List<String> getCoding()
	{
		return coding;
	}

	public void setCoding(List<String> coding)
	{
		this.coding = coding;
	}
	@Field(value = "genomic")
	public List<String> genomic;

	public List<String> getGenomic()
	{
		return genomic;
	}

	public void setGenomic(List<String> genomic)
	{
		this.genomic = genomic;
	}

}
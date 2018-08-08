package org.cbioportal.genome_nexus.model.my_variant_info_model;

import org.springframework.data.mongodb.core.mapping.Field;

public class Hg38
{

	@Field(value = "start")
	public String start;

	public String getStart()
	{
		return start;
	}

	public void setStart(String start)
	{
		this.start = start;
	}
	@Field(value = "end")
	public String end;

	public String getEnd()
	{
		return end;
	}

	public void setEnd(String end)
	{
		this.end = end;
	}

}
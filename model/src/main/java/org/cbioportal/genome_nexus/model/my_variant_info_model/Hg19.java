package org.cbioportal.genome_nexus.model.my_variant_info_model;

import org.springframework.data.mongodb.core.mapping.Field;

public class Hg19
{

	@Field(value = "start")
	public Integer start;

	public Integer getStart()
	{
		return start;
	}

	public void setStart(Integer start)
	{
		this.start = start;
	}
	@Field(value = "end")
	public Integer end;

	public Integer getEnd()
	{
		return end;
	}

	public void setEnd(Integer end)
	{
		this.end = end;
	}

}
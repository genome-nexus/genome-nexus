package org.cbioportal.genome_nexus.model;

import org.springframework.data.mongodb.core.mapping.Field;

public class Hg38 {

	private String start;
	private String end;

   	@Field(value = "start")
	public String getStart()
	{
		return start;
	}

	public void setStart(String start)
	{
		this.start = start;
	}
   	@Field(value = "end")
	public String getEnd()
	{
		return end;
	}

	public void setEnd(String end)
	{
		this.end = end;
	}

}
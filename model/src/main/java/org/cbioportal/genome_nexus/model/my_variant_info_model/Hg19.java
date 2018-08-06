package org.cbioportal.genome_nexus.model;

import org.springframework.data.mongodb.core.mapping.Field;


public class Hg19 {
    private Integer start;
    private Integer end;

    @Field(value = "start")
    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    @Field(value = "end")
    public Integer getEnd() {
        return end;
    }

    public void setEnd(Integer end)
	{
		this.end = end;
	}
}
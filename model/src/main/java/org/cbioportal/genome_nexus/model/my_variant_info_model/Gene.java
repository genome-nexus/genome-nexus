package org.cbioportal.genome_nexus.model;

import org.springframework.data.mongodb.core.mapping.Field;

public class Gene {
    private String geneId;
    private String symbol;

    @Field(value = "geneid")
    public String getGeneid() {
        return geneId;
    }

    public void setGeneid(String geneId) {
        this.geneId = geneId;
    }

    @Field(value = "symbol")
    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

}
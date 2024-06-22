package org.cbioportal.genome_nexus.model;

import org.springframework.data.mongodb.core.mapping.Field;

public class AlphaMissense {

    private Double score;

    private String pathogenicity;

    @Field("am_pathogenicity")
    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    @Field("am_class")
    public String getPathogenicity() {
        return pathogenicity;
    }

    public void setPathogenicity(String pathogenicity) {
        this.pathogenicity = pathogenicity;
    }
}

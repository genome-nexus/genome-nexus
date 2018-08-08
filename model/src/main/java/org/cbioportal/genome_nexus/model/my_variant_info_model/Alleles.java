package org.cbioportal.genome_nexus.model.my_variant_info_model;

import org.springframework.data.mongodb.core.mapping.Field;

public class Alleles {
    private String allele;

    @Field(value = "allele")
    public String getAllele() {
        return allele;
    }

    public void setAllele(String allele) {
        this.allele = allele;
    }
}
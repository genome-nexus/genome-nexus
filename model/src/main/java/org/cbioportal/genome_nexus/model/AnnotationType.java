package org.cbioportal.genome_nexus.model;

import com.fasterxml.jackson.annotation.JsonCreator;


public enum AnnotationType {
    ANNOTATION_SUMMARY("annotation_summary"),
    CLINVAR("clinvar"),
    HOTSPOTS("hotspots"),
    MUTATION_ASSESSOR("mutation_assessor"),
    MY_VARIANT_INFO("my_variant_info"),
    NUCLEOTIDE_CONTEXT("nucleotide_context"),
    ONCOKB("oncokb"),
    PTMS("ptms"),
    SIGNAL("signal");

    private final String value;

    AnnotationType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @JsonCreator
    public static AnnotationType fromString(String value) {
        for (AnnotationType field : AnnotationType.values()) {
            if (field.value.equalsIgnoreCase(value)) {
                return field;
            }
        }
        throw new IllegalArgumentException("Invalid value for AnnotationType: " + value);
    }
}

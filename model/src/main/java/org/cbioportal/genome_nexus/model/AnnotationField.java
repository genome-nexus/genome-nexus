package org.cbioportal.genome_nexus.model;

import com.fasterxml.jackson.annotation.JsonCreator;


public enum AnnotationField {
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

    AnnotationField(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @JsonCreator
    public static AnnotationField fromString(String value) {
        for (AnnotationField field : AnnotationField.values()) {
            if (field.value.equalsIgnoreCase(value)) {
                return field;
            }
        }
        throw new IllegalArgumentException("Invalid value for AnnotationType: " + value);
    }
}

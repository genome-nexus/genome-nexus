package org.cbioportal.genome_nexus.web.converters;

import org.cbioportal.genome_nexus.model.AnnotationField;
import org.springframework.core.convert.converter.Converter;

public class AnnotationFieldEnumConverter implements Converter<String, AnnotationField> {
    @Override
    public AnnotationField convert(String s) {
        return AnnotationField.fromString(s);
    }
}


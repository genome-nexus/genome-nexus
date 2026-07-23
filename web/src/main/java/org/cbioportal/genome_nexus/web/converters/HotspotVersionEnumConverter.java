package org.cbioportal.genome_nexus.web.converters;

import org.cbioportal.genome_nexus.model.HotspotVersion;
import org.springframework.core.convert.converter.Converter;

public class HotspotVersionEnumConverter implements Converter<String, HotspotVersion> {
    @Override
    public HotspotVersion convert(String s) {
        return HotspotVersion.fromString(s);
    }
}

package org.cbioportal.genome_nexus.service.annotation;

import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

@Component
public class StrandSignResolver
{
    @Nullable
    private String resolve(VariantAnnotation variantAnnotation)
    {
        String sign = null;
        Integer strand = variantAnnotation.getStrand();

        if (strand != null)
        {
            if (strand < 0) {
                sign = "-";
            }
            else {
                sign = "+";
            }
        }

        return sign;
    }
}

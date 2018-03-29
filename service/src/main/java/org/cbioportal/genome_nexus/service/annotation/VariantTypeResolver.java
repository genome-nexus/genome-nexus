package org.cbioportal.genome_nexus.service.annotation;

import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

@Component
public class VariantTypeResolver
{
    @Nullable
    public String resolve(VariantAnnotation variantAnnotation)
    {
        String variantType = null;

        if (variantAnnotation != null &&
            variantAnnotation.getAlleleString() != null)
        {
            String[] alleles = variantAnnotation.getAlleleString().split("/", -1);

            if (alleles.length == 2)
            {
                String referenceAllele = alleles[0];
                String variantAllele = alleles[1];

                variantType = resolveVariantType(referenceAllele, variantAllele);
            }
        }

        return variantType;
    }

    @Nullable
    private String resolveVariantType(String referenceAllele, String variantAllele)
    {
        String npType[] = {"SNP", "DNP", "TNP"};

        Integer refLength = referenceAllele == null || referenceAllele.equals("-") ? 0 : referenceAllele.length();
        Integer varLength = variantAllele == null || variantAllele.equals("-") ? 0 : variantAllele.length();

        if (refLength.equals(varLength))
        {
            if (refLength - 1 < 0) {
                // TODO log.info("Check " + mRecord.getTUMOR_SAMPLE_BARCODE() + " " + mRecord.getHUGO_SYMBOL());??
                return null;
            }

            return refLength < 3 ? npType[refLength - 1] : "ONP";
        }
        else {
            return refLength < varLength ? "INS" : "DEL";
        }
    }
}

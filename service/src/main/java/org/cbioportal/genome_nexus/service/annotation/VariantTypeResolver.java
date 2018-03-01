package org.cbioportal.genome_nexus.service.annotation;

import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.springframework.stereotype.Component;

@Component
public class VariantTypeResolver
{
    public String resolve(VariantAnnotation variantAnnotation)
    {
        String variantType = "";

        if (variantAnnotation.getAlleleString() != null)
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

    private String resolveVariantType(String referenceAllele, String variantAllele)
    {
        String npType[] = {"SNP", "DNP", "TNP"};

        Integer refLength = referenceAllele.equals("-") ? 0 : referenceAllele.length();
        Integer varLength = variantAllele.equals("-") ? 0 : variantAllele.length();

        if (refLength.equals(varLength))
        {
            if (refLength - 1 < 0) {
                // TODO log.info("Check " + mRecord.getTUMOR_SAMPLE_BARCODE() + " " + mRecord.getHUGO_SYMBOL());??
                return "";
            }

            return refLength < 3 ? npType[refLength - 1] : "ONP";
        }
        else {
            return refLength < varLength ? "INS" : "DEL";
        }
    }
}

package org.cbioportal.genome_nexus.util;


public class VariantId
{
    /**
     * Build variant id in myVariantInfo id structure
     *
     * @param input input variant
     * @return  variant id
     */
    public static String buildVariantId(String input)
    {
        StringBuilder variant = new StringBuilder(input);
        if(variant.toString().contains("g.") && !variant.toString().startsWith("chr"))
        {
            variant.insert(0,"chr");
        }
        // Check if the variant id contains "del", if so we will remove the deleted nucleotides
        if (variant.toString().contains("del"))
        {
            int start = variant.lastIndexOf("del") + 3;
            int end = variant.length();
            // If the variant id contains "del" and "ins", remove characters between "del" to "ins"
            if (variant.toString().contains("ins")) {
                end = variant.lastIndexOf("ins");
            }
            variant.delete(start, end);
        }
        return variant.toString();
    }

}

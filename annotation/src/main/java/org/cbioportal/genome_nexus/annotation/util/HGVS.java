package org.cbioportal.genome_nexus.annotation.util;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.List;

public class HGVS {

    /**
     * Transforms the given variant string into the comma-separated variant supplied to Mutation Assessor
     *      ex: 7:g.140453136A>T    -->     7,140453136,A,T
     *
     */
    public static String getMutationAssessorString(String input) {
        List<String> variant = parseInput(input);
        if (variant.size() != 6)
        {
            return null;
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < variant.size(); i++) {

            // skip the sequence type and '>' symbol
            if (i == 1 || i == 4)
            {
                continue;
            }
            sb.append(variant.get(i)+",");
        }
        sb.deleteCharAt(sb.length()-1);

        return sb.toString();
    }

    /**
     * Takes in HGVS string and splits elements into list format
     * ONLY SUPPORTS MISSENSE MUTATIONS
     * todo: add in other types of mutations
     *
     * @param input variant in HGVS notation
     * @return ArrayList<String> [<reference_sequence_id>:<sequence_type>.<position><change>]
     */
    public static List<String> parseInput(String input) {

        List<String> variant = new ArrayList<>();

        if (input.matches("[^\\s]+:[a-z].[0-9_]+[A-Za-z>]+"))
        {
            variant.add(input.substring(0, input.indexOf(':')));
            variant.add(Character.toString(input.charAt(input.indexOf(':') + 1)));

            String str = input.substring(input.indexOf(':') + 3);

            // separates position and variation
            Pattern pattern = Pattern.compile("[0-9_][A-Za-z>]");
            Matcher matcher = pattern.matcher(str);
            matcher.find();

            String variantString = str.substring(matcher.start() + 1);
            String positionString = str.substring(0, matcher.start() + 1);

            if (positionString.matches("[0-9]+_[0-9]+")
                || positionString.matches("[0-9]+"))
            {
                variant.add(positionString);
            }

            if (variantString.matches("[A-Za-z]+>[A-Za-z]+"))
            {
                variant.add(variantString.substring(0, variantString.indexOf('>')));
                variant.add(">");
                variant.add(variantString.substring(variantString.indexOf('>') + 1));
            }

        }

        return variant;
    }

}


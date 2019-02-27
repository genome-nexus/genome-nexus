public class GenomicVariant {
    String chromosome;
    Integer start;
    Integer end;
    String ref;
    String alt;

    public GenomicVariant(String chromosome, Integer start, Integer end, String ref, String alt) {
        this.chromosome = chromosome;
        this.start = start;
        this.end = end;
        this.ref = ref;
        this.alt = alt;
    }

    public static GenomicVariant fromHgvs(String hgvs) {
        int chrToStart = hgvs.indexOf(":");
        int startToEnd = hgvs.indexOf("_");
        int ref = getRef(hgvs);
        int refIndex = hgvs.indexOf(ref);

        chromosome = hgvs.substring(0, startIndex);
        start = hgvs.substring(chrToStart + 1, startToEnd);
        end = hgvs.substring(startToEnd + 1, refIndex);
        ref = hgvs.substring(refIndex, refIndex + ref.length);
        alt = hgvs.substring(refIndex + ref.length);
        
        return new GenomicVariant(chromosome, Integer.valueOf(start), Integer.valueOf(end), ref, alt);
    }

    private String getRef(String hgvs){
        String[] refs = {">", "del", "dup", "ins", "con", "delins"};
        String ans;
        for (String ref: refs){ 
            if (hgvs.contains(ref))
                ans = ref;
        }
        return ans;
    }
}
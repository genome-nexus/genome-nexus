package org.cbioportal.genome_nexus.model;

import java.util.*;

public class ProteinLocation
{
    private String transcriptId;
    private Integer start;
    private Integer end;
    private String mutationType;

    public ProteinLocation() {}

    public ProteinLocation(String transcriptId, Integer start, Integer end, String mutationType) {
        this.transcriptId = transcriptId;
        this.start = start;
        this.end = end;
        this.mutationType = mutationType;
    }

    public String getTranscriptId() {
        return transcriptId;
    }

    public Integer getStart() {
        return start;
    }

    public Integer getEnd() {
        return end;
    }

    public String getMutationType() {
        return mutationType;
    }

    public List<Hotspot> filterHotspot(List<Hotspot> hotspots, ProteinLocation proteinLocation){
        int start = proteinLocation.getStart();
        int end = proteinLocation.getEnd();
        String type = proteinLocation.getMutationType();
        List<Hotspot> result = new ArrayList<>();
        for (Hotspot hotspot: hotspots) {
            boolean valid = true;
            
            // Protein location
            int hotspotStart, hotspotStop;
            String hotspotPosition = hotspot.getResidue();
            hotspotPosition = hotspotPosition.replaceAll("[^0-9.]", "");
            if (hotspotPosition.split("-").length > 1){
                hotspotStart = Integer.parseInt(hotspotPosition.split("-")[0]);
                hotspotStop = Integer.parseInt(hotspotPosition.split("-")[1]);
            }
            else {
                hotspotStart = Integer.parseInt(hotspotPosition);
                hotspotStop = Integer.parseInt(hotspotPosition);
            }
            valid &= (start <= hotspotStart && end >= hotspotStop) ? true : false;
            
            // Mutation type
            if (type.equals("Missense_Mutation")) {
                if (hotspot.getType().contains("3d") || hotspot.getType().contains("single residue")) {
                    valid &= true;
                }
                else {
                    valid = false;
                }
            }
            if (type.equals("In_Frame_Ins") || type.equals("In_Frame_Del")) {
                if (hotspot.getType().contains("in-frame")) {
                    valid &= true;
                }
                else {
                    valid = false;
                }
            }
            if (type.equals("Splice_Site") || type.equals("Splice_Region")) {
                if (hotspot.getType().contains("splice")) {
                    valid &= true;
                }
                else {
                    valid = false;
                }
            }

            // Add hotspot
            if (valid) result.add(hotspot);
        }
        
        return result;
    }
}

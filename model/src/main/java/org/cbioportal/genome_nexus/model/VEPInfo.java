package org.cbioportal.genome_nexus.model;

public class VEPInfo {

    // server corresponds to genome nexus VEP code release
    // cache corresponds to downloaded FASTA cache
    // comment is an additional disclaimer in the event we are using ENSEMBL VEP
    public Version server;
    public Version cache;
    public String comment;

    public VEPInfo(String gnVepVersion, String vepDataVersion, String comment, boolean isStatic) {       
        this.server = new Version(gnVepVersion, isStatic);
        this.cache = new Version(vepDataVersion, isStatic);
        if (this.comment != null) {
            this.comment = comment;
        }
    }
}

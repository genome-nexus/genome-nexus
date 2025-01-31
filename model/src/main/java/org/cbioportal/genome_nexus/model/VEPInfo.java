package org.cbioportal.genome_nexus.model;

public class VEPInfo {

    // server corresponds to genome nexus VEP code release
    // cache corresponds to downloaded FASTA cache
    // comment is an additional disclaimer in the event we are using ENSEMBL VEP
    public Version server;
    public Version cache;
    public String comment;

    public VEPInfo(String vepServerVersion, String vepVersion, String comment, boolean isStatic) {       
        this.server = new Version(vepServerVersion, isStatic);
        this.cache = new Version(vepVersion, isStatic);
        if (this.comment != null) {
            this.comment = comment;
        }
    }
}

package org.cbioportal.genome_nexus.model;

import com.univocity.parsers.annotations.Parsed;
import com.univocity.parsers.annotations.Trim;

/**
 * @author Selcuk Onur Sumer
 */
public class PfamA
{
    @Trim
    @Parsed(field = "pfamA_acc")
    private String pfamAccession;

    @Trim
    @Parsed(field = "pfamA_id")
    private String pfamId;

    @Trim
    @Parsed(field = "description")
    private String pfamDescription;

    public String getPfamAccession() {
        return pfamAccession;
    }

    public void setPfamAccession(String pfamAccession) {
        this.pfamAccession = pfamAccession;
    }

    public String getPfamId() {
        return pfamId;
    }

    public void setPfamId(String pfamId) {
        this.pfamId = pfamId;
    }

    public String getPfamDescription() {
        return pfamDescription;
    }

    public void setPfamDescription(String pfamDescription) {
        this.pfamDescription = pfamDescription;
    }
}

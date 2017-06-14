package org.cbioportal.genome_nexus.annotation.service.internal;

import org.cbioportal.genome_nexus.annotation.domain.Projection;
import org.cbioportal.genome_nexus.annotation.domain.TranscriptConsequence;
import org.cbioportal.genome_nexus.annotation.domain.VariantAnnotation;
import org.cbioportal.genome_nexus.annotation.service.AnnotationEnricher;

/**
 * @author Hongxin Zhang
 */
public class VEPEnricher implements AnnotationEnricher {
    private VEPDetailedEnrichmentService vepservice;
    private Projection projection;

    public VEPEnricher(VEPDetailedEnrichmentService vepservice) {
        this(vepservice, Projection.SUMMARY);
    }

    public VEPEnricher(VEPDetailedEnrichmentService vepservice, Projection projection) {
        this.vepservice = vepservice;
        this.projection = projection;
    }

    @Override
    public void enrich(VariantAnnotation annotation) {
        if (annotation.getTranscriptConsequences() != null
            && this.projection != null && this.projection.equals(Projection.DETAILED)) {
            for (TranscriptConsequence transcriptConsequence : annotation.getTranscriptConsequences()) {
                transcriptConsequence.setHgvspShort(vepservice.resolveHgvspShort(transcriptConsequence.getHgvsp()));
                transcriptConsequence.setRefSeq(vepservice.resolveRefSeq(transcriptConsequence.getRefseqTranscriptIds()));
                transcriptConsequence.setCodonChange(vepservice.resolveCodonChange(transcriptConsequence.getCodons()));
                transcriptConsequence.setConsequence(vepservice.resolveConsequence(transcriptConsequence.getConsequenceTerms()));
            }
        }
    }
}

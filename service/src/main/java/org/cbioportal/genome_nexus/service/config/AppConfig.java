package org.cbioportal.genome_nexus.service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppConfig {

    @Value("${prioritize_oncokb_gene_transcripts:false}")
    private String prioritizeOncokbGeneTranscripts;

    public String getPrioritizeOncokbGeneTranscripts() {
        return prioritizeOncokbGeneTranscripts;
    }
}

package org.cbioportal.genome_nexus.service.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cbioportal.genome_nexus.component.annotation.Grch38ReferenceSequenceResolver;
import org.cbioportal.genome_nexus.component.annotation.IndexedFastaGrch38ReferenceSequenceResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

/** Local indexed-FASTA GRCh38 resolver configuration */
@Configuration
public class AlphaGenomeConfiguration
{
    private static final Log LOG = LogFactory.getLog(AlphaGenomeConfiguration.class);

    @Bean
    public Grch38ReferenceSequenceResolver grch38ReferenceSequenceResolver(
        @Value("${grch38.fasta.path:}") String fastaPath)
    {
        if (fastaPath != null && !fastaPath.trim().isEmpty()) {
            File fastaFile = new File(fastaPath);
            if (fastaFile.exists()) {
                try {
                    LOG.info("Using local GRCh38 indexed FASTA reference at " + fastaPath);
                    return new IndexedFastaGrch38ReferenceSequenceResolver(fastaFile);
                } catch (Exception e) {
                    throw new IllegalStateException(
                        "Could not open GRCh38 FASTA at " + fastaPath, e);
                }
            } else {
                LOG.warn("Configured grch38.fasta.path does not exist: " + fastaPath);
            }
        }
        LOG.warn("No local GRCh38 FASTA path configured via grch38.fasta.path");
        return (chrom, zeroBasedStart, zeroBasedEndExclusive) -> {
            throw new IllegalStateException(
                "Grch38ReferenceSequenceResolver is not configured (grch38.fasta.path is unset or file not found)");
        };
    }
}

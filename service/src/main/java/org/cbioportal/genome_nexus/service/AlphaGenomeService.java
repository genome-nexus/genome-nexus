package org.cbioportal.genome_nexus.service;

import org.cbioportal.genome_nexus.model.AlphaGenome;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.exception.AlphaGenomeNotFoundException;
import org.cbioportal.genome_nexus.service.exception.AlphaGenomeWebServiceException;

import java.util.List;

public interface AlphaGenomeService
{
    AlphaGenome getAlphaGenomeByAnnotation(VariantAnnotation annotation)
        throws AlphaGenomeNotFoundException, AlphaGenomeWebServiceException;

    List<AlphaGenome> getAlphaGenomeByAnnotation(List<VariantAnnotation> annotations)
        throws AlphaGenomeWebServiceException;
}

package org.cbioportal.genome_nexus.service;

import java.util.List;

import org.cbioportal.genome_nexus.model.uniprot.ProteinFeatureInfo;

public interface UniprotService {
    ProteinFeatureInfo getUniprotFeaturesByAccession(String accession, List<String> categories, List<String> types);
}
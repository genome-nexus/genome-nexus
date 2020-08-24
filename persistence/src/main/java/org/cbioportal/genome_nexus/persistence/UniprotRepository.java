package org.cbioportal.genome_nexus.persistence;

import java.util.List;

import org.cbioportal.genome_nexus.model.uniprot.ProteinFeatureInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UniprotRepository extends MongoRepository<ProteinFeatureInfo, String>, GenericMongoRepository {
    ProteinFeatureInfo getUniprotFeaturesByAccession(String accession, List<String> categories, List<String> type);
}

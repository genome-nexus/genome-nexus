package org.cbioportal.genome_nexus.persistence;

import org.cbioportal.genome_nexus.model.AlphaGenome;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface AlphaGenomeRepository
    extends MongoRepository<AlphaGenome, String>, GenericMongoRepository {}

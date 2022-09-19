package org.cbioportal.genome_nexus.persistence;

import java.util.Optional;

import org.cbioportal.genome_nexus.model.MutationAssessor;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface MutationAssessorRepository
    extends MongoRepository<MutationAssessor, String>, GenericMongoRepository {
        Optional<MutationAssessor> findById(String id);
    }

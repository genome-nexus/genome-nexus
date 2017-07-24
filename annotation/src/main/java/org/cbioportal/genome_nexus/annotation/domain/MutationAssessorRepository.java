package org.cbioportal.genome_nexus.annotation.domain;

import org.springframework.data.mongodb.repository.MongoRepository;


public interface MutationAssessorRepository extends MongoRepository<MutationAssessor, String> {}

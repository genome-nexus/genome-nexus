package org.cbioportal.genome_nexus.persistence;

import org.cbioportal.genome_nexus.model.SimpleCacheEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author Selcuk Onur Sumer
 */
public interface SimpleCacheRepository extends MongoRepository<SimpleCacheEntity, String> {}

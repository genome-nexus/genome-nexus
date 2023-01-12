package org.cbioportal.genome_nexus.persistence;

import org.cbioportal.genome_nexus.model.SourceVersionInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AnnotationVersionRepository extends MongoRepository<SourceVersionInfo, String>, GenericMongoRepository {}
package org.cbioportal.genome_nexus.persistence.internal;

import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VariantAnnotationRepository extends MongoRepository<VariantAnnotation, String> {}
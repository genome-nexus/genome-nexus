package org.cbioportal.genome_nexus.persistence.internal;

import org.springframework.stereotype.Repository;

@Repository
public class VariantAnnotationRepositoryImpl extends JsonMongoRepositoryImpl
{
    public static final String COLLECTION = "vep.annotation";
}

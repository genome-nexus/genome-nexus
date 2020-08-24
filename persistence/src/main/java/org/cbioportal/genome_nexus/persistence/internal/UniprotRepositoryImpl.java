package org.cbioportal.genome_nexus.persistence.internal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UniprotRepositoryImpl extends JsonMongoRepositoryImpl
{
    public static final String COLLECTION = "uniprot.features";

    @Autowired
    public UniprotRepositoryImpl(MongoTemplate mongoTemplate)
    {
        super(mongoTemplate);
    }    
}
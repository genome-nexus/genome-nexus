package org.cbioportal.genome_nexus.persistence.internal;

import org.cbioportal.genome_nexus.model.SimpleCacheEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PlainTextMongoRepositoryImpl extends BaseGenericMongoRepository
{
    @Autowired
    public PlainTextMongoRepositoryImpl(MongoTemplate mongoTemplate)
    {
        super(mongoTemplate);
    }

    @Override
    public void saveStringValue(String collection, String key, String plainText)
    {
        if (plainText != null && plainText.length() > 0)
        {
            // TODO sanitize value before caching?

            // save the object into the correct repository
            this.mongoTemplate.save(new SimpleCacheEntity(key, plainText), collection);
        }
    }
}

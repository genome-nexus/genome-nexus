package org.cbioportal.genome_nexus.persistence.internal;

import com.mongodb.BulkWriteOperation;
import com.mongodb.DBObject;
import org.cbioportal.genome_nexus.persistence.GenericMongoRepository;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

public abstract class BaseGenericMongoRepository implements GenericMongoRepository
{
    protected final MongoTemplate mongoTemplate;

    public BaseGenericMongoRepository(MongoTemplate mongoTemplate)
    {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void saveDBObjects(String collection, List<DBObject> dbObjects)
    {
        // nothing to save, abort
        if (dbObjects == null || dbObjects.size() == 0) {
            return;
        }

        BulkWriteOperation bulkWriteOperation =
            this.mongoTemplate.getCollection(collection).initializeUnorderedBulkOperation();

        for (DBObject dbObject: dbObjects) {
            bulkWriteOperation.insert(dbObject);
        }

        bulkWriteOperation.execute();
    }
}

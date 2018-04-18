package org.cbioportal.genome_nexus.persistence;

import com.mongodb.DBObject;

import java.util.List;

public interface GenericMongoRepository
{
    void saveStringValue(String collection, String key, String value);
    void saveDBObjects(String collection, List<DBObject> dbObjects);
}

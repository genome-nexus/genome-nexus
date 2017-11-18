package org.cbioportal.genome_nexus.persistence;

public interface GenericMongoRepository
{
    void saveStringValue(String collection, String key, String value);
}

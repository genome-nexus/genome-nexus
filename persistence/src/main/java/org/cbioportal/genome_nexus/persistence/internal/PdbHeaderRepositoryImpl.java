package org.cbioportal.genome_nexus.persistence.internal;

import org.cbioportal.genome_nexus.model.PdbHeader;
import org.cbioportal.genome_nexus.model.SimpleCacheEntity;
import org.cbioportal.genome_nexus.util.PdbHeaderParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class PdbHeaderRepositoryImpl
    extends PlainTextMongoRepositoryImpl // this allows us to save the string value into the simple cache collection
    implements PdbHeaderRepositoryCustom<PdbHeader, String>
{
    public static final String COLLECTION = "pdb.header";

    private final PdbHeaderParser parser;

    @Autowired
    public PdbHeaderRepositoryImpl(MongoTemplate mongoTemplate, PdbHeaderParser parser)
    {
        super(mongoTemplate);
        this.parser = parser;
    }

    public PdbHeader findOne(String id)
    {
        // get the value stored in the simple cache
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));

        SimpleCacheEntity entity = this.mongoTemplate.findOne(query, SimpleCacheEntity.class, COLLECTION);

        // and then map it to a PdbHeader instance to return
        if (entity != null) {
            return this.parser.convertToInstance(entity.getValue());
        }
        else {
            return null;
        }
    }
}

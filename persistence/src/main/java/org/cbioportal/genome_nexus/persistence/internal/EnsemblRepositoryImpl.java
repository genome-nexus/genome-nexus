package org.cbioportal.genome_nexus.persistence.internal;

import org.cbioportal.genome_nexus.model.EnsemblTranscript;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.mongodb.BasicDBObject;
import com.mongodb.Cursor;
import org.springframework.stereotype.Repository;

@Repository
public class EnsemblRepositoryImpl implements EnsemblRepositoryCustom
{
    @Autowired
    private MongoTemplate mongoTemplate;

    public static final String CANONICAL_TRANSCRIPTS_COLLECTION = "ensembl.canonical_transcript_per_hgnc";
    public static final String TRANSCRIPTS_COLLECTION = "ensembl.biomart_transcripts";

    @Override
    public EnsemblTranscript findOneByHugoSymbol(String hugoSymbol, String isoformOverrideSource) {
        BasicDBObject whereQuery = new BasicDBObject();
        whereQuery.put("hgnc_symbol", hugoSymbol);

        Cursor transcriptCursor;
        Cursor canonicalCursor = mongoTemplate.getCollection(CANONICAL_TRANSCRIPTS_COLLECTION).find(whereQuery);

        if (canonicalCursor.hasNext()) {
            BasicDBObject canonicalTranscriptsPerSource = (BasicDBObject) canonicalCursor.next();

            String transcriptId = (String) canonicalTranscriptsPerSource.get(isoformOverrideSource + "_canonical_transcript");

            whereQuery = new BasicDBObject();
            whereQuery.put(EnsemblTranscript.TRANSCRIPT_ID_FIELD_NAME, transcriptId);

            transcriptCursor = mongoTemplate.getCollection(TRANSCRIPTS_COLLECTION).find(whereQuery);
            if (transcriptCursor.hasNext()) {
               EnsemblTranscript transcript = mongoTemplate.getConverter().read(EnsemblTranscript.class, transcriptCursor.next());
                if (transcript != null) {
                    return transcript;
                }
            }
        }
        return null;
    }
}

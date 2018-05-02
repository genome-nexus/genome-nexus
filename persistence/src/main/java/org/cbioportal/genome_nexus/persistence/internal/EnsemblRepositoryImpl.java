package org.cbioportal.genome_nexus.persistence.internal;

import org.cbioportal.genome_nexus.model.EnsemblCanonical;
import org.cbioportal.genome_nexus.model.EnsemblGene;
import org.cbioportal.genome_nexus.model.EnsemblTranscript;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.regex.Pattern;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.Cursor;
import org.springframework.stereotype.Repository;

@Repository
public class EnsemblRepositoryImpl implements EnsemblRepositoryCustom
{
    private final MongoTemplate mongoTemplate;

    @Autowired
    public EnsemblRepositoryImpl(MongoTemplate mongoTemplate)
    {
        this.mongoTemplate = mongoTemplate;
    }

    public static final String CANONICAL_TRANSCRIPTS_COLLECTION = "ensembl.canonical_transcript_per_hgnc";
    public static final String TRANSCRIPTS_COLLECTION = "ensembl.biomart_transcripts";

    private EnsemblCanonical findOneCanonicalByHugoSymbol(String hugoSymbol) {
        Query query = new Query();

        // case insensitive exact match query
        Criteria approvedSymbolCriteria = Criteria.where("hgnc_symbol").regex("^" + hugoSymbol + "$", "i");
        // synonyms
        Criteria synonymCriteria = Criteria.where("synonyms").regex("(^| |,)" + hugoSymbol + "($| |,)", "i");
        // prev symbols
        Criteria prevSymbolsCriteria = Criteria.where("previous_symbols").regex("(^| |,)" + hugoSymbol + "($| |,)", "i");

        query.addCriteria(new Criteria().orOperator(approvedSymbolCriteria, synonymCriteria, prevSymbolsCriteria));

        EnsemblCanonical ensemblCanonical = mongoTemplate.findOne(query,
            EnsemblCanonical.class, CANONICAL_TRANSCRIPTS_COLLECTION);

        return ensemblCanonical;
    }

    @Override
    public EnsemblGene getCanonicalEnsemblGeneIdByHugoSymbol(String hugoSymbol) {
        EnsemblCanonical ensemblCanonical = this.findOneCanonicalByHugoSymbol(hugoSymbol);
        if (ensemblCanonical != null) {
            return new EnsemblGene(ensemblCanonical);
        } else {
            return null;
        }
    }

    @Override
    public EnsemblTranscript findOneByHugoSymbolIgnoreCase(String hugoSymbol, String isoformOverrideSource) {
        EnsemblCanonical ensemblCanonical = this.findOneCanonicalByHugoSymbol(hugoSymbol);
        if (ensemblCanonical != null) {
            Query query = new Query();
            query.addCriteria(Criteria.where(EnsemblTranscript.TRANSCRIPT_ID_FIELD_NAME).is(ensemblCanonical.getCanonicalTranscriptId(isoformOverrideSource)));

            EnsemblTranscript transcript = mongoTemplate.findOne(query, EnsemblTranscript.class, TRANSCRIPTS_COLLECTION);
            if (transcript != null) {
                return transcript;
            }
        }
        return null;
    }
}

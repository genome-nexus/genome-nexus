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
        EnsemblCanonical ensemblCanonical;
        Query query;

        // check approved symbols
        Criteria approvedSymbolCriteria = Criteria.where("hgnc_symbol").regex("^" + hugoSymbol + "$", "i");
        query = new Query();
        query.addCriteria(approvedSymbolCriteria);
        ensemblCanonical = mongoTemplate.findOne(query,
            EnsemblCanonical.class, CANONICAL_TRANSCRIPTS_COLLECTION);

        if (ensemblCanonical == null) {
            // check prev symbols
            Criteria prevSymbolsCriteria = Criteria.where("previous_symbols").regex("(^| |,)" + hugoSymbol + "($| |,)", "i");
            query = new Query();
            query.addCriteria(prevSymbolsCriteria);
            ensemblCanonical = mongoTemplate.findOne(query,
                EnsemblCanonical.class, CANONICAL_TRANSCRIPTS_COLLECTION);

            if (ensemblCanonical == null) {
                // check synonyms
                Criteria synonymCriteria = Criteria.where("synonyms").regex("(^| |,)" + hugoSymbol + "($| |,)", "i");
                query = new Query();
                query.addCriteria(synonymCriteria);
                ensemblCanonical = mongoTemplate.findOne(query,
                    EnsemblCanonical.class, CANONICAL_TRANSCRIPTS_COLLECTION);
            }
        }

        return ensemblCanonical;
    }

    private EnsemblCanonical findOneCanonicalByEntrezGeneId(String entrezGeneId) {
        EnsemblCanonical ensemblCanonical;
        Query query;

        // search by entrez gene id
        Criteria criteria = Criteria.where("entrez_gene_id").is(Integer.valueOf(entrezGeneId));
        query = new Query();
        query.addCriteria(criteria);
        ensemblCanonical = mongoTemplate.findOne(query,
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
    public EnsemblGene getCanonicalEnsemblGeneIdByEntrezGeneId(String entrezGeneId) {
        EnsemblCanonical ensemblCanonical = this.findOneCanonicalByEntrezGeneId(entrezGeneId);
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

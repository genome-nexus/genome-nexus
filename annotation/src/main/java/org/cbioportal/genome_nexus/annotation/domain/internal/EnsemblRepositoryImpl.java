package org.cbioportal.genome_nexus.annotation.domain.internal;

import org.cbioportal.genome_nexus.annotation.util.parse.HugoSymbolRecord;

import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.common.processor.BeanProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.cbioportal.genome_nexus.annotation.domain.EnsemblTranscript;
import org.cbioportal.genome_nexus.annotation.domain.EnsemblRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.mongodb.BasicDBObject;
import com.mongodb.Cursor;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.*;

public class EnsemblRepositoryImpl implements EnsemblRepositoryCustom
{
    @Autowired
    private MongoTemplate mongoTemplate;

    public static final String CANONICAL_TRANSCRIPTS_COLLECTION = "ensembl.canonical_transcript_per_hgnc";
    public static final String TRANSCRIPTS_COLLECTION = "ensembl.biomart_transcripts";

    @Override
    public EnsemblTranscript findByHugoSymbol(String hugoSymbol, String isoformOverrideSource) {
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

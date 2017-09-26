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

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.*;

@Component("defaultEnsemblRepository")
public class EnsemblRepositoryImpl implements EnsemblRepository
{
    private String biomartResourceURI;
    private String canonicalTranscriptPerHugoSymbolResourceURI;

    private List<EnsemblTranscript> ensemblTranscriptList;
    private Map<String, List<EnsemblTranscript>> indexByTranscript;
    private Map<String, List<EnsemblTranscript>> indexByGene;
    private Map<String, List<EnsemblTranscript>> indexByProtein;
    private Map<String, Map<String, List<EnsemblTranscript>>> indexByHugoSymbol;

    @Autowired
    public EnsemblRepositoryImpl(
        @Value("${ensembl.biomart.transcripts:ensembl_biomart_transcripts.txt}") String biomartResourceURI,
        @Value("${ensembl.biomart.hgnc_canonical_transcripts:ensembl_biomart_canonical_transcripts_per_hgnc.txt}") String canonicalTranscriptPerHugoSymbolResourceURI
    )
    {
        this.biomartResourceURI = biomartResourceURI;
        this.canonicalTranscriptPerHugoSymbolResourceURI = canonicalTranscriptPerHugoSymbolResourceURI;

        // populate the list and build the indices
        this.populateData();
    }

    public List<EnsemblTranscript> findAll()
    {
        return this.ensemblTranscriptList;
    }

    @Override
    public List<EnsemblTranscript> findByTranscriptId(String transcriptId) {
        return this.find(this.indexByTranscript, transcriptId);
    }

    @Override
    public List<EnsemblTranscript> findByProteinId(String proteinId) {
        return this.find(this.indexByProtein, proteinId);
    }

    @Override
    public List<EnsemblTranscript> findByGeneId(String geneId) {
        return this.find(this.indexByGene, geneId);
    }

    @Override
    public List<EnsemblTranscript> findByHugoSymbol(String hugoSymbol, String isoformOverrideSource) {
        Map<String, List<EnsemblTranscript>> canonicalTranscripts = indexByHugoSymbol.get(hugoSymbol);
        if (canonicalTranscripts == null) {
            return Collections.emptyList();
        } else {
            List<EnsemblTranscript> transcripts = canonicalTranscripts.get(isoformOverrideSource);
            if (transcripts == null) {
                return Collections.emptyList();
            } else {
                return transcripts;
            }
        }
    }

    private List<EnsemblTranscript> parseBiomartTSV()
    {
        BeanListProcessor<EnsemblTranscript> rowProcessor = new BeanListProcessor<>(EnsemblTranscript.class);

        CsvParserSettings parserSettings = new CsvParserSettings();
        parserSettings.setHeaderExtractionEnabled(true);
        parserSettings.getFormat().setDelimiter('\t');
        parserSettings.setRowProcessor(rowProcessor);

        CsvParser parser = new CsvParser(parserSettings);
        parser.parse(getReader(this.biomartResourceURI));

        return rowProcessor.getBeans();
    }


    private Map<String, Map<String, List<EnsemblTranscript>>> parseCanonicalTranscriptPerHugoSymbolTSV()
    {
        final Map<String, Map<String, List<EnsemblTranscript>>> indexByHugoSymbol = new HashMap<>();
        CsvParserSettings parserSettings = new CsvParserSettings();
        parserSettings.setHeaderExtractionEnabled(true);
        parserSettings.getFormat().setDelimiter('\t');
        parserSettings.setRowProcessor(
            new BeanProcessor<HugoSymbolRecord>(HugoSymbolRecord.class) {
               @Override
               public void beanProcessed(HugoSymbolRecord hugoSymbolRecord, ParsingContext context) {
                   Map<String, List<EnsemblTranscript>> canonicalMap = indexByHugoSymbol.get(hugoSymbolRecord.getHugoSymbol());
                   if (canonicalMap == null) {
                       canonicalMap = new HashMap();
                   } else {
                       System.err.println("Hugo Symbols should be unique");
                   }
                   canonicalMap.put("ensembl", indexByTranscript.get(hugoSymbolRecord.getEnsemblCanonicalTranscript()));
                   canonicalMap.put("genomeNexus", indexByTranscript.get(hugoSymbolRecord.getGenomeNexusCanonicalTranscript()));
                   canonicalMap.put("uniprot", indexByTranscript.get(hugoSymbolRecord.getUniprotCanonicalTranscript()));
                   canonicalMap.put("mskcc", indexByTranscript.get(hugoSymbolRecord.getMskccCanonicalTranscript()));
                   indexByHugoSymbol.put(hugoSymbolRecord.getHugoSymbol(), canonicalMap);
               }
           }
        );

        CsvParser parser = new CsvParser(parserSettings);
        parser.parse(getReader(this.canonicalTranscriptPerHugoSymbolResourceURI));
        return indexByHugoSymbol;
    }

    /**
     * Creates a reader for a resource in the relative path
     *
     * @param resourceURI path of the resource to be read
     * @return a reader of the resource
     */
    private Reader getReader(String resourceURI)
    {
        Resource resource = new ClassPathResource(resourceURI);

        try {
            return new InputStreamReader(resource.getInputStream(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Unable to read input", e);
        } catch (IOException e) {
            throw new IllegalStateException("Input not found", e);
        }
    }

    private List<EnsemblTranscript> find(Map<String, List<EnsemblTranscript>> index, String key)
    {
        List<EnsemblTranscript> transcripts = index.get(key);

        if (transcripts == null)
        {
            transcripts = Collections.emptyList();
        }

        return transcripts;
    }

    private void populateData()
    {
        if (this.ensemblTranscriptList == null)
        {
            this.ensemblTranscriptList = new ArrayList<>();
            this.ensemblTranscriptList.addAll(this.parseBiomartTSV());

            this.indexByTranscript = this.index(ensemblTranscriptList, new TranscriptIndexKeyGetter());
            this.indexByProtein = this.index(ensemblTranscriptList, new ProteinIndexKeyGetter());
            this.indexByGene = this.index(ensemblTranscriptList, new GeneIndexKeyGetter());
        }
        if (this.indexByHugoSymbol == null)
        {
            this.indexByHugoSymbol = this.parseCanonicalTranscriptPerHugoSymbolTSV();
        }
    }

    private Map<String, List<EnsemblTranscript>> index(List<EnsemblTranscript> ensemblTranscriptList, IndexKeyGetter getter)
    {
        Map<String, List<EnsemblTranscript>> map = new HashMap<>();

        for (EnsemblTranscript transcript : ensemblTranscriptList)
        {
            String id = getter.getIndexKey(transcript);

            // skip invalid/empty id values
            if (id == null || id.trim().length() == 0) {
                continue;
            }

            List<EnsemblTranscript> list = map.get(id);

            if (list == null)
            {
                list = new ArrayList<>();
                map.put(id, list);
            }

            list.add(transcript);
        }

        return map;
    }

    private interface IndexKeyGetter
    {
        String getIndexKey(EnsemblTranscript transcript);
    }

    private class TranscriptIndexKeyGetter implements IndexKeyGetter
    {
        public String getIndexKey(EnsemblTranscript transcript)
        {
            return transcript.getTranscriptId();
        }
    }

    private class GeneIndexKeyGetter implements IndexKeyGetter
    {
        public String getIndexKey(EnsemblTranscript transcript)
        {
            return transcript.getGeneId();
        }
    }

    private class ProteinIndexKeyGetter implements IndexKeyGetter
    {
        public String getIndexKey(EnsemblTranscript transcript)
        {
            return transcript.getProteinId();
        }
    }
}

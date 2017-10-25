package org.cbioportal.genome_nexus.persistence.internal;

import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.cbioportal.genome_nexus.model.IsoformOverride;
import org.cbioportal.genome_nexus.persistence.IsoformOverrideRepository;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Selcuk Onur Sumer
 */
public class IsoformOverrideRepositoryImpl implements IsoformOverrideRepository
{
    private List<IsoformOverride> overrideList;
    private Map<String, IsoformOverride> overrideMap;
    private String resourceURI;

    public IsoformOverrideRepositoryImpl(String resourceURI)
    {
        this.resourceURI = resourceURI;
    }

    @Override
    public List<IsoformOverride> findAllAsList()
    {
        if (overrideList == null)
        {
            populateData();
        }

        return overrideList;
    }

    @Override
    public Map<String, IsoformOverride> findAllAsMap()
    {
        if (overrideMap == null)
        {
            populateData();
        }

        return overrideMap;
    }

    @Override
    public IsoformOverride findIsoformOverride(String transcriptId)
    {
        return findAllAsMap().get(transcriptId.toLowerCase());
    }

    private void populateData()
    {
        if (overrideList == null)
        {
            overrideList = new ArrayList<>();
        }

        if (overrideMap == null)
        {
            overrideMap = new HashMap<>();
        }

        overrideList.addAll(parseCSV());

        for (IsoformOverride override: overrideList)
        {
            overrideMap.put(override.getTranscriptId().toLowerCase(), override);
        }
    }

    public List<IsoformOverride> parseCSV()
    {
        BeanListProcessor<IsoformOverride> rowProcessor = new BeanListProcessor<>(IsoformOverride.class);

        CsvParserSettings parserSettings = new CsvParserSettings();
        parserSettings.setHeaderExtractionEnabled(true);
        parserSettings.getFormat().setDelimiter('\t');
        parserSettings.setRowProcessor(rowProcessor);

        CsvParser parser = new CsvParser(parserSettings);
        parser.parse(getReader(this.resourceURI));

        return rowProcessor.getBeans();
    }

    /**
     * Creates a reader for a resource in the relative path
     *
     * @param resourceURI path of the resource to be read
     * @return a reader of the resource
     */
    public Reader getReader(String resourceURI)
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


}

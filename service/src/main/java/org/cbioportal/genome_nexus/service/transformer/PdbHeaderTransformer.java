package org.cbioportal.genome_nexus.service.transformer;

import com.mongodb.DBObject;
import org.cbioportal.genome_nexus.model.PdbHeader;
import org.cbioportal.genome_nexus.service.ResourceTransformer;
import org.cbioportal.genome_nexus.service.exception.ResourceMappingException;
import org.cbioportal.genome_nexus.util.PdbHeaderParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PdbHeaderTransformer implements ResourceTransformer<PdbHeader>
{
    @Autowired
    private PdbHeaderParser parser;

    @Override
    public List<PdbHeader> transform(String pdbTextContent, Class<PdbHeader> type) throws ResourceMappingException
    {
        List<PdbHeader> list = null;
        PdbHeader pdbHeader = this.mapToInstance(pdbTextContent);

        if (pdbHeader != null) {
            list = new ArrayList<>(1);
            list.add(pdbHeader);
        }

        return list;
    }

    @Override
    public List<DBObject> transform(String value) {
        // TODO implement if needed
        return null;
    }

    private PdbHeader mapToInstance(String pdbTextContent) throws ResourceMappingException
    {
        PdbHeader pdbHeader = null;

        try {
            pdbHeader = this.parser.convertToInstance(pdbTextContent);
        } catch (Exception e) {
            throw new ResourceMappingException(e.getMessage());
        }

        return pdbHeader;
    }
}

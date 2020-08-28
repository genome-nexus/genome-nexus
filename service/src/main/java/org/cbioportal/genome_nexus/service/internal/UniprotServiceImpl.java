package org.cbioportal.genome_nexus.service.internal;

import java.util.*;

import org.cbioportal.genome_nexus.model.uniprot.ProteinFeatureInfo;
import org.cbioportal.genome_nexus.service.UniprotService;
import org.cbioportal.genome_nexus.service.exception.ResourceMappingException;
import org.cbioportal.genome_nexus.service.remote.UniprotDataFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

@Service
public class UniprotServiceImpl implements UniprotService {
    private final UniprotDataFetcher uniprotDataFetcher;

    @Autowired
    public UniprotServiceImpl(UniprotDataFetcher uniprotDataFetcher) {
        this.uniprotDataFetcher = uniprotDataFetcher;
    }

    @Override
    public ProteinFeatureInfo getUniprotFeaturesByAccession(String accession, List<String> categories,
            List<String> types) {
        Optional<ProteinFeatureInfo> uniprotFeatures = null;
        List<ProteinFeatureInfo> list;
        try {
            list = uniprotDataFetcher.fetchInstances(generateQueryString(accession, categories, types));
            if (list.size() > 0) {
                uniprotFeatures = Optional.ofNullable(list.get(0));
            }
        } catch (HttpClientErrorException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ResourceAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ResourceMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return uniprotFeatures.get();
    }

    private String generateQueryString(String accession, List<String> categories,
    List<String> types) {
        // example url: P04637?categories=PTM%2CVARIANTS&types=INIT_MET%2CSIGNAL
        String query = accession;
        boolean categoriesExist = categories != null && categories.size() > 0;
        boolean typesExist = types != null && types.size() > 0;
        if (categoriesExist || typesExist) {
            query += "?";
        }
        if (categoriesExist) {
            query += "categories=" + String.join(",", categories);
        }
        if (typesExist) {
            if (categoriesExist) {
                query += "&";
            }
            query += "types=" + String.join(",", types);
        }
        return query;
    }
}
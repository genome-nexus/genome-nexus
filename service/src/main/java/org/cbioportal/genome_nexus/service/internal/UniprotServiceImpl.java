package org.cbioportal.genome_nexus.service.internal;

import java.util.*;
import java.util.Optional;

import org.cbioportal.genome_nexus.model.uniprot.ProteinFeatureInfo;
import org.cbioportal.genome_nexus.persistence.UniprotRepository;
import org.cbioportal.genome_nexus.service.UniprotService;
import org.cbioportal.genome_nexus.service.exception.ResourceMappingException;
import org.cbioportal.genome_nexus.service.remote.UniprotDataFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

@Service
public class UniprotServiceImpl implements UniprotService {
    private final UniprotRepository uniprotRepository;
    private final UniprotDataFetcher uniprotDataFetcher;

    @Autowired
    public UniprotServiceImpl(UniprotRepository uniprotRepository, UniprotDataFetcher uniprotDataFetcher) {
        this.uniprotRepository = uniprotRepository;
        this.uniprotDataFetcher = uniprotDataFetcher;
    }

    @Override
    public ProteinFeatureInfo getUniprotFeaturesByAccession(String accession, List<String> categories,
            List<String> type) {
        Optional<ProteinFeatureInfo> uniprotFeatures = null;
        // uniprotFeatures =
        // Optional.ofNullable(uniprotRepository.fetchAndCache(accession));
        List<ProteinFeatureInfo> list;
        try {
            list = uniprotDataFetcher.fetchInstances(accession);
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
        //return null;
    }
}
package org.cbioportal.genome_nexus.service.internal;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cbioportal.genome_nexus.model.Alteration;
import org.cbioportal.genome_nexus.persistence.OncokbCancerGenesListRepository;
import org.cbioportal.genome_nexus.service.OncokbService;
import org.cbioportal.genome_nexus.service.exception.OncokbNotFoundException;
import org.cbioportal.genome_nexus.service.exception.OncokbWebServiceException;
import org.cbioportal.genome_nexus.service.exception.ResourceMappingException;
import org.cbioportal.genome_nexus.service.remote.OncokbDataFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.oncokb.client.CancerGene;
import org.oncokb.client.IndicatorQueryResp;

@Service
public class OncokbServiceImpl implements OncokbService {
    private static final Log LOG = LogFactory.getLog(OncokbServiceImpl.class);

    private final OncokbDataFetcher oncokbDataFetcher;
    private final OncokbCancerGenesListRepository oncokbCancerGenesListRepository;
    private Set<String> oncokbGeneSymbolList = new HashSet();

    @Autowired
    public OncokbServiceImpl(OncokbDataFetcher oncokbDataFetcher, OncokbCancerGenesListRepository oncokbCancerGenesListRepository) {
        this.oncokbDataFetcher = oncokbDataFetcher;
        this.oncokbCancerGenesListRepository = oncokbCancerGenesListRepository;
        LOG.info("Building OncoKB gene list");
        this.oncokbGeneSymbolList = this.buildList();
        LOG.info("Finished building OncoKB gene list");
    }

    public IndicatorQueryResp getOncokbByProteinChange(Alteration alteration, String token) throws OncokbNotFoundException, OncokbWebServiceException {
        Optional<IndicatorQueryResp> oncokb = null;

        oncokbDataFetcher.setOncokbToken(token);
        try {
            // get the annotation from the web service
            List<IndicatorQueryResp> list = oncokbDataFetcher.fetchInstances(generateQueryString(alteration));
            if (list.size() > 0) {
                oncokb = Optional.ofNullable(list.get(0));
            }
        } catch (HttpServerErrorException e) {
            // failure fetching external resource
            LOG.error("Failure fetching external resource: " + e.getLocalizedMessage());
        } catch (ResourceMappingException e) {
            throw new OncokbWebServiceException(e.getMessage());
        } catch (HttpClientErrorException e) {
            throw new OncokbWebServiceException(e.getResponseBodyAsString(), e.getStatusCode());
        } catch (ResourceAccessException e) {
            throw new OncokbWebServiceException(e.getMessage());
        }

        try {
            return oncokb.get();
        } catch (NoSuchElementException e) {
            throw new OncokbNotFoundException(alteration);
        }
    }

    @Override
    public IndicatorQueryResp getOncokb(Alteration alteration, String token) throws OncokbNotFoundException, OncokbWebServiceException {
        if (alteration != null) {
            IndicatorQueryResp oncokb = this.getOncokbByProteinChange(alteration, token);
            return oncokb;
        } else {
            return null;
        }
    }

    private String generateQueryString(Alteration alteration) {
        // example url: hugoSymbol=BRAF&entrezGeneId=673&alteration=V600E&consequence=missense_variant&proteinStart=600&proteinEnd=600&tumorType=Melanoma
        String query = "";
        if (alteration.getHugoSymbol() != null) {
            query = query + "hugoSymbol=" + alteration.getHugoSymbol();
        }
        if (alteration.getEntrezGeneId() != null) {
            query = query + "&entrezGeneId=" + alteration.getEntrezGeneId();
        }
        if (alteration.getAlteration() != null) {
            query= query + "&alteration=" + alteration.getAlteration();
        }
        if (alteration.getConsequence() != null) {
            query = query + "&consequence=" + alteration.getConsequence();
        }
        if (alteration.getProteinStart() != null) {
            query = query + "&proteinStart=" + alteration.getProteinStart();
        }
        if (alteration.getProteinEnd() != null) {
            query = query + "&proteinEnd=" + alteration.getProteinEnd();
        }
        if (alteration.getTumorType() != null) {
            query = query + "&tumorType=" + alteration.getTumorType();
        }
        query = query + "&referenceGenome=" + alteration.getReferenceAssembly();

        // TODO tumorType is optional for the query, currently genome nexus doesn't have tumorType data

        return query;
    }

    public List<CancerGene> getOncokbCancerGenesList() {
        return this.oncokbCancerGenesListRepository.getOncokbCancerGenesList();
    }

    private Set<String> buildList() {
        List<CancerGene> cancerGenes = this.getOncokbCancerGenesList();
        Set<String> geneSymbolList = new HashSet();
        for (CancerGene cancerGene : cancerGenes) {
            geneSymbolList.add(cancerGene.getHugoSymbol());
        }
        return geneSymbolList;
    }

    @Override
    public Set<String> getOncokbGeneSymbolList()
    {
        return this.oncokbGeneSymbolList;
    }
}

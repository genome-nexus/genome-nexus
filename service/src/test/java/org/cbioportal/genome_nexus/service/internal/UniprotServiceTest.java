package org.cbioportal.genome_nexus.service.internal;

import static org.junit.Assert.assertEquals;

import java.util.*;

import org.cbioportal.genome_nexus.model.uniprot.ProteinFeatureInfo;
import org.cbioportal.genome_nexus.service.exception.ResourceMappingException;
import org.cbioportal.genome_nexus.service.mock.UniprotFeaturesMockData;
import org.cbioportal.genome_nexus.service.remote.UniprotDataFetcher;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.junit.Test;

@RunWith(MockitoJUnitRunner.class)
public class UniprotServiceTest {
    @InjectMocks
    private UniprotServiceImpl service;

    @Mock
    private UniprotDataFetcher fetcher;

    private UniprotFeaturesMockData uniprotFeaturesMockData = new UniprotFeaturesMockData();

    @Test
    public void getUniprotFeaturesInfoByUniprotAccession()
            throws HttpClientErrorException, ResourceAccessException, ResourceMappingException
    {
        Map<String, ProteinFeatureInfo> mockData = this.uniprotFeaturesMockData.generateData();

        List<ProteinFeatureInfo> mockUniprotFeature_P04637 = new ArrayList<ProteinFeatureInfo>();
        List<ProteinFeatureInfo> mockUniprotFeature_invalid = new ArrayList<ProteinFeatureInfo>();

        mockUniprotFeature_P04637.add(mockData.get("P04637"));
        mockUniprotFeature_invalid.add(mockData.get("INVALID"));

        // mock methods in order to prevent hitting the live web API
        Mockito.when(fetcher.fetchInstances("P04637")).thenReturn(mockUniprotFeature_P04637);
        Mockito.when(fetcher.fetchInstances("INVALID")).thenReturn(mockUniprotFeature_invalid);

        ProteinFeatureInfo uniprotFeatures1 = service.getUniprotFeaturesByAccession("P04637", null, null);
        assertEquals(uniprotFeatures1.getAccession(), mockData.get("P04637").getAccession());

        ProteinFeatureInfo uniprotFeatures2 = service.getUniprotFeaturesByAccession("INVALID", null, null);
        assertEquals(uniprotFeatures2.getVersion(), mockData.get("INVALID").getVersion());
    }
}
package org.cbioportal.genome_nexus.service.mock;

import org.cbioportal.genome_nexus.model.uniprot.Feature;
import org.cbioportal.genome_nexus.model.uniprot.ProteinFeatureInfo;
import org.cbioportal.genome_nexus.service.MockData;

import java.util.*;


public class UniprotFeaturesMockData implements MockData<ProteinFeatureInfo>{
    @Override
    public Map<String, ProteinFeatureInfo> generateData()
    {
        Map<String, ProteinFeatureInfo> mockData = new HashMap<>();

        ProteinFeatureInfo uniprotFeaturesInfo;
        Feature uniprotFeature;
        List<Feature> uniprotFeatureList;

        // mock feature data
        uniprotFeature = new Feature();
        uniprotFeatureList = new ArrayList<Feature>();
        uniprotFeature.setCategory("PTM");
        uniprotFeature.setBegin("9");
        uniprotFeature.setEnd("9");
        uniprotFeatureList.add(uniprotFeature);

        // mock uniprotFeaturesInfo for P04637
        uniprotFeaturesInfo = new ProteinFeatureInfo();
        uniprotFeaturesInfo.setAccession("P04637");;
        uniprotFeaturesInfo.setEntryName("P53_HUMAN");
        uniprotFeaturesInfo.setFeatures(uniprotFeatureList);

        mockData.put("P04637", uniprotFeaturesInfo);

        // mock uniprotFeaturesInfo for invalid accession
        uniprotFeaturesInfo = new ProteinFeatureInfo();
        uniprotFeaturesInfo.setAccession(null);
        uniprotFeaturesInfo.setEntryName(null);
        uniprotFeaturesInfo.setFeatures(null);

        mockData.put("INVALID", uniprotFeaturesInfo);

        return mockData;
    }
}
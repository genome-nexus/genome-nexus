package org.cbioportal.genome_nexus.web;

import org.cbioportal.genome_nexus.model.AggregatedHotspots;
import org.cbioportal.genome_nexus.model.GenomicLocation;
import org.cbioportal.genome_nexus.model.Hotspot;
import org.cbioportal.genome_nexus.service.annotation.NotationConverter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
    properties = {
        "vep.url=http://grch37.rest.ensembl.org/vep/human/hgvs/VARIANT?content-type=application/json&xref_refseq=1&ccds=1&canonical=1&domains=1&hgvs=1&numbers=1&protein=1",
        "hotspots.url=https://www.cancerhotspots.org/api/hotspots/single/",
        "hotspots.3d.url=https://www.3dhotspots.org/api/hotspots/3d/",
        "spring.data.mongodb.uri=mongodb://localhost/integration",
        "server.port=38888"
    }
)
public class CancerHotspotsIntegrationTest
{
    private RestTemplate restTemplate = new RestTemplate();

    @Autowired
    NotationConverter notationConverter;

    @Test
    public void testSingleResidueHotspots()
    {
        String baseUrl = "http://localhost:38888/cancer_hotspots/genomic/";

        // genomic location strings (needed for GET requests)
        String[] genomicLocations = {
            "7,140453193,140453193,T,C", // recurrent and 3D
            "17,37880220,37880220,T,C", // recurrent
            "17,37879794,37879794,G,C", // recurrent
            "12,25398284,25398285,CC,GA", // recurrent and 3D
            "12,25380271,25380271,C,T", // 3D
            "10,89692940,89692940,C,T" // none
        };

        // GET requests
        Hotspot[] hotspots0 = restTemplate.getForObject(
            baseUrl + genomicLocations[0], Hotspot[].class);

        // expected to be both recurrent and 3D hotspot
        assertEquals(2, hotspots0.length);
        assertEquals("N581", hotspots0[0].getResidue());
        assertEquals("BRAF", hotspots0[0].getHugoSymbol());
        assertEquals("N581", hotspots0[1].getResidue());
        assertEquals("BRAF", hotspots0[1].getHugoSymbol());


        Hotspot[] hotspots1 = restTemplate.getForObject(
            baseUrl + genomicLocations[1], Hotspot[].class);

        // expected to be a recurrent hotspot but not 3D
        assertEquals(1, hotspots1.length);
        assertEquals("L755", hotspots1[0].getResidue());
        assertEquals("ERBB2", hotspots1[0].getHugoSymbol());
        assertEquals("single residue", hotspots1[0].getType());

        Hotspot[] hotspots2 = restTemplate.getForObject(
            baseUrl + genomicLocations[2], Hotspot[].class);

        // expected to be a recurrent hotspot but not 3D
        assertEquals(1, hotspots2.length);
        assertEquals("V697", hotspots2[0].getResidue());
        assertEquals("ERBB2", hotspots2[0].getHugoSymbol());
        assertEquals("single residue", hotspots2[0].getType());

        Hotspot[] hotspots3 = restTemplate.getForObject(
            baseUrl + genomicLocations[3], Hotspot[].class);

        // expected to be both recurrent and 3D hotspot
        assertEquals(2, hotspots3.length);
        assertEquals("G12", hotspots3[0].getResidue());
        assertEquals("KRAS", hotspots3[0].getHugoSymbol());
        assertEquals("G12", hotspots3[1].getResidue());
        assertEquals("KRAS", hotspots3[1].getHugoSymbol());

        Hotspot[] hotspots4 = restTemplate.getForObject(
            baseUrl + genomicLocations[4], Hotspot[].class);

        // expected to be 3D hotspot only
        assertEquals(1, hotspots4.length);
        assertEquals("E63", hotspots4[0].getResidue());
        assertEquals("KRAS", hotspots4[0].getHugoSymbol());
        assertEquals("3d", hotspots4[0].getType());

        Hotspot[] hotspots5 = restTemplate.getForObject(
            baseUrl + genomicLocations[5], Hotspot[].class);

        // not a hotspot, there should me no match
        assertEquals(0, hotspots5.length);

        // genomic location instances (needed for POST requests)
        List<GenomicLocation> genomicLocationsInstances = Arrays.stream(genomicLocations).map(
            g -> this.notationConverter.parseGenomicLocation(g, ",")).collect(Collectors.toList());

        // POST request
        AggregatedHotspots[] aggregatedHotspots = restTemplate.postForObject(
            baseUrl, genomicLocationsInstances, AggregatedHotspots[].class);

        // for each genomic location we should have one matching AggregatedHotspots instance
        assertEquals(genomicLocations.length, aggregatedHotspots.length);

        // GET and POST requests should return exact same hotspots
        assertEquals(aggregatedHotspots[0].getHotspots().size(), hotspots0.length);
        assertEquals(aggregatedHotspots[1].getHotspots().size(), hotspots1.length);
        assertEquals(aggregatedHotspots[2].getHotspots().size(), hotspots2.length);
        assertEquals(aggregatedHotspots[3].getHotspots().size(), hotspots3.length);
        assertEquals(aggregatedHotspots[4].getHotspots().size(), hotspots4.length);
        assertEquals(aggregatedHotspots[5].getHotspots().size(), hotspots5.length);

        // genomic locations should be mapped correctly for the POST request
        assertEquals(this.notationConverter.genomicToHgvs(genomicLocationsInstances.get(0)),
            this.notationConverter.genomicToHgvs(aggregatedHotspots[0].getGenomicLocation()));
        assertEquals(this.notationConverter.genomicToHgvs(genomicLocationsInstances.get(1)),
            this.notationConverter.genomicToHgvs(aggregatedHotspots[1].getGenomicLocation()));
        assertEquals(this.notationConverter.genomicToHgvs(genomicLocationsInstances.get(2)),
            this.notationConverter.genomicToHgvs(aggregatedHotspots[2].getGenomicLocation()));
        assertEquals(this.notationConverter.genomicToHgvs(genomicLocationsInstances.get(3)),
            this.notationConverter.genomicToHgvs(aggregatedHotspots[3].getGenomicLocation()));
        assertEquals(this.notationConverter.genomicToHgvs(genomicLocationsInstances.get(4)),
            this.notationConverter.genomicToHgvs(aggregatedHotspots[4].getGenomicLocation()));
        assertEquals(this.notationConverter.genomicToHgvs(genomicLocationsInstances.get(5)),
            this.notationConverter.genomicToHgvs(aggregatedHotspots[5].getGenomicLocation()));
    }

    @Test
    public void testIndelHotspots()
    {
        String baseUrl = "http://localhost:38888/cancer_hotspots/genomic/";

        // genomic location strings (needed for GET requests)
        String[] genomicLocations = {
            "7,55242466,55242480,GAATTAAGAGAAGCA,-",
            "7,55249002,55249003,-,CAGCGTGGA"
        };

        // GET requests

        Hotspot[] hotspots0 = restTemplate.getForObject(
            baseUrl + genomicLocations[0], Hotspot[].class);

        assertEquals(1, hotspots0.length);
        assertEquals("EGFR", hotspots0[0].getHugoSymbol());
        assertEquals("in-frame indel", hotspots0[0].getType());

        Hotspot[] hotspots1 = restTemplate.getForObject(
            baseUrl + genomicLocations[1], Hotspot[].class);

        assertEquals(1, hotspots1.length);
        assertEquals("EGFR", hotspots1[0].getHugoSymbol());
        assertEquals("in-frame indel", hotspots1[0].getType());

        // genomic location instances (needed for POST requests)
        List<GenomicLocation> genomicLocationsInstances = Arrays.stream(genomicLocations).map(
            g -> this.notationConverter.parseGenomicLocation(g, ",")).collect(Collectors.toList());

        // POST request

        AggregatedHotspots[] aggregatedHotspots = restTemplate.postForObject(
            baseUrl, genomicLocationsInstances, AggregatedHotspots[].class);

        // for each genomic location we should have one matching AggregatedHotspots instance
        assertEquals(genomicLocations.length, aggregatedHotspots.length);

        // GET and POST requests should return exact same hotspots
        assertEquals(aggregatedHotspots[0].getHotspots().size(), hotspots0.length);
        assertEquals(aggregatedHotspots[1].getHotspots().size(), hotspots1.length);

        // genomic locations should be mapped correctly for the POST request
        assertEquals(this.notationConverter.genomicToHgvs(genomicLocationsInstances.get(0)),
            this.notationConverter.genomicToHgvs(aggregatedHotspots[0].getGenomicLocation()));
        assertEquals(this.notationConverter.genomicToHgvs(genomicLocationsInstances.get(1)),
            this.notationConverter.genomicToHgvs(aggregatedHotspots[1].getGenomicLocation()));
    }
}

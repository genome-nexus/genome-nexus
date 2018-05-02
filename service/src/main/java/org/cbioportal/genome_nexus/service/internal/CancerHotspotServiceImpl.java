/*
 * Copyright (c) 2016 Memorial Sloan-Kettering Cancer Center.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF MERCHANTABILITY OR FITNESS
 * FOR A PARTICULAR PURPOSE. The software and documentation provided hereunder
 * is on an "as is" basis, and Memorial Sloan-Kettering Cancer Center has no
 * obligations to provide maintenance, support, updates, enhancements or
 * modifications. In no event shall Memorial Sloan-Kettering Cancer Center be
 * liable to any party for direct, indirect, special, incidental or
 * consequential damages, including lost profits, arising out of the use of this
 * software and its documentation, even if Memorial Sloan-Kettering Cancer
 * Center has been advised of the possibility of such damage.
 */

/*
 * This file is part of cBioPortal Genome Nexus.
 *
 * cBioPortal is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.cbioportal.genome_nexus.service.internal;

import org.cbioportal.genome_nexus.model.*;
import org.cbioportal.genome_nexus.service.CancerHotspotService;
import org.cbioportal.genome_nexus.service.VariantAnnotationService;
import org.cbioportal.genome_nexus.service.annotation.NotationConverter;
import org.cbioportal.genome_nexus.service.exception.CancerHotspotsWebServiceException;
import org.cbioportal.genome_nexus.service.exception.ResourceMappingException;
import org.cbioportal.genome_nexus.service.exception.VariantAnnotationNotFoundException;
import org.cbioportal.genome_nexus.service.exception.VariantAnnotationWebServiceException;
import org.cbioportal.genome_nexus.service.remote.CancerHotspot3dDataFetcher;
import org.cbioportal.genome_nexus.service.remote.CancerHotspotDataFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.*;

/**
 * @author Selcuk Onur Sumer
 */
@Service
public class CancerHotspotServiceImpl implements CancerHotspotService
{
    private HotspotCache cache;

    private final CancerHotspotDataFetcher cancerHotspotDataFetcher;
    private final CancerHotspot3dDataFetcher cancerHotspot3dDataFetcher;
    private final VariantAnnotationService variantAnnotationService;
    private final HotspotFilter hotspotFilter;
    private final NotationConverter notationConverter;

    @Autowired
    public CancerHotspotServiceImpl(CancerHotspotDataFetcher cancerHotspotDataFetcher,
                                    CancerHotspot3dDataFetcher cancerHotspot3dDataFetcher,
                                    VariantAnnotationService variantAnnotationService,
                                    HotspotFilter hotspotFilter,
                                    NotationConverter notationConverter)
    {
        this.cancerHotspotDataFetcher = cancerHotspotDataFetcher;
        this.cancerHotspot3dDataFetcher = cancerHotspot3dDataFetcher;
        this.variantAnnotationService = variantAnnotationService;
        this.hotspotFilter = hotspotFilter;
        this.notationConverter = notationConverter;
    }

    @Override
    public List<Hotspot> getHotspots(String transcriptId) throws CancerHotspotsWebServiceException
    {
        // get it by using the specific API
//        try
//        {
//            return Transformer.mapJsonToInstance(getHotspotsJSON(transcriptId), Hotspot.class);
//        }
//        catch (IOException e)
//        {
//            return Collections.emptyList();
//        }

        //use cache instead
        return getHotspotsFromCache(transcriptId);
    }

    @Override
    public List<Hotspot> getHotspots(TranscriptConsequence transcript,
                                     VariantAnnotation annotation) throws CancerHotspotsWebServiceException
    {
        List<Hotspot> hotspots = new ArrayList<>();

        for (Hotspot hotspot : this.getHotspots(transcript.getTranscriptId()))
        {
            // include only the hotspots matching certain criteria
            if (this.filterHotspot(hotspot, transcript, annotation)) {
                hotspots.add(hotspot);
            }
        }

        return hotspots;
    }

    @Override
    public List<Hotspot> getHotspots() throws CancerHotspotsWebServiceException
    {
        List<Hotspot> hotspots;

        try
        {
            // fetch recurrent hotspots first
            hotspots = this.cancerHotspotDataFetcher.fetchInstances("");

            // then fetch 3D hotspots

            List<Hotspot> hotspots3d = this.cancerHotspot3dDataFetcher.fetchInstances("");

            // TODO ideally, this should have been done in CancerHotspots web service...
            for (Hotspot hotspot: hotspots3d) {
                hotspot.setType("3d");
            }

            hotspots.addAll(hotspots3d);
        }
        catch (ResourceMappingException e)
        {
            throw new CancerHotspotsWebServiceException(e.getMessage());
        }
        catch (HttpClientErrorException e)
        {
            throw new CancerHotspotsWebServiceException(e.getResponseBodyAsString(), e.getStatusCode());
        }
        catch (ResourceAccessException e)
        {
            throw new CancerHotspotsWebServiceException(e.getMessage());
        }

        return hotspots;
    }

    @Override
    public List<Hotspot> getHotspotAnnotationsByVariant(String variant)
        throws VariantAnnotationNotFoundException, VariantAnnotationWebServiceException,
        CancerHotspotsWebServiceException
    {
        VariantAnnotation variantAnnotation = this.variantAnnotationService.getAnnotation(variant);
        List<Hotspot> hotspots = new ArrayList<>();

        if (variantAnnotation != null)
        {
            hotspots.addAll(this.getHotspotAnnotations(variantAnnotation));
        }

        return hotspots;
    }

    @Override
    public List<AggregatedHotspots> getHotspotAnnotationsByVariants(List<String> variants)
        throws CancerHotspotsWebServiceException
    {
        List<VariantAnnotation> variantAnnotations = this.variantAnnotationService.getAnnotations(variants);

        List<AggregatedHotspots> hotspots = new ArrayList<>();

        for (VariantAnnotation variantAnnotation : variantAnnotations)
        {
            AggregatedHotspots aggregatedHotspots = new AggregatedHotspots();
            aggregatedHotspots.setHotspots(this.getHotspotAnnotations(variantAnnotation));
            aggregatedHotspots.setVariant(variantAnnotation.getVariant());

            hotspots.add(aggregatedHotspots);
        }

        return hotspots;
    }

    @Override
    public List<Hotspot> getHotspotAnnotationsByGenomicLocation(String genomicLocation)
        throws VariantAnnotationNotFoundException, VariantAnnotationWebServiceException,
        CancerHotspotsWebServiceException
    {
        GenomicLocation location = this.notationConverter.parseGenomicLocation(genomicLocation);

        return this.getHotspotAnnotationsByVariant(this.notationConverter.genomicToHgvs(location));
    }

    @Override
    public List<AggregatedHotspots> getHotspotAnnotationsByGenomicLocations(List<GenomicLocation> genomicLocations)
        throws CancerHotspotsWebServiceException
    {
        // convert genomic location to hgvs notation (there is always 1-1 mapping)
        Map<String, GenomicLocation> variantToGenomicLocation = notationConverter.genomicToHgvsMap(genomicLocations);

        // query hotspots service by variant
        List<AggregatedHotspots> hotspots = this.getHotspotAnnotationsByVariants(
            new ArrayList<>(variantToGenomicLocation.keySet()));

        // add genomic location info too
        for (AggregatedHotspots aggregatedHotspots: hotspots) {
            aggregatedHotspots.setGenomicLocation(variantToGenomicLocation.get(aggregatedHotspots.getVariant()));
        }

        return hotspots;
    }

    protected Boolean filterHotspot(Hotspot hotspot, TranscriptConsequence transcript, VariantAnnotation annotation)
    {
        return this.hotspotFilter.filter(hotspot, transcript, annotation);
    }

    private List<Hotspot> getHotspotAnnotations(VariantAnnotation variantAnnotation)
        throws CancerHotspotsWebServiceException
    {
        List<Hotspot> hotspots = new ArrayList<>();

        if (variantAnnotation.getTranscriptConsequences() != null)
        {
            for (TranscriptConsequence transcript : variantAnnotation.getTranscriptConsequences())
            {
                hotspots.addAll(getHotspotAnnotations(transcript, variantAnnotation));
            }
        }

        return hotspots;
    }

    private List<Hotspot> getHotspotAnnotations(TranscriptConsequence transcript,
                                                VariantAnnotation annotation)
        throws CancerHotspotsWebServiceException
    {
        // String transcriptId = transcript.getTranscriptId();
        // Hotspot hotspot = hotspotRepository.findOne(transcriptId);

        // hotspotService.setHotspotsURL("http://cancerhotspots.org/api/hotspots/single/");
        // get the hotspot(s) from the web service
        List<Hotspot> hotspots = this.getHotspots(transcript, annotation);

        // do not cache anything for now
        // hotspotRepository.save(hotspots);

        return hotspots;
    }

    private List<Hotspot> getHotspotsFromCache(String transcriptId) throws CancerHotspotsWebServiceException
    {
        // if null: not initialized yet
        if (cache == null)
        {
            List<Hotspot> hotspots = this.getHotspots();

            if (hotspots.size() > 0)
            {
                this.cache = new HotspotCache(hotspots);
            }
        }

        List<Hotspot> hotspots = null;

        // still null: error at initialization
        if (cache != null) {
            hotspots = cache.findByTranscriptId(transcriptId);
        }

        return hotspots;
    }
}

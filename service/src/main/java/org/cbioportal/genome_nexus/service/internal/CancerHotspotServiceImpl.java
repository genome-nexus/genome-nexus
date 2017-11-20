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

import org.cbioportal.genome_nexus.model.Hotspot;
import org.cbioportal.genome_nexus.model.TranscriptConsequence;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.CancerHotspotService;
import org.cbioportal.genome_nexus.service.VariantAnnotationService;
import org.cbioportal.genome_nexus.service.exception.CancerHotspotsWebServiceException;
import org.cbioportal.genome_nexus.service.exception.ResourceMappingException;
import org.cbioportal.genome_nexus.service.exception.VariantAnnotationNotFoundException;
import org.cbioportal.genome_nexus.service.exception.VariantAnnotationWebServiceException;
import org.cbioportal.genome_nexus.service.remote.CancerHotspotDataFetcher;
import org.cbioportal.genome_nexus.util.Numerical;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Selcuk Onur Sumer
 */
@Service
public class CancerHotspotServiceImpl implements CancerHotspotService
{
    private HotspotCache cache;

    private final CancerHotspotDataFetcher externalResourceFetcher;
    private final VariantAnnotationService variantAnnotationService;

    @Autowired
    public CancerHotspotServiceImpl(CancerHotspotDataFetcher externalResourceFetcher,
                                    VariantAnnotationService variantAnnotationService)
    {
        this.externalResourceFetcher = externalResourceFetcher;
        this.variantAnnotationService = variantAnnotationService;
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
//            e.printStackTrace();
//            return Collections.emptyList();
//        }

        //use cache instead
        return getHotspotsFromCache(transcriptId);
    }

    @Override
    public List<Hotspot> getHotspots(TranscriptConsequence transcript) throws CancerHotspotsWebServiceException
    {
        List<Hotspot> hotspots = new ArrayList<>();

        for (Hotspot hotspot : this.getHotspots(transcript.getTranscriptId()))
        {
            // only include hotspots overlapping the protein change position
            // of the current transcript
            if (Numerical.overlaps(hotspot.getResidue(),
                                   transcript.getProteinStart(),
                                   transcript.getProteinEnd()))
            {
                // TODO use a JSON view instead of copying fields to another model?
                // we have data duplication here...
                hotspot.setGeneId(transcript.getGeneId());
                hotspot.setProteinStart(transcript.getProteinStart());
                hotspot.setProteinEnd(transcript.getProteinEnd());

                hotspots.add(hotspot);
            }
        }

        return hotspots;
    }

    @Override
    public List<Hotspot> getHotspots() throws CancerHotspotsWebServiceException
    {
        try
        {
            return this.externalResourceFetcher.fetchInstances("");
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
    }

    @Override
    public List<Hotspot> getHotspotAnnotations(String variant)
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
    public List<Hotspot> getHotspotAnnotations(List<String> variants)
        throws CancerHotspotsWebServiceException
    {
        List<VariantAnnotation> variantAnnotations = this.variantAnnotationService.getAnnotations(variants);

        List<Hotspot> hotspots = new ArrayList<>();

        for (VariantAnnotation variantAnnotation : variantAnnotations)
        {
            hotspots.addAll(this.getHotspotAnnotations(variantAnnotation));
        }

        return hotspots;
    }

    private List<Hotspot> getHotspotAnnotations(VariantAnnotation variantAnnotation)
        throws CancerHotspotsWebServiceException
    {
        List<Hotspot> hotspots = new ArrayList<>();

        if (variantAnnotation.getTranscriptConsequences() != null)
        {
            for (TranscriptConsequence transcript : variantAnnotation.getTranscriptConsequences())
            {
                hotspots.addAll(getHotspotAnnotations(transcript));
            }
        }

        return hotspots;
    }

    private List<Hotspot> getHotspotAnnotations(TranscriptConsequence transcript)
        throws CancerHotspotsWebServiceException
    {
        // String transcriptId = transcript.getTranscriptId();
        // Hotspot hotspot = hotspotRepository.findOne(transcriptId);

        // hotspotService.setHotspotsURL("http://cancerhotspots.org/api/hotspots/single/");
        // get the hotspot(s) from the web service
        List<Hotspot> hotspots = this.getHotspots(transcript);

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

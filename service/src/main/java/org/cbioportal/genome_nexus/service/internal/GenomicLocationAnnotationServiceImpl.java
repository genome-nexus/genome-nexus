/*
 * Copyright (c) 2015 Memorial Sloan-Kettering Cancer Center.
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
 * This file is part of cBioPortal.
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cbioportal.genome_nexus.model.*;
import org.cbioportal.genome_nexus.service.*;

import org.cbioportal.genome_nexus.component.annotation.NotationConverter;
import org.cbioportal.genome_nexus.service.cached.CachedVariantRegionAnnotationFetcher;
import org.cbioportal.genome_nexus.service.exception.VariantAnnotationNotFoundException;
import org.cbioportal.genome_nexus.service.exception.VariantAnnotationWebServiceException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.*;

import java.util.List;

@Service
public class GenomicLocationAnnotationServiceImpl implements GenomicLocationAnnotationService
{
    private static final Log LOG = LogFactory.getLog(GenomicLocationAnnotationService.class);

    private final Boolean isRegionAnnotationEnabled;
    private final NotationConverter notationConverter;
    private final VariantAnnotationService variantAnnotationService;
    private final GenomicLocationStringToVariantFormat genomicLocationToVariantFormat;
    private final GenomicLocationsToVariantFormats genomicLocationsToVariantFormats;

    @Autowired
    public GenomicLocationAnnotationServiceImpl(CachedVariantRegionAnnotationFetcher cachedVariantRegionAnnotationFetcher,
                                                NotationConverter notationConverter,
                                                // Lazy autowire services used for enrichment,
                                                // otherwise we are getting circular dependency issues
                                                @Lazy VariantAnnotationService hgvsVariantAnnotationService,
                                                @Lazy VariantAnnotationService regionVariantAnnotationService)

    {
        this.notationConverter = notationConverter;
        this.isRegionAnnotationEnabled = cachedVariantRegionAnnotationFetcher.hasValidURI();
        if (this.isRegionAnnotationEnabled) {
            this.variantAnnotationService = regionVariantAnnotationService;
            this.genomicLocationToVariantFormat = notationConverter::genomicToEnsemblRestRegion;
            this.genomicLocationsToVariantFormats = notationConverter::genomicToEnsemblRestRegion;
        } else {
            this.variantAnnotationService = hgvsVariantAnnotationService;
            this.genomicLocationToVariantFormat = notationConverter::genomicToHgvs;
            this.genomicLocationsToVariantFormats = notationConverter::genomicToHgvs;

        }
    }

    @Override
    public VariantAnnotation getAnnotation(GenomicLocation genomicLocation)
        throws VariantAnnotationNotFoundException, VariantAnnotationWebServiceException
    {
        return this.variantAnnotationService.getAnnotation(this.notationConverter.genomicToEnsemblRestRegion(genomicLocation));
    }

    @Override
    public VariantAnnotation getAnnotation(String genomicLocation)
        throws VariantAnnotationNotFoundException, VariantAnnotationWebServiceException
    {
        return this.getAnnotation(this.notationConverter.parseGenomicLocation(genomicLocation));
    }

    @Override
    public List<VariantAnnotation> getAnnotations(List<GenomicLocation> genomicLocations)
    {
        return this.variantAnnotationService.getAnnotations(this.notationConverter.genomicToEnsemblRestRegion(genomicLocations));
    }

    @Override
    public VariantAnnotation getAnnotation(String genomicLocation,
                                           String isoformOverrideSource,
                                           List<String> fields)
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException
    {
        return this.variantAnnotationService.getAnnotation(
            this.genomicLocationToVariantFormat.convert(genomicLocation),
            isoformOverrideSource,
            fields
        );
    }

    @Override
    public List<VariantAnnotation> getAnnotations(List<GenomicLocation> genomicLocations,
                                                                    String isoformOverrideSource,
                                                                    List<String> fields)
    {
        return this.variantAnnotationService.getAnnotations(
            this.genomicLocationsToVariantFormats.convert(genomicLocations),
            isoformOverrideSource,
            fields
        );
    }

    @FunctionalInterface
    private static interface GenomicLocationStringToVariantFormat {
        String convert(String genomicLocation);
    }

    @FunctionalInterface
    private static interface GenomicLocationsToVariantFormats {
        List<String> convert(List<GenomicLocation> genomcLocation);
    }
}

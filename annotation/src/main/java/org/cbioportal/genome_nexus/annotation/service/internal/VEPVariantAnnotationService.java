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

package org.cbioportal.genome_nexus.annotation.service.internal;

import org.cbioportal.genome_nexus.annotation.domain.*;
import org.cbioportal.genome_nexus.annotation.service.*;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.*;

@Service
public class VEPVariantAnnotationService implements VariantAnnotationService
{
    private String vepURL;
    @Value("${vep.url}")
    public void setVEPURL(String vepURL) { this.vepURL = vepURL; }

    private final EnrichmentService enrichmentService;

    /**
     * @author Benjamin Gross
     */
    @Autowired
    public VEPVariantAnnotationService(EnrichmentService enrichmentService)
    {
        this.enrichmentService = enrichmentService;
    }

    public VariantAnnotation getAnnotation(String variant)
    {
        String vepJSON = this.getRawAnnotation(variant);
        VariantAnnotation variantAnnotation = new VariantAnnotation(variant, vepJSON);
        enrichmentService.enrichAnnotation(variantAnnotation);
        return variantAnnotation;
    }

    public String getRawAnnotation(String variant)
    {
        //http://grch37.rest.ensembl.org/vep/human/hgvs/VARIANT?content-type=application/json&xref_refseq=1&ccds=1&canonical=1&domains=1&hgvs=1&numbers=1&protein=1
        String uri = vepURL.replace("VARIANT", variant);
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(uri, String.class);
    }
}

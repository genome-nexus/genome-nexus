/*
 * Copyright (c) 2021 Memorial Sloan-Kettering Cancer Center.
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

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cbioportal.genome_nexus.component.annotation.NotationConverter;
import org.cbioportal.genome_nexus.model.AnnotationField;
import org.cbioportal.genome_nexus.model.GenomicLocation;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.model.VariantType;
import org.cbioportal.genome_nexus.service.exception.VariantAnnotationNotFoundException;
import org.cbioportal.genome_nexus.service.exception.VariantAnnotationWebServiceException;
import org.cbioportal.genome_nexus.util.GenomicLocationUtil;
import org.cbioportal.genome_nexus.util.GenomicVariantUtil;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Service;

@Service
public class VerifiedVariantAnnotationService
{
    private static final Log LOG = LogFactory.getLog(VerifiedVariantAnnotationService.class);
    private final VariantAnnotationService variantAnnotationService;
    private final NotationConverter notationConverter;
    
    @Autowired
    public VerifiedVariantAnnotationService(VariantAnnotationService hgvsVariantAnnotationService, NotationConverter notationConverter)
    {
        this.variantAnnotationService = hgvsVariantAnnotationService;
        this.notationConverter = notationConverter;
    }

    public VariantAnnotation getAnnotation(String variant, VariantType variantType)
            throws VariantAnnotationNotFoundException, VariantAnnotationWebServiceException
    {
        VariantAnnotation annotation = variantAnnotationService.getAnnotation(variant, variantType);
        VariantAnnotation verifiedAnnotation = verifyOrFailAnnotation(
            annotation,
            variantType
        );
        return verifiedAnnotation;
    }

    
    public List<VariantAnnotation> getAnnotations(List<String> variants, VariantType variantType)
    {
        List<VariantAnnotation> annotations = variantAnnotationService.getAnnotations(variants, variantType);
        for (int index = 0; index < annotations.size(); index = index + 1) {
            VariantAnnotation annotation = annotations.get(index);
            VariantAnnotation verifiedAnnotation = verifyOrFailAnnotation(
                annotation,
                variantType
            );
            annotations.set(index, verifiedAnnotation);
        }
        return annotations;
    }

    
    public VariantAnnotation getAnnotation(String variant, VariantType variantType, String isoformOverrideSource, Map<String, String> token, List<AnnotationField> fields)
            throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException
    {
        VariantAnnotation annotation = variantAnnotationService.getAnnotation(variant, variantType, isoformOverrideSource, token, fields);
        VariantAnnotation verifiedAnnotation = verifyOrFailAnnotation(
            annotation,
            variantType
        );
        return verifiedAnnotation;
    }

    
    public List<VariantAnnotation> getAnnotations(List<String> variants, VariantType variantType, String isoformOverrideSource, Map<String, String> token, List<AnnotationField> fields)
    {
        List<VariantAnnotation> annotations = variantAnnotationService.getAnnotations(variants, variantType, isoformOverrideSource, token, fields);
        for (int index = 0; index < annotations.size(); index = index + 1) {
            VariantAnnotation annotation = annotations.get(index);
            VariantAnnotation verifiedAnnotation = verifyOrFailAnnotation(
                annotation,
                variantType
            );
            annotations.set(index, verifiedAnnotation);
        }
        return annotations;
    }

    private VariantAnnotation verifyOrFailAnnotation(VariantAnnotation annotation, VariantType variantType)
    {
        String ref = "";
        if (variantType == VariantType.HGVS) {
            ref = GenomicVariantUtil.providedReferenceAlleleFromHgvs(annotation.getOriginalVariantQuery());
        } else if (variantType == VariantType.GENOMIC_LOCATION) {
            ref = notationConverter.parseGenomicLocation(annotation.getOriginalVariantQuery()).getReferenceAllele();
        }
       
        if (annotation.getStrand() == -1) {
            ref = GenomicLocationUtil.getReverseStrandAllele(ref);
            annotation.setStrand(1);
        }

        if (ref.length() == 0) {
            // no comparison possible : allele not specified in query
            return annotation;
        }
        LOG.debug("verifying providedReferenceAllele : '" + ref + "'");
        String responseReferenceAllele = getReferenceAlleleFromAnnotation(annotation);
        if (responseReferenceAllele.length() != ref.length() ||
         (variantType == VariantType.GENOMIC_LOCATION && responseReferenceAllele.equals("-"))) {
            // for altered length Deletion-Insertion responses, recover full reference allele with followup query
            String followUpVariant = constructFollowUpQuery(annotation.getOriginalVariantQuery(), variantType);

            if (followUpVariant.length() > 0) {
                try {
                    LOG.debug("performing followup annotation request to get VEP genome assembly sequence : '" + ref + "'");
                    VariantAnnotation followUpAnnotation = variantAnnotationService.getAnnotation(followUpVariant, variantType);
                    responseReferenceAllele = getReferenceAlleleFromAnnotation(followUpAnnotation);
                } catch (VariantAnnotationNotFoundException|VariantAnnotationWebServiceException vae) {
                    // followup validation failed - could not verify provided allele, so accept failure
                    LOG.debug("followup annotation request failed - Reference_Allele could not be verified");
                }
            }
        }
        if (ref.equals(responseReferenceAllele)) {
            // validation complete
            return annotation;
        }
        // return annotation failure
        if (annotation.getErrorMessage() == null) {
            annotation.setErrorMessage( String.format("Reference allele extracted from response (%s) does not match given reference allele (%s)", responseReferenceAllele.length() == 0 ? "-" : responseReferenceAllele, ref.length() == 0 ? "-" : ref));
        }
        return createFailedAnnotation(annotation.getOriginalVariantQuery(), annotation.getVariant(), annotation.getErrorMessage());
    }

    private String constructFollowUpQuery(String originalQuery, VariantType variantType)
    {
        String followUpQuery = "";
        if (variantType == VariantType.HGVS) {
            followUpQuery = originalQuery.replaceFirst("ins.*|del.*|dup.*|[A|T|C|G]>[A|T|C|G]","");
            if (followUpQuery.length() == originalQuery.length()) {
                return "";
            }
            followUpQuery += "del";
        } else if (variantType == VariantType.GENOMIC_LOCATION) {
            GenomicLocation genomicLocation = notationConverter.parseGenomicLocation(originalQuery);
            genomicLocation.setVariantAllele("-");
            if (genomicLocation.getReferenceAllele().equals("-")) {
                return "";
            }
            followUpQuery = genomicLocation.toString();
        }
        return followUpQuery;
    }

    private String getReferenceAlleleFromAnnotation(VariantAnnotation annotation)
    {
        String alleleString = annotation.getAlleleString();
        if (alleleString == null) {
            // maybe original annotation attempt failed
            return "";
        }
        int slashPosition = alleleString.indexOf('/');
        if (slashPosition == -1 || slashPosition == 0) {
            return "";
        }

        if (annotation.getOriginalVariantQuery().contains("dup")) {
            return alleleString.substring(slashPosition + 1);
        }
        return alleleString.substring(0,slashPosition);

    }

    private VariantAnnotation createFailedAnnotation(String originalVariantQuery, String originalVariant, String errorMessage)
    {
        VariantAnnotation annotation = new VariantAnnotation();
        if (originalVariantQuery != null && originalVariantQuery.length() > 0) {
            annotation.setOriginalVariantQuery(originalVariantQuery);
        }
        if (originalVariant != null && originalVariant.length() > 0) {
            annotation.setVariant(originalVariant);
        }
        annotation.setSuccessfullyAnnotated(false);
        annotation.setErrorMessage(errorMessage);
        return annotation;
    }
}

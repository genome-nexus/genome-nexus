package org.cbioportal.genome_nexus.service.internal;

import org.cbioportal.genome_nexus.model.CuriousCases;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.CuriousCasesService;
import org.cbioportal.genome_nexus.service.GenomicLocationAnnotationService;
import org.cbioportal.genome_nexus.service.exception.CuriousCasesNotFoundException;
import org.cbioportal.genome_nexus.service.exception.CuriousCasesWebServiceException;
import org.cbioportal.genome_nexus.service.exception.VariantAnnotationNotFoundException;
import org.cbioportal.genome_nexus.service.exception.VariantAnnotationWebServiceException;
import org.cbioportal.genome_nexus.util.GenomicLocationUtil;
import org.cbioportal.genome_nexus.util.GenomicLocationUtil.MutationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.*;

/**
 * @author Xiang Li
 */
@Service
public class CuriousCasesServiceImpl implements CuriousCasesService {
    private final GenomicLocationAnnotationService genomicLocationAnnotationService;

    @Autowired
    public CuriousCasesServiceImpl(GenomicLocationAnnotationService genomicLocationAnnotationService) {
        this.genomicLocationAnnotationService = genomicLocationAnnotationService;
    }

    @Override
    public CuriousCases getCuriousCases(String genomicLocation)
            throws CuriousCasesNotFoundException, CuriousCasesWebServiceException {
        CuriousCases CuriousCases = null;

        try {
            VariantAnnotation annotation = genomicLocationAnnotationService.getAnnotation(genomicLocation, null, null, Arrays.asList("annotation_summary"));
            CuriousCases = generateCuriousCases(annotation, genomicLocation);
            if (CuriousCases != null) {
                CuriousCases.setGenomicLocation(genomicLocation);
                return CuriousCases;
            } else {
                throw new CuriousCasesNotFoundException(genomicLocation);
            }
        } catch (HttpClientErrorException e) {
            // in case of web service error, throw an exception to indicate that there is a
            // problem with the service.
            throw new CuriousCasesWebServiceException(genomicLocation, e.getResponseBodyAsString(), e.getStatusCode());
        } catch (ResourceAccessException e) {
            throw new CuriousCasesWebServiceException(genomicLocation, e.getMessage());
        } catch (VariantAnnotationNotFoundException e) {
            throw new CuriousCasesNotFoundException(genomicLocation);
        } catch (VariantAnnotationWebServiceException e) {
            throw new CuriousCasesNotFoundException(genomicLocation);
        }
    }

    private CuriousCases generateCuriousCases(VariantAnnotation annotation, String genomicLocation) {
        CuriousCases CuriousCases = null;
        if (annotation != null) {
            String comment = null;
            List<Integer> pubmedIds = null;
            String hugoGeneSymbol = annotation.getAnnotationSummary().getTranscriptConsequenceSummary().getHugoGeneSymbol();
            String chr = annotation.getSeqRegionName();
            int start = annotation.getStart();
            int end = annotation.getEnd();
            MutationType variantType = GenomicLocationUtil.getTypeFromGenomicLocation(genomicLocation);

            if (chr.equals("4") && start >= 55593576 && end <= 55595381 && variantType.equals(MutationType.DEL)) {
                comment = "Potential in-frame deletion event at intron10-exon 11 boundary, commmon in Gastrointestinal Stromal Tumors";
                pubmedIds = new ArrayList<>(Arrays.asList(15507676, 27600498, 32697050));
            }
            else if (chr.equals("5") && start == 112151184 && end == 112151184 && variantType.equals(MutationType.SNP)) {
                comment = "Potential introduction of donor splice site, common in colorectal cancers";
                pubmedIds = new ArrayList<>(Arrays.asList(29316426));
            }
            else if (chr.equals("3") && start >= 41265579 && end <= 41266246 && variantType.equals(MutationType.DEL)) {
                comment = "Potential Exon Skip Event common in Colorectal Cancers";
                pubmedIds = new ArrayList<>(Arrays.asList(15507676, 27600498, 3269705));
            }
            else if (chr.equals("7") && variantType.equals(MutationType.SNP) && (start == 116411901 && end == 116411901 ||
                start == 116411902 && end == 116411902 ||
                start == 116412044 && end == 116412044 ||
                start == 116412045 && end == 116412045)) {
                comment = "Potential Exon Skip/In-frame Insertion common in ------";
            }
            else if (chr.equals("7") && start >= 116411901 && end <= 116411902 && variantType.equals(MutationType.DEL)) {
                comment = "Potential Exon Skip Event common in Colorectal Cancers";
            }
            else if (chr.equals("7") && start >= 116412044 && end <= 116412045 && variantType.equals(MutationType.DEL)) {
                comment = "Potential Exon Skip Event common in Colorectal Cancers";
            }
            
            if (comment != null || pubmedIds != null) {
                CuriousCases = new CuriousCases();
                CuriousCases.setHugoGeneSymbol(hugoGeneSymbol);
                if (comment != null) {
                    CuriousCases.setComment(comment);
                }
                if (pubmedIds != null) {
                    CuriousCases.setPubmedIds(pubmedIds);
                }
            }

        }
        return CuriousCases;
    }
}

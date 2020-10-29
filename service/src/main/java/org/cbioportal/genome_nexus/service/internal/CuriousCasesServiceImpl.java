package org.cbioportal.genome_nexus.service.internal;

import org.cbioportal.genome_nexus.model.CuriousCasesComment;
import org.cbioportal.genome_nexus.service.CuriousCasesService;
import org.cbioportal.genome_nexus.service.exception.CuriousCasesCommentNotFoundException;
import org.cbioportal.genome_nexus.service.exception.CuriousCasesCommentWebServiceException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.*;

/**
 * @author Selcuk Onur Sumer
 */
@Service
public class CuriousCasesServiceImpl implements CuriousCasesService
{
    @Override
    public CuriousCasesComment getCuriousCasesComment(String region) throws CuriousCasesCommentNotFoundException, CuriousCasesCommentWebServiceException
    {
        CuriousCasesComment curiousCasesComment = null;

        try {
            curiousCasesComment = generateCuriousCasesComment(region);
            if (curiousCasesComment != null) {
                curiousCasesComment.setRegion(region);
                return curiousCasesComment;
            } else {
                throw new CuriousCasesCommentNotFoundException(region);
            }
        }
        catch (HttpClientErrorException e) {
            // in case of web service error, throw an exception to indicate that there is a problem with the service.
            throw new CuriousCasesCommentWebServiceException(region, e.getResponseBodyAsString(), e.getStatusCode());
        }
        catch (ResourceAccessException e) {
            throw new CuriousCasesCommentWebServiceException(region, e.getMessage());
        }
    }

    private CuriousCasesComment generateCuriousCasesComment(String region) {
        CuriousCasesComment curiousCasesComment = null;
        if (region != null) {
            if (region.contains(":")) {
                String[] splited = region.split(":");
                String chr = splited[0];
                String range = splited[1];
                if (range.contains("-")) {
                    String[] splitedRange = range.split("-");
                    int start = Integer.parseInt(splitedRange[0]);
                    int end = Integer.parseInt(splitedRange[1]);
                    String comment = null;
                    String pubmedIds = null;

                    if (chr.equals("4") && start >= 55595380 && end <= 55595381) {
                        comment = "Potential in-frame deletion event at intron10-exon 11 boundary, commmon in Gastrointestinal Stromal Tumors";
                        pubmedIds = "c(15507676,27600498, 32697050)";
                    }
                    if (chr.equals("5") && start == 112151184 && end == 112151184) {
                        comment = "Potential introduction of donor splice site, common in colorectal cancers";
                        pubmedIds = "c(29316426)";
                    }
                    if (chr.equals("3") && start >= 41266015 && end <= 41266246) {
                        comment = "Potential Exon Skip Event common in Colorectal Cancers";
                        pubmedIds = "c(15507676, 27600498, 3269705)";
                    }
                    if (chr.equals("7") && (start == 116411901 && end == 116411901 ||
                        start == 116411902 && end == 116411902 ||
                        start == 116412044 && end == 116412044 ||
                        start == 116412045 && end == 116412045)) {
                        comment = "Potential Exon Skip/In-frame Insertion common in ------";
                    }
                    if (chr.equals("7") && start >= 116411901 && end <= 116411902) {
                        comment = "Potential Exon Skip Event common in Colorectal Cancers";
                    }
                    if (chr.equals("7") && start >= 116412044 && end <= 116412045) {
                        comment = "Potential Exon Skip Event common in Colorectal Cancers";
                    }

                    if (comment != null || pubmedIds != null) {
                        curiousCasesComment = new CuriousCasesComment();
                        if (comment != null) {
                            curiousCasesComment.setComment(comment);
                        }
                        if (pubmedIds != null) {
                            curiousCasesComment.setPubmedIds(pubmedIds);
                        }
                    }
                }
            }

        }
        return curiousCasesComment;
    }
}

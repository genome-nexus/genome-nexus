package org.cbioportal.genome_nexus.service;

import org.cbioportal.genome_nexus.model.CuriousCasesComment;
import org.cbioportal.genome_nexus.service.exception.CuriousCasesCommentNotFoundException;
import org.cbioportal.genome_nexus.service.exception.CuriousCasesCommentWebServiceException;

public interface CuriousCasesService
{
    CuriousCasesComment getCuriousCasesComment(String region) throws CuriousCasesCommentNotFoundException, CuriousCasesCommentWebServiceException;
}

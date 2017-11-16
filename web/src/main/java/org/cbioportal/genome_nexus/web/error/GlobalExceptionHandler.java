package org.cbioportal.genome_nexus.web.error;

import org.cbioportal.genome_nexus.service.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice("org.cbioportal.genome_nexus.web")
public class GlobalExceptionHandler
{
    @ExceptionHandler(PdbHeaderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleStudyNotFound(PdbHeaderNotFoundException ex)
    {
        return new ResponseEntity<>(
            new ErrorResponse("PDB header not found: " + ex.getPdbId()),
            HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PdbHeaderWebServiceException.class)
    public ResponseEntity<ErrorResponse> handleCancerHotspotsWebServiceException(PdbHeaderWebServiceException ex)
    {
        return new ResponseEntity<>(
            new ErrorResponse("PDB web service error for id " + ex.getPdbId() + ": " + ex.getResponseBody()),
            ex.getStatusCode());
    }

    @ExceptionHandler(PfamDomainNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePfamDomainNotFound(PfamDomainNotFoundException ex)
    {
        return new ResponseEntity<>(
            new ErrorResponse("PFAM domain not found: " + ex.getPfamAccession()),
            HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(VariantAnnotationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleVariantAnnotationNotFound(VariantAnnotationNotFoundException ex)
    {
        return new ResponseEntity<>(
            new ErrorResponse("Variant annotation not found: " + ex.getVariant() + ": " + ex.getResponse()),
            HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(VariantAnnotationWebServiceException.class)
    public ResponseEntity<ErrorResponse> handleCancerHotspotsWebServiceException(VariantAnnotationWebServiceException ex)
    {
        return new ResponseEntity<>(
            new ErrorResponse("VEP web service error for variant " + ex.getVariant() + ": " + ex.getResponseBody()),
            ex.getStatusCode());
    }

    @ExceptionHandler(EnsemblTranscriptNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePfamDomainNotFound(EnsemblTranscriptNotFoundException ex)
    {
        return new ResponseEntity<>(
            new ErrorResponse("Ensembl transcript not found: " + ex.getId()),
            HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CancerHotspotsWebServiceException.class)
    public ResponseEntity<ErrorResponse> handleCancerHotspotsWebServiceException(CancerHotspotsWebServiceException ex)
    {
        return new ResponseEntity<>(
            new ErrorResponse(ex.getResponseBody()),
            ex.getStatusCode());
    }

    @ExceptionHandler(MutationAssessorNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleMutationAssessorNotFound(MutationAssessorNotFoundException ex)
    {
        return new ResponseEntity<>(
            new ErrorResponse("Mutation Assessor annotation not found for variant: " + ex.getVariant()),
            HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MutationAssessorWebServiceException.class)
    public ResponseEntity<ErrorResponse> handleMutationAssessorWebServiceException(MutationAssessorWebServiceException ex)
    {
        return new ResponseEntity<>(
            new ErrorResponse(ex.getResponseBody()),
            ex.getStatusCode());
    }

    @ExceptionHandler(EnsemblWebServiceException.class)
    public ResponseEntity<ErrorResponse> handleEnsemblWebServiceException(EnsemblWebServiceException ex)
    {
        return new ResponseEntity<>(
            new ErrorResponse(ex.getResponseBody()),
            ex.getStatusCode());
    }

    @ExceptionHandler(IsoformOverrideNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleIsoformOverrideNotFound(IsoformOverrideNotFoundException ex)
    {
        String message = "Isoform override not found: source: " + ex.getSource();

        if (ex.getTranscriptId() != null) {
            message += ", transcript id: " + ex.getTranscriptId();
        }

        return new ResponseEntity<>(new ErrorResponse(message), HttpStatus.NOT_FOUND);
    }
}

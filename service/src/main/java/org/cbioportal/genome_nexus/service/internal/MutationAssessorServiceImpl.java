package org.cbioportal.genome_nexus.service.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cbioportal.genome_nexus.component.annotation.ProteinChangeResolver;
import org.cbioportal.genome_nexus.model.MutationAssessor;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.persistence.MutationAssessorRepository;
import org.cbioportal.genome_nexus.service.EnsemblService;
import org.cbioportal.genome_nexus.service.MutationAssessorService;
import org.cbioportal.genome_nexus.service.VariantAnnotationService;
import org.cbioportal.genome_nexus.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MutationAssessorServiceImpl implements MutationAssessorService
{
    private static final Log LOG = LogFactory.getLog(MutationAssessorServiceImpl.class);

    private final MutationAssessorRepository mutationAssessorRepository;
    private final VariantAnnotationService variantAnnotationService;
    private final ProteinChangeResolver proteinChangeResolver;
    private final EnsemblService ensemblService;

    @Autowired
    public MutationAssessorServiceImpl(MutationAssessorRepository mutationAssessorRepository,
                                       VariantAnnotationService verifiedHgvsVariantAnnotationService,
                                       ProteinChangeResolver proteinChangeResolver,
                                       EnsemblService ensemblService)
    {
        this.mutationAssessorRepository = mutationAssessorRepository;
        this.variantAnnotationService = verifiedHgvsVariantAnnotationService;
        this.proteinChangeResolver = proteinChangeResolver;
        this.ensemblService = ensemblService;
    }

    /**
     * @param variant   hgvs variant (ex: 7:g.140453136A>T)
     */
    public MutationAssessor getMutationAssessor(String variant)
        throws VariantAnnotationNotFoundException, VariantAnnotationWebServiceException,
        MutationAssessorNotFoundException
    {
        VariantAnnotation annotation = this.variantAnnotationService.getAnnotation(variant);

        return this.getMutationAssessorByVariantAnnotation(annotation);
    }

    /**
     * @param variants   hgvs variants (ex: 7:g.140453136A>T)
     */
    public List<MutationAssessor> getMutationAssessor(List<String> variants)
    {
        List<MutationAssessor> mutationAssessors = new ArrayList<>();
        List<VariantAnnotation> variantAnnotations = this.variantAnnotationService.getAnnotations(variants);

        for (VariantAnnotation variantAnnotation : variantAnnotations)
        {
            try {
                mutationAssessors.add(this.getMutationAssessorByVariantAnnotation(variantAnnotation));
            } catch (MutationAssessorNotFoundException e) {
                LOG.warn(e.getLocalizedMessage());
            }
        }

        return mutationAssessors;
    }

    public MutationAssessor getMutationAssessor(VariantAnnotation annotation)
        throws MutationAssessorNotFoundException
    {
        // checks annotation is SNP and has transcript consequences
        if (annotation.getStart() == null
            || !annotation.getStart().equals(annotation.getEnd())
            || !annotation.getAlleleString().matches("[A-Z]/[A-Z]")
            || annotation.getTranscriptConsequences() == null
            || annotation.getTranscriptConsequences().get(0).getTranscriptId() == null)
        {
            throw new MutationAssessorNotFoundException(annotation.getVariant());
        }
        String hgvsp = proteinChangeResolver.resolveHgvspShort(annotation);

        String id = this.ensemblService.getUniprotId(annotation.getTranscriptConsequences().get(0).getTranscriptId()) + "," + hgvsp;
        MutationAssessor mutationAssessor = this.getMutationAssessorByMutationAssessorVariant(id, annotation);

        return mutationAssessor;
    }

    /**
     * @param variant   mutation assessor variant (ex: 7,140453136,A,T)
     */
    public MutationAssessor getMutationAssessorByMutationAssessorVariant(String id, VariantAnnotation annotation)
    throws MutationAssessorNotFoundException
    {
        return Optional.ofNullable(mutationAssessorRepository.findById(id)).get()
                .orElseThrow(() -> new MutationAssessorNotFoundException(annotation.getVariant()));
    }

    public MutationAssessor getMutationAssessorByVariantAnnotation(VariantAnnotation annotation)
        throws MutationAssessorNotFoundException
    {
        try {
            return this.getMutationAssessor(annotation);
        } catch (MutationAssessorNotFoundException e) {
            throw e;
        }
    }
}

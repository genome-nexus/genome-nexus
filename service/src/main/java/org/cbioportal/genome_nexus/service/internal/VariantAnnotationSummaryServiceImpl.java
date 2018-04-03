package org.cbioportal.genome_nexus.service.internal;

import org.cbioportal.genome_nexus.model.TranscriptConsequenceSummary;
import org.cbioportal.genome_nexus.model.VariantAnnotationSummary;
import org.cbioportal.genome_nexus.model.TranscriptConsequence;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.VariantAnnotationService;
import org.cbioportal.genome_nexus.service.VariantAnnotationSummaryService;
import org.cbioportal.genome_nexus.service.annotation.*;
import org.cbioportal.genome_nexus.service.exception.EnsemblWebServiceException;
import org.cbioportal.genome_nexus.service.exception.VariantAnnotationNotFoundException;
import org.cbioportal.genome_nexus.service.exception.VariantAnnotationWebServiceException;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VariantAnnotationSummaryServiceImpl implements VariantAnnotationSummaryService
{
    private final VariantAnnotationService variantAnnotationService;
    private final CanonicalTranscriptResolver canonicalTranscriptResolver;
    private final CodonChangeResolver codonChangeResolver;
    private final ConsequenceTermsResolver consequenceTermsResolver;
    private final EntrezGeneIdResolver entrezGeneIdResolver;
    private final GenomicLocationResolver genomicLocationResolver;
    private final HugoGeneSymbolResolver hugoGeneSymbolResolver;
    private final ProteinChangeResolver proteinChangeResolver;
    private final ProteinPositionResolver proteinPositionResolver;
    private final RefSeqResolver refSeqResolver;
    private final StrandSignResolver strandSignResolver;
    private final TranscriptIdResolver transcriptIdResolver;
    private final VariantClassificationResolver variantClassificationResolver;
    private final VariantTypeResolver variantTypeResolver;

    @Autowired
    public VariantAnnotationSummaryServiceImpl(VariantAnnotationService variantAnnotationService,
                                               CanonicalTranscriptResolver canonicalTranscriptResolver,
                                               CodonChangeResolver codonChangeResolver,
                                               ConsequenceTermsResolver consequenceTermsResolver,
                                               EntrezGeneIdResolver entrezGeneIdResolver,
                                               GenomicLocationResolver genomicLocationResolver,
                                               HugoGeneSymbolResolver hugoGeneSymbolResolver,
                                               ProteinChangeResolver proteinChangeResolver,
                                               ProteinPositionResolver proteinPositionResolver,
                                               RefSeqResolver refSeqResolver,
                                               StrandSignResolver strandSignResolver,
                                               TranscriptIdResolver transcriptIdResolver,
                                               VariantClassificationResolver variantClassificationResolver,
                                               VariantTypeResolver variantTypeResolver)
    {
        this.variantAnnotationService = variantAnnotationService;
        this.canonicalTranscriptResolver = canonicalTranscriptResolver;
        this.codonChangeResolver = codonChangeResolver;
        this.consequenceTermsResolver = consequenceTermsResolver;
        this.entrezGeneIdResolver = entrezGeneIdResolver;
        this.genomicLocationResolver = genomicLocationResolver;
        this.hugoGeneSymbolResolver = hugoGeneSymbolResolver;
        this.proteinChangeResolver = proteinChangeResolver;
        this.proteinPositionResolver = proteinPositionResolver;
        this.refSeqResolver = refSeqResolver;
        this.strandSignResolver = strandSignResolver;
        this.transcriptIdResolver = transcriptIdResolver;
        this.variantClassificationResolver = variantClassificationResolver;
        this.variantTypeResolver = variantTypeResolver;
    }

    @Override
    public VariantAnnotationSummary getAnnotationSummaryForCanonical(String variant, String isoformOverrideSource)
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException
    {
        return this.getAnnotationSummaryForCanonical(
            this.variantAnnotationService.getAnnotation(variant, isoformOverrideSource, null));
    }

    @Override
    public VariantAnnotationSummary getAnnotationSummaryForCanonical(VariantAnnotation annotation)
    {
        VariantAnnotationSummary annotationSummary = this.getVariantAnnotationSummary(annotation);
        TranscriptConsequence canonicalTranscript = this.canonicalTranscriptResolver.resolve(annotation);

        if (annotationSummary != null && canonicalTranscript != null)
        {
            List<TranscriptConsequenceSummary> summaries = new ArrayList<>(1);
            summaries.add(this.getTranscriptSummary(annotation, canonicalTranscript));
            annotationSummary.setTranscriptConsequences(summaries);
            annotationSummary.setCanonicalTranscriptId(canonicalTranscript.getTranscriptId());
        }

        return annotationSummary;
    }

    @Override
    public VariantAnnotationSummary getAnnotationSummary(String variant, String isoformOverrideSource)
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException
    {
        return this.getAnnotationSummary(
            this.variantAnnotationService.getAnnotation(variant, isoformOverrideSource, null));
    }

    @Override
    public List<VariantAnnotationSummary> getAnnotationSummary(List<String> variants, String isoformOverrideSource) {
        List<VariantAnnotationSummary> summaries = new ArrayList<>();

        List<VariantAnnotation> annotations =
            this.variantAnnotationService.getAnnotations(variants, isoformOverrideSource, null);

        for (VariantAnnotation annotation: annotations) {
            summaries.add(this.getAnnotationSummary(annotation));
        }

        return summaries;
    }

    @Override
    public List<VariantAnnotationSummary> getAnnotationSummaryForCanonical(List<String> variants, String isoformOverrideSource) {
        List<VariantAnnotationSummary> summaries = new ArrayList<>();

        List<VariantAnnotation> annotations =
            this.variantAnnotationService.getAnnotations(variants, isoformOverrideSource, null);

        for (VariantAnnotation annotation: annotations) {
            summaries.add(this.getAnnotationSummaryForCanonical(annotation));
        }

        return summaries;
    }

    @Override
    public VariantAnnotationSummary getAnnotationSummary(VariantAnnotation annotation)
    {
        VariantAnnotationSummary annotationSummary = this.getAnnotationSummaryForCanonical(annotation);

        if (annotationSummary != null)
        {
            List<TranscriptConsequenceSummary> summaries = new ArrayList<>();

            for (TranscriptConsequence transcriptConsequence: annotation.getTranscriptConsequences()) {
                summaries.add(this.getTranscriptSummary(annotation, transcriptConsequence));
            }

            annotationSummary.setTranscriptConsequences(summaries);
        }

        return annotationSummary;
    }

    @Nullable
    private String resolveEntrezGeneId(TranscriptConsequence canonicalTranscript)
    {
        String entrezGeneId;

        try {
            entrezGeneId = this.entrezGeneIdResolver.resolve(canonicalTranscript);
        } catch (EnsemblWebServiceException e) {
            entrezGeneId = null;
        }

        return entrezGeneId;
    }

    @Nullable
    private VariantAnnotationSummary getVariantAnnotationSummary(VariantAnnotation annotation)
    {
        VariantAnnotationSummary summary = null;

        if (annotation != null)
        {
            summary = new VariantAnnotationSummary();

            summary.setVariant(annotation.getVariant());
            summary.setGenomicLocation(this.genomicLocationResolver.resolve(annotation));
            summary.setStrandSign(this.strandSignResolver.resolve(annotation));
            summary.setVariantType(this.variantTypeResolver.resolve(annotation));
            summary.setAssemblyName(annotation.getAssemblyName());
        }

        return summary;
    }

    @Nullable
    private TranscriptConsequenceSummary getTranscriptSummary(VariantAnnotation annotation,
                                                              TranscriptConsequence transcriptConsequence)
    {
        TranscriptConsequenceSummary summary = null;

        if (transcriptConsequence != null)
        {
            summary = new TranscriptConsequenceSummary();

            summary.setTranscriptId(this.transcriptIdResolver.resolve(transcriptConsequence));
            summary.setCodonChange(this.codonChangeResolver.resolve(transcriptConsequence));
            summary.setEntrezGeneId(this.resolveEntrezGeneId(transcriptConsequence));
            summary.setConsequenceTerms(this.consequenceTermsResolver.resolve(transcriptConsequence));
            summary.setHugoGeneSymbol(this.hugoGeneSymbolResolver.resolve(transcriptConsequence));
            summary.setHgvspShort(this.proteinChangeResolver.resolveHgvspShort(annotation, transcriptConsequence));
            summary.setHgvsp(this.proteinChangeResolver.resolveHgvsp(transcriptConsequence));
            summary.setHgvsc(this.proteinChangeResolver.resolveHgvsc(transcriptConsequence));
            summary.setProteinPosition(this.proteinPositionResolver.resolve(annotation, transcriptConsequence));
            summary.setRefSeq(this.refSeqResolver.resolve(transcriptConsequence));
            summary.setVariantClassification(this.variantClassificationResolver.resolve(annotation, transcriptConsequence));
        }

        return summary;
    }
}

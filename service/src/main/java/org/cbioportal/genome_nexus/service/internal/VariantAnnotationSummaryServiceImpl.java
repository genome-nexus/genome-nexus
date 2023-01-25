package org.cbioportal.genome_nexus.service.internal;

import org.cbioportal.genome_nexus.component.annotation.*;
import org.cbioportal.genome_nexus.model.TranscriptConsequenceSummary;
import org.cbioportal.genome_nexus.model.VariantAnnotationSummary;
import org.cbioportal.genome_nexus.model.EnsemblTranscript;
import org.cbioportal.genome_nexus.model.RevisedProteinEffect;
import org.cbioportal.genome_nexus.model.TranscriptConsequence;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.EnsemblService;
import org.cbioportal.genome_nexus.service.VariantAnnotationService;
import org.cbioportal.genome_nexus.service.VariantAnnotationSummaryService;
import org.cbioportal.genome_nexus.service.annotation.EntrezGeneIdResolver;
import org.cbioportal.genome_nexus.service.exception.EnsemblTranscriptNotFoundException;
import org.cbioportal.genome_nexus.service.exception.EnsemblWebServiceException;
import org.cbioportal.genome_nexus.service.exception.VariantAnnotationNotFoundException;
import org.cbioportal.genome_nexus.service.exception.VariantAnnotationWebServiceException;
import org.cbioportal.genome_nexus.util.IsoformOverrideSource;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.Objects;

import org.cbioportal.genome_nexus.util.jsonReader;
import org.cbioportal.genome_nexus.model.VUEs;

@Service
public class VariantAnnotationSummaryServiceImpl implements VariantAnnotationSummaryService
{
    private final VariantAnnotationService variantAnnotationService;
    private final EnsemblService ensemblService;
    private final CanonicalTranscriptResolver canonicalTranscriptResolver;
    private final CodonChangeResolver codonChangeResolver;
    private final AminoAcidsResolver aminoAcidsResolver;
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
    private final ExonResolver exonResolver;
    private final VUEs[] vuesList;

    @Autowired
    public VariantAnnotationSummaryServiceImpl(
        VariantAnnotationService verifiedHgvsVariantAnnotationService,
        EnsemblService ensemblService,
        CanonicalTranscriptResolver canonicalTranscriptResolver,
        AminoAcidsResolver aminoAcidsResolver,
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
        VariantTypeResolver variantTypeResolver,
        ExonResolver exonResolver
    ) {
        this.variantAnnotationService = verifiedHgvsVariantAnnotationService;
        this.ensemblService = ensemblService;
        this.canonicalTranscriptResolver = canonicalTranscriptResolver;
        this.codonChangeResolver = codonChangeResolver;
        this.aminoAcidsResolver = aminoAcidsResolver;
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
        this.exonResolver = exonResolver;
        this.vuesList = jsonReader.getVuesList();
    }

    @Override
    public VariantAnnotationSummary getAnnotationSummaryForCanonical(String variant, String isoformOverrideSource)
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException
    {
        return this.getAnnotationSummaryForCanonical(
            this.variantAnnotationService.getAnnotation(variant, isoformOverrideSource, null, null)
        );
    }

    @Override
    public VariantAnnotationSummary getAnnotationSummaryForCanonical(VariantAnnotation annotation)
    {
        VariantAnnotationSummary annotationSummary = this.getVariantAnnotationSummary(annotation);
        TranscriptConsequence canonicalTranscript = this.canonicalTranscriptResolver.resolve(annotation);

        if (annotationSummary != null && canonicalTranscript != null)
        {
            annotationSummary.setTranscriptConsequenceSummary(this.getTranscriptSummary(annotation, canonicalTranscript));
            annotationSummary.setCanonicalTranscriptId(canonicalTranscript.getTranscriptId());

            // for backwards compatibility set transcriptConsequences
            List<TranscriptConsequenceSummary> transcriptConsequences = new ArrayList<>(1);
            transcriptConsequences.add(annotationSummary.getTranscriptConsequenceSummary());
            annotationSummary.setTranscriptConsequences(transcriptConsequences);
        }

        return annotationSummary;
    }

    @Override
    public VariantAnnotationSummary getAnnotationSummary(String variant, String isoformOverrideSource)
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException
    {
        return this.getAnnotationSummary(
            this.variantAnnotationService.getAnnotation(variant, isoformOverrideSource, null, null)
        );
    }

    @Override
    public List<VariantAnnotationSummary> getAnnotationSummary(List<String> variants, String isoformOverrideSource) {
        List<VariantAnnotationSummary> summaries = new ArrayList<>();

        List<VariantAnnotation> annotations =
            this.variantAnnotationService.getAnnotations(variants, isoformOverrideSource, null, null);

        for (VariantAnnotation annotation: annotations) {
            summaries.add(this.getAnnotationSummary(annotation));
        }

        return summaries;
    }

    @Override
    public List<VariantAnnotationSummary> getAnnotationSummaryForCanonical(
        List<String> variants,
        String isoformOverrideSource
    ) {
        List<VariantAnnotationSummary> summaries = new ArrayList<>();

        List<VariantAnnotation> annotations =
            this.variantAnnotationService.getAnnotations(variants, isoformOverrideSource, null, null);

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
            List<TranscriptConsequence> transcriptConsequences = annotation.getTranscriptConsequences();
            if (transcriptConsequences != null) {
                for (TranscriptConsequence transcriptConsequence: transcriptConsequences) {
                    summaries.add(this.getTranscriptSummary(annotation, transcriptConsequence));
                }
            }
            annotationSummary.setTranscriptConsequenceSummaries(summaries);
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
            summary.setAminoAcids(this.aminoAcidsResolver.resolve(transcriptConsequence));
            summary.setAminoAcidRef(this.aminoAcidsResolver.getRefAminoAcid(transcriptConsequence));
            summary.setAminoAcidAlt(this.aminoAcidsResolver.getAltAminoAcid(transcriptConsequence));
            summary.setEntrezGeneId(this.resolveEntrezGeneId(transcriptConsequence));
            summary.setConsequenceTerms(this.consequenceTermsResolver.resolve(transcriptConsequence));
            summary.setHugoGeneSymbol(this.hugoGeneSymbolResolver.resolve(transcriptConsequence));
            summary.setHgvspShort(this.proteinChangeResolver.resolveHgvspShort(annotation, transcriptConsequence));
            summary.setHgvsp(this.proteinChangeResolver.resolveHgvsp(transcriptConsequence));
            summary.setHgvsc(this.proteinChangeResolver.resolveHgvsc(transcriptConsequence));
            summary.setProteinPosition(this.proteinPositionResolver.resolve(annotation, transcriptConsequence));
            summary.setRefSeq(this.refSeqResolver.resolve(transcriptConsequence));
            summary.setVariantClassification(this.variantClassificationResolver.resolve(annotation, transcriptConsequence));
            summary.setExon(this.exonResolver.resolve(transcriptConsequence));
            summary.setPolyphenPrediction(transcriptConsequence.getPolyphenPrediction());
            summary.setPolyphenScore(transcriptConsequence.getPolyphenScore());
            summary.setSiftPrediction(transcriptConsequence.getSiftPrediction());
            summary.setSiftScore(transcriptConsequence.getSiftScore());

            List<VUEs> vueArray =  Arrays.asList(vuesList);
            Map <String, RevisedProteinEffect> vuesMap = vueArray
            .stream()
            .map(VUEs::getRevisedProteinEffects)
            .filter(Objects::nonNull)
            .flatMap(revisedProteinEffects -> revisedProteinEffects.stream())
            .collect(Collectors.toMap(RevisedProteinEffect::getTranscriptId, Function.identity()));

            if (vuesMap.get(transcriptConsequence.getTranscriptId()) != null && vuesMap.get(transcriptConsequence.getTranscriptId()).getVariant().equals(annotation.getVariant()))
            {
                summary.setVariantClassification(vuesMap.get(transcriptConsequence.getTranscriptId()).getVariantClassification());
                summary.setHgvspShort(vuesMap.get(transcriptConsequence.getTranscriptId()).getRevisedProteinEffect());
            }

            if (transcriptConsequence.getTranscriptId() != null) {
                try {
                    String uniprotId = this.ensemblService.getUniprotId(transcriptConsequence.getTranscriptId());
                    if (uniprotId != null) {
                        summary.setUniprotId(uniprotId);
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        return summary;
    }
}
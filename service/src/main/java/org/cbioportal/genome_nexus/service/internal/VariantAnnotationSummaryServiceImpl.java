package org.cbioportal.genome_nexus.service.internal;

import org.cbioportal.genome_nexus.component.annotation.*;
import org.cbioportal.genome_nexus.model.TranscriptConsequenceSummary;
import org.cbioportal.genome_nexus.model.VariantAnnotationSummary;
import org.cbioportal.genome_nexus.model.IntergenicConsequenceSummary;
import org.cbioportal.genome_nexus.model.IntergenicConsequences;
import org.cbioportal.genome_nexus.model.RevisedProteinEffectJsonRecord;
import org.cbioportal.genome_nexus.model.TranscriptConsequence;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.EnsemblService;
import org.cbioportal.genome_nexus.service.VariantAnnotationService;
import org.cbioportal.genome_nexus.service.VariantAnnotationSummaryService;
import org.cbioportal.genome_nexus.service.annotation.EntrezGeneIdResolver;
import org.cbioportal.genome_nexus.service.exception.EnsemblWebServiceException;
import org.cbioportal.genome_nexus.service.exception.VariantAnnotationNotFoundException;
import org.cbioportal.genome_nexus.service.exception.VariantAnnotationWebServiceException;
import org.cbioportal.genome_nexus.service.remote.RevueDataFetcher;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cbioportal.genome_nexus.model.Vues;
import org.cbioportal.genome_nexus.model.VuesJsonRecord;

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
    private final Map<String, Vues> vuesMap;
    private final Boolean overwriteByConfirmedRevueOnly;

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
        ExonResolver exonResolver,
        @Value("${revue.url}") String vuesUrl,
        @Value("${overwrite_by_confirmed_revue_only}") String overwriteByConfirmedRevueOnlyValue
    ) throws IOException {
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
        this.vuesMap = this.buildVuesMap(RevueDataFetcher.getRevueData(vuesUrl));
        this.overwriteByConfirmedRevueOnly = Boolean.parseBoolean(overwriteByConfirmedRevueOnlyValue);
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

        if (annotationSummary != null) {
            if (canonicalTranscript == null && annotation.getIntergenicConsequences() != null && !annotation.getIntergenicConsequences().isEmpty()) {
                // Set intergenicConsequenceSummaries when variant is "intergenic_variant", add variant classification based on consequence terms.
                // This is useful for genome nexus annotation pipeline, so when doing curation, those mutations will be filtered out by "IGR" rule, instead of being imported as "MUTATED".
                List<IntergenicConsequenceSummary> intergenicConsequenceSummaries = new ArrayList<>();
                for (IntergenicConsequences intergenicConsequence : annotation.getIntergenicConsequences()) {
                    IntergenicConsequenceSummary intergenicConsequenceSummary = new IntergenicConsequenceSummary();
                    intergenicConsequenceSummary.setVariantClassification(this.variantClassificationResolver.resolve(annotation, canonicalTranscript));
                    intergenicConsequenceSummary.setImpact(intergenicConsequence.getImpact());
                    intergenicConsequenceSummary.setVariantAllele(intergenicConsequence.getVariantAllele());
                    intergenicConsequenceSummary.setConsequenceTerms(intergenicConsequence.getConsequenceTerms());
                    intergenicConsequenceSummaries.add(intergenicConsequenceSummary);
                }
                annotationSummary.setIntergenicConsequenceSummaries(intergenicConsequenceSummaries);
            }
            else {
                annotationSummary.setTranscriptConsequenceSummary(this.getTranscriptSummary(annotation, canonicalTranscript, vuesMap));
                annotationSummary.setCanonicalTranscriptId(canonicalTranscript.getTranscriptId());
                // for backwards compatibility set transcriptConsequences
                List<TranscriptConsequenceSummary> transcriptConsequences = new ArrayList<>(1);
                transcriptConsequences.add(annotationSummary.getTranscriptConsequenceSummary());
                annotationSummary.setTranscriptConsequences(transcriptConsequences);

                // if this variant is VUE, add Vues information
                if (annotationSummary.getTranscriptConsequenceSummary() != null && 
                    annotationSummary.getTranscriptConsequenceSummary().getIsVue() != null && 
                    annotationSummary.getTranscriptConsequenceSummary().getIsVue()) {
                    annotationSummary.setVues(this.vuesMap.get(annotationSummary.getTranscriptConsequenceSummary().getTranscriptId()));
                }
            }
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
                    summaries.add(this.getTranscriptSummary(annotation, transcriptConsequence, vuesMap));
                }
            }
            annotationSummary.setTranscriptConsequenceSummaries(summaries);
            annotationSummary.setTranscriptConsequences(summaries);

            // if this variant is VUE, add Vues infomation
            if (annotationSummary.getTranscriptConsequenceSummary() != null &&
                annotationSummary.getTranscriptConsequenceSummary().getIsVue() != null &&
                annotationSummary.getTranscriptConsequenceSummary().getIsVue() == true) {
                annotationSummary.setVues(this.vuesMap.get(annotationSummary.getTranscriptConsequenceSummary().getTranscriptId() + "-" + annotationSummary.getVariant()));
            }
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

    // Read VUES.json and build Vues map
    // key is transcript id, value is Vues that contians more information of each VUE variant
    private Map <String, Vues> buildVuesMap(VuesJsonRecord[] vuesArray) {
        Map <String, Vues> vuesMap = new HashMap<>();
        for (VuesJsonRecord vueRecord: vuesArray) {
            if (vueRecord.getRevisedProteinEffects() != null) {
                for (RevisedProteinEffectJsonRecord revisedProteinEffectJsonRecord: vueRecord.getRevisedProteinEffects()) {
                    Vues vue = new Vues();
                    vue.setVariant(revisedProteinEffectJsonRecord.getVariant());
                    vue.setGenomicLocation(revisedProteinEffectJsonRecord.getGenomicLocation());
                    vue.setGenomicLocationDescription(vueRecord.getGenomicLocationDescription());
                    vue.setHugoGeneSymbol(vueRecord.getHugoGeneSymbol());
                    vue.setTranscriptId(revisedProteinEffectJsonRecord.getTranscriptId());
                    vue.setDefaultEffect(vueRecord.getDefaultEffect());
                    vue.setComment(vueRecord.getComment());
                    vue.setContext(vueRecord.getContext());
                    vue.setReferences(revisedProteinEffectJsonRecord.getReferences());
                    vue.setRevisedVariantClassificationStandard(this.variantClassificationResolver.resolveRevueVariantClassification(revisedProteinEffectJsonRecord.getRevisedVariantClassification()));
                    vue.setRevisedVariantClassification(revisedProteinEffectJsonRecord.getRevisedVariantClassification());
                    vue.setRevisedProteinEffect(revisedProteinEffectJsonRecord.getRevisedProteinEffect());
                    vue.setVepPredictedVariantClassification(revisedProteinEffectJsonRecord.getVepPredictedVariantClassification());
                    vue.setVepPredictedProteinEffect(revisedProteinEffectJsonRecord.getVepPredictedProteinEffect());
                    vue.setConfirmed(revisedProteinEffectJsonRecord.getConfirmed());
                    vuesMap.put(revisedProteinEffectJsonRecord.getTranscriptId() + "-" + vue.getVariant(), vue);
                }
            }
        }
        return vuesMap;
    }

    @Nullable
    private TranscriptConsequenceSummary getTranscriptSummary(VariantAnnotation annotation,
                                                              TranscriptConsequence transcriptConsequence,
                                                              Map<String, Vues> vuesMap)
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
            summary.setAlphaMissense(transcriptConsequence.getAlphaMissense());
            summary.setPolyphenPrediction(transcriptConsequence.getPolyphenPrediction());
            summary.setPolyphenScore(transcriptConsequence.getPolyphenScore());
            summary.setSiftPrediction(transcriptConsequence.getSiftPrediction());
            summary.setSiftScore(transcriptConsequence.getSiftScore());

            // Check if transcript id and variant id match one of the record in vuesMap
            // If overwriteByConfirmedRevueOnly is true, replace variantClassification, hgvspShort and proteinPosition to the value from vuesMap for confirmed VUE
            // If overwriteByConfirmedRevueOnly is false, replace variantClassification, hgvspShort and proteinPosition to the value from vuesMap for both confirmed and unconfirmed VUE
            Vues vue = vuesMap.get(transcriptConsequence.getTranscriptId() + "-" + annotation.getVariant());
            if (vue != null) {
                if ((overwriteByConfirmedRevueOnly == true && vue.getConfirmed()== true) || overwriteByConfirmedRevueOnly == false) {
                    summary.setVariantClassification(vue.getRevisedVariantClassificationStandard());
                    summary.setHgvspShort(vue.getRevisedProteinEffect());
                    summary.setProteinPosition(this.proteinPositionResolver.extractProteinPos(vue.getRevisedProteinEffect()));
                    summary.setIsVue(true);
                }
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

package org.cbioportal.genome_nexus.component.annotation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.mongodb.lang.Nullable;

import org.cbioportal.genome_nexus.model.Index;
import org.cbioportal.genome_nexus.model.TranscriptConsequence;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IndexResolver {
    private final ProteinChangeResolver proteinChangeResolver;
    @Autowired
    public IndexResolver(ProteinChangeResolver proteinChangeResolver)
    {
        this.proteinChangeResolver = proteinChangeResolver;
    }
    @NotNull
    public Index resolve(VariantAnnotation variantAnnotation)
    {
        Index index = new Index();
        index.setVariant(this.resolveVariant(variantAnnotation));
        index.setHugoSymbol(this.resolveHugoSymbol(variantAnnotation));
        index.setHgvsp(this.resolveHgvsp(variantAnnotation));
        index.setHgvsc(this.resolveHgvsc(variantAnnotation));
        index.setCdna(this.resolveCdna(variantAnnotation));
        index.setHgvspShort(this.resolveHgvspShort(variantAnnotation));

        return index;
    }

    @Nullable
    private String resolveVariant(VariantAnnotation variantAnnotation)
    {
        String variant = null;

        if (variantAnnotation != null &&
            variantAnnotation.getHgvsg() != null)
        {
            variant = variantAnnotation.getHgvsg();
        }

        return variant;
    }

    @Nullable
    private List<String> resolveHugoSymbol(VariantAnnotation variantAnnotation)
    {
        List<String> hugoSymbol = null;

        if (variantAnnotation != null &&
            variantAnnotation.getTranscriptConsequences() != null &&
            variantAnnotation.getTranscriptConsequences().size() > 0)
        {
            Set<String> hugoSymbolSet = new HashSet<>();
            for (TranscriptConsequence transcriptConsequence : variantAnnotation.getTranscriptConsequences()) {
                if (transcriptConsequence.getGeneSymbol() != null) {
                    hugoSymbolSet.add(transcriptConsequence.getGeneSymbol());
                }
            }
            hugoSymbol = hugoSymbolSet.stream().collect(Collectors.toList());
        }

        return hugoSymbol;
    }

    @Nullable
    private List<String> resolveHgvsp(VariantAnnotation variantAnnotation)
    {
        List<String> hgvsp = null;

        if (variantAnnotation != null &&
            variantAnnotation.getTranscriptConsequences() != null &&
            variantAnnotation.getTranscriptConsequences().size() > 0)
        {
            Set<String> hgvspSet = new HashSet<>();
            for (TranscriptConsequence transcriptConsequence : variantAnnotation.getTranscriptConsequences()) {
                if (transcriptConsequence.getHgvsp() != null) {
                    hgvspSet.add(transcriptConsequence.getHgvsp());
                }
            }
            hgvsp = hgvspSet.stream().collect(Collectors.toList());
        }

        return hgvsp;
    }

    @Nullable
    private List<String> resolveHgvsc(VariantAnnotation variantAnnotation)
    {
        List<String> hgvsc = null;

        if (variantAnnotation != null &&
            variantAnnotation.getTranscriptConsequences() != null &&
            variantAnnotation.getTranscriptConsequences().size() > 0)
        {
            Set<String> hgvscSet = new HashSet<>();
            for (TranscriptConsequence transcriptConsequence : variantAnnotation.getTranscriptConsequences()) {
                if (transcriptConsequence.getHgvsc() != null) {
                    hgvscSet.add(transcriptConsequence.getHgvsc());
                }
            }
            hgvsc = hgvscSet.stream().collect(Collectors.toList());
        }

        return hgvsc;
    }

    @Nullable
    private List<String> resolveHgvspShort(VariantAnnotation variantAnnotation)
    {
        List<String> hgvspShort = null;

        if (variantAnnotation != null &&
            variantAnnotation.getTranscriptConsequences() != null &&
            variantAnnotation.getTranscriptConsequences().size() > 0)
        {
            Set<String> hgvspShortSet = new HashSet<>();
            for (TranscriptConsequence transcriptConsequence : variantAnnotation.getTranscriptConsequences()) {
                String hgvspShortResult = this.proteinChangeResolver.resolveHgvspShort(variantAnnotation, transcriptConsequence);
                if (hgvspShortResult != null) {
                    hgvspShortSet.add(hgvspShortResult);
                }
            }
            hgvspShort = hgvspShortSet.stream().collect(Collectors.toList());
        }

        return hgvspShort;
    }

    @Nullable
    private List<String> resolveCdna(VariantAnnotation variantAnnotation)
    {
        List<String> cdna = null;

        if (variantAnnotation != null &&
            variantAnnotation.getTranscriptConsequences() != null &&
            variantAnnotation.getTranscriptConsequences().size() > 0)
        {
            Set<String> cdnaSet = new HashSet<>();
            for (TranscriptConsequence transcriptConsequence : variantAnnotation.getTranscriptConsequences()) {
                if (transcriptConsequence.getHgvsc() != null && transcriptConsequence.getHgvsc().contains(":c.")) {
                    cdnaSet.add(transcriptConsequence.getHgvsc().split(":")[1]);
                }
            }
            cdna = cdnaSet.stream().collect(Collectors.toList());
        }

        return cdna;
    }
}

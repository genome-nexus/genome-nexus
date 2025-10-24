package org.cbioportal.genome_nexus.component.annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cbioportal.genome_nexus.model.Index;
import org.cbioportal.genome_nexus.model.TranscriptConsequence;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mongodb.lang.Nullable;

@Component
public class IndexBuilder {
    private final ProteinChangeResolver proteinChangeResolver;
    private final HugoGeneSymbolResolver hugoGeneSymbolResolver;
    private final Boolean replaceOldGeneSymbol;
    
    @Autowired
    public IndexBuilder(ProteinChangeResolver proteinChangeResolver, HugoGeneSymbolResolver hugoGeneSymbolResolver, @Value("${replace_old_gene_symbol:true}") String replaceOldGeneSymbolValue)
    {
        this.proteinChangeResolver = proteinChangeResolver;
        this.hugoGeneSymbolResolver = hugoGeneSymbolResolver;
        this.replaceOldGeneSymbol = Boolean.valueOf(replaceOldGeneSymbolValue);
    }
    @NotNull
    public Index buildIndex(VariantAnnotation variantAnnotation)
    {
        Index index = new Index();
        index.setVariant(this.resolveVariant(variantAnnotation));
          List<String> hugoSymbols;
        if (Boolean.TRUE.equals(this.replaceOldGeneSymbol)) {
            hugoSymbols = this.hugoGeneSymbolResolver.resolveAllHugoGeneSymbols(variantAnnotation);
        } else {
            hugoSymbols = this.resolveAllOriginalGeneSymbols(variantAnnotation);
        }
        index.setHugoSymbol(hugoSymbols);
        index.setHgvsp(this.proteinChangeResolver.resolveAllHgvsp(variantAnnotation));
        index.setHgvsc(this.proteinChangeResolver.resolveAllHgvsc(variantAnnotation));
        index.setCdna(this.proteinChangeResolver.resolveAllCdna(variantAnnotation));
        index.setHgvspShort(this.proteinChangeResolver.resolveAllHgvspShort(variantAnnotation));

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
    private List<String> resolveAllOriginalGeneSymbols(VariantAnnotation variantAnnotation)
    {
        if (variantAnnotation == null ||
            variantAnnotation.getTranscriptConsequences() == null ||
            variantAnnotation.getTranscriptConsequences().isEmpty())
        {
            return null;
        }

        Set<String> hugoSymbolSet = new HashSet<>();

        for (TranscriptConsequence tc : variantAnnotation.getTranscriptConsequences()) {
            if (tc.getGeneSymbol() != null && !tc.getGeneSymbol().trim().isEmpty()) {
                hugoSymbolSet.add(tc.getGeneSymbol());
            }
        }
        return new ArrayList<>(hugoSymbolSet);
    }


}

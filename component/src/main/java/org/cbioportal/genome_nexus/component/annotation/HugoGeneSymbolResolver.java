package org.cbioportal.genome_nexus.component.annotation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.cbioportal.genome_nexus.model.TranscriptConsequence;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.persistence.internal.EnsemblRepositoryCustom;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class HugoGeneSymbolResolver
{
    private final EnsemblRepositoryCustom ensemblRepository;

    @Autowired
    public HugoGeneSymbolResolver(EnsemblRepositoryCustom ensemblRepository) {
        this.ensemblRepository = ensemblRepository;
    }

    @Nullable
    public String resolve(TranscriptConsequence transcriptConsequence)
    {
        if (transcriptConsequence == null) {
            return null;
        }

        String symbol = transcriptConsequence.getGeneSymbol();
        if (symbol == null || symbol.trim().isEmpty()) {
            return null;
        }

        String hgncId = transcriptConsequence.getHgncId();
        if (hgncId != null && !hgncId.trim().isEmpty()) {
            String mapped = ensemblRepository.getOfficialHugoSymbol(symbol, hgncId);
            return mapped;
        }
        
        return ensemblRepository.getOfficialHugoSymbol(symbol);
    }

    @Nullable
    public List<String> resolveAllHugoGeneSymbols(VariantAnnotation variantAnnotation)
    {
        List<String> hugoSymbol = null;

        if (variantAnnotation != null &&
            variantAnnotation.getTranscriptConsequences() != null &&
            variantAnnotation.getTranscriptConsequences().size() > 0)
        {
            Set<String> hugoSymbolSet = new HashSet<>();
            for (TranscriptConsequence transcriptConsequence : variantAnnotation.getTranscriptConsequences()) {
                if (transcriptConsequence.getGeneSymbol() != null) {
                    hugoSymbolSet.add(ensemblRepository.getOfficialHugoSymbol(transcriptConsequence.getGeneSymbol(), transcriptConsequence.getHgncId()));
                }
            }
            hugoSymbol = hugoSymbolSet.stream().collect(Collectors.toList());
        }

        return hugoSymbol;
    }
}

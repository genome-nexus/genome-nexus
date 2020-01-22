package org.cbioportal.genome_nexus.service.internal;

import org.cbioportal.genome_nexus.model.SignalMutation;
import org.cbioportal.genome_nexus.persistence.SignalMutationRepository;
import org.cbioportal.genome_nexus.service.SignalMutationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SignalMutationServiceImpl implements SignalMutationService
{
    private final SignalMutationRepository signalMutationRepository;

    @Autowired
    public SignalMutationServiceImpl(SignalMutationRepository signalMutationRepository) {
        this.signalMutationRepository = signalMutationRepository;
    }

    @Override
    public List<SignalMutation> getSignalMutations() {
        return this.signalMutationRepository.findAll();
    }

    @Override
    public List<SignalMutation> getSignalMutations(String hugoGeneSymbol) {
        return this.signalMutationRepository.findByHugoGeneSymbol(hugoGeneSymbol);
    }

    @Override
    public List<SignalMutation> getSignalMutations(List<String> hugoGeneSymbols) {
        return this.signalMutationRepository.findByHugoGeneSymbolIn(hugoGeneSymbols);
    }
}

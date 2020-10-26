package org.cbioportal.genome_nexus.service.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cbioportal.genome_nexus.component.annotation.NotationConverter;
import org.cbioportal.genome_nexus.model.*;
import org.cbioportal.genome_nexus.persistence.SignalMutationRepository;
import org.cbioportal.genome_nexus.service.SignalQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SignalQueryServiceImpl implements SignalQueryService
{
    private static final Log LOG = LogFactory.getLog(SignalQueryServiceImpl.class);

    public static final Integer DEFAULT_RETURN_SIZE = 10;
    public static final Integer QUERY_MIN_LENGTH = 2;

    private static final Comparator<SignalQuery> QUERY_COMPARATOR =
        (o1, o2) -> o1.getMatchType().ordinal() - o2.getMatchType().ordinal();

    private final SignalMutationRepository signalMutationRepository;
    private final NotationConverter notationConverter;
    private final List<SignalQuery> signalIndex;

    @Autowired
    public SignalQueryServiceImpl(
        SignalMutationRepository signalMutationRepository,
        NotationConverter notationConverter
    ) {
        this.signalMutationRepository = signalMutationRepository;
        this.notationConverter = notationConverter;

        LOG.info("Building Signal index");
        this.signalIndex = this.processAllSignalMutations();
        LOG.info("Finished building Signal index");
    }

    @Override
    public List<SignalQuery> search(String keyword) {
        return this.search(keyword, null);
    }

    @Override
    public List<SignalQuery> search(String keyword, Integer limit)
    {
        List<SignalQuery> queries = new ArrayList<>();

        // TODO support multiple keywords?
        String queryString = keyword.trim();

        if (queryString.length() >= QUERY_MIN_LENGTH)
        {
            queries.addAll(this.searchByHugoSymbol(this.signalIndex, queryString));
            queries.addAll(this.searchByRegion(this.signalIndex, queryString));

            // TODO this may not be 100% accurate because we don't use annotated data yet
            queries.addAll(this.searchByVariant(this.signalIndex, queryString));

            // TODO we need annotated data to be able to search with protein change
            // queries.addAll(this.searchAlterations(mutations, queryString));
        }

        int returnSize = limit == null || limit < 1 ? DEFAULT_RETURN_SIZE: limit.intValue();
        int toIndex = Math.min(queries.size(), returnSize);

        return queries.size() > 0 ? queries.subList(0, toIndex): queries;
    }

    private List<SignalQuery> processAllSignalMutations() {
        List<SignalMutation> mutations = this.signalMutationRepository.findAll();

        return mutations
            .stream()
            .map(this::mapMutationToExactQuery)
            .collect(Collectors.toList());
    }

    private List<SignalQuery> searchByHugoSymbol(
        List<SignalQuery> signalIndex,
        String queryString
    ) {
        return signalIndex
            .stream()
            .filter(m -> filterByHugoSymbol(m, queryString))
            .map(SignalQuery::getHugoSymbol)
            .distinct()
            .map(gene -> this.mapHugoSymbolToQuery(gene, queryString, signalIndex))
            .sorted(QUERY_COMPARATOR)
            .collect(Collectors.toList());
    }

    private List<SignalQuery> searchByRegion(
        List<SignalQuery> signalIndex,
        String queryString
    ) {
        return signalIndex
            .stream()
            .filter(m -> this.filterByRegion(m, queryString))
            .map(m -> this.mapToVariantQuery(m, SignalQueryType.REGION, queryString))
            .sorted(QUERY_COMPARATOR)
            .collect(Collectors.toList());
    }

    private List<SignalQuery> searchByVariant(
        List<SignalQuery> signalIndex,
        String queryString
    ) {
        return signalIndex
            .stream()
            .filter(m -> this.filterByVariant(m, queryString))
            .map(m -> this.mapToVariantQuery(m, SignalQueryType.VARIANT, queryString))
            .sorted(QUERY_COMPARATOR)
            .collect(Collectors.toList());
    }

    private SignalQuery mapMutationToExactQuery(SignalMutation mutation) {
        String region = this.generateRegion(mutation);
        String variant = null;
        GenomicLocation gl = generateGenomicLocation(mutation);

        if (gl.getChromosome() != null &&
            gl.getVariantAllele() != null &&
            gl.getReferenceAllele() != null) {
            variant = this.notationConverter.genomicToHgvs(gl);
        }

        SignalQuery query = new SignalQuery();

        query.setMatchType(SignalMatchType.EXACT);
        query.setHugoSymbol(mutation.getHugoGeneSymbol());
        query.setRegion(region);
        query.setVariant(variant);
        // TODO query.setAlteration(alteration); // we need annotated data for this

        return query;
    }

    private SignalQuery mapToVariantQuery(
        SignalQuery indexedQuery,
        SignalQueryType queryType,
        String queryString
    ) {
        String actualString = queryType.equals(SignalQueryType.REGION) ?
            indexedQuery.getRegion(): indexedQuery.getVariant();

        SignalQuery query = new SignalQuery();

        query.setHugoSymbol(indexedQuery.getHugoSymbol());
        query.setRegion(indexedQuery.getRegion());
        query.setVariant(indexedQuery.getVariant());
        query.setQueryType(queryType);
        query.setMatchType(this.findMatchType(actualString, queryString));

        return query;
    }

    private SignalQuery mapHugoSymbolToQuery(
        String hugoSymbol,
        String queryString,
        List<SignalQuery> signalIndex
    ) {
        long mutationCount = signalIndex.stream().filter(m -> this.filterByHugoSymbol(m, hugoSymbol)).count();

        SignalQuery query = new SignalQuery();
        query.setQueryType(SignalQueryType.GENE);
        query.setMatchType(this.findMatchType(hugoSymbol, queryString.toUpperCase()));
        query.setHugoSymbol(hugoSymbol);
        query.setDescription(mutationCount + " unique mutations");

        return query;
    }

    private boolean filterByHugoSymbol(
        SignalQuery query,
        String queryString
    ) {
        return (
            query.getHugoSymbol() != null &&
            query.getHugoSymbol().contains(queryString.toUpperCase())
        );
    }

    private boolean filterByRegion(
        SignalQuery query,
        String queryString
    ) {
        return query.getRegion().contains(queryString);
    }

    private boolean filterByVariant(
        SignalQuery query,
        String queryString
    ) {
        return query.getVariant() != null && query.getVariant().contains(queryString);
    }

    private SignalMatchType findMatchType(
        String actualString,
        String queryString
    ) {
        if (actualString == null) {
            return SignalMatchType.NO_MATCH;
        }
        if (actualString.equals(queryString)) {
            return SignalMatchType.EXACT;
        }
        else if (actualString.startsWith(queryString)) {
            return SignalMatchType.STARTS_WITH;
        }
        else if (actualString.contains(queryString)) {
            return SignalMatchType.PARTIAL;
        }
        else {
            return SignalMatchType.NO_MATCH;
        }
    }

    private String generateRegion(SignalMutation mutation) {
        return mutation.getChromosome() + ":" + mutation.getStartPosition() + "-" + mutation.getEndPosition();
    }

    private GenomicLocation generateGenomicLocation(SignalMutation mutation) {
        GenomicLocation gl = new GenomicLocation();

        gl.setChromosome(mutation.getChromosome());
        gl.setStart(mutation.getStartPosition().intValue());
        gl.setEnd(mutation.getEndPosition().intValue());
        gl.setReferenceAllele(mutation.getReferenceAllele());
        gl.setVariantAllele(mutation.getVariantAllele());

        return gl;
    }
}

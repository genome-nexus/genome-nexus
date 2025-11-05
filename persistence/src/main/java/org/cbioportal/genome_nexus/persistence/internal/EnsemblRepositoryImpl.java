package org.cbioportal.genome_nexus.persistence.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.cbioportal.genome_nexus.model.EnsemblCanonical;
import org.cbioportal.genome_nexus.model.EnsemblGene;
import org.cbioportal.genome_nexus.model.EnsemblTranscript;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class EnsemblRepositoryImpl implements EnsemblRepositoryCustom
{
    private final MongoTemplate mongoTemplate;
    private Map<String, String> hugoSymbolToEntrezGeneIdMap = new HashMap<>();
    private Map<String, String> entrezGeneIdToHugoSymbolMap = new HashMap<>();
    private Map<String, List<String>> geneAliasToEntrezGeneIdMap = new HashMap<>();
    private Map<String, String> previousGeneSymbolToOfficialHugoSymbolMap = new HashMap<>();
    private Map<String, Map<String, String>> previousSymbolAndHgncToOfficial = new HashMap<>();
    private Map<String, Set<String>> previousSymbolToOfficialCandidates = new HashMap<>();

    @Autowired
    public EnsemblRepositoryImpl(MongoTemplate mongoTemplate)
    {
        this.mongoTemplate = mongoTemplate;
        initHugoSymbolToEntrezGeneIdMap();
    }

    public static final String CANONICAL_TRANSCRIPTS_COLLECTION = "ensembl.canonical_transcript_per_hgnc";
    public static final String TRANSCRIPTS_COLLECTION = "ensembl.biomart_transcripts";

    private EnsemblCanonical findOneCanonicalByHugoSymbol(String hugoSymbol) {
        EnsemblCanonical ensemblCanonical;
        Query query;

        // check approved symbols
        Criteria approvedSymbolCriteria = Criteria.where("hgnc_symbol").regex("^" + hugoSymbol + "$", "i");
        query = new Query();
        query.addCriteria(approvedSymbolCriteria);
        ensemblCanonical = mongoTemplate.findOne(query,
            EnsemblCanonical.class, CANONICAL_TRANSCRIPTS_COLLECTION);

        if (ensemblCanonical == null) {
            // check prev symbols
            Criteria prevSymbolsCriteria = Criteria.where("previous_symbols").regex("(^| |,)" + hugoSymbol + "($| |,)", "i");
            query = new Query();
            query.addCriteria(prevSymbolsCriteria);
            ensemblCanonical = mongoTemplate.findOne(query,
                EnsemblCanonical.class, CANONICAL_TRANSCRIPTS_COLLECTION);

            if (ensemblCanonical == null) {
                // check synonyms
                Criteria synonymCriteria = Criteria.where("synonyms").regex("(^| |,)" + hugoSymbol + "($| |,)", "i");
                query = new Query();
                query.addCriteria(synonymCriteria);
                ensemblCanonical = mongoTemplate.findOne(query,
                    EnsemblCanonical.class, CANONICAL_TRANSCRIPTS_COLLECTION);
            }
        }

        return ensemblCanonical;
    }

    private EnsemblCanonical findOneCanonicalByEntrezGeneId(String entrezGeneId) {
        EnsemblCanonical ensemblCanonical;
        Query query;

        // search by entrez gene id
        Criteria criteria = Criteria.where("entrez_gene_id").is(Integer.valueOf(entrezGeneId));
        query = new Query();
        query.addCriteria(criteria);
        ensemblCanonical = mongoTemplate.findOne(query,
            EnsemblCanonical.class, CANONICAL_TRANSCRIPTS_COLLECTION);

        return ensemblCanonical;
    }

    @Override
    public EnsemblGene getCanonicalEnsemblGeneIdByHugoSymbol(String hugoSymbol) {
        EnsemblCanonical ensemblCanonical = this.findOneCanonicalByHugoSymbol(hugoSymbol);
        if (ensemblCanonical != null) {
            return new EnsemblGene(ensemblCanonical);
        } else {
            return null;
        }
    }

    @Override
    public EnsemblGene getCanonicalEnsemblGeneIdByEntrezGeneId(String entrezGeneId) {
        EnsemblCanonical ensemblCanonical = this.findOneCanonicalByEntrezGeneId(entrezGeneId);
        if (ensemblCanonical != null) {
            return new EnsemblGene(ensemblCanonical);
        } else {
            return null;
        }
    }

    @Override
    public EnsemblTranscript findOneByHugoSymbolIgnoreCase(String hugoSymbol, String isoformOverrideSource) {
        EnsemblCanonical ensemblCanonical = this.findOneCanonicalByHugoSymbol(hugoSymbol);
        if (ensemblCanonical != null) {
            Query query = new Query();
            query.addCriteria(Criteria.where(EnsemblTranscript.TRANSCRIPT_ID_FIELD_NAME).is(ensemblCanonical.getCanonicalTranscriptId(isoformOverrideSource)));

            EnsemblTranscript transcript = mongoTemplate.findOne(query, EnsemblTranscript.class, TRANSCRIPTS_COLLECTION);
            if (transcript != null) {
                return transcript;
            }
        }
        return null;
    }

    private void initHugoSymbolToEntrezGeneIdMap() {
        List<EnsemblCanonical> transcripts =
            mongoTemplate.findAll(EnsemblCanonical.class, CANONICAL_TRANSCRIPTS_COLLECTION);

        // collect current approved symbols
        Set<String> approvedSymbols = transcripts.stream()
            .map(EnsemblCanonical::getHugoSymbol)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        for (EnsemblCanonical transcript : transcripts) {
            String officialSymbol = transcript.getHugoSymbol();
            String entrezId = transcript.getEntrezGeneId();
            String hgncId = transcript.getHgncId();
            String[] previousSymbols = transcript.getPreviousSymbols();
            String[] synonyms = transcript.getSynonyms();

            hugoSymbolToEntrezGeneIdMap.put(officialSymbol, entrezId);
            entrezGeneIdToHugoSymbolMap.put(entrezId, officialSymbol);

            // alias synonyms to entrez id
            if (synonyms != null) {
                for (String synonym : synonyms) {
                    if (synonym == null) continue;
                    List<String> aliases = geneAliasToEntrezGeneIdMap
                        .computeIfAbsent(synonym, k -> new ArrayList<>());
                    if (entrezId != null && !aliases.contains(entrezId)) {
                        aliases.add(entrezId);
                    }
                }
            }

            // previous symbols
            if (previousSymbols != null) {
                for (String previousSymbol : previousSymbols) {
                    if (previousSymbol == null) continue;

                    List<String> aliases = geneAliasToEntrezGeneIdMap
                        .computeIfAbsent(previousSymbol, k -> new ArrayList<>());
                    if (entrezId != null && !aliases.contains(entrezId)) {
                        aliases.add(entrezId);
                    }

                    // only add previous symbols to map if previousSymbol is NOT reused as another current approved symbol
                    // E.g. ETF1 is the new name for ERF, but ERF now is another gene’s name
                    if (officialSymbol != null && !approvedSymbols.contains(previousSymbol)) {
                        previousSymbolToOfficialCandidates
                            .computeIfAbsent(previousSymbol, k -> new HashSet<>())
                            .add(officialSymbol);

                        // HGNC id map: (previousSymbol, hgncId) -> official symbol
                        // Some genes have the same previous symbol (e.g. IRF4 and PWWP3A have the same previous_name)
                        // This map has previousSymbol as the key, value is map<hgnc_id, officialSymbol>
                        if (hgncId != null) {
                            previousSymbolAndHgncToOfficial
                                .computeIfAbsent(previousSymbol, k -> new HashMap<>())
                                .put(hgncId, officialSymbol);
                        }
                    }
                }
            }
        }

        // keep only unambiguous previousSymbol to official symbol mappings in the simple map
        for (Map.Entry<String, Set<String>> e : previousSymbolToOfficialCandidates.entrySet()) {
            if (e.getValue().size() == 1) {
                previousGeneSymbolToOfficialHugoSymbolMap.put(
                    e.getKey(), e.getValue().iterator().next());
            }
        }
    }
    @Override
    public String getOfficialHugoSymbol(String symbol) {
        return previousGeneSymbolToOfficialHugoSymbolMap.getOrDefault(symbol, symbol);
    }

    @Override
    public String getOfficialHugoSymbol(String previousSymbol, String hgncId) {
        if (previousSymbol == null || hgncId == null) {
            return previousSymbol;
        }
        Map<String, String> byHgnc = previousSymbolAndHgncToOfficial.get(previousSymbol);
        if (byHgnc == null) {
            return previousSymbol;
        }
        return byHgnc.getOrDefault(hgncId, previousSymbol);
    }

    @Override
    public String findEntrezGeneIdByHugoSymbol(String hugoSymbol) {
        return hugoSymbolToEntrezGeneIdMap.get(hugoSymbol);
    }

    @Override
    public List<String> findEntrezGeneIdByHugoSymbol(String hugoSymbol, Boolean searchInAliases) {
        List<String> entrezGeneIds = new ArrayList<>();
        String entrezGeneId = hugoSymbolToEntrezGeneIdMap.get(hugoSymbol);
        if (entrezGeneId != null && !entrezGeneIds.contains(entrezGeneId)) {
            entrezGeneIds.add(entrezGeneId);
        }

        if (Boolean.TRUE.equals(searchInAliases)) {
            List<String> aliasMatches = geneAliasToEntrezGeneIdMap.get(hugoSymbol);
            if (aliasMatches != null) {
                for (String id : aliasMatches) {
                    if (id != null && !entrezGeneIds.contains(id)) entrezGeneIds.add(id);
                }
            }
        }
        return entrezGeneIds;
    }

    @Override
    public String findHugoSymbolByEntrezGeneId(String entrezGeneId) {
        return entrezGeneIdToHugoSymbolMap.get(entrezGeneId);
    }

    @Override
    public Set<String> findCanonicalTranscriptIdsBySource(String isoformOverrideSource) {
        List<EnsemblCanonical> transcripts = mongoTemplate.findAll(
            EnsemblCanonical.class, CANONICAL_TRANSCRIPTS_COLLECTION);

        Set<String> canonicalTranscriptIds = transcripts
            .stream()
            .map(t -> t.getCanonicalTranscriptId(isoformOverrideSource))
            .collect(Collectors.toSet());

        // CDKN2A is a special case where it has two canonical transcripts
        // by default the first canonical transcript ENST00000579755 is returned
        // if the isoform override source is mskcc then the second canonical transcript ENST00000304494 should be added as well
        // ENST00000579755 is MANE plus clinical p14, ENST00000304494 is (p16 MANE). Germline transcript is MANE plus clinical.

        if ("mskcc".equalsIgnoreCase(isoformOverrideSource)) {
            canonicalTranscriptIds.add("ENST00000304494");
        }
        return canonicalTranscriptIds;
    }
}

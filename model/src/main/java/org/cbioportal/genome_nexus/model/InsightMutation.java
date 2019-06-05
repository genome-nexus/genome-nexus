package org.cbioportal.genome_nexus.model;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection="insight.mutation")
public class InsightMutation
{
    @Field("hugo_gene_symbol")
    private String hugoGeneSymbol;

    @Field("chromosome")
    private String chromosome;

    @Field("start_position")
    private Long startPosition;

    @Field("end_position")
    private Long endPosition;

    @Field("reference_allele")
    private String referenceAllele;

    @Field("variant_allele")
    private String variantAllele;

    @Field("mutation_status")
    private String mutationStatus;

    @Field("pathogenic")
    private String pathogenic;

    @Field("penetrance")
    private String penetrance;

    @Field("counts_by_tumor_type")
    private List<CountByTumorType> countsByTumorType;

    @Field("biallelic_counts_by_tumor_type")
    private List<CountByTumorType> biallelicCountsByTumorType;

    @Field("qc_pass_counts_by_tumor_type")
    private List<CountByTumorType> qcPassCountsByTumorType;

    public String getHugoGeneSymbol() {
        return hugoGeneSymbol;
    }

    public void setHugoGeneSymbol(String hugoGeneSymbol) {
        this.hugoGeneSymbol = hugoGeneSymbol;
    }

    public String getChromosome() {
        return chromosome;
    }

    public void setChromosome(String chromosome) {
        this.chromosome = chromosome;
    }

    public Long getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(Long startPosition) {
        this.startPosition = startPosition;
    }

    public Long getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(Long endPosition) {
        this.endPosition = endPosition;
    }

    public String getReferenceAllele() {
        return referenceAllele;
    }

    public void setReferenceAllele(String referenceAllele) {
        this.referenceAllele = referenceAllele;
    }

    public String getVariantAllele() {
        return variantAllele;
    }

    public void setVariantAllele(String variantAllele) {
        this.variantAllele = variantAllele;
    }

    public String getMutationStatus() {
        return mutationStatus;
    }

    public void setMutationStatus(String mutationStatus) {
        this.mutationStatus = mutationStatus;
    }

    public String getPathogenic() {
        return pathogenic;
    }

    public void setPathogenic(String pathogenic) {
        this.pathogenic = pathogenic;
    }

    public String getPenetrance() {
        return penetrance;
    }

    public void setPenetrance(String penetrance) {
        this.penetrance = penetrance;
    }

    public List<CountByTumorType> getCountsByTumorType() {
        return countsByTumorType;
    }

    public void setCountsByTumorType(List<CountByTumorType> countsByTumorType) {
        this.countsByTumorType = countsByTumorType;
    }

    public List<CountByTumorType> getBiallelicCountsByTumorType() {
        return biallelicCountsByTumorType;
    }

    public void setBiallelicCountsByTumorType(List<CountByTumorType> biallelicCountsByTumorType) {
        this.biallelicCountsByTumorType = biallelicCountsByTumorType;
    }

    public List<CountByTumorType> getQcPassCountsByTumorType() {
        return qcPassCountsByTumorType;
    }

    public void setQcPassCountsByTumorType(List<CountByTumorType> qcPassCountsByTumorType) {
        this.qcPassCountsByTumorType = qcPassCountsByTumorType;
    }
}

package org.cbioportal.genome_nexus.model;

import java.util.List;

public class IndexSearch {

    private IndexSearchType queryType;
    private List<Index> indexes;

    public IndexSearchType getQueryType() {
        return queryType;
    }

    public void setQueryType(IndexSearchType queryType) {
        this.queryType = queryType;
    }

    public List<Index> getIndexes() {
        return indexes;
    }

    public void setIndexes(List<Index> indexes) {
        this.indexes = indexes;
    }
}

package org.cbioportal.genome_nexus.persistence.internal;

import java.io.Serializable;

public interface PdbHeaderRepositoryCustom<T, ID extends Serializable>
{
    // we need to override the default MongoRepository.findOne method to avoid the default behavior
    // and fetch & convert data from the simple (generic) cache
    T findOne(ID id);
}

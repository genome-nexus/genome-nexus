/*
 * Copyright (c) 2016 Memorial Sloan-Kettering Cancer Center.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF MERCHANTABILITY OR FITNESS
 * FOR A PARTICULAR PURPOSE. The software and documentation provided hereunder
 * is on an "as is" basis, and Memorial Sloan-Kettering Cancer Center has no
 * obligations to provide maintenance, support, updates, enhancements or
 * modifications. In no event shall Memorial Sloan-Kettering Cancer Center be
 * liable to any party for direct, indirect, special, incidental or
 * consequential damages, including lost profits, arising out of the use of this
 * software and its documentation, even if Memorial Sloan-Kettering Cancer
 * Center has been advised of the possibility of such damage.
 */

/*
 * This file is part of cBioPortal Cancer Hotspots.
 *
 * cBioPortal is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * Mutation Data Utils.
 *
 * @author Selcuk Onur Sumer
 */
var MutationUtils = (function() {

	/**
     * Generates mutation mapper data for the given hotspot mutation list.
     *
     * @param mutations an array of hotspot mutations
     * @returns {Array} an array of mutations for MutationMapper
     */
    function generateMutationMapperData(mutations)
    {
        var mutationData = {};

        _.each(mutations, function(mutation) {
            var counter = 0;
            var residueClass = findResidueClass(mutation.residue, mutations);

            if (!_.isEmpty(mutation.variantAminoAcid))
            {
                _.each(_.keys(mutation.variantAminoAcid), function (variant)
                {
                    _.times(mutation.variantAminoAcid[variant], function ()
                    {
                        counter++;
                        var mapperMutation = initMutation(mutation, residueClass, counter, variant);
                        // index by id instead of adding into an array
                        // this will prevent duplicates
                        mutationData[mapperMutation.mutationId] = mapperMutation;
                    });
                });
            }
            else // TODO a mutation with no variant amino acid composition is not a good sign!
            {
                var mapperMutation = initMutation(mutation, residueClass, 1);
                mutationData[mapperMutation.mutationId] = mapperMutation;
            }

        });

        return _.values(mutationData);
    }

    function findResidueClass(residue, mutations)
    {
        // any mutation with the current residue is enough to determine the class
        var mutation = _.find(mutations, function(mutation) {
            return mutation.residue === residue;
        });

        return mutation.classification;
    }

    function initMutation(mutation, residueClass, counter, variant)
    {
        var id = mutation.hugoSymbol + "_" + mutation.residue + "_" + counter;
        var proteinChange = mutation.residue;

        if (variant)
        {
            proteinChange = proteinChange + variant;
        }

        return {
            mutationId: id,
            mutationSid: id,
            proteinChange: proteinChange,
            geneSymbol: mutation.hugoSymbol,
            residue: mutation.residue,
            residueClass: residueClass
        };
    }

	/**
     * Converts pdb data into a list sorted by p-value.
     *
     * @param pdbData   PDB data as a map of (pdb_chain, p-value) pairs
     * @returns {Array}
     */
    function convertToPdbList(pdbData)
    {
        var pdbList = [];

        // convert map into a list
        _.each(_.keys(pdbData), function(key) {
            var parts = key.split("_");
            pdbList.push({
                pdbId: parts[0],
                chain: parts[1] || "NA",
                pValue: pdbData[key]
            });
        });

        // sort by p-value
        pdbList = _.sortBy(pdbList, function(pdbChain) {
            // ascending order
            return pdbChain.pValue;
        });

        return pdbList;
    }

    function defaultResidueSortValue(data)
    {
        // sort by residue position
        var matched = data.residue.match(/[0-9]+/g);

        if (matched && matched.length > 0) {
            return parseInt(matched[0]);
        }
        else {
            return data.residue;
        }
    }

    return {
        generateMutationMapperData: generateMutationMapperData,
        defaultResidueSortValue: defaultResidueSortValue,
        findResidueClass: findResidueClass,
        convertToPdbList: convertToPdbList
    }
})();

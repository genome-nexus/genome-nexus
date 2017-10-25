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
 *
 * @author Selcuk Onur Sumer
 */
var ViewUtils = (function() {

    var _legacyToOncotree = {
        mmyl: "mm",
        coadread: "coadread",
        gbm: "gbm",
        thca: "thyroid",
        hnsc: "headneck",
        skcm: "skcm",
        hgg: "difg", // ?
        lgg: "encg", // ?
        luad: "luad",
        kirp: "prcc",
        paad: "paad",
        brca: "brca",
        npc: "npc",
        esca: "esca",
        ucs: "ucs",
        blca: "blca",
        laml: "aml",
        ov: "hgsoc",
        lihc: "hcc",
        prad: "prad",
        stad: "stad",
        ucec: "ucec",
        cesc: "cesc",
        mds: "mds",
        acc: "acc",
        lusc: "lusc",
        mbl: "lusc",
        kirc: "ccrcc",
        acyc: "acyc",
        cll: "cll",
        kich: "chrcc",
        gbc: "gbc",
        lusm: "sclc",
        cscc: "cscc",
        pias: "past",
        dlbc: "dlbcl",
        lymbc: "bl",
        nbl: "nbl",
        mcl: "mcl",
        all: "all",
        unk: "other"
    };

    var _oncotree = {
        "all": {color: "orange", name: "Acute Lymphoid Leukemia"},
        "acc": {color: "purple", name: "Adrenocortical Carcinoma"},
        "acyc": {color: "darkred", name: "Adenoid Cystic Carcinoma"},
        "blca": {color: "yellow", name: "Bladder Urothelial Carcinoma"},
        "brca": {color: "hotpink", name: "Invasive Breast Carcinoma"},
        "cesc": {color: "teal", name: "Cervical Squamous Cell Carcinoma"},
        "cll": {color: "lightsalmon", name: "Chronic Lymphocytic Leukemia"},
        "coadread": {color: "saddlebrown", name: "Colorectal Adenocarcinoma"},
        "cscc": {color: "black", name: "Cutaneous Squamous Cell Carcinoma"},
        "dlbcl": {color: "limegreen", name: "Diffuse Large B-Cell Lymphoma"},
        "esca": {color: "lightskyblue", name: "Esophageal Adenocarcinoma"},
        "gbc": {color: "green", name: "Gallbladder Cancer"},
        "gbm": {color: "gray", name: "Glioblastoma Multiforme"},
        "difg": {color: "gray", name: "Diffuse Glioma"},
        "headneck": {color: "darkred", name: "Head and Neck Carcinoma"},
        "ccrcc": {color: "orange", name: "Renal Clear Cell Carcinoma"},
        "chrcc": {color: "orange", name: "Chromophobe Renal Cell Carcinoma"},
        "prcc": {color: "orange", name: "Papillary Renal Cell Carcinoma"},
        "aml": {color: "orange", name: "Acute Myeloid Leukemia"},
        "encg": {color: "gray", name: "Encapsulated Glioma"},
        "hcc": {color: "mediumseagreen", name: "Hepatocellular Carcinoma"},
        "luad": {color: "gainsboro", name: "Lung Adenocarcinoma"},
        "lusc": {color: "gainsboro", name: "Lung Squamous Cell Carcinoma"},
        "sclc": {color: "gainsboro", name: "Small Cell Lung Cancer"},
        "bl": {color: "limegreen", name: "Burkitt Lymphoma"},
        "mbl": {color: "gray", name: "Medulloblastoma"},
        "mcl": {color: "limegreen", name: "Mantle Cell Lymphoma"},
        "mds": {color: "lightsalmon", name: "Myelodysplasia"},
        "mm": {color: "lightsalmon", name: "Multiple Myeloma"},
        "nbl": {color: "gray", name: "Neuroblastoma"},
        "npc": {color: "darkred", name: "Nasopharyngeal Carcinoma"},
        "hgsoc": {color: "lightblue", name: "High-Grade Serous Ovarian Cancer"},
        "paad": {color: "purple", name: "Pancreatic Adenocarcinoma"},
        "past": {color: "gray", name: "Pilocytic Astrocytoma"},
        "prad": {color: "cyan", name: "Prostate Adenocarcinoma"},
        "skcm": {color: "black", name: "Cutaneous Melanoma"},
        "stad": {color: "lightskyblue", name: "Stomach Adenocarcinoma"},
        "ucec": {color: "peachpuff", name: "Endometrial Carcinoma"},
        "ucs": {color: "peachpuff", name: "Uterine Carcinosarcoma / Uterine Malignant Mixed Mullerian Tumor"},
        "pancreas": {"color": "Purple", "name": "Pancreas"},
        "blood": {"color": "LightSalmon", "name": "Blood"},
        "other": {"color": "Black", "name": "Other"},
        "head_neck": {"color": "DarkRed", "name": "Head and Neck"},
        "biliary_tract": {"color": "Green", "name": "Biliary Tract"},
        "pns": {"color": "Gray", "name": "Peripheral Nervous System"},
        "adrenal_gland": {"color": "Purple", "name": "Adrenal Gland"},
        "uterus": {"color": "PeachPuff", "name": "Uterus"},
        "thymus": {"color": "Purple", "name": "Thymus"},
        "liver": {"color": "MediumSeaGreen", "name": "Liver"},
        "testis": {"color": "Red", "name": "Testis"},
        "breast": {"color": "HotPink", "name": "Breast"},
        "bladder": {"color": "Yellow", "name": "Bladder/Urinary Tract"},
        "cervix": {"color": "Teal", "name": "Cervix"},
        "lymph": {"color": "LimeGreen", "name": "Lymph"},
        "brain": {"color": "Gray", "name": "CNS/Brain"},
        "bowel": {"color": "SaddleBrown", "name": "Bowel"},
        "ovary": {"color": "LightBlue", "name": "Ovary/Fallopian Tube"},
        "thyroid": {"color": "Teal", "name": "Thyroid"},
        "skin": {"color": "Black", "name": "Skin"},
        "lung": {"color": "Gainsboro", "name": "Lung"},
        "bone": {"color": "White", "name": "Bone"},
        "prostate": {"color": "Cyan", "name": "Prostate"},
        "soft_tissue": {"color": "LightYellow", "name": "Soft Tissue"},
        "stomach": {"color": "LightSkyBlue", "name": "Esophagus/Stomach"},
        "ampulla_of_vater": {"color": "Purple", "name": "Ampulla of Vater"},
        "kidney": {"color": "Orange", "name": "Kidney"},
        "vulva": {"color": "Purple", "name": "Vulva/Vagina"},
        "eye": {"color": "Green", "name": "Eye"},
        "peritoneum": {"color": "Green", "name": "Peritoneum"},
        "penis": {"color": "Blue", "name": "Penis"},
        "pleura": {"color": "Blue", "name": "Pleura"}
    };

    var _variantType = {
        "A": {color: "#3366cc"},
        "R": {color: "#dc3912"},
        "N": {color: "#dc3912"},
        "D": {color: "#ff9900"},
        "B": {color: "#109618"},
        "C": {color: "#990099"},
        "E": {color: "#0099c6"},
        "Q": {color: "#dd4477"},
        "Z": {color: "#66aa00"},
        "G": {color: "#b82e2e"},
        "H": {color: "#316395"},
        "I": {color: "#994499"},
        "L": {color: "#22aa99"},
        "K": {color: "#aaaa11"},
        "M": {color: "#6633cc"},
        "F": {color: "#e67300"},
        "P": {color: "#8b0707"},
        "S": {color: "#651067"},
        "T": {color: "#329262"},
        "W": {color: "#5574a6"},
        "Y": {color: "#3b3eac"},
        "V": {color: "#b77322"},
        "X": {color: "#16d620"},
        "sp": {color: "#b91383"},
        "*": {color: "#090303"}
    };

    var _classification = {
        "LL": "LL",
        "LH": "LH",
        "H": "H",
        "Cluster-exclusive": "LL",
        "Hotspot-linked": "LH",
        "Hotspot": "H"
    };

    function getDefaultTumorTypeColors()
    {
        var map = {};

        _.each(_.keys(_legacyToOncotree), function (legacyKey) {
            var oncoKey = _legacyToOncotree[legacyKey];
            if (_oncotree[oncoKey] != null) {
                map[legacyKey] = _oncotree[oncoKey].color;
            }
        });

        _.each(_.keys(_oncotree), function (oncoKey) {
            if (map[oncoKey] == null)
            {
                map[oncoKey] = _oncotree[oncoKey].color;
            }
        });

        return map;
    }

    function getDefaultVariantColors()
    {
        var map = {};

        _.each(_.keys(_variantType), function (key) {
            map[key] = _variantType[key].color;
        });

        return map;
    }

    function getTumorTypeNames()
    {
        var map = {};

        _.each(_.keys(_legacyToOncotree), function (legacyKey) {
            var oncoKey = _legacyToOncotree[legacyKey];
            if (_oncotree[oncoKey] != null) {
                map[legacyKey] = _oncotree[oncoKey].name;
            }
        });

        _.each(_.keys(_oncotree), function (oncoKey) {
           if (map[oncoKey] == null)
           {
               map[oncoKey] = _oncotree[oncoKey].name;
           }
        });

        return map;
    }

	/**
     * For the provided metadata options, determines the visibility
     * of the given columns.
     *
     * @param columns   list of DataTable columns
     * @param metadata  metadata containing profile info
     * @returns {Array} columns to be listed in the UI
     */
    function determineVisibility(columns, metadata)
    {
        var includedCols = [];

        if (metadata && metadata.profile)
        {
            var profile = metadata.profile.toLowerCase();
            var profileOpts = null;

            // excluded: excluded from the table (always hidden)
            // hidden: initially hidden, can be toggled visible later
            var options = {
                "3d": {
                    excluded: ["qValue", "qValueCancerType", "qValuePancan", "type"],
                    hidden: []
                },
                "singleresidue": {
                    excluded: ["pValue", "clusters", "classification"],
                    hidden: ["qValueCancerType", "qValuePancan"]
                },
                "other" : {
                    excluded: [],
                    hidden: []
                }
            };

            if (profile.indexOf("singleresidue") != -1) {
                profileOpts = options["singleresidue"];
            }
            else if (profile.indexOf("3d") != -1) {
                profileOpts = options["3d"];
            }
            else {
                profileOpts = options["hidden"];
            }

            _.each(columns, function(column) {
                var isHidden = _.contains(profileOpts.hidden, column.name);
                var isExcluded = _.contains(profileOpts.excluded, column.name);

                if (isHidden || isExcluded) {
                    column.visible = false;
                }

                if (!isExcluded) {
                    includedCols.push(column);
                }

                // TODO workaround for a DataTables issue: even if the column is initially invisible,
                // DataTables still call all available render functions at init!
                if (isExcluded) {
                    delete(column.createdCell);
                }
            });
        }

        return includedCols;
    }

    function determineDownload(dataTable, columns)
    {
        var toDownload = [];

        // if the column is not included in data table add it to the list in any case
        // if the column is in the table but currently hidden, do not add it
        _.each(columns, function(col) {
            if (_.first(dataTable.columns(col.name + ":name").visible()) !== false) {
                toDownload.push(col);
            }
        });

        return toDownload;
    }

    function getClassStyle(classification)
    {
        return _classification[classification];
    }

    return {
        determineVisibility: determineVisibility,
        determineDownload: determineDownload,
        getTumorTypeNames: getTumorTypeNames,
        getDefaultTumorTypeColors: getDefaultTumorTypeColors,
        getDefaultVariantColors: getDefaultVariantColors,
        getClassStyle: getClassStyle
    };

})();

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
 * This file is part of cBioPortal Genome Nexus.
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

package org.cbioportal.genome_nexus.model;

import org.springframework.data.mongodb.core.mapping.Field;

public class ColocatedVariant
{
    @Field(value="id")
    private String dbSnpId;

    @Field(value="gnomad_nfe_maf")
    private String gnomadNfeMaf;

    @Field(value="gnomad_nfe_allele")
    private String gnomadNfeAllele;

    @Field(value="gnomad_afr_maf")
    private String gnomadAfrMaf;

    @Field(value="gnomad_afr_allele")
    private String gnomadAfrAllele;

    @Field(value="gnomad_eas_maf")
    private String gnomadEasMaf;

    @Field(value="gnomad_eas_allele")
    private String gnomadEasAllele;

    public String getDbSnpId() {
        return dbSnpId;
    }

    public void setDbSnpId(String dbSnpId) {
        this.dbSnpId = dbSnpId;
    }

    public String getGnomadNfeMaf() {
        return gnomadNfeMaf;
    }

    public void setGnomadNfeMaf(String gnomadNfeMaf) {
        this.gnomadNfeMaf = gnomadNfeMaf;
    }

    public String getGnomadNfeAllele() {
        return gnomadNfeAllele;
    }

    public void setGnomadNfeAllele(String gnomadNfeAllele) {
        this.gnomadNfeAllele = gnomadNfeAllele;
    }

    public String getGnomadAfrMaf() {
        return gnomadAfrMaf;
    }

    public void setGnomadAfrMaf(String gnomadAfrMaf) {
        this.gnomadAfrMaf = gnomadAfrMaf;
    }

    public String getGnomadAfrAllele() {
        return gnomadAfrAllele;
    }

    public void setGnomadAfrAllele(String gnomadAfrAllele) {
        this.gnomadAfrAllele = gnomadAfrAllele;
    }

    public String getGnomadEasMaf() {
        return gnomadEasMaf;
    }

    public void setGnomadEasMaf(String gnomadEasMaf) {
        this.gnomadEasMaf = gnomadEasMaf;
    }

    public String getGnomadEasAllele() {
        return gnomadEasAllele;
    }

    public void setGnomadEasAllele(String gnomadEasAllele) {
        this.gnomadEasAllele = gnomadEasAllele;
    }
}

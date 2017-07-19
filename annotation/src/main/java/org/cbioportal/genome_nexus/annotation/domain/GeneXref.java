/*
 * Copyright (c) 2017 Memorial Sloan-Kettering Cancer Center.
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

package org.cbioportal.genome_nexus.annotation.domain;

import com.fasterxml.jackson.annotation.*;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;

/**
 *
 * @author ochoaa
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GeneXref {
    
    private String displayId;
    private String primaryId;
    private String version;
    private String description;
    private String dbName;
    private List<String> synonyms;
    private String infoText;
    private String infoType;
    private String dbDisplayName;
    
    /**
     * @return the displayId
     */
    @JsonProperty(value = "display_id", required = true)
    @ApiModelProperty(value = "Display id", required = true)
    public String getDisplayId() {
        return displayId;
    }

    /**
     * @param displayId the displayId to set
     */
    public void setDisplayId(String displayId) {
        this.displayId = displayId;
    }
    
    /**
     * @return the primaryId
     */
    @JsonProperty(value = "primary_id", required = true)
    @ApiModelProperty(value = "Primary id", required = true)
    public String getPrimaryId() {
        return primaryId;
    }

    /**
     * @param primaryId the primaryId to set
     */
    public void setPrimaryId(String primaryId) {
        this.primaryId = primaryId;
    }

    /**
     * @return the version
     */
    @JsonProperty(value = "version", required = true)
    @ApiModelProperty(value = "Version", required = true)
    public String getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return the description
     */
    @JsonProperty(value = "description", required = true)
    @ApiModelProperty(value = "Description", required = true)
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the dbName
     */
    @JsonProperty(value = "dbname", required = true)
    @ApiModelProperty(value = "Database name", required = true)
    public String getDbName() {
        return dbName;
    }

    /**
     * @param dbName the dbName to set
     */
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    /**
     * @return the synonyms
     */
    @JsonProperty(value = "synonyms", required = true)
    @ApiModelProperty(value = "Synonyms", required = false)
    public List<String> getSynonyms() {
        return synonyms;
    }

    /**
     * @param synonyms the synonyms to set
     */
    public void setSynonyms(List<String> synonyms) {
        this.synonyms = synonyms;
    }

    /**
     * @return the infoText
     */
    @JsonProperty(value = "info_text", required = true)
    @ApiModelProperty(value = "Database info text", required = false)
    public String getInfoText() {
        return infoText;
    }

    /**
     * @param infoText the infoText to set
     */
    public void setInfoText(String infoText) {
        this.infoText = infoText;
    }

    /**
     * @return the infoType
     */
    @JsonProperty(value = "info_types", required = true)
    @ApiModelProperty(value = "Database info type", required = false)
    public String getInfoType() {
        return infoType;
    }

    /**
     * @param infoType the infoType to set
     */
    public void setInfoType(String infoType) {
        this.infoType = infoType;
    }

    /**
     * @return the dbDisplayName
     */
    @JsonProperty(value = "db_display_name", required = true)
    @ApiModelProperty(value = "Database display name", required = true)
    public String getDbDisplayName() {
        return dbDisplayName;
    }

    /**
     * @param dbDisplayName the dbDisplayName to set
     */
    public void setDbDisplayName(String dbDisplayName) {
        this.dbDisplayName = dbDisplayName;
    }

}

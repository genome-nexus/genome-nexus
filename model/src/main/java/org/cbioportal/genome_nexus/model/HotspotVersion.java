package org.cbioportal.genome_nexus.model;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Selects which cancer hotspots dataset version(s) to return.
 *
 * V2 means "only hotspots tagged version v2". V3 means "hotspots tagged v2 or v3"
 * (the v3 dataset is a cumulative superset of v2, not v3-only) — this asymmetry is
 * intentional, not a naming mistake.
 */
public enum HotspotVersion {
    V2("v2"),
    V3("v3");

    private final String value;

    HotspotVersion(String value) {
        this.value = value;
    }

    public static HotspotVersion fromString(String value) {
        for (HotspotVersion version : HotspotVersion.values()) {
            if (version.value.equalsIgnoreCase(value)) {
                return version;
            }
        }
        throw new IllegalArgumentException("Invalid value for HotspotVersion: " + value);
    }

    /**
     * Whether a hotspot's raw "version" field should be included when this
     * HotspotVersion was requested. A missing/null raw version (data imported
     * before the version column existed) is treated as implicit "v2" so legacy
     * databases keep returning all their data under a "v2" request instead of
     * silently going empty.
     */
    public boolean matches(String rawVersion) {
        if (this == V3) {
            return true;
        }
        // V2: null/missing raw version counts as legacy v2 data
        return rawVersion == null || "v2".equalsIgnoreCase(rawVersion);
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}

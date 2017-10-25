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

package org.cbioportal.genome_nexus.service.internal;

import org.cbioportal.genome_nexus.model.Hotspot;

import java.util.*;

/**
 * In-memory cache for hotspot mutations for better performance.
 *
 * @author Selcuk Onur Sumer
 */
public class HotspotCache
{
    private List<Hotspot> hotspots;
    private Map<String, List<Hotspot>> mapByTranscript;

    public HotspotCache()
    {
        this(null);
    }

    public HotspotCache(List<Hotspot> hotspots)
    {
        this.hotspots = hotspots;
        this.mapByTranscript = initMapByTranscript(hotspots);
    }

    public List<Hotspot> findByTranscriptId(String transcriptId)
    {
        List<Hotspot> hotspots = null;

        if (transcriptId != null)
        {
            hotspots = mapByTranscript.get(transcriptId.toLowerCase());
        }

        if (hotspots == null)
        {
            hotspots = Collections.emptyList();
        }

        return hotspots;
    }

    private Map<String, List<Hotspot>> initMapByTranscript(List<Hotspot> hotspots)
    {
        if (hotspots == null)
        {
            return Collections.emptyMap();
        }

        Map<String, List<Hotspot>> map = new LinkedHashMap<>();

        for (Hotspot hotspot : hotspots)
        {
            String transcriptId = hotspot.getTranscriptId();

            if (transcriptId != null)
            {
                List<Hotspot> list = map.get(transcriptId.toLowerCase());

                if (list == null)
                {
                    list = new LinkedList<>();
                    map.put(transcriptId.toLowerCase(), list);
                }

                list.add(hotspot);
            }
        }

        return map;
    }
}

package org.cbioportal.genome_nexus.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Common pipeline debug logger for AlphaGenome integration.
 * Routes through commons-logging / SLF4J.
 */
public class AlphaGenomePipelineDebugLogger
{
    private static final Log LOG = LogFactory.getLog(AlphaGenomePipelineDebugLogger.class);

    public static void log(String phase, String message)
    {
        LOG.info(String.format("[%s] %s", phase, message));
    }
}

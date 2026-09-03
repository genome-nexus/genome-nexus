package org.cbioportal.genome_nexus.service.internal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import javax.annotation.PreDestroy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Manages a long-running vibe-vep subprocess in streaming mode.
 *
 * The subprocess reads GenomicLocation JSON (one per line) from stdin
 * and writes VEP-compatible JSON (one per line) to stdout.
 *
 * Thread-safe: all interactions with the subprocess are synchronized.
 */
@Component
@ConditionalOnProperty(name = "vibe_vep.enabled", havingValue = "true")
public class VibeVepProcess {

    private static final Log LOG = LogFactory.getLog(VibeVepProcess.class);

    @Value("${vibe_vep.path:vibe-vep}")
    private String vibeVepPath;

    @Value("${vibe_vep.assembly:GRCh37}")
    private String assembly;

    private Process process;
    private BufferedWriter processStdin;
    private BufferedReader processStdout;

    /**
     * Send a single JSON line to the vibe-vep subprocess and read the result.
     *
     * @param inputJsonLine a single line of GenomicLocation JSON
     * @return the VEP-compatible JSON response line
     * @throws IOException if the subprocess cannot be started or communication fails
     */
    public synchronized String annotate(String inputJsonLine) throws IOException {
        ensureRunning();

        try {
            processStdin.write(inputJsonLine);
            processStdin.newLine();
            processStdin.flush();
        } catch (IOException e) {
            LOG.warn("Failed to write to vibe-vep process, restarting", e);
            destroyProcess();
            throw new IOException("vibe-vep write failed; will restart on next call", e);
        }

        String result = processStdout.readLine();
        if (result == null) {
            // Process died while we were waiting for output
            LOG.warn("vibe-vep process returned null (process likely crashed), restarting");
            destroyProcess();
            throw new IOException("vibe-vep process died unexpectedly; will restart on next call");
        }

        return result;
    }

    /**
     * Ensure the subprocess is running. Start or restart it if necessary.
     */
    private void ensureRunning() throws IOException {
        if (process != null && process.isAlive()) {
            return;
        }

        if (process != null) {
            LOG.warn("vibe-vep process is not alive, restarting");
            destroyProcess();
        }

        startProcess();
    }

    private void startProcess() throws IOException {
        LOG.info("Starting vibe-vep subprocess: " + vibeVepPath + " annotate stream --assembly " + assembly +
            " --input-format genome-nexus-genomic-location-jsonl --output-format ensembl-vep-jsonl");

        ProcessBuilder pb = new ProcessBuilder(
            vibeVepPath,
            "annotate", "stream",
            "--assembly", assembly,
            "--input-format", "genome-nexus-genomic-location-jsonl",
            "--output-format", "ensembl-vep-jsonl"
        );
        pb.redirectErrorStream(false);

        process = pb.start();
        processStdin = new BufferedWriter(new OutputStreamWriter(process.getOutputStream(), StandardCharsets.UTF_8));
        processStdout = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));

        // Start a thread to log stderr
        final Process proc = process;
        Thread stderrThread = new Thread(() -> {
            try (BufferedReader stderr = new BufferedReader(new InputStreamReader(proc.getErrorStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = stderr.readLine()) != null) {
                    LOG.warn("[vibe-vep stderr] " + line);
                }
            } catch (IOException e) {
                // Process ended, ignore
            }
        }, "vibe-vep-stderr");
        stderrThread.setDaemon(true);
        stderrThread.start();

        LOG.info("vibe-vep subprocess started successfully (pid: " + process.pid() + ")");
    }

    private void destroyProcess() {
        if (process != null) {
            try {
                processStdin.close();
            } catch (IOException e) {
                // ignore
            }
            try {
                processStdout.close();
            } catch (IOException e) {
                // ignore
            }
            process.destroyForcibly();
            process = null;
            processStdin = null;
            processStdout = null;
        }
    }

    @PreDestroy
    public void shutdown() {
        LOG.info("Shutting down vibe-vep subprocess");
        destroyProcess();
    }
}

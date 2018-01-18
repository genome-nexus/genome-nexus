package org.cbioportal.genome_nexus;

import java.io.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import org.cbioportal.genome_nexus.persistence.internal.EnsemblRepositoryImpl;


@Component
public class GenomeNexusImporter implements CommandLineRunner {
    private String mongoDBURI;
    @Value("${spring.data.mongodb.uri:}")
    public void setMongoDBURI(String mongoDBURI) { this.mongoDBURI =  mongoDBURI; }
    private String mongoDBImportCommandPrefix;
    @Value("${spring.data.mongodb.import.command.prefix:}")
    public void setMongoDBImportCommandPrefix(String mongoDBImportCommandPrefix) { this.mongoDBImportCommandPrefix =  mongoDBImportCommandPrefix; }

    protected final Log logger = LogFactory.getLog(getClass());
    final static String[][] IMPORT_RESOURCE_FILES = new String[][]{
        new String[]{"/ensembl_biomart_transcripts.json", EnsemblRepositoryImpl.TRANSCRIPTS_COLLECTION, "--type json"},
        new String[]{"/ensembl_biomart_canonical_transcripts_per_hgnc.txt", EnsemblRepositoryImpl.CANONICAL_TRANSCRIPTS_COLLECTION, "--type tsv --headerline"},
        new String[]{"/pfamA.txt", "pfam.domain", "--type tsv --headerline"},
    };

    /**
     * Export a resource embedded into a Jar file to the local file path.
     *
     * @param resourceName ie.: "/SmartLibrary.dll"
     * @return The path to the exported resource
     * @throws Exception
     */
    static public String exportResource(String resourceName) throws Exception {
        InputStream stream = null;
        OutputStream resStreamOut = null;
        File tempFile;
        try {
            // note that each / is a directory down in the "jar tree" been the jar the root of the tree
            stream = GenomeNexusAnnotation.class.getResourceAsStream(resourceName);
            if(stream == null) {
                throw new Exception("Cannot get resource \"" + resourceName + "\" from Jar file.");
            }

            int readBytes;
            byte[] buffer = new byte[4096];
            tempFile = File.createTempFile("exported_resource_", ".tmp");
            tempFile.deleteOnExit();
            resStreamOut = new FileOutputStream(tempFile);
            while ((readBytes = stream.read(buffer)) > 0) {
                resStreamOut.write(buffer, 0, readBytes);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            stream.close();
            resStreamOut.close();
        }

        return tempFile.getCanonicalPath();
    }

    private void importFile(String fileName, String collection, String extraOptions) throws Exception {
        if (mongoDBURI.length() > 0) {
            Runtime r = Runtime.getRuntime();
            Process p = null;

            ClassLoader classLoader = getClass().getClassLoader();

            try {
                File file = new File(GenomeNexusImporter.exportResource(fileName));
                logger.info("Importing " + fileName + " to mongodb collection " + collection + "...");

                String command;
                if (mongoDBImportCommandPrefix.length() > 0) {
                    command = mongoDBImportCommandPrefix;
                } else {
                    command = "mongoimport --uri " + mongoDBURI;
                }
                command += " --drop --collection " + collection + " " + extraOptions + " --file " + file.getAbsolutePath();
                logger.info("Command: " + command);
                p = r.exec(command);
                p.waitFor();
                BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(p.getInputStream()));

                BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(p.getErrorStream()));
                // read the output from the command

                logger.info("Here is the standard output of the command (if any) -->\n");
                String s = null;
                while ((s = stdInput.readLine()) != null) {
                    logger.info(s);
                }

                // read any errors from the attempted command
                logger.info("Here is the standard error of the command (if any) -->\n");
                while ((s = stdError.readLine()) != null) {
                    logger.info(s);
                }

                if (p.exitValue() == 0) {
                    logger.info("Import OK!");
                } else {
                    logger.error("Import of " + fileName + "Failed");
                }
            } catch (Exception e) {
                logger.error("Could not import " + fileName);
                logger.error(e.getMessage());
            }
        }
    }

    @Override
    public void run(String... args) throws Exception {
        for (String[] importToMongo : IMPORT_RESOURCE_FILES) {
            importFile(importToMongo[0], importToMongo[1], importToMongo[2]);
        }
    }

}

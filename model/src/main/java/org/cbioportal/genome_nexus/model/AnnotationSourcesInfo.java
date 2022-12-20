package org.cbioportal.genome_nexus.model;

import java.util.ArrayList;
import java.util.List;

public class AnnotationSourcesInfo {

    public List<SourceVersionInfo> mirroredAnnotationSources;
    public List<SourceVersionInfo> externalAnnotationSources;
    public List<SourceVersionInfo> vepAnnotationSourceVersion;

    public AnnotationSourcesInfo(List<SourceVersionInfo> sourceVersionInfos) {
        for (SourceVersionInfo sourceVersionInfo : sourceVersionInfos) {
            switch(sourceVersionInfo.type) {
                case "mirrored":
                    if (mirroredAnnotationSources == null) {
                        mirroredAnnotationSources = new ArrayList<>();
                    }
                    mirroredAnnotationSources.add(sourceVersionInfo);
                    break;
                case "external":
                    if (externalAnnotationSources == null) {
                        externalAnnotationSources = new ArrayList<>();
                    }
                    externalAnnotationSources.add(sourceVersionInfo);
                    break;
                case "vep":
                    if (vepAnnotationSourceVersion == null) {
                        vepAnnotationSourceVersion = new ArrayList<>();
                    }
                    vepAnnotationSourceVersion.add(sourceVersionInfo);
                    break;
                default:
            }
        }
    }
}

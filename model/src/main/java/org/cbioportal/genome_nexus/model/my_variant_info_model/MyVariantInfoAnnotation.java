package org.cbioportal.genome_nexus.model.my_variant_info_model;



public class MyVariantInfoAnnotation {

    private String license;
    private MyVariantInfo annotation;

    public MyVariantInfoAnnotation()
    {
        this.license = "http://www.apache.org/licenses/LICENSE-2.0";
        this.annotation = new MyVariantInfo();
    }

    public void setLicense(String license) { this.license = license; }

    public String getLicense() { return this.license; }

    public void setAnnotation(MyVariantInfo annotation) { this.annotation = annotation; }

    public MyVariantInfo getAnnotation() { return this.annotation; }

}

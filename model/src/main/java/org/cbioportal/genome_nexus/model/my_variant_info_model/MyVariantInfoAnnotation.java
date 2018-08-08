package org.cbioportal.genome_nexus.model.my_variant_info_model;

import org.springframework.data.mongodb.core.mapping.Field;

public class MyVariantInfoAnnotation
{

	@Field(value = "license")
	public String license;

	public String getLicense()
	{
		return license;
	}

	public void setLicense(String license)
	{
		this.license = license;
	}
	@Field(value = "annotation")
	public MyVariantInfo annotation;

	public MyVariantInfo getAnnotation()
	{
		return annotation;
	}

	public void setAnnotation(MyVariantInfo annotation)
	{
		this.annotation = annotation;
	}

}
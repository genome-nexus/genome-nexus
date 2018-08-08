package org.cbioportal.genome_nexus.service.enricher;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cbioportal.genome_nexus.model.my_variant_info_model.MyVariantInfo;
import org.cbioportal.genome_nexus.model.my_variant_info_model.MyVariantInfoAnnotation;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.AnnotationEnricher;
import org.cbioportal.genome_nexus.service.MyVariantInfoService;
import org.cbioportal.genome_nexus.service.exception.MyVariantInfoNotFoundException;
import org.cbioportal.genome_nexus.service.exception.MyVariantInfoWebServiceException;

public class MyVariantInfoAnnotationEnricher implements AnnotationEnricher
{
    private static final Log LOG = LogFactory.getLog(MyVariantInfoAnnotationEnricher.class);

    private MyVariantInfoService myVariantInfoService;

    public MyVariantInfoAnnotationEnricher(MyVariantInfoService myVariantInfoService) {
        this.myVariantInfoService = myVariantInfoService;
    }

    @Override
    public void enrich(VariantAnnotation annotation) {
        if (annotation != null)
        {
            MyVariantInfo myVariantInfo = null;

            try {
                myVariantInfo = myVariantInfoService.getMyVariantInfo(annotation);
            } catch (MyVariantInfoWebServiceException e) {
                LOG.warn(e.getLocalizedMessage());
            } catch (MyVariantInfoNotFoundException e) {
                // fail silently for this variant annotation
            }

            if (myVariantInfo != null)
            {
                MyVariantInfoAnnotation myVariantInfoAnnotation = new MyVariantInfoAnnotation();
                myVariantInfoAnnotation.setAnnotation(myVariantInfo);

                annotation.setMyVariantInfoAnnotation(myVariantInfoAnnotation);
            }
        }
    }

}

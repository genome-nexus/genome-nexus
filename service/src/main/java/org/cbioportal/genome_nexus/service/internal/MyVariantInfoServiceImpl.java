package org.cbioportal.genome_nexus.service.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cbioportal.genome_nexus.model.my_variant_info_model.MyVariantInfo;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.MyVariantInfoService;
import org.cbioportal.genome_nexus.service.cached.CachedMyVariantInfoFetcher;
import org.cbioportal.genome_nexus.service.exception.MyVariantInfoNotFoundException;
import org.cbioportal.genome_nexus.service.exception.MyVariantInfoWebServiceException;
import org.cbioportal.genome_nexus.service.exception.ResourceMappingException;
import org.cbioportal.genome_nexus.service.exception.VariantAnnotationNotFoundException;
import org.cbioportal.genome_nexus.service.exception.VariantAnnotationWebServiceException;
import org.cbioportal.genome_nexus.util.Hgvs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

@Service
public class MyVariantInfoServiceImpl implements MyVariantInfoService
{
    private static final Log LOG = LogFactory.getLog(MyVariantInfoServiceImpl.class);

    private final CachedMyVariantInfoFetcher cachedExternalResourceFetcher;

    @Autowired
    public MyVariantInfoServiceImpl(CachedMyVariantInfoFetcher cachedExternalResourceFetcher)
    {
        this.cachedExternalResourceFetcher = cachedExternalResourceFetcher;
    }

    /**
     * @param variant   hgvs variant (ex: 7:g.140453136A>T)
     */
    public MyVariantInfo getMyVariantInfo(String variant)
        throws VariantAnnotationNotFoundException, VariantAnnotationWebServiceException,
        MyVariantInfoWebServiceException, MyVariantInfoNotFoundException
    {
        return this.getMyVariantInfoByMyVariantInfoVariant(buildRequest(variant));
    }

    /**
     * @param variants   hgvs variants (ex: 7:g.140453136A>T)
     */
    public List<MyVariantInfo> getMyVariantInfo(List<String> variants)
    {
        List<MyVariantInfo> myVariantInfos = new ArrayList<>();

        for (String variant : variants)
        {
            try {
                myVariantInfos.add(this.getMyVariantInfoByMyVariantInfoVariant(buildRequest(variant)));
            } catch (MyVariantInfoWebServiceException e) {
                LOG.warn(e.getLocalizedMessage());
            } catch (MyVariantInfoNotFoundException e) {
                // fail silently for this variant
            }
        }

        return myVariantInfos;
    }

    public MyVariantInfo getMyVariantInfo(VariantAnnotation annotation)
        throws MyVariantInfoNotFoundException, MyVariantInfoWebServiceException
    {
        // get hgvsg from VEP (ID might not be in hgvsg format)
        String hgvsg = annotation.getHgvsg();
        if (hgvsg != null)
        {
            MyVariantInfo myVariantInfo = this.getMyVariantInfoByMyVariantInfoVariant(buildRequest(hgvsg));

            // add original hgvsg variant value too
            myVariantInfo.setHgvs(hgvsg);

            return myVariantInfo;
        }
        else {
            return null;
        }
    }

    /**
     * @param variant my varint info variant (ex: 1:g.35367G>A)
     */
    public MyVariantInfo getMyVariantInfoByMyVariantInfoVariant(String variant)
        throws MyVariantInfoNotFoundException, MyVariantInfoWebServiceException
    {
        Optional<MyVariantInfo> myVariantInfo = null;

        try {
            // get the annotation from the web service and save it to the DB
            myVariantInfo = Optional.ofNullable(cachedExternalResourceFetcher.fetchAndCache(variant));
        } catch (ResourceMappingException e) {
            throw new MyVariantInfoWebServiceException(e.getMessage());
        } catch (HttpClientErrorException e) {
            throw new MyVariantInfoWebServiceException(e.getResponseBodyAsString(), e.getStatusCode());
        } catch (ResourceAccessException e) {
            throw new MyVariantInfoWebServiceException(e.getMessage());
        }

        try {
            return myVariantInfo.get();
        } catch (NoSuchElementException e) {
            throw new MyVariantInfoNotFoundException(variant);
        }
    }

    private MyVariantInfo getMyVariantInfoByVariantAnnotation(VariantAnnotation variantAnnotation)
        throws MyVariantInfoWebServiceException, MyVariantInfoNotFoundException
    {
        MyVariantInfo myVariantInfoObj = this.getMyVariantInfo(variantAnnotation);

        if (myVariantInfoObj != null)
        {
            return myVariantInfoObj;
        }
        else {
            throw new MyVariantInfoNotFoundException(variantAnnotation.getVariant());
        }
    }

    private String buildRequest(String variant)
    {   
        return Hgvs.addChrPrefix(Hgvs.removeDeletedBases(variant));
    }

}
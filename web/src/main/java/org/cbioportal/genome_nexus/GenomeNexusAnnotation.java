/*
 * Copyright (c) 2015 Memorial Sloan-Kettering Cancer Center.
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

package org.cbioportal.genome_nexus;

import org.cbioportal.genome_nexus.web.config.ApiObjectMapper;
import org.cbioportal.genome_nexus.web.config.InternalApi;
import org.cbioportal.genome_nexus.web.config.PublicApi;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.Arrays;
import java.util.HashSet;

/**
 * @author Benjamin Gross
 */
@SpringBootApplication(scanBasePackages = "org.cbioportal.genome_nexus") // shorthand for @Configuration, @EnableAutoConfiguration, @ComponentScan
@EnableSwagger2 // enable swagger2 documentation
public class GenomeNexusAnnotation extends SpringBootServletInitializer
{
    public static void main(String[] args)
    {
        SpringApplication.run(GenomeNexusAnnotation.class, args);
    }

    @Bean
    public HandlerExceptionResolver sentryExceptionResolver() {
        return new io.sentry.spring.SentryExceptionResolver();
    }

    @Bean
    public Docket publicApi() {
        // default swagger definition file location: <root>/v2/api-docs?group=variant_annotation
        // default swagger UI location: <root>/swagger-ui.html
        return new Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.withClassAnnotation(PublicApi.class))
            .build()
            .useDefaultResponseMessages(false)
            .protocols(new HashSet<>(Arrays.asList("http", "https")))
            .apiInfo(apiInfo());
    }

    @Bean
    public Docket internalApi() {
        return new Docket(DocumentationType.SWAGGER_2).groupName("internal")
            .select()
            .apis(RequestHandlerSelectors.withClassAnnotation(InternalApi.class))
            .build()
            .useDefaultResponseMessages(false)
            .protocols(new HashSet<>(Arrays.asList("http", "https")))
            .apiInfo(apiInfo());
    }

    @Bean
    public MappingJackson2HttpMessageConverter messageConverter() {
        return new MappingJackson2HttpMessageConverter(new ApiObjectMapper());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
            .title("Genome Nexus API")
            .description("Genome Nexus Variant Annotation API")
            //.termsOfServiceUrl("http://terms-of-service-url")
            .contact("CMO, MSKCC")
            .license("GNU AFFERO GENERAL PUBLIC LICENSE Version 3")
            .licenseUrl("https://github.com/cBioPortal/genome-nexus/blob/master/LICENSE")
            .version("2.0")
            .build();
    }

}

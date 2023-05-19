package org.cbioportal.genome_nexus.web.config;
import org.cbioportal.genome_nexus.web.converters.AnnotationFieldEnumConverter;
import org.springframework.boot.autoconfigure.web.format.WebConversionService;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class AppWebMvcConfigurer implements WebMvcConfigurer {
    /**
     * Add custom converters to the default WebConversionService.
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new AnnotationFieldEnumConverter());
        WebConversionService.addDefaultFormatters(registry);
    }
}

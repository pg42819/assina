package eu.assina.app.spring;

import eu.assina.app.model.AssinaCredential;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.mapping.ExposureConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

//@Configuration
public class SpringDataRestConfig {//implements RepositoryRestConfigurer {
//    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration restConfig, CorsRegistry cors) {
        // disable the hyperlinks in responses (ie disable HATEOAS)
        restConfig.useHalAsDefaultJsonMediaType(false);
        // disable the default patch
        ExposureConfiguration config = restConfig.getExposureConfiguration();
        // Disable patch for the time being - we have no need for it
        config.disablePatchOnItemResources();

        // Disable put for creation - only post should create new credentials
        config.disablePutForCreation();
        // disable patch  - there is no PATCH in the CSC spec
        config.forDomainType(AssinaCredential.class).withItemExposure((metadata, httpMethods) ->
                httpMethods.disable(HttpMethod.PATCH));
    }
}

package eu.assina.app.common.config;

import eu.assina.app.common.util.Constants;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final long MAX_AGE_SECS = 3600;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // TODO generalize localhost for client
        .allowedOrigins("http://localhost:3000")
//            .allowedOrigins("*")
        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        .allowedHeaders("*")
        .allowCredentials(true)
        .maxAge(MAX_AGE_SECS);
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix(Constants.API_URL_ROOT,
                HandlerTypePredicate.forAnnotation(RestController.class)
                        .and(HandlerTypePredicate.forBasePackage(
                                        "eu.assina.app.api")));
        configurer.addPathPrefix(Constants.CSC_URL_ROOT,
                HandlerTypePredicate.forAnnotation(RestController.class)
                        .and(HandlerTypePredicate.forBasePackage(
                                "eu.assina.app.csc")));
    }
}

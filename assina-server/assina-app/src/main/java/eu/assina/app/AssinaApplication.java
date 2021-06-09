package eu.assina.app;

import eu.assina.app.config.AppProperties;
import eu.assina.app.config.CSCProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/** Main Spring Boot application class for Assina application */
@SpringBootApplication
@EnableConfigurationProperties({AppProperties.class, CSCProperties.class})
public class AssinaApplication {

	// TODO add the root context for csc/v1 per the spec
	public static void main(String[] args) {
		SpringApplication.run(AssinaApplication.class, args);
	}

}

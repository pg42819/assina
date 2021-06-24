package eu.assina.rssp;

import eu.assina.rssp.common.config.AppProperties;
import eu.assina.rssp.common.config.CSCProperties;
import eu.assina.rssp.common.config.DemoProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/** Main Spring Boot application class for Assina application */
@SpringBootApplication(scanBasePackages = "eu.assina.rssp")
@EnableConfigurationProperties({AppProperties.class, CSCProperties.class, DemoProperties.class})
public class AssinaRSSPApplication {

	// TODO add the root context for csc/v1 per the spec
	public static void main(String[] args) {
		SpringApplication.run(AssinaRSSPApplication.class, args);
	}

}

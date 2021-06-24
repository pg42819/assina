package eu.assina.sa;

import eu.assina.sa.config.FileStorageConfig;
import eu.assina.sa.config.RSSPClientConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/** Main Spring Boot application class for Assina application */
@EnableConfigurationProperties({FileStorageConfig.class, RSSPClientConfig.class})
// disable security on the Signing App - all security is on the RSSP
@SpringBootApplication(scanBasePackages = "eu.assina.sa", exclude = SecurityAutoConfiguration.class)
public class AssinaSigningApplication {

	public static void main(String[] args) {
		SpringApplication.run(AssinaSigningApplication.class, args);
	}
}

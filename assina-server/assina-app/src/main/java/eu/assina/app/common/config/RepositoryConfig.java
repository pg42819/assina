package eu.assina.app.common.config;

import eu.assina.app.api.services.CredentialService;
import eu.assina.app.api.model.AuthProvider;
import eu.assina.app.api.model.RoleName;
import eu.assina.app.api.model.User;
import eu.assina.app.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
class RepositoryConfig {
	private static final Logger log = LoggerFactory.getLogger(RepositoryConfig.class);

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Bean
	CommandLineRunner initRepository(UserRepository userRepo, DemoProperties demoProperties,
									 CredentialService credentialService) {
		return args -> {
			if (userRepo.count() == 0) {
				User admin = new User("admin", "admin", "admin@assina.eu", AuthProvider.local);
				admin.setRole(RoleName.ROLE_ADMIN.name());
				// TODO admin password should be a setup feature - document this
				admin.setPassword(passwordEncoder.encode("admin"));
				userRepo.save(admin);
				log.info("Added admin user {}", admin.getName());
				User bob = new User("bob", "bob", "bob@assina.eu", AuthProvider.local);
				bob.setRole(RoleName.ROLE_USER.name());
				bob.setPassword(passwordEncoder.encode("bob"));
				userRepo.save(bob);
				log.info("Added regular user {}", bob.getName());
			}

			final List<DemoProperties.DemoUser> demoUsers = demoProperties.getUsers();
			if (demoUsers != null) {
				for (DemoProperties.DemoUser demoUser : demoUsers) {
					// allow for additional users without wiping old ones from last startup
					if (userRepo.existsByUsername(demoUser.getUsername())) {
						log.debug("Found demo user {} in database at startup", demoUser.getName());
					} else {
						User user = new User(demoUser.getUsername(), demoUser.getName(), demoUser.getEmail(), AuthProvider.local);
						user.setEmail(demoUser.getEmail());
						user.setPassword(passwordEncoder.encode(demoUser.getPlainPassword()));
						user.setRole(demoUser.getRole());
						userRepo.save(user);
						log.info("Added demo user {} with role: {}", demoUser.getName(), demoUser.getRole());
						for (int i = 0; i < demoUser.getNumCredentials(); i++) {
						    // add credentials for the demo user
							credentialService.createCredential(user.getId(), user.getUsername());
							log.info("Created demo credential for user {}", user.getUsername());
						}
					}
				}
			}
		};
	}
}

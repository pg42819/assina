package eu.assina.app.common.config;

import eu.assina.app.model.AuthProvider;
import eu.assina.app.model.RoleName;
import eu.assina.app.model.User;
import eu.assina.app.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
class RepositoryConfig {
	private static final Logger log = LoggerFactory.getLogger(RepositoryConfig.class);

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Bean
	CommandLineRunner initRepository(UserRepository userRepo) {
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
		};
	}
}

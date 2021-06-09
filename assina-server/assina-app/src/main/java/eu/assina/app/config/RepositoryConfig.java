package eu.assina.app.config;

import eu.assina.app.model.AuthProvider;
import eu.assina.app.model.Role;
import eu.assina.app.model.User;
import eu.assina.app.repository.RoleRepository;
import eu.assina.app.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Collections;

@Configuration
class RepositoryConfig {
	private static final Logger log = LoggerFactory.getLogger(RepositoryConfig.class);

	@Bean
	CommandLineRunner initRepository(UserRepository userRepo, RoleRepository roleRepo) {
		return args -> {
			if (roleRepo.count() == 0) {
				roleRepo.save(new Role("admin"));
				roleRepo.save(new Role("user"));
			}
			Role adminRole = roleRepo.findByName("admin");
			Role userRole = roleRepo.findByName("user");
			if (userRepo.count() == 0) {
				User admin = new User("admin", "admin@assina.eu", AuthProvider.local);
				admin.setRoles(Arrays.asList(adminRole, userRole));
				admin.setPassword("admin");
				userRepo.save(admin);
				log.info("Added admin user {}", admin.getName());
				User bob = new User("bob", "bob@assina.eu", AuthProvider.local);
				bob.setRoles(Collections.singletonList(userRole));
				bob.setPassword("bob");
				userRepo.save(bob);
				log.info("Added regular user {}", bob.getName());
			}
		};
	}
}

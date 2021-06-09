package eu.assina.app.repository;

import eu.assina.app.model.Role;
import org.springframework.data.repository.CrudRepository;

public interface RoleRepository extends CrudRepository<Role, String> {
  Role findByName(String name);
}

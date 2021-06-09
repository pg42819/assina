package eu.assina.app.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "roles")
public class Role {
	@Id
	@Column(name = "role_id")
  private String id;

	private String name;

	public Role(String name) {
		id = UUID.randomUUID().toString();
		this.name = name;
	}

	public Role() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

}

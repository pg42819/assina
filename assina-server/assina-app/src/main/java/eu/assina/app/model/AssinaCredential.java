package eu.assina.app.model;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.security.KeyPair;
import java.security.cert.Certificate;
import java.util.*;

/**
 * Represents the credential object from the CSC Spec v 1.0.4.0:
 *    credential: cryptographic object and related data used to support remote digital signatures over the Internet.
 *    Consists of the combination of a public/private key pair (also named “signing key” in CEN EN 419 241-1 [i.5])
 *    and a X.509 public key certificate managed by a remote signing service provider on behalf of a user.
 */
@Entity
public class AssinaCredential
{
	@Id
	private String id;

	private String owner;
	private String description;

	@Lob
	private Certificate certificate;

	@Lob
	private KeyPair keyPair;

//	@dddElementCollection
//	private List<String> keys = new ArrayList<>();
//
	public AssinaCredential()
	{
		id = UUID.randomUUID().toString();
	}

	public String getId()
	{
		return id;
	}

	public void setId(final String id)
	{
		this.id = id;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(final String description)
	{
		this.description = description;
	}

	public String getOwner()
	{
		return owner;
	}

	public void setOwner(String owner)
	{
		this.owner = owner;
	}

	public Certificate getCertificate()
	{
		return certificate;
	}

	public void setCertificate(Certificate certificate)
	{
		this.certificate = certificate;
	}

	public KeyPair getKeyPair()
	{
		return keyPair;
	}

	public void setKeyPair(KeyPair keyPair)
	{
		this.keyPair = keyPair;
	}
}


package eu.assina.app.model;

import javax.persistence.*;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
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

	/** store the public key separately from the private key */
	private PublicKey publicKey;

	/** store the private key separately and encrypt it */
	@Convert(converter = PrivateKeyConverter.class)
	private PrivateKey privateKey;

	@Lob
	private Certificate certificate;

	/** Mark keyPair transient - we build it from separate private and public key columns */
	@Transient
	private KeyPair keyPair;

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
		if (keyPair == null) {
			keyPair = new KeyPair(this.publicKey, this.privateKey);
		}
		return keyPair;
	}

	public void setKeyPair(KeyPair keyPair)
	{
		// the keypair is transient, but the private and public are stored
		this.keyPair = keyPair;
		this.privateKey = keyPair.getPrivate(); // this is encrypted by the converter
		this.publicKey = keyPair.getPublic();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		AssinaCredential that = (AssinaCredential) o;
		return Objects.equals(owner, that.owner) &&
							 Objects.equals(certificate, that.certificate) &&
							 Objects.equals(keyPair.getPrivate(), that.keyPair.getPrivate()) &&
							 Objects.equals(keyPair.getPublic(), that.keyPair.getPublic());
	}

	@Override
	public int hashCode() {
		return Objects.hash(owner, certificate, keyPair);
	}
}


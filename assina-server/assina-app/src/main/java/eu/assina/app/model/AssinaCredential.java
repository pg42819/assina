package eu.assina.app.model;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents the credential object from the CSC Spec v 1.0.4.0:
 *    credential: cryptographic object and related data used to support remote digital signatures over the Internet.
 *    Consists of the combination of a public/private key pair (also named “signing key” in CEN EN 419 241-1 [i.5])
 *    and a X.509 public key certificate managed by a remote signing service provider on behalf of a user.
 */
@Entity
public class AssinaCredential extends DateAudit
{
	@Id
	private String id;

	private String owner;
	private String description;

	/** store the public key separately from the private key */
	@Convert(converter = PublicKeyConverter.class)
	private PublicKey publicKey;

	/** store the private key separately and encrypt it */
	@Convert(converter = PrivateKeyConverter.class)
	private PrivateKey privateKey;

	@Lob
	private Certificate certificate;

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

	public PublicKey getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(PublicKey publicKey) {
		this.publicKey = publicKey;
	}

	public PrivateKey getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(PrivateKey privateKey) {
		this.privateKey = privateKey;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		AssinaCredential that = (AssinaCredential) o;
		return Objects.equals(owner, that.owner) &&
							 Objects.equals(certificate, that.certificate) &&
							 Objects.equals(publicKey, that.publicKey);
	}

	@Override
	public int hashCode() {
		return Objects.hash(owner, certificate, publicKey);
	}
}


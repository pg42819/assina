package eu.assina.app.api.services;

import eu.assina.app.error.AssinaError;
import eu.assina.app.error.AssinaException;
import eu.assina.app.api.model.AssinaCredential;
import eu.assina.app.repository.CredentialRepository;
import eu.assina.crypto.cert.CertificateGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.security.cert.Certificate;
import java.util.Optional;

@Service
public class CredentialService
{
	CertificateGenerator generator;
	private CredentialRepository credentialRepository;

	@Autowired
	public CredentialService(CredentialRepository credentialRepository)
	{
		this.credentialRepository = credentialRepository;
		generator = new CertificateGenerator();
	}

	/**
	 * Creates a new certificate and keypair combinations and stores them in the repository.
	 * The private key is always stored encrypted.
	 *
	 * @param owner
	 * @param subjectDistinguishedName name of the subject used in the certificate
	 * @return
	 */
	public AssinaCredential createCredential(String owner, String subjectDistinguishedName)
	{
		try {
			final KeyPair keyPair = generator.generateKeyPair();
			final Certificate selfSignedCert = generator.createSelfSignedCert(keyPair, subjectDistinguishedName);
			AssinaCredential credential = new AssinaCredential();
			credential.setOwner(owner);
			credential.setCertificate(selfSignedCert);
			credential.setPrivateKey(keyPair.getPrivate());
			credential.setPublicKey(keyPair.getPublic());
			credentialRepository.save(credential);
			return credential;
		}
		catch (Exception e) {
			throw new AssinaException("Problem generating key pair and cert", e, AssinaError.UnexpectedError);
		}
	}

	/**
	 * Gets credentials for the specified owner a page at a time
	 * @param owner user who owns the credentials (also the subject of the cert
	 * @param pageable
	 * @return
	 */
	public Page<AssinaCredential> getCredentialsByOwner(String owner, Pageable pageable)
	{
		Page<AssinaCredential> byOwner = credentialRepository.findByOwner(owner, pageable);
		return byOwner;
	}

	/**
	 * Gets count of credentials by this owner
	 * @param owner user who owns the credentials (also the subject of the cert
	 * @return
	 */
	public long countCredentialsByOwner(String owner)
	{
		long count = credentialRepository.countByOwner(owner);
		return count;
	}

	/**
	 * Gets credentials for all users a page at a time
	 * @param pageable
	 */
	public Page<AssinaCredential> getCredentials(Pageable pageable)
	{
		Page<AssinaCredential> credentials = credentialRepository.findAll(pageable);
		return credentials;
	}

	public Optional<AssinaCredential> getCredentialWithId(String id) {
		try {
			return credentialRepository.findById(id);
		}
		catch (EmptyResultDataAccessException ex) {
			throw new AssinaException("Attempted to get credentials that do not exist",
					id, AssinaError.CredentialNotFound);
		}
	}

	/**
	 * Deletes the credentials with the specified Id
	 *
	 * @param id id of the credentials to be deleted
	 */
	public void deleteCredentials(String id) {
		try {
			credentialRepository.deleteById(id);
		}
		catch (EmptyResultDataAccessException ex) {
			throw new AssinaException("Attempted to delete credentials that do not exist",
					id, AssinaError.CredentialNotFound);
		}
	}
}

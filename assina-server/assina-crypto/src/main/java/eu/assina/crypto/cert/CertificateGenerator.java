package eu.assina.crypto.cert;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStore.Entry;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.UnrecoverableEntryException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Calendar;
import java.util.Date;

// https://stackoverflow.com/questions/29852290/self-signed-x509-certificate-with-bouncy-castle-in-java
// https://stackoverflow.com/questions/43960761/how-to-store-and-reuse-keypair-in-java/43965528#43965528

// TODO open up to configs
// Store configs in spring boot and pass data objects here
public class CertificateGenerator
{
	public KeyPair generateKeyPair() throws Exception
	{
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		generator.initialize(2048, new SecureRandom());
		KeyPair pair = generator.generateKeyPair();
		return pair;
	}

	public Certificate createSelfSignedCert(KeyPair keyPair, String subjectDN)
			throws OperatorCreationException, CertificateException, IOException
	{
		Provider bcProvider = new BouncyCastleProvider();
		Security.addProvider(bcProvider);

		long now = System.currentTimeMillis();
		Date startDate = new Date(now);

		X500Name dnName = new X500Name("CN=" + subjectDN);
		BigInteger certSerialNumber = new BigInteger(Long.toString(now)); // <-- Using the current timestamp as the certificate serial number

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startDate);
		calendar.add(Calendar.YEAR, 1); // <-- 1 Yr validity

		Date endDate = calendar.getTime();

		String signatureAlgorithm = "SHA256WithRSA"; // <-- Use appropriate signature algorithm based on your keyPair algorithm.

		ContentSigner contentSigner = new JcaContentSignerBuilder(signatureAlgorithm).build(keyPair.getPrivate());

		JcaX509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(dnName, certSerialNumber, startDate, endDate, dnName, keyPair.getPublic());

		// Extensions --------------------------

		// Basic Constraints
		BasicConstraints basicConstraints = new BasicConstraints(true); // <-- true for CA, false for EndEntity

		certBuilder.addExtension(new ASN1ObjectIdentifier("2.5.29.19"), true, basicConstraints); // Basic Constraints is usually marked as critical.

		// -------------------------------------

		return new JcaX509CertificateConverter().setProvider(bcProvider).getCertificate(certBuilder.build(contentSigner));
	}

	public static void main(String[] args) throws Exception {
		KeyPair generatedKeyPair = new CertificateGenerator().generateKeyPair();

		String filename = "test_gen_self_signed.pkcs12";
		char[] password = "test".toCharArray();

		storeToPKCS12(filename, password, generatedKeyPair);

		KeyPair retrievedKeyPair = loadFromPKCS12(filename, password);

		// you can validate by generating a signature and verifying it or by
		// comparing the moduli by first casting to RSAPublicKey, e.g.:

		RSAPublicKey pubKey = (RSAPublicKey)generatedKeyPair.getPublic();
		RSAPrivateKey privKey = (RSAPrivateKey) retrievedKeyPair.getPrivate();
		System.out.println(pubKey.getModulus().equals(privKey.getModulus()));
	}

	private static KeyPair loadFromPKCS12(String filename, char[] password)
			throws KeyStoreException, NoSuchAlgorithmException, CertificateException,
			FileNotFoundException, IOException, UnrecoverableEntryException {
		KeyStore pkcs12KeyStore = KeyStore.getInstance("PKCS12");

		try (FileInputStream fis = new FileInputStream(filename);) {
			pkcs12KeyStore.load(fis, password);
		}

		KeyStore.ProtectionParameter param = new KeyStore.PasswordProtection(password);
		Entry entry = pkcs12KeyStore.getEntry("owlstead", param);
		if (!(entry instanceof PrivateKeyEntry)) {
			throw new KeyStoreException("That's not a private key!");
		}
		PrivateKeyEntry privKeyEntry = (PrivateKeyEntry) entry;
		PublicKey publicKey = privKeyEntry.getCertificate().getPublicKey();
		PrivateKey privateKey = privKeyEntry.getPrivateKey();
		return new KeyPair(publicKey, privateKey);
	}

	private static void storeToPKCS12(
			String filename, char[] password,
			KeyPair generatedKeyPair) throws KeyStoreException, IOException,
			NoSuchAlgorithmException, CertificateException, FileNotFoundException,
			OperatorCreationException {

		Certificate selfSignedCertificate = new CertificateGenerator().createSelfSignedCert(generatedKeyPair, "CN=owlstead");

		KeyStore pkcs12KeyStore = KeyStore.getInstance("PKCS12");
		pkcs12KeyStore.load(null, null);

		KeyStore.Entry entry = new PrivateKeyEntry(generatedKeyPair.getPrivate(),
												   new Certificate[] { selfSignedCertificate });
		KeyStore.ProtectionParameter param = new KeyStore.PasswordProtection(password);

		pkcs12KeyStore.setEntry("owlstead", entry, param);

		try (FileOutputStream fos = new FileOutputStream(filename)) {
			pkcs12KeyStore.store(fos, password);
		}
	}
}

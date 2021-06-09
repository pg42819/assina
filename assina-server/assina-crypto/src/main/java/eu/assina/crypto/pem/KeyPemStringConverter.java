package eu.assina.crypto.pem;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMEncryptor;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.bc.BcPEMDecryptorProvider;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.openssl.jcajce.JcePEMEncryptorBuilder;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.pkcs.PKCSException;
import org.bouncycastle.pkcs.jcajce.JcePKCSPBEInputDecryptorProviderBuilder;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;

public class KeyPemStringConverter {

    private PEMEncryptor pemEncryptor = null;
    private InputDecryptorProvider inputDecryptorProvider = null;
    private BcPEMDecryptorProvider bcPEMDecryptorProvider = null;
    private boolean encryptionEnabled = false;

    public KeyPemStringConverter(char[] passPhraseChars) {
        // Make sure the BC Provider is available
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        if (passPhraseChars != null && passPhraseChars.length > 0) {
            pemEncryptor = (new JcePEMEncryptorBuilder("AES-256-CFB")).build(passPhraseChars);
            JcePKCSPBEInputDecryptorProviderBuilder builder
                    = new JcePKCSPBEInputDecryptorProviderBuilder().setProvider("BC");
            inputDecryptorProvider = builder.build(passPhraseChars);
            bcPEMDecryptorProvider = new BcPEMDecryptorProvider(passPhraseChars);
            encryptionEnabled = true;
        }
    }

    public KeyPemStringConverter() {
        this(null);
    }

    public String publicKeyToString(PublicKey publicKey) throws IOException {
        try (StringWriter sw = new StringWriter();
             JcaPEMWriter pemWriter = new JcaPEMWriter(sw)) {
            pemWriter.writeObject(publicKey);
            pemWriter.flush();
            return sw.toString();
        }
    }

    public PublicKey stringToPublicKey(String keyString) throws IOException {
        try (StringReader keyReader = new StringReader(keyString);
            PEMParser pemParser = new PEMParser(keyReader)) {
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            SubjectPublicKeyInfo publicKeyInfo = SubjectPublicKeyInfo.getInstance(pemParser.readObject());
            return converter.getPublicKey(publicKeyInfo);
        }
    }

    public String privateKeyToString(PrivateKey privateKey)
            throws IOException
    {
        try (StringWriter sw = new StringWriter();
        JcaPEMWriter pemWriter = new JcaPEMWriter(sw)) {
            if (pemEncryptor != null) {
                pemWriter.writeObject(privateKey, pemEncryptor);
            }
            else {
                pemWriter.writeObject(privateKey);
            }

            pemWriter.flush();
            pemWriter.close();
            return sw.toString();
        }
    }

    public PrivateKey stringToPrivateKey(String privateKeyPemString)
    {
        PrivateKeyInfo pki;
        try (PEMParser pemParser = new PEMParser(new StringReader(privateKeyPemString))) {
            Object o = pemParser.readObject();
            if (o instanceof PKCS8EncryptedPrivateKeyInfo) {
                if (!encryptionEnabled) {
                    throw new IllegalStateException("Expected pass-phrase to decrypt the private key");
                }
                PKCS8EncryptedPrivateKeyInfo epki = (PKCS8EncryptedPrivateKeyInfo) o;
                pki = epki.decryptPrivateKeyInfo(inputDecryptorProvider);
            }
            else if (o instanceof PEMEncryptedKeyPair) {
                if (!encryptionEnabled) {
                    throw new IllegalStateException("Expected pass-phrase to decrypt the private key");
                }
                PEMEncryptedKeyPair epki = (PEMEncryptedKeyPair) o;
                PEMKeyPair pkp = epki.decryptKeyPair(bcPEMDecryptorProvider);
                pki = pkp.getPrivateKeyInfo();
            }
            else if (o instanceof PEMKeyPair) {
                if (encryptionEnabled) {
                    throw new IllegalStateException("Expected private key to be encrypted!");
                }
                pki = ((PEMKeyPair)o).getPrivateKeyInfo();
            } else {
                throw new IllegalArgumentException("Invalid encrypted private key class: " + o.getClass().getName());
            }

            JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
            return converter.getPrivateKey(pki);
        } catch (IOException e) {
            // no reason this should ever occur so wrap it in a runtime exception
            throw new IllegalArgumentException(e);
        } catch (PKCSException e) {
            throw new IllegalArgumentException("Invalid private key PEM string or password", e);
        }
    }
}

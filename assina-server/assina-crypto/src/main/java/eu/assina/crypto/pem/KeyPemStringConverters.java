package eu.assina.crypto.pem;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.openssl.*;
import org.bouncycastle.openssl.bc.BcPEMDecryptorProvider;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;
import org.bouncycastle.openssl.jcajce.JcePEMEncryptorBuilder;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.pkcs.PKCSException;
import org.bouncycastle.pkcs.jcajce.JcePKCSPBEInputDecryptorProviderBuilder;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;

import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

public class KeyPemStringConverters {

    public KeyPemStringConverters() {
    }

    public static PublicKey stringToPublicKey(String keyString) throws IOException {
        try (StringReader keyReader = new StringReader(keyString)) {

            PEMParser pemParser = new PEMParser(keyReader);
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            SubjectPublicKeyInfo publicKeyInfo = SubjectPublicKeyInfo.getInstance(pemParser.readObject());

            return converter.getPublicKey(publicKeyInfo);
        }
    }

//    public static RSAPrivateKey stringToPrivateKey(String file) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
//        KeyFactory factory = KeyFactory.getInstance("RSA");
//        try (StringReader keyReader = new StringReader(file);
//             PemReader pemReader = new PemReader(keyReader)) {
//
//            PemObject pemObject = pemReader.readPemObject();
//            byte[] content = pemObject.getContent();
//            PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(content);
//            return (RSAPrivateKey) factory.generatePrivate(privKeySpec);
//        }
//    }

//    public static RSAPrivateKey stringToPrivateKey2(String file) throws IOException {
//        try (StringReader keyReader = new StringReader(file)) {
//
//            PEMParser pemParser = new PEMParser(keyReader);
//            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
//            PrivateKeyInfo privateKeyInfo = PrivateKeyInfo.getInstance(pemParser.readObject());
//
//            return (RSAPrivateKey) converter.getPrivateKey(privateKeyInfo);
//        }
//    }

    public static String privateKeyToString(PrivateKey privateKey) throws IOException
    {
        StringWriter sw = new StringWriter();
        JcaPEMWriter pemWriter = new JcaPEMWriter(sw);
        pemWriter.writeObject(privateKey);
        pemWriter.flush();
        pemWriter.close();
        return sw.toString();
    }

    public static String privateKeyToEncryptedString(PrivateKey privateKey, char[] passwordChars)
            throws IOException
    {
        StringWriter sw = new StringWriter();
        PEMEncryptor pemEncryptor = (new JcePEMEncryptorBuilder("AES-256-CFB")).build(passwordChars);
        JcaPEMWriter pemWriter = new JcaPEMWriter(sw);
        pemWriter.writeObject(privateKey, pemEncryptor);
        pemWriter.flush();
        pemWriter.close();
        return sw.toString();
    }

//    public static PrivateKey stringToPrivateKey(String privateKeyFileName, char [] password) {
//        File privateKeyFile = new File(privateKeyFileName); // private key file in PEM format
//        PEMParser pemParser = new PEMParser(new FileReader(privateKeyFile));
//        Object object = pemParser.readObject();
////        PEMDecryptorProvider decProv = new JcePEMDecryptorProviderBuilder().build(password);
//        JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
//        KeyPair kp;
//        if (object instanceof PEMEncryptedKeyPair) {
//            System.out.println("Encrypted key - we will use provided password");
//            kp = converter.getKeyPair(((PEMEncryptedKeyPair) object).decryptKeyPair(decProv));
//        } else {
//            System.out.println("Unencrypted key - no password needed");
//            kp = converter.getKeyPair((PEMKeyPair) object);
//        }

//    }

    public static String publicKeyToString(PublicKey publicKey) throws IOException {
        StringWriter sw = new StringWriter();
        JcaPEMWriter pemWriter = new JcaPEMWriter(sw);
        pemWriter.writeObject(publicKey);
        pemWriter.flush();
        pemWriter.close();
        return sw.toString();
    }
    // https://stackoverflow.com/questions/25129822/export-rsa-public-key-to-pem-string-using-java

    public static String publicKeyToString3(RSAPublicKey publicKey) throws IOException {
        StringWriter sw = new StringWriter();
        PemWriter pemWriter = new PemWriter(sw);
        pemWriter.writeObject(new PemObject("PUBLIC KEY", publicKey.getEncoded()));
        pemWriter.flush();
        pemWriter.close();
        return sw.toString();
    }

    // https://stackoverflow.com/questions/22920131/read-an-encrypted-private-key-with-bouncycastle-spongycastle
    private static PrivateKey stringToPrivateKeyImpl(String privateKeyPemString, char[] passwordChars) {
//        https://stackoverflow.com/questions/3711754/why-java-security-nosuchproviderexception-no-such-provider-bc
        // TODO move into class
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        PrivateKeyInfo pki;

        try (PEMParser pemParser = new PEMParser(new StringReader(privateKeyPemString))) {
            Object o = pemParser.readObject();
            if (o instanceof PKCS8EncryptedPrivateKeyInfo) {
                if (passwordChars == null) {
                    throw new IllegalStateException("Expected password to decrypt the private key");
                }
                PKCS8EncryptedPrivateKeyInfo epki = (PKCS8EncryptedPrivateKeyInfo) o;
                JcePKCSPBEInputDecryptorProviderBuilder builder;
                builder = new JcePKCSPBEInputDecryptorProviderBuilder().setProvider("BC");
                InputDecryptorProvider idp = builder.build(passwordChars);

                pki = epki.decryptPrivateKeyInfo(idp);
            } else if (o instanceof PEMEncryptedKeyPair) {
                if (passwordChars == null) {
                    throw new IllegalStateException("Expected password to decrypt the private key");
                }
                PEMEncryptedKeyPair epki = (PEMEncryptedKeyPair) o;
                PEMKeyPair pkp = epki.decryptKeyPair(new BcPEMDecryptorProvider(passwordChars));
                pki = pkp.getPrivateKeyInfo();
            }
            else if (o instanceof PEMKeyPair) {
                if (passwordChars != null && passwordChars.length > 0) {
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

    public static PrivateKey stringToPrivateKey(String privateKeyPemString) {
        return stringToPrivateKeyImpl(privateKeyPemString, null);
    }

    public static PrivateKey encrytpedStringToPrivateKey(String privateKeyPemString, char[] passwordChars) {
        return stringToPrivateKeyImpl(privateKeyPemString, passwordChars);
    }
}

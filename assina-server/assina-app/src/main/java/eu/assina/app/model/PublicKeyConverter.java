package eu.assina.app.model;

import eu.assina.crypto.pem.KeyPemStringConverter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;
import java.security.PublicKey;

@Converter
public class PublicKeyConverter implements AttributeConverter<PublicKey, String> {

    private final KeyPemStringConverter keyPemStringConverter;

    public PublicKeyConverter() {
        keyPemStringConverter = new KeyPemStringConverter();
    }

    @Override
    public String convertToDatabaseColumn(PublicKey publicKey) {
        if (publicKey == null) {
            return null;
        }

        String keyString = null;
        try {
            keyString = keyPemStringConverter.publicKeyToString(publicKey);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return keyString;
    }

    @Override
    public PublicKey convertToEntityAttribute(String publicKeyString) {
        if (publicKeyString == null) {
            return null;
        }

        final PublicKey publicKey;
        try {
            publicKey = keyPemStringConverter.stringToPublicKey(publicKeyString);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return publicKey;
    }
}

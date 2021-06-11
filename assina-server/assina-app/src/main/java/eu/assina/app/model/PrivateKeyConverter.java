package eu.assina.app.model;

import eu.assina.app.common.config.AppProperties;
import eu.assina.crypto.pem.KeyPemStringConverter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;
import java.security.PrivateKey;

@Converter
public class PrivateKeyConverter implements AttributeConverter<PrivateKey, String> {

    private final KeyPemStringConverter keyPemStringConverter;

    @Autowired
    public PrivateKeyConverter(AppProperties appProperties) {
        String passphrase = appProperties.getAuth().getPassphrase();
        if (passphrase == null) {
            throw new IllegalStateException("The passphrase for local encryption must be set in the application.yaml or in the environment");
        }
        keyPemStringConverter = new KeyPemStringConverter(passphrase.toCharArray());
    }

    @Override
    public String convertToDatabaseColumn(PrivateKey privateKey) {
        if (privateKey == null) {
            return null;
        }

        String keyString = null;
        try {
            keyString = keyPemStringConverter.privateKeyToString(privateKey);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return keyString;
    }

    @Override
    public PrivateKey convertToEntityAttribute(String privateKeyString) {
        if (privateKeyString == null) {
            return null;
        }

        final PrivateKey privateKey = keyPemStringConverter.stringToPrivateKey(privateKeyString);
        return privateKey;
    }
}

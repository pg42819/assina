/*
 * Copyright 2012-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.assina.app;

import eu.assina.crypto.cert.CertificateGenerator;
import eu.assina.crypto.pem.KeyPemStringConverter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest
public class CryptoTests {

	@Test
	public void testPrivateKeyRoundTrip() throws Exception {
		CertificateGenerator generator = new CertificateGenerator();
		final KeyPair keyPair = generator.generateKeyPair();
		final PrivateKey originalKey = keyPair.getPrivate();

		final KeyPemStringConverter converter = new KeyPemStringConverter();
		String keyString = converter.privateKeyToString(originalKey);
		System.out.println(keyString);
		// convert back
		final PrivateKey restoredKey = converter.stringToPrivateKey(keyString);
		Assert.assertEquals("Expected the private key to be the same after round-trip through string",
				originalKey, restoredKey);
	}

    @Test
    public void testEncryptedPrivateKeyRoundTrip() throws Exception {
		CertificateGenerator generator = new CertificateGenerator();
        final KeyPair keyPair = generator.generateKeyPair();
        final PrivateKey originalKey = keyPair.getPrivate();

		final char[] passwordChars = "test-password".toCharArray();
		final KeyPemStringConverter converter = new KeyPemStringConverter(passwordChars);
		String keyString = converter.privateKeyToString(originalKey);
		System.out.println(keyString);
		// convert back
		final PrivateKey restoredKey = converter.stringToPrivateKey(keyString);
		Assert.assertEquals("Expected the private key to be the same after round-trip through string",
				originalKey, restoredKey);
    }

	@Test
	public void testPublicKeyRoundTrip() throws Exception {
		CertificateGenerator generator = new CertificateGenerator();
		final KeyPair keyPair = generator.generateKeyPair();
		final PublicKey originalKey = keyPair.getPublic();

		final char[] passwordChars = "test-password".toCharArray();
		// password should be ignored
		final KeyPemStringConverter converter = new KeyPemStringConverter(passwordChars);

		String keyString = converter.publicKeyToString(originalKey);
		System.out.println("String form:");
		System.out.println(keyString);
		final PublicKey restoredKey = converter.stringToPublicKey(keyString);
		Assert.assertEquals("Expected the public key to be the same after round-trip through string",
				originalKey, restoredKey);
	}
}

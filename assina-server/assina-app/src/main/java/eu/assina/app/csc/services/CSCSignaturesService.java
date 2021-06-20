package eu.assina.app.csc.services;

import eu.assina.app.common.model.AssinaCredential;
import eu.assina.app.api.services.CredentialService;
import eu.assina.app.common.error.ApiException;
import eu.assina.app.crypto.AssinaCryptoService;
import eu.assina.app.csc.error.CSCInvalidRequest;
import eu.assina.app.csc.payload.CSCSignaturesSignHashRequest;
import eu.assina.app.csc.payload.CSCSignaturesSignHashResponse;
import eu.assina.app.csc.payload.CSCSignaturesTimestampRequest;
import eu.assina.app.csc.payload.CSCSignaturesTimestampResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CSCSignaturesService {

	private final CredentialService credentialService;
	private AssinaCryptoService cryptoService;

	public CSCSignaturesService(@Autowired CredentialService credentialService,
								@Autowired AssinaCryptoService cryptoService) {
		this.credentialService = credentialService;
		this.cryptoService = cryptoService;
	}

	public CSCSignaturesSignHashResponse signHash(CSCSignaturesSignHashRequest signHashRequest) {
		CSCSignaturesSignHashResponse response = new CSCSignaturesSignHashResponse();
		final String credentialID = signHashRequest.getCredentialID();

		final AssinaCredential credential =
				credentialService.getCredentialWithId(credentialID).orElseThrow(
						() -> new ApiException(CSCInvalidRequest.InvalidCredentialId,
								"No credential found with the given Id", credentialID));

		// TODO can the SAD be a JWT
		validateSAD(signHashRequest.getSAD());
		// assume our sign algo has no params and our hashAlgo is implied by the signalgo
		final List<String> hashes = signHashRequest.getHash();
		List<String> signedHashes = new ArrayList<>();
		for (String hash : signHashRequest.getHash()) {
			// TODO safe to assume default charset for hash (UTF-8)
			byte[] signedData = cryptoService.signWithPemCertificate(
					hash.getBytes(),
					credential.getCertificate(),
					credential.getPrivateKey(),
					signHashRequest.getSignAlgo(),
					signHashRequest.getSignAlgoParams());
			signedHashes.add(new String(signedData)); // assumes UTF8
		}
		response.setSignatures(signedHashes);
		return response;
	}

	private void validateSAD(String SAD) {
		throw new IllegalStateException("Not yet implemented: SAD Validation");
		// TODO validate SAD as a JWT
	}

	public CSCSignaturesTimestampResponse generateTimestamp(CSCSignaturesTimestampRequest timestampRequest) {
		throw new IllegalStateException("Not Yet Implemented"); // TODO implement this
	}
}

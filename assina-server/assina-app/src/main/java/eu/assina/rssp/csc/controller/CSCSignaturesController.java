package eu.assina.rssp.csc.controller;

import eu.assina.csc.payload.*;
import eu.assina.rssp.csc.services.CSCSignaturesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Signatures endpoints from:
 * From section 11.9 of the CSC API V_1.0.4.0 spec
 */
@RestController
@RequestMapping(value = "/signatures")
public class CSCSignaturesController
{
	private CSCSignaturesService signaturesService;

	@Autowired
	public CSCSignaturesController(CSCSignaturesService signaturesService) {
		this.signaturesService = signaturesService;
	}

	/**
	 * Calculate the remote digital signature of one or multiple hash values provided in input.
	 * This method requires credential authorization in the form of Signature Activation Data (SAD).
	 * The signature application SHALL first pass to this method the SAD obtained from
	 * either a credential/authorize, as defined in section 11.6, or a oauth2/authorize calls, as
	 * defined in section 8.3.2, depending on the type of supported authorization mechanisms
	 * associated with the credential.
	 * In case of multi-signature transactions, the SAD SHALL be updated with
	 * credentials/extendTransaction, as defined in section 11.7, every time this method is
	 * invoked until the maximum number of authorized signatures has been generated.
	 *
	 * Example request:
	 *   POST /csc/v1/signatures/signHash HTTP/1.1
	 *   Host: service.domain.org
	 *   Content-Type: application/json
	 *   Authorization: Bearer 4/CKN69L8gdSYp5_pwH3XlFQZ3ndFhkXf9P2_TiHRG-bA
	 *   {
	 *     "credentialID": "GX0112348",
	 *     "SAD": "_TiHRG-bAH3XlFQZ3ndFhkXf9P24/CKN69L8gdSYp5_pw",
	 *     "hash":
	 *     [
	 *        "sTOgwOm+474gFj0q0x1iSNspKqbcse4IeiqlDg/HWuI=",
	 *        "c1RPZ3dPbSs0NzRnRmowcTB4MWlTTnNwS3FiY3NlNEllaXFsRGcvSFd1ST0="
	 *     ],
	 *     "hashAlgo": "2.16.840.1.101.3.4.2.1",
	 *     signAlgo": "1.2.840.113549.1.1.1",
	 *     "clientData": "12345678"
	 *  }
	 *
	 *
	 * Example response:
	 *
	 *   HTTP/1.1 200 OK
	 *   Content-Type: application/json;charset=UTF-8
	 *   {
	 *     "signatures":
	 *     [
	 *       "KedJuTob5gtvYx9qM3k3gm7kbLBwVbEQRl26S2tmXjqNND7MRGtoew==",
	 *       "Idhef7xzgtvYx9qM3k3gm7kbLBwVbE98239S2tm8hUh85KKsfdowel=="
	 *     ]
	 *   }
	 */
	@PostMapping("signHash")
	@ResponseStatus(HttpStatus.OK)
	public CSCSignaturesSignHashResponse signHash(
			@Valid @RequestBody CSCSignaturesSignHashRequest signHashRequest)
	{
		CSCSignaturesSignHashResponse response = signaturesService.signHash(signHashRequest);
		return response;
	}

	/**
	 * Generate a time-stamp token for the input hash value.
	 * The time-stamp token can be generated directly by the RSSP or by a Time Stamping Authority
	 * connected to it.
	 * The reason to implement this method instead of providing time-stamp services through
	 * widespread RFC 3161 [2] protocols directly is to facilitate the creation of longterm
	 * validation digital signatures and to support billing operations. In both cases, the RSSP
	 * provider can offer pre-configured time-stamp services instead of requiring the signature
	 * application to obtain time-stamp services from a different provider.
	 *
	 * Example request:
	 *
	 *   POST /csc/v1/signatures/timestamp HTTP/1.1
	 *   Host: service.domain.org
	 *   Content-Type: application/json
	 *   Authorization: Bearer 4/CKN69L8gdSYp5_pwH3XlFQZ3ndFhkXf9P2_TiHRG-bA
	 *   {
	 *     "hash": "sTOgwOm+474gFj0q0x1iSNspKqbcse4IeiqlDg/HWuI=",
	 *     "hashAlgo": "2.16.840.1.101.3.4.2.1",
	 *     "clientData": "12345678"
	 *   }
	 *
	 * Example response:
	 *
	 *   HTTP/1.1 200 OK
	 *   Content-Type: application/json;charset=UTF-8
	 *   {
	 *     "timestamp":
	 *    "MGwCAQEGCSsGAQQB7U8CATAxMA0GCWCGSAFlAwQCAQUABCCrCqnrjH0VxXyQQlfnFJRx1jjrviTs7/GjKghr2AmluQIIVs5D8OUB4p4YDzIwMTQxMTE5MTEzMjM5WjADAgEBAgkAnWn2SSIWlXk="
	 *   }
	 */
	@PostMapping("timestamp")
	@ResponseStatus(HttpStatus.OK)
	public CSCSignaturesTimestampResponse timestamp(
			@Valid @RequestBody CSCSignaturesTimestampRequest timestampRequest)
	{
		CSCSignaturesTimestampResponse response = signaturesService.generateTimestamp(timestampRequest);
		return response;
	}
}

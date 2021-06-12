package eu.assina.app.csc.controller;

import eu.assina.app.common.error.ApiException;
import eu.assina.app.csc.error.CSCInvalidRequest;
import eu.assina.app.csc.payload.*;
import eu.assina.app.csc.services.CSCCredentialsService;
import eu.assina.app.security.CurrentUser;
import eu.assina.app.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Credentials endpoints from:
 * From section 11.4/11.5 of the CSC API V_1.0.4.0 spec
 */
@RestController
@RequestMapping(value = "/credentials")
public class CSCCredentialsController
{
	private CSCCredentialsService credentialsService;

	@Autowired
	public CSCCredentialsController(CSCCredentialsService credentialsService) {
		this.credentialsService = credentialsService;
	}

	/**
	 * Returns the list of credentials associated with a user identifier.
	 * A user MAY have one or multiple credentials hosted by a single remote signing service
	 * provider.
	 * If the user is authenticated directly by the RSSP then the userID is implicit and SHALL
	 * NOT be specified.
	 * This method can also be used in case of a community of users, to let the client retrieve
	 * the list of credentials assigned to a specific user of the community. In this case the
	 * userID SHALL be passed explicitly to retrieve the list of credentialIDs for a specific
	 * user.
	 * Managing a community of users that are authenticated by the client using a specific
	 * authentication framework is out of the scope of this specification.
	 *
	 * Example request:
     *
	 *   POST /csc/v1/credentials/list HTTP/1.1
	 *   Host: service.domain.org
	 *   Authorization: Bearer 4/CKN69L8gdSYp5_pwH3XlFQZ3ndFhkXf9P2_TiHRG-bA
	 *   Content-Type: application/json
	 *   {
	 *     "maxResults": 10
	 *   }
	 *
	 * Example response:
	 *   HTTP/1.1 200 OK
	 *   Content-Type: application/json;charset=UTF-8
	 *   {
	 *     "credentialIDs": [ "GX0112348", "HX0224685" ]
	 *   }
	 *
	 * @param listRequest
	 */
	@PostMapping("list")
	@ResponseStatus(HttpStatus.OK)
	public CSCCredentialsListResponse list(@CurrentUser UserPrincipal userPrincipal,
										   CSCCredentialsListRequest listRequest)
	{
		// id must be from logged in user only - user-specified
		if (StringUtils.hasText(listRequest.getUserId())) {
			throw new ApiException(CSCInvalidRequest.NonNullUserId);
		}
		listRequest.setUserId(userPrincipal.getId());

		CSCCredentialsListResponse credentialsList =
				credentialsService.listCredentials(listRequest);
		return credentialsList;
	}

	/**
	 * Example request:
	 *
	 * @param infoRequest
	 */
	@PostMapping("info")
	@ResponseStatus(HttpStatus.OK)
	public CSCCredentialsInfoResponse info(CSCCredentialsInfoRequest infoRequest)
	{
		CSCCredentialsInfoResponse credentialsInfo =
				credentialsService.getCredentialsInfo(infoRequest);
		return credentialsInfo;
	}

	/**
	 * authorize
	 *
	 * Per 11.6 of the CSC Spec:
	 *   This method SHALL NOT be used in case of “oauth2” credential authorization;
	 *   instead, any of the available OAuth 2.0 authorization mechanisms SHALL be used.
	 *
	 * OPTIONAL: NOT YET IMPLEMENTED IN ASSINA
	 */
     //	@PostMapping("authorize")

	/**
	 * extendTransaction
	 * Per 11.7 of the CSC Spec:
	 *   Description: Extends the validity of a multi-signature transaction authorization by
	 *   obtaining a new Signature Activation Data (SAD). This method SHALL be used in case of
	 *   multi-signature transaction when the API method signatures/signHash, as defined in section
	 *   11.9, is invoked multiple times with a single credential authorization event.
	 *   It can also be used to renew a SAD, before it expires, when signature operations take
	 *   longer
	 *   than allowed by the expiresIn value. Expired SAD cannot be extended.
	 *   The RSSP SHALL invalidate the SAD when the number of authorized signatures, specified with
	 *   numSignatures in the credential authorization event, has been created.
	 *
	 * OPTIONAL: NOT YET IMPLEMENTED IN ASSINA
     */
	// @PostMapping("extendTransaction")

	/**
 	 * sendOTP
	 * Described in 11.8 of the CSC Spec
	 *
	 * NOT IMPLEMENTED IN ASSINA: OAUTH2 is used instead
	 */
	// @PostMapping("sendOTP")
 }

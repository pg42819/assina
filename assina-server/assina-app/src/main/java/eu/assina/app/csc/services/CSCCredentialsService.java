package eu.assina.app.csc.services;

import eu.assina.app.api.model.AssinaCredential;
import eu.assina.app.api.services.CredentialService;
import eu.assina.app.common.config.PaginationHelper;
import eu.assina.app.common.error.ApiException;
import eu.assina.app.csc.error.CSCInvalidRequest;
import eu.assina.app.csc.payload.CSCCredentialsInfoRequest;
import eu.assina.app.csc.payload.CSCCredentialsInfoResponse;
import eu.assina.app.csc.payload.CSCCredentialsListRequest;
import eu.assina.app.csc.payload.CSCCredentialsListResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CSCCredentialsService {

	private static final Logger log = LoggerFactory.getLogger(CSCCredentialsService.class);

	@Autowired
	PaginationHelper paginationHelper;
	private CredentialService credentialService;


	public CSCCredentialsService(@Autowired CredentialService credentialService) {
		this.credentialService = credentialService;
	}

	/**
	 * Returns a list of credentials and a pageToken to allow the client to get the next page.
	 *
	 * Page start is taken from the pageToken in the request, and page length is calculated as
	 * the maximum of the MaxResults in the request and the max page size defined in
	 * application.yml under csc.api, or the default pageSize if the request does nto specify
	 */
	public CSCCredentialsListResponse listCredentials(CSCCredentialsListRequest listRequest) {
		Pageable pageable;
		String nextPageToken;
		try {
			pageable = paginationHelper.pageTokenToPageable(listRequest.getPageToken(), listRequest.getMaxResults());
			nextPageToken = paginationHelper.pageableToNextPageToken(pageable);
		}
		catch (Exception e) {
			// invalid page token per the CSC spec
			throw new ApiException(CSCInvalidRequest.InvalidPageToken, e);
		}

		final Page<AssinaCredential> credentialsPage =
				credentialService.getCredentialsByOwner(listRequest.getUserId(), pageable);

		final List<String> credentialIds =
				credentialsPage.map(AssinaCredential::getId).stream().collect(Collectors.toList());

		CSCCredentialsListResponse response = new CSCCredentialsListResponse();
		response.setCredentialIDs(credentialIds);
		response.setNextPageToken(nextPageToken);
		return response;
	}

	public CSCCredentialsInfoResponse getCredentialsInfo(CSCCredentialsInfoRequest infoRequest) {
		// validate the lang parameter
		infoRequest.validate();

		final String credentialID = infoRequest.getCredentialID();
		final Optional<AssinaCredential> credential = credentialService.getCredentialWithId(credentialID);
		// TODO check if owner matches credential
		// TODO handle missing credential check the spec
		throw new IllegalStateException("Not Yet Implemented");
	}
}

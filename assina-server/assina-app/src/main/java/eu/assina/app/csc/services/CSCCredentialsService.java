package eu.assina.app.csc.services;

import eu.assina.app.common.model.AssinaCredential;
import eu.assina.app.api.services.CredentialService;
import eu.assina.app.common.config.PaginationHelper;
import eu.assina.app.common.error.ApiException;
import eu.assina.app.crypto.AssinaCryptoService;
import eu.assina.app.csc.error.CSCInvalidRequest;
import eu.assina.app.csc.model.CertificateStatus;
import eu.assina.app.csc.payload.CSCCredentialsInfoRequest;
import eu.assina.app.csc.payload.CSCCredentialsInfoResponse;
import eu.assina.app.csc.payload.CSCCredentialsListRequest;
import eu.assina.app.csc.payload.CSCCredentialsListResponse;
import eu.assina.app.util.CertificateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CSCCredentialsService {

	private static final Logger log = LoggerFactory.getLogger(CSCCredentialsService.class);

	@Autowired
	PaginationHelper paginationHelper;
	private CredentialService credentialService;
	private AssinaCryptoService cryptoService;


	public CSCCredentialsService(CredentialService credentialService,
								 AssinaCryptoService cryptoService) {
		this.credentialService = credentialService;
		this.cryptoService = cryptoService;
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
		// don't set the next page token if its the last page
		if (credentialsPage.isLast()) {
			nextPageToken = null;
		}
		response.setNextPageToken(nextPageToken);
		return response;
	}

	// TODO authorize creds
	//	public CSCCredentialsAuthorizeResponse authorizeCredential(CSCCredentialsAuthorizeRequest);

	public CSCCredentialsInfoResponse getCredentialsInfo(CSCCredentialsInfoRequest infoRequest) {
		// validate the basic parameters
		infoRequest.validate();

		final String credentialID = infoRequest.getCredentialID();
		final AssinaCredential credential =
				credentialService.getCredentialWithId(credentialID).orElseThrow(
						() -> new ApiException(CSCInvalidRequest.InvalidCredentialId,
						"No credential found with the given Id", credentialID));

		// TODO check if owner matches credential
		CSCCredentialsInfoResponse response = new CSCCredentialsInfoResponse();

		// REQUIRED
		// One of implicit | explicit | oauth2code
		// TODO make a constant and enforce the explicit per the standard
		response.setAuthMode("explicit");

		response.setDescription(credential.getDescription());
		response.setMultisign(1); // Only 1 signature supported
		// “1”: The hash  to-be-signed is not linked to the signature activation data.
		response.setSCAL("1");
		response.setKey(buildKeyInfo(credential));

		CSCCredentialsInfoResponse.Cert cert = new CSCCredentialsInfoResponse.Cert();
		final String pemCertificate = credential.getCertificate();
		final X509Certificate x509Certificate = cryptoService.pemToX509Certificate(pemCertificate);
		List<String> cscCertificates = new ArrayList<>();
		switch (infoRequest.getCertsRequest()) {
			case none:
				// nothing requested, move on
				break;
			case single:
			    // certificates are already stored as PEM strings which are Base64 encoded
				cscCertificates.add(pemCertificate);
				break;
			case chain:
				// TODO consider supporting chain request
				throw new IllegalArgumentException("Not Yet Implmented");
		}
		cert.setCertificates(cscCertificates);

		// only if certInfo is true in the request:
		if (infoRequest.isCertInfo()) {
			addCertInfo(cert, x509Certificate);
		}
		response.setCert(cert);
        if (cryptoService.isCertificateExpired(x509Certificate)) {
			cert.setStatus(CertificateStatus.expired.name());
		}
        else {
			// Consider handling other cases like "revoked" and "suspended"
			cert.setStatus(CertificateStatus.valid.name());
		}

		// Per CSC spec, we only return OTP and PIN info if authInfo is true in the request
		if (infoRequest.isAuthInfo()) {
			response.setPIN(buildPINInfo());
			response.setOTP(buildOTPInfo());
		}

		return response;
	}

	/**
	 * Update info about the cert in the response
	 * According to the CSC standard, these properties are only set when the certInfo property
	 * is true in the request
	 */
	private void addCertInfo(CSCCredentialsInfoResponse.Cert cert, X509Certificate x509Certificate) {
		cert.setIssuerDN(x509Certificate.getIssuerDN().getName());
		cert.setSubjectDN(x509Certificate.getSubjectDN().getName());
		cert.setSerialNumber(String.valueOf(x509Certificate.getSerialNumber()));

		// per CSC spec: encoded as GeneralizedTime (RFC 5280 [8]) e.g.  “YYYYMMDDHHMMSSZ”
		cert.setValidFrom(CertificateUtils.x509Date(x509Certificate.getNotBefore()));
		cert.setValidTo(CertificateUtils.x509Date(x509Certificate.getNotAfter()));

	}

	private CSCCredentialsInfoResponse.OTP buildOTPInfo() {
		return null; // TODO if needed add OTP
	}

	private CSCCredentialsInfoResponse.PIN buildPINInfo() {
		return null; // TODO if needed add PIN
	}

	private CSCCredentialsInfoResponse.Key buildKeyInfo(AssinaCredential credential) {
		CSCCredentialsInfoResponse.Key key = new CSCCredentialsInfoResponse.Key();
		key.setAlgo(credential.getKeyAlgorithmOIDs());
		key.setCurve(credential.getECDSACurveOID());
		key.setLen(String.valueOf(credential.getKeyBitLength())); // num bits in key
        key.setStatus(credential.isKeyEnabled() ? "enabled" : "disabled");
        return key;
	}
}

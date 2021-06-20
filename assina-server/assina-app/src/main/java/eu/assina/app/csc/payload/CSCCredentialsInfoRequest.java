package eu.assina.app.csc.payload;

import com.fasterxml.jackson.annotation.JsonIgnore;
import eu.assina.app.common.error.ApiException;
import eu.assina.app.csc.error.CSCInvalidRequest;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import static eu.assina.app.csc.error.CSCInvalidRequest.MissingCredentialId;

/**
 * Body for request of credentials/info - information about a specific credential record
 * of credential IDs
 * From section 11.5 of the CSC API V_1.0.4.0 spec
 */
public class CSCCredentialsInfoRequest extends AbstractLangRequest{

    public CSCCredentialsInfoRequest() {}

    // Allowed values for the certificates attribute: not used in payload but derived in validation
    public enum CertsRequest { none, single, chain }

    // REQUIRED
    // The unique identifier associated to the credential.
    @NotBlank(message = "MissingCredentialId")
    private String credentialID;

    // OPTIONAL
    // none | single | chain
    // Specifies which certificates from the certificate chain shall be returned in certs/certificates.
    //  • “none”: No certificate SHALL be returned.
    //  • “single”: Only the end entity certificate SHALL be returned.
    //  • “chain”: The full certificate chain SHALL be returned.
    // The default value is “single”, so if the parameter is omitted then the method will
    // only return the end entity certificate.
    private String certificates;

    // converts the certficates above to an enum (see validation below)
    @JsonIgnore
    private CertsRequest _certsRequest;


    // OPTIONAL
    // Request to return various parameters containing information from the end entity
    // certificate.
    // This is useful in case the signature application wants to retrieve some details
    // of the certificate without having to decode it first.
    // The default value is “false”, so if the parameter is omitted then the information will
    // not be returned.
    private boolean certInfo = false;

    // OPTIONAL
    // Request to return various parameters containing information on the
    // authorization mechanisms supported by this  credential (PIN and OTP groups).
    // The default value is “false”, so if the parameter is omitted then the
    // information will not be returned.
    private boolean authInfo = false;

    // OPTIONAL
    // String The clientData as defined in the Input parameter table in section 8.3.2.
    private String clientData;

    public String getCredentialID() {
        return credentialID;
    }

    public void setCredentialID(String credentialID) {
        this.credentialID = credentialID;
    }

    public String getCertificates() {
        return certificates;
    }

    public void setCertificates(String certificates) {
        this.certificates = certificates;
    }

    public boolean isCertInfo() {
        return certInfo;
    }

    public void setCertInfo(boolean certInfo) {
        this.certInfo = certInfo;
    }

    public boolean isAuthInfo() {
        return authInfo;
    }

    public void setAuthInfo(boolean authInfo) {
        this.authInfo = authInfo;
    }

    public String getClientData() {
        return clientData;
    }

    public void setClientData(String clientData) {
        this.clientData = clientData;
    }

    /** helper to convert the string certificates property to an enum */
    public CertsRequest getCertsRequest() {
        if (_certsRequest == null) {
            if (StringUtils.hasText(certificates)) {
                try {
                    _certsRequest = CertsRequest.valueOf(certificates);
                } catch (IllegalArgumentException e) {
                    // certificates was not one of none, single or chain, which is an error
                    throw new ApiException(CSCInvalidRequest.InvalidCertificatesParameter);
                }
            }
            else{
                // certificates is optional and defaults to single
                _certsRequest = CertsRequest.single;
            }
        }
        return _certsRequest;
    }
}

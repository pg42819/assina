package eu.assina.app.csc.payload;

import eu.assina.app.common.error.ApiException;
import eu.assina.app.csc.error.CSCInvalidRequest;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Body for request of signatures/signHash for signing document hashes
 * From section 11.9 of the CSC API V_1.0.4.0 spec
 */
public class CSCSignaturesSignHashRequest {

    // REQUIRED
    // The credentialID as defined in the Input parameter table in section 11.5.
    private String credentialID;

    // REQUIRED
    // The Signature Activation Data returned by the Credential Authorization methods.
    private String SAD;

    // REQUIRED
    // One or more hash values to be signed.
    // This parameter SHALL contain the Base64-encoded raw message digest(s).
    private List<String> hash;

    // REQUIRED_Conditional
    // The OID of the algorithm used to calculate the hash value(s).
    // This parameter SHALL be omitted or ignored if the hash algorithm is implicitly specified
    // by the signAlgo algorithm.
    // Only hashing algorithms as strong or stronger than SHA256 SHALL be used.
    // The hash algorithm SHOULD follow the recommendations of ETSI TS 119 312 [21].
    private String hashAlgo;

    // REQUIRED
    // The OID of the algorithm to use for signing.
    // It SHALL be one of the values allowed by the credential as returned in keyAlgo by the
    // credentials/info method, as defined in section 11.5.
    private String signAlgo;

    // REQUIRED_Conditional
    // The Base64-encoded DER-encoded ASN.1 signature parameters, if required by the signature
    // algorithm. Some algorithms like RSASSA-PSS, as defined in RFC 8917 [18],
    // may require additional parameters.
    private String signAlgoParams;

    // OPTIONAL
    // The clientData as defined in the Input parameter table in section 8.3.2.
    private String clientData;

    public String getCredentialID() {
        return credentialID;
    }

    public void setCredentialID(String credentialID) {
        this.credentialID = credentialID;
    }

    public String getSAD() {
        return SAD;
    }

    public void setSAD(String SAD) {
        this.SAD = SAD;
    }

    public List<String> getHash() {
        return hash;
    }

    public void setHash(List<String> hash) {
        this.hash = hash;
    }

    public String getHashAlgo() {
        return hashAlgo;
    }

    public void setHashAlgo(String hashAlgo) {
        this.hashAlgo = hashAlgo;
    }

    public String getSignAlgo() {
        return signAlgo;
    }

    public void setSignAlgo(String signAlgo) {
        this.signAlgo = signAlgo;
    }

    public String getSignAlgoParams() {
        return signAlgoParams;
    }

    public void setSignAlgoParams(String signAlgoParams) {
        this.signAlgoParams = signAlgoParams;
    }

    public String getClientData() {
        return clientData;
    }

    public void setClientData(String clientData) {
        this.clientData = clientData;
    }

    /**
     * Fail if the request does not match the standard - according to CSC spec 11.9
     */
    public void validate() {
        if (!StringUtils.hasText(credentialID)) {
            throw new ApiException(CSCInvalidRequest.MissingCredentialId);
        }
        if (!StringUtils.hasText(SAD)) {
            throw new ApiException(CSCInvalidRequest.MissingSAD);
        }
        if (hash == null || hash.isEmpty()) {
            throw new ApiException(CSCInvalidRequest.InvalidHashArray);
        }
        if (!StringUtils.hasText(signAlgo)) {
            throw new ApiException(CSCInvalidRequest.MissingSignAlgo);
        }
    }

}

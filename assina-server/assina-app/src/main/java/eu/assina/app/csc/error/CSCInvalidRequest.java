package eu.assina.app.csc.error;

import eu.assina.app.common.error.ApiError;

/**
 * Enum holding all of the Assina error codes and descriptions
 */
public enum CSCInvalidRequest implements ApiError {

  MissingBearer("The request is missing a required parameter, includes an invalid parameter " +
                        "value, includes a parameter more than once, or is otherwise malformed."),

  InvalidPageToken("Invalid parameter pageToken"),

  // From CSC Spec:
  // If a user-specific service authorization is present, it SHALL NOT be allowed to use this
  // parameter to obtain the list of credentials associated to a different user.
  // The remote service SHALL return an error in such case.
  // NOTE 1: User-specific service authorization include the following authType:
  //        “basic”, “digest” and “oauth2code”.
  //        Non-user-specific service authorization include the following authType:
  //        “external”, “TLS” or “oauth2client”.
  NonNullUserId("userID parameter MUST be null"),

  // Should not be needed since we use user-specified authorization
  InvalidUserId("Invalid parameter userID"),

  //
  // From 10.5 of the CSC Spec for credentials/info
  //
  MissingCredentialId("Missing (or invalid type) string parameter credentialID"),
  InvalidCredentialId("Invalid parameter credentialID"),
  InvalidCertificatesParameer("Invalid parameter certificates");



  private String description;

  CSCInvalidRequest(String description) {
    this.description = description;
  }

  public String getCode() {
    // All API errors in the CSC spec return invalid_request
    return "invalid_request";
  }

  public int getHttpCode() {
    // All API errors in the CSC spec return http status 400
    return 400;
  }

  public String getDescription() {
    return description;
  }
}


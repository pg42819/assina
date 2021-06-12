package eu.assina.app.api.error;

import eu.assina.app.common.error.ApiError;

/**
 * Enum holding all of the Assina error codes and descriptions
 */
public enum AssinaError implements ApiError {

  CredentialNotFound("credential_not_found",
          "No credential was found matching this query", 404),

  CredentialAlreadyExists("Assina-CredentialAlreadyExists",
          "A credential matching this description already exists", 409),
  CredentialRequestMissingRequiredProperty("Assina-MissingRequiredProperty",
      "The object in the request is missing a required property", 400),
  CredentialRequestInvalidProperty("Assina-InvalidProperty",
      "The object in the request has an invalid property", 400),
  CredentialRequestInvalid("Assina-InvalidObject",
      "The object in the request is invalid", 400),

  // Failing all else general CredentialStore error
  UnexpectedError("assina_unexpected_error",
      "An unexpected internal error occurred in the Assina RSSP", 500),

  UserNotFound("user_not_found", "Could not find the requested user in Assina", 404),

  UserEmailAlreadyUsed("already_used",
          "The username or email address belongs to an existing user", 409),

  OauthUnauthorizedRedirect("unauthorized_redirect", "Sorry! We've got an Unauthorized Redirect " +
                             "URI and can't proceed wtih authentication", 400),
  FailedCreatingCredential("failed_creating_credential",
          "An Error occured while generating certificate and keys", 500);

  private final String code;
  private final int httpCode;
  private String desc;

  // TODO expand into CSC errors
  AssinaError(String code, String desc, int httpCode) {
    this.code = code;
    this.desc = desc;
    this.httpCode = httpCode;
  }

  @Override
  public String getCode() {
    return code;
  }

  @Override
  public int getHttpCode() {
    return httpCode;
  }

  @Override
  public String getDescription() {
    return desc;
  }
}


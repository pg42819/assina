package eu.assina.app.error;

/**
 * Enum holding all of the Assina error codes and descriptions
 */
public enum AssinaError {

  CredentialNotFound("Assina-CredentialNotFound",
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
  UnexpectedError("Assina-UnexpectedError",
      "An unexpected internal error occurred in the CredentialStore", 500);

  private final String code;
  private final int httpCode;
  private String desc;

  // TODO expand into CSC errors
  AssinaError(String code, String desc, int httpCode) {
    this.code = code;
    this.desc = desc;
    this.httpCode = httpCode;
  }

  public String getCode() {
    return code;
  }

  public int getHttpCode() {
    return httpCode;
  }

  public String getDesc() {
    return desc;
  }
}


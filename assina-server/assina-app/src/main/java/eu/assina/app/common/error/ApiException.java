package eu.assina.app.common.error;

public class ApiException extends RuntimeException {
	private ApiError apiError;
	private String[] messageParams;

	public ApiException(ApiError apiError) {
		super(apiError.getDescription());
		this.apiError = apiError;
	}

	public ApiException(String message, ApiError apiError, String... args) {
		super(message);
		this.apiError = apiError;
		this.messageParams = args;
	}

	public ApiException(String message, ApiError apiError, Exception cause) {
		super(message, cause);
		this.apiError = apiError;
	}

	public ApiException(ApiError apiError, Exception cause) {
		super(apiError.getDescription(), cause);
		this.apiError = apiError;
	}

	public ApiError getApiError() {
		return apiError;
	}

	public String[] getMessageParams() {
		return messageParams;
	}
}

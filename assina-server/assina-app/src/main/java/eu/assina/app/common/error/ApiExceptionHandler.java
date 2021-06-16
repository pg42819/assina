package eu.assina.app.common.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


/**
 * Captures exceptions going out of the REST layer and converts ApiErrors into proper
 * error responses. The error response deliberatly matches that prescribed in the
 * CSC spec v1.0.4.0
 *
 * Example: (From 10.1 of CSC spec 1.0.4.0)
 *   HTTP/1.1 400 Bad Request
 *   Date: Mon, 03 Dec 2018 12:00:00 GMT Content-Type: application/json;charset=utf-8
 *   Content-Length: ...
 *   {
 *     "error": "invalid_request",
 *     "error_description": "The access token is not valid"
 *   }
 */
// TODO add support for missing bearer token etc
@ControllerAdvice
public class ApiExceptionHandler
		extends ResponseEntityExceptionHandler
{
	private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

	@ExceptionHandler(value = {ApiException.class})
	protected ResponseEntity<?> handleApiException(RuntimeException ae, WebRequest request) {
		ApiError apiError = ((ApiException)ae).getApiError();
		String message = ae.getMessage();
		return apiErrorToResponse(apiError, message, ae, request);
	}

	@Override
	protected ResponseEntity<Object> handleExceptionInternal(
			Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
		if (ex instanceof ApiException) {
			log.debug("Handled exception in Assina application", ex);
			log.warn("Handled Error: " + ex.getMessage(), (Object[]) ((ApiException)ex).getMessageParams());
		}
		else {
			log.error("Unhandled exception in Assina application", ex);
		}
		return super.handleExceptionInternal(ex, body, headers, status, request);
	}

	private ResponseEntity<Object> apiErrorToResponse(
			ApiError error, String message, Exception ex, WebRequest request) {
		ApiErrorResponse response = new ApiErrorResponse(error.getCode(), error.getDescription());
		HttpStatus httpStatus = HttpStatus.valueOf(error.getHttpCode());
		// log a warning for all messages
		log.warn("Responding to error: {} with status {}. Error description {}; message: {}",
				 error.getCode(), error.getHttpCode(), error.getDescription(), message);

		return handleExceptionInternal(ex, response, new HttpHeaders(), httpStatus, request);
	}
}

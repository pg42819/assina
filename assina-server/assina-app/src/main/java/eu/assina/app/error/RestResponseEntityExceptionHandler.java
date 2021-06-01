package eu.assina.app.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice
public class RestResponseEntityExceptionHandler
		extends ResponseEntityExceptionHandler
{
	private static final Logger log = LoggerFactory.getLogger(RestResponseEntityExceptionHandler.class);

	@ExceptionHandler(value = {AssinaException.class})
	protected ResponseEntity<?> handleAssinaException(RuntimeException ae, WebRequest request) {
		AssinaError errorCode = ((AssinaException)ae).getErrorCode();
		String message = ae.getMessage();
		return apiErrorToResponse(errorCode, message, ae, request);
	}
	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
		if (ex instanceof AssinaException) {
			log.debug("Handled exception in Spring controller", ex);
		}
		else {
			log.error("Unhandled exception in Spring controller", ex);
		}
		return super.handleExceptionInternal(ex, body, headers, status, request);
	}

	private ResponseEntity<Object> apiErrorToResponse(AssinaError error, String message,
																										Exception ex, WebRequest request) {
		AssinaErrorResponse response = new AssinaErrorResponse(error.getCode(), error.getDesc());
		HttpStatus httpStatus = HttpStatus.valueOf(error.getHttpCode());

		return handleExceptionInternal(ex, response, new HttpHeaders(), httpStatus, request);
	}
}

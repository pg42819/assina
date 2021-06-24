package eu.assina.sa.error;

import eu.assina.common.ApiError;
import eu.assina.common.ApiErrorResponse;
import org.springframework.web.reactive.function.client.ClientResponse;

public class RSSPClientException extends RuntimeException implements ApiError {

    private String code;
    private int httpCode;
    private String description;

    public RSSPClientException(ApiErrorResponse rsspServerError, int httpCode) {
        super(rsspServerError.getError_description());
        this.httpCode = httpCode;
        this.code = rsspServerError.getError();
        this.description = rsspServerError.getError_description();
    }

    public RSSPClientException(String code, int httpCode, String description) {
        super(description);
        this.code = code;
        this.httpCode = httpCode;
        this.description = description;
    }

    public RSSPClientException(ClientResponse response) {
        super(response.statusCode().getReasonPhrase(), response.createException().block());
        this.code = "Unexpected Client Error";
        this.httpCode = response.statusCode().value();
        this.description = response.statusCode().getReasonPhrase();
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
        return description;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setHttpCode(int httpCode) {
        this.httpCode = httpCode;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

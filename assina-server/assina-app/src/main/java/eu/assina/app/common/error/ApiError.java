package eu.assina.app.common.error;

public interface ApiError {
    String getCode();

    int getHttpCode();

    String getDescription();
}

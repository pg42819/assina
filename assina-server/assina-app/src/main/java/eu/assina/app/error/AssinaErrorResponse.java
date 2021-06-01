package eu.assina.app.error;

/**
 * Error response as defined in the CSC standard
 */
public class AssinaErrorResponse
{
    private String error;
    private String error_description;

    public AssinaErrorResponse(String error)
    {
        this(error, "");
    }

    public AssinaErrorResponse(String error, String error_description)
    {
        this.error = error;
        this.error_description = error_description;
    }

    public String getError()
    {
        return error;
    }

    public void setError(String error)
    {
        this.error = error;
    }

    public String getError_description()
    {
        return error_description;
    }

    public void setError_description(String error_description)
    {
        this.error_description = error_description;
    }
}

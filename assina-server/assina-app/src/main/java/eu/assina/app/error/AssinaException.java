package eu.assina.app.error;

public class AssinaException extends RuntimeException
{
	private final String resourceId;
	private AssinaError errorCode;

	public AssinaException(String message, String resourceId)
	{
		this(message, null, resourceId, AssinaError.UnexpectedError);
	}

	public AssinaException(String message, String resourceId, AssinaError errorCode)
	{
	  this(message, null, resourceId, errorCode);
	}

	public AssinaException(String message, Exception cause, AssinaError errorCode)
	{
		this(message, cause, null, errorCode);
	}

	public AssinaException(String message, Exception cause, String resourceId, AssinaError errorCode)
	{
		super(message, cause);
		this.resourceId = resourceId;
		this.errorCode = errorCode;
	}

	protected String getResourceId()
	{
		return resourceId;
	}

	public AssinaError getErrorCode()
	{
		return errorCode;
	}
}

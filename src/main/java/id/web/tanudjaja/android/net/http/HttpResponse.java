
package id.web.tanudjaja.android.net.http;

public class HttpResponse
{
	// data
	private final int mStatusCode;
	private final byte[] mContent;
	private final String mContentType;
	private String mRedirectionUrl;
	
	/**
	 * The constructor of JHttpResponse
	 * @param aStatusCode	The HTTP status code of the response.
	 * @param aContent		The content of the response.
	 * @param aContentType	The content type of the response.
	 */
	public HttpResponse(int aStatusCode, byte[] aContent, String aContentType)
	{
		mStatusCode = aStatusCode;
		mContent = aContent;
		mContentType = aContentType;
	}

	/**
	 * @return The HTTP status code.
	 */
	public int getStatusCode()
	{
		return mStatusCode;
	}

	/**
	 * @return The content.
	 */
	public byte[] getContent()
	{
		return mContent;
	}

	/**
	 * @return The content type.
	 */
	public String getContentType()
	{
		return mContentType == null ? "" : mContentType;
	}

	public void setRedirectionUrl(String aUrl)
	{
		mRedirectionUrl = aUrl;
	}

	public String getRedirectionUrl()
	{
		return mRedirectionUrl == null ? "" : mRedirectionUrl;
	}

};

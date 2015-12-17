
package id.web.tanudjaja.android.net.http;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest
{
	// data
	private final String mMethod;
	protected String mUrl;
	private final Map<String, String> mAdditionalHeaders;
	
	/**
	 * The constructor of JHttpRequest
	 * @param aMethod	The method used to send this request.
	 * @param aUrl		The target URL.
	 */
	public HttpRequest(String aMethod, String aUrl)
	{
		mMethod=aMethod;
		mUrl=aUrl;
		mAdditionalHeaders=new HashMap<String, String>();
	}

	/**
	 * @return The method used to send this request.
	 */
	public String getMethod()
	{
		return mMethod==null ? "" : mMethod;
	}

	/**
	 * @return The target URL.
	 */
	public String getUrl()
	{
		return mUrl==null ? "" : mUrl;
	}

	/**
	 * @return The additional headers.
	 */
	public Map<String, String> getAdditionalHeaders()
	{
		return mAdditionalHeaders;
	}



};

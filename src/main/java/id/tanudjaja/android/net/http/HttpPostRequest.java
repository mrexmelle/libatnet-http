
package id.tanudjaja.android.net.http;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.HashMap;

import id.web.tanudjaja.android.common.port.errno;
import id.web.tanudjaja.android.common.port.stdc;

public class HttpPostRequest extends HttpRequest
{
	// data
	private byte[] mContent;
	private Map<String, String> mMap;
	private boolean mAllowEditParam;

	/**
	 * The constructor of JHttpPostRequest
	 *
	 * @param aUrl The target URL.
	 */
	public HttpPostRequest(String aUrl, byte[] aContent)
	{
		super("POST", aUrl);

		mAllowEditParam = false;
		if (aContent != null)
		{
			mContent = new byte[aContent.length];
			stdc.memcpy(mContent, aContent, aContent.length);
		}
	}

	public HttpPostRequest(String aUrl, Map<String, String> aParamList)
	{
		super("POST", aUrl);
		mAllowEditParam = true;
		mMap = new LinkedHashMap<>();
		mMap.putAll(aParamList);
		processContent(mMap);
	}

	public int editMapContent(Map<String, String> aParamList)
	{
		if(!mAllowEditParam)
		{
			return errno.EOPNOTSUPP;
		}
		else
		{
			mMap.putAll(aParamList);
			processContent(mMap);
			return errno.SUCCESS;
		}
	}

	private void processContent(Map<String, String> aParamList)
	{
		if (aParamList != null)
		{
			String content = "";
			for (Map.Entry<String, String> entry : aParamList.entrySet())
			{
				try
				{
					// validate the key
					String key=entry.getKey();
					if("".equals(key))
					{
						continue;
					}
					
					// validate the value
					String val=entry.getValue();
					if(val==null)
					{
						val="null";
					}
					
					content += String.format("%s=%s&", key, URLEncoder.encode(val, "UTF-8"));
				}
				catch (UnsupportedEncodingException e)
				{
					continue;
				}
			}

			if (content.length() > 0)
			{
				mContent = new byte[content.length() - 1];
				try
				{
					stdc.memcpy(mContent, content.getBytes("UTF-8"), content.length() - 1);
				}
				catch (UnsupportedEncodingException e)
				{
					mContent = null;
				}
			}
		}
	}

	/**
	 * @return The content of the request.
	 */
	public byte[] getContent()
	{
		return mContent;
	}
};

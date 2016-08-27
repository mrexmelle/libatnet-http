
package id.tanudjaja.android.net.http;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.LinkedHashMap;

public class HttpGetRequest extends HttpRequest
{
	private Map<String,String> mMap;
	private String mParams;
	/**
	* The constructor of JHttpGetRequest
	* @param aUrl		The target URL.
	*/
	public HttpGetRequest(String aUrl)
	{
		super("GET", aUrl);
		mMap = new LinkedHashMap<>();
	}

	public HttpGetRequest(String aUrl, Map<String, String> aParamList)
	{
		super("GET", aUrl);
		mMap = new LinkedHashMap<>();
		mMap.putAll(aParamList);
		processContent(mMap);
	}

	public void editMapContent(Map<String,String> aParamList)
	{
		mMap.putAll(aParamList);
		processContent(mMap);
	}

	private void processContent(Map<String,String> aParamList)
	{
		if(aParamList!=null)
		{
			mParams="";
			for(Map.Entry<String, String> entry : aParamList.entrySet())
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

					mParams += String.format("%s=%s&", key, URLEncoder.encode(val, "UTF-8"));
				}
				catch (UnsupportedEncodingException e)
				{
					continue;
				}
			}
			
//			if(iParams.length()>0)
//			{
//				iUrl+="?";
//				iUrl+=iParams.substring(0, iParams.length()-1);
//			}
		}
	}

	@Override
	public String getUrl()
	{
		if(mParams==null) {  return super.getUrl(); }
		if(mParams.length()==0) {  return super.getUrl(); }

		return super.getUrl()+"?"+mParams.substring(0, mParams.length()-1);
	} 
};

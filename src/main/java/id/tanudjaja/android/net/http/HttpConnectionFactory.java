
package id.tanudjaja.android.net.http;

// Java's imports
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Map;

// Android's imports
import android.util.Log;

import id.web.tanudjaja.android.common.NetUtils;

public class HttpConnectionFactory
{
	private final static String TAG="HttpConnectionFactory";
    
    /**
	 * Opens connection to specified <u>aUrl</u>.
	 * @since	1.1.0
	 * @param	aUrl The URL to which a connection is established.
	 * @throws	MalformedURLException The specified <u>aUrl</u> is invalid.
	 * @throws	IOException Cannot establish connection to the specified <u>aUrl</u>.
	 * @return	HttpURLConnection A URLConnection class that stores information about the connection.
	 */
	public static HttpURLConnection openConnectionL(String aUrl)
		throws MalformedURLException, IOException
	{
		// initiate URL
		URL url=null;
		try
		{
			url=new URL(aUrl);
		}
		catch (MalformedURLException e)
		{
			throw e;
		}
		
		// open the connection
		try
		{
			return (HttpURLConnection)(url.openConnection());
		}
		catch (IOException e)
		{
			throw e;
		}
	}
    
    /**
	 * Adds header fields to <u>aConnection</u>.
	 * @since	1.1.0
	 * @param	aConnection The connection to which the header fields are appended.
	 * @param	aHeaderFields A map of header fields to be appended.
	 * @return	HttpURLConnection A pointer to aConnection.
	 */
	public static HttpURLConnection addHeaderFields(
		HttpURLConnection aConnection,
		Map<String, String> aHeaderFields)
	{
		if (aHeaderFields != null)
		{
			for (Map.Entry<String, String> field : aHeaderFields.entrySet())
			{
				aConnection.setRequestProperty(field.getKey(), field.getValue());
			}
		}
		
		return aConnection;
	}
	
	/**
	 * Prepares a connection to do a GET request to <u>aUrl</u> with specified parameters.
	 * @since	1.1.0
	 * @param	aUrl The URL to which a connection is established.
	 * @param	aConnectTimeout The time in millisecond to wait for a response before a connecting attempt is canceled.
	 * @param	aReadTimeout The time in millisecond to wait for a response in a connection before the connection is dropped.
	 * @param	aHeaderFields A map of header fields to be appended.
	 * @throws	MalformedURLException The specified <u>aUrl</u> is invalid.
	 * @throws	IOException Cannot establish connection to the specified <u>aUrl</u>.
	 * @throws	ProtocolException The GET method is not available at this JRE.
	 * @return	URLConnection A pointer to aConnection.
	 */
	public static HttpURLConnection setupGet(String aUrl,
		int aConnectTimeout, int aReadTimeout,
		Map<String, String> aHeaderFields)
		throws MalformedURLException, IOException, ProtocolException
	{
		HttpURLConnection con=null;
		
		Log.d(TAG, "opening connection");
		
		try
		{
			con=openConnectionL(aUrl);
		}
		catch(MalformedURLException e)
		{
			throw e;
		}
		catch(IOException e)
		{
			throw e;
		}
		
		// set basic behaviors
		con.setConnectTimeout(aConnectTimeout);
		con.setReadTimeout(aReadTimeout);
		con.setDefaultUseCaches(false);
		
		//		Log.d(TAG, "content-length: " + con.getContentLength());
		
		// set to GET
		try
		{
			con.setRequestMethod("GET");
		}
		catch (ProtocolException e)
		{
			throw e;
		}
		con.setDoInput(true);
		con.setDoOutput(false);
		
		// add additional header fields
		con=addHeaderFields(con, aHeaderFields);
		
		return con;
	}

	/**
	 * Prepares a connection to do a POST request to <u>aUrl</u> with specified parameters.
	 * @since	1.1.0
	 * @param	aUrl The URL to which a connection is established.
	 * @param	aConnectTimeout The time in millisecond to wait for a response before a connecting attempt is canceled.
	 * @param	aReadTimeout The time in millisecond to wait for a response in a connection before the connection is dropped.
	 * @param	aHeaderFields A map of header fields to be appended.
	 * @throws	MalformedURLException The specified <u>aUrl</u> is invalid.
	 * @throws	IOException Cannot establish connection to the specified <u>aUrl</u>.
	 * @throws	ProtocolException The POST method is not available at this JRE.
	 * @return	URLConnection A pointer to aConnection.
	 */
	public static HttpURLConnection setupPost(String aUrl,
		int aConnectTimeout, int aReadTimeout,
		Map<String, String> aHeaderFields)
		throws MalformedURLException, IOException, ProtocolException
	{
		HttpURLConnection con=null;
		
		try
		{
			con=openConnectionL(aUrl);
		}
		catch (MalformedURLException e)
		{
			throw e;
		}
		catch(IOException e)
		{
			throw e;
		}
		
		// set basic behaviors
		con.setConnectTimeout(aConnectTimeout);
		con.setReadTimeout(aReadTimeout);
		con.setDefaultUseCaches(false);
		
		//		Log.d(TAG, "content-length: " + con.getContentLength());
		
		// set to POST
		try
		{
			con.setRequestMethod("POST");
		}
		catch (ProtocolException e)
		{
			throw e;
		}
		con.setDoInput(true);
		con.setDoOutput(true);
		
		// add additional header fields
		con=addHeaderFields(con, aHeaderFields);
		
		return con;
	}
};



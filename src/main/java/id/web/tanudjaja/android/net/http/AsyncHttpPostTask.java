
package id.web.tanudjaja.android.net.http;

import id.web.tanudjaja.android.common.NetUtils;
import id.web.tanudjaja.android.common.port.errno;
import id.web.tanudjaja.android.common.port.stdc;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.zip.GZIPInputStream;

import android.util.Log;

public class AsyncHttpPostTask extends AsyncHttpTask
{
	private final static String TAG="AsyncHttpPostTask";
	
	/**
	 * The constructor of JAsyncHttpPostTask. This constructor calls the constructor of JAsyncHttpTask and passes "POST" as the method.
	 * @param aUrl		The target URL.
	 * @param aContent	The content of a post request.
	 * @param aListener	The listener of a http-put operation.
	 */
	public AsyncHttpPostTask(HttpPostRequest aHttpRequest, AsyncHttpTaskListener aListener)
	{
		super(aHttpRequest, aListener);
	}

	@Override
	protected HttpResponse doInBackground(Void... params)
	{
		final byte [] content = ((HttpPostRequest)(mHttpRequest)).getContent();
		byte [] miniBuf = new byte[256];
		int bytesRead;

		HttpURLConnection con = null;
		
		Log.d(TAG, "checking cancellation before preparing http post with url: " + mHttpRequest.getUrl());
		if(isCancelled()==true)
		{
			Log.d(TAG, "is cancelled is true - cancelling");
			return createCancelResponse();
		}
		
		try
		{
			con=HttpConnectionFactory.setupPost(mHttpRequest.getUrl(),
				mConnectTimeout, mReadTimeout,
				mHttpRequest.getAdditionalHeaders());
		}
		catch(MalformedURLException e)
		{
			Log.d(TAG, "MalformedURLException raised");
			return new HttpResponse(
				errno.EFAULT,
				new byte[]{0},
				"");
		}
		catch(ProtocolException e)
		{
			Log.d(TAG, "ProtocolException raised");
			return new HttpResponse(
				errno.EPROTONOSUPPORT,
				new byte[]{0},
				"");
		}
		catch(IOException e)
		{
			Log.d(TAG, "IOException raised");
			return new HttpResponse(
				errno.EIO,
				new byte[]{0},
				"");
		}

        con.setInstanceFollowRedirects(true);

		Log.d(TAG, "checking cancellation before writing the content");
		if(isCancelled()==true)
		{
			Log.d(TAG, "is cancelled is true - cancelling");
			return createCancelResponse();
		}
		
		// write content to body
		if (content != null)
		{	
			try
			{
				DataOutputStream dos = new DataOutputStream(con.getOutputStream());
				dos.write(content);
				dos.flush();
				dos.close();
			}
			catch(IOException e)
			{
				Log.d(TAG, "exception raised: " + e.getMessage());
				return new HttpResponse(errno.EIO, new byte[]{0}, "");
			}
		}

		// try getting the response code
		int responseCode=-1;

		try
		{
			responseCode=con.getResponseCode();
		}
		catch(IOException e)
		{
			// upon failure, return KErrCouldNotConnect to the user
			Log.d(TAG, "exception occurs when obtaining response code: " + e.getMessage());
			return new HttpResponse(errno.ENOTCONN, new byte[]{0}, "");
		}
		
		if(responseCode<200 || responseCode>399)
		{
			// if the response is not 200, no need to get the input stream
			return new HttpResponse(responseCode, new byte[]{0}, "");
		}
		
		if(responseCode>=300 && responseCode<=300)
		{
			HttpResponse httpResponse = new HttpResponse(responseCode, new byte[]{0}, "");
			String location=con.getHeaderField("location");
			if(location != null)
			{
				httpResponse.setRedirectionUrl(con.getHeaderField("location"));
			}
			else
			{
				httpResponse.setRedirectionUrl("");
			}
			return httpResponse;
		}
		
		Log.d(TAG, "checking cancellation before determining the content type");
		if(isCancelled()==true)
		{
			Log.d(TAG, "is cancelled is true - cancelling");
			return createCancelResponse();
		}
		
		boolean isGzip=false;
		if(con.getContentEncoding()==null)
		{
			isGzip=false;
		}
		else
		{
			if(con.getContentEncoding().equalsIgnoreCase("gzip"))
			{
				isGzip=true;
			}
		}

		Log.d(TAG, "checking cancellation before getting the content");
		if(isCancelled()==true)
		{
			Log.d(TAG, "is cancelled is true - cancelling");
			return createCancelResponse();
		}
		
		// try getting the content
		InputStream is = null;
		if (isGzip==true)
		{
			Log.d(TAG, "ready to receive gzip");
			
			try
			{
				is = new GZIPInputStream(con.getInputStream());
			}
			catch (IOException e)
			{
				Log.d(TAG, "exception raised");
				return new HttpResponse(errno.EIO, new byte[]{0}, "");
			}
		}
		else
		{
			Log.d(TAG, "ready to receive plain text");			
			
			try 
			{
				is = con.getInputStream();
			}
			catch (IOException e)
			{
				Log.d(TAG, "exception raised");
				return new HttpResponse(errno.EIO, new byte[]{0}, "");
			}
		}
		
		// provide the buffer
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		Log.d(TAG, "checking cancellation before getting the content length");
		if(isCancelled()==true)
		{
			Log.d(TAG, "is cancelled is true - cancelling");
			return createCancelResponse();
		}

		int conLength=con.getContentLength();
		boolean conLengthAvailable=(conLength>0);
		Log.d(TAG, "content-length: " + conLength); 
		Log.d(TAG, "content-length is available: " + conLengthAvailable);
		
		Log.d(TAG, "checking cancellation before reading");
		if(isCancelled()==true)
		{
			Log.d(TAG, "is cancelled is true - cancelling");
			return createCancelResponse();
		}

		boolean cancelled=isCancelled();

		int totalBytesRead=0;
		while (!cancelled)
		{
			try
			{
				stdc.memset(miniBuf, 0x0, miniBuf.length);
				bytesRead = is.read(miniBuf);
				totalBytesRead += bytesRead;
				if(conLengthAvailable)
				{
					publishProgress((double)(totalBytesRead)/(conLength));
				}
			}
			catch (IOException e)
			{
				return new HttpResponse(errno.EIO, new byte[]{0}, "");
			}

			// returning -1 means finish
			if(bytesRead==-1)
			{
				break;
			}

			// append to buffer
			baos.write(miniBuf, 0, bytesRead);
			
			// checking cancellation
			cancelled=isCancelled();
		}
		
		// close the stream
		try
		{
			is.close();
		}
		catch (IOException e)
		{
		}
		
		if(cancelled)
		{
			Log.d(TAG, "break from loop because of cancel is called");
			return createCancelResponse();
		}
		else
		{
			Log.d(TAG, "break from loop because of download completion");
		}

        	return new HttpResponse(responseCode, baos.toByteArray(), con.getContentType());
	}
};

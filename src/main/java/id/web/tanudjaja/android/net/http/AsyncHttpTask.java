
package id.web.tanudjaja.android.net.http;

// Android's imports
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

// internal imports
import id.web.tanudjaja.android.common.port.errno;

public abstract class AsyncHttpTask extends AsyncTask<Void, Double, HttpResponse>
{
	// data
	protected final HttpRequest		mHttpRequest;
	protected AsyncHttpTaskListener	mListener;
	protected int				mConnectTimeout;
	protected int				mReadTimeout;
	
	private final static String TAG="AsyncHttpTask";

	/**
	 * The constructor of AsyncHttpTask
	 * @param aUrl		The Url to which the task is targeting.
	 * @param aObserver	The observer that reports the request completion state.
	 */
	public AsyncHttpTask(HttpRequest aHttpRequest, AsyncHttpTaskListener aListener)
	{
		mHttpRequest=aHttpRequest;
		mListener=aListener;
		mConnectTimeout=0;
		mReadTimeout=0;
	}
	
	/**
	 * The constructor of JAsyncHttpTask
	 * @param aUrl		The Url to which the task is targeting.
	 */
	public AsyncHttpTask(HttpRequest aHttpRequest)
	{
		mHttpRequest=aHttpRequest;
		mListener=null;
		mConnectTimeout=0;
		mReadTimeout=0;
	}
	
	/**
	 * This function set the listener for the task's operation.
	 * @param aListener	The listener to listen the task's operation.
	 * @return 			This object to ease setting.
	 */
	public AsyncHttpTask setListener(AsyncHttpTaskListener aListener)
	{
		mListener=aListener;
		return this;
	}
	
	/**
	 * This function cancels the operation.
	 */
	public int cancel()
	{
		if(isRunning())
		{
			Log.d(TAG, "Cancel - is running, cancelling now");
			cancel(true);			
		}
		else
		{
			Log.d(TAG, "Cancel - is not running, no use cancelling");
			return errno.ECANCELED;
		}
		
		return errno.SUCCESS;
	}

	/**
	 * This function set the connection timeout. The default value is 0 which will timeout in several minutes.
	 * @param aMs		The timeout value in milliseconds.
	 * @return 		This object to ease setting.
	 */
	public AsyncHttpTask setConnectTimeout(int aMs)
	{
		mConnectTimeout=aMs;
		return this;
	}
	
	/**
	 * This function set the read timeout. The default value is 0 which will attempt an indefinite timeout.
	 * @param aMs		The timeout value in milliseconds.
	 * @return 		This object to ease setting.
	 */
	public AsyncHttpTask setReadTimeout(int aMs)
	{
		mReadTimeout=aMs;
		return this;
	}

	/**
	 * This function generate an HttpResponse object with KErrCancel as the error code and empty String as the content and the content-type
	 * @return	The HttpResponse object.
	*/
	protected final HttpResponse createCancelResponse()
	{
		HttpResponse resp=new HttpResponse(errno.ECANCELED, new byte[]{0}, "");

		// force calling onCancelled if used by Android Gingerbread and below
		if(android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.GINGERBREAD_MR1)
		{
			onCancelled(resp);
		}
		return resp;
	}
	
	/**
	 * This function checks whether the task is running.
	 */
	@TargetApi(Build.VERSION_CODES.CUPCAKE)
	public boolean isRunning()
	{
		return getStatus()==AsyncTask.Status.RUNNING;
	}

	/**
	 * This function checks whether the task is already finished.
	 */
	@TargetApi(Build.VERSION_CODES.CUPCAKE)
	public boolean isFinished()
	{
		return getStatus()==AsyncTask.Status.FINISHED;
	}

	/**
	 * This function checks whether the task is not pending.
	 */
	@TargetApi(Build.VERSION_CODES.CUPCAKE)
	public boolean isPending()
	{
		return getStatus()==AsyncTask.Status.PENDING;
	}
	
	@Override
	public void onPostExecute(HttpResponse aResponse)
	{
		super.onPostExecute(aResponse);
		Log.d(TAG, "onPostExecute is called");
		if(mListener!=null) { mListener.onComplete(this, aResponse); }
	}
	
	@Override
	protected void onCancelled(HttpResponse aResponse)
	{
		// intentionally not calling the superclass's function as stated in the document.
//		super.onCancelled(aResponse);
		Log.d(TAG, "onCancelled is called");
		if(mListener!=null) { mListener.onCancelled(this); }
	}
	
	@Override
	protected void onProgressUpdate(Double... aProgress)
	{
		if(aProgress.length>0)
		{
			if(mListener!=null) { mListener.onProgress(this, aProgress[0]); }
		}
	}
};


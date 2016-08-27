
package id.tanudjaja.android.net.http;

import android.util.Log;
import id.web.tanudjaja.android.common.port.errno;

public class HttpTaskManager implements AsyncHttpTaskListener
{
	private final static String TAG="HttpTaskManager";
	
	protected class HttpRequestProps
	{
		public HttpRequestProps()
		{
			mPckg=null;
			mAgain=false;
			mConnTimeout=0;
			mReadTimeout=0;
			mTask=null;
		}
		
		public HttpRequestProps(HttpRequest aPckg, boolean aAgain, int aConnTimeout, int aReadTimeout, AsyncHttpTask aTask)
		{
			mPckg=aPckg;
			mAgain=aAgain;
			mConnTimeout=aConnTimeout;
			mReadTimeout=aReadTimeout;
			mTask=aTask;
		}
		
		public HttpRequest 	mPckg;
		public boolean		mAgain;
		public int		mConnTimeout;
		public int		mReadTimeout;
		public AsyncHttpTask	mTask;
	};
	
	// data
	private HttpRequestProps [] mPropList;
	private HttpTaskManagerListener mListener;
	private final int mDefaultConnectTimeout;
	private final int mDefaultReadTimeout;

	/**
	 * The constructor of HttpTaskManager.
	 * @param aRequestList The list of request to be sent to the server side
	 * @param aListener The listener to listen the status of the request
	 */
	public HttpTaskManager(int aRequestCount, HttpTaskManagerListener aListener)
	{
		int i;
		
		// create prop list
		mPropList=new HttpRequestProps[aRequestCount];
		
		// initiate prop list
		for(i=0; i<mPropList.length; i++)
		{
			mPropList[i]=new HttpRequestProps();
		}
		
		// set the listener
		mListener=aListener;

		// set default timeouts
		mDefaultConnectTimeout=0;
		mDefaultReadTimeout=0;
	}

	/**
	 * The constructor of HttpTaskManager.
	 * @param aRequestList The list of request to be sent to the server side
	 * @param aListener The listener to listen the status of the request
	 */
	private HttpTaskManager(int aRequestCount, HttpTaskManagerListener aListener, int aConnectTimeout, int aReadTimeout)
	{
		int i;
		
		// create prop list
		mPropList=new HttpRequestProps[aRequestCount];
		
		// initiate prop list
		for(i=0; i<mPropList.length; i++)
		{
			mPropList[i]=new HttpRequestProps();
		}
		
		// set the listener
		mListener=aListener;

		// set default timeouts
		mDefaultConnectTimeout=aConnectTimeout;
		mDefaultReadTimeout=aReadTimeout;
	}
	
	public int setPckg(int aIndex, HttpRequest aRequest)
	{
		if(aIndex<0 || aIndex>=mPropList.length)
		{
			return errno.EINVAL;
		}
		
		mPropList[aIndex].mPckg=aRequest;
		
		return errno.SUCCESS;
	}
	
	public HttpRequest getPckg(int aIndex)
	{
		if(aIndex<0 || aIndex>=mPropList.length)
		{
			return null;
		}
		
		return mPropList[aIndex].mPckg;
	}
	
	/**
	 * The function to execute the request.
	 * @param aIndex				The index of request to be executed.
	 * @param aInterruptIfRunning	Determine whether the currently running request should be cancelled.
	 * @param aConnectTimeOut		The time to wait when establishing connection.
	 * @param aReadTimeOut			The time to wait until the next byte is received.
	 * @return						The status of the operation.
	 */
	public synchronized int execute(int aIndex, boolean aInterruptIfRunning, int aConnectTimeOut, int aReadTimeOut)
	{
		if(aIndex<0 || aIndex>=mPropList.length)
		{
			return errno.EINVAL;
		}
		
		// get reference to selected task
		AsyncHttpTask task=mPropList[aIndex].mTask;
		
		// check if task is running
		if(task!=null)
		{
			Log.d(TAG, "Execute - task is still exist");
			
			if(task.isRunning())
			{
				Log.d(TAG, "Execute - task is still running");
				if(aInterruptIfRunning==true)
				{
					Log.d(TAG, "Execute - task is still running but interruption is allowed. Cancelling now.");
					mPropList[aIndex].mAgain=true;
					mPropList[aIndex].mConnTimeout=aConnectTimeOut;
					mPropList[aIndex].mReadTimeout=aReadTimeOut;
					task.cancel();
					return errno.SUCCESS;
				}
				else
				{
					Log.d(TAG, "Execute - task is still running and interruption is not allowed. Returning now");
					return errno.EBUSY;
				}
			}
			else // task is not running
			{
				Log.d(TAG, "Execute - task is not running.");
				// execute
				try
				{
					mPropList[aIndex].mConnTimeout=aConnectTimeOut;
					mPropList[aIndex].mReadTimeout=aReadTimeOut;
					task.setConnectTimeout(aConnectTimeOut).setReadTimeout(aReadTimeOut).execute();
				}
				// IsRunning() is false because the task is finished, renew the task!
				catch(IllegalStateException e)
				{
					Log.d(TAG, "Execute - illegal state exception");			
					mPropList[aIndex].mTask=null;
					execute(aIndex, aInterruptIfRunning, aConnectTimeOut, aReadTimeOut);
				}
				return errno.SUCCESS;
			}
		}
		
		Log.d(TAG, "Execute - selecting request.");
		
		//  get reference to selected request package
		HttpRequest selectedRequest=mPropList[aIndex].mPckg;

		// create task object based on the method
		if(selectedRequest.getMethod().equalsIgnoreCase("GET"))
		{
			Log.d(TAG, "Execute - arranging GET request.");
			HttpGetRequest req=(HttpGetRequest)(selectedRequest);
			mPropList[aIndex].mTask=new AsyncHttpGetTask(req, this);
		}
		else if(selectedRequest.getMethod().equalsIgnoreCase("POST"))
		{
			Log.d(TAG, "Execute - arranging POST request.");
			HttpPostRequest req=(HttpPostRequest)(selectedRequest);
			mPropList[aIndex].mTask=new AsyncHttpPostTask(req, this);
		}
		else
		{
			Log.d(TAG, "Execute - unknown type of request.");
			return errno.EOPNOTSUPP;
		}

		Log.d(TAG, "Execute - executing");
		
		// execute
		try
		{
			mPropList[aIndex].mTask.setConnectTimeout(aConnectTimeOut).setReadTimeout(aReadTimeOut).execute();
		}
		catch(IllegalStateException e)
		{
			Log.d(TAG, "Execute - illegal state exception");
			mPropList[aIndex].mTask=null;
			execute(aIndex, aInterruptIfRunning, aConnectTimeOut, aReadTimeOut);
		}

		Log.d(TAG, "Execute - task created");
		
		return errno.SUCCESS;
	}
	
	/**
	 * The function to execute the request with default connect and read timeout. Similar with calling Execute(aIndex, aInterruptIfRunning, 0, 0)
	 * @param aIndex			The index of request to be executed.
	 * @return					The status of the operation.
	 */
	public int execute(int aIndex, boolean aInterruptIfRunning)
	{
		return execute(aIndex, aInterruptIfRunning, mDefaultConnectTimeout, mDefaultReadTimeout);
	}
	
	/**
	 * The function cancels an ongoing request.
	 * @param aIndex	The index of ongoing request to be canceled.
	 * @return			The status of the operation.
	 */
	public int cancel(int aIndex)
	{
		if(aIndex<0 || aIndex>=mPropList.length)
		{
			return errno.EINVAL;
		}
		
		return mPropList[aIndex].mTask.cancel();
	}
	
	/**
	 * The function finds the address of the task and find the matching address in iPropList.
	 * @param aTask	The task.
	 * @return		The index of the task or -1 if the task is not in iPropList.
	 */
	private int findTaskIndex(AsyncHttpTask aTask)
	{
		int i;
		for(i=0; i<mPropList.length; i++)
		{
			if(aTask==mPropList[i].mTask) { return i; }
		}
		
		return errno.ENODATA;
	}
	
	@Override
	public void onComplete(AsyncHttpTask aTask, HttpResponse aResponse)
	{
		Log.d(TAG, "OnComplete - start");

		int idx=findTaskIndex(aTask);
		
		// orphan task found
		if(idx==errno.ENODATA)
		{
			Log.d(TAG, "OnComplete - task is orphant");
			return;
		}
		
		Log.d(TAG, "OnComplete - idx found: " + idx);

		// report to listener
		if(mListener!=null)
		{
			Log.d(TAG, "OnComplete - propagating to listener");
			mListener.onComplete(this, idx, aResponse);
		}
	}

	@Override
	public void onCancelled(AsyncHttpTask aTask)
	{
		Log.d(TAG, "OnCancelled - start");
		
		int idx=findTaskIndex(aTask);
		
		// orphan task found
		if(idx==errno.ENODATA)
		{
			Log.d(TAG, "OnCancelled - task is orphant");
			return;
		}
		
		if(mPropList[idx].mAgain==false)
		{
			Log.d(TAG, "OnCancelled - should stop");
			if(mListener!=null) { mListener.onCancelled(this, idx); }
			return;
		}
		else
		{
			Log.d(TAG, "OnCancelled - should re-request");
			
			mPropList[idx].mAgain=false;
			
			// create task object based on the method
			HttpRequest selectedRequest=mPropList[idx].mPckg;
			Log.d(TAG, "OnCancelled - Request Method: " + selectedRequest.getMethod());
			
			if(selectedRequest.getMethod().equalsIgnoreCase("GET"))
			{
				HttpGetRequest req=(HttpGetRequest)(selectedRequest);
				mPropList[idx].mTask=new AsyncHttpGetTask(req, this);
			}
			else if(selectedRequest.getMethod().equalsIgnoreCase("POST"))
			{
				HttpPostRequest req=(HttpPostRequest)(selectedRequest);
				mPropList[idx].mTask=new AsyncHttpPostTask(req, this);
			}
			else
			{
				return;
			}

			Log.d(TAG, "OnCancelled - executing task");

			// execute
			try
			{
				mPropList[idx].mTask
					.setConnectTimeout(mPropList[idx].mConnTimeout)
					.setReadTimeout(mPropList[idx].mReadTimeout).
					execute();
			}
			catch(IllegalStateException e)
			{
				Log.d(TAG, "OnCancelled - illegal state exception : " + e.getMessage());			
			}
		}
	}

	@Override
	public void onProgress(AsyncHttpTask aTask, double aPercentage)
	{
		int idx=findTaskIndex(aTask);
		
		// orphan task found
		if(idx==errno.ENODATA)
		{
			return;
		}
		
		// report to listener
		if(mListener!=null) { mListener.onProgress(this, idx, aPercentage); }
	}
};

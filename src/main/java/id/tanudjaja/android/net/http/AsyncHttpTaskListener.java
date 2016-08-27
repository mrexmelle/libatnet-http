
package id.tanudjaja.android.net.http;

public interface AsyncHttpTaskListener
{
	public abstract void onComplete(AsyncHttpTask aTask, HttpResponse aResponse);
	public abstract void onProgress(AsyncHttpTask aTask, double aPercentage);
	public abstract void onCancelled(AsyncHttpTask aTask);		
};

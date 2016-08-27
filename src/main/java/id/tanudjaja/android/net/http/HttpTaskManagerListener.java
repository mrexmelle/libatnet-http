
package id.tanudjaja.android.net.http;

public interface HttpTaskManagerListener
{
	public abstract void onComplete(HttpTaskManager aManager, int aIndex, HttpResponse aResponse);
	public abstract void onProgress(HttpTaskManager aManager, int aIndex, double aPercentage);
	public abstract void onCancelled(HttpTaskManager aManager, int aIndex);
};

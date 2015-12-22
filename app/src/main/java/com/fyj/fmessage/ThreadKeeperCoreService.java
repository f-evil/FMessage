package com.fyj.fmessage;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import java.util.List;

public class ThreadKeeperCoreService extends Service {

	private String TAG = "ThreadKeeperCoreService";
	private Thread repeatQueryThread;
	private boolean flag = true;
	private boolean twoIsRun = false;

	public ThreadKeeperCoreService() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		repeatQueryThread = new Thread(new Runnable() {
			@Override
			public void run() {


				while (flag) {
					try {
						repeatQueryThread.sleep(1000 * 4);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					twoIsRun = isServiceRunning("com.fyj.fmessage.MyTelService");

					if (!twoIsRun) {
						Intent mainActivityIntent = new Intent(ThreadKeeperCoreService.this, MyTelService.class);  //
						mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						mainActivityIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
						startService(mainActivityIntent);
					}

				}
			}
		});
		repeatQueryThread.start();
		foregroundNotif();
		return START_STICKY;
	}

	private void foregroundNotif() {
		Notification notification = new Notification();
		notification.flags = Notification.FLAG_ONGOING_EVENT;
		notification.flags |= Notification.FLAG_NO_CLEAR;
		notification.flags |= Notification.FLAG_FOREGROUND_SERVICE;
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
			notification.priority = Notification.PRIORITY_MIN;
		}
		this.startForeground(1120, notification);
	}

	@Override
	public void onDestroy() {
		stopForeground(true);
		super.onDestroy();
	}

	public boolean isServiceRunning(String serviceClassName) {
		final ActivityManager activityManager = (ActivityManager) ThreadKeeperCoreService.this.getSystemService(Context
				.ACTIVITY_SERVICE);
		final List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer
				.MAX_VALUE);

		for (ActivityManager.RunningServiceInfo runningServiceInfo : services) {
			if (runningServiceInfo.service.getClassName().contains(serviceClassName)) {
				return true;
			}
		}
		return false;
	}
}

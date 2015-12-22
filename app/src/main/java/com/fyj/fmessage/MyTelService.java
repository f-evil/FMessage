package com.fyj.fmessage;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * fyj
 */
public class MyTelService extends Service {

	private LocalBroadcastManager lbm;
	private BroadcastReceiver mReceiver;

	private SharedPreferences sp;
	private SmsManager sms;

	private Thread repeatQueryThread;
	private boolean flag = true;
	private boolean twoIsRun = false;

	public MyTelService() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		sp = getSharedPreferences("configs", Activity.MODE_PRIVATE);
		sms = SmsManager.getDefault();

		lbm = LocalBroadcastManager.getInstance(this);
		mReceiver = new BroadcastReceiver() {
			SmsMessage msg = null;

			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equals(BroadCmd.SEND_MESSAGE)) {

				} else if (intent.getAction().equals(BroadCmd.GET_MESSAGE)) {
					String tel = sp.getString("tel", "");
					if (null != tel && !tel.isEmpty()) {
						Bundle bundle = intent.getExtras();
						Object[] pdusObj = (Object[]) bundle.get("pdus");
						for (Object p : pdusObj) {
							msg = SmsMessage.createFromPdu((byte[]) p);
							sengMeg(tel, formatMsg(msg));
						}

					}
				}
			}

		};

		lbm.registerReceiver(mReceiver, new IntentFilter(BroadCmd.SEND_MESSAGE));
		lbm.registerReceiver(mReceiver, new IntentFilter(BroadCmd.GET_MESSAGE));
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		lbm.sendBroadcast(new Intent(BroadCmd.START_SERVICE));
		flag = true;

		repeatQueryThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (flag) {
					try {
						repeatQueryThread.sleep(1000 * 4);

						twoIsRun = isServiceRunning("com.fyj.fmessage.ThreadKeeperCoreService");

						if (!twoIsRun) {
							startService(new Intent(MyTelService.this, ThreadKeeperCoreService.class));
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		repeatQueryThread.start();
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		flag = false;
		lbm.sendBroadcast(new Intent(BroadCmd.STOP_SERVICE));
		lbm.unregisterReceiver(mReceiver);
	}

	private String formatMsg(SmsMessage msg) {

		String msgTxt = msg.getMessageBody();

		Date date = new Date(msg.getTimestampMillis());
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String receiveTime = format.format(date);
		String senderNumber = msg.getOriginatingAddress();

		String formatContent = "发送人：" + senderNumber + "\n" + "短信内容：" + msgTxt + "\n" + "接受时间：" +
				receiveTime;
		return formatContent;
	}

	private void sengMeg(String tel, String msg) {
		sms.sendTextMessage(tel, null, msg, null, null);
	}

	public boolean isServiceRunning(String serviceClassName) {
		final ActivityManager activityManager = (ActivityManager) MyTelService.this.getSystemService(Context
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

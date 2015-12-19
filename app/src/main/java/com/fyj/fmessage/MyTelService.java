package com.fyj.fmessage;

import android.app.Activity;
import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * fyj
 */
public class MyTelService extends Service {

	private LocalBroadcastManager lbm;
	private BroadcastReceiver mReceiver;

	private SharedPreferences sp;
	private SmsManager sms;

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
		foregroundNotif();
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		lbm.sendBroadcast(new Intent(BroadCmd.STOP_SERVICE));
		lbm.unregisterReceiver(mReceiver);
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

}

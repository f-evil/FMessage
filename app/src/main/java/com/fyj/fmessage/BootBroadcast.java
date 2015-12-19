package com.fyj.fmessage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

/**
 * fyj
 */
public class BootBroadcast extends BroadcastReceiver {

	private LocalBroadcastManager lbm;

	final static String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(SMS_RECEIVED)) {
			Bundle bundle = intent.getExtras();
			lbm = LocalBroadcastManager.getInstance(context);
			Intent i = new Intent(BroadCmd.GET_MESSAGE);
			i.putExtras(bundle);
			lbm.sendBroadcast(i);
		}
	}


}

package com.fyj.fmessage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

/**
 * fyj
 */
public class BootBroadcast extends BroadcastReceiver {

	private LocalBroadcastManager lbm;

	final static String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (lbm==null){
			lbm = LocalBroadcastManager.getInstance(context);
		}
		if (intent.getAction().equals(SMS_RECEIVED)) {
			if (Global.isStart){
				Bundle bundle = intent.getExtras();
				Intent i = new Intent(BroadCmd.GET_MESSAGE);
				i.putExtras(bundle);
				lbm.sendBroadcast(i);
			}else {
				lbm.sendBroadcast(new Intent(BroadCmd.NO_START_SERVICE));
			}

		}
	}


}

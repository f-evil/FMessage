package com.fyj.fmessage;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


/**
 * fyj
 */
public class MainActivity extends Activity {

	/**
	 * 所有的短信
	 */
	public static final String SMS_URI_ALL = "content://sms/";
	/**
	 * 收件箱短信
	 */
	public static final String SMS_URI_INBOX = "content://sms/inbox";
	/**
	 * 发件箱短信
	 */
	public static final String SMS_URI_SEND = "content://sms/sent";
	/**
	 * 草稿箱短信
	 */
	public static final String SMS_URI_DRAFT = "content://sms/draft";

	private TextView mTelTextView;
	private EditText mTelEditText;
	private Button mSaveButton;
	private Button mStopMessageButton;
	private Button mStartButton;

	private SharedPreferences sp;
	private SharedPreferences.Editor editor;

	private LocalBroadcastManager lbm;
	private BroadcastReceiver mReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		mTelTextView = (TextView) findViewById(R.id.tv_tel);
		mTelEditText = (EditText) findViewById(R.id.ed_tel);
		mSaveButton = (Button) findViewById(R.id.btn_save);
		mStartButton = (Button) findViewById(R.id.btn_start);
		mStopMessageButton = (Button) findViewById(R.id.btn_stop_message);

		lbm = LocalBroadcastManager.getInstance(this);

		mTelEditText = (EditText) findViewById(R.id.ed_tel);
		mSaveButton = (Button) findViewById(R.id.btn_save);
		mStartButton = (Button) findViewById(R.id.btn_start);

		sp = getSharedPreferences("configs", Activity.MODE_PRIVATE);
		editor = sp.edit();

		String telTemp = sp.getString("tel", "");

		mTelEditText.setText(telTemp);

		mSaveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				saveTel();
			}
		});

		mStartButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				sendMessage();
			}
		});

		mStopMessageButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				stopService();
			}
		});

		mReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equals(BroadCmd.START_SERVICE)) {
					Toast.makeText(MainActivity.this, "短信转发服务启动", Toast.LENGTH_SHORT).show();
				} else if (intent.getAction().equals(BroadCmd.STOP_SERVICE)) {
					Toast.makeText(MainActivity.this, "短信转发服务关闭", Toast.LENGTH_SHORT).show();
				}
			}
		};

		lbm.registerReceiver(mReceiver, new IntentFilter(BroadCmd.START_SERVICE));
		lbm.registerReceiver(mReceiver, new IntentFilter(BroadCmd.STOP_SERVICE));

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		lbm.unregisterReceiver(mReceiver);
	}

	private void saveTel() {
		editor.putString("tel", mTelEditText.getText().toString());
		editor.commit();
		Toast.makeText(MainActivity.this, "手机号保存成功", Toast.LENGTH_SHORT).show();
	}

	private void sendMessage() {
		String telTemp = mTelEditText.getText().toString();
		if (null != telTemp && !telTemp.isEmpty()) {
			Intent i = new Intent(MainActivity.this, MyTelService.class);
			startService(i);
		}
	}

	private void stopService() {
		Intent i = new Intent(MainActivity.this, MyTelService.class);
		stopService(i);
	}

	private void yuliy() {
		Uri uri = Uri.parse("smsto://18658433792");

		Intent intent = new Intent(Intent.ACTION_SENDTO, uri);

		intent.putExtra("sms_body", "send detail");

		startActivity(intent);
	}

}

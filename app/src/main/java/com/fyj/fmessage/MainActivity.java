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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


/**
 * fyj
 */
public class MainActivity extends Activity {

	private TextView tv_about;
	private EditText mTelEditText;
	private Button mSaveButton;
	private Button mStopMessageButton;
	private Button mStartButton;

	private ImageView mBack;
	private TextView mTitle;

	private SharedPreferences sp;
	private SharedPreferences.Editor editor;

	private LocalBroadcastManager lbm;
	private BroadcastReceiver mReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		initView();
		initBroad();
		getDataFromSp();
		bindEvent();

	}

	private void bindEvent(){

		mBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 注意
				intent.addCategory(Intent.CATEGORY_HOME);
				startActivity(intent);
			}
		});

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

		tv_about.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivity(new Intent(MainActivity.this,AboutActivity.class));
			}
		});
	}


	private void getDataFromSp(){
		sp = getSharedPreferences("configs", Activity.MODE_PRIVATE);
		editor = sp.edit();

		String telTemp = sp.getString("tel", "");

		mTelEditText.setText(telTemp);
	}

	private void initView(){

		mBack = (ImageView) findViewById(R.id.btnBack1);
		mTitle = (TextView) findViewById(R.id.txtTitle1);

		tv_about = (TextView) findViewById(R.id.tv_about);
		mTelEditText = (EditText) findViewById(R.id.ed_tel);
		mSaveButton = (Button) findViewById(R.id.btn_save);
		mStartButton = (Button) findViewById(R.id.btn_start);
		mStopMessageButton = (Button) findViewById(R.id.btn_stop_message);

		mTelEditText = (EditText) findViewById(R.id.ed_tel);
		mSaveButton = (Button) findViewById(R.id.btn_save);
		mStartButton = (Button) findViewById(R.id.btn_start);

		mTitle.setText("Android 短信自动转发工具");
	}

	private void initBroad(){
		lbm = LocalBroadcastManager.getInstance(this);

		mReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equals(BroadCmd.START_SERVICE)) {
					Toast.makeText(MainActivity.this, "短信转发服务启动", Toast.LENGTH_SHORT).show();
				} else if (intent.getAction().equals(BroadCmd.STOP_SERVICE)) {
					Toast.makeText(MainActivity.this, "短信转发服务关闭", Toast.LENGTH_SHORT).show();
				}else if (intent.getAction().equals(BroadCmd.NO_START_SERVICE)){
					Toast.makeText(MainActivity.this, "短信转发服务未开启,无法转发短信", Toast.LENGTH_SHORT).show();
				}
			}
		};

		lbm.registerReceiver(mReceiver, new IntentFilter(BroadCmd.START_SERVICE));
		lbm.registerReceiver(mReceiver, new IntentFilter(BroadCmd.STOP_SERVICE));
		lbm.registerReceiver(mReceiver, new IntentFilter(BroadCmd.NO_START_SERVICE));
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		Global.isStart=false;
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
			Global.isStart=true;
			Intent i = new Intent(MainActivity.this, MyTelService.class);
			startService(i);
		}
	}

	private void stopService() {
		Global.isStart=false;
		Intent i = new Intent(MainActivity.this, MyTelService.class);
		stopService(i);
	}


}

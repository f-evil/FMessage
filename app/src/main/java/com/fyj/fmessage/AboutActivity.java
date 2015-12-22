package com.fyj.fmessage;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class AboutActivity extends Activity {

	private ImageView mBack;
	private TextView mTitle;
	private TextView tv_about;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		initView();
		bindEvent();
	}

	private void bindEvent() {
		mBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
	}

	private void initView() {
		mBack = (ImageView) findViewById(R.id.btnBack1);
		mTitle = (TextView) findViewById(R.id.txtTitle1);
		tv_about = (TextView) findViewById(R.id.tv_about);

		mTitle.setText("关于");
		tv_about.setText("短信转发费用自理"+"\n"+"不会产生其他额外费用!"+"\n"+"项目地址:https://github.com/f-evil/FMessage/");
	}


}

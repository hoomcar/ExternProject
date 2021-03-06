package com.androidhuman.example.CameraPreview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

@SuppressLint("HandlerLeak")
public class SplashActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.splash);
		final Intent indent = new Intent(this,CameraPreview.class);
		Handler handler = new Handler () {
			
			@Override
			public void handleMessage(Message msg) {
				finish();
				startActivity(indent);
			}
		};

		handler.sendEmptyMessageDelayed(0, 2000);
	}
}



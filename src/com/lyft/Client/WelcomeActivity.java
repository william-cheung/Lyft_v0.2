package com.lyft.Client;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class WelcomeActivity extends Activity {
	public boolean finished = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_welcome);
		finished = false;
		View contentView = findViewById(R.id.fullscreen_content);
		contentView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivity(new Intent(getApplicationContext(), MainActivity.class));
				WelcomeActivity.this.finish();
				finished = true;
			}
		});
		AutoCloseThread thread = new AutoCloseThread();
		thread.start();
	}
	
	public class AutoCloseThread extends Thread {
		@Override
		public void run() {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) { }
			if (!finished) {
				startActivity(new Intent(getApplicationContext(), MainActivity.class));
				WelcomeActivity.this.finish();
				System.out.println("Thread Close");
				finished = true;
			}
		}
	}
}

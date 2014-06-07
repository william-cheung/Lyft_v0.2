package com.lyft.Client;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class UserAccountManager extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);   
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);   
		setContentView(R.layout.activity_user_account_manager);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.default_title_bar);
		
		ImageButton closeButton = (ImageButton) findViewById(R.id.close_button);
		closeButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				UserAccountManager.this.finish();
			}
		});
	}

}

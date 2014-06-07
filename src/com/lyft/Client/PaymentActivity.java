package com.lyft.Client;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;

public class PaymentActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);   
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);   
		setContentView(R.layout.activity_payment);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.default_title_bar);;
		
		ImageButton closeButton = (ImageButton) findViewById(R.id.close_button);
		closeButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				PaymentActivity.this.finish();
			}
		});
		
		Button makePayment = (Button) findViewById(R.id.pay_button);
		makePayment.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				PaymentActivity.this.finish();
			}
		});
	}

}

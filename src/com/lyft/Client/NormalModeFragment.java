package com.lyft.Client;

import java.util.ArrayList;

import com.lyft.BasicClasses.Location;
import com.lyft.InteractionLayer.InteractionLayer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;

public class NormalModeFragment extends MapFragment {

	private RequestThread requestThread = null;

	private EditText addressEditor = null;
	private AlertDialog requestDialog = null;

	private PopupWindow driverInfoPanel = null;

	public NormalModeFragment() {
		super(R.layout.normal_mode_fragment, R.id.passenger_mapview);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View view = super.onCreateView(inflater, container, savedInstanceState);

		addressEditor = (EditText) view.findViewById(R.id.passenger_location_editor);

		Button requestButton = (Button) view.findViewById(R.id.passenger_request_button);
		requestButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showRequestInfoDialog();
			}
		});

		ImageButton getAddressButton = (ImageButton) view.findViewById(R.id.passenger_locate);
		getAddressButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				CharSequence address = getAddrDistrict() + getAddrStreet() + getAddrStreetNumber();
				addressEditor.setText(address);
				addressEditor.setSelection(address.length());
				animateToCurrentLocation();
			}
		});

		return view;
	}

	private void showRequestInfoDialog() {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View layout = inflater.inflate(R.layout.passenger_request_dialog,
				(ViewGroup) getActivity().findViewById(R.layout.passenger_request_dialog));	
		final EditText srcAddressText = (EditText) layout.findViewById(R.id.dialog_src_editor);
		srcAddressText.setText(addressEditor.getText());
		final EditText dstAddressText = (EditText) layout.findViewById(R.id.dialog_dst_editor);
		dstAddressText.setText("");		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		Button okButton = (Button) layout.findViewById(R.id.dialog_button_ok);
		okButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (requestDialog != null) {
					String fromAddress = srcAddressText.getText().toString();
					String toAddress = dstAddressText.getText().toString();
					System.out.println("From : " + fromAddress + " To : " + toAddress);
					closeInputMethod();
					requestDialog.cancel();
				}
			}
		});
		Button cancelButton = (Button) layout.findViewById(R.id.dialog_button_cancel);
		cancelButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (requestDialog != null) {
					closeInputMethod();
					requestDialog.cancel();
				}
			}
		});
		requestDialog = builder.setView(layout).show();
	}

	private void closeInputMethod() {
		InputMethodManager inputMethodManager = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);  
		inputMethodManager.hideSoftInputFromWindow(requestDialog.getCurrentFocus().getWindowToken(),  
				InputMethodManager.HIDE_NOT_ALWAYS);
	}

	public class MyOnTapListener implements OnTapOverLayItemListener {
		int userid; 

		public MyOnTapListener(int userid) {
			this.userid = userid;
		}
		@Override
		public void onTap() {
			// TODO
			System.out.println("Tap from " + userid);
			showPopupWindow();
		}
	}
	
	@SuppressWarnings("deprecation")
	private void showPopupWindow() {
		LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
		View layout = layoutInflater.inflate(R.layout.driver_info_panel, null);
		driverInfoPanel  = new PopupWindow(layout, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		driverInfoPanel.setTouchable(true);
		driverInfoPanel.setOutsideTouchable(true);
		driverInfoPanel.setBackgroundDrawable(new BitmapDrawable());
		driverInfoPanel.setAnimationStyle(R.style.PopupWindowAnim3);
		driverInfoPanel.showAtLocation(getView().findViewById(R.id.fragment_passenger_layout), Gravity.BOTTOM, 5, 5);
		System.out.println("Show PopupWindow");
		
		View startPaymentButton = layout.findViewById(R.id.start_payment_button);
		startPaymentButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {	
				startActivity(new Intent(getActivity(), PaymentActivity.class));
				if (driverInfoPanel != null) {
					driverInfoPanel.dismiss();
				}
			}
		});
	}

	public class RequestThread extends Thread {
		public boolean isStoped = false;
		@Override
		public void run() {
			while (!isStoped) {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {}

				InteractionLayer interaction = InteractionLayer.getInstance();
				ArrayList<com.lyft.BasicClasses.LocationData> locationDataList = 
						interaction.dowloadDriverLocationData(InteractionLayer.myUID, 2000);
				clearOverlayItems();
				for (com.lyft.BasicClasses.LocationData locdata : locationDataList) {
					Location location = locdata.getLocation();
					int uid = locdata.getUserId();
					addOverlayItem(location.getLatitude(), location.getLongitude(), 
							R.drawable.car_marker_black, new MyOnTapListener(uid));
				}
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {}
				System.out.println("Message From Passenger Thread");
			}
		}

		public void forceStop() {
			isStoped = true;
		}

		public void start() {
			isStoped = false;
			super.start();
		}
	}

	@Override
	public void onPause() {
		if (requestThread != null) {
			requestThread.forceStop();
		}
		System.out.println("OnPause Passenger");
		super.onPause();
	}

	@Override
	public void onResume() {
		if (requestThread != null) {
			requestThread.isStoped = false;
			requestThread = null;
			System.out.println("Null Thread Passenger");
		} 

		requestThread = new RequestThread(); 
		requestThread.start();
		System.out.println("Start Thread Passenger");
		System.out.println("OnResume Passenger");
		super.onResume();
	}

	@Override
	public void onDestroy() {
		if (requestThread != null) {
			requestThread.forceStop();
		}
		if (driverInfoPanel != null) {
			driverInfoPanel.dismiss();
		}
		super.onDestroy();
	}
}

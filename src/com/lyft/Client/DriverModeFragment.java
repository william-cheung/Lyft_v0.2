package com.lyft.Client;

import java.util.ArrayList;

import com.lyft.BasicClasses.Location;
import com.lyft.InteractionLayer.InteractionLayer;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;

public class DriverModeFragment extends MapFragment {

	RequestThread requestThread = null;
	PopupWindow passengerInfoPanel = null;

	public DriverModeFragment() {
		super(R.layout.driver_mode_fragment, R.id.driver_mapview);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View view = super.onCreateView(inflater, container, savedInstanceState);

		ImageButton locateButton = (ImageButton) view.findViewById(R.id.driver_locate_button);
		locateButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				animateToCurrentLocation();
			}
		});

		//		passengerInfoPanel = (LinearLayout) view.findViewById(R.id.passenger_info_pannel);
		//		passengerInfoPanel.setVisibility(View.GONE);

		return view;
	}

	public class MyOnTapListener implements OnTapOverLayItemListener {
		int userid; 

		public MyOnTapListener(int userid) {
			this.userid = userid;
		}
		@Override
		public void onTap() {
			System.out.println("Tap from " + userid);
			//passengerInfoPanel.setVisibility(View.VISIBLE);
			showPopupWindow();
		}
	}

	@SuppressWarnings("deprecation")
	private void showPopupWindow() {
		LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
		View layout = layoutInflater.inflate(R.layout.passenger_info_panel, null);
		passengerInfoPanel = new PopupWindow(layout, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		passengerInfoPanel.setTouchable(true);
		passengerInfoPanel.setOutsideTouchable(true);
		passengerInfoPanel.setBackgroundDrawable(new BitmapDrawable());
		Button button = (Button) layout.findViewById(R.id.driver_agree_button);
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO
				passengerInfoPanel.dismiss();	
			}
		});
		passengerInfoPanel.setAnimationStyle(R.style.PopupWindowAnim3);
		passengerInfoPanel.showAtLocation(getView().findViewById(R.id.driver_map_layout), Gravity.BOTTOM, 5, 5);
		System.out.println("Show PopupWindow");
	}

	public class RequestThread extends Thread {
		boolean isStoped = false;
		@Override
		public void run() {
			while (!isStoped) {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {}

				InteractionLayer interaction = InteractionLayer.getInstance();
				ArrayList<com.lyft.BasicClasses.LocationData> locationDataList = 
						interaction.dowloadPassengerLocationData(InteractionLayer.myUID, 2000);
				clearOverlayItems();
				for (com.lyft.BasicClasses.LocationData locdata : locationDataList) {
					Location location = locdata.getLocation();
					int uid = locdata.getUserId();
					addOverlayItem(location.getLatitude(), location.getLongitude(), 
							R.drawable.generic_woman, new MyOnTapListener(uid));
				}

				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {}

				System.out.println("Message From Driver Thread");
			}
		}

		public void forceStop() {
			isStoped = true;
		}

		@Override
		public void start() {
			isStoped = false;
			if (!this.isAlive()) {
				super.start();
			}
		}
	}

	@Override
	public void onPause() {
		if (requestThread != null) {
			requestThread.forceStop();
		}
		System.out.println("OnPause Driver");
		super.onPause();
	}

	@Override
	public void onResume() {
		if (requestThread != null) {
			requestThread.isStoped = false;
			requestThread = null;
			System.out.println("Null Thread Driver");
		} 

		requestThread = new RequestThread(); 
		requestThread.start();
		System.out.println("Start Thread Driver");
		System.out.println("OnResume Driver");
		super.onResume();
	}

	@Override
	public void onDestroy() {
		if (requestThread != null) {
			requestThread.forceStop();
		}
		if (passengerInfoPanel != null) {
			passengerInfoPanel.dismiss();
		}
		super.onDestroy();
	}
}

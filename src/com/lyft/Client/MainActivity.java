package com.lyft.Client;

import android.os.Bundle;
import android.app.FragmentManager;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;

public class MainActivity extends Activity {

	private ImageButton changeRoleButton = null;
	private boolean isDriverMode = false;
	
	private ImageButton plusButton = null;
	private int plusButtonStatus = +1;
	private PopupWindow popupMenu = null;
	private PopupWindow auxWindow = null;
	private View[] menuItems = new View[0];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.MyCustomTheme);
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);   
		setContentView(R.layout.activity_main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.main_title_bar);
		
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction()
			.replace(R.id.frame_container, new NormalModeFragment()).commit();
		
		initPopupMenu();
		
		changeRoleButton = (ImageButton) findViewById(R.id.change_role_button);
		changeRoleButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Fragment fragment = null;
				if (isDriverMode) {
					changeRoleButton.setImageResource(R.drawable.driver_mode);
					isDriverMode = false; 
					fragment = new NormalModeFragment();
				} else {
					changeRoleButton.setImageResource(R.drawable.normal_mode);
					isDriverMode = true;
					fragment = new DriverModeFragment();
				}
				if (popupMenu != null) {
					popupMenu.dismiss();
				}
				if (fragment != null) {
					FragmentManager fragmentManager = getFragmentManager();
					fragmentManager.beginTransaction()
							.replace(R.id.frame_container, fragment).commit();
				}
			}
		});

		plusButton = (ImageButton) findViewById(R.id.plus_button);
		plusButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (plusButtonStatus == +1) {
					plusButton.setImageResource(R.drawable.minus_icon);
					plusButtonStatus = -1;
					auxWindow.showAsDropDown(findViewById(R.id.title_bar_layout), -auxWindow.getHeight(), 0);
					popupMenu.showAsDropDown(findViewById(R.id.title_bar_layout), -popupMenu.getHeight(), 0);
					
				} else {
					plusButton.setImageResource(R.drawable.plus_icon);
					plusButtonStatus = +1;
					if (popupMenu != null) {
						popupMenu.dismiss();
					}
				}
			}
		});
	}
	
	@SuppressWarnings("deprecation")
	private void initPopupMenu() {
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		View aux_layout = layoutInflater.inflate(R.layout.menu_outside, null);
		auxWindow = new PopupWindow(aux_layout, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		auxWindow.setTouchable(true);
		auxWindow.setOutsideTouchable(false);
		auxWindow.setBackgroundDrawable(new BitmapDrawable());
		auxWindow.setAnimationStyle(R.style.PopupWindowAnim2);
		aux_layout.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (popupMenu != null) {
					popupMenu.dismiss();
				}
			}
		});
		
		View menuayout = layoutInflater.inflate(R.layout.main_menu_popup, null);
		popupMenu = new PopupWindow(menuayout, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		popupMenu.setTouchable(true);
		popupMenu.setOutsideTouchable(false);
		popupMenu.setBackgroundDrawable(new BitmapDrawable());
		popupMenu.setAnimationStyle(R.style.PopupWindowAnim);
		popupMenu.setOnDismissListener(new OnDismissListener() {
			public void onDismiss() {
				auxWindow.dismiss();
				plusButton.setImageResource(R.drawable.plus_icon);
				plusButtonStatus = +1;
			}
		});
		
		menuItems = new View[4];
		menuItems[0] = menuayout.findViewById(R.id.user_account_button);
		menuItems[1] = menuayout.findViewById(R.id.history_button);
		menuItems[2] = menuayout.findViewById(R.id.settings_button);
		menuItems[3] = menuayout.findViewById(R.id.about_button);
		OnClickListener listener = new OnClickListener() {
			public void onClick(View v) {
				if (v == menuItems[0]) {
					System.out.println("0 pressed");
					startActivity(new Intent(getApplicationContext(), UserAccountManager.class));
				} else if (v == menuItems[1]) {
					System.out.println("1 pressed");
				} else if (v == menuItems[2]) {
					System.out.println("2 pressed");
				} else if (v == menuItems[3]) {
					System.out.println("3 pressed");
				}
				popupMenu.dismiss();
			}
		};
		for (View view : menuItems) {
			view.setOnClickListener(listener);
		}
	}

	@Override
	protected void onDestroy() {
		if (popupMenu != null) {
			popupMenu.dismiss();
		}
		super.onDestroy();
	}
	
	
}

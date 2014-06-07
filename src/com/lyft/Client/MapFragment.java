package com.lyft.Client;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.lyft.BasicClasses.Location;
import com.lyft.InteractionLayer.InteractionLayer;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

@SuppressLint("ValidFragment")
public abstract class MapFragment extends Fragment {

	private BMapManager mapManager = null;
	private MapView mapView = null;
	private MapController mapController = null;

	private int layout_res_id;
	private int mapview_id;

	private LocationClient locationClient = null;
	private LocationData locationData = null;
	private LyftLocationOverlay locationOverlay = null;
	private boolean isFirstRequest = true;

	MyItermizedOverlay overlayItems = null;

	MKSearch mkSearch = null;
	MKAddrInfo addressInfo = null;

	public MapFragment(int layoutId, int mapViewId) {
		this.layout_res_id = layoutId;
		this.mapview_id = mapViewId;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mapManager = new BMapManager(getActivity().getApplication());
		mapManager.init(null);

		locationData = new LocationData();
		locationClient = new LocationClient(getActivity().getApplicationContext());
		locationClient.registerLocationListener(new MyLocationListenner());
		LocationClientOption locationClientOption = new LocationClientOption();
		locationClientOption.setOpenGps(true);
		locationClientOption.setCoorType("bd09ll");
		locationClientOption.setScanSpan(10);
		locationClient.setLocOption(locationClientOption);
		locationClient.start();

		mkSearch = new MKSearch();
		mkSearch.init(mapManager, new MKSearchListener() {
			@Override
			public void onGetWalkingRouteResult(MKWalkingRouteResult arg0, int arg1) { }
			@Override
			public void onGetTransitRouteResult(MKTransitRouteResult arg0, int arg1) { }
			@Override
			public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) { }
			@Override
			public void onGetShareUrlResult(MKShareUrlResult arg0, int arg1, int arg2) { }
			@Override
			public void onGetPoiResult(MKPoiResult arg0, int arg1, int arg2) { }
			@Override
			public void onGetPoiDetailSearchResult(int arg0, int arg1) { }
			@Override
			public void onGetDrivingRouteResult(MKDrivingRouteResult arg0, int arg1) { }
			@Override
			public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) { }			
			@Override
			public void onGetAddrResult(MKAddrInfo addrInfo, int error) { 
				if (error != 0) {
					return;
				}
				addressInfo = addrInfo;
			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(layout_res_id, container, false);
		mapView = (MapView) view.findViewById(mapview_id);
		mapController = mapView.getController();
		mapController.setZoom((float) 17.0);
		mapController.enableClick(true);
		mapController.setOverlookingGesturesEnabled(false);

		locationClient.requestLocation();
		locationOverlay = new LyftLocationOverlay(mapView);

		//		locationOverlay.setData(locationData);
		locationOverlay.enableCompass();
		mapView.getOverlays().add(locationOverlay);
		mapView.refresh();
		//		ImageButton locateButton = (ImageButton) view.findViewById(locate_button_id);
		//		locateButton.setOnClickListener(new OnClickListener() {
		//			public void onClick(View v) {
		//				mapController.animateTo(new GeoPoint((int)(locationData.latitude* 1e6), 
		//						(int)(locationData.longitude *  1e6)));
		//			}
		//		});

		overlayItems = new MyItermizedOverlay(
				getResources().getDrawable(R.drawable.car_marker_black), mapView);
		mapView.getOverlays().add(overlayItems);
		mapView.refresh();

		return view;
	}

	protected void animateToCurrentLocation() {
		if (locationClient != null) {
			locationClient.requestLocation();
		}
		if (locationData != null) {
			int latitude = (int)(locationData.latitude * 1e6);
			int longitude = (int)(locationData.longitude * 1e6);
			if (mapController != null) {
				mapController.animateTo(new GeoPoint(latitude, longitude));
			}
		}
	}

	protected void addOverlayItem(int latitude, int longitude, int markerId, OnTapOverLayItemListener listener) {
		try {
			GeoPoint location = new GeoPoint(latitude, longitude);
			Drawable marker = getResources().getDrawable(markerId);
			MyOverlayItem item = new MyOverlayItem(location, "");
			item.setMarker(marker);
			item.setOnTapListener(listener);
			overlayItems.addItem(item);
			mapView.refresh();
		} catch (Exception e) {
		}
	}
	
	protected void clearOverlayItems() {
		try {
			overlayItems.removeAll();
			mapView.refresh();
		} catch (Exception e) {
		}
	}

	private void uploadLocationInfo(int latitude, int longitude) {
		Location location = new Location(latitude, longitude);
		InteractionLayer interaction = InteractionLayer.getInstance();
		interaction.uploadLocationData(InteractionLayer.myUID, location);
	}

	protected String getAddrDistrict() {
		if (addressInfo != null) {
			return addressInfo.addressComponents.district;
		} 
		return "";
	}

	protected String getAddrStreet() {
		if (addressInfo != null) {
			return addressInfo.addressComponents.street;
		} 
		return "";
	}

	protected String getAddrStreetNumber() {
		if (addressInfo != null) {
			return addressInfo.addressComponents.streetNumber;
		} 
		return "";
	}

	@Override
	public void onDestroy() {
		if (mapView != null) {
			mapView.destroy();
			mapView = null;
		}
		if (mapManager != null) {
			mapManager.destroy();
			mapManager = null;
		}
		super.onDestroy();
	}

	@Override
	public void onPause() {
		mapView.onPause();
		if (mapManager != null) {
			mapManager.stop();
		}
		super.onPause();
	}

	@Override
	public void onResume() {
		mapView.onResume();
		if (mapManager != null) {
			mapManager.start();
		}
		super.onResume();
	}

	public class MyLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			try {
				locationData.latitude = location.getLatitude();
				locationData.longitude = location.getLongitude();
				locationData.accuracy = location.getRadius();
				locationData.direction = location.getDerect();
				//System.out.println(location.getLatitude() + ", " + location.getLongitude());

				locationOverlay.setData(locationData);
				mapView.refresh();

				int latitude = (int)(location.getLatitude() * 1e6);
				int longitude = (int)(location.getLongitude() * 1e6);

				if (mkSearch != null) {
					mkSearch.reverseGeocode(new GeoPoint(latitude, longitude));
				}

				if (isFirstRequest) {
					mapController.animateTo(new GeoPoint(latitude, longitude));
					isFirstRequest = false;
					//locationOverlay.setLocationMode(LocationMode.FOLLOWING);
				}
				uploadLocationInfo(latitude, longitude);

			} catch (Exception e) {
			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null){
				return ;
			}
		}
	}

	public class LyftLocationOverlay extends MyLocationOverlay {
		public LyftLocationOverlay(MapView mapView) {
			super(mapView);
		}
		protected boolean dispatchTap() {
			return true;
		}
	}

	public class MyOverlayItem extends OverlayItem {
		OnTapOverLayItemListener listener = null;
		public MyOverlayItem(GeoPoint loaction, String title) {
			super(loaction, title, "");
		}
		public OnTapOverLayItemListener getOnTapListener() {
			return listener;
		}
		public void setOnTapListener(OnTapOverLayItemListener listener) {
			this.listener = listener;
		}
	}

	public class MyItermizedOverlay extends ItemizedOverlay<MyOverlayItem> {

		public MyItermizedOverlay(Drawable defaultMarker, MapView mapView) {
			super(defaultMarker, mapView);
		}

		@Override
		public boolean onTap(int index){
			MyOverlayItem item = (MyOverlayItem) getItem(index);
			if (item.getOnTapListener() != null) {
				item.getOnTapListener().onTap();
			}
			return true;
		}

		@Override
		public boolean onTap(GeoPoint pt , MapView mMapView){
			return false;
		}
	}
}

interface OnTapOverLayItemListener {
	public void onTap();
}

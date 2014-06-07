package com.lyft.InteractionLayer;

import java.util.ArrayList;

import com.lyft.BasicClasses.Location;
import com.lyft.BasicClasses.LocationData;

// Singleton: http://nkliuliu.iteye.com/blog/980851
public class InteractionLayer {
	private static InteractionLayer instance = null;
	public static int myUID = -1;
	
	private InteractionLayer() {
	}
	
	public static synchronized InteractionLayer getInstance() {
		if (instance == null) {
			instance = new InteractionLayer();
		}
		return instance;
	}
	
	public boolean uploadLocationData(int uid, Location location) {
		return true;
	}
	
	public ArrayList<LocationData> dowloadDriverLocationData(int uid, int radius) {
		double mLon1 = 113.971250;
		double mLat1 = 22.597300;
		double mLon2 = 113.974700;
		double mLat2 = 22.597000;
		double mLon3 = 113.976952;
		double mLat3 = 22.597800;
		double mLon4 = 113.975700;
		double mLat4 = 22.596300;
		
		ArrayList<LocationData> locationDataList = new ArrayList<LocationData>();
		locationDataList.add(new LocationData(1, new Location((int)(mLat1*1e6), (int)(mLon1*1e6))));
		locationDataList.add(new LocationData(2, new Location((int)(mLat2*1e6), (int)(mLon2*1e6))));
		locationDataList.add(new LocationData(3, new Location((int)(mLat3*1e6), (int)(mLon3*1e6))));
		locationDataList.add(new LocationData(4, new Location((int)(mLat4*1e6), (int)(mLon4*1e6))));
		return locationDataList;
	}
	
	public ArrayList<LocationData> dowloadPassengerLocationData(int uid, int radius) {
		double mLon1 = 113.971250;
		double mLat1 = 22.597300;
		double mLon2 = 113.974700;
		double mLat2 = 22.597000;
		double mLon3 = 113.976952;
		double mLat3 = 22.597800;
		double mLon4 = 113.975700;
		double mLat4 = 22.596300;
		
		ArrayList<LocationData> locationDataList = new ArrayList<LocationData>();
		locationDataList.add(new LocationData(1, new Location((int)(mLat1*1e6), (int)(mLon1*1e6))));
		locationDataList.add(new LocationData(2, new Location((int)(mLat2*1e6), (int)(mLon2*1e6))));
		locationDataList.add(new LocationData(3, new Location((int)(mLat3*1e6), (int)(mLon3*1e6))));
		locationDataList.add(new LocationData(4, new Location((int)(mLat4*1e6), (int)(mLon4*1e6))));
		return locationDataList;
	}
}

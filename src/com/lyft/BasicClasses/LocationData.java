package com.lyft.BasicClasses;

public class LocationData {
	Location location;
	String tag = "";	// e.g. sex=male
	int userId;
	
	public LocationData(int userId, Location location) {
		this.location = location;
		this.userId = userId;
	}
	
	public LocationData(int userId, Location location, String tag) {
		this.location = location;
		this.userId = userId;
		this.tag = tag;
	}
	
	public int getUserId() {
		return userId;
	}
	public Location getLocation() {
		return location;
	}

	public String getTag() {
		return tag;
	}
}

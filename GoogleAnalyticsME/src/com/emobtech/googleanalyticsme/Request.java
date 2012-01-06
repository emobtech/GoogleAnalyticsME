/*
 * Request.java
 * 21/12/2010
 * Google Analytics ME
 * Copyright(c) Ernandes Mourao Junior (ernandes@gmail.com)
 * All rights reserved
 */
package com.emobtech.googleanalyticsme;

/**
 * <p>
 * This class defines a Request type that can be sent to Google Analytics,
 * in order to represent a given action.
 * </p>
 * @author Ernandes Mourao Junior (ernandes@gmail.com)
 * @since 1.0
 * @see PageView
 * @see Event
 */
public abstract class Request {
	/**
	 * <p>
	 * Application Id.
	 * </p>
	 */
	private String appId;

	/**
	 * <p>
	 * User local Id.
	 * </p>
	 */
	private int userId;

	/**
	 * <p>
	 * Timestamp of first visit.
	 * </p>
	 */
	private long firstVisitTimestamp;

	/**
	 * <p>
	 * Timestamp of last visit.
	 * </p>
	 */
	private long lastVisitTimestamp;
	
	/**
	 * <p>
	 * Timestamp of current visit.
	 * </p>
	 */
	private long currentVisitTimestamp;
	
	/**
	 * <p>
	 * Screen height.
	 * </p>
	 */
	private int screenHeight = -1;

	/**
	 * <p>
	 * Screen width.
	 * </p>
	 */
	private int screenWidth = -1;
	
	/**
	 * <p>
	 * Number of colors.
	 * </p>
	 */
	private int numberOfColors = -1;

	/**
	 * <p>
	 * Gets color of depth.
	 * </p>
	 * @param numberOfColors Number of colors.
	 * @return Depth.
	 */
	static int getColorDepth(int numberOfColors) {
		switch (numberOfColors) {
			case 2:
				return 1;
			case 4:
				return 2;
			case 16:
				return 4;
			case 256:
				return 8;
			case 65536:
				return 16;
			case 16777216:
				return 24;
			default:
				return 32;
		}
	}

	/**
	 * <p>
	 * Gets the Application Id.
	 * </p>
	 * @return Id.
	 */
	String getAppId() {
		return appId;
	}

	/**
	 * <p>
	 * Sets the Application Id.
	 * </p>
	 * @param appId Id.
	 */
	void setAppId(String appId) {
		this.appId = appId;
	}

	/**
	 * <p>
	 * Gets the User Id.
	 * </p>
	 * @return Id.
	 */
	int getUserId() {
		return userId;
	}

	/**
	 * <p>
	 * Sets the User Id.
	 * </p>
	 * @param userId Id.
	 */
	void setUserId(int userId) {
		this.userId = userId;
	}

	/**
	 * <p>
	 * Gets the timestamp of first visit.
	 * </p>
	 * @return Timestamp.
	 */
	long getFirstVisitTimestamp() {
		return firstVisitTimestamp;
	}

	/**
	 * <p>
	 * Sets the timestamp of first visit.
	 * </p>
	 * @param firstVisitTimestamp Timestamp.
	 */
	void setFirstVisitTimestamp(long firstVisitTimestamp) {
		this.firstVisitTimestamp = firstVisitTimestamp;
	}

	/**
	 * <p>
	 * Gets the timestamp of last visit.
	 * </p>
	 * @return Timestamp.
	 */
	long getLastVisitTimestamp() {
		return lastVisitTimestamp;
	}

	/**
	 * <p>
	 * Sets the timestamp of last visit.
	 * </p>
	 * @param lastVisitTimestamp Timestamp.
	 */
	void setLastVisitTimestamp(long lastVisitTimestamp) {
		this.lastVisitTimestamp = lastVisitTimestamp;
	}

	/**
	 * <p>
	 * Gets the timestamp of current visit.
	 * </p>
	 * @return Timestamp.
	 */
	long getCurrentVisitTimestamp() {
		return currentVisitTimestamp;
	}

	/**
	 * <p>
	 * Sets the timestamp of current visit.
	 * </p>
	 * @param currentVisitTimestamp Timestamp.
	 */
	void setCurrentVisitTimestamp(long currentVisitTimestamp) {
		this.currentVisitTimestamp = currentVisitTimestamp;
	}

	/**
	 * <p>
	 * Gets the screen height.
	 * </p>
	 * @return Height.
	 */
	int getScreenHeight() {
		return screenHeight;
	}

	/**
	 * <p>
	 * Sets the screen height.
	 * </p>
	 * @param screenHeight Height.
	 */
	void setScreenHeight(int screenHeight) {
		this.screenHeight = screenHeight;
	}

	/**
	 * <p>
	 * Gets the screen width.
	 * </p>
	 * @return Width.
	 */
	int getScreenWidth() {
		return screenWidth;
	}

	/**
	 * <p>
	 * Sets the screen width.
	 * </p>
	 * @param screenWidth Width.
	 */
	void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
	}

	/**
	 * <p>
	 * Gets the number of colors.
	 * </p>
	 * @return Number.
	 */
	int getNumberOfColors() {
		return numberOfColors;
	}

	/**
	 * <p>
	 * Sets the number of colors.
	 * </p>
	 * @param numberOfColors Number.
	 */
	void setNumberOfColors(int numberOfColors) {
		this.numberOfColors = numberOfColors;
	}
	
	/**
	 * <p>
	 * Gets session cookie.
	 * </p>
	 * @return Cookie.
	 */
	String getCookie() {
		final String cookie =
			"__utma%3D1." + userId + "." + firstVisitTimestamp + "." + 
			lastVisitTimestamp + "." + currentVisitTimestamp + ".1;";
		//
		return cookie;
	}

	/**
	 * <p>
	 * Returns an URL to be sent to Google Analytics.
	 * </p>
	 * @param trackingCode Tracking code.
	 * @return URL.
	 */
	TrackingURL trackingURL() {
		TrackingURL turl = new TrackingURL();
		//
		turl.addParameter("utmac", appId);
		turl.addParameter("utmcc", getCookie());
		if (screenWidth != -1 && screenHeight != -1) {
			turl.addParameter("utmsr", screenWidth + "x" + screenHeight);	
		}
		if (numberOfColors != -1) {
			turl.addParameter("utmsc", getColorDepth(numberOfColors) + "-bit");	
		}
		//
		return turl;
	}
}

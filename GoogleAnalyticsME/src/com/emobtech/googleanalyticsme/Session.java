/*
 * Session.java
 * 04/07/2012
 * Google Analytics ME
 * Copyright(c) Ernandes Mourao Junior (ernandes@gmail.com)
 * All rights reserved
 */
package com.emobtech.googleanalyticsme;

import java.util.Random;

import com.emobtech.googleanalyticsme.util.PropertyStore;

/**
 * <p>
 * This class defines a visitor's session.
 * </p>
 * @author Ernandes Mourao Junior (ernandes@gmail.com)
 * @since 2.1
 */
final class Session {
	/**
	 * <p>
	 * Property store name.
	 * </p>
	 */
	private final String storeName;
	
	/**
	 * <p>
	 * Google Analytics Application Id.
	 * </p>
	 */
	private final String appId;
	
	/**
	 * <p>
	 * Domain hash.
	 * </p>
	 */
	private final int domainHash;
	
	/**
	 * <p>
	 * User Id.
	 * </p>
	 */
	private int userId;
	
	/**
	 * <p>
	 * First visit timestamp.
	 * </p>
	 */
	private long firstVisitTimestamp;
	
	/**
	 * <p>
	 * Last visit timestamp.
	 * </p>
	 */
	private long lastVisitTimestamp;
	
	/**
	 * <p>
	 * Current visit timestamp.
	 * </p>
	 */
	private long currentVisitTimestamp;
	
	/**
	 * <p>
	 * Visit number.
	 * </p>
	 */
	private int visitNumber;
	
	/**
	 * <p>
	 * Last request timestamp.
	 * </p>
	 */
	private long lastRequestTimestamp;
	
	/**
	 * <p>
	 * Returns current time in seconds.
	 * </p>
	 * @return Current time.
	 */
	private static long nowInSecs() {
		return System.currentTimeMillis() / 1000; //millis to secs.
	}
	
	/**
	 * <p>
	 * Creates an instance of Session class.
	 * </p>
	 * @param appId Application Id.
	 */
	Session(String appId) {
		this.appId = appId;
		this.domainHash = appId.hashCode();
		this.storeName = appId + "_v11";
		//
		load();
	}
	
	/**
	 * <p>
	 * Updates last request timestamp to current time.
	 * </p>
	 */
	void updateLastRequestTimestamp() {
		lastRequestTimestamp = nowInSecs();
		//
		save();
	}
	
	/**
	 * <p>
	 * Checks if the session is expired and then renew it.
	 * </p>
	 */
	void renewIfExpired() {
		if (isExpired()) {
			visitNumber++;
			lastVisitTimestamp = currentVisitTimestamp;
			currentVisitTimestamp = nowInSecs();
			lastRequestTimestamp = currentVisitTimestamp;
			//
			save();
		}
	}
	
	/**
	 * <p>
	 * Checks whether the session is expired.
	 * </p>
	 * @return Expired (true).
	 */
	boolean isExpired() {
		final long SESSION_EXPIRES_IN = 30*60; //30 mins.
		final long elapsed = nowInSecs() - lastRequestTimestamp;
		//
		return elapsed >= SESSION_EXPIRES_IN;
	}

	/**
	 * <p>
	 * Gets Application Id.
	 * </p>
	 * @return Id.
	 */
	String getAppId() {
		return appId;
	}

	/**
	 * <p>
	 * Gets the domain hash.
	 * </p>
	 * @return Hash.
	 */
	int getDomainHash() {
		return domainHash;
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
	 * Gets the timestamp of first visit.
	 * </p>
	 * @return Timestamp.
	 */
	long getFirstVisitTimestamp() {
		return firstVisitTimestamp;
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
	 * Gets the timestamp of current visit.
	 * </p>
	 * @return Timestamp.
	 */
	long getCurrentVisitTimestamp() {
		return currentVisitTimestamp;
	}

	/**
	 * <p>
	 * Gets the number of visits.
	 * </p>
	 * @return Number.
	 */
	int getVisitNumber() {
		return visitNumber;
	}

	/**
	 * <p>
	 * Loads session's data.
	 * </p>
	 */
	private void load() {
		PropertyStore prefs = new PropertyStore(storeName);
		//
		if (prefs.size() > 0) {
			userId = prefs.getInt("userId");
			firstVisitTimestamp = prefs.getLong("firstVisitTimestamp");
			lastVisitTimestamp = prefs.getLong("lastVisitTimestamp");
			currentVisitTimestamp = prefs.getLong("currentVisitTimestamp");
			visitNumber = prefs.getInt("visitNumber");
			lastRequestTimestamp = prefs.getLong("lastRequestTimestamp");
		} else {
			Random r = new Random(System.currentTimeMillis());
			//
			r.nextInt();
			r.nextInt(); //avoid duplicated number.
			r.nextInt();
			//
			userId = Math.abs(r.nextInt());
			currentVisitTimestamp = nowInSecs();
			firstVisitTimestamp = currentVisitTimestamp;
			lastVisitTimestamp = currentVisitTimestamp;
			lastRequestTimestamp = currentVisitTimestamp;
			visitNumber = 1;
			//
			save();
		}
	}

	/**
	 * <p>
	 * Saves session's data.
	 * </p>
	 */
	private void save() {
		PropertyStore prefs = new PropertyStore(storeName);
		//
		prefs.putInt("userId", userId);
		prefs.putLong("firstVisitTimestamp", firstVisitTimestamp);
		prefs.putLong("lastVisitTimestamp", lastVisitTimestamp);
		prefs.putLong("currentVisitTimestamp", currentVisitTimestamp);
		prefs.putInt("visitNumber", visitNumber);
		prefs.putLong("lastRequestTimestamp", lastRequestTimestamp);
		//
		prefs.save();
	}
}
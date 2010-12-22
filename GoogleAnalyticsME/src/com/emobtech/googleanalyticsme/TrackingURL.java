/*
 * TrackingURL.java
 * 21/12/2010
 * Google Analytics ME
 * Copyright(c) Ernandes Mourao Junior (ernandes@gmail.com)
 * All rights reserved
 */
package com.emobtech.googleanalyticsme;

import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;

import com.emobtech.googleanalyticsme.util.StringUtil;
import com.emobtech.googleanalyticsme.util.URLEncoder;

/**
 * <p>
 * This class represents a tracking URL, which is used for the communication
 * with Google Analytics, in order to track the application's events.
 * </p>
 * 
 * @author Ernandes Mourao Junior (ernandes@gmail.com)
 * @since 1.0
 */
public final class TrackingURL {
	/**
	 * <p>
	 * Google Analytics URL preffix.
	 * </p> 
	 */
	private String urlPreffix = "http://www.google-analytics.com/__utm.gif";
	
	/**
	 * <p>
	 * Parameters.
	 * </p>
	 */
	private Hashtable parameters;
	
	/**
	 * <p>
	 * Creates an instance of TrackingURL class. 
	 * </p>
	 * @param trackingCode Tracking code.
	 */
	public TrackingURL(String trackingCode) {
		if (StringUtil.isEmpty(trackingCode)) {
			throw new IllegalArgumentException(
				"Tracking code must not be empty.");
		}
		//
		parameters = new Hashtable(15);
		//
		parameters.put("utmwv", "1");
		parameters.put("utmn", new Random().nextInt() + "");
		parameters.put("utmcs", getProperty("microedition.encoding", "UTF-8"));
		parameters.put("utmul", getProperty("microedition.locale", "en-US"));
		parameters.put("utmje", "1");
		parameters.put("utmcr", "1");
		parameters.put("utmhn", "localhost");
		parameters.put("utmr", "http://kenai.com/projects/googleanalyticsme");
		parameters.put("utmac", trackingCode);
		parameters.put("utmcc", getCookie());
	}
	
	/**
	 * <p>
	 * Adds a parameter to the URL.
	 * </p>
	 * @param key Parameter key.
	 * @param value Parameter value.
	 * @throws NullPointerException If key or value is null;
	 */
	public void addParameter(String key, String value) {
		parameters.put(key, value);
	}
	
	/**
	 * <p>
	 * Removes a given parameter from the URL.
	 * </p>
	 * @param key Parameter key.
	 */
	public void removeParameter(String key) {
		parameters.remove(key);
	}
	
	/**
	 * <p>
	 * Returns the URL as a string.
	 * </p>
	 * @return String.
	 */
	public String toString() {
		return urlPreffix + '?' + queryString();
	}
	
	/**
	 * <p>
	 * Returns the value for the parameter "utmcc".
	 * </p>
	 * @return Cookie.
	 */
	private String getCookie() {
		Random random = new Random();
		//
		int cookie = random.nextInt();
		long now = new Date().getTime();
		//
		random.setSeed(2147483647);
		int randomValue = random.nextInt() - 1;
		//
		return
			"__utma%3D'"
			+ cookie
			+ '.'
			+ randomValue
			+ '.'
			+ now
			+ '.'
			+ now
			+ '.'
			+ now
			+ ".2%3B%2B__utmb%3D"
			+ cookie
			+ "%3B%2B__utmc%3D"
			+ cookie
			+ "%3B%2B__utmz%3D"
			+ cookie
			+ '.'
			+ now
			+ ".2.2.utmccn%3D"
			+ "(direct)%7Cutmcsr%3D"
			+ "(direct)%7Cutmcmd%3D"
			+ "(none)%3B%2B__utmv%3D"
			+ cookie;
	}

	/**
	 * <p>
	 * Returns all parameters as a query string.
	 * </p>
	 * @return Query string.
	 */
	private String queryString() {
		StringBuffer queryStr = new StringBuffer();
		Enumeration mdKeys = parameters.keys();
		//
		while (mdKeys.hasMoreElements()) {
			String key = mdKeys.nextElement().toString();
			//
			queryStr.append('&' + key + '=');
			queryStr.append(
				URLEncoder.encode(parameters.get(key).toString(), "UTF-8"));
		}
		//
		return queryStr.toString();
	}
	
	/**
	 * <p>
	 * Returns the value of a given property.
	 * </p>
	 * @param key Property key.
	 * @param defaultValue Default value in case property´s value is null.
	 * @return Value.
	 */
	private String getProperty(String key, String defaultValue) {
		return (key = System.getProperty(key)) != null ? key : defaultValue;
	}
}

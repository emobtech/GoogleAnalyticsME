/*
 * TrackingURL.java
 * 21/12/2010
 * Google Analytics ME
 * Copyright(c) Ernandes Mourao Junior (ernandes@gmail.com)
 * All rights reserved
 */
package com.emobtech.googleanalyticsme;

import java.util.Random;
import java.util.Vector;

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
	 * Random number generator.
	 * </p>
	 */
	private static final Random random = new Random(System.currentTimeMillis());
	
	/**
	 * <p>
	 * Cookie.
	 * </p>
	 */
	private static final int cookie = random.nextInt();

	/**
	 * <p>
	 * Google Analytics URL preffix.
	 * </p> 
	 */
	private static final String urlPreffix =
		"http://www.google-analytics.com/__utm.gif";
		
	/**
	 * <p>
	 * List of parameters' key.
	 * </p>
	 */
	private Vector keys = new Vector();
	
	/**
	 * <p>
	 * List of parameters's value.
	 * </p>
	 */
	private Vector values = new Vector();
	
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
		keys = new Vector(15);
		values = new Vector(15);
		//
		addParameter("utmwv", "1");
		addParameter("utmn", random.nextInt() + "");
		addParameter("utmcs", getProperty("microedition.encoding", "UTF-8"));
		addParameter("utmul", getProperty("microedition.locale", "en-us"));
		addParameter("utmdt", "");
		addParameter("utmhn", URLEncoder.encode("kenai.com"));
		addParameter("utmr", URLEncoder.encode("http://kenai.com"));
		addParameter("utmt", "");
		addParameter("utme", "");
		addParameter("utmp", "");
		addParameter("utmac", trackingCode);
		addParameter("utmcc", getCookie());
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
		if (keys.contains(key)) {
			values.setElementAt(value, keys.indexOf(key));
		} else {
			keys.addElement(key);
			values.addElement(value);
		}
	}
	
	/**
	 * <p>
	 * Removes a given parameter from the URL.
	 * </p>
	 * @param key Parameter key.
	 */
	public void removeParameter(String key) {
		if (keys.contains(key)) {
			int ix = keys.indexOf(key);
			//
			values.removeElementAt(ix);
			keys.removeElementAt(ix);
		}
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		return toString().equals(obj);
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return toString().hashCode();
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
	 * Returns all parameters as a query string.
	 * </p>
	 * @return Query string.
	 */
	private String queryString() {
		StringBuffer query = new StringBuffer();
		int size = keys.size();
		String value;
		//
		for (int i = 0; i < size; i++) {
			value = (String)values.elementAt(i);
			//
			if (!StringUtil.isEmpty(value)) {
				query.append(keys.elementAt(i));
				query.append("=");
				query.append(value);
				//
				if (i +1 < size) {
					query.append("&");
				}
			}
		}
		//
		return query.toString();
	}
	
	/**
	 * <p>
	 * Returns the value of a given property.
	 * </p>
	 * @param key Property key.
	 * @param defaultValue Default value in case property�s value is null.
	 * @return Value.
	 */
	private String getProperty(String key, String defaultValue) {
		return (key = System.getProperty(key)) != null ? key : defaultValue;
	}
	
	/**
	 * <p>
	 * Returns cookie.
	 * </p>
	 * @return Cookie.
	 */
	private String getCookie() {
		return
			"__utma%3D" +
			cookie + "." +
			cookie + "." +
			cookie + "." +
			cookie + "." +
			cookie + ".1%3B";
	}
}

/*
 * PageView.java
 * 21/12/2010
 * Google Analytics ME
 * Copyright(c) Ernandes Mourao Junior (ernandes@gmail.com)
 * All rights reserved
 */
package com.emobtech.googleanalyticsme;

import com.emobtech.googleanalyticsme.util.StringUtil;
import com.emobtech.googleanalyticsme.util.URLEncoder;

/**
 * <p>
 * This class represents a Page View event. This event is used to track each
 * time a page or a screen is displayed to the user.
 * </p>
 * @author Ernandes Mourao Junior (ernandes@gmail.com)
 * @since 1.0
 * @see Tracker
 */
public final class PageView extends Request {
	/**
	 * <p>
	 * Page's title.
	 * </p> 
	 */
	private String title;
	
	/**
	 * <p>
	 * Page's URI.
	 * </p> 
	 */
	private String uri;
	
	/**
	 * <p>
	 * Creates an instance of PageView class.
	 * </p>
	 * @param uri Page's URI.
	 */
	public PageView(String uri) {
		this(null, uri);
	}
	
	/**
	 * <p>
	 * Creates an instance of PageView class.
	 * </p>
	 * @param title Page's title.
	 * @param uri Page's URI.
	 * @throws IllegalArgumentException If URI is empty.
	 */
	public PageView(String title, String uri) {
		if (StringUtil.isEmpty(uri)) {
			throw new IllegalArgumentException("URI must not be empty.");
		}
		//
		this.title = title;
		this.uri = !uri.startsWith("/") ? "/" + uri : uri;
	}
	
	/**
	 * @see com.emobtech.googleanalyticsme.Request#trackingURL()
	 */
	TrackingURL trackingURL() {
		TrackingURL turl = super.trackingURL();
		//
		turl.addParameter("utmp", URLEncoder.encode(uri));
		if (!StringUtil.isEmpty(title)) {
			turl.addParameter("utmdt", URLEncoder.encode(title));
		}
		//
		return turl;
	}
}

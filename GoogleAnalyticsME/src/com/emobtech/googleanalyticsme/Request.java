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
 * This interface defines a Request type that can be sent to Google Analytics,
 * in order to represent a given action.
 * </p>
 * 
 * @author Ernandes Mourao Junior (ernandes@gmail.com)
 * @since 1.0
 * @see PageView
 * @see Event
 */
public interface Request {
	/**
	 * <p>
	 * Returns an URL to be sent to Google Analytics.
	 * </p>
	 * @param trackingCode Tracking code.
	 * @return URL.
	 */
	public String url(String trackingCode);
}

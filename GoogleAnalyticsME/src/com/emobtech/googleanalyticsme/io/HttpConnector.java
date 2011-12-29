/*
 * HttpConnector.java
 * 12/12/2011
 * Google Analytics ME
 * Copyright(c) Ernandes Mourao Junior (ernandes@gmail.com)
 * All rights reserved
 */
package com.emobtech.googleanalyticsme.io;

import java.io.IOException;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import com.emobtech.googleanalyticsme.util.StringUtil;

/**
 * <p>
 * This is factory class for creating new HttpConnection objects.
 * </p>
 * <p>
 * The parameter string that describes the target should conform to the Http URL
 * format as described in RFC 1738.
 * </p>
 * @author Ernandes Mourao Junior (ernandes@gmail.com)
 * @since 2.0
 */
public final class HttpConnector {
	/**
	 * <p>
	 * RIM connection parameters.
	 * </p> 
	 */
	private static Object rimConnParams;
	
	static {
		final String RIM_CLASS =
			"com.emobtech.googleanalyticsme.io.RIMConnectionString";
		//
		try {
			rimConnParams = Class.forName(RIM_CLASS).newInstance();
			rimConnParams.toString();
		} catch (Throwable e) {
			rimConnParams = null;
		}
	}
	
	/**
	 * <p>
	 * Create and open a HttpConnection.
	 * </p>
	 * @param url The URL for the connection.
	 * @return A new HttpConnection object.
	 * @throws IOException If an I/O error occurs.
	 * @throws IllegalArgumentException If url is null or empty.
	 */
	public static HttpConnection open(String url) throws IOException {
		if (StringUtil.isEmpty(url)) {
			throw new IllegalArgumentException("URL must not be empty.");
		}
		//
		if (rimConnParams != null) {
			url += rimConnParams.toString();
		}
		//
		return (HttpConnection)Connector.open(url);
	}
	
	/**
	 * <p>
	 * Private constructor to avoid object instantiation.
	 * </p>
	 */
	private HttpConnector() {
	}
}

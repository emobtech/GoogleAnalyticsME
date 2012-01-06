/*
 * StringUtil.java
 * 05/12/2010
 * Google Analytics ME
 * Copyright(c) Ernandes Mourao Junior (ernandes@gmail.com)
 * All rights reserved
 */
package com.emobtech.googleanalyticsme.util;

/**
 * <p>
 * This class implements a set of util string methods.
 * </p>
 * @author Ernandes Mourao Junior (ernandes@gmail.com)
 * @since 1.0
 */
public final class StringUtil {
    /**
	 * <p>
	 * Verifies whether a given string is empty (length = 0 or null).
	 * </p>
	 * @param str String.
	 * @return Empty (true).
	 */
	public static final boolean isEmpty(String str) {
		return str == null || str.trim().length() == 0;
	}
	
	/**
	 * <p>
	 * Extract device from platform.
	 * </p>
	 * @param platform Platform string.
	 * @return Device.
	 */
	public static final String extractDevice(String platform) {
		if (isEmpty(platform)) {
			throw new IllegalArgumentException("Platform must not be emoty.");
		}
		//
		int i = platform.indexOf('/');
		//
		if (i != -1) {
			String device = platform.substring(0, i);
			//
			i = platform.indexOf(';', i);
			//
			if (i != -1) {
				device = platform.substring(0, i);
			} else {
				device = platform;
			}
			//
			return device;
		} else if ((i = platform.indexOf(';')) != -1) {
			return platform.substring(0, i);
		} else {
			return platform;
		}
	}
    
	/**
	 * <p>
	 * Create an instance of StringUtil class.
	 * </p>
	 * <p>
	 * Private constructor to avoid object instantiation.
	 * </p>
	 */
	private StringUtil() {}
}

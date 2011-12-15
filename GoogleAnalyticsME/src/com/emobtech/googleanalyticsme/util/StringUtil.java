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
	 * Create an instance of StringUtil class.
	 * </p>
	 * <p>
	 * Private constructor to avoid object instantiation.
	 * </p>
	 */
	private StringUtil() {}
}

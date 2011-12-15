/*
 * URLEncoder.java
 * 05/12/2010
 * Google Analytics ME
 * Copyright(c) Ernandes Mourao Junior (ernandes@gmail.com)
 * All rights reserved
 */
package com.emobtech.googleanalyticsme.util;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

/**
 * <p>
 * This class is responsible for enconding strings.
 * </p>
 * @author Ernandes Mourao Junior (ernandes@gmail.com)
 * @since 1.0
 */
public final class URLEncoder {
    /**
	 * <p>
	 * Encode a given string, according to the encoding type defined by the
	 * system property microedition.encoding. If encoding type is not present,
	 * UTF-8 is used.
	 * </p>
	 * @param s String to encode.
	 * @return Encoded string.
	 * @throws IllegalArgumentException If string is empty or null.
	 */
	public static String encode(String s) {
		return encode(s, System.getProperty("microedition.encoding"));
	}
    /**
	 * <p>
	 * Encode a given string. If encoding type is not informed, UTF-8 is used.
	 * </p>
	 * @param s String to encode.
	 * @param enc Encode.
	 * @return Encoded string.
	 * @throws IllegalArgumentException If string is empty or null.
	 */
	public static String encode(String s, String enc) {
		if (s == null) {
			throw new IllegalArgumentException("String must not be null");
		}
		if (enc == null) {
			enc = "UTF-8";
		}
		//
		ByteArrayInputStream bIn;
		//
		try {
			bIn = new ByteArrayInputStream(s.getBytes(enc));
		} catch (UnsupportedEncodingException e) {
			bIn = new ByteArrayInputStream(s.getBytes());
		}
		//
		int c = bIn.read();
		StringBuffer ret = new StringBuffer();
		//
		while (c >= 0) {
			if ((c >= 'a' && c <= 'z')
					|| (c >= 'A' && c <= 'Z')
					|| (c >= '0' && c <= '9')
					|| c == '.'
					|| c == '-'
					|| c == '*'
					|| c == '_') {
				ret.append((char) c);
			} else if (c == ' ') {
				ret.append("%20");
			} else {
				if (c < 128) {
					ret.append(getHexChar(c));
				} else if (c < 224) {
					ret.append(getHexChar(c));
					ret.append(getHexChar(bIn.read()));
				} else if (c < 240) {
					ret.append(getHexChar(c));
					ret.append(getHexChar(bIn.read()));
					ret.append(getHexChar(bIn.read()));
				}
			}
			//
			c = bIn.read();
		}
		//
		return ret.toString();
	}
	
	/**
	 * <p>
	 * Get a hex value of a char.
	 * </p>
	 * @param c Char.
	 */
	private static String getHexChar(int c) {
		return (c < 16 ? "%0" : "%") + Integer.toHexString(c).toUpperCase();
	}
	
	/**
	 * <p>
	 * Create an instance of URLEncoder class.
	 * </p>
	 * <p>
	 * Private constructor to avoid object instantiation.
	 * </p>
	 */
	private URLEncoder() {}
}

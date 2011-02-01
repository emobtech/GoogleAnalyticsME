package com.emobtech.googleanalyticsme.util;

import junit.framework.TestCase;

public class URLEncoderTest extends TestCase {

	public void testEncodeString() {
		try {
			URLEncoder.encode(null, null);
			fail();
		} catch (IllegalArgumentException e) {
		} catch (Exception e) {
			fail();
		}
		//
		assertEquals("", URLEncoder.encode("", null));
		assertEquals("%21*%22%27%28%29%3B%40%2B%24%2C%25%23%5B%5D.-_", URLEncoder.encode("!*\"\'();@+$,%#[].-_", null));
		assertEquals("%22google%20analytics%20me%22", URLEncoder.encode("\"google analytics me\"", null));
		assertEquals("google%26param1%3Dgoogle%20api%26param2%3Dme", URLEncoder.encode("google&param1=google api&param2=me", null));
	}
}

package com.emobtech.googleanalyticsme.util;

import junit.framework.TestCase;

public class StringUtilTest extends TestCase {

	public void testIsEmpty() {
		assertTrue(StringUtil.isEmpty(null));
		assertTrue(StringUtil.isEmpty(""));
		assertTrue(StringUtil.isEmpty("     "));
		assertFalse(StringUtil.isEmpty("a"));
		assertFalse(StringUtil.isEmpty(" a "));
		assertFalse(StringUtil.isEmpty("     a"));
		assertFalse(StringUtil.isEmpty("a     "));
		assertFalse(StringUtil.isEmpty("abcdef"));
	}
	
	public void testExtractDevice() {
		assertEquals("Nokia", StringUtil.extractDevice("Nokia"));
		assertEquals("Nokia", StringUtil.extractDevice("Nokia;E65"));
		assertEquals("Nokia/E65", StringUtil.extractDevice("Nokia/E65"));
		assertEquals("Nokia/E65", StringUtil.extractDevice("Nokia/E65;sw1.2"));
		//
		try {
			StringUtil.extractDevice(null);
			fail();
		} catch (Exception e) {
		}
		try {
			StringUtil.extractDevice("");
			fail();
		} catch (Exception e) {
		}
		try {
			StringUtil.extractDevice("   ");
			fail();
		} catch (Exception e) {
		}
	}
}

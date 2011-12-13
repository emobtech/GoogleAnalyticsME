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
}

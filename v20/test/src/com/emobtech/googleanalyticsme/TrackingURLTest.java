package com.emobtech.googleanalyticsme;

import junit.framework.TestCase;

public class TrackingURLTest extends TestCase {

	public void testHashCode() {
		TrackingURL turl = new TrackingURL();
		//
		assertEquals(turl.toString().hashCode(), turl.hashCode());
	}
	
	public void testAddParameter() {
		TrackingURL turl = new TrackingURL();
		turl.addParameter("a", "a1");
		turl.addParameter("b", "b1");
		//
		assertTrue(turl.toString().endsWith("&a=a1&b=b1"));
		//
		turl.addParameter("a", "a2");
		turl.addParameter("b", "b2");
		//
		assertTrue(turl.toString().endsWith("&a=a2&b=b2"));
		//
		turl.addParameter(null, "c1");
		turl.addParameter("d", null);
		turl.addParameter("  ", "e1");
		turl.addParameter("f", "  ");
		//
		assertTrue(turl.toString().endsWith("&a=a2&b=b2"));
	}

	public void testRemoveParameter() {
		TrackingURL turl = new TrackingURL();
		//
		try {
			turl.removeParameter(null);
			turl.removeParameter("");
			turl.removeParameter("   ");
			turl.removeParameter("" + System.currentTimeMillis());
		} catch (Exception e) {
			fail();
		}
	}

	public void testEqualsObject() {
		TrackingURL turl1 = new TrackingURL();
		TrackingURL turl2 = new TrackingURL();
		//
		assertTrue(turl1.equals(turl1));
		assertFalse(turl1.equals(null));
		assertFalse(turl1.equals(this));
		//
		assertFalse(turl1.equals(turl2));
		//
		turl1.removeParameter("utmn");
		turl1.removeParameter("utmcc");
		turl2.removeParameter("utmn");
		turl2.removeParameter("utmcc");
		//
		assertTrue(turl1.equals(turl2));
	}

	public void testGetParameter() {
		TrackingURL turl = new TrackingURL();
		turl.addParameter("a", "a1");
		turl.addParameter("b", "b1");
		//
		assertEquals("a1", turl.getParameter("a"));
		assertEquals("b1", turl.getParameter("b"));
		assertNull(turl.getParameter("c"));
		//
		turl.removeParameter("a");
		turl.removeParameter("b");
		//
		assertNull(turl.getParameter("a"));
		assertNull(turl.getParameter("b"));
	}
}

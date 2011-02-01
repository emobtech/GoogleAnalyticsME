package com.emobtech.googleanalyticsme;

import com.emobtech.googleanalyticsme.util.StringUtil;
import com.emobtech.googleanalyticsme.util.URLEncoder;

import junit.framework.TestCase;

public class TrackingURLTest extends TestCase {

	public void testHashCode() {
		TrackingURL turl = new TrackingURL("UA-54321A");
		//
		assertEquals(turl.toString().hashCode(), turl.hashCode());
	}
	
	public void testToString() {
		TrackingURL turl = new TrackingURL("UA-54321A");
		//
		assertEquals("http://www.google-analytics.com/__utm.gif?utmwv=1&utmn=" + turl.getParameter("utmn") + "&utmcs=UTF-8&utmul=en-us&utmhn=kenai.com&utmr=http%3A%2F%2Fkenai.com&utmac=UA-54321A&utmcc=" + TrackingURL.getCookie(), turl.toString());
	}

	public void testTrackingURL() {
		try {
			new TrackingURL(null);
		} catch (IllegalArgumentException e) {
		} catch (Exception e) {
			fail();
		}
		//
		try {
			new TrackingURL("  ");
		} catch (IllegalArgumentException e) {
		} catch (Exception e) {
			fail();
		}
		//
		TrackingURL turl = new TrackingURL("UA-54321A");
		//
		assertEquals("1", turl.getParameter("utmwv"));
		assertFalse(StringUtil.isEmpty(turl.getParameter("utmn")));
		try {
			assertTrue(Integer.parseInt(turl.getParameter("utmn")) != 0);
		} catch (NumberFormatException e) {
			fail();
		}
		assertEquals(TrackingURL.getProperty("microedition.encoding", "UTF-8"), turl.getParameter("utmcs"));
		assertEquals(TrackingURL.getProperty("microedition.locale", "en-us"), turl.getParameter("utmul"));
		assertEquals("", turl.getParameter("utmdt"));
		assertEquals(URLEncoder.encode("kenai.com"), turl.getParameter("utmhn"));
		assertEquals(URLEncoder.encode("http://kenai.com"), turl.getParameter("utmr"));
		assertEquals("", turl.getParameter("utmt"));
		assertEquals("", turl.getParameter("utme"));
		assertEquals("", turl.getParameter("utmp"));
		assertEquals("UA-54321A", turl.getParameter("utmac"));
		assertEquals(TrackingURL.getCookie(), turl.getParameter("utmcc"));
	}

	public void testAddParameter() {
		TrackingURL turl = new TrackingURL("UA-54321A");
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
		TrackingURL turl = new TrackingURL("UA-54321A");
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
		TrackingURL turl1 = new TrackingURL("UA-54321A");
		TrackingURL turl2 = new TrackingURL("UA-54321A");
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
		TrackingURL turl = new TrackingURL("UA-54321A");
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
	
	public void testGetProperty() {
		assertFalse("home".equals(TrackingURL.getProperty("java.home", "home")));
		assertTrue("home".equals(TrackingURL.getProperty("java", "home")));
	}
}

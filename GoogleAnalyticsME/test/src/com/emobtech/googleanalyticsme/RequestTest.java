package com.emobtech.googleanalyticsme;

import junit.framework.TestCase;

public class RequestTest extends TestCase {

	public void testGetColorDepth() {
		assertEquals(1, Request.getColorDepth(2));
		assertEquals(2, Request.getColorDepth(4));
		assertEquals(4, Request.getColorDepth(16));
		assertEquals(8, Request.getColorDepth(256));
		assertEquals(16, Request.getColorDepth(65536));
		assertEquals(24, Request.getColorDepth(16777216));
		assertEquals(32, Request.getColorDepth(16777216 +1));
		assertEquals(32, Request.getColorDepth(16777216 +2));
	}

	public void testGetCookie() {
		PageView p = new PageView("/page");
		p.setAppId("UA-12345");
		p.setUserId(777);
		p.setFirstVisitTimestamp(111);
		p.setLastVisitTimestamp(222);
		p.setCurrentVisitTimestamp(333);
		//
		assertEquals("__utma%3D1.777.111.222.333.1;", p.getCookie());
	}

	public void testTrackingURL() {
		PageView p = new PageView("/page");
		p.setAppId("UA-12345");
		p.setUserId(777);
		p.setFirstVisitTimestamp(111);
		p.setLastVisitTimestamp(222);
		p.setCurrentVisitTimestamp(333);
		//
		assertEquals("UA-12345", p.getAppId());
		assertEquals(777, p.getUserId());
		assertEquals(111, p.getFirstVisitTimestamp());
		assertEquals(222, p.getLastVisitTimestamp());
		assertEquals(333, p.getCurrentVisitTimestamp());
		
		assertEquals(-1, p.getScreenWidth());
		assertEquals(-1, p.getScreenHeight());
		assertEquals(-1, p.getNumberOfColors());
		//
		TrackingURL turl = p.trackingURL();
		//
		assertEquals("UA-12345", turl.getParameter("utmac"));
		assertEquals(p.getCookie(), turl.getParameter("utmcc"));
		assertNull(turl.getParameter("utmsr"));
		assertNull(turl.getParameter("utmsc"));
		//
		p.setScreenWidth(240);
		//
		turl = p.trackingURL();
		//
		assertNull(turl.getParameter("utmsr"));
		//
		p.setScreenHeight(320);
		//
		turl = p.trackingURL();
		//
		assertEquals("240x320", turl.getParameter("utmsr"));
		assertNull(turl.getParameter("utmsc"));
		//
		p.setNumberOfColors(16);
		//
		turl = p.trackingURL();
		//
		assertEquals("4-bit", turl.getParameter("utmsc"));
	}
}

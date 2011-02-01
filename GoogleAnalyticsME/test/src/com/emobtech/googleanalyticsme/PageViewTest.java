package com.emobtech.googleanalyticsme;

import com.emobtech.googleanalyticsme.util.URLEncoder;

import junit.framework.TestCase;

public class PageViewTest extends TestCase {

	public void testPageViewString() {
		try {
			new PageView("Title", null);
			fail();
		} catch (IllegalArgumentException e) {
		} catch (Exception e) {
			fail();
		}
		//
		try {
			new PageView("Title", "  ");
			fail();
		} catch (IllegalArgumentException e) {
		} catch (Exception e) {
			fail();
		}
		//
		try {
			new PageView(null, "/uri");
		} catch (Exception e) {
			fail();
		}
		//
		try {
			new PageView("  ", "uri");
		} catch (Exception e) {
			fail();
		}
	}

	public void testUrl() {
		PageView pv = new PageView("Page 1", "/uri1");
		String url = pv.url("UA-54321A");
		//
		assertTrue(url.indexOf("&utmac=UA-54321A&") != -1);
		assertTrue(url.indexOf("&utmp=" + URLEncoder.encode("/uri1") + "&") != -1);
		assertTrue(url.indexOf("&utmdt=" + URLEncoder.encode("Page 1") + "&") != -1);
		//
		pv = new PageView("/uri2");
		url = pv.url("UA-54321A");
		//
		assertTrue(url.indexOf("&utmac=UA-54321A&") != -1);
		assertTrue(url.indexOf("&utmp=" + URLEncoder.encode("/uri2") + "&") != -1);
		assertTrue(url.indexOf("&utmdt") == -1);
	}
}

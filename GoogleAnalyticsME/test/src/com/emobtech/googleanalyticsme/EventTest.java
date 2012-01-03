package com.emobtech.googleanalyticsme;

import junit.framework.TestCase;

import com.emobtech.googleanalyticsme.util.URLEncoder;

public class EventTest extends TestCase {

	public void testEvent() {
		try {
			new Event(null, null, null, null);
			fail();
		} catch (IllegalArgumentException e) {
		} catch (Exception e) {
			fail();
		}
		//
		try {
			new Event(null, "action", null, null);
			fail();
		} catch (IllegalArgumentException e) {
		} catch (Exception e) {
			fail();
		}
		//
		try {
			new Event("category", null, null, null);
			fail();
		} catch (IllegalArgumentException e) {
		} catch (Exception e) {
			fail();
		}
		//
		try {
			new Event("  ", "action", null, null);
			fail();
		} catch (IllegalArgumentException e) {
		} catch (Exception e) {
			fail();
		}
		//
		try {
			new Event("category", "  ", null, null);
			fail();
		} catch (IllegalArgumentException e) {
		} catch (Exception e) {
			fail();
		}
		//
		try {
			new Event("category", "action", null, null);
		} catch (Exception e) {
			fail();
		}
	}

	public void testUrl() {
		Event e = new Event("Category 1", "Action 1", "Value 1", new Integer(7));
		String url = e.trackingURL().getURL();
		//
		assertTrue(url.indexOf("&utmt=event&") != -1);
		assertTrue(url.indexOf("&utme=" + URLEncoder.encode("5(Category 1*Action 1*Value 1)(7)") + "&") != -1);
		//
		e = new Event("Category 2", "Action 2", "Value 2", null);
		url = e.trackingURL().getURL();
		//
		assertTrue(url.indexOf("&utmt=event&") != -1);
		assertTrue(url.indexOf("&utme=" + URLEncoder.encode("5(Category 2*Action 2*Value 2)") + "&") != -1);
		//
		e = new Event("Category 3", "Action 3", null, null);
		url = e.trackingURL().getURL();
		//
		assertTrue(url.indexOf("&utmt=event&") != -1);
		assertTrue(url.indexOf("&utme=" + URLEncoder.encode("5(Category 3*Action 3)") + "&") != -1);
	}
}

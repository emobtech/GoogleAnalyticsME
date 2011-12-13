package com.emobtech.googleanalyticsme;

import junit.framework.TestCase;

public class TrackerTest extends TestCase {
	
	private static final String TRACKING_CODE = "UA-3518295-8";

	public void testGetInstanceString() {
		try {
			Tracker.getInstance(null);
			fail();
		} catch (IllegalArgumentException e) {
		} catch (Exception e) {
			fail();
		}
		//
		try {
			Tracker.getInstance("  ");
			fail();
		} catch (IllegalArgumentException e) {
		} catch (Exception e) {
			fail();
		}
		//
		assertSame(Tracker.getInstance("UA-12345"), Tracker.getInstance("UA-12345"));
		assertNotSame(Tracker.getInstance("UA-12345"), Tracker.getInstance("UA-54321"));
	}

	public void testTrack() {
		Tracker t = Tracker.getInstance(TRACKING_CODE);
		//
		try {
			t.track(null);
			fail();
		} catch (IllegalArgumentException e) {
		} catch (Exception e) {
			fail();
		}
		//
		try {
			t.track(new PageView("\\testTrack"));
		} catch (Exception e) {
			fail();
		}
	}

	public void testAddToQueue() {
		Tracker t = Tracker.getInstance(TRACKING_CODE);
		//
		try {
			t.addToQueue(null);
			fail();
		} catch (IllegalArgumentException e) {
		} catch (Exception e) {
			fail();
		}
		//
		try {
			t.addToQueue(new PageView("\\testAddToQueue"));
		} catch (Exception e) {
			fail();
		}
	}

	public void testQueueSize() {
		Tracker t = Tracker.getInstance(TRACKING_CODE);
		//
		int size = t.queueSize();
		//
		t.addToQueue(new PageView("\\testQueueSize"));
		t.addToQueue(new PageView("\\testQueueSize"));
		t.addToQueue(new PageView("\\testQueueSize"));
		//
		assertEquals(size +3, t.queueSize());
	}

	public void testFlush() {
		Tracker t = Tracker.getInstance(TRACKING_CODE);
		//
		t.addToQueue(new PageView("\\testFlush"));
		t.addToQueue(new PageView("\\testFlush"));
		t.addToQueue(new PageView("\\testFlush"));
		//
		try {
			t.flush(false);
		} catch (Exception e) {
			fail();
		}
		//
		assertEquals(0, t.queueSize());
		//
		t.addToQueue(new PageView("\\testFlush"));
		t.addToQueue(new PageView("\\testFlush"));
		t.addToQueue(new PageView("\\testFlush"));
		//
		try {
			t.flush(true);
		} catch (Exception e) {
			fail();
		}
		//
		int rm = 5;
		while (rm-- > 0 && t.queueSize() != 0) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
			}
		}
		//
		assertEquals(0, t.queueSize());
	}
}

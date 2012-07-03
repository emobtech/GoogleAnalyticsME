/*
 * Tracker.java
 * 21/12/2010
 * Google Analytics ME
 * Copyright(c) Ernandes Mourao Junior (ernandes@gmail.com)
 * All rights reserved
 */
package com.emobtech.googleanalyticsme;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;

import com.emobtech.googleanalyticsme.io.HttpConnector;
import com.emobtech.googleanalyticsme.util.PropertyStore;
import com.emobtech.googleanalyticsme.util.StringUtil;

/**
 * <p>
 * This class is responsible for tracking an application's events, in order to
 * send to Google Analytics. A Tracker object must be associated to given Google
 * Analytics Application Id, which represents a application's account.
 * </p>
 * @author Ernandes Mourao Junior (ernandes@gmail.com)
 * @since 1.0
 */
public final class Tracker {
	/**
	 * <p>
	 * Pool of Tracker objects. Only one object per Application Id.
	 * </p>
	 */
	private static Hashtable trackerPool;
	
	/**
	 * <p>
	 * Google Analytics Application Id.
	 * </p>
	 */
	private String appId;
	
	/**
	 * <p>
	 * Queue of events.
	 * </p>
	 */
	private Vector queue;
	
	/**
	 * <p>
	 * Timer used by the background sending process.
	 * </p>
	 */
	private Timer timer;
	
	/**
	 * <p>
	 * Task used by the timer.
	 * </p>
	 */
	private Task task;
	
	/**
	 * <p>
	 * Automatic events flushing interval.
	 * </p>
	 */
	private long flushInterval;
	
	/**
	 * <p>
	 * Domain hash.
	 * </p>
	 */
	private final int domainHash;
	
	/**
	 * <p>
	 * User local Id.
	 * </p>
	 */
	private int userId;
	
	/**
	 * <p>
	 * Timestamp of first visit.
	 * </p>
	 */
	private long firstVisitTimestamp;

	/**
	 * <p>
	 * Timestamp of last visit.
	 * </p>
	 */
	private long lastVisitTimestamp;
	
	/**
	 * <p>
	 * Timestamp of current visit.
	 * </p>
	 */
	private final long currentVisitTimestamp;
	
	/**
	 * <p>
	 * Number of visits.
	 * </p>
	 */
	private int visitNumber;
	
	/**
	 * <p>
	 * MIDlet.
	 * </p>
	 */
	private MIDlet midlet;
	
	/**
	 * <p>
	 * Creates a Tracker object associated to the given Application Id.
	 * </p>
	 * <p>
	 * A Tracker object will flush automatically all queued events. The 
	 * automatic flush is a fixed-delay execution process, which delay in this
	 * case is a default value defined by this class.
	 * </p>
	 * <p>
	 * In addition, explicit calls to {@link Tracker#flush(boolean)} will also
	 * flush the queued events, without affecting the automatic process.
	 * </p>
	 * @param midlet MIDlet instance.
	 * @param appId Application Id.
	 * @return Tracker object.
	 * @throws IllegalArgumentException If MIDlet or Application Id is null.
	 */
	public synchronized static Tracker getInstance(MIDlet midlet,
		String appId) {
		return getInstance(midlet, appId, 60);
	}
	
	/**
	 * <p>
	 * Creates a Tracker object associated to the given Application Id.
	 * </p>
	 * <p>
	 * A Tracker object will flush automatically all queued events. The 
	 * automatic flush is a fixed-delay execution process, which delay in this
	 * case is defined by the flushInterval parameter.
	 * </p>
	 * <p>
	 * In addition, explicit calls to {@link Tracker#flush(boolean)} will also
	 * flush the queued events, without affecting the automatic process.
	 * </p>
	 * <p>
	 * If you wish not to work with the automatic flush process, just pass a 
	 * value lower than or equal to zero to the flushInterval parameter. In this
	 * case no background process is started and then you will be responsible 
	 * for flushing all queued events, by calling 
	 * {@link Tracker#flush(boolean)}. Otherwise, you may just work with 
	 * {@link Tracker#track(Request)}, which send the events synchronously.
	 * </p>
	 * @param midlet MIDlet instance.
	 * @param appId Application Id.
	 * @param flushInterval Automatic flush interval (in seconds).
	 * @return Tracker object.
	 * @throws IllegalArgumentException If MIDlet or Application Id is null.
	 */
	public synchronized static Tracker getInstance(MIDlet midlet, String appId,
		long flushInterval) {
		if (midlet == null) {
			throw new IllegalArgumentException("MIDlet must not be null.");
		}
		if (StringUtil.isEmpty(appId)) {
			throw new IllegalArgumentException(
				"Application Id must not be empty.");
		}
		//
		if (trackerPool == null) {
			trackerPool = new Hashtable();
		}
		//
		Tracker tracker = (Tracker)trackerPool.get(appId);
		//
		if (tracker == null) {
			tracker = new Tracker(midlet, appId, flushInterval);
			trackerPool.put(appId, tracker);
		}
		//
		return tracker;
	}
	
	/**
	 * <p>
	 * Creates an instance of Tracker class.
	 * </p>
	 * @param midlet MIDlet instance.
	 * @param appId Application Id.
	 * @param flushInterval Automatic flush interval (in seconds).
	 */
	private Tracker(MIDlet midlet, String appId, long flushInterval) {
		flushInterval *= 1000; //secs to millis.
		//
		this.midlet = midlet;
		this.domainHash = appId.hashCode();
		this.appId = appId;
		this.flushInterval = flushInterval;
		this.currentVisitTimestamp = System.currentTimeMillis();
		//
		loadCookie();
		//
		queue = new Vector(5);
		timer = new Timer();
		task = new Task();
		//
		if (flushInterval > 0) {
			timer.schedule(task, flushInterval, flushInterval);
		}
	}
	
	/**
	 * <p>
	 * Tracks a given application's event. This methods immediately sends the
	 * request to Google Analytics.
	 * </p>
	 * <p>
	 * Since it is a synchronous request, this method performs a blocking
	 * operation. For asynchronous requests, use
	 * {@link Tracker#addToQueue(Request)} method.
	 * </p>
	 * @param request Event.
	 * @throws IllegalArgumentException If request is null.
	 * @see PageView
	 * @see Event
	 */
	public void track(Request request) {
		if (request == null) {
			throw new IllegalArgumentException("Request must not be null.");
		}
		//
		fillRequestParams(request);
		//
		try {
			process(request);
		} catch (IOException e) {
			addToQueue(request);
		}
	}
	
	/**
	 * <p>
	 * Tracks a given application's event. This methods does not immediately
	 * sends the request to Google Analytics. Actually, it is added to a queue,
	 * in order to be sent afterwards, by a background process.
	 * </p>
	 * <p>
	 * To force that all queued requests be sent right away, just call the
	 * {@link Tracker#flush(boolean)} method.
	 * </p>
	 * @param request Event.
	 * @throws IllegalArgumentException If request is null.
	 * @see PageView
	 * @see Event
	 */
	public void addToQueue(Request request) {
		if (request == null) {
			throw new IllegalArgumentException("Request must not be null.");
		}
		//
		fillRequestParams(request);
		//
		synchronized (queue) {
			queue.addElement(request);
		}
	}
	
	/**
	 * <p>
	 * Returns the events queue's size.
	 * </p>
	 * @return Size.
	 */
	public int queueSize() {
		return queue.size();
	}
	
	/**
	 * <p>
	 * Forces all queued events to be sent to Google Analytics.
	 * </p>
	 * <p><b>
	 * Make to sure to call this method (synchronously) before the app is
	 * destroyed. Otherwise, any queued requests will be lost.
	 * </b></p>
	 * @param asynchronously Send event asynchronously (non-blocking).
	 */
	public void flush(boolean asynchronously) {
		if (queue.size() > 0) {
			if (asynchronously) {
				runTask(1000);
			} else {
				processQueue();
			}
		}
	}
	
	/**
	 * <p>
	 * Processes all queued events.
	 * </p>
	 */
	private synchronized void processQueue() {
		int size = queue.size();
		int head = 0;
		//
		while (size-- > 0) {
			try {
				process((Request)queue.elementAt(head));
				//
				synchronized (queue) {
					queue.removeElementAt(head);
				}
			} catch (IOException e) {
				head++;
			}
		}
	}
	
	/**
	 * <p>
	 * Process a given request, in order to send the event to Google Analytics.
	 * </p>
	 * @param request Request.
	 * @throws IOException If any I/O error accours.
	 */
	private synchronized void process(Request request) throws IOException {
		TrackingURL turl = request.trackingURL();
		//
		HttpConnection conn = null;
		//
		try {
			conn = HttpConnector.open(turl.getURL());
			conn.setRequestProperty("User-Agent", getUserAgent());
			//
			if (conn.getResponseCode() != HttpConnection.HTTP_OK) {
				throw new IOException();
			}
		} catch (IOException e) {
			throw e;
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}
	
	/**
	 * <p>
	 * Runs the flush process after a given delay.
	 * </p>
	 * @param delay Delay (in millis).
	 */
	private void runTask(long delay) {
		if (!task.isRunning) {
			if (flushInterval > 0) {
				long delayNextRun =
					task.endExecutionTime +
					flushInterval -
					System.currentTimeMillis();
				//
				if (delayNextRun > delay) {
					task.cancel();
					task = new Task();
					//
					timer.schedule(task, delay, flushInterval);
				}
			} else {
				task = new Task();
				timer.schedule(task, delay);
			}
		}
	}
	
	/**
	 * <p>
	 * Fill the request's parameters.
	 * </p>
	 * @param request Request.
	 */
	private void fillRequestParams(Request request) {
		request.setAppId(appId);
		request.setUserId(userId);
		request.setDomainHash(domainHash);
		request.setFirstVisitTimestamp(firstVisitTimestamp);
		request.setLastVisitTimestamp(lastVisitTimestamp);
		request.setCurrentVisitTimestamp(currentVisitTimestamp);
		request.setVisitNumber(visitNumber);
		//
		Display display = Display.getDisplay(midlet);
		Displayable screen = display.getCurrent();
		//
		if (screen != null) {
			request.setScreenWidth(screen.getWidth());
			request.setScreenHeight(screen.getHeight());
			request.setNumberOfColors(display.numColors());
		}
	}
	
	/**
	 * <p>
	 * Loads cookie.
	 * </p>
	 */
	private void loadCookie() {
		PropertyStore prefs = new PropertyStore(appId);
		//
		if (prefs.size() > 0) {
			userId = prefs.getInt("userId");
			firstVisitTimestamp = prefs.getLong("firstVisitTimestamp");
			lastVisitTimestamp = prefs.getLong("lastVisitTimestamp");
			//
			visitNumber = prefs.getInt("visitNumber");
			if (visitNumber == Integer.MIN_VALUE) { //compatibility with 2.0!
				visitNumber = 0;
			}
			visitNumber++;
			//
			prefs.putLong("lastVisitTimestamp", currentVisitTimestamp);
			prefs.putInt("visitNumber", visitNumber);
		} else {
			Random r = new Random(System.currentTimeMillis());
			//
			r.nextInt();
			r.nextInt();
			r.nextInt();
			//
			userId = Math.abs(r.nextInt());
			firstVisitTimestamp = currentVisitTimestamp;
			lastVisitTimestamp = currentVisitTimestamp;
			visitNumber = 1;
			//
			prefs.putInt("userId", userId);
			prefs.putLong("firstVisitTimestamp", firstVisitTimestamp);
			prefs.putLong("lastVisitTimestamp", lastVisitTimestamp);
			prefs.putInt("visitNumber", visitNumber);
		}
		//
		prefs.save();
	}
	
	/**
	 * <p>
	 * Gets user agent.
	 * </p>
	 * @return Agent.
	 */
	private String getUserAgent() {
		final String customUA = midlet.getAppProperty("GAME-Custom-UserAgent");
		//
		if (customUA != null) {
			return customUA;
		}
		//
		String midletName = midlet.getAppProperty("MIDlet-Name");
		String midletVersion = midlet.getAppProperty("MIDlet-Version");
		String profile = midlet.getAppProperty("MicroEdition-Profile");
		String config = midlet.getAppProperty("MicroEdition-Configuration");
		String device = System.getProperty("microedition.platform");
		//
		device = device != null ? StringUtil.extractDevice(device) : "Unknown";
		//
		String userAgent =
			midletName + "/" + midletVersion + 
			" (JavaME; " + 
			device + "; " +
			"Profile/" + profile + " Configuration/" + config + ")";
		//
		return userAgent;
	}

	/**
	 * <p>
	 * Task that executes the flushing process.
	 * </p>
	 * @author Ernandes Mourao Junior (ernandes@gmail.com)
	 * @since 1.0
	 */
	private class Task extends TimerTask {
		/**
		 * <p>
		 * End of last execution time.
		 * </p>
		 */
		public long endExecutionTime = System.currentTimeMillis();
		
		/**
		 * <p>
		 * If the task is running.
		 * </p>
		 */
		public boolean isRunning;
		
		/**
		 * @see java.util.TimerTask#run()
		 */
		public void run() {
			isRunning = true;
			//
			processQueue();
			//
			isRunning = false;
			endExecutionTime = System.currentTimeMillis();
		}
	}
}

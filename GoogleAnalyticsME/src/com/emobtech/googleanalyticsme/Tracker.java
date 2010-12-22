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
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import com.emobtech.googleanalyticsme.util.StringUtil;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

/**
 * <p>
 * This class is responsible for tracking an application's events, in order to
 * send to Google Analytics. A Tracker object must be associated to given Google
 * Analytics tracking code, which represents a application's account.
 * </p>
 * 
 * @author Ernandes Mourao Junior (ernandes@gmail.com)
 * @since 1.0
 */
public final class Tracker {
	/**
	 * <p>
	 * Pool of Tracker objects. Only one object per tracking code.
	 * </p>
	 */
	private static Hashtable trackerPool;
	
	/**
	 * <p>
	 * Google Analytics tracking code.
	 * </p>
	 */
	private String trackingCode;
	
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
	 * Creates a Tracker object associated to the given tracking code.
	 * </p>
	 * <p>
	 * A Tracker object instantiated from this method does not flush events 
	 * automatically. Actually, all queued events must be flushed explicitly by
	 * calling {@link Tracker#flush(boolean)}.
	 * </p>
	 * @param trackingCode App's tracking code.
	 * @return Tracker object.
	 * @throws IllegalArgumentException If tracking code is empty.
	 */
	public synchronized static Tracker getInstance(String trackingCode) {
		return getInstance(trackingCode, 0);
	}
	
	/**
	 * <p>
	 * Creates a Tracker object associated to the given tracking code.
	 * </p>
	 * <p>
	 * A Tracker object instantiated from this method flushes automatically all
	 * queued events. The automatic flush is a fixed-delay execution process,
	 * which delay is according to the given flush interval parameter.
	 * </p>
	 * <p>
	 * In addition, explicit calls to {@link Tracker#flush(boolean)} will flush
	 * the events, without affecting the automatic process.
	 * </p>
	 * @param trackingCode App's tracking code.
	 * @param flushInterval Automatic flush interval (in seconds).
	 * @return Tracker object.
	 * @throws IllegalArgumentException If tracking code is empty.
	 */
	public synchronized static Tracker getInstance(String trackingCode,
		long flushInterval) {
		if (StringUtil.isEmpty(trackingCode)) {
			throw new IllegalArgumentException(
				"Tracking code must not be empty.");
		}
		//
		if (trackerPool == null) {
			trackerPool = new Hashtable();
		}
		//
		Tracker tracker = (Tracker)trackerPool.get(trackingCode);
		//
		if (tracker == null) {
			tracker = new Tracker(trackingCode, flushInterval);
			trackerPool.put(trackingCode, tracker);
		}
		//
		return tracker;
	}
	
	/**
	 * <p>
	 * Creates an instance of Tracker class.
	 * </p>
	 * @param trackingCode App's tracking code.
	 * @param flushInterval Automatic flush interval (in seconds).
	 */
	private Tracker(String trackingCode, long flushInterval) {
		flushInterval *= 1000; //secs to millis.
		//
		this.trackingCode = trackingCode;
		this.flushInterval = flushInterval;
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
	 * @see PageView
	 * @see Event
	 */
	public void track(Request request) {
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
	 * @see PageView
	 * @see Event
	 */
	public void addToQueue(Request request) {
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
	synchronized void processQueue() {
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
	 * Process a given requests, in order to send the event to Google Analytics.
	 * </p>
	 * @param request Request.
	 * @throws IOException If any I/O error accours.
	 */
	synchronized void process(Request request) throws IOException {
		String url = request.url(trackingCode);
		String userAgent = "Google Analytics ME/1.0 (compatible; Profile/MIDP-2.0 Configuration/CLDC-1.0)";
		HttpConnection conn = null;
		//
		try {
			conn = (HttpConnection)Connector.open(url);
			conn.setRequestProperty("User-Agent", userAgent);
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
	void runTask(long delay) {
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
				timer.schedule(task, delay);
			}
		}
	}

	/**
	 * <p>
	 * Task that executes the flushing process.
	 * </p>
	 * 
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

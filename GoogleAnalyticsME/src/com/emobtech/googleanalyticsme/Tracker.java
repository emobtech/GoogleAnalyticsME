package com.emobtech.googleanalyticsme;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import com.emobtech.googleanalyticsme.util.StringUtil;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

public final class Tracker {
	
	private static Hashtable trackerPool;
	
	private String trackingCode;
	
	private Vector queue;
	
	private Timer timer;
	
	private Task task;
	
	private long flushInterval;
	
	public synchronized static Tracker getInstance(String trackingCode) {
		return getInstance(trackingCode, 0);
	}
	
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
	
	public void track(Request request) {
		try {
			process(request);
		} catch (IOException e) {
			addToQueue(request);
		}
	}
	
	public void addToQueue(Request request) {
		synchronized (queue) {
			queue.addElement(request);
		}
	}
	
	public int queueSize() {
		return queue.size();
	}
	
	public void flush(boolean asynchronously) {
		if (queue.size() > 0) {
			if (asynchronously) {
				runTask(1000);
			} else {
				processQueue();
			}
		}
	}
	
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

	private class Task extends TimerTask {
		
		public long endExecutionTime = System.currentTimeMillis();
		
		public boolean isRunning;
		
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

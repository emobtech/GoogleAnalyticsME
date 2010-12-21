package com.emobtech.googleanalyticsme;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

public final class Tracker {
	
	private String trackingCode;
	
	private Vector queue;
	
	private Dispatcher dispatcher;
	
	public static Tracker getInstance(String trackingCode, long flushInterval) {
		return new Tracker(trackingCode, flushInterval);
	}
	
	private Tracker(String trackingCode, long flushInterval) {
		this.trackingCode = trackingCode;
		queue = new Vector(5);
		dispatcher = new Dispatcher(flushInterval);
		//
		dispatcher.start(flushInterval);
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
				dispatcher.start(1000);
			} else {
				processQueue();
			}
		}
	}
	
	synchronized void processQueue() {
		int size = queue.size();
		int head = 0;
		//
		for (int i = 0; i < size; i++) {
			try {
				process((Request)queue.elementAt(i));
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
	
	private class Dispatcher extends TimerTask {
		
		private Timer timer;
		
		private long period;
		
		private boolean isRunning;

		public Dispatcher(long period) {
			this.period = period * 1000;
			timer = new Timer();
			//
			if (period > 0) {
				timer.schedule(this, period, period);
			}
		}
		
		public void start(long delay) {
			if (!isRunning) {
				if (period > 0) {
					long delayNext =
						scheduledExecutionTime() - System.currentTimeMillis();
					//
					if (delayNext > delay) {
						cancel();
						//
						timer.schedule(this, delay * 1000, period);
					}
				} else {
					timer.schedule(this, delay * 1000);
				}
			}
		}
		
		public void run() {
			isRunning = true;
			processQueue();
			isRunning = false;
		}
	}
}

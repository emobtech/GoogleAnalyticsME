package com.emobtech.googleanalyticsme;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

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
			queue.add(request);			
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
					queue.remove(head);
				}
			} catch (IOException e) {
				head++;
			}
		}
	}
	
	synchronized void process(Request request) throws IOException {
		String url = request.url(trackingCode);
		//
		System.out.println("Processing: " + url);
	}
	
	private class Dispatcher extends TimerTask {
		
		private Timer timer;
		
		private long period;
		
		private boolean isRunning;

		public Dispatcher(long period) {
			this.period = period;
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
						timer.schedule(this, delay, period);
					}
				} else {
					timer.schedule(this, delay);
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

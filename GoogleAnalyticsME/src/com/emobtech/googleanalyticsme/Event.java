package com.emobtech.googleanalyticsme;

import com.emobtech.googleanalyticsme.util.StringUtil;

public final class Event implements Request {
	
	private String event;
	
	public Event(String category, String action, String label, String value) {
		if (StringUtil.isEmpty(category)) {
			throw new IllegalArgumentException("Category must not be empty.");
		}
		if (StringUtil.isEmpty(action)) {
			throw new IllegalArgumentException("Action must not be empty.");
		}
		//
		StringBuffer event = new StringBuffer();
		//
		event.append("5(");
		event.append(category);
		event.append('*');
		event.append(action);
		if (!StringUtil.isEmpty(label)) {
			event.append('*');
			event.append(action);
		}
		event.append(')');
		if (!StringUtil.isEmpty(value)) {
			event.append('(');
			event.append(action);
			event.append(')');
		}
		//
		this.event = event.toString();
	}

	public String url(String trackingCode) {
		TrackingURL params = new TrackingURL(trackingCode);
		params.addParameter("utmt", "event");
		params.addParameter("utme", event);
		//
		return params.toString();
	}
}

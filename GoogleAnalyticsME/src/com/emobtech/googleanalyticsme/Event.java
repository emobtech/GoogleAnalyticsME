/*
 * Event.java
 * 21/12/2010
 * Google Analytics ME
 * Copyright(c) Ernandes Mourao Junior (ernandes@gmail.com)
 * All rights reserved
 */
package com.emobtech.googleanalyticsme;

import com.emobtech.googleanalyticsme.util.StringUtil;
import com.emobtech.googleanalyticsme.util.URLEncoder;

/**
 * <p>
 * This class represents an Event that can be used to record user interaction
 * with application's elements. For instance, you can track how many times the
 * users hit the Play or Stop button.
 * </p>
 * <p>
 * For further information on Event tracking, access <a href=
 * "http://code.google.com/apis/analytics/docs/tracking/eventTrackerOverview.html"
 * target="_blank"> Event Tracking Overview </a>.
 * </p>
 * 
 * @author Ernandes Mourao Junior (ernandes@gmail.com)
 * @since 1.0
 * @see Tracker
 */
public final class Event extends Request {
	/**
	 * <p>
	 * Event request value.
	 * </p>
	 */
	private String event;

	/**
	 * <p>
	 * Creates an instance of Event class.
	 * </p>
	 * <p>
	 * For further information on event's values, access <a href=
	 * "http://code.google.com/apis/analytics/docs/tracking/eventTrackerGuide.html"
	 * target="_blank"> Event Tracking Guide </a>.
	 * 
	 * @param category
	 *            The name you supply for the group of objects you want to
	 *            track.
	 * @param action
	 *            A string that is uniquely paired with each category, and
	 *            commonly used to define the type of user interaction for the
	 *            web object.
	 * @param label
	 *            An optional string to provide additional dimensions to the
	 *            event data.
	 * @param value
	 *            An integer that you can use to provide numerical data about
	 *            the user event.
	 * @throws If
	 *             category or action is empty.
	 */
	public Event(String category, String action, String label, Integer value) {
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
			event.append(label);
		}
		event.append(')');
		if (value != null) {
			event.append('(');
			event.append(value.intValue());
			event.append(')');
		}
		//
		this.event = event.toString();
	}

	/**
	 * @see com.emobtech.googleanalyticsme.Request#trackingURL()
	 */
	TrackingURL trackingURL() {
		TrackingURL turl = super.trackingURL();
		//
		turl.addParameter("utmt", "event");
		turl.addParameter("utme", URLEncoder.encode(event));
		//
		return turl;
	}
}

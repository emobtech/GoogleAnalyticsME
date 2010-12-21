package com.emobtech.googleanalyticsme;

import com.emobtech.googleanalyticsme.util.StringUtil;

public final class PageView implements Request {
	
	private String title;
	private String uri;
	
	public PageView(String uri) {
		this(null, uri);
	}
	
	public PageView(String title, String uri) {
		if (StringUtil.isEmpty(uri)) {
			throw new IllegalArgumentException("URI must not be empty.");
		}
		//
		this.title = title;
		this.uri = uri;
	}

	public String url(String trackingCode) {
		TrackingURL params = new TrackingURL(trackingCode);
		params.addParameter("utmt", "page");
		params.addParameter("utmp", uri);
		if (!StringUtil.isEmpty(title)) {
			params.addParameter("utmdt", title);
		}
		//
		return params.toString();
	}
}

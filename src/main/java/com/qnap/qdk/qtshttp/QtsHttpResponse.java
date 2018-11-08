package com.qnap.qdk.qtshttp;

public class QtsHttpResponse {
	private int mResponseCode;
	private String mContent;
	
	/**
	 * Constructor
	 * @param responseCode The status code of the response obtained from the HTTP request.
	 * @param responseMessage The HTTP response content.
	 */
	public QtsHttpResponse(int responseCode, String content) {
		this.mResponseCode = responseCode;
		this.mContent = content;
	}
	
	/**
	 * Get the status code of the response obtained from the HTTP request.
	 * @return Status code.
	 */
	public int getResponseCode() {
		return this.mResponseCode;
	}
	
	/**
	 * Get the HTTP response content.
	 * @return Content.
	 */
	public String getContent() {
		return this.mContent;
	}
}

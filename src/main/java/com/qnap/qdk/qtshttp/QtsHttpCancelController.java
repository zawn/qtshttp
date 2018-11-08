package com.qnap.qdk.qtshttp;

/**
 * Class of cancal controller 
 */
public class QtsHttpCancelController {
	private boolean mCancel;
	
	/**
	 * Constructor
	 */
	public QtsHttpCancelController() {
		mCancel = false;
	}
	
	/**
	 * Set cancel
	 */
	public void setCancel() {
		mCancel = true;
	}
	
	/**
	 * Check is cancel
	 * @return Cancel or not
	 */
	public boolean isCancel() {
		return this.mCancel;
	}
}

package com.qnap.qdk.qtshttp;

public class QtsHttpSession {
	private String mHostName;            /** Host name */
	private String mComputerName;        /** Computer name */
	private String mUserName;            /** User name */
	private String mPassword;            /** Password */
	private boolean mSecureConnection;   /** SSL connect */
	private long mPortNum;               /** Connecting Port */
	private long mSSLPortNum;            /** Connecting SSL Port */
	private String mAgentName;           /** Agent name */
	private int mTimeOutMilliseconds;    /** Network timeout (milliseconds) */
	private String mSID;                 /** SID of NAS */
	
	public QtsHttpSession() {
		
	}

	public String getHostName() {
		return mHostName;
	}

	public void setHostName(String hostName) {
		this.mHostName = hostName;
	}

	public String getComputerName() {
		return mComputerName;
	}

	public void setComputerName(String computerName) {
		this.mComputerName = computerName;
	}

	public String getUserName() {
		return mUserName;
	}

	public void setUserName(String userName) {
		this.mUserName = userName;
	}

	public String getPassword() {
		return mPassword;
	}

	public void setPassword(String password) {
		this.mPassword = password;
	}

	public boolean isSecureConnection() {
		return mSecureConnection;
	}

	public void setSecureConnection(boolean secureConnection) {
		this.mSecureConnection = secureConnection;
	}

	public long getPortNum() {
		return mPortNum;
	}

	public void setPortNum(long portNum) {
		this.mPortNum = portNum;
	}

	public long getSSLPortNum() {
		return mSSLPortNum;
	}

	public void setSSLPortNum(long sslPortNum) {
		this.mSSLPortNum = sslPortNum;
	}

	public String getAgentName() {
		return mAgentName;
	}

	public void setAgentName(String agentName) {
		this.mAgentName = agentName;
	}

	public int getTimeOutMilliseconds() {
		return mTimeOutMilliseconds;
	}

	public void setTimeOutMilliseconds(int timeOutMilliseconds) {
		this.mTimeOutMilliseconds = timeOutMilliseconds;
	}

	public String getSID() {
		return mSID;
	}

	public void setSID(String sid) {
		this.mSID = sid;
	}
	
	
}

package com.qnap.qdk.qtshttp;

/**
 * @category QNAP Turbo NAS Server information class.
 * @author Edison
 *
 */
public class QtsHttpServerInfo {
	private String mHostName;            /** Host name */
	private String mUserName;            /** User name */
	private String mPassword;            /** Password */
	private boolean mSecureConnection;   /** SSL connection */
	private String mComputerName;        /** Computer name */
		
	/**
	 * Constructor with parameters.
	 * @param hostName Host name.
	 * @param userName User name.
	 * @param password Password.
	 * @param secureConnection SSL connect.
	 * @param computerName Computer name.
	 */
	public QtsHttpServerInfo(String hostName, String userName, String password, boolean secureConnection, String computerName) {
		this.mHostName = hostName;
		this.mUserName = userName;
		this.mPassword = password;
		this.mSecureConnection = secureConnection;
		this.mComputerName = computerName;
	}

	/**
	 * Get host name.
	 * @return Host name.
	 */
	public String getHostName() {
		return this.mHostName;
	}

	/**
	 * Get user name.
	 * @return User name.
	 */
	public String getUserName() {
		return this.mUserName;
	}

	/**
	 * Get password.
	 * @return Password
	 */
	public String getPassword() {
		return this.mPassword;
	}

	/**
	 * Is SSL connect.
	 * @return true SSL connect.
	 * @return false non-SSL connect.
	 */
	public boolean isSecureMode() {
		return this.mSecureConnection;
	}

	/**
	 * Get computer name.
	 * @return Computer name
	 */
	public String getComputerName() {
		return this.mComputerName;
	}

}

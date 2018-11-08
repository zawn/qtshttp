package com.qnap.qdk.qtshttp;

import com.qnap.qdk.qtshttp.exception.QtsHttpException;
import com.qnap.qdk.qtshttp.exception.QtsHttpNotAuthorizedException;
import com.qnap.qdk.qtshttp.exception.QtsHttpParameterInvalidException;
import com.qnap.qdk.qtshttp.filestation.IQtsHttpFileStation;
import com.qnap.qdk.qtshttp.filestation.QtsHttpFileStation;
import com.qnap.qdk.qtshttp.filestation.QtsHttpFileStationApiVersion;

/**
 * @category QTS HTTP Server class.
 * @author Edison
 *
 * 1. Support to login QNAP Turbo NAS file station.
 * 2. Open and close QNAP Turbo NAS file station.
 */
public class QtsHttpServer {
	private QtsHttpServerInfo mServerInfo;
	private long mFSPortNum;
	private long mFSSSLPortNum;
	private String mAgentName;
	private int mTimeOutMilliseconds;
	private QtsHttpFileStationApiVersion mFSVersion;
	private QtsHttpFileStation mFileStation;
	
	/**
	 * Constructor with QtsHttpServerInfo server info.
	 * @param serverInfo QTS HTTP server info.
	 */
	public QtsHttpServer(QtsHttpServerInfo serverInfo) {
		this.mServerInfo = serverInfo;
		this.mFSPortNum = 8080;
		this.mFSSSLPortNum = 443;
		this.mAgentName = "QTS_HTTP";
		this.mTimeOutMilliseconds = 30 * 1000;
	}
	
	/**
	 * Developers can change HTTP agent name before call QTS NAS file station API.
	 * @param strAgentName New HTTP agent name.
	 * @throws Exception
	 */
	public void setAgentName(String agentName) throws Exception {
		if (agentName == null) {
			QtsHttpException e = new QtsHttpParameterInvalidException();
			throw e;
		}		
		this.mAgentName = agentName;
	}

	/**
	 * Developer can change HTTP time-out value before call QTS NAS file station API.
	 * @param nTimeOutMilliseconds HTTP time-out value, in milliseconds.
	 * @throws Exception
	 * QTS HTTP API will use default time-out value (30000);
	 */
	public void setTimeout(int timeoutMilliseconds) throws Exception {
		if (timeoutMilliseconds < 0) {
			QtsHttpException e = new QtsHttpParameterInvalidException();
			throw e;
		}
		this.mTimeOutMilliseconds = timeoutMilliseconds;
	}
	
	/**
	 * Login NAS file station of QNAP Turbo NAS Server.
	 * @param loginStation Login station type.
	 * @throws Exception
	 */
	public void login(QtsHttpStationType loginStation) throws Exception {		
		switch(loginStation) {
		case QTS_HTTP_STATION_TYPE_FILE_STATION:
			mFileStation = new QtsHttpFileStation(
					this.mServerInfo, this.mFSPortNum, this.mFSSSLPortNum, this.mAgentName, this.mTimeOutMilliseconds);
			try {
				mFileStation.login();
				this.mFSVersion = QtsHttpFileStationApiVersion.QTS_HTTP_FILE_STATION_API_V1;
			}
			catch (Exception e) {
				throw e;
			}
			break;
		}
	}
	
	/**
	 * Is login File Station.
	 * @return true Login in File Station
	 * @result false Not login in File Station
	 */
	public boolean isLoginFS() {
		boolean result = false;
		//if (mSession.getSID() != "") {
			//result = true;
		//}
		return result;
	}
	
	/**
	 * Set file station connection port number.
	 * @param portNumber File station port number.
	 * @throws Exception
	 * If no set port number, QTS file station API will use QNAP default port number to connect.
	 * Non-SSL: 8080
	 */
	public void setFileStationPortNum(long portNumber) throws Exception {
		if ((portNumber < 0) || (portNumber > 65535)) {
			QtsHttpException e = new QtsHttpParameterInvalidException();
			throw e;
		}
		this.mFSPortNum = portNumber;
	}
	
	/**
	 * Get file station connection port number.
	 * @return File station port number.
	 * @throws Exception
	 */
	public long getFileStationPortNum() throws Exception {
		return this.mFSPortNum;
	}
	
	/**
	 * Set file station SSL connection port number.
	 * @param lSSLPortNumber File station SSL port number.
	 * @throws Exception
	 * If no set SSL port number, QTS file station API will use QNAP default SSL port number to connect.
	 * SSL: 443
	 */
	public void setFileStationSSLPortNum(long sslPortNumber) throws Exception {
		if ((sslPortNumber < 0) || (sslPortNumber > 65535)) {
			QtsHttpException e = new QtsHttpParameterInvalidException();
			throw e;
		}
		this.mFSSSLPortNum = sslPortNumber;
	}
	
	/**
	 * Open file station and get file station object.
	 * @return IQtsHttpFileStation
	 * @throws Exception
	 */
	public IQtsHttpFileStation openFileStation() throws Exception {
		if (!this.mFileStation.isLogin()) {
			QtsHttpException e = new QtsHttpNotAuthorizedException();
			throw e;
		}
		else {
			if (this.mFSVersion == null) {
				QtsHttpException e = new QtsHttpNotAuthorizedException();
				throw e;
			}
			switch(this.mFSVersion) {
			case QTS_HTTP_FILE_STATION_API_V1:
				break;
			default:
				QtsHttpException e = new QtsHttpNotAuthorizedException();
				throw e;
			}
		}
		
		return (IQtsHttpFileStation)mFileStation;		
	}
	
	/**
	 * Close file station.
	 * @param pFileStation File station object
	 * @throws Exception
	 */
	public void closeFileStation(IQtsHttpFileStation pFileStation) throws Exception {
		((QtsHttpFileStation)pFileStation).logout();
		mFileStation = null;
	}
	
}

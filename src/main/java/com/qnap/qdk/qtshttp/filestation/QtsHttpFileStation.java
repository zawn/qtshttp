package com.qnap.qdk.qtshttp.filestation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.qnap.qdk.parser.JsonParser;
import com.qnap.qdk.qtshttp.IQtsHttpTransferedProgressListener;
import com.qnap.qdk.qtshttp.QtsHttpCancelController;
import com.qnap.qdk.qtshttp.QtsHttpConnection;
import com.qnap.qdk.qtshttp.QtsHttpResponse;
import com.qnap.qdk.qtshttp.QtsHttpServerInfo;
import com.qnap.qdk.qtshttp.QtsHttpSession;
import com.qnap.qdk.qtshttp.exception.QtsHttpAuthorizationFailedException;
import com.qnap.qdk.qtshttp.exception.QtsHttpException;
import com.qnap.qdk.qtshttp.exception.QtsHttpNetworkTimeoutException;
import com.qnap.qdk.qtshttp.exception.QtsHttpParameterInvalidException;
import com.qnap.qdk.qtshttp.exception.QtsHttpServerNotExistException;
import com.qnap.qdk.util.StringTranslator;

public class QtsHttpFileStation implements IQtsHttpFileStation {
	private QtsHttpSession mSession;

	public QtsHttpFileStation(QtsHttpServerInfo serverInfo, long portNum, long sslPortNum, String agentName, int timeOutMilliseconds) {
		this.mSession = new QtsHttpSession();
		this.mSession.setHostName(serverInfo.getHostName());
		this.mSession.setComputerName(serverInfo.getComputerName());
		this.mSession.setUserName(serverInfo.getUserName());
		this.mSession.setPassword(serverInfo.getPassword());
		this.mSession.setSecureConnection(serverInfo.isSecureMode());
		this.mSession.setPortNum(portNum);
		this.mSession.setSSLPortNum(sslPortNum);
		this.mSession.setAgentName(agentName);
		this.mSession.setTimeOutMilliseconds(timeOutMilliseconds);
	}
	
	public boolean isLogin() {
		if (this.mSession.getSID().isEmpty()) {
			return false;
		}
		else {
			return true;
		}		
	}
	
	public void login() throws Exception {
		String encodedUserName = StringTranslator.replaceBlank(URLEncoder.encode(this.mSession.getUserName(), "UTF-8"));
		String cgi = "cgi-bin/filemanager/wfm2Login.cgi";
		String data = "user=" + encodedUserName + "&pwd=" + StringTranslator.ezEncode(this.mSession.getPassword());
		String jsonString;
		String tagStringValue;
		JsonParser jsonParser;
		QtsHttpResponse httpResult;
		QtsHttpException qtse;
		int status = 0;

		try {
			QtsHttpConnection conn = new QtsHttpConnection();
			httpResult = conn.doPost(this.mSession, cgi, data, null);
			switch(httpResult.getResponseCode()) {
			case HttpURLConnection.HTTP_OK:
				break;
			case HttpURLConnection.HTTP_NOT_FOUND:
				qtse = new QtsHttpServerNotExistException();
				throw qtse;
			case HttpURLConnection.HTTP_CLIENT_TIMEOUT:
				qtse = new QtsHttpNetworkTimeoutException();
				throw qtse;
			}
			jsonString = httpResult.getContent();
			jsonParser = new JsonParser(jsonString);
			tagStringValue = jsonParser.getTagValue("status");
			status = Integer.parseInt(tagStringValue);
			switch(status) {
			case QtsHttpFileStationStatus.STATUS_SUCCESS:
				break;
			case QtsHttpFileStationStatus.STATUS_FAILURE:
				qtse = new QtsHttpAuthorizationFailedException();
				throw qtse;
			}
			this.mSession.setSID(jsonParser.getTagValue("sid"));
		}
		catch (Exception e) {
			
		}

	}
	
	
	public void logout() {
		String cgi = "cgi-bin/filemanager/wfm2Logout.cgi";
		String data = "sid=" + this.mSession.getSID() + "&logout=1";
		String jsonString;
		QtsHttpResponse httpResult;
		try {
			QtsHttpConnection conn = new QtsHttpConnection();
			httpResult = conn.doGet(this.mSession, cgi, data, null);
			jsonString = httpResult.getContent();
		} catch (Exception e) {
			
		}
	}
	
	@Override
	public ArrayList<QtsHttpFileEntry> getShareFolderList(QtsHttpCancelController cancel)
			throws Exception {
		String cgi = "cgi-bin/filemanager/utilRequest.cgi";
		String data = "func=get_tree&sid=" + this.mSession.getSID() + "&is_iso=0&node=share_root";
		String jsonString;
		String tagStringValue;
		JsonParser jsonParser;
		JSONArray jsonArray;
		JSONObject jsonObj;
		ArrayList<QtsHttpFileEntry> fileList = new ArrayList<QtsHttpFileEntry>();
		QtsHttpResponse httpResult;
		QtsHttpException qtse;
		int status = 0;
		
		try {
			QtsHttpConnection conn = new QtsHttpConnection();
			httpResult = conn.doPost(mSession, cgi, data, cancel);
			switch(httpResult.getResponseCode()) {
			case HttpURLConnection.HTTP_OK:
				break;
			case HttpURLConnection.HTTP_NOT_FOUND:
				qtse = new QtsHttpServerNotExistException();
				throw qtse;
			case HttpURLConnection.HTTP_CLIENT_TIMEOUT:
				qtse = new QtsHttpNetworkTimeoutException();
				throw qtse;
			}
			jsonString = httpResult.getContent();
			try {
				jsonArray = new JSONArray(jsonString);
				for (int i = 0; i < jsonArray.length(); i++) {
					jsonObj = jsonArray.getJSONObject(i);
					String fileName = jsonObj.getString("text");
					boolean isFolder = jsonObj.getString("iconCls").equals("folder")? true: false;
					QtsHttpFileEntry fileEntry = new QtsHttpFileEntry(isFolder, 0, "/", fileName);
					fileList.add(fileEntry);
				}
			} catch (JSONException e) {
				e.printStackTrace();
				// get QtsHttpApi status
				jsonParser = new JsonParser(jsonString);
				tagStringValue = jsonParser.getTagValue("status");
				status = Integer.parseInt(tagStringValue);
				switch(status) {
				case QtsHttpFileStationStatus.STATUS_FAILURE:
					qtse = new QtsHttpParameterInvalidException();
					throw qtse;
				}
			}
		}
		catch (Exception e) {
			throw e;
		}

		return fileList;
	}

	@Override
	public ArrayList<QtsHttpFileEntry> getFileList(String path, int startIndex,
			int fileLimit, QtsHttpCancelController cancel) throws Exception {
		String encodedFolderName = StringTranslator.replaceBlank(URLEncoder.encode(path, "UTF-8"));
		String cgi = "cgi-bin/filemanager/utilRequest.cgi";
		String data =
				"func=get_list&sid="
		        + this.mSession.getSID()
				+ "&is_iso=0&list_mode=all&path=" + encodedFolderName
				+ "&limit=" + fileLimit
				+ "&start=" + startIndex
				+ "&hiddle_file=0&sort=filename&dir=ASC";
		String jsonString;
		String tagStringValue;
		JsonParser jsonParser;
		JSONArray jsonArray;
		JSONObject jsonObj;
		ArrayList<QtsHttpFileEntry> fileList = new ArrayList<QtsHttpFileEntry>();
		QtsHttpResponse httpResult;
		QtsHttpException qtse;
		int status = 0;
		
		try {
			QtsHttpConnection conn = new QtsHttpConnection();
			httpResult = conn.doPost(mSession, cgi, data, cancel);
			switch(httpResult.getResponseCode()) {
			case HttpURLConnection.HTTP_OK:
				break;
			case HttpURLConnection.HTTP_NOT_FOUND:
				qtse = new QtsHttpServerNotExistException();
				throw qtse;
			case HttpURLConnection.HTTP_CLIENT_TIMEOUT:
				qtse = new QtsHttpNetworkTimeoutException();
				throw qtse;
			}
			jsonString = httpResult.getContent();
			try {
				jsonParser = new JsonParser(jsonString);
				int total = Integer.valueOf(jsonParser.getTagValue("total"));
				jsonArray = jsonParser.getJsonArray("datas");
				for (int i = 0; i < jsonArray.length(); i++) {
					jsonObj = jsonArray.getJSONObject(i);
					String fileName = jsonObj.getString("filename");
					long size = Long.parseLong(jsonObj.getString("filesize"));
					boolean isFolder = jsonObj.getString("isfolder").equals("1")? true: false;
					QtsHttpFileEntry fileEntry = new QtsHttpFileEntry(isFolder, size, path, fileName);
					fileList.add(fileEntry);
				}
			} catch (JSONException e) {
				e.printStackTrace();
				// get QtsHttpApi status
				jsonParser = new JsonParser(jsonString);
				tagStringValue = jsonParser.getTagValue("status");
				status = Integer.parseInt(tagStringValue);
				switch(status) {
				case QtsHttpFileStationStatus.STATUS_FAILURE:
					qtse = new QtsHttpParameterInvalidException();
					throw qtse;
				}
			}
		}
		catch (Exception e) {
			throw e;
		}

		return fileList;
	}

	@Override
	public void uploadFileByPath(String uploadFileFullPath, String toFilePath,
			QtsHttpCancelController cancel, IQtsHttpTransferedProgressListener progressListener)
			throws Exception {
		try {
			File uploadFile = new File(uploadFileFullPath);
			if (uploadFile.exists()) {

				String destPath = toFilePath + "/" + uploadFile.getName();
				String destPathReplace = StringTranslator.replaceBlank(URLEncoder.encode(
						toFilePath.replace("/", "-") + "-", HTTP.UTF_8));
				String encodedDstFolderPath = StringTranslator.replaceBlank(URLEncoder.encode(
						toFilePath, HTTP.UTF_8));
				String cgi = "/cgi-bin/filemanager/utilRequest.cgi";
				String data = "func=upload&type=standard&sid=" + mSession.getSID()
						+ "&dest_path=" + encodedDstFolderPath
						+ "&overwrite=1"
						+ "&progress=" + destPathReplace
						+ StringTranslator.replaceBlank(URLEncoder.encode(uploadFile.getName(), HTTP.UTF_8));
				try {
					QtsHttpConnection conn = new QtsHttpConnection();
					conn.doUploadFile(mSession, uploadFileFullPath, cgi, data, cancel, progressListener);
				} catch (Exception e) {
					
				}
			}
		} catch (Exception e) {

		}
	}

	@Override
	public void downloadFileByPath(String downloadFileFullPath,
			String toFilePath, long offset, QtsHttpCancelController cancel,
			IQtsHttpTransferedProgressListener progressListener) throws Exception {
		// Get file and path name
		int idx;
		idx = downloadFileFullPath.lastIndexOf(File.separatorChar);
		String downloadFileName = downloadFileFullPath.substring(idx+1, downloadFileFullPath.length());
		String downloadPathName = downloadFileFullPath.substring(0, idx);
		
		idx = toFilePath.lastIndexOf(File.separatorChar);
		String tempDestPathName = toFilePath.substring(0, idx);
		String tempDestFileName = toFilePath + ".download";
		
		// temp file name
		File tempDestFile = new File(tempDestFileName);
		File tempDir = new File(tempDestPathName);
		File destFile = new File(toFilePath);

		try {
			if (tempDestFile.exists()) {
				tempDestFile.delete();
			} else {
				tempDir.mkdirs();
			}
			if (tempDestFile.createNewFile()) {
				String encodedPath = StringTranslator.replaceBlank(URLEncoder.encode(
						downloadPathName, "UTF-8"));
				String encodedName = StringTranslator.replaceBlank(URLEncoder.encode(
						downloadFileName, "UTF-8"));
				String cgi = "cgi-bin/filemanager/utilRequest.cgi";
				String data = "func=download&sid=" + this.mSession.getSID()
						+ "&isfolder=0" 
						+ "&source_path=" + encodedPath
						+ "&source_file=" + encodedName
						+ "&source_total=1";

				QtsHttpConnection conn = new QtsHttpConnection();
				conn.doDownloadFile(this.mSession, tempDestFileName, cgi, data, cancel, progressListener);
				if (destFile.exists()) {
					destFile.delete();
				}
				tempDestFile.renameTo(destFile);
			}
		} catch (SocketTimeoutException e) {
			QtsHttpNetworkTimeoutException ex = new QtsHttpNetworkTimeoutException();
			throw ex;
		} catch (FileNotFoundException e) {
			throw e;
		} catch (MalformedURLException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		}
	}
}

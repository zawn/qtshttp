package com.qnap.qdk.qtshttp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerPNames;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import com.qnap.qdk.util.EasySSLSocketFactory;



public class QtsHttpConnection {

	public QtsHttpConnection() {
	}
	
	/**
	 * Always verify the host - don't check for certificate
	 */
    final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
          public boolean verify(String hostname, SSLSession session) {
              return true;
          }
    };
   
    /**
     * Trust every server - don't check for any certificate
     */
    private void trustAllHosts() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[] {};
            }

                public void checkClientTrusted(X509Certificate[] chain,
                    String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain,
                    String authType) throws CertificateException {
                }
            }
        };

        // Install the all-trusting trust manager
        try {
    	    SSLContext sc = SSLContext.getInstance("TLS");
    	    sc.init(null, trustAllCerts, new java.security.SecureRandom());
    	    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
	private HttpClient mHttpClient = null;
	private ThreadSafeClientConnManager mClientConnectionManager;
	private HttpContext mHttpContext;
	private HttpParams mHttpParams;
	private void initHttpClient(QtsHttpSession session) {
		SchemeRegistry schemeRegistry = new SchemeRegistry();

		if (session == null) {
			// http scheme
			schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 8080));
			// https scheme
			schemeRegistry.register(new Scheme("https", new EasySSLSocketFactory(), 443));
		} else {
			// http scheme
			schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), (int)session.getPortNum()));
			// https scheme
			schemeRegistry.register(new Scheme("https", new EasySSLSocketFactory(), (int)session.getSSLPortNum()));
		}

		mHttpParams = new BasicHttpParams();
		mHttpParams.setParameter(ConnManagerPNames.MAX_TOTAL_CONNECTIONS, 128);  // 10
		mHttpParams.setParameter(ConnManagerPNames.MAX_CONNECTIONS_PER_ROUTE,
				new ConnPerRouteBean(3));

		mHttpParams.setParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
		HttpProtocolParams.setVersion(mHttpParams, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(mHttpParams, "utf8");
		HttpConnectionParams.setConnectionTimeout(mHttpParams, 30000);
		HttpConnectionParams.setSoTimeout(mHttpParams, 30000);

		mClientConnectionManager = new ThreadSafeClientConnManager(mHttpParams, schemeRegistry);
		mHttpContext = new BasicHttpContext();
		mHttpClient = new DefaultHttpClient(mClientConnectionManager, mHttpParams);
	}

	private void deinitHttpClient() {
		if (mClientConnectionManager != null) {
			mClientConnectionManager.shutdown();
			mClientConnectionManager = null;
		}
	}

	
	/**
	 * Do URL using Post method.
	 * @param url
	 * @param postData
	 * @return QTS HTTP response
	 * @throws Exception
	 */
	public QtsHttpResponse doPost(QtsHttpSession session, String url,
			String postData, QtsHttpCancelController cancel) throws Exception {
		BufferedReader reader = null;
		StringBuilder stringBuilder = null;
		HttpURLConnection urlConnection = null;
		DataOutputStream outputStream = null;
		QtsHttpResponse httpResponse = null;
		int responseCode = 0;
		String sURL = "";

		try {
			if (session.isSecureConnection()) {
				// SSL connection
				trustAllHosts();
				sURL = "https://" + session.getHostName() + ":" + session.getSSLPortNum() + "/" + url;
				HttpsURLConnection sslConnection = (HttpsURLConnection) new URL(sURL).openConnection();
				sslConnection.setHostnameVerifier(DO_NOT_VERIFY);
				urlConnection = sslConnection;
			}
			else {
				// Normal connection
				sURL = "http://" + session.getHostName() + ":" + session.getPortNum() + "/" + url;
				urlConnection = (HttpURLConnection) (new URL(sURL)).openConnection();
			}

			urlConnection.setUseCaches(false);
			urlConnection.setDoInput(true);
			urlConnection.setDoOutput(true);
			urlConnection.setRequestMethod("POST");                                           // Post method
			urlConnection.setRequestProperty("Charset", HTTP.UTF_8);                          // Set charset
			urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + HTTP.UTF_8);
			urlConnection.setRequestProperty("User-agent", session.getAgentName());           // Set agent name
			urlConnection.setConnectTimeout(session.getTimeOutMilliseconds());                // Set millisecond of network timeout
			urlConnection.setReadTimeout(session.getTimeOutMilliseconds());                   // Set millisecond of network timeout

			if (postData != null) {
				urlConnection.setFixedLengthStreamingMode(postData.length());
			}
			outputStream = new DataOutputStream(urlConnection.getOutputStream());
			if (outputStream != null && postData != null) {
				outputStream.writeBytes(postData);
			}
			
			// Read the output from the server
			reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			responseCode = urlConnection.getResponseCode();

			stringBuilder = new StringBuilder();
			String line = null;
			while ( (line = reader.readLine()) != null ) {
				stringBuilder.append(line + "\n");
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		urlConnection.disconnect();
		
		httpResponse = new QtsHttpResponse(responseCode, (stringBuilder != null) ? stringBuilder.toString() : "");

		return httpResponse;
	}
	
	
	/**
	 * Do URL using Get method.
	 * @param url
	 * @param postData
	 * @return QTS HTTP response
	 * @throws Exception
	 */
	public QtsHttpResponse doGet(QtsHttpSession session, String url,
			String postData, QtsHttpCancelController cancel) throws Exception {
		BufferedReader reader = null;
		StringBuilder stringBuilder = null;
		HttpURLConnection urlConnection = null;
		QtsHttpResponse httpResponse = null;
		int responseCode = 0;
		String sURL = "";

		try {
			if (session.isSecureConnection()) {
				sURL = "https://" + session.getHostName() + ":" + session.getSSLPortNum() + "/" + url + "?" + postData;
				urlConnection = (HttpsURLConnection) new URL(sURL).openConnection();
			}
			else {
				sURL = "http://" + session.getHostName() + ":" + session.getPortNum() + "/" + url + "?" + postData;
				urlConnection = (HttpURLConnection) (new URL(sURL)).openConnection();
			}

			urlConnection.setUseCaches(false);
			urlConnection.setDoInput(true);
			urlConnection.setDoOutput(false);
			urlConnection.setRequestMethod("GET");                                            // Get method
			urlConnection.setRequestProperty("Charset", HTTP.UTF_8);                          // Set charset
			urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + HTTP.UTF_8);
			urlConnection.setRequestProperty("User-agent", session.getAgentName());           // Set agent name
			urlConnection.setConnectTimeout(session.getTimeOutMilliseconds());                // Millisecond of network timeout
			urlConnection.setReadTimeout(session.getTimeOutMilliseconds());                   // Millisecond of network timeout
			urlConnection.connect();

			// Read the output from the server
			reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			responseCode = urlConnection.getResponseCode();

			stringBuilder = new StringBuilder();
			String line = null;
			while ( (line = reader.readLine()) != null ) {
				stringBuilder.append(line + "\n");
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		urlConnection.disconnect();

		httpResponse = new QtsHttpResponse(responseCode, (stringBuilder != null) ? stringBuilder.toString() : "");

		return httpResponse;
	}

	/**
	 * Download file
	 * @param session Session of File Station
	 * @param downloadFileDestFullPath Download file destination full path
	 * @param url URL
	 * @param postData Post data
	 * @param cancel Cancel download file controller
	 * @param progressListener Progress listener of download file
	 * @exception Exception
	 */
	public void doDownloadFile(QtsHttpSession session, String downloadFileDestFullPath,
			String url, String postData, QtsHttpCancelController cancel,
			IQtsHttpTransferedProgressListener progressListener) throws Exception {
		HttpURLConnection urlConnection = null;
		DataOutputStream outputStream = null;
		InputStream inputStream = null;
		String sURL = "";
		
		byte [] postDataByteArray = postData.getBytes();
		
		if (session.isSecureConnection()) {
			sURL = "https://" + session.getHostName() + ":" + session.getSSLPortNum() + "/" + url;
			urlConnection = (HttpsURLConnection) new URL(sURL).openConnection();
		}
		else {
			sURL = "http://" + session.getHostName() + ":" + session.getPortNum() + "/" + url;
			urlConnection = (HttpURLConnection) (new URL(sURL)).openConnection();
		}
		urlConnection.setDoInput(true);
		urlConnection.setDoOutput(true);
		urlConnection.setUseCaches(false);
		urlConnection.setRequestMethod("POST");
		urlConnection.setReadTimeout(session.getTimeOutMilliseconds());
		urlConnection.setConnectTimeout(session.getTimeOutMilliseconds());	
		
		urlConnection.setFixedLengthStreamingMode(postDataByteArray.length);
		urlConnection.setRequestProperty("Charset", HTTP.UTF_8);
		urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + HTTP.UTF_8);
		urlConnection.setRequestProperty("User-agent", session.getAgentName());
		urlConnection.setRequestProperty("Connection", "Keep-Alive");
		outputStream = new DataOutputStream(urlConnection.getOutputStream());
		if (outputStream != null) {
			outputStream.write(postDataByteArray);
		}
		
		inputStream = urlConnection.getInputStream();
		
		long transferedFileLengthInBytes = 0;
		FileOutputStream downloadingOutputStream = new FileOutputStream(downloadFileDestFullPath);
		byte buf[] = new byte[8192];  // 8 * 1024
		do {
			int numread = inputStream.read(buf);
			transferedFileLengthInBytes += numread;
			if (numread <= 0) {
				break;
			}
			downloadingOutputStream.write(buf, 0, numread);
			if (progressListener != null) {
				progressListener.onProgress(transferedFileLengthInBytes);
			}
		} while (!cancel.isCancel());
		downloadingOutputStream.flush();
		downloadingOutputStream.close();
		urlConnection.disconnect();
	}
	
	/**
	 * Upload file
	 * @param session Session of File Station
	 * @param uploadFileFullPath Upload file source full path
	 * @param url URL
	 * @param postData Post data
	 * @param cancel Cancel upload file controller
	 * @param progressListener Progress listener of upload file
	 * @throws Exception
	 */
	public void doUploadFile(QtsHttpSession session, String uploadFileFullPath, String url,
			String postData, QtsHttpCancelController cancel, IQtsHttpTransferedProgressListener progressListener) throws Exception {
		
		String boundary = "*****";

		initHttpClient(session);

		try {
			File uploadFile = new File(uploadFileFullPath);

			String sURL = "";
			if (session.isSecureConnection()) {
				sURL = "https://" + session.getHostName() + ":" + session.getSSLPortNum() + url + "?" + postData;
			}
			else {
				sURL = "http://" + session.getHostName() + ":" + session.getPortNum() + url + "?" + postData;
			}
			HttpPost mHttpPost = new HttpPost(sURL);
			QtsHttpUploadFileEntry entity = new QtsHttpUploadFileEntry(uploadFile, "multipart/form-data;boundary="+boundary, progressListener);
			entity.setChunked(false);
			entity.setContentEncoding("UTF-8");
			mHttpPost.setEntity(entity);

		    HttpResponse response = mHttpClient.execute(mHttpPost, mHttpContext);
			StatusLine statusLine = response.getStatusLine();

			if (statusLine.getStatusCode() == HttpURLConnection.HTTP_OK) {
				HttpEntity responseEntity = response.getEntity();
				InputStream is = responseEntity.getContent();
				int line;
				StringBuffer sb = new StringBuffer();
				int outputcount = 1;

				while (!cancel.isCancel() && ((line = is.read()) != -1) ) {
					++outputcount;
					if (outputcount % 100 == 0) {
						System.gc();
					}
					sb.append((char) line);
				}
				String responseStr = sb.toString().trim();
			}
	    } catch (Exception e) {
	    	e.printStackTrace();
	    } finally {
	    	deinitHttpClient();
	    }
	}
}


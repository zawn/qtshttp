package com.qnap.qdk.util;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class EasyX509TrustManager implements X509TrustManager {

//	private X509TrustManager mStandardTrustManager = null;
	 
    /**
     * Constructor for EasyX509TrustManager.
     */
    public EasyX509TrustManager(KeyStore keystore) throws NoSuchAlgorithmException, KeyStoreException {
        super();
        TrustManagerFactory factory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        factory.init(keystore);
        TrustManager[] trustmanagers = factory.getTrustManagers();
        if (trustmanagers.length == 0) {
            throw new NoSuchAlgorithmException("no trust manager found");
        }
    }
 
    /**
     * @see X509TrustManager#checkClientTrusted(X509Certificate[],String authType)
     */
    @Override
	public void checkClientTrusted(X509Certificate[] certificates, String authType) throws CertificateException {
    
    }
 
    /**
     * @see X509TrustManager#checkServerTrusted(X509Certificate[],String authType)
     */
    @Override
	public void checkServerTrusted(X509Certificate[] certificates, String authType) throws CertificateException {

    }
 
    /**
     * @see X509TrustManager#getAcceptedIssuers()
     */
    @Override
	public X509Certificate[] getAcceptedIssuers() {
    	return null;
    }

}

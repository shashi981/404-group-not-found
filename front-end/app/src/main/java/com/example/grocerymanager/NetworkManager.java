package com.example.grocerymanager;


import android.content.Context;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

public class NetworkManager {
    private static NetworkManager instance;

    private OkHttpClient client;
    private InputStream inputStream;
    private Certificate certificate;
    private KeyStore keyStore;
    private TrustManagerFactory trustManagerFactory;
    private TrustManager[] trustManagers;
    private SSLContext sslContext;
    private Context context;


    private NetworkManager() {
        makeNetworkRequestWithSSL();
    }
    public NetworkManager(Context context) {
        this.context = context;
        makeNetworkRequestWithSSL();
    }

    public static synchronized NetworkManager getInstance() {
        if (instance == null) {
            instance = new NetworkManager();
        }
        return instance;
    }

    private void makeNetworkRequestWithSSL() {
        try {
            inputStream = context.getResources().openRawResource(R.raw.certificate);
            certificate = readCertificate(inputStream);

            keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", certificate);

            trustManagerFactory = TrustManagerFactory.getInstance(
                    TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            trustManagers = trustManagerFactory.getTrustManagers();

            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagers, new SecureRandom());

            client = new OkHttpClient.Builder()
                    .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustManagers[0])
                    .hostnameVerifier((hostname, session) -> hostname.equals("20.104.197.24"))
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private Certificate readCertificate(InputStream inputStream) throws CertificateException {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        return certificateFactory.generateCertificate(inputStream);
    }

    public OkHttpClient getClient() {
        return client;
    }
}


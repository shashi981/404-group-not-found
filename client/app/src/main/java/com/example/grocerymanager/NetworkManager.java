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
    private Context context;


    //    ChatGPT Usage: Yes
    private NetworkManager() {
        makeNetworkRequestWithSSL();
    }

    //    ChatGPT Usage: Yes
    public NetworkManager(Context context) {
        this.context = context;
        makeNetworkRequestWithSSL();
    }

    //    ChatGPT Usage: Yes
    public static synchronized NetworkManager getInstance() {
        if (instance == null) {
            instance = new NetworkManager();
        }
        return instance;
    }

    //    ChatGPT Usage: Yes
    private void makeNetworkRequestWithSSL() {
        try {
            InputStream inputStream = context.getResources().openRawResource(R.raw.certificate);
            Certificate certificate = readCertificate(inputStream);

            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", certificate);

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                    TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagers, new SecureRandom());

            client = new OkHttpClient.Builder()
                    .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustManagers[0])
                    .hostnameVerifier((hostname, session) -> hostname.equals("20.104.197.24"))
                    .build();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //    ChatGPT Usage: Yes
    private Certificate readCertificate(InputStream inputStream) throws CertificateException {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        return certificateFactory.generateCertificate(inputStream);
    }

    //    ChatGPT Usage: Yes
    public OkHttpClient getClient() {
        return client;
    }
}


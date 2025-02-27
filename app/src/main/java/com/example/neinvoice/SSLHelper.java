package com.example.neinvoice;

import android.content.Context;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import okhttp3.CertificatePinner;
import okhttp3.OkHttpClient;

public class SSLHelper {

    /**
     * Creates an OkHttpClient instance with a custom SSL configuration using the provided certificate
     * and applies certificate pinning for security.
     *
     * @param context The application context.
     * @return OkHttpClient with custom SSL settings.
     */
    public static OkHttpClient getSafeOkHttpClient(Context context) {
        try {
            // Load the certificate from res/raw
            InputStream certificateInputStream = context.getResources().openRawResource(R.raw.localhost);

            // Create a CertificateFactory for X.509 certificates
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");

            // Generate the certificate from the InputStream
            Certificate certificate = certificateFactory.generateCertificate(certificateInputStream);
            certificateInputStream.close(); // Close the InputStream

            // Create a KeyStore to hold the certificate
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null); // Initialize the keystore
            keyStore.setCertificateEntry("localhost", certificate);

            // Create a TrustManagerFactory with the KeyStore
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);

            // Get the default TrustManager
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            X509TrustManager trustManager = (X509TrustManager) trustManagers[0];

            // Create an SSLContext with the TrustManager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{trustManager}, null);

            // Certificate pinning for security
            CertificatePinner certPinner = new CertificatePinner.Builder()
                    .add("10.0.2.2", "sha256/sN21pslzI9K0uk7RvDOLXGt9hlrc2VcNg5qavGc+3vk=") // Replace with actual fingerprint
                    .build();

            // Return an OkHttpClient with SSL configuration and certificate pinning
            return new OkHttpClient.Builder()
                    .sslSocketFactory(sslContext.getSocketFactory(), trustManager)
                    .certificatePinner(certPinner)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return new OkHttpClient.Builder().build(); // Fallback to default client in case of an error
        }
    }
}


package de.erdbeerbaerlp.splatcord2.util;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

public class SSLBypass implements X509TrustManager {
    private static final X509Certificate[] _AcceptedIssuers = new X509Certificate[]{};
    private static TrustManager[] trustManagers;

    public static void allowAllSSL(HttpsURLConnection conn) {
        conn.setHostnameVerifier((arg0, arg1) -> true);

        SSLContext context = null;
        if (trustManagers == null) {
            trustManagers = new TrustManager[]{new SSLBypass()};
        }

        try {
            context = SSLContext.getInstance("TLSv1.2");
            context.init(null, trustManagers, new SecureRandom());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }

        conn.setSSLSocketFactory(context != null ? context.getSocketFactory() : null);
    }

    @Override
    public void checkClientTrusted(
            X509Certificate[] x509Certificates, String s) {

    }

    @Override
    public void checkServerTrusted(
            X509Certificate[] x509Certificates, String s) {

    }

    public boolean isClientTrusted(X509Certificate[] chain) {
        return true;
    }

    public boolean isServerTrusted(X509Certificate[] chain) {
        return true;
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return _AcceptedIssuers;
    }
}
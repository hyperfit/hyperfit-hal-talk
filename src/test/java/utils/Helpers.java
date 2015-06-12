package utils;

import com.bodybuilding.haltalk.ContractConstants;
import com.bodybuilding.haltalk.HALTalkProcessor;
import com.bodybuilding.haltalk.Root;
import com.squareup.okhttp.OkHttpClient;
import org.hyperfit.HyperfitProcessor;
import org.hyperfit.net.HyperClient;
import org.hyperfit.net.okhttp2.OkHttp2HyperClient;

import javax.net.ssl.*;
import java.security.cert.CertificateException;

public class Helpers {
    public static OkHttpClient allTrustingOkHttpClient(){


        OkHttpClient okHttpClient = new OkHttpClient();


        try {
            final TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {

                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                }


                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                }

                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            }
            };

            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();


            okHttpClient.setSslSocketFactory(sslSocketFactory);
            okHttpClient.setHostnameVerifier(new HostnameVerifier() {

                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            return okHttpClient;
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public static Root fetchRoot(){
        HyperfitProcessor processor = HALTalkProcessor.builder()
        .hyperClient(new OkHttp2HyperClient(Helpers.allTrustingOkHttpClient()))
        .build();


       return processor.processRequest(Root.class, ContractConstants.rootURL);
    }
}

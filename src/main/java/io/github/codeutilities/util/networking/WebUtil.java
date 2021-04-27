package io.github.codeutilities.util.networking;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.codeutilities.CodeUtilities;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

public class WebUtil {
    private static final TrustManager[] trustAllCerts = {
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }

            }
    };

    private static final HostnameVerifier allHostsValid = (hostname, session) -> true;

    static {
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }

    }

    public static String getString(String urlToRead, Charset charset) throws IOException {
        URL url = new URL(urlToRead);
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), charset));
        StringBuilder builder = new StringBuilder();
        String line;

        while ((line = in.readLine()) != null) {
            builder.append("\n").append(line);
        }
        in.close();
        return builder.toString();
    }

    public static String getString(String urlToRead) throws IOException {
        //System.out.println(urlToRead);
        return getString(urlToRead, Charset.defaultCharset());
    }

    public static HttpResponse makePost(String url, JsonObject obj) {
        try {
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost post = new HttpPost(url);
            StringEntity postingString = new StringEntity(obj.toString());
            post.setEntity(postingString);
            post.setHeader("Content-type", "application/json");
            return httpClient.execute(post);
        } catch (Exception err) {
            err.printStackTrace();
            return null;
        }
    }

    public static JsonElement getJson(String url) throws IOException {
        return CodeUtilities.JSON_PARSER.parse(getString(url));
    }

}

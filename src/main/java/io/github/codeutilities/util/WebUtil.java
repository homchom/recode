package io.github.codeutilities.util;

import java.io.*;
import java.net.URL;

public class WebUtil {

    public static String getString(String urlToRead) throws IOException {
        URL url = new URL(urlToRead);
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
        StringBuilder builder = new StringBuilder();
        String line;

        while ((line = in.readLine()) != null) {
            builder.append("\n" + line);
        }
        in.close();
        return builder.toString();
    }
}

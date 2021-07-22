package io.github.codeutilities.sys.util.templates;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CompressionUtil {

    public static byte[] fromBase64(byte[] bytes) {
        return Base64.getDecoder().decode(bytes);
    }

    public static byte[] toBase64(byte[] bytes) {
        return Base64.getEncoder().encode(bytes);
    }

    public static byte[] fromGZIP(byte[] bytes) throws IOException {
        if (bytes == null) {
            return null;
        }

        GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(bytes));
        BufferedReader bf = new BufferedReader(new InputStreamReader(gis, StandardCharsets.UTF_8));
        StringBuilder outStr = new StringBuilder();
        String line;
        while ((line = bf.readLine()) != null) {
            outStr.append(line);
        }

        return outStr.toString().getBytes();
    }

    public static byte[] toGZIP(byte[] bytes) throws IOException {
        if (bytes == null) {
            return null;
        }
        ByteArrayOutputStream obj = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(obj);
        gzip.write(bytes);
        gzip.close();

        return obj.toByteArray();
    }

}
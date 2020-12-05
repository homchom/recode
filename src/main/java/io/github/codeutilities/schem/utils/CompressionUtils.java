package io.github.codeutilities.schem.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.GZIPOutputStream;

public final class CompressionUtils {
	
	/**
	 * Compress text using gzip and base64.
	 * 
	 * @param uncompressed Uncompressed Text.
	 * @return Compressed Text.
	 * @throws IOException
	 */
	public static String toGzipBase64(String uncompressed) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(uncompressed.length());
        GZIPOutputStream gzip = new GZIPOutputStream(bos);
        
        // Convert to gzip
		gzip.write(uncompressed.getBytes());
        gzip.close();
        byte[] compressed = bos.toByteArray();
        bos.close();

        // Convert to base64 and returns it
        return new String(Base64.getEncoder().encode(compressed), StandardCharsets.UTF_8);
	}
}

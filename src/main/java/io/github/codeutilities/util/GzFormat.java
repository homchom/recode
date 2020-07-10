package io.github.codeutilities.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public abstract class GzFormat {
	public static byte[] decryptBase64(byte[] Base64F) {
		return  Base64.getDecoder().decode(Base64F);
	}
	
	public static byte[] encryptBase64(byte[] Base64F) {
		return Base64.getEncoder().encode(Base64F);
	}
	
	public static byte[] decompress(byte[] str) throws Exception {
		if (str == null ) {
			return null;
		}

		GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(str));
		BufferedReader bf = new BufferedReader(new InputStreamReader(gis, "UTF-8"));
		String outStr = "";
		String line;
		while ((line=bf.readLine())!=null) {
			outStr += line;
		}
		return outStr.getBytes();

	}

	public static byte[] compress(byte[] str) throws Exception {
		if (str == null) {
			return null;
		}
		ByteArrayOutputStream obj=new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(obj);
		gzip.write(str);
		gzip.close();

		return obj.toByteArray();
	}

}
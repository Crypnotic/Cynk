package me.crypnotic.cynk.util;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

public class Files {

	public static String getMD5Signature(File file) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");

			FileInputStream input = new FileInputStream(file);

			int length;
			byte[] buffer = new byte[1024];
			while ((length = input.read(buffer)) > 0) {
				digest.update(buffer, 0, length);
			}
			input.close();

			byte[] checksum = digest.digest();

			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < checksum.length; i++) {
				builder.append(Integer.toString((checksum[i] & 0xff) + 0x100, 16).substring(1));
			}

			return builder.toString();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return null;
	}
}

package me.crypnotic.cynk;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DuplicateScanner implements Runnable {

	private final File directory;
	private final List<String> hashes;
	private final Comparator<File> sort;
	private int deleted;

	public DuplicateScanner(File directory) {
		this.directory = directory;
		this.hashes = new ArrayList<String>();
		this.deleted = 0;

		this.sort = (alpha, beta) -> {
			if (alpha.isDirectory() && beta.isFile()) {
				return 1;
			} else if (alpha.isFile() && beta.isDirectory()) {
				return -1;
			} else {
				return beta.getName().compareTo(alpha.getName());
			}
		};
	}

	public void run() {
		if (hashes.size() > 0) {
			hashes.clear();
		}

		List<File> duplicates = getDuplicates(directory);

		duplicates.forEach(this::delete);

		this.deleted = duplicates.size();
	}

	private List<File> getDuplicates(File directory) {
		List<File> duplicates = new ArrayList<File>();

		List<File> list = Arrays.asList(directory.listFiles());

		Collections.sort(list, sort);

		for (File file : list) {
			if (file.isDirectory()) {
				List<File> child = getDuplicates(file);

				if (child.size() == file.listFiles().length) {
					duplicates.add(file);
				} else {
					duplicates.addAll(child);
				}
			} else {
				String checksum = checksum(file);

				if (checksum == null) {
					continue;
				}

				if (hashes.contains(checksum)) {
					duplicates.add(file);
				} else {
					hashes.add(checksum);
				}
			}
		}

		return duplicates;
	}

	private String checksum(File file) {
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

	private int delete(File file) {
		int deleted = 0;
		if (file.isDirectory()) {
			for (File local : file.listFiles()) {
				deleted += delete(local);
			}
		}
		file.delete();

		return (deleted += 1);
	}

	public int getDeleted() {
		return deleted;
	}
}

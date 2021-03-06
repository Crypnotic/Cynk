package me.crypnotic.cynk.api;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import me.crypnotic.cynk.util.Files;

public class DuplicateScanner implements Runnable {

	private final File directory;
	private final List<String> hashes;
	private final Comparator<File> sorter;
	private int deleted;

	public DuplicateScanner(File directory) {
		this.directory = directory;
		this.hashes = new ArrayList<String>();
		this.deleted = 0;

		this.sorter = (alpha, beta) -> {
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

		this.deleted = getDuplicates(directory).stream().mapToInt(this::delete).sum();
	}

	private List<File> getDuplicates(File directory) {
		List<File> duplicates = new ArrayList<File>();

		for (File file : Files.sort(directory.listFiles(), sorter)) {
			if (file.isDirectory()) {
				List<File> child = getDuplicates(file);

				if (child.size() == file.listFiles().length) {
					duplicates.add(file);
				} else {
					duplicates.addAll(child);
				}
			} else {
				String checksum = Files.getMD5Signature(file);

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

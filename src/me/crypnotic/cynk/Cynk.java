package me.crypnotic.cynk;

import java.io.File;

import me.crypnotic.cynk.api.DuplicateScanner;

public class Cynk {

	public static void main(String[] arguments) {
		if (arguments.length > 0) {
			Cynk remover = new Cynk();

			remover.init(arguments[0]);
		} else {
			System.out.println("Arguments required: [path]");
		}
	}

	public void init(String name) {
		File directory;
		if (name.startsWith("./")) {
			directory = new File(System.getProperty("user.dir"), name.substring(2));
		} else {
			directory = new File(name);
		}

		if (directory == null || !directory.exists() || !directory.isDirectory()) {
			log("Unknown directory: " + name);
			return;
		}

		DuplicateScanner scanner = new DuplicateScanner(directory);

		log("Initializing scan for duplicate files in directory: " + directory.getAbsolutePath());

		scanner.run();

		log("Deleted " + scanner.getDeleted() + " duplicate files.");
	}

	public static void log(Object... values) {
		for (Object value : values) {
			System.out.println(value);
		}
	}
}

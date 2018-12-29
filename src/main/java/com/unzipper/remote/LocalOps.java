package com.unzipper.remote;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LocalOps implements FileOps {

	@Override
	public void copy(Path source, Path destinationDirectory, boolean dryRun) {
		log.info("Copying from: {} ,to: {}", source, destinationDirectory);
		
		if (dryRun) {
			return;
		}
		
		File src = source.toFile();
		File destDir = destinationDirectory.toFile();

		try {
			if (src.isDirectory()) {
				FileUtils.copyDirectory(src, destDir);
			} else {
				File destFile = destinationDirectory.resolve(source.getFileName()).toFile();
				FileUtils.copyFile(src, destFile);
			}

		} catch (IOException e) {
			log.error("Failed to copy");
		}

	}

	@Override
	public Set<String> ls(Path directory, String[] extensions) {
		File dir = directory.toFile();
		LinkedHashSet<String> fileNames = new LinkedHashSet<>();
		if (dir.isDirectory()) {
			FileUtils.listFiles(dir, extensions, false).stream().forEach(f -> fileNames.add(f.getName()));
			return fileNames;
		} else {
			log.error("Not a directory");
		}
		return fileNames;
	}

}

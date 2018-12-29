package com.unzipper.unzip;

import java.nio.charset.Charset;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.hash.Hashing;
import com.unzipper.config.Configs;
import com.unzipper.config.Configs.Cloud;
import com.unzipper.remote.FileOps;
import com.unzipper.remote.LocalOps;

import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

@Service
@Slf4j
public class UnzipService {

	@Autowired
	private Configs conf;

	private FileOps fileOps;

	public void execute(String... args) {
		conf.init(args);
		initFileOps();
		unzipFiles();
	}

	private void initFileOps() {
		if (conf.getCloud().equals(Cloud.GCP)) {
			// TODO:
			log.info("Setting up gcp file ops");
		} else if (conf.getCloud().equals(Cloud.AWS)) {
			// TODO:
			log.info("Setting up aws file ops");
		} else {
			log.info("Setting up local file ops");
			fileOps = new LocalOps();
		}
	}

	private void unzipFiles() {
		String[] extensions = { "zip" };
		for (String zipName : fileOps.ls(conf.getRemoteDir(), extensions)) {
			if (filterBasedOnHash(zipName)) {
				fileOps.copy(conf.getRemoteDir().resolve(zipName), conf.getWorkDir(), 
						conf.isDry());

				unzip(conf.getWorkDir().resolve(zipName), conf.getWorkDir().resolve(conf.getCurrentWorker()),
						conf.isDry());

				fileOps.copy(conf.getWorkDir().resolve(conf.getCurrentWorker()), conf.getRemoteDir(), 
						conf.isDry());
			}
		}
	}

	private void unzip(Path file, Path outputDir, boolean dryRun) {
		log.info("Starting to unzip: {} to outputDir {}", file.toString(), outputDir.toString());
		if (dryRun) {
			return;
		}

		try {

			ZipFile zip = new ZipFile(file.toString());
			zip.extractAll(outputDir.toString());

		} catch (ZipException e) {
			log.error("Failed to extract zip", e);
		}

	}

	private boolean filterBasedOnHash(String name) {
		int workerId = Hashing.consistentHash(Hashing.md5().hashString(name, Charset.defaultCharset()),
				conf.getParticipatingWorkers().size());
		String worker = conf.getParticipatingWorkers().get(workerId);

		if (worker.equalsIgnoreCase(conf.getCurrentWorker())) {
			log.debug("{} selected for work: {}", conf.getCurrentWorker(), name);
			return true;
		} else {
			return false;
		}
	}

//	private boolean filterExtension(Path file, String extension) {
//		return file.getFileName().toString().endsWith(extension);
//	}

}

package com.unzipper.unzip;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.hash.Hashing;
import com.unzipper.config.Configs;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UnzipService {
	
	@Autowired
	private Configs conf;
	
	public void execute(String... args) {
		conf.init(args);
		unzipFiles();
	}
	
	private void unzipFiles() {
		try {
			Files.walk(conf.getRemoteDir())
				.filter(f -> filterExtension(f, ".zip"))
				.filter(f -> filterBasedOnHash(f.getFileName().toString()))
				.forEach(f -> unzip(f, conf.getWorkDir()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void unzip(Path file, Path outputDir) {
		log.info("Starting to unzip: {}", file.toString());
	}
	
	private boolean filterBasedOnHash(String name) {
		int workerId = Hashing.consistentHash(Hashing.md5().hashString(name, Charset.defaultCharset()), conf.getParticipatingWorkers().size());
		String worker = conf.getParticipatingWorkers().get(workerId);
		
		if(worker.equalsIgnoreCase(conf.getCurrentWorker())) {
			log.debug("{} selected for work: {}", conf.getCurrentWorker(), name);
			return true;
		}else {
			return false;
		}
	}
	
	private boolean filterExtension(Path file, String extension) {
		return file.getFileName().toString().endsWith(extension);
	}

}

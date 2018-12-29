package com.unzipper.unzip;

import java.nio.charset.Charset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.hash.Hashing;
import com.unzipper.config.Configs;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UnzipService {
	
	@Autowired
	private Configs configs;
	
	public void execute(String... args) {
		configs.init(args);
		log.info(configs.currentWorker);
		log.info(configs.participatingWorkers.toString());
	}
	
	private boolean filterBasedOnHash(String name) {
		int workerId = Hashing.consistentHash(Hashing.md5().hashString(name, Charset.defaultCharset()), 10);
		return true;
	}

}

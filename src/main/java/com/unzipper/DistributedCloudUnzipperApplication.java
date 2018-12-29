package com.unzipper;

import static java.lang.System.exit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner.Mode;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import com.unzipper.unzip.UnzipService;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class DistributedCloudUnzipperApplication implements CommandLineRunner{

	@Autowired
	private UnzipService unzipService;
	
	public static void main(String[] args) {
		SpringApplication app = new SpringApplicationBuilder(DistributedCloudUnzipperApplication.class)
				.bannerMode(Mode.OFF).build();
		
		app.run(args);
		
	}

	@Override
	public void run(String... args) throws Exception {
		
		if (args.length > 0) {
			unzipService.execute(args);
        } else {
            log.info("No arguments provided, exiting.");
        }

        exit(0);
	}

}


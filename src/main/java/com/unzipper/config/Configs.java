package com.unzipper.config;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.context.annotation.Configuration;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.internal.Lists;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@Data
public class Configs {

	@Parameter(names = "--participatingWorkers", description = "Total workers participating in the unzipping")
    private List<String> participatingWorkers = Lists.newArrayList();
 
    @Parameter(names = "--currentWorker", description = "Current worker name")
    private String currentWorker;
    
    @Parameter(names = "--workDir", description = "Work dir")
    private Path workDir = Paths.get("src/main/resources/work");
    
    @Parameter(names = "--remoteDir", description = "Remote dir which contains the zips")
    private Path remoteDir = Paths.get("src/main/resources/remote");
    
    
    
    
    
    public void init(String... args) {
    	JCommander.newBuilder()
		  .addObject(this)
		  .build()
		  .parse(args);
    	
    	log.info("Arguments initialized ...");
    	for (Field field: Configs.class.getDeclaredFields()) {
    		try {
				log.info("{} => {}", field.getName(), field.get(this));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
    	}
    	
    }

}

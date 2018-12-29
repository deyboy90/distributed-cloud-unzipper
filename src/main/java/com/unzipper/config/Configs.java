package com.unzipper.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.internal.Lists;

@Configuration
public class Configs {

	@Parameter(names = "--participatingWorkers", description = "Total workers participating in the unzipping")
    public List<String> participatingWorkers = Lists.newArrayList();
 
    @Parameter(names = "--currentWorker", description = "Current worker name")
    public String currentWorker;
    
    @Parameter(names = "--debug", description = "Debug mode")
    public boolean debug = false;
    
    public void init(String... args) {
    	JCommander.newBuilder()
		  .addObject(this)
		  .build()
		  .parse(args);
    }

}

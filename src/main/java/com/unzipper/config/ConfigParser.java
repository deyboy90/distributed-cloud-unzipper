package com.unzipper.config;

import com.beust.jcommander.JCommander;

public class ConfigParser {
	
	public Configs configs;
	
	public void parseConfigs(String[] args) {
    	configs = new Configs();

		JCommander.newBuilder()
		  .addObject(configs)
		  .build()
		  .parse(args);
		
    }

}

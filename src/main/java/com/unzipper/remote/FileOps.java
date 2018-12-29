package com.unzipper.remote;

import java.nio.file.Path;
import java.util.Set;

public interface FileOps {
	
	public void copy(Path source, Path destination, boolean dryRun);
	
	public Set<String> ls(Path directory, String[] extensions);

}

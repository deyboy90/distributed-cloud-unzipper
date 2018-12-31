package com.unzipper.remote;

import java.nio.file.Path;
import java.util.Set;

public interface FileOps {
	
	public void upload(Path remote, Path local, boolean dryRun);
	
	public void download(Path remote, Path local, boolean dryRun);
	
	public Set<String> ls(Path directory, String[] extensions);

}

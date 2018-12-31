package com.unzipper.remote;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.Set;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.BlobListOption;
import com.google.cloud.storage.StorageOptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GCPOps implements FileOps {
	Storage storage;

	public GCPOps() {
		log.info("Initalizing gcs storage");
		storage = StorageOptions.getDefaultInstance().getService();
	}

	@Override
	public void download(Path remote, Path local, boolean dryRun) {
		log.info("Downloading  from: {} ,to: {}", remote, local);
		
		String bucketName = remote.getName(0).toString();
		
		
		try {
			if (isDir(remote)) {
				// again call ls and get all files under it and download
			} else {
				String objectName = remote.subpath(1, remote.getNameCount()).toString();
				Blob blob = storage.get(BlobId.of(bucketName, objectName));
				
				// Creating the file locally before downloading, deleting if exists
				Path localFile = local.resolve(remote.getFileName());
				Files.deleteIfExists(localFile);
				Files.createFile(localFile);
				
				blob.downloadTo(localFile);
			}

		} catch (Exception e) {
			log.error("Failed to copy", e);
		}
	}
	
	@Override
	public void upload(Path local, Path remote, boolean dryRun) {
		log.info("Uploading from: {} ,to: {}", local, remote);
		
		String bucketName = remote.getName(0).toString();
		
		
		
		try {
			if (local.toFile().isDirectory()) {

				for (String f:local.toFile().list()){
					String objectName = remote.subpath(1, remote.getNameCount()).resolve(f).toString();
					uploadBlob(local.resolve(f), bucketName, objectName);
				}
				
			} else {
				String objectName = remote.subpath(1, remote.getNameCount()).toString();
				uploadBlob(local, bucketName, objectName);
			}

		} catch (Exception e) {
			log.error("Failed to copy", e);
		}
	}
	
	private void uploadBlob(Path file, String bucketName, String objectName) {
		BlobId blobId = BlobId.of(bucketName, objectName);
		BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
		try {
			storage.create(blobInfo, Files.readAllBytes(file));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private boolean isDir(Path path) {
		if (path.toString().endsWith("/")) {
			return true;
		} else {
			return false;
		}
		
		
	}

	@Override
	public Set<String> ls(Path directory, String[] extensions) {
		String bucketName = directory.getName(0).toString();
		String subDir = directory.subpath(1, directory.getNameCount()).toString();
		
		LinkedHashSet<String> fileNames = new LinkedHashSet<>();
		
		Page<Blob> blobs = storage.list( bucketName, BlobListOption.prefix(subDir));
			
		for (Blob blob : blobs.iterateAll()) {
			if (!blob.getName().endsWith("/")) {
				fileNames.add(Paths.get(blob.getName()).getFileName().toString());
			}
		}
		return fileNames;
	}
	
	public static void main(String[] args) {
		Path directory = Paths.get("/coverage-bucket/remote/test/test2/test");
		String bucketName = directory.getName(0).toString();
		System.out.println(bucketName);
		String subDir = directory.subpath(1, directory.getNameCount()).toString();
		System.out.println(subDir);
		
		
	}

}

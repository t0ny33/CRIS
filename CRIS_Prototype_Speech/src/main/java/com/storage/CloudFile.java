package com.storage;

public class CloudFile {
	private String bucketName;
	private String blobPath;
	private String contentType;
	private String gcsURI;

	public CloudFile(String bucketName, String blobPath, String gcsURI, String contentType) {
		this.bucketName = bucketName;
		this.blobPath = blobPath;
		this.gcsURI = gcsURI;
		this.contentType = contentType;
	}

	public String getBucketName() {
		return bucketName;
	}

	public String getBlobPath() {
		return blobPath;
	}

	public String getContentType() {
		return contentType;
	}

	public String getGcsURI() {
		return gcsURI;
	}

}

package com.storage;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CloudStorageController c = new CloudStorageController();
		String bucketName = "speech-client-dialogue";
		String directory = "All Recordings";
		String contentType = "audio/flac";
		String recordingName = "Voice 009.flac";
		
		System.out.println(c.generateSingleRecordingURI(bucketName, directory, contentType, recordingName));
	}

}

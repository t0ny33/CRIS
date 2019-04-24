package com.storage;

import java.util.ArrayList;

import com.config.Config;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CloudStorageController c = new CloudStorageController();
		DataStorage d = new DataStorage(Config.LOG_FILE_PATH);
		c.setLogStorageLocation(d);
		String bucketName = "cris-storage-bucket";
		String directory = "short-audio-recordings";
		String contentType = "audio/x-flac";
		String recordingName = "Voice 009.flac";

		ArrayList<String> uriList = new ArrayList<String>();
		uriList = c.generateBatchRecordingsURI(bucketName, directory, contentType);

		for (String string : uriList) {
			System.out.println(string);
		}

		uriList = c.generateAllRecordingsURI(bucketName, contentType);

		for (String string : uriList) {
			System.out.println(string);
		}

		System.out.println(c.generateSingleRecordingURI(bucketName, directory, contentType, recordingName));
	}

}

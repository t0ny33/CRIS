package com.sandbox;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.google.cloud.storage.Storage;
import com.speech.DataStorage;
import com.speech.Recognizer;
import com.speech.Transcription;
import com.storage.*;
//import com.google.api.gax.paging.Page;
//import com.google.cloud.storage.Blob;
//import com.google.cloud.storage.Bucket;
//import com.google.cloud.storage.Storage;
//import com.google.cloud.storage.StorageOptions;test-bucket-01
//import com.google.cloud.storage.Storage.BlobGetOption;
//import com.google.cloud.storage.Storage.BlobListOption;

public class TestCRIS {

	static CloudStorageController c;
	static DataStorage jsonStorage;
	static DataStorage localStorage;
	static List<String> recordingsURI = null;
	static String jsonFilePath;
	static String selectedBucketName;
	static String selectedContentType;
	static String selectedDirectoryName;
	static List<String> batchRecordingsURI = null;

	public static void main(String... args) throws Exception {
		CRIS cris = new CRIS();
		cris.runCRIS();
	}

}

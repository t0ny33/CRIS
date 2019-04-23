package com.storage;

import java.sql.Timestamp;
import java.util.ArrayList;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.Storage.BlobListOption;
import com.google.cloud.storage.StorageClass;
import com.google.cloud.storage.StorageOptions;
import com.speech.DataStorage;
import com.speech.Transcription;

public class CloudStorageController {

	private Storage mainStorage;
	private DataStorage logStorage;

	public CloudStorageController() {
		this.mainStorage = StorageOptions.getDefaultInstance().getService();
	}

	public void setLogStorageLocation(DataStorage newDataStorage) {
		this.logStorage = newDataStorage;
	}

	public void createTranscriptionFile(String bucketName, String blobName, String contentType, Transcription t) {
		try {
			Timestamp time = new Timestamp(System.currentTimeMillis());
			System.out.println("---> GENERATING BLOB FILE...");
			BlobId blobId = BlobId.of(bucketName, blobName);
			BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(contentType).build();
			mainStorage.create(blobInfo, t.getTranscriptionText().getBytes());

			System.out.println("|--> Created blob: " + blobName);
			System.out.println("|--> Created at: " + time);
			System.out.println("|--> Bucket location:  " + bucketName);
			System.out.println("---> DONE!");
			System.out.println();
			logStorage.saveGenericLog("BLOB: " + blobName + " WAS CREATED AT: " + time + ", BUCKET LOCATION: "
					+ bucketName + ", CONTENT TYPE:" + contentType);
		} catch (StorageException e) {
			System.out.println("---> ERROR AT TRANSCRIPTION FILE GENERATION!");
			System.out.println("---> CHECK INPUT PARAMETERS FOR FILE GENERATION AND TRY AGAIN...");
			System.out.println();
		}
	}

	public void createBucket(String bucketName) {
		try {
			Timestamp time = new Timestamp(System.currentTimeMillis());
			System.out.println("---> BUCKET GENERATION STARTED AT: " + time);
			Bucket createdBucket = mainStorage.create(BucketInfo.newBuilder(bucketName)
					.setStorageClass(StorageClass.COLDLINE).setLocation("eur4").build());

			System.out.println("---> Created bucket: " + createdBucket.getName());
			System.out.println("---> Bucket location: " + createdBucket.getLocation());
			System.out.println("---> Bucket creation time: " + time);
			System.out.println("---> DONE!");
			System.out.println();
			logStorage.saveGenericLog("BUCKET: " + bucketName + "WAS CREATED AT:" + time);
		} catch (StorageException e) {
			System.out.println("---> ERROR AT BUCKET GENERATION!");
			System.out.println("---> CHECK IF BUCKET NAME IS UNIQUE AND TRY AGAIN...");
			System.out.println();
		}

	}

	public void listAll(String bucketName, String contentType) {
		try {
			Timestamp time = new Timestamp(System.currentTimeMillis());
			System.out.println("---> LISTING ALL URIs...");
			System.out.println("---> File type: " + contentType);
			System.out.println("---> Bucket: " + bucketName);
			Bucket rootBucket = mainStorage.get(bucketName);
			int n = 1;
			for (Blob currentBlob : rootBucket.list().iterateAll()) {
				if (currentBlob.getContentType().contentEquals(contentType)) {
					System.out.print(n + "\t---> ");
					System.out.println("gs://" + rootBucket.getName() + "/" + currentBlob.getName());
					n++;
				}
			}
			System.out.println("---> DONE!");
			System.out.println();
			logStorage.saveGenericLog("COMPLETE URI LISTING FOR BUCKET: " + bucketName + " WAS CREATED AT:" + time);
		} catch (StorageException e) {
			System.out.println("---> ERROR AT LISTING ALL THE RECORDINGS URI PATHS!");
			System.out.println("---> CHECK INPUT PARAMETERS AND TRY AGAIN...");
			System.out.println();
		} catch (NullPointerException e) {
			System.out.println("---> ERROR AT LISTING ALL THE RECORDINGS URI PATHS!");
			System.out.println("---> NO INPUT PARAMETERS WERE DETECTED!");
			System.out.println("---> MAKE SURE YOU INITIALIZE THEM FIRST...");
			System.out.println();
		}
	}

	public void listRecordingsFromDirectory(String bucketName, String directory, String contentType) {
		try {
			directory = directory.concat("/");
			Timestamp time = new Timestamp(System.currentTimeMillis());
			System.out.println("---> LISTING ALL URIs FROM A DIRECTORY...");
			System.out.println("|--> Listing all the URIs for file type: " + contentType);
			System.out.println("|--> Bucket: " + bucketName);
			System.out.println("|--> Directory: " + directory);

			Page<Blob> blobs = mainStorage.list(bucketName, BlobListOption.currentDirectory(),
					BlobListOption.prefix(directory));
			int n = 1;

			for (Blob blob : blobs.iterateAll()) {
				if (blob.getContentType().contentEquals(contentType)) {
					System.out.println(n + "\t|-->" + "gs://" + bucketName + "/" + blob.getName());
					n++;
				}
			}
			System.out.println("---> DONE!");
			System.out.println();
			logStorage.saveGenericLog("URI LISTING FOR DIRECTORY: " + directory + " FROM BUCKET:" + bucketName
					+ " WAS CREATED AT:" + time);
		} catch (StorageException e) {
			System.out.println("---> ERROR AT LISTING RECORDINGS FROM A DIRECTORY!");
			System.out.println("---> CHECK INPUT PARAMETERS AND TRY AGAIN...");
			System.out.println();
		}

	}

	public ArrayList<String> generateRecordingsURIFromDirectory(String bucketName, String directory,
			String contentType) {
		try {
			Timestamp time = new Timestamp(System.currentTimeMillis());
			ArrayList<String> listRecordingURI = new ArrayList<>();

			System.out.println("---> GENERATING ALL RECORDING URIs FROM DIRECTORY..");
			System.out.println("|--> Bucket: " + bucketName);
			System.out.println("|--> Directory: " + directory);
			System.out.println("|--> Content type: " + contentType);

			Page<Blob> blobs = mainStorage.list(bucketName, BlobListOption.currentDirectory(),
					BlobListOption.prefix(directory));

			for (Blob blob : blobs.iterateAll()) {
				if (blob.getContentType().contentEquals(contentType)) {
					listRecordingURI.add("gs://" + bucketName + "/" + blob.getName());
				}
			}

			System.out.println("|--> Recordings URIs generated: " + listRecordingURI.size());
			System.out.println("---> DONE!");
			System.out.println();
			logStorage.saveGenericLog(listRecordingURI.size() + " URIs WERE GENERATED FOR DIRECTORY: " + directory
					+ " FROM BUCKET:" + bucketName + " AT:" + time);
			return listRecordingURI;
		} catch (StorageException e) {
			System.out.println("---> ERROR AT LISTING RECORDINGS FROM A DIRECTORY!");
			System.out.println("---> CHECK INPUT PARAMETERS AND TRY AGAIN...");
			System.out.println();
		}
		return null;
	}

	public ArrayList<String> generateAllRecordingsURI(String bucketName, String contentType) {
		try {
			Timestamp time = new Timestamp(System.currentTimeMillis());
			ArrayList<String> listRecordingURI = new ArrayList<>();

			System.out.println("---> GENERATING ALL RECORDING URIs..");
			System.out.println("---> Bucket: " + bucketName);
			System.out.println("---> Content type: " + contentType);

			Bucket rootBucket = mainStorage.get(bucketName);

			for (Blob currentBlob : rootBucket.list().iterateAll()) {
				if (currentBlob.getContentType().contentEquals(contentType)) {
					listRecordingURI.add("gs://" + rootBucket.getName() + "/" + currentBlob.getName());
				}
			}
			System.out.println("---> Recordings URIs generated: " + listRecordingURI.size());
			System.out.println("---> DONE!");
			System.out.println();
			logStorage.saveGenericLog(
					listRecordingURI.size() + " WERE GENERATED FROM BUCKET:" + bucketName + " AT:" + time);
			return listRecordingURI;
		} catch (StorageException e) {
			System.out.println("---> ERROR AT COMPLETE GENERATION FOR RECORDINGS URI PATHS!");
			System.out.println("---> CHECK INPUT PARAMETERS AND TRY AGAIN...");
			System.out.println();
		}
		return null;
	}

	public void saveCloudToJSON(String bucketName, String contentType, DataStorage d) {
		try {
			Timestamp time = new Timestamp(System.currentTimeMillis());
			System.out.println("---> SAVING ALL CLOUD FILE PATHS TO JSON FORMAT...");
			System.out.println("|--> Bucket: " + bucketName);
			System.out.println("|--> Content type: " + contentType);

			Bucket rootBucket = mainStorage.get(bucketName);
			ArrayList<CloudFile> filesToExport = new ArrayList<>();

			for (Blob currentBlob : rootBucket.list().iterateAll()) {
				if (currentBlob.getContentType().contentEquals(contentType)) {
					CloudFile currentFile = new CloudFile(rootBucket.getName(),
							rootBucket.getName() + "/" + currentBlob.getName(),
							"gs://" + rootBucket.getName() + "/" + currentBlob.getName(), contentType);
					filesToExport.add(currentFile);

				}
			}
			d.saveCloudBlobs(filesToExport);
			System.out.println("---> DONE!");
			System.out.println();
			logStorage.saveGenericLog("CLOUD BLOB URI INFROMATION WAS EXPORTED TO JSON AT " + time);
		} catch (StorageException e) {
			System.out.println("---> ERROR AT COMPLETE GENERATION FOR RECORDINGS URI PATHS!");
			System.out.println("---> CHECK INPUT PARAMETERS AND TRY AGAIN...");
			System.out.println();
		}
	}

	public ArrayList<String> generateBatchRecordingsURI(String bucketName, String directory, String contentType) {
		try {
			Timestamp time = new Timestamp(System.currentTimeMillis());
			Page<Blob> blobs = mainStorage.list(bucketName, BlobListOption.currentDirectory(),
					BlobListOption.prefix(directory));
			ArrayList<String> listRecordingURI = new ArrayList<>();

			System.out.println("---> GENERATING BATCH RECORDINGS URIs...");
			System.out.println("|--> Directory:" + directory);
			System.out.println("|--> Content type: " + contentType);

			for (Blob blob : blobs.iterateAll()) {
				if (blob.getContentType().contentEquals(contentType)) {
					listRecordingURI.add("gs://" + bucketName + "/" + blob.getName());
				}
			}

			System.out.println("|--> URI(s)found: " + listRecordingURI.size());
			System.out.println("|--> Bucket: " + bucketName);
			System.out.println("|--> Directory: " + directory);
			System.out.println("---> DONE!");
			logStorage.saveGenericLog(listRecordingURI.size() + " URI(s) were generated from /" + bucketName + "/"
					+ directory + " directory" + time);
			System.out.println();
			return listRecordingURI;
		} catch (StorageException e) {
			System.out.println("---> ERROR AT BATCH GENERATION FOR THE RECORDINGS URI PATHS!");
			System.out.println("---> CHECK INPUT PARAMETERS AND TRY AGAIN...");
			System.out.println();
		}
		return null;
	}

	public String generateSingleRecordingURI(String bucketName, String directory, String contentType,
			String recordingName) {
		try {
			directory = directory.concat("/");
			Page<Blob> blobs = mainStorage.list(bucketName, BlobListOption.currentDirectory(),
					BlobListOption.prefix(directory));
			String generatedURI = "";
			System.out.println("---> GENERATING SINGLE RECORDING URI...");

			for (Blob blob : blobs.iterateAll()) {
				if (blob.getName().equals(directory + recordingName)) {
					generatedURI = "gs://" + bucketName + "/" + directory + recordingName;
					System.out.println("|--> Bucket: " + bucketName);
					System.out.println("|--> Directory: " + directory);
					System.out.println("|--> Blob: " + recordingName);
					System.out.println("|--> Content type: " + contentType);
					System.out.println("---> DONE!");
					System.out.println();
					System.out.println("---> Generated URI: " + generatedURI);
					return generatedURI;
				}
			}
		} catch (StorageException e) {
			System.out.println("---> ERROR AT SINGLE GENERATION FOR RECORDING URI PATH!");
			System.out.println("---> CHECK INPUT PARAMETERS AND TRY AGAIN...");
			System.out.println();
		}
		System.out.println("---> ERROR AT SINGLE GENERATION FOR RECORDING URI PATH!");
		System.out.println("---> CHECK INPUT PARAMETERS AND TRY AGAIN...");
		System.out.println();
		return null;
	}
}
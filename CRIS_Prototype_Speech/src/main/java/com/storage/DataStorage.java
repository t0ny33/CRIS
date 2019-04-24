package com.storage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.language.Conversation;
import com.speech.Transcription;

public class DataStorage {

	private String storagePath;
	private OutputStream outputStream;
	private File file;

	public DataStorage(String storagePath) {
		this.storagePath = storagePath;
		this.file = new File(storagePath);
		this.outputStream = null;
	}

	public void saveTranscriptions(List<Transcription> transcriptions) {
		try {
			System.out.println("---> SAVING TRANSCRIPTION RESULTS IN JSON FORMAT...");
			System.out.println("|--> Number of transcriptions: " + transcriptions.size());

			outputStream = null;

			Gson gson = new GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().disableHtmlEscaping()
					.setDateFormat("yyyy-MM-dd hh:mm:ss").create();

			this.outputStream = new FileOutputStream(file, true);
			BufferedWriter bufferedWriter;
			bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

			for (Transcription transcription : transcriptions) {
				gson.toJson(transcription, bufferedWriter);
			}

			System.out.println("|--> Transcriptions saved to JSON format: " + transcriptions.size());
			System.out.println("|--> JSON saved at: " + this.storagePath);
			System.out.println("---> DONE!");
			System.out.println();

			bufferedWriter.close();
		} catch (IOException e) {
			System.out.println("---> ERROR AT JSON WRITING!");
			System.out.println("---> CHECK INPUT PARAMETERS AND TRY AGAIN...");
		}

	}

	public void saveTranscription(Transcription transcription) {
		try {
			System.out.println("---> SAVING TRANSCRIPTION RESULT IN JSON FORMAT...");

			outputStream = null;
			Gson gson = new GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().disableHtmlEscaping()
					.setDateFormat("yyyy-MM-dd hh:mm:ss").create();
			this.outputStream = new FileOutputStream(file, true);
			BufferedWriter bufferedWriter;
			bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
			gson.toJson(transcription, bufferedWriter);

			System.out.println("|--> Transcription saved to JSON format!");
			System.out.println("|--> JSON saved at: " + this.storagePath);
			System.out.println("---> DONE!");
			System.out.println();

			bufferedWriter.close();
		} catch (IOException e) {
			System.out.println("---> ERROR AT JSON WRITING!");
			System.out.println("---> CHECK INPUT PARAMETERS AND TRY AGAIN...");
		}

	}

	public void saveCoversation(Conversation c) {
		try {
			System.out.println("---> SAVING CONVERSATION RESULT IN JSON FORMAT...");

			outputStream = null;
			Gson gson = new GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().disableHtmlEscaping()
					.setDateFormat("yyyy-MM-dd hh:mm:ss").create();
			this.outputStream = new FileOutputStream(file, true);
			BufferedWriter bufferedWriter;
			bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
			gson.toJson(c, bufferedWriter);

			System.out.println("|--> Conversation saved to JSON format!");
			System.out.println("|--> JSON saved at: " + this.storagePath);
			System.out.println("---> DONE!");
			System.out.println();

			bufferedWriter.close();
		} catch (IOException e) {
			System.out.println("---> ERROR AT JSON WRITING!");
			System.out.println("---> CHECK INPUT PARAMETERS AND TRY AGAIN...");
		}
	}

	public void saveCloudBlob(CloudFile blob) {
		try {
			System.out.println("---> SAVING BLOB IN JSON FORMAT...");

			outputStream = null;
			Gson gson = new GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().disableHtmlEscaping()
					.setDateFormat("yyyy-MM-dd hh:mm:ss").create();
			this.outputStream = new FileOutputStream(file, true);
			BufferedWriter bufferedWriter;
			bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
			gson.toJson(blob, bufferedWriter);

			System.out.println("|--> Blob saved to JSON format!");
			System.out.println("|--> JSON saved at: " + this.storagePath);
			System.out.println("---> DONE!");
			System.out.println();

			bufferedWriter.close();
		} catch (IOException e) {
			System.out.println("---> ERROR AT JSON WRITING!");
			System.out.println("---> CHECK INPUT PARAMETERS AND TRY AGAIN...");
		}

	}

	public void saveCloudBlobs(List<CloudFile> blobs) {
		try {
			System.out.println("---> SAVING MULTIPLE BLOBS IN JSON FORMAT...");
			System.out.println("|--> Blobs to save: " + blobs.size());

			outputStream = null;
			Gson gson = new GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().disableHtmlEscaping()
					.setDateFormat("yyyy-MM-dd hh:mm:ss").create();
			this.outputStream = new FileOutputStream(file, true);
			BufferedWriter bufferedWriter;
			bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

			for (CloudFile cloudFile : blobs) {
				gson.toJson(cloudFile, bufferedWriter);
			}

			System.out.println("|--> Blobs saved to JSON format!");
			System.out.println("|--> JSON path: " + this.storagePath);
			System.out.println("---> DONE!");
			System.out.println();

			bufferedWriter.close();
		} catch (IOException e) {
			System.out.println("---> ERROR AT JSON WRITING!");
			System.out.println("---> CHECK INPUT PARAMETERS AND TRY AGAIN...");
		}

	}

	public void saveGenericLog(String objectToSave) {
		BufferedWriter writer = null;
		try {
			System.out.println();
			System.out.println("---> SAVING ACTIVITY LOG IN TEXT FORMAT...");

			String timeLog = "cloud_storage_log_"
					+ new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
			File logFile = new File(this.storagePath);

			writer = new BufferedWriter(new FileWriter(logFile, true));
			writer.write("[" + timeLog + "]: " + objectToSave + "\n");
			System.out.println("|--> Activity log updated at: " + this.storagePath);
			System.out.println("---> DONE!");
			System.out.println();

		} catch (Exception e) {
			System.out.println("---> ERROR AT LOG TEXT WRITING!");
			System.out.println("---> CHECK INPUT PARAMETERS AND TRY AGAIN...");
		} finally {
			try {
				writer.close();
			} catch (Exception e) {

			}
		}
	}
	
	public void saveConsole(String objectToSave) {
		BufferedWriter writer = null;
		try {
			System.out.println();
			System.out.println("---> SAVING ACTIVITY LOG IN TEXT FORMAT...");

			String timeLog = "console_log_"
					+ new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
			File logFile = new File(this.storagePath);

			writer = new BufferedWriter(new FileWriter(logFile, true));
			writer.write("[" + timeLog + "]: " + objectToSave + "\n");
			System.out.println("|--> Console log updated at: " + this.storagePath);
			System.out.println("---> DONE!");
			System.out.println();

		} catch (Exception e) {
			System.out.println("---> ERROR AT LOG TEXT WRITING!");
			System.out.println("---> CHECK INPUT PARAMETERS AND TRY AGAIN...");
		} finally {
			try {
				writer.close();
			} catch (Exception e) {

			}
		}
	}

	public void setStoragePath(String storagePath) {
		this.storagePath = storagePath;
	}

	public String getStoragePath() {
		return this.storagePath;
	}

}

package com.sandbox;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.config.Config;
import com.language.ClientNLP;
import com.speech.Recognizer;
import com.speech.Transcription;
import com.storage.CloudStorageController;
import com.storage.DataStorage;

public class CRIS {
	private boolean isCloudInitialized = false;
	private boolean isSpeechInitialized = false;
	private boolean isCrisRunning = false;

	private CloudStorageController c;
	private Recognizer speechRecognizer;
	private ClientNLP nlpClient = new ClientNLP();

	private DataStorage jsonCloudStorage;
	private DataStorage jsonTranscriptionStorage;
	private DataStorage localLogStorage;

	// private String jsonFilePath;

	private String selectedBucketName;
	private String selectedContentType;
	private String selectedDirectoryName;
	private String selectedRecordingName;
	private String selectedURI;
	private String selectedTranscription;

	private List<String> batchRecordingsURI = null;
	private List<String> recordingsURI = null;
	private HashMap<Integer, String> menuPotions = new HashMap<>();
	ArrayList<String> generatedPhrases;

	Scanner sc = new Scanner(System.in);

	public void displaymenu() throws InterruptedException {
		if (isCrisRunning == false) {
			Thread.sleep(500);
			System.out.println("+----------------------------------------------------------------------------+");
			System.out.println("| C.R.I.S. (Consultant for Recommended Interactions Software)!");
			System.out.println("+----------------------------------------------------------------------------+");
		}
		if (isCloudInitialized == false) {
			Thread.sleep(1000);
			System.out.println("+----------------------------------------------------------------------------+");
			System.out.println("| WARNING: THE CLOUD SERVICE HAS NOT BEEN STARTED!");
			System.out.println("+----------------------------------------------------------------------------+");
		}
		if (isSpeechInitialized == false) {
			Thread.sleep(1000);
			System.out.println("+----------------------------------------------------------------------------+");
			System.out.println("| WARNING: THE SPECCH RECOGNITION SERVICE HAS NOT BEEN INITIALIZED!");
			System.out.println("+----------------------------------------------------------------------------+");
		}
		Thread.sleep(1000);
		System.out.println("+----------------------------------------------------------------------------+");
		System.out.println("| MAIN MENU");
		System.out.println("+----------------------------------------------------------------------------+");
		Thread.sleep(500);

		buildMenu();
		for (Map.Entry<Integer, String> entry : menuPotions.entrySet()) {
			int key = entry.getKey();
			String value = entry.getValue();
			System.out.println("OPTION " + key + "\t|-> " + value);
			Thread.sleep(100);
		}
		System.out.println("+----------------------------------------------------------------------------+");

		isCrisRunning = true;
	}

	public void addMenuOption(int position, String content) {
		menuPotions.put(position, content);
	}

	public void buildMenu() {
		addMenuOption(1, "Initialize the Cloud Storage Client");
		addMenuOption(2, "Initialize the Speech Recognition Service");
		addMenuOption(3, "Display current Cloud Storage active selections");
		addMenuOption(4, "Display current transcript selection");
		addMenuOption(5, "Change current trasncript selection");
		addMenuOption(6, "Change current Cloud Storage active recording");
		addMenuOption(7, "Generate recording URI from current active selections");
		addMenuOption(8, "Run speech transcription on selected recording");
		addMenuOption(9, "Run speech transcription on a conversation (hard-coded)");
		addMenuOption(10, "Run NLP on selected trasncript");
		addMenuOption(11, "Run NLP on saved conversation transcript");
		addMenuOption(12, "Console print all recording files for current bucket");
		addMenuOption(13, "Console print all recording files for current directory");
		addMenuOption(14, "Console print URI path for current active recording");
		addMenuOption(15, "Console print saved transcripts");
		addMenuOption(16, "Shut Down CRIS!");
	}

	public int getMenuChoice() throws InterruptedException, IOException {

		int optionSelected = 0;
		Thread.sleep(1000);
		System.out.println();
		System.out.println("+----------------------------------------------------------------------------+");
		System.out.print("| Please type in your selected option (1-16) ---> ");
		if (sc.hasNextInt()) {
			optionSelected = sc.nextInt();
			return optionSelected;
		}
		return optionSelected;
	}

	public int getConfirmation() {
		System.out.println("+----------------------------------------------------------------------------+");
		String input = "";
		System.out.print("| Are you sure about this? (YES/NO): ");
		if (sc.hasNext()) {
			input = sc.next();
			if (input.toUpperCase().equals("YES")) {
				return 1;
			}

			if (input.toUpperCase().equals("NO")) {
				return 0;
			}
		}
		System.out.println("+----------------------------------------------------------------------------+");
		System.out.println("| ERROR: Invalid input detected!");
		System.out.println("| Try again with correct values (YES/NO)...");
		System.out.println("+----------------------------------------------------------------------------+");
		System.out.println();
		getConfirmation();
		return -1;
	}

	public void setCloudSelections() throws InterruptedException {

		displayStorageSelection(this.selectedBucketName, this.selectedDirectoryName, this.selectedContentType,
				this.selectedRecordingName, this.selectedURI);
		System.out.println("+----------------------------------------------------------------------------+");
		sc.nextLine();
		System.out.print("| Select a recording (ex: Voice XXX.flac): ");
		this.selectedRecordingName = sc.nextLine();
		System.out.println("+----------------------------------------------------------------------------+");
		displayStorageSelection(this.selectedBucketName, this.selectedDirectoryName, this.selectedContentType,
				this.selectedRecordingName, this.selectedURI);

		System.out.println("+----------------------------------------------------------------------------+");
		System.out.println();

	}

	public void setCloudBucket() throws InterruptedException {

		displayStorageSelection(this.selectedBucketName, this.selectedDirectoryName, this.selectedContentType,
				this.selectedRecordingName, this.selectedURI);
		System.out.println("+----------------------------------------------------------------------------+");
		System.out.print("| Select a bucket: ");
		this.selectedBucketName = sc.nextLine();
		System.out.println("+----------------------------------------------------------------------------+");
		displayStorageSelection(this.selectedBucketName, this.selectedDirectoryName, this.selectedContentType,
				this.selectedRecordingName, this.selectedURI);

		System.out.println("+----------------------------------------------------------------------------+");
		System.out.println();

	}

	public void setCloudDirectory() throws InterruptedException {

		displayStorageSelection(this.selectedBucketName, this.selectedDirectoryName, this.selectedContentType,
				this.selectedRecordingName, this.selectedURI);
		System.out.println("+----------------------------------------------------------------------------+");
		System.out.print("| Select a directory (ex: Speaker X/16/): ");
		this.selectedDirectoryName = sc.nextLine();
		System.out.println("+----------------------------------------------------------------------------+");
		displayStorageSelection(this.selectedBucketName, this.selectedDirectoryName, this.selectedContentType,
				this.selectedRecordingName, this.selectedURI);

		System.out.println("+----------------------------------------------------------------------------+");
		System.out.println();

	}

	public void setCloudContentType() throws InterruptedException {

		displayStorageSelection(this.selectedBucketName, this.selectedDirectoryName, this.selectedContentType,
				this.selectedRecordingName, this.selectedURI);
		System.out.println("+----------------------------------------------------------------------------+");
		System.out.print("| Select your content type (ex: text/plain, audio/flac): ");
		this.selectedContentType = sc.nextLine();
		System.out.println("+----------------------------------------------------------------------------+");
		displayStorageSelection(this.selectedBucketName, this.selectedDirectoryName, this.selectedContentType,
				this.selectedRecordingName, this.selectedURI);

		System.out.println("+----------------------------------------------------------------------------+");
		System.out.println();

	}

	public void setCloudFile() throws InterruptedException {

		displayStorageSelection(this.selectedBucketName, this.selectedDirectoryName, this.selectedContentType,
				this.selectedRecordingName, this.selectedURI);
		System.out.println("+----------------------------------------------------------------------------+");
		System.out.print("| Select a recording (ex: Voice 260.flac): ");
		this.selectedRecordingName = sc.nextLine();
		System.out.println("+----------------------------------------------------------------------------+");
		displayStorageSelection(this.selectedBucketName, this.selectedDirectoryName, this.selectedContentType,
				this.selectedRecordingName, this.selectedURI);
		System.out.println("+----------------------------------------------------------------------------+");
		System.out.println();

	}

	public void runCRIS() throws InterruptedException, IOException {
		localLogStorage = new DataStorage(Config.LOG_FILE_PATH);
		jsonCloudStorage = new DataStorage(Config.CLOUD_STORAGE_LOG_FILE_PATH);
		jsonTranscriptionStorage = new DataStorage(Config.TRANSCRIPTION_LOG_FILE_PATH);
		displaymenu();
		int choice = getMenuChoice();
		while (true) {
			switch (choice) {
			case 1:
				displayOptionSelected(choice);
				if (isCloudInitialized == true) {
					Thread.sleep(500);
					System.out
							.println("+----------------------------------------------------------------------------+");
					System.out.println("| You have already initialized the Cloud Storage Client!");
					System.out
							.println("+----------------------------------------------------------------------------+");
					System.out.println();
					break;
				} else {
					initializeStorageClient();
					isCloudInitialized = true;
					Thread.sleep(500);
					System.out
							.println("+----------------------------------------------------------------------------+");
					System.out.println("| You have initialized the Cloud Storage Client!");
					System.out
							.println("+----------------------------------------------------------------------------+");
					System.out.println();
					break;
				}
			case 2:
				displayOptionSelected(choice);
				if (isSpeechInitialized == true) {
					Thread.sleep(500);
					System.out
							.println("+----------------------------------------------------------------------------+");
					System.out.println("| You have already initialized the Speech Recognition Service!");
					System.out
							.println("+----------------------------------------------------------------------------+");
					System.out.println();
					break;
				} else {
					initializeSpeechClient();

					isSpeechInitialized = true;
					Thread.sleep(500);
					System.out
							.println("+----------------------------------------------------------------------------+");
					System.out.println("| You have initialized the Speech Recognition Service!");
					System.out
							.println("+----------------------------------------------------------------------------+");
					System.out.println();
					break;
				}
			case 3:
				displayOptionSelected(choice);
				displayStorageSelection(selectedBucketName, selectedDirectoryName, selectedContentType,
						selectedRecordingName, selectedURI);
				break;
			case 4:
				displayOptionSelected(choice);
				printSelectedTranscription();
				break;
			case 5:
				displayOptionSelected(choice);
				selectPhrase();
				break;
			case 6:
				displayOptionSelected(choice);
				setCloudSelections();
				break;
			case 7:
				displayOptionSelected(choice);
				buildrecordingURI(selectedBucketName, selectedDirectoryName, selectedContentType,
						selectedRecordingName);
				displayGeneratedURI();
				break;
			case 8:
				displayOptionSelected(choice);
				transcribeRecording(selectedURI);
				break;
			case 9:
				displayOptionSelected(choice);
				transcribeConversation();
				break;
			case 10:
				displayOptionSelected(choice);
				printShortPhraseNLP();
				break;
			case 11:
				displayOptionSelected(choice);
				printConversationOutput();
				break;
			case 12:
				displayOptionSelected(choice);
				printAllRecordingsURI(selectedBucketName, selectedContentType);
				break;
			case 13:
				displayOptionSelected(choice);
				printBatchRecordingsURI(selectedBucketName, selectedDirectoryName, selectedContentType);
				break;
			case 14:
				displayOptionSelected(choice);
				printSelectedURI();
				break;
			case 15:
				displayOptionSelected(choice);
				printAllPhrases();
				break;
			case 16:
				displayOptionSelected(choice);
				shutdownCRIS();
				break;
			default:
				Thread.sleep(500);
				System.out.println("+----------------------------------------------------------------------------+");
				System.out.println("| Please type in a valid menu option! Only nubers 0 through 16 are allowed!");
				System.out.println("+----------------------------------------------------------------------------+");
				System.out.println();
			}
			displaymenu();
			choice = getMenuChoice();
		}
	}

	public void shutdownCRIS() throws InterruptedException, IOException {
		int confirmation;

		confirmation = getConfirmation();

		if (confirmation == 1) {
			System.out.println("+----------------------------------------------------------------------------+");
			System.out.println("| CRIS is shutting down...");
			System.out.println("+----------------------------------------------------------------------------+");
			System.out.println();
			System.exit(0);
		} else {
			runCRIS();
		}
	}

	public void displayOptionSelected(int choice) throws InterruptedException {
		Thread.sleep(500);
		System.out.println("+----------------------------------------------------------------------------+");
		System.out.println("| You have selected option: " + choice);
		System.out.println("+----------------------------------------------------------------------------+");
		System.out.println();
	}

	public void displayStorageSelection(String bucketName, String directoryName, String contentType,
			String recordingName, String uriPath) throws InterruptedException {
		Thread.sleep(1000);
		System.out.println("+----------------------------------------------------------------------------+");
		System.out.println("| You have selected bucket: " + bucketName);
		System.out.println("| You have selected directory: " + directoryName);
		System.out.println("| You have selected content type: " + contentType);
		System.out.println("| You have selected recording file: " + recordingName);
		System.out.println("| You have selected recording URI: " + uriPath);
		System.out.println("+----------------------------------------------------------------------------+");
		System.out.println();
	}

	public void displayGeneratedURI() {
		System.out.println("+----------------------------------------------------------------------------+");
		System.out.println("| Your active URI: " + selectedURI);
		System.out.println("+----------------------------------------------------------------------------+");
		System.out.println();
	}

	public void initializeStorageClient() {
		c = new CloudStorageController();
		c.setLogStorageLocation(localLogStorage);
	}

	public void initializeSpeechClient() {
		try {
			this.speechRecognizer = new Recognizer(Config.SPEECH_RECOGNIZER_LANGUAGE_CODE,
					Config.SPEECH_RECOGNIZER_AUDIO_SAMPLE_RATE, Config.SPEECH_RECOGNIZER_AUDIO_ENCODING_FLAC);
			speechRecognizer.setLogStorageLocation(localLogStorage);
			this.selectedBucketName = Config.CLOUD_DEFAULT_BUCKET_NAME;
			this.selectedDirectoryName = Config.CLOUD_SHORT_RECORDINGS_DIRECTORY_NAME;
			this.selectedContentType = Config.CLOUD_AUDIO_FLAC_CONTENT_FLAG;
			System.out.println("+----------------------------------------------------------------------------+");
			System.out.println("| Initailizing the Speech Recognition Client...");
			System.out.println("| Your configuration parameters:");
			System.out.println("| Language code: " + Config.SPEECH_RECOGNIZER_LANGUAGE_CODE);
			System.out.println("| Audio sample rate: " + Config.SPEECH_RECOGNIZER_AUDIO_SAMPLE_RATE);
			System.out.println("| Audio encoding: " + Config.SPEECH_RECOGNIZER_AUDIO_ENCODING_FLAC);
			System.out.println("| DONE!");
			System.out.println("+----------------------------------------------------------------------------+");
			System.out.println();
		} catch (IOException e) {
			System.out.println("+----------------------------------------------------------------------------+");
			System.out.println("| ERROR: Something went wrong when initailizing the Speech Recognition Client");
			System.out.println("| Check your configuration parameters and try again...");
			System.out.println("| Language code: " + Config.SPEECH_RECOGNIZER_LANGUAGE_CODE);
			System.out.println("| Audio sample rate: " + Config.SPEECH_RECOGNIZER_AUDIO_SAMPLE_RATE);
			System.out.println("| Audio encoding: " + Config.SPEECH_RECOGNIZER_AUDIO_ENCODING_FLAC);
			System.out.println("+----------------------------------------------------------------------------+");
			System.out.println();
			e.printStackTrace();
		}
	}

	public void printSelectedTranscription() {
		System.out.println("+----------------------------------------------------------------------------+");
		System.out.println("Trasncript: " + this.selectedTranscription);
		System.out.println("+----------------------------------------------------------------------------+");
		System.out.println();
	}

	public void printAllRecordingsURI(String bucketName, String contentType) {
		c.listAll(bucketName, contentType);
	}

	public void printBatchRecordingsURI(String bucketName, String currentDirectory, String contentType) {
		c.listRecordingsFromDirectory(bucketName, currentDirectory, contentType);
	}

	public void printSelectedURI() {
		System.out.println("+----------------------------------------------------------------------------+");
		System.out.println("| You have selected the following URI: " + selectedURI);
		System.out.println("+----------------------------------------------------------------------------+");
		System.out.println();
	}

	public void buildrecordingURI(String bucketName, String directory, String contentType, String recordingName)
			throws InterruptedException, IOException {
		displayStorageSelection(bucketName, directory, contentType, recordingName, this.selectedURI);
		this.selectedURI = c.generateSingleRecordingURI(bucketName, directory, contentType, recordingName);
		Thread.sleep(500);
		System.out.println("+----------------------------------------------------------------------------+");
		System.out.println("| You have generated the following URI: " + selectedURI);
		System.out.println("| This is now you active URI selection!");
		System.out.println("+----------------------------------------------------------------------------+");
		System.out.println();
	}

	public void buildAllRecordingsURI() {
		this.recordingsURI = c.generateBatchRecordingsURI(selectedBucketName, selectedDirectoryName,
				selectedContentType);
	}

	public void selectPhrase() throws FileNotFoundException, IOException {
		this.generatedPhrases = nlpClient.generateShortPhrasesArray();
		System.out.println("+----------------------------------------------------------------------------+");
		System.out.print("| Please type in your selected option (0 -> 84) ---> ");
		int optionSelected = 0;
		if (sc.hasNextInt()) {
			optionSelected = sc.nextInt();
			this.selectedTranscription = this.generatedPhrases.get(optionSelected);
			System.out.println();
			System.out.println("| Selected: " + this.generatedPhrases.get(optionSelected));
			System.out.println("| This is now your active selection! ");
			System.out.println("+----------------------------------------------------------------------------+");
			System.out.println();
		}
	}

	public void printShortPhraseNLP() throws IOException {
		if (this.selectedTranscription == null) {
			printAllPhrases();
			selectPhrase();
			nlpClient.analyzeShortPhrase(this.selectedTranscription);
		}
		nlpClient.analyzeShortPhrase(this.selectedTranscription);
	}

	public void printAllPhrases() throws FileNotFoundException, IOException {
		this.generatedPhrases = nlpClient.generateShortPhrasesArray();
		System.out.println("+----------------------------------------------------------------------------+");
		System.out.println("| Listing all short phrases...");
		int index = 0;
		for (String phrase : generatedPhrases) {
			System.out.println(index + "-->\t" + phrase);
			index++;
		}
		System.out.println("+----------------------------------------------------------------------------+");
		System.out.println();
	}

	public void printConversationOutput() throws IOException {
		int optionSelected = 0;
		System.out.println("+----------------------------------------------------------------------------+");
		System.out.print("| Please type in your selected option (1 / 2 / 3) ---> ");
		if (sc.hasNextInt()) {
			optionSelected = sc.nextInt();
			System.out.println("| Selected: " + optionSelected);
			System.out.println("| NLP output for conversation " + optionSelected);
			nlpClient.analyzeConversation(optionSelected);
			System.out.println("+----------------------------------------------------------------------------+");
			System.out.println();
		}

	}

	public void transcribeConversation() {
		String uri = Config.CLOUD_LONG_RECORDINGS_FILE_PATH;
		System.out.print("| Please type in your selected audio conversation file (Voice 574 -> 599.flac) ---> ");
		String selectedFile = sc.nextLine();
		if (sc.hasNextLine()) {
			uri = uri.concat(selectedFile);
			System.out.println(uri);
			speechRecognizer.setURI(uri);
			try {
				speechRecognizer.transcribeDiarization(uri);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void transcribeRecording(String selectedURI) throws InterruptedException, IOException {
		System.out.println("+----------------------------------------------------------------------------+");
		System.out.println("| Running speech transcription...");
		speechRecognizer.setURI(selectedURI);
		Transcription activeTranscription = new Transcription();
		speechRecognizer.speechRecognizeGCS();
		activeTranscription = speechRecognizer.buildTranscription();
		System.out.println("| Trasncription text: " + activeTranscription.getTranscriptionText());
		jsonTranscriptionStorage.saveTranscription(activeTranscription);
		System.out.println("| Transcription succesfully exported to JSON!");
		System.out.println("| JSON path: " + localLogStorage.getStoragePath());
		System.out.println("+----------------------------------------------------------------------------+");
		System.out.println();
	}

	public void transcribeAll(List<String> recordingsURI) throws InterruptedException, IOException {
		System.out.println("+----------------------------------------------------------------------------+");
		System.out.println("| Running speech transcription...");
		System.out.println("| Number of recordings found: " + recordingsURI.size());

		System.out.println();
		for (String activeURI : recordingsURI) {
			speechRecognizer.setURI(activeURI);
			Transcription activeTranscription = new Transcription();
			speechRecognizer.speechRecognizeGCS();
			activeTranscription = speechRecognizer.buildTranscription();
			jsonTranscriptionStorage.saveTranscription(activeTranscription);
		}

		System.out.println("| Transcriptions succesfully exported to JSON!");
		System.out.println("| JSON path: " + localLogStorage.getStoragePath());
		System.out.println("+----------------------------------------------------------------------------+");
		System.out.println();
	}

	public void exportCloudInfoToJSON(String bucketName, String contentType, DataStorage dataStorage)
			throws InterruptedException {
		displayStorageSelection(selectedBucketName, selectedDirectoryName, selectedContentType, selectedRecordingName,
				selectedURI);
		c.saveCloudToJSON(bucketName, contentType, dataStorage);
	}
}

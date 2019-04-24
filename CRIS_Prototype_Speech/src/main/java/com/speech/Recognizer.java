package com.speech;

import com.google.api.gax.longrunning.OperationFuture;
import com.google.cloud.speech.v1p1beta1.LongRunningRecognizeMetadata;
import com.google.cloud.speech.v1p1beta1.LongRunningRecognizeResponse;
import com.google.cloud.speech.v1p1beta1.RecognitionAudio;
import com.google.cloud.speech.v1p1beta1.RecognitionConfig;
import com.google.cloud.speech.v1p1beta1.RecognitionConfig.AudioEncoding;
import com.google.cloud.speech.v1p1beta1.RecognitionMetadata;
import com.google.cloud.speech.v1p1beta1.RecognitionMetadata.InteractionType;
import com.google.cloud.speech.v1p1beta1.RecognitionMetadata.MicrophoneDistance;
import com.google.cloud.speech.v1p1beta1.RecognitionMetadata.RecordingDeviceType;
import com.storage.DataStorage;
import com.google.cloud.speech.v1p1beta1.RecognizeResponse;
import com.google.cloud.speech.v1p1beta1.SpeechClient;
import com.google.cloud.speech.v1p1beta1.SpeechContext;
import com.google.cloud.speech.v1p1beta1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1p1beta1.SpeechRecognitionResult;
//--Imports the Google Cloud library

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class Recognizer {

	private String gcsURI;
	private String audioEncoding;
	private String languageCode;
	private int audioSampleRate;

	private RecognitionMetadata metadata;
	private SpeechContext hints;
	private RecognitionConfig config;
	private RecognitionAudio audio;
	private SpeechClient speech;
	private RecognizeResponse shortResponse;
	private ArrayList<String> speechPhraseHints = new ArrayList<>();

	private OperationFuture<LongRunningRecognizeResponse, LongRunningRecognizeMetadata> longResponse;
	private List<SpeechRecognitionResult> textResult;

	private Transcription transcriptionResult;
	private DataStorage logStorage;

	public Recognizer(String languageCode, int audioSampleRate, String audioEncoding) throws IOException {
		this.languageCode = languageCode;
		this.audioSampleRate = audioSampleRate;
		this.audioEncoding = audioEncoding;
		try (SpeechClient createdSpeech = SpeechClient.create()) {
			this.speech = createdSpeech;
		}
		this.metadata = RecognitionMetadata.newBuilder().setInteractionType(InteractionType.DICTATION)
				.setMicrophoneDistance(MicrophoneDistance.NEARFIELD)
				.setRecordingDeviceType(RecordingDeviceType.SMARTPHONE).build();
		addPhraseHints();
		this.hints = SpeechContext.newBuilder().addAllPhrases(speechPhraseHints).build();
	}

	public void addPhraseHints() {
		String hintsFile = "src\\main\\resources\\PhraseHints";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";

		try {

			br = new BufferedReader(new FileReader(hintsFile));
			while ((line = br.readLine()) != null) {
				String[] lineHints = line.split(cvsSplitBy);

				for (String hint : lineHints) {
					this.speechPhraseHints.add(hint);
				}

			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void setLogStorageLocation(DataStorage newDataStorage) {
		this.logStorage = newDataStorage;
	}

	public void setURI(String gcsURI) {
		this.gcsURI = gcsURI;
	}

	public Transcription buildTranscription() {
		try {
			for (SpeechRecognitionResult recognizedWord : textResult) {
				Timestamp time = new Timestamp(System.currentTimeMillis());
				SpeechRecognitionAlternative bestAlternative = recognizedWord.getAlternativesList().get(0);
				this.transcriptionResult = new Transcription(this.gcsURI, this.audioEncoding, this.audioSampleRate,
						bestAlternative.getTranscript());
				System.out.println("---> SPEECH RECOGNIZER END");
				logStorage.saveGenericLog("TRANSCRIPTION FOR RECORDING: " + this.gcsURI + " WAS GENERATED AT: " + time);
			}
			try {
				if (this.speech.awaitTermination(5, TimeUnit.SECONDS) == true) {
					this.speech.shutdown();
					this.speech.shutdownNow();
				}
			} catch (InterruptedException e) {
				System.out.println("---> ERROR: SPEECH CLIENT FAILED TO SHUTDOWN CORRECTLY!");
			}
		} catch (NullPointerException e) {
			System.out.println("---> ERROR: SPEECH RECOGNITION ERROR!! CHECK INPUT PARAMETERS AND TRY AGAIN...");
		}

		return transcriptionResult;
	}

	public Transcription buildDummyTranscript() {
		Timestamp time = new Timestamp(System.currentTimeMillis());
		System.out.println("---> Building dummy trascript for " + this.gcsURI + "...");
		this.transcriptionResult = new Transcription(this.gcsURI, this.audioEncoding, this.audioSampleRate,
				"I'm a dummy transcription, dummy! This transcription was generated at: " + time);
		System.out.println("|--> Transcription: " + transcriptionResult.getTranscriptionText());
		System.out.println("|--> Created at: " + transcriptionResult.getTranscriptionTimestamp());
		System.out.println("---> DONE!");
		System.out.println();
		logStorage.saveGenericLog("DUMMY TRANSCRIPTION FOR RECORDING: " + this.gcsURI + " WAS GENERATED AT: " + time);
		return transcriptionResult;
	}

	public void dummySpeechRecognizer() {
		Timestamp time = new Timestamp(System.currentTimeMillis());
		printRecognizerDetails(this.gcsURI, this.audioSampleRate, this.audioEncoding, false);
		System.out.println("---> DUMMY SPEECH RECOGNIZER END");
		logStorage.saveGenericLog("DUMMY RECOGNIZER FOR RECORDING: " + this.gcsURI + " WAS STARTED AT: " + time);

	}

	public Transcription getTranscriptionResult() {
		return transcriptionResult;
	}

	public void speechRecognizeGCS() throws InterruptedException, IOException {
		Timestamp time = new Timestamp(System.currentTimeMillis());
		printRecognizerDetails(this.gcsURI, this.audioSampleRate, this.audioEncoding, false);
		try (SpeechClient createdSpeechClient = SpeechClient.create()) {
			this.speech = createdSpeechClient;
			this.config = RecognitionConfig.newBuilder().setEncoding(AudioEncoding.FLAC)
					.setLanguageCode(this.languageCode).setSampleRateHertz(audioSampleRate)
					.setEnableAutomaticPunctuation(true).setUseEnhanced(true).setMetadata(metadata)
					.addSpeechContexts(hints).build();

			this.audio = RecognitionAudio.newBuilder().setUri(this.gcsURI).build();

			this.shortResponse = speech.recognize(this.config, this.audio);
			this.textResult = shortResponse.getResultsList();
			logStorage.saveGenericLog("RECOGNIZER FOR RECORDING: " + this.gcsURI + " WAS STARTED AT: " + time);

			try {
				if (this.speech.awaitTermination(5, TimeUnit.SECONDS) == true) {
					this.speech.shutdown();
					this.speech.shutdownNow();
				}
			} catch (InterruptedException e) {
				System.out.println("---> ERROR: SPEECH CLIENT FAILED TO SHUTDOWN CORRECTLY!");
			}

		} catch (com.google.api.gax.rpc.InvalidArgumentException e) {
			System.out.println(
					"---> ERROR: SHORT FILE RECOGNIZER FAILED TO RETURN A RESULT! RUNNING LONG FILE RECOGNIZER...");
			speechRecognizeLongGCS();

		}

	}

	public void speechRecognizeLongGCS() throws IOException, InterruptedException {

		printRecognizerDetails(this.gcsURI, this.audioSampleRate, this.audioEncoding, true);

		try {
			Timestamp time = new Timestamp(System.currentTimeMillis());
			this.config = RecognitionConfig.newBuilder().setEncoding(AudioEncoding.FLAC).setLanguageCode("en-US")
					.setSampleRateHertz(audioSampleRate).setEnableAutomaticPunctuation(true).setUseEnhanced(true)
					.setMetadata(metadata).build();

			this.audio = RecognitionAudio.newBuilder().setUri(this.gcsURI).build();

			this.longResponse = this.speech.longRunningRecognizeAsync(this.config, this.audio);

			while (!this.longResponse.isDone()) {
				System.out.println("|--> Waiting for long audio recognizer...");
				Thread.sleep(2000);
			}

			this.textResult = longResponse.get().getResultsList();
			logStorage.saveGenericLog(
					"RECOGNIZER (LONG AUDIO) FOR RECORDING: " + this.gcsURI + " WAS STARTED AT: " + time);

			try {
				if (this.speech.awaitTermination(5, TimeUnit.SECONDS) == true) {
					this.speech.shutdown();
					this.speech.shutdownNow();
				}
			} catch (InterruptedException e) {
				System.out.println("---> ERROR: SPEECH CLIENT FAILED TO SHUTDOWN CORRECTLY!");
			}

		} catch (io.grpc.StatusRuntimeException e) {
			System.out.println("---> ERROR: RECOGNIZER FAILED TO TRANSCRIBE AUDIO FROM THIS GCS URI!");
			this.speech.shutdown();
			this.speech.shutdownNow();
		} catch (ExecutionException e) {
			System.out.println("---> ERROR: RECOGNIZER FAILED TO TRANSCRIBE AUDIO FROM THIS GCS URI!");
			this.speech.shutdown();
			this.speech.shutdownNow();
		}
	}

	public void speechRecognizeConversation() throws IOException, InterruptedException, ExecutionException {
		printRecognizerDetails(this.gcsURI, this.audioSampleRate, this.audioEncoding, true);

		Timestamp time = new Timestamp(System.currentTimeMillis());
		this.config = RecognitionConfig.newBuilder().setEncoding(AudioEncoding.FLAC).setLanguageCode("en-US")
				.setDiarizationSpeakerCount(2).setEnableSpeakerDiarization(true).setAudioChannelCount(2)
				.setSampleRateHertz(this.audioSampleRate).setEnableAutomaticPunctuation(true).setUseEnhanced(true)
				.setMetadata(metadata).build();

		this.audio = RecognitionAudio.newBuilder().setUri(this.gcsURI).build();

		this.longResponse = this.speech.longRunningRecognizeAsync(this.config, this.audio);

		while (!this.longResponse.isDone()) {
			System.out.println("|--> Waiting for long audio recognizer...");
			Thread.sleep(2000);
		}

		this.textResult = longResponse.get().getResultsList();
		logStorage.saveGenericLog("RECOGNIZER (LONG AUDIO) FOR RECORDING: " + this.gcsURI + " WAS STARTED AT: " + time);

		for (SpeechRecognitionResult result : textResult) {
			// There can be several alternative transcripts for a given chunk of speech.
			// Just
			// use the first (most likely) one here.
			SpeechRecognitionAlternative alternative = result.getAlternatives(0);
			System.out.format("Transcript : %s\n", alternative.getTranscript());
			// The words array contains the entire transcript up until that point.
			// Referencing the last spoken word to get the associated Speaker tag
			System.out.format("Speaker Tag %s: %s\n",
					alternative.getWords((alternative.getWordsCount() - 1)).getSpeakerTag(),
					alternative.getTranscript());

		}

		try {
			if (this.speech.awaitTermination(5, TimeUnit.SECONDS) == true) {
				this.speech.shutdown();
				this.speech.shutdownNow();
			}
		} catch (InterruptedException e) {
			System.out.println("---> ERROR: SPEECH CLIENT FAILED TO SHUTDOWN CORRECTLY!");
		}

	}

	public void transcribeDiarization(String gcsURI) throws Exception {

		try (SpeechClient speechClient = SpeechClient.create()) {

			RecognitionMetadata metadata = RecognitionMetadata.newBuilder()
					.setInteractionType(InteractionType.DISCUSSION).setMicrophoneDistance(MicrophoneDistance.NEARFIELD)
					.setRecordingDeviceType(RecordingDeviceType.SMARTPHONE).build();

			RecognitionAudio recognitionAudio = RecognitionAudio.newBuilder().setUri(gcsURI).build();

			RecognitionConfig config = RecognitionConfig.newBuilder().setEncoding(AudioEncoding.FLAC)
					.setLanguageCode("en-US").setSampleRateHertz(16000).setEnableSpeakerDiarization(true)
					.setAudioChannelCount(2).setEnableAutomaticPunctuation(true).setUseEnhanced(true)
					.setMetadata(metadata).setDiarizationSpeakerCount(2).build();

			OperationFuture<LongRunningRecognizeResponse, LongRunningRecognizeMetadata> response = speechClient
					.longRunningRecognizeAsync(config, recognitionAudio);
			while (!response.isDone()) {
				System.out.println("Waiting for response...");
				Thread.sleep(10000);
			}

			List<SpeechRecognitionResult> results = response.get().getResultsList();
			System.out.println("Printing audio transcript for " + gcsURI + "...");
			for (SpeechRecognitionResult result : results) {
				SpeechRecognitionAlternative alternative = result.getAlternatives(0);
				System.out.print(alternative.getTranscript());
			}
		}
	}

	private void printRecognizerDetails(String gcsURI, int audioSampleRate, String audioEncoding, boolean isLongAudio) {
		gcsURI = this.gcsURI;

		if (isLongAudio == false) {
			System.out.println("---> SPEECH RECOGNITION START");
			System.out.println("|--> Recognizer: " + "SHORT LENGTH AUDIO");
			System.out.println("|--> GCS URI: " + this.gcsURI);
			System.out.println("|--> Audio Encoding: " + audioEncoding);
			System.out.println("|--> Sample Rate: " + audioSampleRate + " Hz");
		} else {
			System.out.println("---> SPEECH RECOGNITION START");
			System.out.println("|--> Recognizer: " + "LONG LENGTH AUDIO");
			System.out.println("|--> GCS URI: " + this.gcsURI);
			System.out.println("|--> Audio Encoding: " + audioEncoding);
			System.out.println("|--> Sample Rate: " + audioSampleRate + " Hz");
		}
	}

}

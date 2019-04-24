package com.sandbox;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.google.api.gax.longrunning.OperationFuture;
import com.google.cloud.speech.v1p1beta1.LongRunningRecognizeMetadata;
import com.google.cloud.speech.v1p1beta1.LongRunningRecognizeResponse;
import com.google.cloud.speech.v1p1beta1.RecognitionAudio;
import com.google.cloud.speech.v1p1beta1.RecognitionConfig;
import com.google.cloud.speech.v1p1beta1.RecognitionMetadata;
import com.google.cloud.speech.v1p1beta1.RecognitionConfig.AudioEncoding;
import com.google.cloud.speech.v1p1beta1.RecognitionMetadata.InteractionType;
import com.google.cloud.speech.v1p1beta1.RecognitionMetadata.MicrophoneDistance;
import com.google.cloud.speech.v1p1beta1.RecognitionMetadata.RecordingDeviceType;
import com.google.cloud.speech.v1p1beta1.RecognizeResponse;
import com.google.cloud.speech.v1p1beta1.SpeechClient;
import com.google.cloud.speech.v1p1beta1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1p1beta1.SpeechRecognitionResult;
import com.speech.Recognizer;
import com.speech.Transcription;
import com.storage.DataStorage;

public class TestSpeech {

	public static void main(String[] args) throws Exception {
		
		//574-599
		String gcsURI1 = "gs://cris-storage-bucket/long-audio-recordings/Voice 580.flac";
		transcribeDiarization(gcsURI1);
	}

	public static void transcribeDiarization(String gcsURI) throws Exception {

		try (SpeechClient speechClient = SpeechClient.create()) {
//			RecognitionMetadata metadata = RecognitionMetadata.newBuilder()
//					.setInteractionType(InteractionType.DISCUSSION).setMicrophoneDistance(MicrophoneDistance.NEARFIELD)
//					.setRecordingDeviceType(RecordingDeviceType.SMARTPHONE).build();

			RecognitionAudio recognitionAudio = RecognitionAudio.newBuilder().setUri(gcsURI).build();

			RecognitionConfig config = RecognitionConfig.newBuilder().setEncoding(AudioEncoding.FLAC)
					.setLanguageCode("en-US").setSampleRateHertz(16000).setEnableSpeakerDiarization(true)
					.setAudioChannelCount(2).setEnableAutomaticPunctuation(true).setUseEnhanced(true)
					.setDiarizationSpeakerCount(2).build();

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

}

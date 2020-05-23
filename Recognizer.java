package com.speech;

import java.util.ArrayList;
import com.config.*;

import com.google.api.gax.rpc.ClientStream;
import com.google.api.gax.rpc.ResponseObserver;
import com.google.api.gax.rpc.StreamController;

import com.google.cloud.speech.v1p1beta1.RecognitionConfig;
import com.google.cloud.speech.v1p1beta1.RecognitionMetadata;
import com.google.cloud.speech.v1p1beta1.RecognitionMetadata.InteractionType;
import com.google.cloud.speech.v1p1beta1.RecognitionMetadata.MicrophoneDistance;
import com.google.cloud.speech.v1p1beta1.RecognitionMetadata.OriginalMediaType;
import com.google.cloud.speech.v1p1beta1.RecognitionMetadata.RecordingDeviceType;
import com.google.cloud.speech.v1p1beta1.SpeechClient;
import com.google.cloud.speech.v1p1beta1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1p1beta1.StreamingRecognitionConfig;
import com.google.cloud.speech.v1p1beta1.StreamingRecognitionResult;
import com.google.cloud.speech.v1p1beta1.StreamingRecognizeRequest;
import com.google.cloud.speech.v1p1beta1.StreamingRecognizeResponse;
import com.google.protobuf.ByteString;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.TargetDataLine;

public class Recognizer {

	RecognitionConfig recognitionConfig;
	StreamingRecognitionConfig streamingRecognitionConfig;
	ArrayList<StreamingRecognizeResponse> responses = new ArrayList<>();
	public String responseTranscript;

	public Recognizer() {
		RecognitionMetadata metadata = RecognitionMetadata.newBuilder().setInteractionType(InteractionType.VOICE_SEARCH)
				.setMicrophoneDistance(MicrophoneDistance.NEARFIELD).setRecordingDeviceType(RecordingDeviceType.PC)
				.setOriginalMediaType(OriginalMediaType.AUDIO).build();

		recognitionConfig = RecognitionConfig.newBuilder().setEncoding(SpeechConfig.AUDIO_ENCODING)
				.setLanguageCode(SpeechConfig.AUDIO_LANGUAGE_CODE).setSampleRateHertz(SpeechConfig.AUDIO_SAMPLE_RATE)
				.setEnableAutomaticPunctuation(true).setModel("command_and_search").setMetadata(metadata)
				.setUseEnhanced(true).build();

		streamingRecognitionConfig = StreamingRecognitionConfig.newBuilder().setConfig(recognitionConfig).build();
	}

	public void streamingMicRecognize() throws Exception {

		ResponseObserver<StreamingRecognizeResponse> responseObserver = null;
		try (SpeechClient client = SpeechClient.create()) {

			responseObserver = new ResponseObserver<StreamingRecognizeResponse>() {
				public void onStart(StreamController controller) {
				}

				public void onResponse(StreamingRecognizeResponse response) {
					responses.add(response);
				}

				public void onComplete() {
					for (StreamingRecognizeResponse response : responses) {
						StreamingRecognitionResult result = response.getResultsList().get(0);
						SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
						System.out.printf("Transcript : %s\n", alternative.getTranscript());
					}
				}

				public void onError(Throwable t) {
					System.out.println("Error when performing speech recognition!");
					System.out.println(t);
				}
			};

			ClientStream<StreamingRecognizeRequest> clientStream = client.streamingRecognizeCallable()
					.splitCall(responseObserver);

			StreamingRecognizeRequest request = StreamingRecognizeRequest.newBuilder()
					.setStreamingConfig(streamingRecognitionConfig).build();
			clientStream.send(request);

			DataLine.Info targetInfo = new Info(TargetDataLine.class, SpeechConfig.AUDIO_FORMAT);

			if (!AudioSystem.isLineSupported(targetInfo)) {
				System.out.println("Error when performing speech recognition!");
				System.out.println("Microphone not supported!");
				System.exit(0);
			}

			TargetDataLine targetDataLine = (TargetDataLine) AudioSystem.getLine(targetInfo);
			targetDataLine.open(SpeechConfig.AUDIO_FORMAT);
			targetDataLine.start();

			System.out.println("Start speaking");
			long startTime = System.currentTimeMillis();
			AudioInputStream audio = new AudioInputStream(targetDataLine);

			while (true) {
				long estimatedTime = System.currentTimeMillis() - startTime;
				byte[] data = new byte[SpeechConfig.AUDIO_BYTE_READ];
				audio.read(data);

				if (estimatedTime > SpeechConfig.AUDIO_BYTE_READ) {
					System.out.println("Stop speaking.");
					targetDataLine.stop();
					targetDataLine.close();
					break;
				}

				request = StreamingRecognizeRequest.newBuilder().setAudioContent(ByteString.copyFrom(data)).build();
				clientStream.send(request);
			}

			responseObserver.onComplete();
		}
	}

	public void microphoneTest() {
		TargetDataLine microphone;
		AudioInputStream audioInputStream;
		SourceDataLine sourceDataLine;

		try {
			microphone = AudioSystem.getTargetDataLine(SpeechConfig.AUDIO_FORMAT);

			DataLine.Info info = new DataLine.Info(TargetDataLine.class, SpeechConfig.AUDIO_FORMAT);
			microphone = (TargetDataLine) AudioSystem.getLine(info);
			microphone.open(SpeechConfig.AUDIO_FORMAT);
			ByteArrayOutputStream out = new ByteArrayOutputStream();

			try {

				int numBytesRead;
				int CHUNK_SIZE = SpeechConfig.MICROPHONE_TEST_READ_CHUNK_SIZE;
				byte[] data = new byte[microphone.getBufferSize() / 5];
				microphone.start();
				int bytesRead = 0;
				while (bytesRead < SpeechConfig.MICROPHONE_TEST_BYTES_READ) {
					numBytesRead = microphone.read(data, 0, CHUNK_SIZE);
					bytesRead = bytesRead + numBytesRead;
					System.out.println(bytesRead);
					out.write(data, 0, numBytesRead);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			byte audioData[] = out.toByteArray();

			InputStream byteArrayInputStream = new ByteArrayInputStream(audioData);
			audioInputStream = new AudioInputStream(byteArrayInputStream, SpeechConfig.AUDIO_FORMAT,
					audioData.length / SpeechConfig.AUDIO_FORMAT.getFrameSize());
			DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, SpeechConfig.AUDIO_FORMAT);
			sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
			sourceDataLine.open(SpeechConfig.AUDIO_FORMAT);
			sourceDataLine.start();
			int cnt = 0;
			byte tempBuffer[] = new byte[SpeechConfig.MICROPHONE_TEST_BUFFER_BYTES];

			try {
				while ((cnt = audioInputStream.read(tempBuffer, 0, tempBuffer.length)) != -1) {
					if (cnt > 0) {
						sourceDataLine.write(tempBuffer, 0, cnt);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			sourceDataLine.drain();
			sourceDataLine.close();
			microphone.close();

		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}

	public String getTrasncript() {
		String transcript = "";
		for (StreamingRecognizeResponse response : responses) {
			StreamingRecognitionResult result = response.getResultsList().get(0);
			SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
			responses.clear();
			return transcript + alternative.getTranscript();
			
		}
		return transcript;
	}
}

package com.speech;

import java.util.ArrayList;
import java.util.List;

public class TestJSON {

	public static void main(String[] args) {
		Transcription t1 = new Transcription("gcsURI1", "FLAC", 16000, "I'm a transcription");
		Transcription t2 = new Transcription("gcsURI2", "FLAC", 16000, "I'm a transcription");
		Transcription t3 = new Transcription("gcsURI3", "FLAC", 16000, "I'm a transcription");
		Transcription t4 = new Transcription("gcsURI4", "FLAC", 16000, "I'm a transcription");
		Transcription t5 = new Transcription("gcsURI5", "FLAC", 16000, "I'm a transcription");
		Transcription t6 = new Transcription("gcsURI6", "FLAC", 16000, "I'm a transcription");

		List<Transcription> allTrascriptions = new ArrayList<Transcription>();

		allTrascriptions.add(t1);
		allTrascriptions.add(t2);
		allTrascriptions.add(t3);
		allTrascriptions.add(t4);
		allTrascriptions.add(t5);

		DataStorage d1 = new DataStorage(
				"C:\\Users\\EneR\\Desktop\\Eclipse Workspace\\CRIS_Prototype_Speech\\src\\main\\resources\\TranscriptionOutput");
		d1.saveTranscriptions(allTrascriptions);
		d1.saveTranscription(t6);

	}

}

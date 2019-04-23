package com.speech;

import java.sql.Timestamp;

public class Transcription {
	private String gcsURI;
	private String audioEncoding;
	private int sampleRate;
	private String transcriptionText;
	private Timestamp transcriptionTimestamp;

	public Transcription() {

	}

	public Transcription(String gcsURI, String audioEncoding, int sampleRate, String transcriptionText) {
		this.gcsURI = gcsURI;
		this.audioEncoding = audioEncoding;
		this.sampleRate = sampleRate;
		this.transcriptionText = transcriptionText;
		this.transcriptionTimestamp = new Timestamp(System.currentTimeMillis());
	}

	public String getGcsURI() {
		return gcsURI;
	}

	public void setGcsURI(String gcsURI) {
		this.gcsURI = gcsURI;
	}

	public String getAudioEncoding() {
		return audioEncoding;
	}

	public void setAudioEncoding(String audioEncoding) {
		this.audioEncoding = audioEncoding;
	}

	public int getSampleRate() {
		return sampleRate;
	}

	public void setSampleRate(int sampleRate) {
		this.sampleRate = sampleRate;
	}

	public String getTranscriptionText() {
		return transcriptionText;
	}

	public void setTranscriptionText(String transcriptionText) {
		this.transcriptionText = transcriptionText;
	}

	public Timestamp getTranscriptionTimestamp() {
		return transcriptionTimestamp;
	}

	public void setTranscriptionTimestamp(Timestamp transcriptionTimestamp) {
		this.transcriptionTimestamp = transcriptionTimestamp;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Transcription [gcsURI=");
		builder.append(gcsURI);
		builder.append(", audioEncoding=");
		builder.append(audioEncoding);
		builder.append(", sampleRate=");
		builder.append(sampleRate);
		builder.append(", transcriptionText=");
		builder.append(transcriptionText);
		builder.append(", transcriptionTimestamp=");
		builder.append(transcriptionTimestamp);
		builder.append("]");
		return builder.toString();
	}

}

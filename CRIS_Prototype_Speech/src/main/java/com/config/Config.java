package com.config;

public class Config {

	public final static String LOG_FILE_PATH = "src\\main\\resources\\CloudStorageLog";
	public final static String CLOUD_STORAGE_LOG_FILE_PATH = "src\\main\\resources\\CloudFileOutput";
	public final static String TRANSCRIPTION_LOG_FILE_PATH = "src\\main\\resources\\TranscriptionOutput";
	public final static String SHORT_RECORDING_SCRIPTS_FILE_PATH = "src\\main\\resources\\AllShortRecordings";

	public final static String CLOUD_DEFAULT_BUCKET_NAME = "cris-storage-bucket";
	public final static String CLOUD_SHORT_RECORDINGS_DIRECTORY_NAME = "short-audio-recordings";
	public final static String CLOUD_LONG_RECORDINGS_DIRECTORY_NAME = "long-audio-recordings";
	public final static String CLOUD_SHORT_RECORDINGS_FILE_PATH = "gs://cris-storage-bucket/short-audio-recordings/";
	public final static String CLOUD_LONG_RECORDINGS_FILE_PATH = "gs://cris-storage-bucket/long-audio-recordings/";
	public final static String CLOUD_AUDIO_FLAC_CONTENT_FLAG = "audio/x-flac";

	public final static String SPEECH_RECOGNIZER_PHRASE_HINTS_FILE_PATH = "src\\main\\resources\\PhraseHints";
	public final static String SPEECH_RECOGNIZER_LANGUAGE_CODE = "en-US";
	public final static String SPEECH_RECOGNIZER_AUDIO_ENCODING_FLAC = "FLAC";
	public final static int SPEECH_RECOGNIZER_AUDIO_SAMPLE_RATE = 16000;
	
	public final static String CALL_CENTER_CONVERSATION_TRANSCRIPT_1_FILE_PATH = "src\\main\\resources\\Conversation1";
	public final static String CALL_CENTER_CONVERSATION_TRANSCRIPT_2_FILE_PATH = "src\\main\\resources\\Conversation2";
	public final static String CALL_CENTER_CONVERSATION_TRANSCRIPT_3_FILE_PATH = "src\\main\\resources\\Conversation3";
	
	public final static String CALL_CENTER_CONVERSATION_1_NLP_FILE_PATH = "src\\main\\resources\\NLP Results\\Conversation1Results";
	public final static String CALL_CENTER_CONVERSATION_2_NLP_FILE_PATH = "src\\main\\resources\\NLP Results\\Conversation1Results";
	public final static String CALL_CENTER_CONVERSATION_3_NLP_FILE_PATH = "src\\main\\resources\\NLP Results\\Conversation1Results";
	

}

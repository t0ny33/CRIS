package sandbox;

import javax.sound.sampled.AudioFormat;

import com.google.cloud.speech.v1p1beta1.RecognitionConfig;
import com.google.cloud.speech.v1p1beta1.RecognitionMetadata.InteractionType;
import com.google.cloud.speech.v1p1beta1.RecognitionMetadata.MicrophoneDistance;
import com.google.cloud.speech.v1p1beta1.RecognitionMetadata.OriginalMediaType;
import com.google.cloud.speech.v1p1beta1.RecognitionMetadata.RecordingDeviceType;

public class SpeechConfig {

	// SPEECH RECOGNIZER CONFIGURATION VALUES
	public static final InteractionType METADATA_INTERACTION_TYPE = InteractionType.VOICE_SEARCH;
	public static final MicrophoneDistance METADATA_MICROPHONE_DISTANCE = MicrophoneDistance.NEARFIELD;
	public static final RecordingDeviceType METADATA_RECORDING_DEVICE_TYPE = RecordingDeviceType.PC;
	public static final OriginalMediaType METADATA_ORIGINAL_MEDIA_TYPE = OriginalMediaType.AUDIO;
	// SPEECH RECOGNIZER CONFIGURATION VALUES

	// SPEECH RECOGNIZER CONFIGURATION VALUES
	public static final RecognitionConfig.AudioEncoding AUDIO_ENCODING = RecognitionConfig.AudioEncoding.LINEAR16;
	public static final String AUDIO_LANGUAGE_CODE = "en-US";
	public static final int AUDIO_SAMPLE_RATE = 16000;
	// SPEECH RECOGNIZER CONFIGURATION VALUES

	// MICROPHONE RECORDING CONFIGURATION VALUES
	public static final int AUDIO_CLIENT_RESPONSE_TIMER = 10000;
	public static final AudioFormat AUDIO_FORMAT = new AudioFormat(16000, 16, 1, true, false);
	public static final int AUDIO_BYTE_READ = 10000;
	public static final int MICROPHONE_TEST_READ_CHUNK_SIZE = 1024;
	public static final int MICROPHONE_TEST_BYTES_READ = 100000;
	public static final int MICROPHONE_TEST_BUFFER_BYTES = 10000;
	// MICROPHONE RECORDING CONFIGURATION VALUES

}

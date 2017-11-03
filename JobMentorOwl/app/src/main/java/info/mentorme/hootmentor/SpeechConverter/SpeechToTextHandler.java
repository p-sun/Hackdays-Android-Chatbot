package info.mentorme.hootmentor.SpeechConverter;

/**
 * Created by psun on 2017-10-15.
 */

public interface SpeechToTextHandler {
    void onStart();
    void onVolumeChanged(float volume);
    void onPartialResult(String partial);
    void onCompletion(boolean success, String result);
}

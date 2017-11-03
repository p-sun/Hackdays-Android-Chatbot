package info.mentorme.hootmentor.SpeechConverter;

/**
 * Created by psun on 2017-11-03.
 */

public interface TextToSpeechHandler {
    void onStart();
    void onRangeStart();
    void onCompletion(boolean success);
}

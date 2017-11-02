package info.mentorme.hootmentor.SpeechConverter;

/**
 * Created by psun on 2017-10-15.
 */

public interface ConversionCompletion {
    void onCompletion(boolean success, String result);
    void onPartialResult(String partial);
}

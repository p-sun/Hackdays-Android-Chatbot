package info.mentorme.hootmentor.SpeechConverter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.ArrayList;
import java.util.Locale;

public class SpeechToTextConvertor {
    private ArrayList data;
    private SpeechToTextHandler handler;

    public SpeechToTextConvertor(Activity appContext, SpeechToTextHandler conversionCallBack) {
        this.handler = conversionCallBack;

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                appContext.getPackageName());

        //Listen to intent
        CustomRecognitionListener listener = new CustomRecognitionListener();
        SpeechRecognizer sr = SpeechRecognizer.createSpeechRecognizer(appContext);
        sr.setRecognitionListener(listener);
        sr.startListening(intent);
    }

    class CustomRecognitionListener implements RecognitionListener {
        private static final String TAG = "RecognitionListener";

        public void onReadyForSpeech(Bundle params) {
            handler.onStart();
        }

        public void onBeginningOfSpeech() {
//            Log.d(TAG, "onBeginningOfSpeech");
        }

        public void onRmsChanged(float rmsdB) {
//            Log.d(TAG, "onRmsChanged");
            handler.onVolumeChanged(rmsdB);
        }

        public void onBufferReceived(byte[] buffer) {
//            Log.d(TAG, "onBufferReceived");
        }

        public void onEndOfSpeech() {
//            Log.d(TAG, "onEndofSpeech");
        }

        public void onError(int error) {
            Log.e(TAG, "error " + error);
            handler.onCompletion(false, "");
        }

        public void onResults(Bundle results) {
            data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            System.out.println("onResults: " + data.get(0) + "\n");
            handler.onCompletion(true, data.get(0) + "\n");
        }

        public void onPartialResults(Bundle partialResults) {
            data = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            String partial = data.get(0) + "\n";
            handler.onPartialResult(partial);
        }

        public void onEvent(int eventType, Bundle params) {
            Log.d(TAG, "onEvent " + eventType);
        }
    }
}

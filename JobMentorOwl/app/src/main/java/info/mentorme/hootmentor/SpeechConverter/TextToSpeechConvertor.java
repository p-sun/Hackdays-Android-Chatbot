package info.mentorme.hootmentor.SpeechConverter;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import java.util.HashMap;
import java.util.Locale;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
public class TextToSpeechConvertor implements  TextToSpeech.OnInitListener {
    private TextToSpeech tts;
    private String output;
    private TextToSpeechHandler handler;

    public TextToSpeechConvertor(String aOutput, Context context, TextToSpeechHandler aHandler) {
        tts = new TextToSpeech(context, this);
        this.output = aOutput;
        this.handler = aHandler;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    private void speakOut() {
        tts.setOnUtteranceProgressListener(utteranceListener);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UniqueID");
        tts.speak(output, TextToSpeech.QUEUE_FLUSH, map);
    }

    public void destroy() {
        // Don't forget to shutdown!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);
//            tts.setPitch(0); // set pitch level
//             tts.setSpeechRate(1); // set speech speed rate

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Language is not supported");
            } else {
                speakOut();
            }

        } else {
            Log.e("TTS", "Initialization Failed");
        }
    }

    UtteranceProgressListener utteranceListener = new UtteranceProgressListener() {
        @Override
        public void onRangeStart(String utteranceId, int start, int end, int frame) {
            handler.onRangeStart();
        }

        @Override
        public void onStart(String utteranceId) {
            handler.onStart();
        }

        @Override
        public void onDone(String utteranceId) {
            handler.onCompletion(true);
        }

        @Override
        public void onError(String utteranceId) { }
    };
}


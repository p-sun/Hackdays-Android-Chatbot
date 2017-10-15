package info.androidhive.speechtotext;

import android.os.Handler;

/**
 * Author : Rajanikant
 * Date : 16 Jan 2016
 * Time : 13:08
 */
public class Delay {

    // Delay mechanism

    public interface DelayCallback{
        void afterDelay();
    }

    public static void delay(Double secs, final DelayCallback delayCallback){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                delayCallback.afterDelay();
            }
        }, secs.longValue() * 1000); // afterDelay will be executed after (secs*1000) milliseconds.
    }
}

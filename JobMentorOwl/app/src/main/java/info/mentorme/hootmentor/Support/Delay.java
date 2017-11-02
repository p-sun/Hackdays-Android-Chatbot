package info.mentorme.hootmentor.Support;
import android.os.Handler;

public class Delay {

    public interface DelayCallback{
        void afterDelay();
    }

    public static void delay(Double seconds, final DelayCallback delayCallback){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                delayCallback.afterDelay();
            }
        }, seconds.longValue() * 1000);
    }
}

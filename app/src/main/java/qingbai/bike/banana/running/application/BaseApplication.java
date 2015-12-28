package qingbai.bike.banana.running.application;

import android.app.Application;
import android.os.Handler;
import android.os.HandlerThread;


/**
 * Created by chaoziliang on 15/12/28.
 */
public class BaseApplication  extends Application{


    private static final HandlerThread SHORT_TIME_WORKER_THREAD = new HandlerThread("short worker thread");


    static {
        SHORT_TIME_WORKER_THREAD.start();
    }

    private final static Handler SHORT_TIME_WORKER_HANDLER  = new Handler(SHORT_TIME_WORKER_THREAD.getLooper());

}

package qingbai.bike.banana.running.function.pedometers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Alost on 15/12/31.
 */
public class ScreenChangeReceiver extends BroadcastReceiver {
    private String TAG = "zou";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_SCREEN_ON.equals(action)) {
            /*******启动定时器进行数据刷新*********/
            PedometerManager.getInstance().startStepCountTask();
            Log.d(TAG, "screen on");
        } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
            PedometerManager.getInstance().stopStepCountTask();
            Log.d(TAG, "screen off");
        } else if (Intent.ACTION_USER_PRESENT.equals(action)) {
            Log.d(TAG, "screen unlock");
        } else if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(intent.getAction())) {
            Log.i(TAG, " receive Intent.ACTION_CLOSE_SYSTEM_DIALOGS");
            PedometerManager.getInstance().stopStepCountTask();
        }

    }

}
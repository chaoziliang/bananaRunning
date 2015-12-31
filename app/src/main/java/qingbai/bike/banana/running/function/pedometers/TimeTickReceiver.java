package qingbai.bike.banana.running.function.pedometers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Alost on 15/12/31.
 */
public class TimeTickReceiver extends BroadcastReceiver {

    private boolean flag;

    @Override
    public void onReceive(Context context, Intent intent) {
//		System.out.println(时间变了 + intent.getAction());

        if (intent.getAction().equals(Intent.ACTION_DATE_CHANGED)) {
            //TODO:每过一天 触发ACTION_DATE_CHANGED,第二天开始，记录当天的记步数据并清零计步器数据

            StepDetector.CURRENT_STEP = 0;
            StepDetector.FIRST_STEP_COUNT = 0;
            StepDetector.STEP_COUNT = 0;

            Log.i("zou","TimeTickReceiver 第二天开始，记录当天的记步数据并清零计步器数据");
        }

    }

}
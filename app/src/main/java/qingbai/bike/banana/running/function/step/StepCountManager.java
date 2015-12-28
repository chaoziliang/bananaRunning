package qingbai.bike.banana.running.function.step;

import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zou on 15/12/25.
 * 计步器管理类
 */
public class StepCountManager {
    private static StepCountManager sInstance;

    private Timer mTimer; // 定时器
    private TimerTask mStepCountTask;
    private static final int WAIT_TIME = 2 * 1000; // 2秒
    private static final int CHECK_TIME = 300; // 每300毫秒查询一次

    private int mTotalStep = 0;   //走的总步数

    private StepCountManager() {

    }

    public synchronized static StepCountManager getInstance() {
        if (sInstance == null) {
            sInstance = new StepCountManager();
        }
        return sInstance;
    }

    /**
     * 开启计步器服务
     ***/
    public void startStepCountTask() {
        mTimer = new Timer();
        mStepCountTask = new StepCountTask();

        //启动定时服务
        mTimer.schedule(mStepCountTask, WAIT_TIME, CHECK_TIME);
    }

    /**
     * 计步器查询任务
     **/
    private class StepCountTask extends TimerTask {
        @Override
        public void run() {
            //TODO:通知主线程
            countStep();
        }
    }

    public void stopStepCountTime() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

        if (mStepCountTask != null) {
            mStepCountTask.cancel();
            mStepCountTask = null;
        }
    }

    private void countStep() {
        mTotalStep = StepDetector.CURRENT_STEP;
        Log.i("zou", "StepCountTask" + "  mTotalStep = " + mTotalStep);
    }


    public int getTotalStep() {
        return mTotalStep;
    }


}

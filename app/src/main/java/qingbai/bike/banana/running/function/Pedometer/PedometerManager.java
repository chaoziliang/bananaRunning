package qingbai.bike.banana.running.function.Pedometer;

import java.util.Timer;
import java.util.TimerTask;

import qingbai.bike.banana.running.application.BaseApplication;

/**
 * Created by zoubo on 15/12/25.
 * 计步器管理类:定时刷新数据
 */
public class PedometerManager {
    private static PedometerManager sInstance;

    private Timer mTimer; // 定时器
    private TimerTask mStepCountTask;
    private static final int WAIT_TIME = 1 * 1000; // 延时1秒后开始记步
    private static final int CHECK_TIME = 300; // 每300毫秒查询一次

    private PedometerManager() {

    }

    public synchronized static PedometerManager getInstance() {
        if (sInstance == null) {
            sInstance = new PedometerManager();
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
            //TODO:通知UI线程
            countStep();
        }
    }

    public void stopStepCountTask() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

        if (mStepCountTask != null) {
            mStepCountTask.cancel();
            mStepCountTask = null;
        }
    }

    /**
     * 开始记步，并通知UI线程
     */
    private void countStep() {
        PedometerEvent event = new PedometerEvent();
        event.mIsUpdate = true;
        event.mTotalStep = StepDetector.CURRENT_STEP;
        BaseApplication.postEvent(event);
    }

}

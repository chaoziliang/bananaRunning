package qingbai.bike.banana.running.function.pedometers;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.widget.Toast;

import qingbai.bike.banana.running.application.BaseApplication;

/**
 * zoubo
 * 计步器服务
 */
public class PedometerService extends Service {

    public static Boolean FLAG = false;  // 服务运行标志

    private SensorManager mSensorManager;  // 传感器服务
    private StepDetector detector;  // 传感器监听对象

    private PowerManager mPowerManager; // 电源管理服务
    private PowerManager.WakeLock mWakeLock;  // 屏幕灯

    private Sensor mStepCount;
    private Sensor mStepDetector;


    //监听时间变化的 这个receiver只能动态创建
    private TimeTickReceiver mTickReceiver;
    private IntentFilter mFilter;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mFilter = new IntentFilter();
        mFilter.addAction(Intent.ACTION_TIME_TICK); //每分钟变化的action
        mFilter.addAction(Intent.ACTION_TIME_CHANGED); //设置了系统时间的action
        mTickReceiver = new TimeTickReceiver();
        registerReceiver(mTickReceiver, mFilter);


        FLAG = true; // 标记为服务正在运行

        // 创建监听器类，实例化监听对象
        detector = new StepDetector(this);

        // 获取传感器的服务，初始化传感器
        mSensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);

        /*******如果是4.4以上版本,直接使用计步器,否则采集传感器数据*******/
//        Sensor countSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        mStepCount = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        mStepDetector = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        if (mStepCount != null) {
            Toast.makeText(this, "记步传感器可用!", Toast.LENGTH_SHORT).show();
            mSensorManager.registerListener(detector, mStepCount, SensorManager.SENSOR_DELAY_UI);
            mSensorManager.registerListener(detector, mStepDetector, SensorManager.SENSOR_DELAY_UI);
        } else {  //采用加速的传感器计算
            Toast.makeText(this, "记步传感器不可用，加速度算法记步！", Toast.LENGTH_SHORT).show();
            // 注册传感器，注册监听器
            mSensorManager.registerListener(detector,
                    mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_FASTEST);
        }

        /*******启动定时器进行数据刷新*********/
        PedometerManager.getInstance().startStepCountTask();

        // 电源管理服务  PARTIAL_WAKE_LOCK : CPU 运转，屏幕和键盘灯关闭
        mPowerManager = (PowerManager) BaseApplication.getAppContext()
                .getSystemService(Context.POWER_SERVICE);
//        mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "AccelOn");
        mWakeLock = mPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "AccelOn");
        mWakeLock.acquire();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        FLAG = false;// 服务停止
        if (detector != null) {
            mSensorManager.unregisterListener(detector);
        }

        if (mWakeLock != null) {
            mWakeLock.release();
        }

        if (mTickReceiver != null) {
            unregisterReceiver(mTickReceiver);
        }

        PedometerManager.getInstance().stopStepCountTask();
    }

}

package qingbai.bike.banana.running.function.Pedometer;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.widget.Toast;

/**
 * 计步器服务
 */
public class PedometerService extends Service {

    public static Boolean FLAG = false;  // 服务运行标志

    private SensorManager mSensorManager;  // 传感器服务
    private StepDetector detector;  // 传感器监听对象

    private PowerManager mPowerManager; // 电源管理服务
    private WakeLock mWakeLock;  // 屏幕灯

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        FLAG = true; // 标记为服务正在运行

        // 创建监听器类，实例化监听对象
        detector = new StepDetector(this);

        // 获取传感器的服务，初始化传感器
        mSensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);

        /*******如果是4.4以上版本,直接使用计步器,否则采集传感器数据*******/
        Sensor countSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        if (countSensor != null) {
            mSensorManager.registerListener(detector, countSensor, SensorManager.SENSOR_DELAY_UI);
            Toast.makeText(this, "Count sensor is available!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Count sensor not available!", Toast.LENGTH_LONG).show();
            // 注册传感器，注册监听器
            mSensorManager.registerListener(detector,
                    mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_FASTEST);
        }

        // 电源管理服务
        mPowerManager = (PowerManager) this
                .getSystemService(Context.POWER_SERVICE);
        mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "S");
        mWakeLock.acquire();

        //启动定时器进行数据刷新
        PedometerManager.getInstance().startStepCountTask();
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

        PedometerManager.getInstance().stopStepCountTime();
    }

}

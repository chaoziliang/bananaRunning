package qingbai.bike.banana.running.function.Pedometer;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * zoubo
 * 走步检测器，用于检测走步并计数
 * 具体算法不太清楚，本算法是从谷歌计步器：Pedometer上截取的部分计步算法
 */
public class StepDetector implements SensorEventListener {
    private Context mContext;

    public static int CURRENT_STEP = 0;

    public static float SENSITIVITY = 3;   //SENSITIVITY灵敏度

    private float mLastValues[] = new float[3 * 2];
    private float mScale[] = new float[2];
    private float mYOffset;
    private static long end = 0;
    private static long start = 0;

    /**
     * 最后加速度方向
     */
    private float mLastDirections[] = new float[3 * 2];
    private float mLastExtremes[][] = {new float[3 * 2], new float[3 * 2]};
    private float mLastDiff[] = new float[3 * 2];
    private int mLastMatch = -1;

    /**
     * 传入上下文的构造函数
     *
     * @param context
     */
    public StepDetector(Context context) {
        super();
        mContext = context;
        int h = 480;
        mYOffset = h * 0.5f;
        mScale[0] = -(h * 0.5f * (1.0f / (SensorManager.STANDARD_GRAVITY * 2)));
        mScale[1] = -(h * 0.5f * (1.0f / (SensorManager.MAGNETIC_FIELD_EARTH_MAX)));
    }

    public void setSensitivity(float sensitivity) {
        SENSITIVITY = sensitivity; // 1.97 2.96 4.44 6.66 10.00 15.00 22.50  33.75  50.62
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        Sensor sensor = event.sensor;
        synchronized (this) {
            if (sensor.getType() == Sensor.TYPE_ORIENTATION) {

            } else if (sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
                // Step Counter
                CURRENT_STEP = (int) event.values[0];

            } else if (sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
                // Step Detector
                if (event.values[0] == 1.0) {
                    CURRENT_STEP++;
                }
            } else if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

                //加速度传感器
                int j = (sensor.getType() == Sensor.TYPE_ACCELEROMETER) ? 1 : 0;
                if (j == 1) {
                    //加速度取模
                    float vSum = 0;
                    for (int i = 0; i < 3; i++) {
                        final float v = mYOffset + event.values[i] * mScale[j];
                        vSum += v;
                    }
                    int k = 0;
                    float v = vSum / 3;

                    float direction = (v > mLastValues[k] ? 1 : (v < mLastValues[k] ? -1 : 0));
                    if (direction == -mLastDirections[k]) {
                        // Direction changed
                        int extType = (direction > 0 ? 0 : 1); // minumum or
                        // maximum?
                        mLastExtremes[extType][k] = mLastValues[k];
                        float diff = Math.abs(mLastExtremes[extType][k] - mLastExtremes[1 - extType][k]);

                        if (diff > SENSITIVITY) {
                            boolean isAlmostAsLargeAsPrevious = diff > (mLastDiff[k] * 2 / 3);
                            boolean isPreviousLargeEnough = mLastDiff[k] > (diff / 3);
                            boolean isNotContra = (mLastMatch != 1 - extType);

                            if (isAlmostAsLargeAsPrevious && isPreviousLargeEnough && isNotContra) {
                                end = System.currentTimeMillis();

                                //步数矫正，步伐间隔<200ms和>2000ms，认为是无效步数,这部分也是目前终端计步器算法的核心。
                                if (end - start > 200 && end - start < 2000) {  // 此时判断为走了一步
                                    CURRENT_STEP++;
                                    mLastMatch = extType;
                                }
//                                Log.i("zou", "StepDetector CURRENT_STEP:" + CURRENT_STEP + "&& end - start = " + (end - start));
                                start = end;
                            } else {
                                mLastMatch = -1;
                            }
                        }
                        mLastDiff[k] = diff;
                    }
                    mLastDirections[k] = direction;
                    mLastValues[k] = v;
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

}

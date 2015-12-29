package qingbai.bike.banana.running.function.Pedometer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import qingbai.bike.banana.running.R;
import qingbai.bike.banana.running.application.BaseApplication;

/**
 * zoubo
 * 计步器模块
 */
public class PedometerActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView mStepNumTextView;
    private Button mStartCount;
    private Button mStopCount;

    private TextView mSensitivityTextView;
    private SeekBar sb_sensitivity;
    private int sensitivity = 0;
    public static SharedPreferences sharedPreferences;
    public static final String SENSITIVITY_VALUE = "sensitivity_value";// 灵敏值
    public static final String SETP_SHARED_PREFERENCES = "setp_shared_preferences";// 设置
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_count);
        BaseApplication.globalRegisterEvent(this);

        initView();
    }

    private void initView() {
        mStepNumTextView = (TextView) findViewById(R.id.tv_step_number);
        mStartCount = (Button) findViewById(R.id.btn_start_count);
        mStopCount = (Button) findViewById(R.id.btn_stop_count);

        mSensitivityTextView = (TextView) this
                .findViewById(R.id.sensitivity_value);
        sb_sensitivity = (SeekBar) this.findViewById(R.id.sensitivity);

        mStartCount.setOnClickListener(this);
        mStopCount.setOnClickListener(this);

        sb_sensitivity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {    // 灵敏值动作的监听

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar,
                                          int progress, boolean fromUser) {
                sensitivity = progress;
                mSensitivityTextView.setText(sensitivity + "");
                editor.putInt(SENSITIVITY_VALUE, 10 - sensitivity);
                editor.commit();
                StepDetector.SENSITIVITY = 10 - sensitivity;

            }
        });


        if (sharedPreferences == null) {    //SharedPreferences是Android平台上一个轻量级的存储类，
            //主要是保存一些常用的配置比如窗口状态
            sharedPreferences = getSharedPreferences(SETP_SHARED_PREFERENCES,
                    MODE_PRIVATE);
        }

        editor = sharedPreferences.edit();
        sensitivity = 10 - sharedPreferences.getInt(SENSITIVITY_VALUE, 7);
        mSensitivityTextView.setText(sensitivity + "");

    }

    public void onEventMainThread(PedometerEvent event) {
        if (event.mIsUpdate) {
            mStepNumTextView.setText(event.mTotalStep + "");  // 显示当前步数

        }
    }


    @Override
    public void onClick(View v) {
        Intent service = new Intent(this, PedometerService.class);
        if (v.getId() == R.id.btn_start_count) {
            StepDetector.CURRENT_STEP = 0;
            startService(service);
        } else if (v.getId() == R.id.btn_stop_count) {
            stopService(service);
            StepDetector.CURRENT_STEP = 0;
//            mStepNumTextView.setText(StepDetector.CURRENT_STEP + "");  // 显示当前步数
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BaseApplication.globalUnRegisterEvent(this);
    }
}

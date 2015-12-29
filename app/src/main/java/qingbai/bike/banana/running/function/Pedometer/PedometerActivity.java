package qingbai.bike.banana.running.function.Pedometer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import qingbai.bike.banana.running.R;
import qingbai.bike.banana.running.application.BaseApplication;

/**
 * zoubo
 * 计步器模块
 * */
public class PedometerActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView mStepNumTextView;
    private Button mStartCount;
    private Button mStopCount;

    private int mTotalStep = 0;   //走的总步数

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

        mStartCount.setOnClickListener(this);
        mStopCount.setOnClickListener(this);

    }

    public void onEventMainThread(PedometerEvent event) {
        if (event.mIsUpdate) {
            countStep();
            mStepNumTextView.setText(mTotalStep + "");  // 显示当前步数
        }
    }

    /**
     * 实际的步数
     */
    private void countStep() {
        mTotalStep = StepDetector.CURRENT_STEP;
        Log.i("zou", "MainActivity mTotalStep＝ " + mTotalStep);
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
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BaseApplication.globalUnRegisterEvent(this);
    }
}

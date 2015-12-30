package qingbai.bike.banana.running;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import qingbai.bike.banana.running.function.Pedometer.PedometerActivity;
import qingbai.bike.banana.running.function.runningMap.MapMainActivity;
import qingbai.bike.banana.running.function.weather.WeatherDemo;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.hi);
        init();
    }

    private void init() {
        findViewById(R.id.btn_goto_pedometer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PedometerActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_goto_map).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapMainActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.goto_weather).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WeatherDemo.class);
                startActivity(intent);
            }
        });
    }
}

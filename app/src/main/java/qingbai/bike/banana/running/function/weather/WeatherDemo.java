package qingbai.bike.banana.running.function.weather;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import qingbai.bike.banana.running.R;

public class WeatherDemo extends AppCompatActivity {

    private WeatherManager mWeatherManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_demo);

        mWeatherManager = new WeatherManager();
        mWeatherManager.startGetInfoTask();
    }
}

package qingbai.bike.banana.running.function.weather;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import qingbai.bike.banana.running.R;
import qingbai.bike.banana.running.application.BananaApplication;

public class WeatherDemo extends AppCompatActivity {

    private WeatherManager mWeatherManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_demo);

        mWeatherManager = new WeatherManager();
        mWeatherManager.startGetInfoTask();

        BananaApplication.getGlobalEventBus().register(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        BananaApplication.getGlobalEventBus().unregister(this);
    }

    public void onEventMainThread(WeatherEvent event) {

        if(event.mIsSuccessful){

            findViewById(R.id.weather_content).setVisibility(View.VISIBLE);
            weatherInfo info = event.mWeatherInfo;


            TextView cityName = (TextView) findViewById(R.id.city_name);
            cityName.setText(info.mCity + "  " + info.mTime);

            ((TextView) findViewById(R.id.today_weather)).setText(info.mWeatherDes);
            ((TextView) findViewById(R.id.today_templeture)).setText(info.mTemplature + "°");
            ((TextView) findViewById(R.id.today_wind)).setText(info.mWind);
            ((TextView) findViewById(R.id.today_su)).setText("相对湿度 " + info.mWding);

            ((TextView) findViewById(R.id.today_quality)).setText(info.mPmValue + "  " + info.mQuerty);

            weatherInfo.ForecastDaily day1 = (weatherInfo.ForecastDaily) info.mForecastDailyList.get(0);
            ((TextView) findViewById(R.id.tomorrow_weather)).setText(day1.mWeatherDes);
            ((TextView) findViewById(R.id.tomorrow_templeture)).setText(day1.mTemplature + "°");
            ((TextView) findViewById(R.id.tomorrow_su)).setText("相对湿度 " + day1.mWding);
            ((TextView) findViewById(R.id.tomorrow_wind)).setText(day1.mWind);

            weatherInfo.ForecastDaily day2 = (weatherInfo.ForecastDaily) info.mForecastDailyList.get(1);

            ((TextView) findViewById(R.id.last_weather)).setText(day2.mWeatherDes);
            ((TextView) findViewById(R.id.last_templeture)).setText(day2.mTemplature + "°");
            ((TextView) findViewById(R.id.last_su)).setText("相对湿度 " + day2.mWding);
            ((TextView) findViewById(R.id.last_wind)).setText(day2.mWind);

        } else {
            TextView cityName = (TextView) findViewById(R.id.city_name);
            cityName.setText(event.mErrorMessage);
        }


    }
}

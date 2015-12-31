package qingbai.bike.banana.running.function.weather;

import android.content.Context;
import android.widget.Toast;

import qingbai.bike.banana.running.application.BaseApplication;
import qingbai.bike.banana.running.function.weather.network.IGetWeatherResult;
import qingbai.bike.banana.running.function.weather.network.WeatherJsonRequest;

/**
 * Created by chaoziliang on 15/12/29.
 */
public class WeatherManager implements ILocationResult, IGetWeatherResult{

    private int tryTime = 0;
    private Context mContext;

    private CityLocation mCityLocation;

    private weatherInfo mWeatherInfo;


    public WeatherManager(){

        mCityLocation = new CityLocation(this);
        mContext = BaseApplication.getAppContext();

    }

    public void startGetInfoTask(){
        mCityLocation.startLocation();

    }


    @Override
    public void onSuccessfulLocation(String city) {

        new WeatherJsonRequest(this, city).executeRequest();

        mCityLocation.stop();
    }

    @Override
    public void onFailLocation(String errorMessage) {

        tryTime++;
        if (tryTime > 3){

            String msg = "定位出错，请检查网络和是否打开了GPS";
            Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();

            WeatherEvent event = new WeatherEvent();
            event.mIsSuccessful = false;
            event.mErrorMessage = msg;
            BaseApplication.postEvent(event);

            mCityLocation.stop();
        }
    }

    @Override
    public void onSuccessGet(weatherInfo info) {
        mWeatherInfo = info;

        WeatherEvent event = new WeatherEvent();
        event.mWeatherInfo = info;
        event.mIsSuccessful = true;
        BaseApplication.postEvent(event);
    }

    @Override
    public void onFailGet(String errorMessage) {
        Toast.makeText(mContext, errorMessage, Toast.LENGTH_LONG).show();

        WeatherEvent event = new WeatherEvent();
        event.mIsSuccessful = false;
        event.mErrorMessage = errorMessage;
        BaseApplication.postEvent(event);
    }
}

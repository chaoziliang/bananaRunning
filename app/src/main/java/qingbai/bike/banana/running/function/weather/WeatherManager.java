package qingbai.bike.banana.running.function.weather;

import android.util.Log;

import qingbai.bike.banana.running.function.weather.network.WeatherJsonRequest;

/**
 * Created by chaoziliang on 15/12/29.
 */
public class WeatherManager implements ILocationResult{

    @Override
    public void onSuccessfulLocation(String city) {
        Log.i("chao", "city: " + city);
    }

    @Override
    public void onFailLocation(String errorMessage) {

    }

    private CityLocation mCityLocation;

    private weatherInfo mWeatherInfo;


    public WeatherManager(){

        mCityLocation = new CityLocation(this);

    }

    public void startGetInfoTask(){
        mCityLocation.startLocation();

        new WeatherJsonRequest("guangzhou").executeRequest();
    }


}

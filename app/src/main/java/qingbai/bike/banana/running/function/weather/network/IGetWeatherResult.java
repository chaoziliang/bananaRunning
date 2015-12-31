package qingbai.bike.banana.running.function.weather.network;

import qingbai.bike.banana.running.function.weather.weatherInfo;

/**
 * Created by chaoziliang on 15/12/29.
 */
public interface IGetWeatherResult {


     void onSuccessGet(weatherInfo  info);

     void onFailGet(String errorMessage);

}

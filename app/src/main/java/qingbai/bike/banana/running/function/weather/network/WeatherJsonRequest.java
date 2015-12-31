package qingbai.bike.banana.running.function.weather.network;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import qingbai.bike.banana.running.application.BaseApplication;
import qingbai.bike.banana.running.function.weather.weatherInfo;


/**
 * Created by chaoziliang on 15/12/29.
 */
public class WeatherJsonRequest  {

    private static final String RequestURL = "https://api.heweather.com/x3/weather?key=c6d3302a423f439ba7752f5e3c9c7736&city=";

    private String mRequestURl;

    private IGetWeatherResult mResultCallball = null;

    public WeatherJsonRequest(IGetWeatherResult resultCallball, String cityName) {
        mRequestURl = RequestURL + cityName;
        mResultCallball = resultCallball;
    }

    public void executeRequest() {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, mRequestURl, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    weatherInfo info = new weatherInfo();
                    JSONArray data = response.getJSONArray("HeWeather data service 3.0");

                    JSONObject weather = (JSONObject) data.get(0);

                    String result  = weather.optString("status", "");
                    if(result.equals("ok")){
                        Log.i("chao", "weather: " + weather);
                        JSONObject now = weather.getJSONObject("now");

                        if(now != null){
                            info.mTemplature = now.getString("tmp");
                            JSONObject cond = now.getJSONObject("cond");
                            info.mWeatherDes = cond.getString("txt");
                            info.mWding = now.getString("hum");

                            JSONObject wind = now.getJSONObject("wind");
                            info.mWind = wind.getString("dir");
                        }

                        JSONObject basic = weather.getJSONObject("basic");
                        if(basic != null){
                            info.mCity = basic.getString("city");
                            JSONObject update = basic.getJSONObject("update");
                            info.mTime = update.getString("loc");
                        }

                        JSONObject aqi = weather.getJSONObject("aqi");

                        if(aqi != null){
                            JSONObject city = aqi.getJSONObject("city");
                            info.mQuerty = city.getString("qlty");
                            info.mPmValue = city.getString("pm25");
                        }

                        JSONArray daily_forecast = weather.getJSONArray("daily_forecast");

                        if(daily_forecast != null){

                            JSONObject day1 = (JSONObject) daily_forecast.get(0);

                            if(day1 != null){
                                info.addForecastDaily(parseForecastDaily(day1));
                            }

                            JSONObject day2= (JSONObject) daily_forecast.get(1);

                            if(day2 != null){
                                info.addForecastDaily(parseForecastDaily(day2));
                            }

                        }

                        if(mResultCallball != null){
                            mResultCallball.onSuccessGet(info);
                        }

                        Log.i("chao", "info: " + info.toString());
                    }else{
                        if(mResultCallball != null){
                            mResultCallball.onFailGet("天气服务器出现问题，请稍后再试");
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    if(mResultCallball != null){
                        mResultCallball.onFailGet("天气服务器出现问题，请稍后再试");
                    }
                } catch (Exception e){
                    e.printStackTrace();
                    if(mResultCallball != null){
                        mResultCallball.onFailGet("天气服务器出现问题，请稍后再试");
                    }
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if(mResultCallball != null){
                    mResultCallball.onFailGet(error.getMessage());
                }
            }
        });

        BaseApplication.getGlobalRequestQueue().add(jsonObjectRequest);
    }


    private weatherInfo.ForecastDaily parseForecastDaily(JSONObject day) throws JSONException {
        weatherInfo.ForecastDaily daily = new weatherInfo.ForecastDaily();

        JSONObject wind = day.getJSONObject("wind");
        if(wind != null){
            daily.mWind = wind.getString("dir");
        }

        daily.mWding =  day.getString("hum");

        JSONObject tmp = day.getJSONObject("tmp");
        if(tmp != null){
            String min = tmp.getString("min");
            String max = tmp.getString("max");
            daily.mTemplature = min + "~" + max;
        }
        daily.mTime = day.getString("date");
        JSONObject cond = day.getJSONObject("cond");
        if(cond != null){
            daily.mWeatherDes = cond.getString("txt_d");
        }

        return  daily;
    }


}

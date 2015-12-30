package qingbai.bike.banana.running.function.weather.network;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import qingbai.bike.banana.running.application.BaseApplication;


/**
 * Created by chaoziliang on 15/12/29.
 */
public class WeatherJsonRequest  {

    private static final String RequestURL = "https://api.heweather.com/x3/weather?key=c6d3302a423f439ba7752f5e3c9c7736&city=";

    private String mRequestURl;

    public WeatherJsonRequest(String cityName) {

        mRequestURl = RequestURL + cityName;

        Log.i("chao", "requset url : " + mRequestURl);

    }

    public void executeRequest() {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, mRequestURl, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("chao", "success url : " + response.toString());
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("chao", "onErrorResponse  : " + error.toString());
            }
        });

        BaseApplication.getGlobalRequestQueue().add(jsonObjectRequest);
    }

}

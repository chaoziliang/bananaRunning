package qingbai.bike.banana.running.utils.volleyImp;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;

import org.json.JSONObject;

import java.util.Map;

import qingbai.bike.banana.running.application.BaseApplication;

/**
 *
 * 网络请求的静态类
 * 需要扩展不同类型的请求，在这里提供相应的方式
 */
public class VolleyImpl {

    /**
     * 网路请求:发送一个POST请求，请求响应返回的是一个JsonObject
     */
    public static void sendPostJsonObjectRequest(String url, final Map<String, String> param,
                                                 Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        Request<JSONObject> request = new NormalPostRequest(url, listener, errorListener,param);

        request.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        BaseApplication.getGlobalRequestQueue().add(request);
    }


    public static void cancel() {
        BaseApplication.getGlobalRequestQueue().cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return true;
            }
        });
    }
}

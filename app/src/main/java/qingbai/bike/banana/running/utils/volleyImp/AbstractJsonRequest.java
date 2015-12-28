package qingbai.bike.banana.running.utils.volleyImp;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import qingbai.bike.banana.running.application.BaseApplication;
import qingbai.bike.banana.running.utils.DrawUtil;
import qingbai.bike.banana.running.utils.Machine;
import qingbai.bike.banana.running.utils.NetUtils;

/**
 * Created by joeychao on 2015/8/19.
 */
public abstract class AbstractJsonRequest {

    private String mRequestURL;

    //静态的头对象，避免每次创建
    private static final JSONObject REQUESTHEAD = getPhead();

    public AbstractJsonRequest(String url) {
        mRequestURL = url;
    }

    /**
     * 组装自己的请求数据
     *
     * @param jsonObject
     */
    public abstract void buildJsonObject(JSONObject jsonObject);

    /**
     * 处理成功后的返回数据
     *
     * @param jsonObject
     */
    public abstract void handleSuccess(JSONObject jsonObject);

    /**
     * 错误的处理，有jason解析的错误和VolleyError
     *
     * @param errorException
     */
    public abstract void handleErrorResponse(Exception errorException);


    public void executeRequest() {

        JSONObject jsonObject = new JSONObject();
        JSONObject dataObject = new JSONObject();
        try {

            Map<String, String> param = new HashMap<String, String>();
            jsonObject.put(ProtocolConstant.PARA_P_HEAD, REQUESTHEAD);
            buildJsonObject(dataObject);
            jsonObject.put(ProtocolConstant.PARA_DATA, dataObject);
            param.put(ProtocolConstant.PARA_DATA, jsonObject.toString());
            Log.i("zou", "<executeRequest> jsonObject.toString()=" + jsonObject.toString());
            VolleyImpl.sendPostJsonObjectRequest(mRequestURL, param, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {

                    if (jsonObject != null) {
                        JSONObject headJson = jsonObject.optJSONObject(ProtocolConstant.PARA_RESULT);
                        if (headJson != null) {

                            HeadBean mHeadBean = JsonHandler.parseResponseHead(headJson);
                            if (mHeadBean.getStatus() == ProtocolConstant.STATUS_SUCCESS) {

                                handleSuccess(jsonObject);

                            }
                        } else {
                            handleErrorResponse(new VolleyError(" " + ErrorCode.ERROR_CODE_SERVER));
                        }

                    } else {

                        handleErrorResponse(new VolleyError(" " + ErrorCode.ERROR_CODE_SERVER));
                    }
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    handleErrorResponse(volleyError);
                }
            });

        } catch (JSONException e) {

            handleErrorResponse(e);
        }
    }

    /**
     * 协议头
     **/
    public static JSONObject getPhead() {

        Context context = BaseApplication.getAppContext();

        Map<String, String> map = new HashMap<String, String>();

        map.put(ProtocolConstant.PARA_P_VERSION, String.valueOf(ProtocolConstant.P_VERSION));
        map.put(ProtocolConstant.PARA_ANDROID_ID, Machine.getAndroidId(context));
        map.put(ProtocolConstant.PARA_C_ID, "1");
        map.put(ProtocolConstant.PARA_C_VERSION, Machine.getVersionCode(context));
        map.put(ProtocolConstant.PARA_C_VERSION_NAME, Machine.getVersionName(context));
        map.put(ProtocolConstant.PARA_USER_ID, "000001");
        map.put(ProtocolConstant.PARA_CHANNEL, "253");
        map.put(ProtocolConstant.PARA_COUNTRY, "CN");
        map.put(ProtocolConstant.PARA_LANG, Machine.getLanguage(context));
        map.put(ProtocolConstant.PARA_SDK, String.valueOf(Build.VERSION.SDK_INT));
        map.put(ProtocolConstant.PARA_IMSI, Machine.getIMSI(context));
        map.put(ProtocolConstant.PARA_IMEI, Machine.getIMEI(context));
        map.put(ProtocolConstant.PARA_OFFICIAL, "1");
        map.put(ProtocolConstant.PARA_SYS, Build.MODEL);
        map.put(ProtocolConstant.PARA_ROM, "");
        map.put(ProtocolConstant.PARA_PHONE, Build.MANUFACTURER);
        map.put(ProtocolConstant.PARA_PHONE_NUM, Machine.getPhoneNumber(context));
        String dpi = DrawUtil.sWidthPixels + "*" + DrawUtil.sHeightPixels;
        map.put(ProtocolConstant.PARA_DPI, dpi);
        map.put(ProtocolConstant.PARA_NET, NetUtils.buildNetworkState(context));
        map.put(ProtocolConstant.PARA_S_BUY, "1");
        map.put(ProtocolConstant.PARA_APP_KEY, "DiaryLocker");
        map.put(ProtocolConstant.PARA_S_KEY, "DiaryLocker");
        map.put(ProtocolConstant.PARA_GOOGLEPAY_ID, "");
        map.put(ProtocolConstant.PARA_C_IP, NetUtils.getIpAddress(context));

        JSONObject phead = JsonHandler.getRquestHead(map);
        return phead;
    }
}

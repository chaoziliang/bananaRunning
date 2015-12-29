package qingbai.bike.banana.running.utils.volleyImp;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by zoubo on 2015/8/11.
 * json 处理
 */
public class JsonHandler {

    public static JSONObject getRquestHead(Map<String, String> map) {
        JSONObject jsonObject = new JSONObject();
        if (map != null) {
            for (String key : map.keySet()) {
                try {
                    jsonObject.put(key, map.get(key));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return jsonObject;
    }

    /**
     * 解析头
     ***/
    public static HeadBean parseResponseHead(JSONObject headJson) {
        if (headJson == null) {
            return null;
        }
        HeadBean head = new HeadBean();
        head.setStatus(headJson.optInt(ProtocolConstant.PARA_STATUS));
        head.setErrorCode(headJson.optInt(ProtocolConstant.PARA_ERROR_CODE));
        head.setMsg(headJson.optString(ProtocolConstant.PARA_MSG));
        return head;
    }

}

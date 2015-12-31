package qingbai.bike.banana.running.function.weather;

import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import qingbai.bike.banana.running.application.BaseApplication;

/**
 * Created by chaoziliang on 15/12/29.
 */
public class CityLocation {


    private ILocationResult mLocationResultCallBack;
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new CityLocationListener();

    public CityLocation(ILocationResult locationResult) {

        mLocationResultCallBack = locationResult;

        mLocationClient = new LocationClient(BaseApplication.getAppContext());
        mLocationClient.registerLocationListener(myListener);
        initLocation();

    }

    public void stop(){
        if(mLocationClient != null){
            mLocationClient.stop();
        }
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

    public void startLocation() {

        mLocationClient.start();
    }


    public class CityLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            int code = bdLocation.getLocType();

            if (code == BDLocation.TypeGpsLocation || code == BDLocation.TypeNetWorkLocation || code == BDLocation.TypeOffLineLocation) {
                Log.i("chao", "location is successful");
                if (mLocationResultCallBack != null) {

                    String city = bdLocation.getCity();
                    if(city != null){
                        if(city.contains("市")){
                            city = city.replace("市", "");
                        }

                        try {
                            city = URLEncoder.encode(city, "UTF-8");
                            Log.i("chao", "city: " + bdLocation.getCity() + " district: " + bdLocation.getDistrict());
                            mLocationResultCallBack.onSuccessfulLocation(city);
                            mLocationClient.stop();
                            return;
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (mLocationResultCallBack != null) {
                    String errorMessge = "定位失败";
//                    if(code == )
                    mLocationResultCallBack.onFailLocation(errorMessge);
                }

            } else {
                if (mLocationResultCallBack != null) {
                    String errorMessge = "定位失败";
//                    if(code == )
                    mLocationResultCallBack.onFailLocation(errorMessge);
                }
            }

        }
    }


}

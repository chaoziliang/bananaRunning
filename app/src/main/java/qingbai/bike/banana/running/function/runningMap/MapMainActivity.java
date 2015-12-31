package qingbai.bike.banana.running.function.runningMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import qingbai.bike.banana.running.R;
import qingbai.bike.banana.running.application.BaseApplication;
import qingbai.bike.banana.running.utils.DrawUtil;

/**
 * <br>类描述:登录界面
 * <br>功能详细描述:
 */
public class MapMainActivity extends Activity {
    MapView mMapView = null;
    BaiduMap mBaiduMap = null;

    //鹰眼服务ID
    private static final long SERVER_ID = 102552;
    //entity标识
    private static final String ENTITY_NAME = "MyCar";

    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();

    boolean isFirstLoc = true;// 是否首次定位

    private int mMapMode = 0; //地图模式 0普通 1导航

    private ImageView mLogoutApp;
    private LinearLayout mChageMode;
    private TextView mModeTextView;
    private Button mZoomInBtn;
    private Button mZoomOutBtn;
    private MyLocationConfiguration.LocationMode mCurrentMode = MyLocationConfiguration.LocationMode.FOLLOWING;
    private BitmapDescriptor mCurrentMarker;
    private MyOrientationListener mMyOrientationListener;
    private int mXDirection = 0;
    private float mRadius = 0;
    private double mLatitude = 0;
    private double mLongitude = 0;

    private Timer mTimer = new Timer();
    TimerTask mTask = null;
    private static final int MESSAGE_TIMER_LOAD = 1001;

    private Map<String, SoftReference<Bitmap>> mImageCache = new HashMap<>();
    private Map<String, String> mIconCache = new HashMap<>();

    private Map<String, Marker> mMarkerMap = new HashMap<>();

    private ArrayList<LatLng> points = new ArrayList<LatLng>(); //绘制当前定位轨迹线路
    private ArrayList<LatLng> mHistoryPoints = new ArrayList<LatLng>(); //保存历史坐标点绘制历史轨迹
    private double mTotalDistance;
    private Date mStartTime;
    private Button mStartButton;
    private Date mEndTime;

    private int mIsCalculate = 0; //1开始  2停止

    private int mWeight = 0; //人体重量 公斤

    private Handler mHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == 1) {
                mEndTime = new Date();
                long sportTime = mEndTime.getTime() - mStartTime.getTime();
                ((TextView) findViewById(R.id.sport_time)).setText("运动时间：" + sportTime/1000 + "秒");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map_main);

        Bitmap tempDefaultBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.position_blue_point);
        Bitmap resultDefault = DrawUtil.resizeImage(tempDefaultBitmap, 25, 38);
        mCurrentMarker = BitmapDescriptorFactory.fromBitmap(resultDefault);

        init();

    }

    private void init() {

        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);
        hideZoomView(mMapView);
        mBaiduMap = mMapView.getMap();

        //退出程序
        mLogoutApp = (ImageView) findViewById(R.id.logout_app);
        mLogoutApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog();
            }
        });

        mZoomInBtn = (Button) findViewById(R.id.zoomin);
        mZoomOutBtn = (Button) findViewById(R.id.zoomout);
        mZoomInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float zoomLevel = mBaiduMap.getMapStatus().zoom;
                if (zoomLevel <= 19) {
//					MapStatusUpdateFactory.zoomIn();
                    if (mBaiduMap != null) {
                        mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomIn());
                    }
                    mZoomOutBtn.setEnabled(true);
                } else {
                    Toast.makeText(MapMainActivity.this, "已经放至最大！", Toast.LENGTH_SHORT).show();
                    mZoomInBtn.setEnabled(false);
                }
            }
        });
        mZoomOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float zoomLevel = mBaiduMap.getMapStatus().zoom;
                if (zoomLevel > 2) {
                    if (mBaiduMap != null) {
                        mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomOut());
                    }
                    mZoomInBtn.setEnabled(true);
                } else {
                    mZoomOutBtn.setEnabled(false);
                    Toast.makeText(MapMainActivity.this, "已经缩至最小！", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mModeTextView = (TextView) findViewById(R.id.mode_title);
        //改变地图模式
        mChageMode = (LinearLayout) findViewById(R.id.change_mode);
        mChageMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //如果是跟随 切换到罗盘
                if (mMapMode == 0) {
                    mMapMode = 1;
                    mModeTextView.setText("罗盘");
                    mCurrentMode = MyLocationConfiguration.LocationMode.COMPASS;

                } else if (mMapMode == 1) {
                    mMapMode = 0;
                    mModeTextView.setText("跟随");
                    MapStatus mapStatus = new MapStatus.Builder(mBaiduMap.getMapStatus()).rotate(0).overlook(0).build();
                    MapStatusUpdate msu = MapStatusUpdateFactory.newMapStatus(mapStatus);
                    mBaiduMap.animateMapStatus(msu);
                    mCurrentMode = MyLocationConfiguration.LocationMode.FOLLOWING;
                }

                MyLocationConfiguration config = new MyLocationConfiguration(
                        mCurrentMode, true, mCurrentMarker);
                if (mBaiduMap != null) {
                    mBaiduMap.setMyLocationConfigeration(config);
                }
            }
        });

        findViewById(R.id.weight_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et = (EditText) findViewById(R.id.weight_edit);
                if (et.getText().toString() != null && Integer.parseInt(et.getText().toString()) != 0) {
                    mWeight = Integer.parseInt(et.getText().toString());
                    findViewById(R.id.dialog_layout).setVisibility(View.GONE);
                    findViewById(R.id.data_layout).setVisibility(View.VISIBLE);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et.getWindowToken(), 0); //强制隐藏键盘
                } else {
                    Toast.makeText(MapMainActivity.this, "请输入正确的体重数量", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //点击开始计算按钮
        mStartButton = (Button) findViewById(R.id.controlButton);
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("开始".equals(((Button) v).getText())) {

                    if (mWeight == 0) {
                        findViewById(R.id.data_layout).setVisibility(View.GONE);
                        findViewById(R.id.dialog_layout).setVisibility(View.VISIBLE);
                        Toast.makeText(MapMainActivity.this, "请先输入你的体重！", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ((Button) v).setText("停止");
                    mStartTime = new Date();
                    mTotalDistance = 0;
                    points.clear();
                    mHistoryPoints.clear();
                    mBaiduMap.clear();

                    mHander.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mIsCalculate = 1;
                        }
                    }, 1000);

                    mTask = new TimerTask() {

                        @Override
                        public void run() {
                            // 需要做的事:发送消息
                            Message message = new Message();
                            message.what = 1;
                            mHander.sendMessage(message);
                        }
                    };
                    mTimer.schedule(mTask, 1000, 1000); // 1s后执行task,经过1s再次执行
//
//                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, 300);
//                    mMapView.setLayoutParams(lp);

//                    Context context = MapMainActivity.this;
//                    ApplicationInfo appi;
//                    try {
//                        appi = context.getPackageManager().getApplicationInfo(
//                                context.getPackageName(), PackageManager.GET_META_DATA);
//                        appi.metaData.putString("com.baidu.lbsapi.API_KEY", "");
//                    } catch (PackageManager.NameNotFoundException e1) {
//                        e1.printStackTrace();
//                    }

                } else {
                    ((Button) v).setText("开始");
                    mIsCalculate = 2;
                    if (mTask != null) {
                        mTask.cancel();
                    }
//                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//                    mMapView.setLayoutParams(lp);
                }

            }
        });

        // 开启定位图层
        if (mBaiduMap != null) {
            mBaiduMap.setMyLocationEnabled(true);
            //普通地图
            mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);

            mBaiduMap.setOnMapTouchListener(new BaiduMap.OnMapTouchListener() {
                @Override
                public void onTouch(MotionEvent motionEvent) {
//                    mBaiduMap.hideInfoWindow();//影藏气泡
//
//                    //如果是跟随 切换到普通模式
//                    if (mMapMode == 0) {
//                        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
//                        MyLocationConfiguration config = new MyLocationConfiguration(
//                                mCurrentMode, true, mCurrentMarker);
//                        if (mBaiduMap != null) {
//                            mBaiduMap.setMyLocationConfigeration(config);
//                        }
//                    }
                }
            });
        }

        mLocationClient = ((BaseApplication) getApplication()).mLocationClient;
        mLocationClient.registerLocationListener(myListener);    //注册监听函数

        //初始化传感器
        initOritationListener();
        initLocation();
        //开始定位
        startLoc();
//		showLoading();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mLocationClient.isStarted()) {
            mLocationClient.start();
        }
        // 开启方向传感器
        mMyOrientationListener.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mTimer != null) {
            mTimer.cancel();
        }

        // 退出时销毁定位
        if (mLocationClient != null) {
            mLocationClient.stop();
        }
        // 关闭方向传感器
        if (mMyOrientationListener != null) {
            mMyOrientationListener.stop();
        }
        // 关闭定位图层
        if (mBaiduMap != null) {
            mBaiduMap.setMyLocationEnabled(false);
        }
        if (mMapView != null) {
            mMapView.onDestroy();
            mMapView = null;
        }

        if (mMarkerMap != null) {
            mMarkerMap.clear();
            mMarkerMap = null;
        }

        if (mImageCache != null) {
            mImageCache.clear();
            mImageCache = null;
        }

        if (mIconCache != null) {
            mIconCache.clear();
            mIconCache = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }


    /**
     * <br>功能简述:显示toast
     * <br>功能详细描述:
     * <br>注意:
     *
     * @param err
     */
    private void showToast(String err) {
        Toast.makeText(getApplicationContext(), err, Toast.LENGTH_LONG).show();
    }


    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 2000;
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


    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null)
                return;

//			Log.v("zou", "latitude-------" + location.getLatitude() + "------" + location.getLongitude());
            mRadius = location.getRadius();
            mLatitude = location.getLatitude();
            mLongitude = location.getLongitude();

            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(mRadius)
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mXDirection).latitude(mLatitude)
                    .longitude(mLongitude).build();
            if (mBaiduMap != null) {
                mBaiduMap.setMyLocationData(locData);
            }

            MyLocationConfiguration config = new MyLocationConfiguration(
                    mCurrentMode, true, mCurrentMarker);
            if (mBaiduMap != null) {
                mBaiduMap.setMyLocationConfigeration(config);
            }

            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, 17);
                if (mBaiduMap != null) {
                    mBaiduMap.animateMapStatus(u);
                }
            }

            ((TextView) findViewById(R.id.currentSpeed)).setText("当前配速：" + location.getSpeed() + "公里/时");

            Double onceRecent = 0.0;
            LatLng point = new LatLng(mLatitude, mLongitude);
            if (mIsCalculate == 1) { //开始的时候记录点
                if (points.size() >= 1) {
                    LatLng pointPre = points.get(0);
                    onceRecent = DistanceUtil.getDistance(pointPre, point);

                    if (onceRecent < 20) {
                        points.add(point);
                        mHistoryPoints.add(point);
                    }
                } else {
                    points.add(point);
                    mHistoryPoints.add(point);
                }
            }

//              Log.v("zou", "------" + location.getSpeed() + "---distance---" + recent);
            if (mIsCalculate == 1 && points.size() >= 2) { //正在运动 且 绘制点大于2个再绘制

                OverlayOptions ooPolyline = new PolylineOptions().width(10)
                        .color(0xAAFF0000).points(points);
                mBaiduMap.addOverlay(ooPolyline);  //绘制折线

                mTotalDistance += onceRecent;

                ((TextView) findViewById(R.id.totalDistance)).setText("运动距离:" + Math.round((float) mTotalDistance * 100) / 100.0 + "米");
                long sportTime = mEndTime.getTime() - mStartTime.getTime();
                long hourTime = sportTime / (60 * 1000);
                float everSpeed = (float) (Math.round((float) (mTotalDistance / hourTime) * 100) / 100.0);
                ((TextView) findViewById(R.id.speedEver)).setText("平均速度：" + everSpeed + "米/分");

                if (mTotalDistance > 0) {
                    ((TextView) findViewById(R.id.speedEfficiency)).setText("平均速率：" + Math.round(1000 / everSpeed * 100) / 100.0 + "分/千米");

                    double kaluli = calcCalorie(sportTime / 1000f, mTotalDistance, mTotalDistance / (sportTime / 1000f), 0, mWeight);
                    ((TextView) findViewById(R.id.kaluli)).setText("卡路里：" + Math.round(kaluli * 100) / 100.0 + "大卡");
                } else {
                    ((TextView) findViewById(R.id.speedEfficiency)).setText("平均速率：" + 0.0 + "分/千米");

                    ((TextView) findViewById(R.id.kaluli)).setText("卡路里：" + 0.0 + "大卡");
                }

                if (mHistoryPoints.size() >= 3) {
                    mBaiduMap.clear();
                    ooPolyline = new PolylineOptions().width(10)
                            .color(0xAAFF0000).points(mHistoryPoints);
                    mBaiduMap.addOverlay(ooPolyline);  //绘制历史折线
                }
                points.clear();
                points.add(point);
            }
        }
    }


    /**
     * 初始化方向传感器
     */
    private void initOritationListener() {
        mMyOrientationListener = new MyOrientationListener(
                getApplicationContext());
        mMyOrientationListener
                .setOnOrientationListener(new MyOrientationListener.OnOrientationListener() {
                    @Override
                    public void onOrientationChanged(float x) {
                        mXDirection = (int) x;

//						// 构造定位数据
//						MyLocationData locData = new MyLocationData.Builder()
//								.accuracy(mRadius)
//										// 此处设置开发者获取到的方向信息，顺时针0-360
//								.direction(mXDirection)
//								.latitude(mLatitude)
//								.longitude(mLongitude).build();
//						// 设置定位数据
//						mBaiduMap.setMyLocationData(locData);
//						// 设置自定义图标
//						BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
//								.fromResource(R.drawable.left_more);
//						MyLocationConfiguration config = new MyLocationConfiguration(
//								mCurrentMode, true, mCurrentMarker);
//						mBaiduMap.setMyLocationConfigeration(config);


                    }
                });
    }

    /**
     * 查询实时轨迹
     */
    public void startLoc() {
        if (mLocationClient != null) {
            mLocationClient.start();
        }
    }

    /**
     * 隐藏缩放控件
     *
     * @param mapView
     */
    private void hideZoomView(MapView mapView) {
        // 隐藏缩放控件
        int childCount = mapView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = mapView.getChildAt(i);
            if (child instanceof ZoomControls || child instanceof ImageView) {
                child.setVisibility(View.GONE);
            }
        }
    }


    //移动标注到中心点
    private void moveCenterPoint(double latitude, double longitude) {
        LatLng pt = null;
        pt = new LatLng(latitude, longitude);
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(pt)
                .zoom(17)
                .build();
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        if (mBaiduMap != null) {
            mBaiduMap.animateMapStatus(mMapStatusUpdate);
        }
    }

    private void dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MapMainActivity.this);
        builder.setMessage("确认注销并退出吗?");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                MapMainActivity.this.finish();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }


    //计算卡路里
    //deltaTime 两次测量间隔时常，单位：秒
    //deltaDistance 两次测量间隔的距离，单位：米
    //speed 两次测量间的平均速度，单位：米/秒
    //deltaAltitude 两次测量间的海拔变化，单位：米    注：先尝试传0看结果，再尝试传入两次测量间距离变化*0.01作为海拔变化
    //weight 体重 单位：公斤
    public static double calcCalorie(double deltaTime, double deltaDistance, double speed, double
            deltaAltitude, double weight) {
        double[] array1 = {0.0, 0.223, 0.447, 0.67, 0.894, 1.117, 1.341, 1.564, 1.788, 2.011, 2.235,
                2.332, 2.682, 2.98, 3.155, 3.352, 3.576, 3.831, 4.126, 4.47, 4.876};
        double[] array2 = {1.2, 1.5, 1.8, 2.1, 2.5, 3.0, 3.3, 3.8, 5.0, 6.3, 8.0, 9.0, 10.0, 11.0, 11.5,
                12.5, 13.5, 14.0, 15.0, 16.0, 18.0};

        double UPHILLFACTOR = 5.0; //uphillfactor
        double DOWNHILLFACTOR = 0.2; //downhillfactor

        double[] v4 = array1; //METspeeds
        double[] v5 = array2; //METvals
        float v2 = 0.0f; //METinterpolated
        double v14; //slopeFactor

        double v6; //calorie

        if (speed >= v4[1] && speed <= v4[v4.length - 1]) {
            for (int i = 1; i < v4.length; i++) {
                if (speed < v4[i]) {
                    double v12 = (speed - v4[i - 1]) / (v4[i] - v4[i - 1]); //scalefactor
                    v2 = (float) (v5[i - 1] + (v5[i] - v5[i - 1]) * v12);
                    break;
                }
            }
        } else {
            v2 = (float) v5[0];
        }

        v6 = weight * v2 * deltaTime / 3600;

        if (deltaAltitude < 0) {
            v14 = DOWNHILLFACTOR;
        } else {
            v14 = UPHILLFACTOR;
        }
        //goto_1
        if (deltaDistance > 0) {
            v6 = v6 * (1.0 + v14 * deltaAltitude / deltaDistance);
        }
        return v6;
    }
}

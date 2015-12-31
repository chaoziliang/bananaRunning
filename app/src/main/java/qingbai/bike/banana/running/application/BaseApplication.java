package qingbai.bike.banana.running.application;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;

import de.greenrobot.event.EventBus;
import qingbai.bike.banana.running.utils.DrawUtil;


/**
 * application
 */
public class BaseApplication extends Application {
    /**
     * 异步线程，用于处理一般比较短暂的耗时操作，如数据库读写操作等<br>
     */
    protected static final HandlerThread SHORT_TASK_WORKER_THREAD = new HandlerThread(
            "Short-Task-Worker-Thread");

    static {
        SHORT_TASK_WORKER_THREAD.start();
    }

    protected final static Handler SHORT_TASK_HANDLER = new Handler(
            SHORT_TASK_WORKER_THREAD.getLooper());
    protected final static Handler MAIN_LOOPER_HANDLER = new Handler(
            Looper.getMainLooper());
    public LocationClient mLocationClient;
    public MyLocationListener mMyLocationListener;

    private static final HandlerThread SHORT_TIME_WORKER_THREAD = new HandlerThread("short worker thread");

    protected final static EventBus GLOBAL_EVENT_BUS = EventBus.getDefault();

    protected static BaseApplication sInstance;
    protected static RequestQueue GLOBAL_REQUESTQUEUE;

    public BaseApplication() {
        sInstance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        DrawUtil.resetDensity(this);
        GLOBAL_REQUESTQUEUE = Volley.newRequestQueue(this);

        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());

        mLocationClient = new LocationClient(this.getApplicationContext());
        mMyLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mMyLocationListener);
    }



    /**
     * 获取一个全局的网络请求<br>
     *
     * @return
     */
    public static RequestQueue getGlobalRequestQueue() {
        return GLOBAL_REQUESTQUEUE;
    }



    /**
     * 获取一个全局的EventBus实例<br>
     *
     * @return
     */
    public static EventBus getGlobalEventBus() {
        return GLOBAL_EVENT_BUS;
    }

    /**
     * 使用全局EventBus post一个事件<br>
     *
     * @param event
     */
    public static void postEvent(Object event) {
        GLOBAL_EVENT_BUS.post(event);
    }

    /**
     * 使用全局EventBus post一个Sticky事件<br>
     *
     * @param event
     */
    public static void postStickyEvent(Object event) {
        GLOBAL_EVENT_BUS.postSticky(event);
    }

    /**
     * 注册事件
     *
     * @param object
     */
    public static void globalRegisterEvent(Object object) {
        GLOBAL_EVENT_BUS.register(object);
    }

    /**
     * 反注册事件
     *
     * @param object
     */
    public static void globalUnRegisterEvent(Object object) {
        GLOBAL_EVENT_BUS.unregister(object);
    }

    /**
     * 提交一个Runable到短时任务线程执行<br>
     * <p>
     * <strong>NOTE:</strong>
     * 只充许提交比较短暂的耗时操作，如数据库读写操作等，像网络请求这类可能耗时较长的<i>不能</i>提交，<br>
     * 以免占用线程影响其他的重要数据库操作。
     * </p>
     *
     * @see #postRunOnShortTaskThread(Runnable, long)
     * @see #removeFromShortTaskThread(Runnable)
     * @param r
     */
    public static void postRunOnShortTaskThread(Runnable r) {
        postRunnableByHandler(SHORT_TASK_HANDLER, r);
    }

    /**
     * 提交一个Runable到短时任务线程执行<br>
     * <p>
     * <strong>NOTE:</strong>
     * 只充许提交比较短暂的耗时操作，如数据库读写操作等，像网络请求这类可能耗时较长的<i>不能</i>提交，<br>
     * 以免占用线程影响其他的重要数据库操作。
     * </p>
     *
     * @see #postRunOnShortTaskThread(Runnable)
     * @see #removeFromShortTaskThread(Runnable)
     * @param r
     * @param delayMillis
     *            延迟指定的毫秒数执行.
     */
    public static void postRunOnShortTaskThread(Runnable r, long delayMillis) {
        postRunnableByHandler(SHORT_TASK_HANDLER, r, delayMillis);
    }

    /**
     * 从短时任务线程移除一个先前post进去的Runable<b>
     *
     * @see #postRunOnShortTaskThread(Runnable)
     * @see #postRunOnShortTaskThread(Runnable, long)
     * @param r
     */
    public static void removeFromShortTaskThread(Runnable r) {
        removeRunnableFromHandler(SHORT_TASK_HANDLER, r);
    }

    /**
     * 提交一个Runable到UI线程执行<br>
     *
     * @see #removeFromUiThread(Runnable)
     * @param r
     */
    public static void postRunOnUiThread(Runnable r) {
        postRunnableByHandler(MAIN_LOOPER_HANDLER, r);
    }

    /**
     * 提交一个Runable到UI线程执行<br>
     *
     * @see #postRunOnUiThread(Runnable)
     * @see #removeFromUiThread(Runnable)
     * @param r
     * @param delayMillis
     *            延迟指定的毫秒数执行.
     */
    public static void postRunOnUiThread(Runnable r, long delayMillis) {
        postRunnableByHandler(MAIN_LOOPER_HANDLER, r, delayMillis);
    }

    /**
     * 从UI线程移除一个先前post进去的Runable<b>
     *
     * @see #postRunOnUiThread(Runnable)
     * @param r
     */
    public static void removeFromUiThread(Runnable r) {
        removeRunnableFromHandler(MAIN_LOOPER_HANDLER, r);
    }

    /**
     * 是否运行在UI线程<br>
     *
     * @return
     */
    public static boolean isRunOnUiThread() {
        return Thread.currentThread() == Looper.getMainLooper().getThread();
    }

    private static void postRunnableByHandler(Handler handler, Runnable r) {
        handler.post(r);
    }

    private static void postRunnableByHandler(Handler handler, Runnable r,
                                              long delayMillis) {
        if (delayMillis <= 0) {
            postRunnableByHandler(handler, r);
        } else {
            handler.postDelayed(r, delayMillis);
        }
    }

    private static void removeRunnableFromHandler(Handler handler, Runnable r) {
        handler.removeCallbacks(r);
    }

    public static Context getAppContext() {
        return sInstance;
    }

    /**
     * 实现实时位置回调监听
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            //Receive Location
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation){// GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());// 单位：公里每小时
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\nheight : ");
                sb.append(location.getAltitude());// 单位：米
                sb.append("\ndirection : ");
                sb.append(location.getDirection());
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append("\ndescribe : ");
                sb.append("gps定位成功");

            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){// 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                //运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());
                sb.append("\ndescribe : ");
                sb.append("网络定位成功");
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }

            Log.i("BaiduLocationApiDem", sb.toString());
            // mLocationClient.setEnableGpsRealTimeTransfer(true);
        }
    }
}

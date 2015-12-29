package qingbai.bike.banana.running.utils.volleyImp;

/**
 * Created by zoubo on 2015/9/2.
 * 网络相关常量
 */
public class NetConstant {
    // 测试服务器域名地址
    public static final String DIARY_LOCKER_WALLPAPER_URL_SIT = "http://10.0.0.204:20002/diaryinterface/";
    //正式服务器地址
    public static final String DIARY_LOCKER_WALLPAPER_URL = "http://diaryinterface.gzbyte.com/diaryinterface/";

    //在线壁纸库
    public static final String REMDINFO = "remdinfo.shtml";
    
    //每日壁纸
    public static final String IMAGEINFO_QUERY = "imageinfo_query.shtml";

    public static String getFullUrl(String url) {
        String host = DIARY_LOCKER_WALLPAPER_URL;
        String fullUrl = host + url;
        return fullUrl;
    }

}

package qingbai.bike.banana.running.utils.volleyImp;

/**
 * Created by zoubo on 2015/8/11.
 * 请求协议常量
 */
public class ProtocolConstant {
    public static final float P_VERSION = 1.0f;

    /********协议头相关**********/
    public static final String PARA_P_HEAD = "phead";
    public static final String PARA_P_VERSION = "pversion";  // 协议版本号
    public static final String PARA_ANDROID_ID = "aid";  // 手机androidid
    public static final String PARA_C_ID = "cid";  //产品id
    public static final String PARA_C_VERSION = "cversion"; // 客户端软件版本num
    public static final String PARA_C_VERSION_NAME = "cversionname";
    public static final String PARA_USER_ID = "uid";  // 用户id
    public static final String PARA_CHANNEL = "channel"; // 渠道号
    public static final String PARA_COUNTRY = "country"; //// 国家(大写)
    public static final String PARA_LANG = "lang"; // 语言（小写）
    public static final String PARA_SDK = "sdk"; // 系统sdklevel
    public static final String PARA_IMSI = "imsi"; // 运营商编码
    public static final String PARA_IMEI = "imei"; // imei
    public static final String PARA_OFFICIAL = "official"; //// 是否为官方 默认：0，1：官方，2：非官方
    public static final String PARA_SYS = "sys";  // 系统版本
    public static final String PARA_ROM = "rom"; // 用户手机ROM信息
    public static final String PARA_PHONE = "phone"; // 机型
    public static final String PARA_PHONE_NUM = "phonenum"; // 手机号码
    public static final String PARA_DPI = "dpi"; // 手机分辨率 如320*480
    public static final String PARA_NET = "net"; // 网络类型
    public static final String PARA_S_BUY = "sbuy"; // 是否支持内购 0：不支持，1：支持
    public static final String PARA_APP_KEY = "appkey"; // 应用key
    public static final String PARA_S_KEY = "skey"; // 密钥key
    public static final String PARA_GOOGLEPAY_ID = "googlepayid";  // 谷歌帐号
    public static final String PARA_C_IP = "cip"; // 用户ip

    public static final String PARA_DATA = "data";

    /****************返回**************/
    public static final String PARA_RESULT = "result";
    public static final int STATUS_SUCCESS = 1; //成功状态
    public static final String PARA_STATUS = "status";  //服务器处理结果
    public static final String PARA_ERROR_CODE = "errorcode";  //错误码
    public static final String PARA_MSG = "msg";  //错误信息

    /*****************请求类型***********/
    public static final String PARA_MAIN_TYPES = "types";
    public static final int PARA_TYPE_WALLPAPER = 1;  //错误信息
    public static final String PARA_MAIN_TYPES_RESDATA = "resdata";



}

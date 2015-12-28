package qingbai.bike.banana.running.utils.volleyImp;

/**
 * Created by zoubo on 2015/8/11.
 * <br>类描述:服务器响应头部数据
 * <br>功能详细描述:服务器响应头部，包括服务器处理结果，错误码等
 */
public class HeadBean {
    private int mStatus;
    private int mErrorCode;
    private String msg;

    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int status) {
        this.mStatus = status;
    }

    public int getErrorCode() {
        return mErrorCode;
    }

    public void setErrorCode(int errorCode) {
        this.mErrorCode = errorCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}

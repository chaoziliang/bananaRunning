package qingbai.bike.banana.running.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewConfiguration;

/**
 * 绘制工具类
 * 
 * @author luopeihuan
 * 
 */
public class DrawUtil {
	public static float sDensity = 1.0f;
	public static int sDensityDpi;
	public static int sWidthPixels;
	public static int sHeightPixels;
	public static float sFontDensity;
	public static int sTouchSlop = 15; // 点击的最大识别距离，超过即认为是移动
	public static int sNavBarLocation;


	// 在某些机子上存在不同的density值，所以增加两个虚拟值
	public static float sVirtualDensity = -1;
	public static float sVirtualDensityDpi = -1;
	/**
	 * dip/dp转像素
	 * 
	 * @return 像素值
	 */
	public static int dip2px(float dipVlue) {
		return (int) (dipVlue * sDensity + 0.5f);
	}

	/**
	 * 像素转dip/dp
	 * 
	 * @param pxValue
	 *            像素大小
	 * @return dip值
	 */
	public static int px2dip(float pxValue) {
		final float scale = sDensity;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * sp 转 px
	 * 
	 * @param spValue
	 *            sp大小
	 * @return 像素值
	 */
	public static int sp2px(float spValue) {
		final float scale = sDensity;
		return (int) (scale * spValue);
	}

	/**
	 * px转sp
	 * 
	 * @param pxValue
	 *            像素大小
	 * @return sp值
	 */
	public static int px2sp(float pxValue) {
		final float scale = sDensity;
		return (int) (pxValue / scale);
	}

	public static void resetDensity(Context context) {
		if (context != null && null != context.getResources()) {
			DisplayMetrics metrics = context.getResources().getDisplayMetrics();
			sDensity = metrics.density;
			sFontDensity = metrics.scaledDensity;
			sWidthPixels = metrics.widthPixels;
			sHeightPixels = metrics.heightPixels;
			sDensityDpi = metrics.densityDpi;
			try {
				final ViewConfiguration configuration = ViewConfiguration.get(context);
				if (null != configuration) {
					sTouchSlop = configuration.getScaledTouchSlop();
				}
			} catch (Throwable e) {
				Log.i("DrawUtils", "resetDensity has error" + e.getMessage());
			}
		}
	}


}

package com.limahao.ticket.utils;

import java.io.File;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.limahao.ticket.R;

public class CommonUtil {
	public static DisplayMetrics metric = new DisplayMetrics();
	/**
	 * 解决scalview与listview冲突问题
	 * @param listView
	 */
	  public static void setListViewHeightBasedOnChildren(ListView listView) {
          ListAdapter listAdapter = listView.getAdapter(); 
          if (listAdapter == null) {
              // pre-condition
              return;
          }

          int totalHeight = 0;
          for (int i = 0; i < listAdapter.getCount(); i++) {
              View listItem = listAdapter.getView(i, null, listView);
              listItem.measure(0, 0);
              totalHeight += listItem.getMeasuredHeight();
          }

          ViewGroup.LayoutParams params = listView.getLayoutParams();
          params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
          listView.setLayoutParams(params);
      }
	/**
	 * 检测sdcard是否可用
	 * 
	 * @return true为可用，否则为不可用
	 */
	public static boolean sdCardIsAvailable() {
		String status = Environment.getExternalStorageState();
		if (!status.equals(Environment.MEDIA_MOUNTED))
			return false;
		return true;
	}

	/**
	 * Checks if there is enough Space on SDCard
	 * 
	 * @param updateSize
	 *            Size to Check
	 * @return True if the Update will fit on SDCard, false if not enough space on SDCard Will also return false, if the SDCard is
	 *         not mounted as read/write
	 */
	public static boolean enoughSpaceOnSdCard(long updateSize) {
		String status = Environment.getExternalStorageState();
		if (!status.equals(Environment.MEDIA_MOUNTED))
			return false;
		return (updateSize < getRealSizeOnSdcard());
	}

	/**
	 * get the space is left over on sdcard
	 */
	public static long getRealSizeOnSdcard() {
		File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return availableBlocks * blockSize;
	}

	/**
	 * Checks if there is enough Space on phone self
	 * 
	 */
	public static boolean enoughSpaceOnPhone(long updateSize) {
		return getRealSizeOnPhone() > updateSize;
	}

	/**
	 * get the space is left over on phone self
	 */
	public static long getRealSizeOnPhone() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		long realSize = blockSize * availableBlocks;
		return realSize;
	}
	
	/**
	 * 根据手机分辨率从dp转成px
	 * @param context
	 * @param dpValue
	 * @return
	 */
	public static  int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }  
	  
    /** 
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp 
     */  
	public static  int px2dip(Context context, float pxValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f)-15;  
    }  

	/** 
     * 得到自定义的progressDialog 
     * @param context 
     * @param msg 
     * @return 
     */  
    public static Dialog createLoadingDialog(Activity context, String msg) {  
  
        LayoutInflater inflater = LayoutInflater.from(context);  
        View v = inflater.inflate(R.layout.dialog_loading, null);// 得到加载view  

        TextView tipTextView = (TextView) v.findViewById(R.id.dialog_loading_tv);// 提示文字  
        tipTextView.setText(msg);// 设置加载信息  
//        PopupWindow loadingDialog = new PopupWindow(context);
//        loadingDialog.setContentView(v);
        Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog  
  
        loadingDialog.setCancelable(false);// 不可以用“返回键”取消  
        loadingDialog.setContentView(v);
        loadingDialog.setContentView(v, new LinearLayout.LayoutParams(  
                LinearLayout.LayoutParams.MATCH_PARENT,  
                LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局  
        return loadingDialog;  
  
    }
    
    public static int darkenColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f;
        return Color.HSVToColor(hsv);
    }
    
	/**
	 * 判断sim是否存在
	 * @return true 存在 flase 不存在
	 */
	public static boolean isSimExist(Context context){
		TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return manager.getSimState() == TelephonyManager.SIM_STATE_ABSENT ? false : true;
	}
	/**
	 * 得到屏幕高度
	 */
	public static int getScreenHeight(Activity context){
		context.getWindowManager().getDefaultDisplay().getMetrics(metric);
		return metric.heightPixels;
		
	}
	
	/**
	 * 得到屏幕宽度
	 */
	public static int getScreenWidth(Activity context){
		context.getWindowManager().getDefaultDisplay().getMetrics(metric);
		return metric.widthPixels;
		
	}
	/**
	 *  得到手机DPI
	 */
	public static int getdensityDpi(Activity context){
		context.getWindowManager().getDefaultDisplay().getMetrics(metric);
		return metric.densityDpi;
	}
}

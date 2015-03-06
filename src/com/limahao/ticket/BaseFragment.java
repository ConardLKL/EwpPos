package com.limahao.ticket;

import com.limahao.ticket.R;
import com.limahao.ticket.utils.CommonUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @fileName BaseFragment.java
 * @description Fragment基类
 * @version 1.0
 */
public abstract class BaseFragment extends Fragment implements OnClickListener{
	/** Appliction基类对象 **/
	protected BaseApplication mApplication;
	/** Acitivity对象 **/
	protected BaseActivity mActivity;
	/** 上下文 **/
	protected Context mContext;
	/** 当前显示的内容 **/
	protected View mView;
	
	/**
	 * 弹出
	 */
	protected Dialog dialog = null;
	
	private Toast tipToast;
	
	public BaseFragment(){
	}
	
//	
//	public BaseFragment(BaseApplication application, BaseActivity activity,
//			Context context) {
//		mApplication = application;
//		mActivity = activity;
//		mContext = context;
//	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = (BaseActivity)activity;
		mApplication = (BaseApplication) mActivity.getApplication();;
		mContext = mActivity;
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	/** 绑定界面UI **/
	protected abstract void findViewById();

	/** 界面UI事件监听 **/
	protected abstract void setListener();

	/** 界面数据初始化 **/
	protected abstract void init();

	/** 通过ID绑定UI **/
	protected View findViewById(int id) {
		return mView.findViewById(id);
	}


	/** 短暂显示Toast提示(来自res) **/
	protected void showShortToast(int resId) {
		showShortToast(getString(resId));
	}
	/** 短暂显示Toast提示(来自String) **/
	protected void showShortToast(String text) {
		// 获取LayoutInflater对象
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// 由layout文件创建一个View对象
		View layout = inflater.inflate(R.layout.toast, null);
		// 实例化ImageView和TextView对象
		TextView textView = (TextView) layout.findViewById(R.id.toast_tv);
		if (tipToast == null) {
			tipToast = new Toast(getActivity());
			textView.setText(text);
			tipToast.setView(layout);
		}else {
			textView = (TextView)tipToast.getView().findViewById(R.id.toast_tv);
			textView.setText(text);
		}
		tipToast.setDuration(Toast.LENGTH_SHORT);
		tipToast.show();
//		if (tipToast == null) {
//			tipToast=Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
//		}else{
//			tipToast.setText(text);
//			tipToast.setDuration(Toast.LENGTH_SHORT);
//		}
//		tipToast.show();
	}

	/** 长时间显示Toast提示(来自res) **/
	protected void showLongToast(int resId) {
		showLongToast(getString(resId));
	}

	/** 长时间显示Toast提示(来自String) **/
	protected void showLongToast(String text) {
		// 获取LayoutInflater对象
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// 由layout文件创建一个View对象
		View layout = inflater.inflate(R.layout.toast, null);
		// 实例化ImageView和TextView对象
		TextView textView = (TextView) layout.findViewById(R.id.toast_tv);
		if (tipToast == null) {
			tipToast = new Toast(getActivity());
			textView.setText(text);
			tipToast.setView(layout);
		}else {
			textView = (TextView)tipToast.getView().findViewById(R.id.toast_tv);
			textView.setText(text);
		}
		tipToast.setDuration(Toast.LENGTH_LONG);
		tipToast.show();
//		if (tipToast == null) {
//			tipToast=Toast.makeText(mContext, text, Toast.LENGTH_LONG);
//		}else{
//			tipToast.setText(text);
//			tipToast.setDuration(Toast.LENGTH_LONG);
//		}
//		tipToast.show();
	}

	/** Debug输出Log日志 **/
	protected void showLogDebug(String tag, String msg) {
		Log.d(tag, msg);
	}

	/** Error输出Log日志 **/
	protected void showLogError(String tag, String msg) {
		Log.e(tag, msg);
	}

	/** 通过Class跳转界面 **/
	protected void startActivity(Class<?> cls) {
		startActivity(cls, null);
	}

	/** 含有Bundle通过Class跳转界面 **/
	protected void startActivity(Class<?> cls, Bundle bundle) {
		Intent intent = new Intent();
		intent.setClass(mContext, cls);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		mContext.startActivity(intent);
		mActivity.overridePendingTransition(R.anim.push_left_in,
				R.anim.push_left_out);
	}
	
	/** 含有Bundle通过Action跳转界面 **/
	protected void startActivityForRet(Class<?> cls, Bundle bundle, int requestCode) {
		Intent intent = new Intent();
		intent.setClass(mContext, cls);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		startActivityForResult(intent, requestCode);
		mActivity.overridePendingTransition(R.anim.push_left_in,
				R.anim.push_left_out);
	}

	/** 通过Action跳转界面 **/
	protected void startActivity(String action) {
		startActivity(action, null);
	}

	/** 含有Bundle通过Action跳转界面 **/
	protected void startActivity(String action, Bundle bundle) {
		Intent intent = new Intent();
		intent.setAction(action);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		mContext.startActivity(intent);
		mActivity.overridePendingTransition(R.anim.push_left_in,
				R.anim.push_left_out);
	}

	/** 含有标题和内容的对话框 **/
	protected AlertDialog showAlertDialog(String title, String message) {
		AlertDialog alertDialog = new AlertDialog.Builder(mContext)
				.setTitle(title).setMessage(message).show();
		return alertDialog;
	}

	/** 含有标题、内容、图标、两个按钮的对话框 **/
	protected AlertDialog showAlertDialog(String title, String message,
			int icon, String positiveText,
			DialogInterface.OnClickListener onPositiveClickListener,
			String negativeText,
			DialogInterface.OnClickListener onNegativeClickListener) {
		AlertDialog alertDialog = new AlertDialog.Builder(mContext)
				.setTitle(title).setMessage(message).setIcon(icon)
				.setPositiveButton(positiveText, onPositiveClickListener)
				.setNegativeButton(negativeText, onNegativeClickListener)
				.show();
		return alertDialog;
	}

	/** 含有标题、内容、两个按钮的对话框 **/
	protected AlertDialog showAlertDialog(String title, String message,
			String positiveText,
			DialogInterface.OnClickListener onPositiveClickListener,
			String negativeText,
			DialogInterface.OnClickListener onNegativeClickListener) {
		AlertDialog alertDialog = new AlertDialog.Builder(mContext)
				.setTitle(title).setMessage(message)
				.setPositiveButton(positiveText, onPositiveClickListener)
				.setNegativeButton(negativeText, onNegativeClickListener)
				.show();
		return alertDialog;
	}

	  /**
     * 弹出对话框
     * @param msg
     */
	public void showResultDialog() {
		if (dialog != null) {
			dialog.dismiss();
			dialog = null;
		}
		String msg = getString(R.string.httpLoading);
		dialog = CommonUtil.createLoadingDialog(mActivity, msg);
        dialog.show(); 
	}
	
	/**
	 * 消除对话框
	 */
	public void dismissResultDialog(){
		if (dialog != null) {
			dialog.dismiss();
			dialog = null;
		}
	}

}

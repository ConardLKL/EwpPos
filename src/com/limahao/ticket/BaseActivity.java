package com.limahao.ticket;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.limahao.ticket.config.Configs;
import com.limahao.ticket.utils.CommonUtil;
import com.umeng.analytics.MobclickAgent;

public abstract class BaseActivity extends BaseFragmentActivity implements
		OnClickListener {

	// private TipsToast tipsToast;
	private Toast tipToast;

	// 网络连接失败布局
	protected View layout_http_fail;
	protected View layout_body;

	/**
	 * 弹出框
	 */
	protected Dialog dialog = null;
	protected Configs configs = Configs.getAppConfig(this);
	// protected ImageLoader imageLoader = ImageLoader.getInstance();
	protected InputMethodManager imm = null;
	// 是否允许销毁
	private boolean allowDestroy = true;

	private View view;

	/**
	 * 画面初期化·控件相关联
	 */
	protected abstract void findView();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// 添加Activity到堆栈
		AppManager.getAppManager().addActivity(this);
		mApplication = (BaseApplication) getApplication();
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// 结束Activity&从堆栈中移除
		AppManager.getAppManager().finishActivity(this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && view != null) {
			view.onKeyDown(keyCode, event);
			if (!allowDestroy) {
				return false;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onResume() {
		super.onResume();
		// 友盟统计
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		// 友盟统计
		MobclickAgent.onPause(this);
	}

	public void setAllowDestroy(boolean allowDestroy) {
		this.allowDestroy = allowDestroy;
	}

	public void setAllowDestroy(boolean allowDestroy, View view) {
		this.allowDestroy = allowDestroy;
		this.view = view;
	}

	/**
	 * 弹出对话框
	 * 
	 * @param msg
	 */
	public void showResultDialog() {
		if (dialog != null) {
			dialog.dismiss();
			dialog = null;
		}
		String msg = getString(R.string.httpLoading);
		dialog = CommonUtil.createLoadingDialog(this, msg);
		dialog.show();
	}
	/**
	 * 消除对话框
	 */
	public void dismissResultDialog() {
		if (dialog != null) {
			dialog.dismiss();
			dialog = null;
		}
	}

	/** 短暂显示Toast提示(来自res) **/
	protected void showShortToast(int resId) {
		showShortToast(getString(resId));
	}

	/** 短暂显示Toast提示(来自String) **/
	protected void showShortToast(String text) {
		// 获取LayoutInflater对象
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// 由layout文件创建一个View对象
		View layout = inflater.inflate(R.layout.toast, null);
		// 实例化ImageView和TextView对象
		TextView textView = (TextView) layout.findViewById(R.id.toast_tv);
		if (tipToast == null) {
			tipToast = new Toast(this);
			textView.setText(text);
			tipToast.setView(layout);
		}else {
			textView = (TextView)tipToast.getView().findViewById(R.id.toast_tv);
			textView.setText(text);
		}
		tipToast.setDuration(Toast.LENGTH_SHORT);
		tipToast.show();
//		if (tipToast == null) {
//			tipToast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
//		} else {
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
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// 由layout文件创建一个View对象
		View layout = inflater.inflate(R.layout.toast, null);
		// 实例化ImageView和TextView对象
		TextView textView = (TextView) layout.findViewById(R.id.toast_tv);
		if (tipToast == null) {
			tipToast = new Toast(this);
			textView.setText(text);
			tipToast.setView(layout);
		}else {
			textView = (TextView)tipToast.getView().findViewById(R.id.toast_tv);
			textView.setText(text);
		}
		tipToast.setDuration(Toast.LENGTH_LONG);
		tipToast.show();
//		if (tipToast == null) {
//			tipToast = Toast.makeText(this, text, Toast.LENGTH_LONG);
//		} else {
//			tipToast.setText(text);
//			tipToast.setDuration(Toast.LENGTH_LONG);
//		}
//		tipToast.show();
	}

	protected void dismissToast() {
		if (tipToast != null) {
			tipToast.cancel();
		}

	}

	protected void showAlertDailog(String title, String msg, String btn1Name,
			android.content.DialogInterface.OnClickListener ls1) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(title);
		builder.setMessage(msg);
		builder.setCancelable(false);
		builder.show();
	}

	protected void showAlertDailog(String title, String msg, String btn1Name,
			String btn2Name,
			android.content.DialogInterface.OnClickListener ls1,
			android.content.DialogInterface.OnClickListener ls2) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(title);
		builder.setMessage(msg);
		builder.setCancelable(false);
		builder.setPositiveButton(btn1Name, ls1);
		builder.setNeutralButton(btn2Name, ls2);
		builder.show();
	}

	/*
	 * protected void showTips(int iconResId, int msgResId) { if (tipsToast !=
	 * null) { if (Build.VERSION.SDK_INT <
	 * Build.VERSION_CODES.ICE_CREAM_SANDWICH) { tipsToast.cancel(); } } else {
	 * tipsToast = TipsToast.makeText(getApplication().getBaseContext(),
	 * msgResId, TipsToast.LENGTH_SHORT); } tipsToast.show();
	 * tipsToast.setIcon(iconResId); tipsToast.setText(msgResId); }
	 */
	/**
	 * 这里适合用在每个activity中的ondestroy()方法中调用此方法， 便于避免重复滴出现多个Toast提示
	 */
	/*
	 * protected void dismissToast(){ if (tipsToast!=null) { tipsToast.cancel();
	 * } }
	 * 
	 * /*protected Handler handlerPhone =new Handler(){ public void
	 * handleMessage(android.os.Message msg) {
	 * 
	 * }; };
	 */

	/** 带有右进右出动画的退出 **/
	public void finish() {
		super.finish();
		// dismissToast();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}

	/**
	 * 初期化网络请求失败布局 子类放在setContentView后执行
	 */
	protected void initHttpFailView() {
		// 网络连接失败布局
		layout_http_fail = findViewById(R.id.layout_http_fail);
		if (layout_http_fail != null) {
			layout_http_fail.setOnClickListener(this);
			layout_http_fail.setVisibility(View.GONE);
		}
	}

	/**
	 * 网络请求失败后点击刷新隐藏view
	 */
	public void hideHttpFailView() {
		if (layout_http_fail != null) {
			layout_http_fail.setVisibility(View.GONE);
		}
	}

	/**
	 * 网络请求失败后显示view
	 */
	public void showHttpFailView() {
		if (layout_http_fail != null) {
			layout_http_fail.setVisibility(View.VISIBLE);
		}
	}

	// /** 默认退出 **/
	// protected void defaultFinish() {
	// super.finish();
	// }

	/*
	 * protected void showPopupWindow(final String [] title) { Rect frame = new
	 * Rect(); getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
	 * int state_heght = frame.top;// 状态栏的高度
	 * 
	 * int y = liLayoutCenter.getBottom() + state_heght; int x =
	 * getWindowManager().getDefaultDisplay().getWidth() / 2;
	 * 
	 * layoutBaseTitle = (LinearLayout) LayoutInflater.from(this).inflate(
	 * R.layout.popupdialog, null); listViewBaseTitle = (ListView)
	 * layoutBaseTitle .findViewById(R.id.listview_dialog_popupwindow);
	 * listViewBaseTitle.setAdapter(new ArrayAdapter<String>(this,
	 * R.layout.popuptext, R.id.textview_popupwindow_list_item, title));
	 * popupWindowBaseTitle = new PopupWindow(this);
	 * popupWindowBaseTitle.setBackgroundDrawable(new BitmapDrawable());
	 * popupWindowBaseTitle.setWidth(200);
	 * popupWindowBaseTitle.setHeight(LayoutParams.WRAP_CONTENT);
	 * popupWindowBaseTitle.setOutsideTouchable(true);
	 * popupWindowBaseTitle.setFocusable(true);
	 * popupWindowBaseTitle.setAnimationStyle(R.style.PopupAnimation);
	 * popupWindowBaseTitle.setContentView(layoutBaseTitle); //
	 * showAsDropDown会把里面的view作为参照物，所以要那满屏幕parent //
	 * popupWindow.showAsDropDown(findViewById(R.id.tv_title), x, 10);
	 * popupWindowBaseTitle.showAtLocation(liLayoutCenter, Gravity.LEFT |
	 * Gravity.TOP, (x - popupWindowBaseTitle.getWidth() / 2), y);//
	 * 需要指定Gravity，默认情况是center. listViewBaseTitle.setOnItemClickListener(new
	 * OnItemClickListener() {
	 * 
	 * @Override public void onItemClick(AdapterView<?> arg0, View arg1, int
	 * position, long arg3) { // TODO Auto-generated method stub if
	 * (null!=bookButton) { // MyLog.i("bookButton...不为kong......");
	 * 
	 * if (title[position].equals(getResources().
	 * getString(R.string.zheshmagazine_title_zsmagazine))) {
	 * bookButton.setVisibility(View.VISIBLE);
	 * lilayoutZsMagazine.setVisibility(View.VISIBLE);
	 * pagerPhoneResults.setVisibility(View.GONE);
	 * tabsPhoneResults.setVisibility(View.GONE); }else {
	 * bookButton.setVisibility(View.INVISIBLE);
	 * lilayoutZsMagazine.setVisibility(View.GONE);
	 * pagerPhoneResults.setVisibility(View.VISIBLE);
	 * tabsPhoneResults.setVisibility(View.VISIBLE); } }
	 * titleTextView.setText(title[position]); popupWindowBaseTitle.dismiss();
	 * popupWindowBaseTitle = null; } }); }
	 */

}

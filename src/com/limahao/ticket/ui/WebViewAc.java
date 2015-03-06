package com.limahao.ticket.ui;

import java.util.Timer;
import java.util.TimerTask;

import com.limahao.ticket.BaseActivity;
import com.limahao.ticket.R;
import com.limahao.ticket.config.Constants;
import com.limahao.ticket.config.Urls;
import com.limahao.ticket.log.DebugLog;
import com.limahao.ticket.view.MyWebChromeClient;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @Title: Help.java
 * 
 * @author email: <a href="cj@12pai.cn">陈杰</a>
 * @date 2014-8-19 上午9:22:36
 * @version :
 * @Description: 更多加载网页系列
 */
public class WebViewAc extends BaseActivity {
	private final String TAG = "WebViewAc";
	private final String HtmlTitle = "立马好杭州汽车票手机APP";
	private LinearLayout layoutBack;
	private WebView mWebView;
	private WebViewClient mwbClient;
	private Timer timer;
	private Handler mHandler;
	private String title = "";
	private String url = "";

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webviewac);
		initHttpFailView();
		findView();
		String tag = getIntent()
				.getStringExtra(Constants.ParamKey.Key_More_Tag);

		// 判断哪个页面
		if (Constants.TAGS.MORE_ABOUT.equals(tag)) {
			// 关于我们
			title = Constants.TAGS.MORE_ABOUT;
			url = Urls.MORE_ABOUT;
		} else if (Constants.TAGS.MORE_NOTICE.equals(tag)) {
			// 用户指南
			title = Constants.TAGS.MORE_NOTICE;
			url = Urls.MORE_NOTICE;
		} else if (Constants.TAGS.MORE_PROTOCOL.equals(tag)) {
			// 免责声明
			title = Constants.TAGS.MORE_PROTOCOL;
			url = Urls.MORE_PROTOCOL;
		}

		DebugLog.d(TAG, " title; " + title + " url; " + url);

		iniTitle(title);
		WebSettings settings = mWebView.getSettings();
		// 设置加载进来的页面自适应手机屏幕
		settings.setJavaScriptEnabled(true); // 支持js
		// settings.setUseWideViewPort(true); // 宽屏手机
		// //settings.setLoadWithOverviewMode(true);
		settings.setSupportZoom(false); // 缩放
		settings.setBuiltInZoomControls(false); // 缩放
		// settings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN); // 布局
		
		timer = new Timer();
		// 加载超时处理
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 1) {
					// 超时 当加载失败
					DebugLog.d(TAG, title + "handleMessage 加载超时 取消计时 停止加载  显示网络失败界面  dismissResultDialog 隐藏mWebView");
					mWebView.stopLoading();
					
					// 网页加载失败
					dismissResultDialog();
					mWebView.setVisibility(View.GONE);
					showHttpFailView();// 显示网络失败界面
				}
				super.handleMessage(msg);
			}
		};
		
		// webviewclient
		mwbClient = new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				DebugLog.d(TAG, "onPageStarted 隐藏请求失败界面 showResultDialog  开始计时");
				hideHttpFailView();// 隐藏请求失败界面
				mWebView.setVisibility(View.GONE);
				showResultDialog();
//				TimerTask tt = new TimerTask() {
//					@Override
//					public void run() {
//						// 时间到了加载未成功
//						Message msg = new Message();
//						msg.what = 1;
//						mHandler.sendMessage(msg);
//					}
//				};
//
//				timer.schedule(tt, 5000); // 5秒超时
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				view.loadUrl("javascript:window.handler.show(document.body.innerHTML);");
//				timer.cancel();
//				timer.purge();
				DebugLog.d(TAG, "onPageFinished 取消计时");
				super.onPageFinished(view, url);
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				DebugLog.d(TAG, "onReceivedError ");
				super.onReceivedError(view, errorCode, description, failingUrl);
			}
		};
		mWebView.setWebViewClient(mwbClient);
		
		// 根据网页title判断网页加载是否成功
		mWebView.setWebChromeClient(new WebChromeClient(){
			@Override
			public void onReceivedTitle(WebView view, String title) {
				super.onReceivedTitle(view, title);
				dismissResultDialog();
				// 网页加载成功
				if(HtmlTitle.equals(title.trim())){
					DebugLog.d(TAG, "onReceivedTitle 成功  |" + title);
					mWebView.setVisibility(View.VISIBLE);
				} else {
					// 网页加载失败
					DebugLog.d(TAG, "onReceivedTitle 失败  |" + title);
					mWebView.setVisibility(View.GONE);
					showHttpFailView();// 显示网络失败界面
				}
			}
		});
		mWebView.loadUrl(url);
	}

	/**
	 * 标题设置
	 */
	private void iniTitle(String title) {
		layoutBack = (LinearLayout) findViewById(R.id.include_left_layout);
		layoutBack.setVisibility(View.VISIBLE);
		layoutBack.setOnClickListener(this);
		LinearLayout center_layout = (LinearLayout) findViewById(R.id.include_center_layout);
		center_layout.setVisibility(View.VISIBLE);
		TextView center_tv = (TextView) findViewById(R.id.include_center_tv);
		center_tv.setVisibility(View.VISIBLE);
		center_tv.setText(title);
	}

	@Override
	public void onClick(View v) {
		if (v == layoutBack) {
			finish();
			// 点击刷新
		} else if (v == layout_http_fail) {
			mWebView.loadUrl(url);
		}
	}

	@Override
	protected void findView() {
		mWebView = (WebView) findViewById(R.id.webview_webview_web);
	}

}

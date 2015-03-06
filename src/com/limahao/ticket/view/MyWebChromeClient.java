package com.limahao.ticket.view;

import com.limahao.ticket.BaseActivity;
import com.limahao.ticket.R;
import com.limahao.ticket.log.DebugLog;

import android.app.Activity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;

public class MyWebChromeClient extends WebChromeClient {
	private final String TAG = "MyWebChromeClient";
	private BaseActivity activity;

	public MyWebChromeClient(BaseActivity activity) {
		this.activity = activity;
//		activity.showResultDialog();
	}

	@Override
	public void onProgressChanged(WebView view, int newProgress) {
		// pb=(ProgressBar)activity.findViewById(R.id.pb);
//		if (newProgress == 100) {
//			activity.dismissResultDialog();
//		} 
		super.onProgressChanged(view, newProgress);
	}

	@Override
	public void onReceivedTitle(WebView view, String title) {
		DebugLog.d(TAG, title);
		super.onReceivedTitle(view, title);
	}
	
	
}
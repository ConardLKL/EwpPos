package com.limahao.ticket.ui;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.limahao.ticket.BaseActivity;
import com.limahao.ticket.MainActivity;
import com.limahao.ticket.R;


/** 
 * @Title: SplashScreen.java 
 * 
 * @author email: <a href="cj@ewppay.cn">陈杰</a> 
 * @date 2014-07-28 上午9:21:49 
 * @version :
 * @Description: 
 */
public class SplashScreen extends BaseActivity {
	private final String TAG = "SplashScreen";
	private final  int SWITCH_MAINACTIVITY = 1000;
    private final  int SWITCH_GUIDACTIVITY = 1001;
    
	public Handler mHandler = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splashscreen);
		
		mHandler = new Handler(){
	        public void handleMessage(Message msg) {
	            switch(msg.what){
	            case SWITCH_MAINACTIVITY:
	        		dropToNextActivity(MainActivity.class, null);
	                finish();
	                break;
	            case SWITCH_GUIDACTIVITY:
	            	//设置引导页
	            	 dropToNextActivity(Guide.class, null);
		             finish();
	                break;
	            }
	            super.handleMessage(msg);
	        }
	    };
		
		// 初期化登录状态
		mApplication.initLogin();
		
		Thread loadThread = new Thread(new Runnable() {
			@Override
			public void run() {
				SharedPreferences preferences = getSharedPreferences("first_pref",MODE_PRIVATE);
				boolean isFirstStart = preferences.getBoolean("isFirstIn", true);
				if(isFirstStart)
		            mHandler.sendEmptyMessageDelayed(SWITCH_GUIDACTIVITY,1000);
		        else
		            mHandler.sendEmptyMessageDelayed(SWITCH_MAINACTIVITY,2000);
			}
		});
		loadThread.start();
	}


	
	@Override
	public void onClick(View v) {
	}

	@Override
	protected void findView() {
	}

}

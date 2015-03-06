package com.limahao.ticket.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.limahao.ticket.AppManager;
import com.limahao.ticket.BaseActivity;
import com.limahao.ticket.MainActivity;
import com.limahao.ticket.R;
import com.limahao.ticket.config.Constants;
import com.limahao.ticket.log.DebugLog;

public class AccountInfo extends BaseActivity {
	private final String TAG = "Account";
	private LinearLayout layoutBack;
	private TextView account_register_tv,account_register_date_tv,account_register_phone_tv;
	private RelativeLayout account_alter_password,accout_alter_phone;
	private TextView exit_login;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		DebugLog.i(TAG, "onCreate");
		setContentView(R.layout.account);
		iniTitle();
		findView();
		settext();
	}

	private void settext() {
		account_register_tv.setText(mApplication.getUserInfo().getPHONE());
		account_register_date_tv.setText(mApplication.getUserInfo().getREGDATE());
		account_register_phone_tv.setText(mApplication.getUserInfo().getPHONE());
	}

	private void iniTitle() {
		// TODO Auto-generated method stub
		layoutBack = (LinearLayout) findViewById(R.id.include_left_layout);
		layoutBack.setVisibility(View.VISIBLE);
		layoutBack.setOnClickListener(this);
		LinearLayout center_layout = (LinearLayout) findViewById(R.id.include_center_layout);
		center_layout.setVisibility(View.VISIBLE);
		TextView center_tv = (TextView) findViewById(R.id.include_center_tv);
		center_tv.setVisibility(View.VISIBLE);
		center_tv.setText(getString(R.string.include_account_txt));
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == layoutBack) {
			finish();
		}else if(v == exit_login){
			mApplication.LoginOut();
			MainActivity mainAct = (MainActivity)AppManager.getAppManager().getActivity(MainActivity.class);
			Message msg = new Message();
			msg.what = Constants.ActionCode.ACT_LOGIN_SWICH;
			mainAct.mHandler.dispatchMessage(msg); // 切换主界面登录状态
			finish();
		}else if(v == account_alter_password){
			startActivityForRet(AlterPwd.class, null, Constants.ActionCode.ACT_ALTER_PWD);
		}else if(v == accout_alter_phone){
			startActivityForRet(BindPhone.class, null, Constants.ActionCode.ACT_BIND_PHONE);
		}
	}

	@Override
	protected void findView() {
		// TODO Auto-generated method stub
		account_register_tv = (TextView) findViewById(R.id.bind_register_tv);
		account_register_date_tv = (TextView) findViewById(R.id.bind_register_date_tv);
		account_register_phone_tv = (TextView) findViewById(R.id.bind_register_phone_tv);
		
		accout_alter_phone = (RelativeLayout) findViewById(R.id.accout_alter_phone);
		accout_alter_phone.setOnClickListener(this);
		account_alter_password = (RelativeLayout) findViewById(R.id.account_alter_password);
		account_alter_password.setOnClickListener(this);
		exit_login = (TextView) findViewById(R.id.exit_login);
		exit_login.setOnClickListener(this);
	}
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK)
			return;
		MainActivity mainAct = (MainActivity)AppManager.getAppManager().getActivity(MainActivity.class);
		switch (requestCode) {
		   // 修改密码成功时返回 需重新登录
			case Constants.ActionCode.ACT_ALTER_PWD:
				mApplication.LoginOut();
				Message msg = new Message();
				msg.what = Constants.ActionCode.ACT_LOGIN_SWICH;
				mainAct.mHandler.dispatchMessage(msg); // 切换主界面登录状态
				finish();
				break;
				
		   // 绑定手机成功时返回	需重新登录
			case Constants.ActionCode.ACT_BIND_PHONE:
				mApplication.LoginOut();
				Message msg1 = new Message();
				msg1.what = Constants.ActionCode.ACT_LOGIN_SWICH;
				mainAct.mHandler.dispatchMessage(msg1); // 切换主界面登录状态
				finish();
				break;
		}
	}

}

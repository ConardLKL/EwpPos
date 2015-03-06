package com.limahao.ticket.ui;

import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.limahao.ticket.AppManager;
import com.limahao.ticket.BaseActivity;
import com.limahao.ticket.BaseAsyncTask;
import com.limahao.ticket.MainActivity;
import com.limahao.ticket.R;
import com.limahao.ticket.config.Constants;
import com.limahao.ticket.dao.TicketDao;
import com.limahao.ticket.entity.ItemUserInfo;
import com.limahao.ticket.entity.ResponseBase;
import com.limahao.ticket.entity.ResponseLogin;
import com.limahao.ticket.log.DebugLog;
import com.limahao.ticket.sign.MD5;
import com.limahao.ticket.utils.MatcherUtils;
import com.limahao.ticket.utils.Utility;

public class Login extends BaseActivity {
	private final String TAG = "Login";
	private LinearLayout layoutBack,right_layout;
	private TextView login_tv,forget_password_tv;
	private EditText phone_et,password_et;
	private String signpassword;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		DebugLog.i(TAG, "onCreate");
		setContentView(R.layout.login);
		iniTitle();
		findView();
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
		center_tv.setText(getString(R.string.include_login_txt));
		
		right_layout = (LinearLayout) findViewById(R.id.include_right_layout);
		right_layout.setVisibility(View.VISIBLE);
		right_layout.setOnClickListener(this);
		TextView right_tv = (TextView) findViewById(R.id.include_right_tv);
		right_tv.setVisibility(View.VISIBLE);
		right_tv.setText(getString(R.string.include_register_txt));
		
	}

	@Override
	public void onClick(View v) {
		if (v == right_layout) {
			// 注册界面
			startActivityForRet(Register.class, null, Constants.ActionCode.ACT_REGISTER);
		}else if(v == login_tv){
			if (isValidated()) {
				LoginAsy loginAsy = new LoginAsy(Login.this);
				loginAsy.execute();
			}
		}else if(v == layoutBack){
			if (imm !=null && imm.isActive()) {
				imm.hideSoftInputFromWindow(Login.this.getCurrentFocus().getWindowToken(), 
						InputMethodManager.HIDE_NOT_ALWAYS);
			}
			finish();
		}else if(v == forget_password_tv){
			// 忘记密码
			dropToNextActivity(ForgotPwd.class, null);
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK)
			return;
		MainActivity mainAct = (MainActivity)AppManager.getAppManager().getActivity(MainActivity.class);
		switch (requestCode) {
		   // 注册成功时返回 处于登录状态
			case Constants.ActionCode.ACT_REGISTER:
				Message msg = new Message();
				msg.what = Constants.ActionCode.ACT_LOGIN_SWICH;
				mainAct.mHandler.dispatchMessage(msg); // 切换主界面登录状态
				finish();
				break;
		}
	}
	
	/*
	 * 登陆校验
	 */
	private boolean isValidated() {
		boolean result = false;
		if (Utility.isNullOrEmpty(phone_et.getText().toString().trim())) { // 不为空
			showLongToast(getResources().getString(R.string.login_error_require_phone_number));
		} else if (!MatcherUtils.isMobileNO(phone_et.getText().toString().trim())) { // 手机号码格式
			showLongToast(getResources().getString(R.string.login_error_username));
		} else if (Utility.isNullOrEmpty(password_et.getText().toString().trim())) {
			showLongToast(getResources().getString(R.string.login_error_password));
		} else if (!MatcherUtils.isPasswordNo(password_et.getText().toString().trim())) { // 密码格式
			showLongToast(getResources().getString(R.string.login_error_password_require));
		}else {
			result = true;
		}
		return result;
	}
	
	@Override
	protected void findView() {
		// TODO Auto-generated method stub
		phone_et = (EditText) findViewById(R.id.phone_et);
		password_et = (EditText) findViewById(R.id.password_et);
		forget_password_tv = (TextView) findViewById(R.id.forget_password_tv);
		forget_password_tv.setOnClickListener(this);
		login_tv = (TextView) findViewById(R.id.user_login_tv);
		login_tv.setOnClickListener(this);
	}
	private class LoginAsy extends BaseAsyncTask {
		public LoginAsy(BaseActivity activity) {
			super(activity);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			showResultDialog();
		}

		@Override
		protected ResponseBase doInBackground(Void... params) {
			// TODO Auto-generated method stub
			TicketDao dao = new TicketDao(Login.this);
			HashMap<String, String> loginMap = new HashMap<String, String>();
			loginMap.put("regphone", phone_et.getText().toString().trim());
			signpassword = MD5.encode(password_et.getText().toString().trim());
			loginMap.put("password",signpassword);
			return dao.getLogininfo(loginMap) ;
		}

		@Override
		protected void onPostExecute(ResponseBase result) {
			super.onPostExecute(result);
			dismissResultDialog();
			if (result != null) {
				if (Constants.CD_SUCCESS.equals(result.getERR_INFO().getERROR_CODE())) {
					ItemUserInfo userInfo = ((ResponseLogin)result).getRET_INFO();
					userInfo.setPassword(signpassword); //密码也保存
					mApplication.UpdateUserInfo(userInfo);
					setResult(RESULT_OK); // 登录成功告诉父界面
					finish();
					showShortToast("登录成功");
				}
			}
		}

	}
}

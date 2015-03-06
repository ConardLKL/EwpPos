package com.limahao.ticket.ui;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.limahao.ticket.BaseActivity;
import com.limahao.ticket.BaseAsyncTask;
import com.limahao.ticket.R;
import com.limahao.ticket.config.Configs;
import com.limahao.ticket.config.Constants;
import com.limahao.ticket.dao.TicketDao;
import com.limahao.ticket.entity.ItemUserInfo;
import com.limahao.ticket.entity.ResponseBase;
import com.limahao.ticket.log.DebugLog;
import com.limahao.ticket.sign.MD5;
import com.limahao.ticket.utils.MatcherUtils;
import com.limahao.ticket.utils.Utility;
import com.limahao.ticket.view.CountTimer;
import com.limahao.ticket.view.MyAlertDialog;

public class Register extends BaseActivity {
	private final String TAG = "Register";
	private LinearLayout layoutBack;
	private TextView checked_code_txt, register_tv;
	private EditText register_phone_et, register_password_et, checked_code_et;
	private String msmvode;
	private String msmvodePhone; // 验证码对应的手机号
	private String signpassword;
	private CountTimer count;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		DebugLog.i(TAG, "Register");
		setContentView(R.layout.register);
		iniTitle();
		findView();
		// 获取最后一次保存的验证码
		msmvode = Configs.getAppConfig(this).get(
				Constants.ParamKey.Key_Message_Code);
		// 获取最后一次保存的验证码对应的手机号
		msmvodePhone = Configs.getAppConfig(this).get(
				Constants.ParamKey.Key_Message_Phone);
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
		center_tv.setText(getString(R.string.include_register_txt));
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == layoutBack) {
			if (imm != null && imm.isActive()) {
				imm.hideSoftInputFromWindow(Register.this.getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
			finish();
		} else if (v == register_tv) {
			if (isValidated()) {
				RegisterAsy registerAsy = new RegisterAsy(this);
				registerAsy.execute();
			}
		} else if (v == checked_code_txt) {
			if (isMsmValidated()) {
				RegisterMsmVodeAsy msmVodeasy = new RegisterMsmVodeAsy(this);
				msmVodeasy.execute();
			}
		}
	}

	@Override
	protected void findView() {
		// TODO Auto-generated method stub
		checked_code_txt = (TextView) findViewById(R.id.checked_code_txt);
		checked_code_txt.setOnClickListener(this);
		register_tv = (TextView) findViewById(R.id.user_register_tv);
		register_tv.setOnClickListener(this);

		register_phone_et = (EditText) findViewById(R.id.register_phone_et);
		register_password_et = (EditText) findViewById(R.id.register_password_et);
		checked_code_et = (EditText) findViewById(R.id.checked_code_et);
	}

	/*
	 * 获得短信校验
	 */
	private boolean isMsmValidated() {
		boolean result = false;
		if (Utility
				.isNullOrEmpty(register_phone_et.getText().toString().trim())) { // 不为空
			showLongToast(getResources().getString(
					R.string.login_error_require_phone_number));
		} else if (!MatcherUtils.isMobileNO(register_phone_et.getText()
				.toString().trim())) { // 手机号码格式
			showLongToast(getResources().getString(
					R.string.login_error_username));
		} else {
			result = true;
		}
		return result;
	}

	/*
	 * 校验
	 */
	private boolean isValidated() {
		boolean result = false;
		if (Utility
				.isNullOrEmpty(register_phone_et.getText().toString().trim())) { // 不为空
			showLongToast(getResources().getString(
					R.string.login_error_require_phone_number));
		} else if (!MatcherUtils.isMobileNO(register_phone_et.getText()
				.toString().trim())) { // 手机号码格式
			showLongToast(getResources().getString(
					R.string.login_error_username));
		} else if (Utility.isNullOrEmpty(register_password_et.getText()
				.toString().trim())) { // 密码空
			showLongToast(getResources().getString(
					R.string.login_error_password));
		} else if (!MatcherUtils.isPasswordNo(register_password_et.getText()
				.toString().trim())) { // 密码格式
			showLongToast(getResources().getString(
					R.string.login_error_password_require));
		} else if (checked_code_et.getText().toString().trim().equals("")) { // 验证码空
			showLongToast(getResources().getString(
					R.string.register_error_msm_null));
			// 验证码5-6位 
		} else if (!(checked_code_et.getText().toString().trim().length() == 6 || checked_code_et
				.getText().toString().trim().length() == 5)) { // 验证码为6位数字
			showLongToast(getResources().getString(
					R.string.register_error_msm_format));
			//  验证码错误 验证码和获取验证码的手机号配对
		} else if (Utility.isNullOrEmpty(msmvode)
				|| !(msmvode
						.equals(checked_code_et.getText().toString().trim()) && register_phone_et
						.getText().toString().trim().equals(msmvodePhone))) { // 验证码错误
			showLongToast(getResources().getString(R.string.register_error_msm));
		} else {
			result = true;
		}
		return result;
	}

	private class RegisterMsmVodeAsy extends BaseAsyncTask {
		public RegisterMsmVodeAsy(BaseActivity activity) {
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
			TicketDao dao = new TicketDao(Register.this);
			HashMap<String, String> msmMap = new HashMap<String, String>();
			msmMap.put("regphone", register_phone_et.getText().toString()
					.trim());
			msmMap.put("type", "new");
			return dao.getSmsvode(msmMap);
		}

		@Override
		protected void onPostExecute(ResponseBase result) {
			super.onPostExecute(result);
			dismissResultDialog();
			if (result != null) {
				if (Constants.CD_SUCCESS.equals(result.getERR_INFO()
						.getERROR_CODE())) {
					showLongToast(getString(R.string.message_code_send));
					msmvode = result.getERR_INFO().getERROR_MSG();
					// 保存验证码对应的手机号
					msmvodePhone = register_phone_et.getText().toString()
							.trim();
					checked_code_txt.setWidth(checked_code_txt.getWidth()); // 宽度保持不变
					count = new CountTimer(checked_code_txt, Configs.CODE_TIME,
							checked_code_txt.getText().toString(), "剩余%s");
					count.Start();
					// 保存验证码
					Configs.getAppConfig(mActivity).set(
							Constants.ParamKey.Key_Message_Code, msmvode);
					// 保存验证码对应的手机号
					Configs.getAppConfig(mActivity).set(
							Constants.ParamKey.Key_Message_Phone, msmvodePhone);
				}
			}
		}

	}

	private class RegisterAsy extends BaseAsyncTask {
		public RegisterAsy(BaseActivity activity) {
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
			TicketDao dao = new TicketDao(Register.this);
			HashMap<String, String> registerMap = new HashMap<String, String>();
			registerMap.put("regphone", register_phone_et.getText().toString()
					.trim());
			signpassword = MD5.encode(register_password_et.getText().toString()
					.trim());
			registerMap.put("password", signpassword);
			return dao.getRegister(registerMap);
		}

		@Override
		protected void onPostExecute(ResponseBase result) {
			super.onPostExecute(result);
			dismissResultDialog();
			if (result != null) {
				if (Constants.CD_SUCCESS.equals(result.getERR_INFO()
						.getERROR_CODE())) {
					ItemUserInfo userInfo = new ItemUserInfo();
					userInfo.setPHONE(register_phone_et.getText().toString()
							.trim());
					userInfo.setPassword(signpassword);

					Calendar c = Calendar.getInstance();
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					Date date = c.getTime();
					userInfo.setREGDATE(df.format(date));
					mApplication.UpdateUserInfo(userInfo);
					final MyAlertDialog alert = new MyAlertDialog(Register.this);
					alert.setMessage("您已经成功注册为立马好会员");
					alert.setOnYesclick(new View.OnClickListener() {

						@Override
						public void onClick(View arg0) {
							alert.dismiss();
						}
					});
					// 窗口消失触发事件
					alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
						@Override
						public void onDismiss(DialogInterface arg0) {
							setResult(Activity.RESULT_OK); // 成功返回父界面做跳转控制
							finish();
						}
					});
					alert.show();
				}
			}
		}

	}
}

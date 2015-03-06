package com.limahao.ticket.ui;

import java.util.HashMap;

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
import com.limahao.ticket.entity.ResponseBase;
import com.limahao.ticket.log.DebugLog;
import com.limahao.ticket.sign.MD5;
import com.limahao.ticket.utils.MatcherUtils;
import com.limahao.ticket.utils.Utility;
import com.limahao.ticket.view.CountTimer;

public class ForgotPwd extends BaseActivity {
	private final String TAG = "AlterPwd";
	private LinearLayout layoutBack;
	private TextView alter_tv;
	private TextView checked_code_txt;
	private EditText forgot_phone_et, forgot_new_password_et,
			forgot_ensure_password, forgot_checked_code_et;
	private CountTimer count;
	private String msmvode;
	private String msmvodePhone; // 验证码对应的手机号
	private String signnewpwd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		DebugLog.i(TAG, "onCreate");
		setContentView(R.layout.fogotpwd);
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
		center_tv.setText(getString(R.string.include_alterpwd_txt));
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == layoutBack) {
			if (imm != null && imm.isActive()) {
				imm.hideSoftInputFromWindow(ForgotPwd.this.getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
			finish();
		} else if (v == alter_tv) {
			if (isValidated()) {
				ForgotPwdAsy forgotPwdAsy = new ForgotPwdAsy(this);
				forgotPwdAsy.execute();
			}
		} else if (v == checked_code_txt) {
			if (isMsmValidated()) {
				ForgotPwdMsmVodeAsy msmVodeasy = new ForgotPwdMsmVodeAsy(this);
				msmVodeasy.execute();
			}
		}
	}

	@Override
	protected void findView() {
		alter_tv = (TextView) findViewById(R.id.user_alter_tv);
		alter_tv.setOnClickListener(this);

		checked_code_txt = (TextView) findViewById(R.id.alter_checked_code_txt);
		checked_code_txt.setOnClickListener(this);

		forgot_phone_et = (EditText) findViewById(R.id.forgot_phone_et);
		forgot_new_password_et = (EditText) findViewById(R.id.forgot_new_password_et);
		forgot_ensure_password = (EditText) findViewById(R.id.forgot_ensure_password);
		forgot_checked_code_et = (EditText) findViewById(R.id.forgot_checked_code_et);
	}

	/*
	 * 获得短信校验
	 */
	private boolean isMsmValidated() {
		boolean result = false;
		if (Utility.isNullOrEmpty(forgot_phone_et.getText().toString().trim())) { // 不为空
			showLongToast(getResources().getString(
					R.string.login_error_require_phone_number));
		} else if (!MatcherUtils.isMobileNO(forgot_phone_et.getText()
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
		if (Utility.isNullOrEmpty(forgot_phone_et.getText().toString().trim())) { // 手机号不为空
			showLongToast(getResources().getString(
					R.string.login_error_require_phone_number));
			// 手机号码格式
		} else if (!MatcherUtils.isMobileNO(forgot_phone_et.getText()
				.toString().trim())) {
			showLongToast(getResources().getString(
					R.string.login_error_username));
			// 新密码空
		} else if (Utility.isNullOrEmpty(forgot_new_password_et.getText()
				.toString().trim())) {
			showLongToast("新"
					+ getResources().getString(R.string.login_error_password));
			// 新密码格式
		} else if (!MatcherUtils.isPasswordNo(forgot_new_password_et.getText()
				.toString().trim())) {
			showLongToast("新"
					+ getResources().getString(
							R.string.login_error_password_require));
			// 确认密码空
		} else if (Utility.isNullOrEmpty(forgot_ensure_password.getText()
				.toString().trim())) {
			showLongToast("确认"
					+ getResources().getString(R.string.login_error_password));
			// 确认密码格式
		} else if (!MatcherUtils.isPasswordNo(forgot_ensure_password.getText()
				.toString().trim())) {
			showLongToast("确认"
					+ getResources().getString(
							R.string.login_error_password_require));
			// 密码一致
		} else if (!forgot_ensure_password.getText().toString().trim()
				.equals(forgot_new_password_et.getText().toString().trim())) {
			showLongToast(getResources().getString(R.string.alter_surepwd_txt));
			// 验证码空
		} else if (forgot_checked_code_et.getText().toString().trim()
				.equals("")) {
			showLongToast(getResources().getString(
					R.string.register_error_msm_null));
			// 验证码为5-6位数字
		} else if (!(forgot_checked_code_et.getText().toString().trim()
				.length() == 6 || forgot_checked_code_et.getText().toString()
				.trim().length() == 5)) {
			showLongToast(getResources().getString(
					R.string.register_error_msm_format));
			// 验证码错误
		} else if (Utility.isNullOrEmpty(msmvode)
				|| !(msmvode.equals(forgot_checked_code_et.getText().toString()
						.trim()) && forgot_phone_et.getText().toString()
						.trim().equals(msmvodePhone))) {
			showLongToast(getResources().getString(R.string.register_error_msm));
		} else {
			result = true;
		}
		return result;
	}

	private class ForgotPwdMsmVodeAsy extends BaseAsyncTask {
		public ForgotPwdMsmVodeAsy(BaseActivity activity) {
			super(activity);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected ResponseBase doInBackground(Void... params) {
			// TODO Auto-generated method stub
			TicketDao dao = new TicketDao(ForgotPwd.this);
			HashMap<String, String> msmMap = new HashMap<String, String>();
			msmMap.put("regphone", forgot_phone_et.getText().toString().trim());
			return dao.getSmsvode(msmMap);
		}

		@Override
		protected void onPostExecute(ResponseBase result) {
			super.onPostExecute(result);
			dismissResultDialog();
			if (result != null) {
				if (Constants.CD_SUCCESS.equals(result.getERR_INFO()
						.getERROR_CODE())) {
					msmvode = result.getERR_INFO().getERROR_MSG();
					showLongToast(getString(R.string.message_code_send));
					// 保存验证码对应的手机号
					msmvodePhone = forgot_phone_et.getText().toString().trim();
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

	private class ForgotPwdAsy extends BaseAsyncTask {
		public ForgotPwdAsy(BaseActivity activity) {
			super(activity);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showResultDialog();
		}

		@Override
		protected ResponseBase doInBackground(Void... params) {
			TicketDao dao = new TicketDao(ForgotPwd.this);
			HashMap<String, String> forgotMap = new HashMap<String, String>();
			forgotMap.put("regphone", forgot_phone_et.getText().toString()
					.trim());
			signnewpwd = MD5.encode(forgot_new_password_et.getText().toString()
					.trim());
			forgotMap.put("newpassword", signnewpwd);
			forgotMap.put("smscode", msmvode);
			return dao.getForgotPwd(forgotMap);
		}

		@Override
		protected void onPostExecute(ResponseBase result) {
			super.onPostExecute(result);
			dismissResultDialog();
			if (result != null) {
				if (Constants.CD_SUCCESS.equals(result.getERR_INFO()
						.getERROR_CODE())) {
					finish();
				}
			}
		}

	}
}

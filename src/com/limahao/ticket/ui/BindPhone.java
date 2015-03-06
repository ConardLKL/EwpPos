package com.limahao.ticket.ui;

import java.util.HashMap;

import android.app.Activity;
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
import com.limahao.ticket.utils.MatcherUtils;
import com.limahao.ticket.utils.Utility;
import com.limahao.ticket.view.CountTimer;

public class BindPhone extends BaseActivity {
	private final String TAG = "BindPhone";
	private LinearLayout layoutBack;
	private EditText bind_phone_et, alter_msm_vode_et;
	private TextView surebind, bind_sms_tv;
	private String msmvode;
	private String msmvodePhone; // 验证码对应的手机号
	private CountTimer count;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		DebugLog.i(TAG, "onCreate");
		setContentView(R.layout.bindphone);
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
		center_tv.setText(getString(R.string.include_bind_phone_txt));
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == layoutBack) {
			if (imm != null && imm.isActive()) {
				imm.hideSoftInputFromWindow(BindPhone.this.getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
			finish();
		} else if (v == surebind) {
			if (isValidated()) {
				BindNewPhoneVodeAsy bindnewphoneasy = new BindNewPhoneVodeAsy(
						this);
				bindnewphoneasy.execute();
			}
		} else if (v == bind_sms_tv) {
			if (isMsmValidated()) {
				BindNewMsmVodeAsy msmVodeasy = new BindNewMsmVodeAsy(this);
				msmVodeasy.execute();
			}
		}
	}

	/*
	 * 获得短信校验
	 */
	private boolean isMsmValidated() {
		boolean result = false;
		if (Utility.isNullOrEmpty(bind_phone_et.getText().toString().trim())) { // 不为空
			showLongToast(getResources().getString(
					R.string.login_error_require_phone_number));
		} else if (!MatcherUtils.isMobileNO(bind_phone_et.getText().toString()
				.trim())) { // 手机号码格式
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
		// 手机不为空
		if (Utility.isNullOrEmpty(bind_phone_et.getText().toString().trim())) {
			showLongToast(getResources().getString(
					R.string.login_error_require_phone_number));
			// 手机号码格式
		} else if (!MatcherUtils.isMobileNO(bind_phone_et.getText().toString()
				.trim())) {
			showLongToast(getResources().getString(
					R.string.login_error_username));
			// 验证码空
		} else if (alter_msm_vode_et.getText().toString().trim().equals("")) {
			showLongToast(getResources().getString(
					R.string.register_error_msm_null));
			// 验证码为5-6位数字
		} else if (!(alter_msm_vode_et.getText().toString().trim().length() == 6 || alter_msm_vode_et
				.getText().toString().trim().length() == 5)) {
			showLongToast(getResources().getString(
					R.string.register_error_msm_format));
			// 验证码错误 验证码和获取验证码的手机号配对
		} else if (Utility.isNullOrEmpty(msmvode)
				|| !(msmvode.equals(alter_msm_vode_et.getText().toString()
						.trim()) && bind_phone_et.getText().toString().trim()
						.equals(msmvodePhone))) {
			showLongToast(getResources().getString(R.string.register_error_msm));
		} else {
			result = true;
		}
		return result;
	}

	@Override
	protected void findView() {
		bind_sms_tv = (TextView) findViewById(R.id.bind_sms_tv);
		bind_sms_tv.setOnClickListener(this);
		surebind = (TextView) findViewById(R.id.bind_phone_sure);
		surebind.setOnClickListener(this);
		bind_phone_et = (EditText) findViewById(R.id.bind_new_phone_et);
		alter_msm_vode_et = (EditText) findViewById(R.id.bind_msm_vode_et);
	}

	private class BindNewMsmVodeAsy extends BaseAsyncTask {
		public BindNewMsmVodeAsy(BaseActivity activity) {
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
			TicketDao dao = new TicketDao(BindPhone.this);
			HashMap<String, String> bindphoneMap = new HashMap<String, String>();
			bindphoneMap.put("newphone", bind_phone_et.getText().toString()
					.trim());// 修改手机号
			bindphoneMap.put("regphone", mApplication.getUserInfo().getPHONE()); // 原手机号
			return dao.getSmsvode(bindphoneMap);
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
					msmvodePhone = bind_phone_et.getText().toString().trim();
					bind_sms_tv.setWidth(bind_sms_tv.getWidth()); // 宽度保持不变
					count = new CountTimer(bind_sms_tv, Configs.CODE_TIME,
							bind_sms_tv.getText().toString(), "剩余%s");
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

	private class BindNewPhoneVodeAsy extends BaseAsyncTask {
		public BindNewPhoneVodeAsy(BaseActivity activity) {
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
			TicketDao dao = new TicketDao(BindPhone.this);
			HashMap<String, String> bindnewMap = new HashMap<String, String>();
			bindnewMap.put("newphone", bind_phone_et.getText().toString()
					.trim());
			bindnewMap.put("smscode", msmvode);
			bindnewMap.put("regphone", mApplication.getUserInfo().getPHONE());
			bindnewMap
					.put("password", mApplication.getUserInfo().getPassword());
			return dao.getBindNewPhone(bindnewMap);
		}

		@Override
		protected void onPostExecute(ResponseBase result) {
			super.onPostExecute(result);
			dismissResultDialog();
			if (result != null) {
				if (Constants.CD_SUCCESS.equals(result.getERR_INFO()
						.getERROR_CODE())) {
					// 修改密码，绑定手机后帮助用户减少一次输入手机号的操作
					mApplication.getParams().put(
							Constants.ParamKey.Key_Login_Phone,
							bind_phone_et.getText().toString().trim());
					mApplication.LoginOut(); // 重新登录
					setResult(Activity.RESULT_OK); // 成功返回父界面做跳转控制
					// 隐去键盘
					if (imm != null && imm.isActive()) {
						imm.hideSoftInputFromWindow(BindPhone.this
								.getCurrentFocus().getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
					}
					finish();
					showShortToast(result.getERR_INFO().getERROR_MSG());
				}
			}
		}

	}
}

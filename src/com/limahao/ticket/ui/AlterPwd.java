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
import com.limahao.ticket.config.Constants;
import com.limahao.ticket.dao.TicketDao;
import com.limahao.ticket.entity.ItemUserInfo;
import com.limahao.ticket.entity.ResponseBase;
import com.limahao.ticket.log.DebugLog;
import com.limahao.ticket.sign.MD5;
import com.limahao.ticket.utils.MatcherUtils;
import com.limahao.ticket.utils.Utility;

public class AlterPwd extends BaseActivity {
	private final String TAG = "AlterPwd";
	private LinearLayout layoutBack;
	private EditText oldpwd, newpwd, surepwd;
	private TextView surealter;
	private String signpwd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		DebugLog.i(TAG, "onCreate");
		setContentView(R.layout.alterpwd);
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
		center_tv.setText(getString(R.string.include_alter_password_txt));
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == layoutBack) {
			if (imm != null && imm.isActive()) {
				imm.hideSoftInputFromWindow(AlterPwd.this.getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
			finish();
		} else if (v == surealter) {
			if (isValidated()) {
				AlterPwdAsy alterpwd = new AlterPwdAsy(this);
				alterpwd.execute();
			}
		}
	}

	@Override
	protected void findView() {
		oldpwd = (EditText) findViewById(R.id.alter_old_pwd_et);
		newpwd = (EditText) findViewById(R.id.alter_new_pwd_et);
		surepwd = (EditText) findViewById(R.id.alter_pwd_sure_et);
		surealter = (TextView) findViewById(R.id.alter_pwd_sure);
		surealter.setOnClickListener(this);
	}

	/*
	 * 登陆校验
	 */
	private boolean isValidated() {
		boolean result = false;
		// 旧密码空
		if (Utility.isNullOrEmpty(oldpwd.getText().toString().trim())) {
			showLongToast("旧" + getResources().getString(
					R.string.login_error_password));
			// 旧密码格式
		} else if (!MatcherUtils.isPasswordNo(oldpwd.getText().toString()
				.trim())) {
			showLongToast("旧" + getResources().getString(
					R.string.login_error_password_require));
			// 新密码空
		} else if (Utility.isNullOrEmpty(newpwd.getText().toString().trim())) {
			showLongToast("新" + getResources().getString(
					R.string.login_error_password));
			// 新密码格式
		} else if (!MatcherUtils.isPasswordNo(newpwd.getText().toString()
				.trim())) {
			showLongToast("新" + getResources().getString(
					R.string.login_error_password_require));
			// 确认密码
		} else if (Utility.isNullOrEmpty(surepwd.getText().toString().trim())) {
			showLongToast("确认" + getResources().getString(
					R.string.login_error_password));
			// 确认密码格式
		} else if (!MatcherUtils.isPasswordNo(surepwd.getText().toString()
				.trim())) {
			showLongToast("确认" +getResources().getString(
					R.string.login_error_password_require));
			// 密码不一致
		} else if (!newpwd.getText().toString().trim()
				.equals(surepwd.getText().toString().trim())) {
			showLongToast(getResources().getString(R.string.alter_surepwd_txt));
			// 旧密码不正确
		} /*else if (!MD5.encode(oldpwd.getText().toString().trim()).equals(
				mApplication.getUserInfo().getPassword())) {
			showLongToast(getResources().getString(R.string.alter_oldpwd_txt));
		}*/ else {
			result = true;
		}
		return result;
	}

	private class AlterPwdAsy extends BaseAsyncTask {
		public AlterPwdAsy(BaseActivity activity) {
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
			TicketDao dao = new TicketDao(AlterPwd.this);
			HashMap<String, String> alterpwdmap = new HashMap<String, String>();
			alterpwdmap.put("regphone", mApplication.getUserInfo().getPHONE());
			alterpwdmap.put("password", MD5.encode(oldpwd.getText().toString().trim()));
			signpwd = MD5.encode(newpwd.getText().toString().trim());
			alterpwdmap.put("newpassword", signpwd);
			return dao.getAlterPwd(alterpwdmap);
		}

		@Override
		protected void onPostExecute(ResponseBase result) {
			super.onPostExecute(result);
			dismissResultDialog();
			if (result != null) {
				if (Constants.CD_SUCCESS.equals(result.getERR_INFO()
						.getERROR_CODE())) {
					// 修改密码，绑定手机后帮助用户减少一次输入手机号的操作
					mApplication.getParams().put(Constants.ParamKey.Key_Login_Phone, mApplication.getUserInfo().getPHONE());
					mApplication.LoginOut(); //重新登录
					setResult(Activity.RESULT_OK); // 成功返回父界面做跳转控制
					// 隐去键盘
					if (imm != null && imm.isActive()) {
						imm.hideSoftInputFromWindow(AlterPwd.this.getCurrentFocus()
								.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
					}
					finish();
					showShortToast(result.getERR_INFO().getERROR_MSG());
				}
			}
		}

	}
}

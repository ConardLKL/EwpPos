package com.limahao.ticket.ui;

import java.util.HashMap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.limahao.ticket.BaseActivity;
import com.limahao.ticket.BaseAsyncTask;
import com.limahao.ticket.R;
import com.limahao.ticket.config.Constants;
import com.limahao.ticket.dao.TicketDao;
import com.limahao.ticket.entity.ItemUserInfo;
import com.limahao.ticket.entity.ResponseBase;
import com.limahao.ticket.entity.ResponseLogin;
import com.limahao.ticket.log.DebugLog;
import com.limahao.ticket.utils.MatcherUtils;
import com.limahao.ticket.utils.Utility;

public class PersonInfo extends BaseActivity {
	private final String TAG = "PersonInfo";
	private LinearLayout layoutBack, right_layout;
	private EditText person_name_txt_et, person_id_card_et;
	private TextView person_register_phone_tv, person_sex_txt_et;
	private ImageView right_iv, right_sure_iv;
	private final String[] sex = { "男", "女" };
	private final String[] sexVal = { "male", "female" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		DebugLog.i(TAG, "onCreate");
		setContentView(R.layout.personinfo);
		iniTitle();
		findView();
		settext();
	}

	private void settext() {
		// TODO Auto-generated method stub
		person_name_txt_et.setText(mApplication.getUserInfo().getNAME());
		person_id_card_et.setText(mApplication.getUserInfo().getIDNUMBER());
		if (mApplication.getUserInfo().getSEX().equals(sexVal[0])) {
			person_sex_txt_et.setText(sex[0]); // 男
		} else if (mApplication.getUserInfo().getSEX().equals(sexVal[1])) {
			person_sex_txt_et.setText(sex[1]); // 女
		} else { // 空
			person_sex_txt_et.setText("");
		}
		person_register_phone_tv.setText(mApplication.getUserInfo().getPHONE());
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
		center_tv.setText(getString(R.string.include_personinfo_txt));

		right_layout = (LinearLayout) findViewById(R.id.include_right_layout);
		right_layout.setVisibility(View.VISIBLE);
		right_layout.setOnClickListener(this);
		right_iv = (ImageView) findViewById(R.id.include_right_img);
		right_iv.setVisibility(View.VISIBLE);
		right_sure_iv = (ImageView) findViewById(R.id.include_right_sure_img);
	}

	/*
	 * 校验
	 */
	private boolean isValidated() {
		boolean result = false;
		
		// 名字不能为空
		if(Utility.isNullOrEmpty(person_name_txt_et.getText().toString()
				.trim())){
			showLongToast(getResources().getString(
					R.string.person_error_name_require));
			// 性别不能为空
		} else if(Utility.isNullOrEmpty(person_sex_txt_et.getText().toString()
					.trim())){
				showLongToast(getResources().getString(
						R.string.person_error_sex_require));
			// 身份证不能为空
		} else if (Utility.isNullOrEmpty(person_id_card_et.getText().toString()
				.trim())) {
			showLongToast(getResources().getString(
					R.string.person_error_id_card_require));
			
			// 身份证有输入的时候验证格式
		} else if (!MatcherUtils.ischeckIdCard(person_id_card_et.getText()
						.toString().trim())) {
			showLongToast(getResources().getString(
					R.string.person_error_id_card));
		} else {
			result = true;
		}
		return result;
	}

	@Override
	public void onClick(View v) {
		if (v == layoutBack) {
			if (imm != null && imm.isActive()) {
				imm.hideSoftInputFromWindow(PersonInfo.this.getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
			finish();
		} else if (v == right_layout) {
			if (right_iv.getVisibility() == View.VISIBLE) {
				right_iv.setVisibility(View.GONE);
				right_sure_iv.setVisibility(View.VISIBLE);
				person_name_txt_et.setSelection(person_name_txt_et.getText().length());
				person_name_txt_et.setEnabled(true);
				person_sex_txt_et.setEnabled(true);
				person_id_card_et.setEnabled(true);
			} else if (right_sure_iv.getVisibility() == View.VISIBLE) {
				if (isValidated()) {
					PersonInfoAsy asy = new PersonInfoAsy(this);
					asy.execute();
				}
			}
		} else if (v == person_sex_txt_et) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					PersonInfo.this);
			builder.setIcon(R.drawable.include_center_img);
			builder.setTitle("请选择");
//			builder.setCustomTitle(View.inflate(PersonInfo.this, R.layout.alert_title, null));
			builder.setItems(sex, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					person_sex_txt_et.setText(sex[which]);
				}
			});
			builder.show();
		}
	}

	@Override
	protected void findView() {
		// TODO Auto-generated method stub
		person_name_txt_et = (EditText) findViewById(R.id.person_name_txt_et);
		person_sex_txt_et = (TextView) findViewById(R.id.person_sex_txt_et);
		person_sex_txt_et.setOnClickListener(this);
		person_id_card_et = (EditText) findViewById(R.id.person_id_card_et);
		person_register_phone_tv = (TextView) findViewById(R.id.person_register_phone_tv);
	}

	private class PersonInfoAsy extends BaseAsyncTask {
		public PersonInfoAsy(BaseActivity activity) {
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
			TicketDao dao = new TicketDao(PersonInfo.this);
			HashMap<String, String> personinfomap = new HashMap<String, String>();
			personinfomap
					.put("regphone", mApplication.getUserInfo().getPHONE());
			personinfomap.put("password", mApplication.getUserInfo()
					.getPassword());
			personinfomap.put("name", person_name_txt_et.getText().toString()
					.trim());
			if (person_sex_txt_et.getText().toString().trim().equals(sex[0])) {
				personinfomap.put("sex", sexVal[0]); // 男
			} else if (person_sex_txt_et.getText().toString().trim()
					.equals(sex[1])) {
				personinfomap.put("sex", sexVal[1]); // 女
			} else { // 空
				personinfomap.put("sex", "");
			}
			personinfomap.put("idnumber", person_id_card_et.getText()
					.toString().trim());
			return dao.getPerson(personinfomap);
		}

		@Override
		protected void onPostExecute(ResponseBase result) {
			super.onPostExecute(result);
			dismissResultDialog();
			if (result != null) {
				if (Constants.CD_SUCCESS.equals(result.getERR_INFO()
						.getERROR_CODE())) {
					showLongToast(result.getERR_INFO().getERROR_MSG());
					person_name_txt_et.setEnabled(false);
					person_sex_txt_et.setEnabled(false);
					person_id_card_et.setEnabled(false);
					right_iv.setVisibility(View.VISIBLE);
					right_sure_iv.setVisibility(View.GONE);
					ResponseLogin responseLogin = (ResponseLogin) result;
					ItemUserInfo userInfo = responseLogin.getRET_INFO();
					person_name_txt_et.setText(responseLogin.getRET_INFO()
							.getNAME());
					if (responseLogin.getRET_INFO().getSEX().equals(sexVal[0])) {
						person_sex_txt_et.setText(sex[0]); //男
					} else if (responseLogin.getRET_INFO().getSEX()
							.equals(sexVal[1])) {
						person_sex_txt_et.setText(sex[1]); //女
					} else { // 空
						person_sex_txt_et.setText("");
					}
					person_id_card_et.setText(responseLogin.getRET_INFO()
							.getIDNUMBER());
					person_register_phone_tv.setText(responseLogin
							.getRET_INFO().getPHONE());
					mApplication.UpdateUserInfo(userInfo);
				} else {
					showLongToast(result.getERR_INFO().getERROR_MSG());
				}
			}
		}

	}
}

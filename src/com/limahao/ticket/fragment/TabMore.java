package com.limahao.ticket.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.style.BulletSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.limahao.ticket.BaseFragment;
import com.limahao.ticket.R;
import com.limahao.ticket.config.Constants;
import com.limahao.ticket.log.DebugLog;
import com.limahao.ticket.ui.ConversationActivity;
import com.limahao.ticket.ui.WebViewAc;
import com.limahao.ticket.utils.CommonUtil;
import com.limahao.ticket.view.MyAlertDialog;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;

public class TabMore extends BaseFragment {
	private final String TAG = "TabMore";
	private TextView tv_version;
	private RelativeLayout layout_customer_server;
	private RelativeLayout layout_about;
	private RelativeLayout layout_notice;
	private RelativeLayout layout_protocol;
	private RelativeLayout umeng_update, umeng_feek;

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		DebugLog.i(TAG, "onAttach");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		DebugLog.i(TAG, "onCreate");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateView(inflater, container, savedInstanceState);
		mView = inflater.inflate(R.layout.tab_more, container, false);
		inittitle();
		findViewById();
		setListener();
		init();
		return mView;
	}

	private void inittitle() {
		// TODO Auto-generated method stub
		LinearLayout center_layout = (LinearLayout) findViewById(R.id.include_center_layout);
		center_layout.setVisibility(View.VISIBLE);
		TextView center_tv = (TextView) findViewById(R.id.include_center_tv);
		center_tv.setText(getActivity().getResources().getString(
				R.string.tabs_more));
		center_tv.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v) {
		if (v == layout_customer_server) {
			if (!CommonUtil.isSimExist(mContext)) {
				showLongToast(getResources().getString(R.string.simisNull));
			} else {
				// 提示未支付是否离开
				final MyAlertDialog  alert = new MyAlertDialog(mActivity,
						MyAlertDialog.Mode_Yes_No);
				alert.setMessage("确认拨打客服？");
				alert.setOnYesclick(new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri
								.parse("tel:"
										+ getResources().getString(
												R.string.contact_number)));
						// 启动
						startActivity(phoneIntent);
						alert.dismiss();
					}
				});
				alert.show();
			}
		} else if (v == umeng_update) {
			// 更新
			UmengUpdateAgent.setUpdateOnlyWifi(false); // 目前我们默认在Wi-Fi接入情况下才进行自动提醒。如需要在其他网络环境下进行更新自动提醒，则请添加该行代码
			UmengUpdateAgent.setUpdateAutoPopup(false);
			UmengUpdateAgent.setUpdateListener(updateListener);
			UmengUpdateAgent.forceUpdate(getActivity());
		} else if (v == umeng_feek) {
			// FeedbackAgent agent = new FeedbackAgent(getActivity());
			// agent.startFeedbackActivity();
			startActivity(ConversationActivity.class, null);
		} else if (v == layout_about) {
			// 关于我们
			Bundle bd = new Bundle();
			bd.putString(Constants.ParamKey.Key_More_Tag,
					Constants.TAGS.MORE_ABOUT);
			startActivity(WebViewAc.class, bd);
		} else if (v == layout_notice) {
			// 用户指南
			Bundle bd = new Bundle();
			bd.putString(Constants.ParamKey.Key_More_Tag,
					Constants.TAGS.MORE_NOTICE);
			startActivity(WebViewAc.class, bd);
		} else if (v == layout_protocol) {
			// 免责声明
			Bundle bd = new Bundle();
			bd.putString(Constants.ParamKey.Key_More_Tag,
					Constants.TAGS.MORE_PROTOCOL);
			startActivity(WebViewAc.class, bd);
		}
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		umeng_update = (RelativeLayout) findViewById(R.id.umeng_update);
		umeng_feek = (RelativeLayout) findViewById(R.id.umeng_feek);

		tv_version = (TextView) findViewById(R.id.tv_version);
		layout_customer_server = (RelativeLayout) findViewById(R.id.layout_customer_server);
		layout_about = (RelativeLayout) findViewById(R.id.layout_about);
		layout_notice = (RelativeLayout) findViewById(R.id.layout_notice);
		layout_protocol = (RelativeLayout) findViewById(R.id.layout_protocol);
	}

	@Override
	protected void setListener() {
		layout_customer_server.setOnClickListener(this);
		umeng_update.setOnClickListener(this);
		umeng_feek.setOnClickListener(this);
		layout_about.setOnClickListener(this);
		layout_notice.setOnClickListener(this);
		layout_protocol.setOnClickListener(this);
	}

	@Override
	protected void init() {
		// 显示版本号
		tv_version.setText("V" + mApplication.getPackageInfo().versionName);
	}

	UmengUpdateListener updateListener = new UmengUpdateListener() {
		@Override
		public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
			switch (updateStatus) {
			case 0: // has update
				UmengUpdateAgent.showUpdateDialog(getActivity(), updateInfo);
				break;
			case 1: // has no update
//				Toast.makeText(getActivity(), "当前版本已经是最新版本", Toast.LENGTH_SHORT)
//						.show();
				showShortToast("当前版本已经是最新版本");
				break;
			/*
			 * case 2: // none wifi Toast.makeText(mActivity,
			 * "没有wifi连接， 只在wifi下更新", Toast.LENGTH_SHORT) .show(); break;
			 */
			case 3: // time out
//				Toast.makeText(getActivity(), "网络连接超时", Toast.LENGTH_SHORT)
//						.show();
				showShortToast("网络连接超时");
				break;
			case 4: // is updating
				/*
				 * Toast.makeText(mContext, "正在下载更新...", Toast.LENGTH_SHORT)
				 * .show();
				 */
				break;
			}

		}
	};
}

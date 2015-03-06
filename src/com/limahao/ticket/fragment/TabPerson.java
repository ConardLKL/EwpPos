package com.limahao.ticket.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.limahao.ticket.BaseFragment;
import com.limahao.ticket.R;
import com.limahao.ticket.config.Constants;
import com.limahao.ticket.log.DebugLog;
import com.limahao.ticket.ui.AccountInfo;
import com.limahao.ticket.ui.PersonInfo;
import com.limahao.ticket.ui.TicketOrder;

public class TabPerson extends BaseFragment {
	private final String TAG = "TabPerson";
	private RelativeLayout myinfo,myaccount,myticket_order;
	private View persion_iv_msg_notice;
	
	// 界面状态控制 改方法只在MainActivity中会被调用
	public Handler mHandler;
	
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
		mView = inflater.inflate(R.layout.tab_person, container, false);
		inititle();
		findViewById();
		setListener();
		init();
		return mView;
	}
	private void inititle() {
		LinearLayout center_layout = (LinearLayout) findViewById(R.id.include_center_layout);
		center_layout.setVisibility(View.VISIBLE);
		TextView center_tv = (TextView) findViewById(R.id.include_center_tv);
		center_tv.setVisibility(View.VISIBLE);
		center_tv.setText(getActivity().getResources().getString(R.string.include_person_txt));
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == myinfo) {
			startActivity(PersonInfo.class,null);
		}else if(v == myaccount){
			startActivity(AccountInfo.class, null);
		}else if(v == myticket_order){
			startActivity(TicketOrder.class,null);
		}
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		myinfo = (RelativeLayout) findViewById(R.id.myinfo);
		myaccount = (RelativeLayout) findViewById(R.id.myaccount);
		myticket_order = (RelativeLayout) findViewById(R.id.myticket_order);
		persion_iv_msg_notice = findViewById(R.id.persion_iv_msg_notice);
	}

	@Override
	protected void setListener() {
		// TODO Auto-generated method stub
		myticket_order.setOnClickListener(this);
		myinfo.setOnClickListener(this);
		myaccount.setOnClickListener(this);
	}
	
	/**
	 * 初期化消息通知图标
	 */
	private void initNotice() {
		// 处于登录状态并且有通知消息则展示消息图标，否则隐藏
		if (mApplication.isLogin() && mApplication.isNotice()) {
			persion_iv_msg_notice.setVisibility(View.VISIBLE);
		} else {
			persion_iv_msg_notice.setVisibility(View.GONE);
		}
	}
	@Override
	protected void init() {
		initNotice();
		// 界面状态控制 
		mHandler = new Handler() {
			@Override
			public void dispatchMessage(Message msg) {
				super.dispatchMessage(msg);
				switch (msg.what) {
					// 有消息通知
					case Constants.ActionCode.ACT_MESSAGE_NOTICE:
						// 这里只做状态更新 不需要设置
						persion_iv_msg_notice.setVisibility(View.VISIBLE);
						break;
					// 取消消息通知
					case Constants.ActionCode.ACT_MESSAGE_CANCEL:
						// 这里只做状态更新 不需要设置
						persion_iv_msg_notice.setVisibility(View.GONE);
						break;
					default:
						break;
				}
			}
		};
	}
	
}

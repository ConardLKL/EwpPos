package com.limahao.ticket.ui;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.limahao.ticket.BaseActivity;
import com.limahao.ticket.R;
import com.limahao.ticket.config.Constants;
import com.limahao.ticket.view.CountTimer;

/**
 * @Title: TicketQueryDetail.java
 * 
 * @author email: <a href="cj@ewppay.com">陈杰</a>
 * @date 2014-8-11 下午1:29:28
 * @version :
 * @Description: 支付成功
 */
public class TicketPaySuccesss extends BaseActivity {
	private final String TAG = "TicketPaySuccesss";
	private LinearLayout layoutBack;

	private TextView tvDetail;
	private TextView tvTime;
	private final long time = 6 * 1000; // 6秒
	private CountTimer count;
	private Handler mHandler = null;
	private Runnable run = null;
	private  String orderNo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ticket_pay_success);
		iniTitle();
		findView();
		init();
	}

	/**
	 * 标题设置
	 */
	private void iniTitle() {
		layoutBack = (LinearLayout) findViewById(R.id.include_left_layout);
		layoutBack.setVisibility(View.VISIBLE);
		layoutBack.setOnClickListener(this);
		LinearLayout center_layout = (LinearLayout) findViewById(R.id.include_center_layout);
		center_layout.setVisibility(View.VISIBLE);
		TextView center_tv = (TextView) findViewById(R.id.include_center_tv);
		center_tv.setVisibility(View.VISIBLE);
		center_tv.setText(getString(R.string.include_tick_pay_success_txt));
	}

	@Override
	protected void findView() {
		tvDetail = (TextView) findViewById(R.id.ticket_pay_success_tv_detail);
		tvDetail.setOnClickListener(this);
		tvTime = (TextView) findViewById(R.id.ticker_pay_success_tv_time);
	}

	private void init() {
		orderNo = getIntent().getStringExtra(Constants.ParamKey.Key_Ticket_Order_No);
		
		// 剩余跳转时间 5秒
		count = new CountTimer(tvTime, time, String.format(
				getString(R.string.ticker_pay_success_time), "0"),
				getString(R.string.ticker_pay_success_time));
		count.Start();

		run = new Runnable() {
			
			@Override
			public void run() {
				Bundle bd = new Bundle();
				bd.putString(Constants.ParamKey.Key_Ticket_Order_No, orderNo); // 订单号
				dropToNextActivity(TicketOrderSuccesDetial.class, bd); //详情界面
				finish();
			}
		};
		// 剩余跳转时间 5秒
		mHandler = new Handler();
		mHandler.postDelayed(run, time);
	}

	@Override
	public void onClick(View v) {
		if (v == layoutBack) {
			// 取消自动跳转
			if(mHandler != null){
				mHandler.removeCallbacks(run);
			}
			finish();
		} else if (v == tvDetail) {
			// 取消自动跳转
			if(mHandler != null){
				mHandler.removeCallbacks(run);
			}
			Bundle bd = new Bundle();
			bd.putString(Constants.ParamKey.Key_Ticket_Order_No, orderNo); // 订单号
			dropToNextActivity(TicketOrderSuccesDetial.class, bd); //详情界面
			finish();
		}
	}
}

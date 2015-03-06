package com.limahao.ticket.ui;

import java.text.NumberFormat;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.limahao.ticket.BaseActivity;
import com.limahao.ticket.BaseAsyncTask;
import com.limahao.ticket.R;
import com.limahao.ticket.config.Constants;
import com.limahao.ticket.dao.TicketDao;
import com.limahao.ticket.entity.ResponseBase;
import com.limahao.ticket.entity.ResponseTickerOrderInfo;
import com.limahao.ticket.log.DebugLog;
import com.limahao.ticket.utils.Utility;
import com.limahao.ticket.view.CountTimer;
import com.limahao.ticket.view.MyAlertDialog;

public class TicketOrderPayDetial extends BaseActivity {
	private final String TAG = "TicketOrderPayDetial";
	private LinearLayout left_layout;
	private TextView order_detail_pay_tv_buy;
	private TextView pay_detail_phone, pay_datial_remain_date, pay_datial_date;
	private TextView pay_detial_tv_start, pay_detial_tv_end;
	private TextView pay_detial_order_num, pay_detial_order_price;
	private TextView order_pay_detial_ordernum;
	private double ticketPrice = 0.0;
	private NumberFormat numberFormat;
	private CountTimer count;
	private ResponseTickerOrderInfo rt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		DebugLog.i(TAG, "onCreate");
		setContentView(R.layout.ticket_order_pay_detial);
		inititle();
		initHttpFailView();
		findView();
		intDate();
	}

	private void inititle() {
		// TODO Auto-generated method stub
		left_layout = (LinearLayout) findViewById(R.id.include_left_layout);
		left_layout.setVisibility(View.VISIBLE);
		left_layout.setOnClickListener(this);
		ImageView left_img = (ImageView) findViewById(R.id.include_left_img);
		left_img.setVisibility(View.VISIBLE);
		TextView left_tv = (TextView) findViewById(R.id.include_left_tv);
		left_tv.setVisibility(View.VISIBLE);

		LinearLayout center_layout = (LinearLayout) findViewById(R.id.include_center_layout);
		center_layout.setVisibility(View.VISIBLE);
		TextView center_tv = (TextView) findViewById(R.id.include_center_tv);
		center_tv.setText(this.getResources().getString(
				R.string.include_ticket_order_detia));
		center_tv.setVisibility(View.VISIBLE);
	}

	private void intDate() {
		// TODO Auto-generated method stub
		TicketOrderPayDetialAsy ticketfailasy = new TicketOrderPayDetialAsy(
				this);
		ticketfailasy.execute();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 退出键
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// 订单过期告诉父界面重新刷新列表
			if (!count.isCount()) {
				DebugLog.d(TAG, "订单过期告诉父界面重新刷新列表");
				setResult(Activity.RESULT_OK);
			}
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == left_layout) {
			// 订单过期告诉父界面重新刷新列表
			if (!count.isCount()) {
				DebugLog.d(TAG, "订单过期告诉父界面重新刷新列表");
				setResult(Activity.RESULT_OK);
			}
			finish();
		} else if (v == order_detail_pay_tv_buy) {
			// 在支付时间内
			if (count.isCount()) {
				Bundle bundle = new Bundle();
				bundle.putString(Constants.ParamKey.Key_Ticket_Order_No, rt
						.getRET_INFO().getOrderNo());
				// 去支付界面
				startActivityForRet(TicketOrderPay.class, bundle,
						Constants.ActionCode.ACT_ORDER_PAY);
			} else {
				// 超出支付时间提示无法支付
				MyAlertDialog alert = new MyAlertDialog(this);
				alert.setMessage("订单已过期,无法支付,请重新下单！");
				alert.show();
			}
			// 点击刷新
		} else if (v == layout_http_fail) {
			intDate();
		}
	}

	/**
	 * 支付成功跳转控制
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 从支付界面返回
		if (resultCode != Activity.RESULT_OK) {
			// 支付不成功
			if (data == null
					|| !data.getBooleanExtra(
							Constants.ParamKey.Key_Ticket_Pay_Status, false)) {
				finish(); // 关闭本界面
			}
			return;
		}

		switch (requestCode) {
		// 如果支付成功则关闭本界面并告诉上个界面成功
		case Constants.ActionCode.ACT_ORDER_PAY:
			if (data != null
					&& data.getBooleanExtra(
							Constants.ParamKey.Key_Ticket_Pay_Status, false)) {
				setResult(Activity.RESULT_OK); // 告诉上个界面支付成功
			}
			finish();
			break;
		}

	}

	@Override
	protected void findView() {
		// TODO Auto-generated method stub
		order_detail_pay_tv_buy = (TextView) findViewById(R.id.order_detail_pay_tv_buy);
		order_detail_pay_tv_buy.setOnClickListener(this);

		pay_detail_phone = (TextView) findViewById(R.id.order_pay_detail_phone);
		pay_datial_date = (TextView) findViewById(R.id.order_pay_datial_date);
		pay_datial_remain_date = (TextView) findViewById(R.id.order_pay_datial_remain_date);
		pay_detial_tv_start = (TextView) findViewById(R.id.order_pay_detial_tv_start);
		pay_detial_tv_end = (TextView) findViewById(R.id.order_pay_detial_tv_end);

		pay_detial_order_num = (TextView) findViewById(R.id.order_pay_detial_order_num);
		pay_detial_order_price = (TextView) findViewById(R.id.order_pay_detial_order_price);
		order_pay_detial_ordernum = (TextView)findViewById(R.id.order_pay_detial_ordernum);
	}

	private class TicketOrderPayDetialAsy extends BaseAsyncTask {
		public TicketOrderPayDetialAsy(BaseActivity activity) {
			super(activity);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showResultDialog();
		}

		@Override
		protected ResponseBase doInBackground(Void... params) {
			// TODO Auto-generated method stub
			TicketDao dao = new TicketDao(TicketOrderPayDetial.this);
			HashMap<String, String> ticketorderfailMap = new HashMap<String, String>();
			ticketorderfailMap.put("regphone", mApplication.getUserInfo()
					.getPHONE());
			ticketorderfailMap.put(
					"orderno",
					getIntent().getStringExtra(
							Constants.ParamKey.Key_Ticket_Order_No));
			return dao.getTitckerOrderDetial(ticketorderfailMap);
		}

		@Override
		protected void onPostExecute(ResponseBase result) {
			super.onPostExecute(result);
			dismissResultDialog();
			if (result != null) {
				if (Constants.CD_SUCCESS.equals(result.getERR_INFO()
						.getERROR_CODE())) {
					hideHttpFailView();// 隐藏请求失败界面

					rt = (ResponseTickerOrderInfo) result;
					pay_detail_phone.setText(rt.getRET_INFO().getUserPhone());
//					SimpleDateFormat dateFormat = new SimpleDateFormat(
//							"yyyy-MM-dd HH:mm");
//					Date date = new Date(rt.getRET_INFO().getLeaveDate());
					pay_datial_date.setText(rt.getRET_INFO().getLeaveDate()+ "  "
							+ rt.getRET_INFO().getLeaveTime());
					String routename = rt.getRET_INFO().getRoutename();
					String[] start_station = routename.split("-");
					pay_detial_tv_start.setText(start_station[0]);
					pay_detial_tv_end.setText(start_station[1]);
					numberFormat = Utility.getMoneyFormt(); // 货币格式
					pay_detial_order_num
							.setText(rt.getRET_INFO().getOrderNum());

					ticketPrice = Integer.valueOf(rt.getRET_INFO()
							.getOrderCash()) / 100; // 票价单位是分，转换为显示用的元

					pay_detial_order_price.setText(String.format(
							getString(R.string.ticker_query_detail_price),
							numberFormat.format(ticketPrice)));
					order_pay_detial_ordernum.setText(rt.getRET_INFO().getOrderNo());

					// 剩余支付时间 开始计时
					count = new CountTimer(pay_datial_remain_date, rt
							.getRET_INFO().getExpired(), "00:00", "%s",
							CountTimer.Displey_Second_Minute);
					count.Start();
				} else {
					showHttpFailView();// 显示网络失败界面
				}
			} else {
				showHttpFailView();// 显示网络失败界面
			}
		}

	}
}

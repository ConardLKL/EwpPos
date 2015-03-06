package com.limahao.ticket.ui;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import android.os.Bundle;
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

public class TicketOrderFailDetial extends BaseActivity {
	private final String TAG = "TicketOrderFailDetial";
	private NumberFormat numberFormat;
	private LinearLayout left_layout;
	private TextView fail_detail_phone, fail_datial_date;
	private TextView fail_detial_tv_start, fail_detial_tv_end;
	private TextView fail_detial_order_num, fail_detial_order_price;
	private TextView fail_detail_status;
	private TextView order_fail_datial_ordernum;
	private double ticketPrice = 0.0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		DebugLog.i(TAG, "onCreate");
		setContentView(R.layout.ticket_order_fail_detial);
		inititle();
		initHttpFailView();
		findView();
		intDate();
	}

	private void intDate() {
		// TODO Auto-generated method stub
		TicketOrderFailDetialAsy ticketfailasy = new TicketOrderFailDetialAsy(
				this);
		ticketfailasy.execute();
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == left_layout) {
			finish();
			// 点击刷新
		} else if (v == layout_http_fail) {
			intDate();
		}
	}

	@Override
	protected void findView() {
		// TODO Auto-generated method stub
		fail_detail_phone = (TextView) findViewById(R.id.order_fail_detail_phone);
		fail_datial_date = (TextView) findViewById(R.id.order_fail_datial_date);

		fail_detial_tv_start = (TextView) findViewById(R.id.order_fail_detial_tv_start);
		fail_detial_tv_end = (TextView) findViewById(R.id.order_fail_detial_tv_end);

		fail_detial_order_num = (TextView) findViewById(R.id.order_fail_detial_order_num);
		fail_detial_order_price = (TextView) findViewById(R.id.order_fail_detial_order_price);
		fail_detail_status = (TextView) findViewById(R.id.order_fail_detail_status);
		order_fail_datial_ordernum = (TextView) findViewById(R.id.order_fail_datial_ordernum);
		
		// 如果订票异常显示订票异常
		if (Constants.OrderStatus.Status_Order_Exception.equals(getIntent()
				.getStringExtra(Constants.ParamKey.Key_Ticket_Order_Status))) {
			fail_detail_status
					.setText(getString(R.string.ticket_order_detial_exception));
		}

	}

	private class TicketOrderFailDetialAsy extends BaseAsyncTask {
		public TicketOrderFailDetialAsy(BaseActivity activity) {
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
			TicketDao dao = new TicketDao(TicketOrderFailDetial.this);
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

					ResponseTickerOrderInfo rt = (ResponseTickerOrderInfo) result;
					fail_detail_phone.setText(rt.getRET_INFO().getUserPhone());
//					SimpleDateFormat dateFormat = new SimpleDateFormat(
//							"yyyy-MM-dd HH:mm");
//					Date date = new Date(rt.getRET_INFO().getOrderTime());
					fail_datial_date.setText(rt.getRET_INFO().getLeaveDate()+ "  "
							+ rt.getRET_INFO().getLeaveTime());

					String routename = rt.getRET_INFO().getRoutename();
					String[] start_station = routename.split("-");
					fail_detial_tv_start.setText(start_station[0]);
					fail_detial_tv_end.setText(start_station[1]);
					numberFormat = Utility.getMoneyFormt(); // 货币格式
					fail_detial_order_num.setText(rt.getRET_INFO()
							.getOrderNum());

					ticketPrice = Integer.valueOf(rt.getRET_INFO()
							.getOrderCash()) / 100; // 票价单位是分，转换为显示用的元

					fail_detial_order_price.setText(String.format(
							getString(R.string.ticker_query_detail_price),
							numberFormat.format(ticketPrice)));
					order_fail_datial_ordernum.setText(rt.getRET_INFO().getOrderNo());
				} else {
					showHttpFailView();// 显示网络失败界面
				}
			} else {
				showHttpFailView();// 显示网络失败界面
			}
		}

	}
}

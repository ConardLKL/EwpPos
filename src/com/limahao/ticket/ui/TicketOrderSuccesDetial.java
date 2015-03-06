package com.limahao.ticket.ui;

import java.text.NumberFormat;
import java.util.HashMap;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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

public class TicketOrderSuccesDetial extends BaseActivity {
	private final String TAG = "TicketOrderSuccesDetial";
	private LinearLayout left_layout;
	private TextView succes_detail_phone;
	private TextView succes_detial_riding_time;
	private TextView succes_detial_tv_start, succes_detial_tv_end;
	private TextView succes_detial_train_number, succes_detial_ticket_entrance;
	private TextView succes_detial_seat_number;
	private TextView succes_detial_order_num, succes_detial_order_price;
	private EditText order_succes_detial_pwd;
	private TextView order_succes_detial_ordernum;

	private double ticketPrice = 0.0;
	private NumberFormat numberFormat;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		DebugLog.i(TAG, "onCreate");
		setContentView(R.layout.ticket_order_succes_detial);
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
		TicketOrderSuccesDetialAsy ticketfailasy = new TicketOrderSuccesDetialAsy(
				this);
		ticketfailasy.execute();
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

		// TODO Auto-generated method stub
		succes_detail_phone = (TextView) findViewById(R.id.order_succes_detail_phone);
		succes_detial_riding_time = (TextView) findViewById(R.id.order_succes_detial_riding_time);

		succes_detial_tv_start = (TextView) findViewById(R.id.order_succes_detial_tv_start);
		succes_detial_tv_end = (TextView) findViewById(R.id.order_succes_detial_tv_end);
		succes_detial_train_number = (TextView) findViewById(R.id.order_succes_detial_train_number);
		succes_detial_ticket_entrance = (TextView) findViewById(R.id.order_succes_detial_ticket_entrance);

		succes_detial_seat_number = (TextView) findViewById(R.id.order_succes_detial_seat_number);

		succes_detial_order_num = (TextView) findViewById(R.id.order_succes_detial_order_num);
		succes_detial_order_price = (TextView) findViewById(R.id.order_succes_detial_order_price);
		
		// 取票密码
		order_succes_detial_pwd = (EditText) findViewById(R.id.order_succes_detial_pwd);
		order_succes_detial_ordernum = (TextView)findViewById(R.id.order_succes_detial_ordernum);

	}

	private class TicketOrderSuccesDetialAsy extends BaseAsyncTask {
		public TicketOrderSuccesDetialAsy(BaseActivity activity) {
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
			TicketDao dao = new TicketDao(TicketOrderSuccesDetial.this);
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
					// 取票密码
					if(!Utility.isNullOrEmpty(rt.getRET_INFO().getIdNumber())){
						order_succes_detial_pwd.setText(rt.getRET_INFO().getIdNumber());
					}
					succes_detail_phone
							.setText(rt.getRET_INFO().getUserPhone());
					succes_detial_riding_time.setText(rt.getRET_INFO()
							.getLeaveDate()
							+ "  "
							+ rt.getRET_INFO().getLeaveTime());
					String routename = rt.getRET_INFO().getRoutename();
					String[] start_station = routename.split("-");
					succes_detial_tv_start.setText(start_station[0]);
					succes_detial_tv_end.setText(start_station[1]);
					succes_detial_train_number.setText(rt.getRET_INFO()
							.getBusNo());
					succes_detial_ticket_entrance.setText(rt.getRET_INFO()
							.getGate());
					String sitno = rt.getRET_INFO().getSitNo();
					if (!Utility.isNullOrEmpty(sitno)) {
						succes_detial_seat_number.setText(sitno.replaceAll(",",
								" "));
					} else {
						succes_detial_seat_number.setText("");
					}
					numberFormat = Utility.getMoneyFormt(); // 货币格式
					succes_detial_order_num.setText(rt.getRET_INFO()
							.getOrderNum());
					ticketPrice = Integer.valueOf(rt.getRET_INFO()
							.getOrderCash()) / 100; // 票价单位是分，转换为显示用的元
					succes_detial_order_price.setText(String.format(
							getString(R.string.ticker_query_detail_price),
							numberFormat.format(ticketPrice)));
					order_succes_detial_ordernum.setText(rt.getRET_INFO().getOrderNo());
				} else {
					showHttpFailView();// 显示网络失败界面
				}
			} else {
				showHttpFailView();// 显示网络失败界面
			}
		}

	}
}

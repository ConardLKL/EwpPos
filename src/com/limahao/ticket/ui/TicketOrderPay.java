package com.limahao.ticket.ui;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hxcr.umspay.activity.Initialize;
import com.hxcr.umspay.util.Utils;
import com.limahao.ticket.BaseActivity;
import com.limahao.ticket.BaseAsyncTask;
import com.limahao.ticket.R;
import com.limahao.ticket.config.Configs;
import com.limahao.ticket.config.Constants;
import com.limahao.ticket.dao.TicketDao;
import com.limahao.ticket.entity.ItemTickerOrder;
import com.limahao.ticket.entity.ResponseBase;
import com.limahao.ticket.entity.ResponseTickerOrderInfo;
import com.limahao.ticket.log.DebugLog;
import com.limahao.ticket.utils.Utility;
import com.limahao.ticket.view.CountTimer;
import com.limahao.ticket.view.MyAlertDialog;

/**
 * @Title: TicketQueryDetail.java
 * 
 * @author email: <a href="cj@ewppay.com">陈杰</a>
 * @date 2014-8-11 下午1:29:28
 * @version :
 * @Description: 订购支付
 */
public class TicketOrderPay extends BaseActivity {
	private final String TAG = "TicketOrderPay";
	private LinearLayout layoutBack;
	private RelativeLayout layoutUmspay;
	private TextView tvOrderNo; // 订单号
	private TextView tvDate;
	private TextView tvPayTime;
	private TextView tvStart;
	private TextView tvEnd;
	private TextView tvOrderNum;
	private TextView tvOrderPrice;

	private NumberFormat numberFormat;
	private CountTimer count;
	private String orderNo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ticket_order_pay);
		iniTitle();
		initHttpFailView();
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
		center_tv.setText(getString(R.string.include_tick_order_pay_txt));
	}

	@Override
	protected void findView() {
		layoutUmspay = (RelativeLayout) findViewById(R.id.order_pay_layout_umspay);
		layoutUmspay.setOnClickListener(this);

		tvOrderNo = (TextView) findViewById(R.id.order_pay_tv_orderno);
		tvDate = (TextView) findViewById(R.id.order_pay_tv_date);
		tvPayTime = (TextView) findViewById(R.id.order_pay_tv_paytime);
		tvStart = (TextView) findViewById(R.id.order_pay_tv_start);
		tvEnd = (TextView) findViewById(R.id.order_pay_tv_end);
		tvOrderNum = (TextView) findViewById(R.id.order_pay_tv_order_num);
		tvOrderPrice = (TextView) findViewById(R.id.order_pay_tv_order_price);
	}

	private void init() {
		orderNo = getIntent().getStringExtra(
				Constants.ParamKey.Key_Ticket_Order_No);
		numberFormat = Utility.getMoneyFormt(); // 货币格式
		InitAsy init = new InitAsy(this);
		init.execute();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//退出键
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// 如果键盘打开则关闭
			if (imm != null && imm.isActive() && getCurrentFocus() != null) {
				imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
			}
			
			// 提示未支付是否离开
			final MyAlertDialog alert = new MyAlertDialog(this, MyAlertDialog.Mode_Yes_No);
			alert.setMessage("尚未支付订单！");
			alert.setOnYesclick(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					alert.dismiss();
					// 订单过期告诉父界面重新刷新列表
					if(!count.isCount()){
						DebugLog.d(TAG, "订单过期告诉父界面重新刷新列表");
						setResult(Activity.RESULT_OK);
					}
					finish();
				}
			});
			alert.show();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		if (v == layoutBack) {
			// 如果键盘打开则关闭
			if (imm != null && imm.isActive() && getCurrentFocus() != null) {
				imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
			}
			
			// 提示未支付是否离开
			final MyAlertDialog alert = new MyAlertDialog(this, MyAlertDialog.Mode_Yes_No);
			alert.setMessage("尚未支付订单！");
			alert.setOnYesclick(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					alert.dismiss();
					// 订单过期告诉父界面重新刷新列表
					if(!count.isCount()){
						DebugLog.d(TAG, "订单过期告诉父界面重新刷新列表");
						setResult(Activity.RESULT_OK);
					}
					finish();
				}
			});
			alert.show();
			
		} else if (v == layoutUmspay) {
			// 在支付时间内
			if (count.isCount()) {
				UmsPayAsy asy = new UmsPayAsy(this);
				asy.execute();
			} else {
				// 超出支付时间提示无法支付
				MyAlertDialog alert = new MyAlertDialog(this);
				alert.setMessage("订单已过期,无法支付,请重新下单！");
				alert.show();
			}
			// 点击刷新
		} else if (v == layout_http_fail) {
			init();
		}
	}

	@Override
	// ！！！！！！！！！！！ 重要的方法
	// 用于返回插件返回的支付结果报文
	public void onResume() {
		String result = Utils.getPayResult();
		if (!Utility.isNullOrEmpty(result)) {
			System.out.println(result);
			String respCode = "";
			String respDesc = "";
			InputStream is = new ByteArrayInputStream(result.getBytes());
			XmlPullParser parser = Xml.newPullParser();
			try {
				parser.setInput(is, "UTF-8");
				int event = parser.getEventType();// 产生第一个事件
				while (event != XmlPullParser.END_DOCUMENT) {
					switch (event) {
					case XmlPullParser.START_DOCUMENT:// 判断当前事件是否是文档开始事件
						break;
					case XmlPullParser.START_TAG:// 判断当前事件是否是标签元素开始事件
						if ("respCode".equals(parser.getName())) {// 判断开始标签元素是否是merchantId
							respCode = parser.nextText();
						} else if ("respDesc".equals(parser.getName())) {// 判断开始标签元素是否是OrderTime
							respDesc = parser.nextText();
						}
						break;
					case XmlPullParser.END_TAG:// 判断当前事件是否是标签元素结束事件
						break;
					}
					event = parser.next();// 进入下一个元素并触发相应事件
				}// end while
			} catch (Exception e) {
				e.printStackTrace();
			}

			if ("0000".equals(respCode)) {
				// 支付成功
				Bundle bd = new Bundle();
				bd.putString(Constants.ParamKey.Key_Ticket_Order_No, orderNo); // 订单号
				dropToNextActivity(TicketPaySuccesss.class, bd);
				// 如果是等待支付界面进入的则需告诉支付状态成功
				Intent data = new Intent();
				data.putExtra(Constants.ParamKey.Key_Ticket_Pay_Status, true);
				setResult(Activity.RESULT_OK, data); // 告诉上个界面支付成功
				finish();
			} // 不需要提示了
//			else {
//				// 支付失败
//				// 超出支付时间提示无法支付
//				MyAlertDialog alert = new MyAlertDialog(this);
//				alert.setMessage("支付失败！");
//				alert.show();
//			}
			DebugLog.d(TAG, "initPayResult前报文返回onResume:" + result);
			Utils.initPayResult();// 清楚返回结果了
			DebugLog.d(TAG,
					"initPayResult后报文返回onResume:" + Utils.getPayResult());
		}
		super.onResume();
	}

	// ！！！！！！！！！！！ 重要的方法
	// 用于返回插件返回的支付结果报文
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 银联貌似不支持这种方式，这段代码没用先扔着吧
		if (resultCode != Activity.RESULT_OK)
			return;
		switch (requestCode) {
		case Constants.ActionCode.ACT_UMS_PAY:
			String haha = data.getExtras().getString("result");
			// haha 字段为返回的结果报文
			DebugLog.d(TAG, "报文返回onActivityResult:" + haha);
			// Info.init();
			break;
		}

	}

	/**
	 * 银联全民支付
	 * 
	 * @author Administrator
	 * 
	 */
	private class InitAsy extends BaseAsyncTask {
		public InitAsy(BaseActivity activity) {
			super(activity);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showResultDialog();
		}

		@Override
		protected ResponseBase doInBackground(Void... params) {
			// 根据订单号获取待支付订单信息
			TicketDao dao = new TicketDao(mActivity);
			HashMap<String, String> orderParams = new HashMap<String, String>();
			orderParams.put("orderno", orderNo); // 订单号
			orderParams.put("regphone", mApplication.getUserInfo().getPHONE());
			return dao.getTitckerOrderDetial(orderParams);
		}

		@Override
		protected void onPostExecute(ResponseBase result) {
			super.onPostExecute(result);
			if (result != null) {
				if (Constants.CD_SUCCESS.equals(result.getERR_INFO()
						.getERROR_CODE())) {
					hideHttpFailView();// 隐藏请求失败界面
					// 详情参数
					ItemTickerOrder orderDetail = ((ResponseTickerOrderInfo) result)
							.getRET_INFO();
					tvDate.setText(orderDetail.getLeaveDate() + "  "
							+ orderDetail.getLeaveTime()); // 出发日期
					tvOrderNo.setText(orderDetail.getOrderNo()); // 订单编号
					String[] staions = orderDetail.getRoutename().split("-"); // 截取
																				// 始发站
																				// 终点站
					tvStart.setText(staions[0]); // 始发站
					tvEnd.setText(staions[1]); // 终点站 "杭州南-武义(快)"
					tvOrderNum.setText(orderDetail.getOrderNum()); // 订票数
					// 订单价格
					tvOrderPrice.setText(String.format(
							getString(R.string.ticker_query_detail_price),
							numberFormat.format(Integer.valueOf(orderDetail
									.getOrderCash()) / 100))); // 票价 单位分

					// 剩余支付时间 开始计时
					count = new CountTimer(tvPayTime, orderDetail.getExpired(),
							"00:00", "%s", CountTimer.Displey_Second_Minute);
					count.Start();
				} else {
					showHttpFailView();// 显示网络失败界面
				}
			} else {
				showHttpFailView();// 显示网络失败界面
			}
			dismissResultDialog();
		}
	}

	/**
	 * 银联全民支付
	 * 
	 * @author Administrator
	 * 
	 */
	private class UmsPayAsy extends BaseAsyncTask {
		public UmsPayAsy(BaseActivity activity) {
			super(activity);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showResultDialog();
		}

		@Override
		protected ResponseBase doInBackground(Void... params) {
			// 根据订单号获取银联支付信息
			TicketDao dao = new TicketDao(mActivity);
			HashMap<String, String> payParams = new HashMap<String, String>();
			payParams.put("orderno", orderNo); // 订单号
			return dao.getUmsPayInfo(payParams);
		}

		@Override
		protected void onPostExecute(ResponseBase result) {
			super.onPostExecute(result);
			if (result != null) {
				if (Constants.CD_SUCCESS.equals(result.getERR_INFO()
						.getERROR_CODE())) {
					// 分为四段 第一段：MerSign 商户私钥签名后的信息如TransId,ChrCode
					// 第二段：ChrCode 下单后银商返回给商户后台的特征码
					// 第三段：TransId 下单后银商返回给商户的交易流水号
					// 第四段：merchantID 商户号
					String payinfo = result.getERR_INFO().getERROR_MSG();
					if (!Utility.isNullOrEmpty(payinfo)) {
						String[] temp = payinfo.split("\\|");
						if (!temp[2].equals("") && temp[2] != null) {
							Intent intent = new Intent(mActivity,
									Initialize.class);
							intent.putExtra("xml", payinfo);
							intent.putExtra("istest", Configs.umspaycfg);// 生产 传'0'，测试传1
							startActivityForRet(intent,
									Constants.ActionCode.ACT_UMS_PAY);
						} else {
							showLongToast("TransId交易流水号为空");
						}

					} else {
						showLongToast("下单失败！~~~~");
					}
				}
			}
			dismissResultDialog();
		}
	}
}

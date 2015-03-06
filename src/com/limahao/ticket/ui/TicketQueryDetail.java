package com.limahao.ticket.ui;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.limahao.ticket.AppManager;
import com.limahao.ticket.BaseActivity;
import com.limahao.ticket.BaseAsyncTask;
import com.limahao.ticket.MainActivity;
import com.limahao.ticket.R;
import com.limahao.ticket.config.Constants;
import com.limahao.ticket.dao.TicketDao;
import com.limahao.ticket.db.provider.HistoryOrderDB;
import com.limahao.ticket.db.provider.PreDateDB;
import com.limahao.ticket.entity.ItemTicker;
import com.limahao.ticket.entity.ResponseBase;
import com.limahao.ticket.entity.ResponseTickerOrderInfo;
import com.limahao.ticket.utils.MatcherUtils;
import com.limahao.ticket.utils.Utility;

/**
 * @Title: TicketQueryDetail.java
 * 
 * @author email: <a href="cj@ewppay.com">陈杰</a>
 * @date 2014-8-1 下午1:29:28
 * @version :
 * @Description: 车票查询详情
 */
public class TicketQueryDetail extends BaseActivity {
	private final String TAG = "TicketQueryDetail";
	private LinearLayout layoutBack;
	private TextView tvBuy;
	private TextView tvNum;
	private TextView tvNumCut;
	private TextView tvNumAdd;
	private TextView tvDateTime;
	private TextView tvServiceCash;
	private TextView tvStart;
	private TextView tvEnd;
	private TextView tvBusType;
	private TextView tvPrice;
	private TextView tvSurplus;
	private TextView tvMileage;
	private TextView tvTotalPrice;
	private TextView tvTotalPayPrice;

	private EditText etPhone;
	private TextView tvPhoneSelect;

	private PopupWindow phonePop;
	private PopupWindow confirmPop;

	private int ticketCurNum = 1;
	private int ticketMaxNum = 5;
	private int ticketMinNum = 1;
	private double ticketPrice = 0.0;
	private double servicePrice = 0.0; // 服务费，这个app不是白订票的，如果是你还会用吗？
	private double totalServicePrice = 0.0; // 服务费，这个app不是白订票的，如果是你还会用吗？
	private double ticketTotalPrice = 0.0; // 总票价
	private List<String> history;
	private ItemTicker detail;
	private String endStationId;
	private Animation mRotateUpAnim;
	private Animation mRotateDownAnim;
	private final int ROTATE_ANIM_DURATION = 180;
	private NumberFormat numberFormat;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ticket_query_detail);
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
		center_tv.setText(getString(R.string.include_tick_query_detail_txt));
	}

	@Override
	protected void findView() {
		tvBuy = (TextView) findViewById(R.id.query_detail_tv_buy);
		tvBuy.setOnClickListener(this);

		tvNum = (TextView) findViewById(R.id.query_detail_tv_number);
		tvNumCut = (TextView) findViewById(R.id.query_detail_tv_num_cut);
		tvNumCut.setOnClickListener(this);
		tvNumAdd = (TextView) findViewById(R.id.query_detail_tv_num_add);
		tvNumAdd.setOnClickListener(this);

		etPhone = (EditText) findViewById(R.id.query_detail_et_phone);
		tvPhoneSelect = (TextView) findViewById(R.id.query_detail_tv_phone_select);
		tvPhoneSelect.setOnClickListener(this);

		tvDateTime = (TextView) findViewById(R.id.query_detail_tv_date);
		tvServiceCash = (TextView) findViewById(R.id.query_detail_tv_servercash);
		tvStart = (TextView) findViewById(R.id.query_detail_tv_start);
		tvEnd = (TextView) findViewById(R.id.query_detail_tv_end);
		tvBusType = (TextView) findViewById(R.id.query_detail_tv_bustype);
		tvPrice = (TextView) findViewById(R.id.query_detail_tv_price);
		tvSurplus = (TextView) findViewById(R.id.query_detail_tv_surplus);
		tvMileage = (TextView) findViewById(R.id.query_detail_tv_mileage);
		tvTotalPrice = (TextView) findViewById(R.id.query_detail_tv_totalp_price);
		tvTotalPayPrice = (TextView) findViewById(R.id.query_detail_tv_total_price);
	}

	private void init() {
		// 数据库查询预售票
		PreDateDB predateDB = new PreDateDB(this);
		String maxTck = predateDB.queryPreDate().getMaxTicket();
		ticketMaxNum = Integer.valueOf(maxTck);
		predateDB.dbClose();

		// 数据库查询 历史订票手机号
		HistoryOrderDB historyDB = new HistoryOrderDB(this);
		history = historyDB.queryHistory();
		historyDB.dbClose();

		// 详情参数
		detail = (ItemTicker) mApplication.getParams().get(
				Constants.ParamKey.Key_Ticket_Detail);
		numberFormat = Utility.getMoneyFormt(); // 货币格式
		endStationId = getIntent().getStringExtra(
				Constants.ParamKey.Key_City_ID);

		tvDateTime
				.setText(detail.getLeaveDate() + "\n" + detail.getLeaveTime()); // 出发日期
																				// 出发时间

		servicePrice = Integer.valueOf(detail.getServiceCash()) / 100; // 服务费
		// 单位是分，转换为显示用的元
		tvServiceCash.setText(String.format(
				getString(R.string.ticker_query_detail_servicecash),
				numberFormat.format(servicePrice))); // 服务费
		tvStart.setText(detail.getStartStation()); // 始发站
		tvEnd.setText(detail.getRoutename().split("-")[1]); // 终点站 "杭州南-武义(快)"
															// 截取终点站
		tvBusType.setText(String.format(
				getString(R.string.ticker_query_detail_bustype),
				detail.getBusType())); // 车型
		ticketPrice = Integer.valueOf(detail.getFullPrice()) / 100; // 票价单位是分，转换为显示用的元

		tvPrice.setText(String.format(
				getString(R.string.ticker_query_detail_price),
				numberFormat.format(ticketPrice))); // 票价 单位分

		tvSurplus.setText(detail.getSurplusTicket()); // 余票
		tvMileage.setText(String.format(
				getString(R.string.ticker_query_detail_mileage),
				detail.getMileage())); // 里程

		// 余票不足，比预售票数少
		if (ticketMaxNum > Integer.valueOf(detail.getSurplusTicket())) {
			ticketMaxNum = Integer.valueOf(detail.getSurplusTicket());
		}
		// 更新票数增减按钮状态
		updateNumState();
		// tvNum.setWidth(tvNum.getWidth());// 宽度保持不变

		// 动画效果更定义
		mRotateUpAnim = new RotateAnimation(0.0f, -180.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
		mRotateUpAnim.setFillAfter(true);
		mRotateDownAnim = new RotateAnimation(-180.0f, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
		mRotateDownAnim.setFillAfter(true);
	}

	@Override
	public void onClick(View v) {
		if (v == layoutBack) {
			// 如果键盘打开则关闭
			if (imm != null && imm.isActive()) {
				imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
			}
			finish();
		} else if (v == tvBuy) {
			if (Utility.isNullOrEmpty(etPhone.getText().toString().trim())) { // 不为空
				showLongToast(getResources().getString(
						R.string.login_error_require_phone_number));
			} else if (!MatcherUtils.isMobileNO(etPhone.getText().toString()
					.trim())) { // 手机号码格式
				showLongToast(getResources().getString(
						R.string.login_error_username));
			} else {
				// 如果键盘打开则关闭
				if (imm != null && imm.isActive()) {
					imm.hideSoftInputFromWindow(getCurrentFocus()
							.getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
				}
				// 如果未登录去登录界面
				if (!mApplication.isLogin()) {
					startActivityForRet(Login.class, null, Constants.ActionCode.ACT_LOGIN_SWICH);
				} else {
					// 否则提示确认购买
					showConfirmPop();
				}
			}
		} else if (v == tvNumCut) {
			ticketCurNum--;
			updateNumState();
		} else if (v == tvNumAdd) {
			ticketCurNum++;
			updateNumState();
		} else if (v == tvPhoneSelect) {
			// 如果键盘打开则关闭
			if (imm != null && imm.isActive()) {
				imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
			}
			animaUp();
			showPhonePop();

		}
	}
	
	/**
	 * 下单成功跳转控制
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode != Activity.RESULT_OK)
			return;
		switch (requestCode) {
		// 如果登录成功则下订单
		case Constants.ActionCode.ACT_LOGIN_SWICH:
//			// 登陆后的返回，建议返回温馨提示确认界面，客户再看一遍，慎重考虑
//			// 下订单
//			TicketOrderAsy order = new TicketOrderAsy(
//					TicketQueryDetail.this);
//			order.execute();
			break;
		}

	}

	private void updateNumState() {
		tvNum.setText(ticketCurNum + "");
		if (ticketCurNum == ticketMaxNum) {
			tvNumCut.setEnabled(true);
			tvNumAdd.setEnabled(false);
		} else if (ticketCurNum == ticketMinNum) {
			tvNumCut.setEnabled(false);
			tvNumAdd.setEnabled(true);
		} else {
			tvNumCut.setEnabled(true);
			tvNumAdd.setEnabled(true);
		}

		// 计算总票价 总服务费
		ticketTotalPrice = ticketPrice * ticketCurNum;
		totalServicePrice = servicePrice * ticketCurNum;
		
		// 合计票价
		tvTotalPrice.setText(String.format(
				getString(R.string.ticker_query_detail_price),
				numberFormat.format(ticketTotalPrice)));
		// 总价含服务费
		tvTotalPayPrice.setText(String.format(
				getString(R.string.ticker_query_detail_price),
				numberFormat.format(ticketTotalPrice + totalServicePrice)));
	}

	private void animaUp() {
		tvPhoneSelect.clearAnimation();
		tvPhoneSelect.startAnimation(mRotateUpAnim);
	}

	private void animaDown() {
		tvPhoneSelect.clearAnimation();
		tvPhoneSelect.startAnimation(mRotateDownAnim);
	}

	private void showPhonePop() {
		LinearLayout layout = (LinearLayout) LayoutInflater.from(this).inflate(
				R.layout.popupwindow_phone_list, null);
		ListView listView = (ListView) layout
				.findViewById(R.id.poup_phone_list);
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.popupwindow_phone_item, R.id.poup_phone_tv_item,
				history);

		listView.setAdapter(adapter);
		phonePop = new PopupWindow(this);
		phonePop.setWidth(etPhone.getWidth());
		phonePop.setHeight(LayoutParams.WRAP_CONTENT);
		phonePop.setOutsideTouchable(true);
		phonePop.setFocusable(true);
		phonePop.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.ticket_number_none_selector));
		phonePop.setAnimationStyle(R.style.PopupAnimationPullDown);
		phonePop.setContentView(layout);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				etPhone.setText(history.get(position));
				etPhone.setSelection(etPhone.getText().length()); // 设置光标
				phonePop.dismiss();
				phonePop = null;
			}
		});
		phonePop.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				animaDown();
			}
		});

		phonePop.showAsDropDown(etPhone);
		// popupWindow.showAtLocation(publistBtn, Gravity.LEFT
		// | Gravity.TOP, x, y);//需要指定Gravity，默认情况是center.

	}

	private void showConfirmPop() {
		RelativeLayout layout = (RelativeLayout) LayoutInflater.from(this)
				.inflate(R.layout.popupwindow_order_ticket, null);
		LinearLayout layoutDetail = (LinearLayout) findViewById(R.id.layout_ticket_query_detail);
		TextView tvBuy = (TextView) layout
				.findViewById(R.id.popu_order_ticket_tv_confrim_by);
		TextView tvClose = (TextView) layout
				.findViewById(R.id.popu_order_ticket_tv_close);

		confirmPop = new PopupWindow(this);
		confirmPop.setWidth(layoutDetail.getWidth());
		confirmPop.setHeight(LayoutParams.WRAP_CONTENT);
		confirmPop.setOutsideTouchable(true);
		confirmPop.setFocusable(true);
		confirmPop.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.popupwindow_order_ticket_bg));
		confirmPop.setAnimationStyle(R.style.PopupAnimation);
		confirmPop.setContentView(layout);
		tvBuy.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				confirmPop.dismiss();
				confirmPop = null;

				// 保存历史订票手机号码
				HistoryOrderDB historyDB = new HistoryOrderDB(
						TicketQueryDetail.this);
				historyDB.updateHistory(etPhone.getText().toString().trim());
				historyDB.dbClose();

				if (!mApplication.isLogin()) {
					startActivityForRet(Login.class, null, Constants.ActionCode.ACT_LOGIN_SWICH);
				} else {
					// 下订单
					TicketOrderAsy order = new TicketOrderAsy(
							TicketQueryDetail.this);
					order.execute();
				}
			}
		});

		tvClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				confirmPop.dismiss();
				confirmPop = null;
			}
		});

		confirmPop.showAtLocation(layoutDetail,
				Gravity.BOTTOM | Gravity.CENTER, 0, 0);
	}

	private class TicketOrderAsy extends BaseAsyncTask {
		public TicketOrderAsy(BaseActivity activity) {
			super(activity);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showResultDialog();
		}

		@Override
		protected ResponseBase doInBackground(Void... params) {
			TicketDao dao = new TicketDao(TicketQueryDetail.this);
			HashMap<String, String> orderParams = new HashMap<String, String>();
			// ticketsPrice 订单金额 根据3.3节返回数据和订单张数计算 （单张票价+3元）*张数
			orderParams.put("ticketsPrice",
					(int) ((ticketTotalPrice + totalServicePrice) * 100) + ""); // 单位为分
			// orderNum 订票张数
			orderParams.put("ordernum", ticketCurNum + "");
			// busNo 车次号
			orderParams.put("busNo", detail.getBusNo());
			// phone 手机号码
			orderParams.put("phone", etPhone.getText().toString().trim());
			// Password 用户密码
			orderParams.put("password", mApplication.getUserInfo()
					.getPassword());
			// regPhone 注册手机
			orderParams.put("regphone", mApplication.getUserInfo().getPHONE());
			// Routename 线路名称
			orderParams.put("routename", detail.getRoutename());
			// Leavetime 出发时间
			orderParams.put("Leavetime", detail.getLeaveTime());
			// LeaveDate 出发日期
			orderParams.put("LeaveDate", detail.getLeaveDate());
			// endStationId 到站编号
			orderParams.put("endStationId", endStationId);

			// 下订单
			return dao.orderTicket(orderParams);
		}

		@Override
		protected void onPostExecute(ResponseBase result) {
			super.onPostExecute(result);
			if (result != null) {
				if (Constants.CD_SUCCESS.equals(result.getERR_INFO()
						.getERROR_CODE())) {
					ResponseTickerOrderInfo rt = (ResponseTickerOrderInfo) result;
					showShortToast("订票成功,请在有效时间内支付！！！");
					Bundle bd = new Bundle();
					bd.putString(Constants.ParamKey.Key_Ticket_Order_No, rt.getRET_INFO().getOrderNo());
					dropToNextActivity(TicketOrderPay.class, bd);
					setResult(Activity.RESULT_OK); // 告诉车次查询界面下单成功了 车次查询界面知道后关闭
					
					// 订票成功后设定消息通知
					MainActivity mainAct = (MainActivity)AppManager.getAppManager().getActivity(MainActivity.class);
					Message msg = new Message();
					msg.what = Constants.ActionCode.ACT_MESSAGE_NOTICE;
					mainAct.mHandler.sendMessage(msg);
					finish();
				}
			}
			dismissResultDialog();
		}
	}
}

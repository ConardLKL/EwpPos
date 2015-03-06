package com.limahao.ticket.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Message;
import android.text.format.DateUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.limahao.ticket.AppManager;
import com.limahao.ticket.BaseActivity;
import com.limahao.ticket.BaseAsyncTask;
import com.limahao.ticket.BaseSlidingFragmentActivity;
import com.limahao.ticket.MainActivity;
import com.limahao.ticket.R;
import com.limahao.ticket.adapter.SlidingMenuListAdapter;
import com.limahao.ticket.adapter.TicketOrderListAdapter;
import com.limahao.ticket.config.Constants;
import com.limahao.ticket.dao.TicketDao;
import com.limahao.ticket.entity.ItemTickerOrder;
import com.limahao.ticket.entity.ResponseBase;
import com.limahao.ticket.entity.ResponseTickerOrder;
import com.limahao.ticket.log.DebugLog;
import com.limahao.ticket.slidingmenu.SlidingMenu;
import com.limahao.ticket.utils.Utility;
import com.limahao.ticket.view.fresh.PullToRefreshBase;
import com.limahao.ticket.view.fresh.PullToRefreshBase.Mode;
import com.limahao.ticket.view.fresh.PullToRefreshBase.OnRefreshListener2;
import com.limahao.ticket.view.fresh.PullToRefreshBase.State;
import com.limahao.ticket.view.fresh.PullToRefreshListView;

/**
 * @Title: TicketOrder.java
 * 
 * @author email: <a href="cj@ewppay.com">陈杰</a>
 * @date 2014-8-4 下午1:29:28
 * @version :
 * @Description: 用户车票订购列表查询
 */
public class TicketOrder extends BaseSlidingFragmentActivity implements
		OnRefreshListener2<ListView> {
	private final String TAG = "TicketOrder";
	private LinearLayout left_layout, right_layout;
	private TicketOrderListAdapter orderadapter;
	private PullToRefreshListView orderlist;

	// 侧滑菜单
	private SlidingMenu sm;
	private ListView menulist;
	private String menuTitle[];
	private String menuVal[];
	private SlidingMenuListAdapter smAdapter;

	// 筛选订单类型 默认全部 为空
	private String orderType = "";

	// 分页相关
	private int currentPage = 1; // 当前页 默认第一页
	private int pageSize = 20; // 最大页条数 默认20条
	private int currentPageSize = 0; // 当前返回页条数，默认是0

	// 是否加载更多
	private boolean isloadingMore = false; // 默认为否

	// 消息设定flag
	private boolean isNotice = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		DebugLog.i(TAG, "onCreate");
		setContentView(R.layout.ticket_order);
		inititle();
		initHttpFailView();
		findView();
		initSlidingMenu();
		initData();
	}

	private void initData() {
		currentPage = 1; // 页数置为第一页
		TicketOrderAsy ticketasy = new TicketOrderAsy(this);
		ticketasy.execute();
	}

	private void inititle() {
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
				R.string.include_ticket_order));
		center_tv.setVisibility(View.VISIBLE);

		right_layout = (LinearLayout) findViewById(R.id.include_right_layout);
		right_layout.setVisibility(View.VISIBLE);
		right_layout.setOnClickListener(this);
		TextView right_tv = (TextView) findViewById(R.id.include_right_tv);
		right_tv.setText(this.getResources().getString(
				R.string.ticker_query_filter));
		right_tv.setVisibility(View.VISIBLE);
	}

	/**
	 * 侧滑初期化
	 */
	private void initSlidingMenu() {
		setBehindContentView(R.layout.behind_slidingmenu);
		// customize the SlidingMenu
		sm = getSlidingMenu();
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		// sm.setFadeDegree(0.35f);//
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		sm.setShadowDrawable(R.drawable.slidingmenu_shadow);// 阴影
		// sm.setShadowWidth(20);
		sm.setBehindScrollScale(0);
		sm.setMode(SlidingMenu.RIGHT);

		// 隐藏标题栏
		findViewById(R.id.layout_sliding_menu_title).setVisibility(View.GONE);

		// 设置菜单内容
		menuTitle = getResources().getStringArray(R.array.list_order_title);
		menuVal = getResources().getStringArray(R.array.list_order_value);
		smAdapter = new SlidingMenuListAdapter(this);

		List<String> tls = new ArrayList<String>();
		Collections.addAll(tls, menuTitle);
		smAdapter.appendToList(tls);
		menulist = (ListView) findViewById(R.id.listview_sliding_show);
		menulist.setAdapter(smAdapter);
		smAdapter.setSelectedItem(0); // 默认选中第一个
		menulist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 点击筛选时间
				smAdapter.setSelectedItem(position);
				orderType = menuVal[position];
				DebugLog.d(TAG, "点击筛选订单：" + menuVal[position]);
				// 收回菜单
				toggle();
				// 查询
				initData();
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_MENU) {
			if (sm.isMenuShowing()) {
				toggle();
			} else {
				showMenu();
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == left_layout) {
			finish(); // 返回
		} else if (v == right_layout) {
			showMenu();
			// 点击刷新
		} else if (v == layout_http_fail) {
			initData();
		}
	}

	@Override
	protected void findView() {
		orderlist = (PullToRefreshListView) findViewById(R.id.ticket_order_list);
		orderlist.setOnRefreshListener(this);
		orderadapter = new TicketOrderListAdapter(this);
		orderlist.setMode(Mode.BOTH); // 设置为两头刷新
		Typeface tf = Typeface.DEFAULT;
		orderlist.getLoadingLayoutProxy(true, true).setTextTypeface(tf);

		// 加载时的提示
		orderlist.getLoadingLayoutProxy(false, true).setPullLabel(
				getString(R.string.xlistview_footer_hint_normal));
		orderlist.getLoadingLayoutProxy(false, true).setRefreshingLabel(
				getString(R.string.xlistview_header_hint_loading));
		orderlist.getLoadingLayoutProxy(false, true).setReleaseLabel(
				getString(R.string.xlistview_footer_hint_ready));
		orderlist.setAdapter(orderadapter);
		orderlist.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long id) {
				final ItemTickerOrder item = (ItemTickerOrder) orderadapter
						.getItem(position - 1);
				Bundle bd = new Bundle();
				bd.putString(Constants.ParamKey.Key_Ticket_Order_No,
						item.getOrderNo());
				bd.putString(Constants.ParamKey.Key_Ticket_Order_Status, item.getStatus());
				int status = Integer.valueOf(item.getStatus());
				switch (status) {

				// 等待付款 0 成功订单 1 失败订单3 异常订单 9
				// 等待付款 0 成功订单 1 失败订单 3 异常订单 9 （这个不用筛选，查询全部的时候查询出来就可以）
				case 0:
					// 等待支付界面
					startActivityForRet(TicketOrderPayDetial.class, bd,
							Constants.ActionCode.ACT_ORDER_PAY);
					break;
				case 1:
					dropToNextActivity(TicketOrderSuccesDetial.class, bd);
					break;
				case 3:
					dropToNextActivity(TicketOrderFailDetial.class, bd);
					break;
				case 9:
					dropToNextActivity(TicketOrderFailDetial.class, bd);
					break;
				default:
					break;
				}

				TextView tvStatus = (TextView) view
						.findViewById(R.id.order_status_tv);
				tvStatus.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						Bundle bundle = new Bundle();
						bundle.putString(
								Constants.ParamKey.Key_Ticket_Order_No,
								item.getOrderNo());
						// 去支付界面
						startActivityForRet(TicketOrderPay.class, bundle,
								Constants.ActionCode.ACT_ORDER_PAY);
					}
				});

			}
		});
	}

	/**
	 * 支付成功刷新控制
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode != Activity.RESULT_OK)
			return;
		switch (requestCode) {
		// 如果支付成功返回订单界面则需重新刷新更新状态
		case Constants.ActionCode.ACT_ORDER_PAY:
			isNotice = false; // 重新刷 通知的状态
			initData();
			break;
		}

	}

	private void updateRefreshTime() {
		String label = DateUtils.formatDateTime(this,
				System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
						| DateUtils.FORMAT_SHOW_DATE
						| DateUtils.FORMAT_ABBREV_ALL);

		// Update the LastUpdatedLabel
		orderlist.getLoadingLayoutProxy(true, false).setLastUpdatedLabel(
				getString(R.string.xlistview_header_last_time) + label);
	}

	// 放到设置完currentPageSize数后执行
	private void setUpRefreshState() {

		orderlist.onRefreshComplete();
		// 如果返回的一页条数小于最大条数说明有下一页 -1为网络请求失败
		if (currentPageSize < pageSize && currentPageSize >= 0) {
			// 否则是最后一页只能刷新不能加载更多
			orderlist.setMode(Mode.PULL_FROM_START);
			if (isloadingMore) {
				showShortToast("已经是最后一页");
			}
		} else {
			orderlist.setMode(Mode.BOTH);
		}

		// 如果请求页条数为0或-1 并且当前正在加载更多操作 默认为请求失败 页数复位
		if (currentPageSize <= 0 && isloadingMore) {
			currentPage--;
		}

		isloadingMore = false; // 加载更多标志复位
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// 刷新
		initData();
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// 加载更多
		currentPage++; // 页数加一
		isloadingMore = true; // 设置加载更多标志
		TicketOrderAsy ticketasy = new TicketOrderAsy(this);
		ticketasy.execute();
	}

	private class TicketOrderAsy extends BaseAsyncTask {
		public TicketOrderAsy(BaseActivity activity) {
			super(activity);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (!(orderlist.getState() == State.REFRESHING)) { // 刷新过程不弹提示框，用刷新框架自带的加载动画
				showResultDialog();
			}
			currentPageSize = 0; // 每次请求前当前页数置0
		}

		@Override
		protected ResponseBase doInBackground(Void... params) {
			TicketDao dao = new TicketDao(TicketOrder.this);
			HashMap<String, String> ticketorderMap = new HashMap<String, String>();
			ticketorderMap.put("regphone", mApplication.getUserInfo()
					.getPHONE());
			ticketorderMap.put("password", mApplication.getUserInfo()
					.getPassword());
			ticketorderMap.put("currentpage", currentPage + "");
			ticketorderMap.put("pagesize", pageSize + "");
			if (!Utility.isNullOrEmpty(orderType)) {
				ticketorderMap.put("status", orderType);
			}
			return dao.getTitckerOrder(ticketorderMap);
		}

		@Override
		protected void onPostExecute(ResponseBase result) {
			super.onPostExecute(result);
			dismissResultDialog();
			if (!isloadingMore) { // 如果是加载更多不清空，否则是刷新需清空
				orderadapter.clear();
				updateRefreshTime(); // 更新刷新时间
			}
			if (result != null) {
				if (Constants.CD_SUCCESS.equals(result.getERR_INFO()
						.getERROR_CODE())) {
					hideHttpFailView();// 隐藏请求失败界面
					ResponseTickerOrder rt = (ResponseTickerOrder) result;
					currentPageSize = rt.getRET_INFO().getTrade_Order_LIST()
							.size();
					orderadapter.appendToList(rt.getRET_INFO()
							.getTrade_Order_LIST());

					// 加载成功后 如没有等待支付的订单则 取消消息通知 设定一次就够了
					if (!isNotice) {
						isNotice = true;
						if (!orderadapter.isWaitingPay()) { // 遍历是否有未支付订单
							// 进入订票列表后 默认用户已查看消息 取消消息通知
							MainActivity mainAct = (MainActivity) AppManager
									.getAppManager().getActivity(
											MainActivity.class);
							Message msg = new Message();
							msg.what = Constants.ActionCode.ACT_MESSAGE_CANCEL;
							mainAct.mHandler.sendMessage(msg);
						}
					}
				}
			} else {
				currentPageSize = -1; // 网络请求失败
				if (!isloadingMore) { // 如果是加载更多不显示
					showHttpFailView();// 显示网络失败界面
				}
			}
			// 放到设置完currentPageSize数后执行
			setUpRefreshState();
		}

	}

}

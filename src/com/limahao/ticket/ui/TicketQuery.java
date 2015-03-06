package com.limahao.ticket.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.limahao.ticket.BaseActivity;
import com.limahao.ticket.BaseAsyncTask;
import com.limahao.ticket.BaseSlidingFragmentActivity;
import com.limahao.ticket.R;
import com.limahao.ticket.adapter.SlidingMenuListAdapter;
import com.limahao.ticket.adapter.TicketListAdapter;
import com.limahao.ticket.config.Constants;
import com.limahao.ticket.dao.TicketDao;
import com.limahao.ticket.entity.ResponseBase;
import com.limahao.ticket.entity.ResponseTicker;
import com.limahao.ticket.log.DebugLog;
import com.limahao.ticket.slidingmenu.SlidingMenu;
import com.limahao.ticket.view.fresh.PullToRefreshBase;
import com.limahao.ticket.view.fresh.PullToRefreshBase.Mode;
import com.limahao.ticket.view.fresh.PullToRefreshBase.OnRefreshListener2;
import com.limahao.ticket.view.fresh.PullToRefreshBase.State;
import com.limahao.ticket.view.fresh.PullToRefreshListView;

public class TicketQuery extends BaseSlidingFragmentActivity implements
		OnRefreshListener2<ListView> {
	private final String TAG = "TicketQuery";
	private LinearLayout left_layout, right_layout;
	private PullToRefreshListView list;
	private View layout_day_select;
	private TextView dep_des_tv;
	private TextView day_before_iv;
	private TextView day_after_tv;
	private TicketListAdapter ticketadapter;
	private int maxDate;
	private int dayDate = 0;
	private Calendar calendar1;
	private String curData;
	private ArrayList<String> mDateDisStrings;

	// 侧滑菜单
	private SlidingMenu sm;
	private ListView menulist;
	private String menuTitle[];
	private String menuVal[];
	private SlidingMenuListAdapter smAdapter;

	// 筛选时间段 默认全天
	private String startTime = "0000";
	private String endTime = "2359";
	private String endStationId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		DebugLog.i(TAG, "onCreate");
		setContentView(R.layout.ticket_query);
		inititle();
		initHttpFailView();
		findView();
		initDay();
		initSlidingMenu();
		initData();
	}

	private void initDay() {
		// TODO Auto-generated method stub
		maxDate = Integer.valueOf(getIntent().getStringExtra(
				Constants.ParamKey.Key_Pre_Date));
		mDateDisStrings = new ArrayList<String>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Calendar calendar = Calendar.getInstance();
		try {
			Date date = sdf.parse(getIntent().getStringExtra(
					Constants.ParamKey.Key_Start_Date));
			calendar.setTime(date);
			mDateDisStrings.add((String) DateFormat
					.format("yyyyMMdd", calendar));
			for (int i = 0; i < maxDate - 1; ++i) {
				calendar.add(Calendar.DAY_OF_YEAR, 1);
				mDateDisStrings.add((String) DateFormat.format("yyyyMMdd",
						calendar));
			}
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
			Date curdate = sdf1.parse(getIntent().getStringExtra(
					Constants.ParamKey.Key_Leave_Date).substring(0, 10));
			calendar1 = Calendar.getInstance();
			calendar1.setTime(curdate);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initData() {
		try {
			String cur = (String) DateFormat.format("yyyyMMdd", calendar1);
			for (int i = 0; i < mDateDisStrings.size(); i++) {
				if (cur.equals(mDateDisStrings.get(i))) {
					dayDate = i;
				}
			}
			curData = (String) DateFormat.format("yyyy-MM-dd", calendar1);
			dep_des_tv.setText((String) DateFormat.format("yyyy-MM-dd EEEE",
					calendar1));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (dayDate == 0) {
			day_before_iv.setEnabled(false);
			day_after_tv.setEnabled(true);
		} else if (dayDate == maxDate - 1) {
			day_before_iv.setEnabled(true);
			day_after_tv.setEnabled(false);
		} else {
			day_before_iv.setEnabled(true);
			day_after_tv.setEnabled(true);
		}
		TicketQueryAsy ticketasy = new TicketQueryAsy(this);
		ticketasy.execute();

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
		center_tv.setText(this.getResources().getString(R.string.departure_txt)
				+ "-"
				+ getIntent()
						.getStringExtra(Constants.ParamKey.Key_End_Station));
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

		// 设置菜单内容
		menuTitle = getResources().getStringArray(R.array.list_time_title);
		menuVal = getResources().getStringArray(R.array.list_time_value);
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
				String[] timeRange = menuVal[position].split("–");
				startTime = timeRange[0];
				endTime = timeRange[1];
				smAdapter.setSelectedItem(position);
				DebugLog.d(TAG, "点击筛选时间：" + timeRange[0] + " - " + timeRange[1]);
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
		} else if (v == day_before_iv) {
			dayDate = dayDate - 1;
			calendar1.add(Calendar.DAY_OF_YEAR, -(dayDate - (dayDate - 1)));
			initData();
		} else if (v == day_after_tv) {
			dayDate = dayDate + 1;
			calendar1.add(Calendar.DAY_OF_YEAR, dayDate - (dayDate - 1));
			initData();
			// 点击刷新
		} else if (v == layout_http_fail) {
			initData();
		}

	}

	@Override
	protected void findView() {
		dep_des_tv = (TextView) findViewById(R.id.dep_des_tv);
		// dep_des_tv.setText(getIntent().getStringExtra(Constants.ParamKey.Key_Leave_Date));
		layout_day_select = findViewById(R.id.layout_day_select);
		layout_day_select.setVisibility(View.GONE);
		day_before_iv = (TextView) findViewById(R.id.day_before_iv);
		day_before_iv.setOnClickListener(this);
		day_after_tv = (TextView) findViewById(R.id.day_after_tv);
		day_after_tv.setOnClickListener(this);
		list = (PullToRefreshListView) findViewById(R.id.ticket_query_list);
		list.setOnRefreshListener(this);
		list.setMode(Mode.PULL_FROM_START);
		Typeface tf = Typeface.DEFAULT;
		list.getLoadingLayoutProxy().setTextTypeface(tf);

		// 加载时的提示
		list.getLoadingLayoutProxy(false, true).setPullLabel(
				getString(R.string.xlistview_footer_hint_normal));
		list.getLoadingLayoutProxy(false, true).setRefreshingLabel(
				getString(R.string.xlistview_header_hint_loading));
		list.getLoadingLayoutProxy(false, true).setReleaseLabel(
				getString(R.string.xlistview_footer_hint_ready));

		endStationId = getIntent().getStringExtra(
				Constants.ParamKey.Key_City_ID); // 终点站id

		ticketadapter = new TicketListAdapter(this);
		list.setAdapter(ticketadapter);
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mApplication.getParams().put(
						Constants.ParamKey.Key_Ticket_Detail,
						ticketadapter.getItem(position - 1));
				Bundle bd = new Bundle();
				// 终点站 id
				bd.putString(Constants.ParamKey.Key_City_ID, endStationId);
				// 查询详情界面即下单界面
				startActivityForRet(TicketQueryDetail.class, bd,
						Constants.ActionCode.ACT_ORDER);
			}
		});
	}

	/**
	 * 下单成功跳转控制
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode != Activity.RESULT_OK)
			return;
		switch (requestCode) {
		// 如果下单成功则刷新查询车次列表界面
		case Constants.ActionCode.ACT_ORDER:
			initData();
			break;
		}

	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		initData();
	}

	private void updateRefreshTime() {
		String label = DateUtils.formatDateTime(TicketQuery.this,
				System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
						| DateUtils.FORMAT_SHOW_DATE
						| DateUtils.FORMAT_ABBREV_ALL);

		// Update the LastUpdatedLabel
		list.getLoadingLayoutProxy(true, false).setLastUpdatedLabel(
				getString(R.string.xlistview_header_last_time) + label);
	}

	private void setUpRefreshState() {
		list.onRefreshComplete();
		list.setMode(Mode.PULL_FROM_START);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub

	}

	private class TicketQueryAsy extends BaseAsyncTask {
		public TicketQueryAsy(BaseActivity activity) {
			super(activity);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

			if (!(list.getState() == State.REFRESHING)) { // 刷新过程不弹提示框，用刷新框架自带的加载动画
				showResultDialog();
			}

		}

		@Override
		protected ResponseBase doInBackground(Void... params) {
			// TODO Auto-generated method stub
			TicketDao dao = new TicketDao(TicketQuery.this);
			HashMap<String, String> ticketMap = new HashMap<String, String>();
			ticketMap.put("endstationid", endStationId);
			ticketMap.put("leavedate", curData);
			ticketMap.put("starttime", startTime);
			ticketMap.put("endtime", endTime);
			return dao.getTitckerinfo(ticketMap);
		}

		@Override
		protected void onPostExecute(ResponseBase result) {
			super.onPostExecute(result);
			dismissResultDialog();
			setUpRefreshState();
			updateRefreshTime();
			ticketadapter.clear(); // 清除上次保存数据
			if (result != null) {
				layout_day_select.setVisibility(View.VISIBLE);
				if (Constants.CD_SUCCESS.equals(result.getERR_INFO()
						.getERROR_CODE())) {
					hideHttpFailView();// 隐藏请求失败界面
					ResponseTicker rt = (ResponseTicker) result;
					ticketadapter.appendToList(rt.getRET_INFO()
							.getBUS_TRIP_LIST());
				}
			} else {
				showHttpFailView();// 显示网络失败界面
			}
		}

	}

}

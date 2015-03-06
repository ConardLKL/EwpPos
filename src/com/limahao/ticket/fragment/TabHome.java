package com.limahao.ticket.fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.format.DateFormat;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.limahao.ticket.BaseActivity;
import com.limahao.ticket.BaseAsyncTask;
import com.limahao.ticket.BaseFragment;
import com.limahao.ticket.R;
import com.limahao.ticket.config.Constants;
import com.limahao.ticket.dao.TicketDao;
import com.limahao.ticket.db.provider.PreDateDB;
import com.limahao.ticket.entity.ItemPreDate;
import com.limahao.ticket.entity.ResponseBase;
import com.limahao.ticket.entity.ResponsePreDate;
import com.limahao.ticket.https.NetWorkHelper;
import com.limahao.ticket.log.DebugLog;
import com.limahao.ticket.ui.CitySelect;
import com.limahao.ticket.ui.TicketQuery;
import com.limahao.ticket.ui.TicketQueryDetail;
import com.limahao.ticket.ui.Website;
import com.limahao.ticket.utils.Utility;
import com.limahao.ticket.view.NumberPickerPopupWindow;

public class TabHome extends BaseFragment {
	private final String TAG = "TabHome";
	private TextView date_tv_select;
	private TextView destination_tv;
	private NumberPickerPopupWindow pickerPopupWindow;
	private RadioGroup rgMemu;
	private LinearLayout layoutInput;
	private RelativeLayout layoutSearch;
	private ItemPreDate item;
	private SimpleDateFormat dateformat;
	private Calendar calendar = Calendar.getInstance();
	private String city;
	private String cityID;
	private boolean isFirst = false;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		DebugLog.i(TAG, "TabHome");
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		DebugLog.i(TAG, "onCreate");
	}
	
	@Override
	public void onResume() {
		super.onResume();
		init(); // 初期化处理
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		DebugLog.i(TAG, "onCreateView");
		super.onCreateView(inflater, container, savedInstanceState);
		mView = inflater.inflate(R.layout.tab_home, container, false);
		inititle();
		findViewById();
		settxt();
		setListener();
		return mView;
	}
	private void settxt() {
		// TODO Auto-generated method stub
		try {
			SpannableStringBuilder spannableStringBuilder = null;
			if (item != null && !item.getCurDate().equals("")) {
				Date date = dateformat.parse(item.getCurDate());
				calendar.setTime(date);
				spannableStringBuilder = new SpannableStringBuilder((String) DateFormat.format("yyyy-MM-dd 今天",calendar));
				spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.RED),11, 13, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
				date_tv_select.setText(spannableStringBuilder);
			}else{
				calendar.setTimeInMillis(calendar.getTimeInMillis());
				spannableStringBuilder = new SpannableStringBuilder((String) DateFormat.format("yyyy-MM-dd 今天",calendar));
				spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.RED),11, 13, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
				date_tv_select.setText(spannableStringBuilder);
			}
		} catch (ParseException e) {   
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void inititle() {
		// TODO Auto-generated method stub
		LinearLayout center_layout = (LinearLayout) findViewById(R.id.include_center_layout);
		center_layout.setVisibility(View.VISIBLE);
		ImageView center_imag = (ImageView) findViewById(R.id.include_center_img);
		center_imag.setImageResource(R.drawable.include_center_img);
		center_imag.setVisibility(View.VISIBLE);
		TextView center_tv = (TextView) findViewById(R.id.include_center_tv);
		center_tv.setText(getActivity().getResources().getString(R.string.include_home_txt));
		center_tv.setVisibility(View.VISIBLE);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == date_tv_select ) {
			// 预售票信息为空
			if(item == null || item.getCurDate().equals("")){
				init();
				return;
			}
			pickerPopupWindow = new NumberPickerPopupWindow(getActivity(), date_tv_select,item);
			pickerPopupWindow.setAnimationStyle(R.style.PopupAnimation);
			pickerPopupWindow.showAtLocation(getActivity().findViewById(R.id.home), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0,0);
			pickerPopupWindow.update();
		}else if(v == layoutSearch){
			// 预售票信息为空
			if(item == null || item.getCurDate().equals("")){
				init();
				return;
			}
			
			if(!Utility.isNullOrEmpty(cityID)){
				Bundle bundle = new Bundle();
				bundle.putString(Constants.ParamKey.Key_End_Station,destination_tv.getText().toString().trim());
				bundle.putString(Constants.ParamKey.Key_Leave_Date, date_tv_select.getText().toString().trim());
				bundle.putString(Constants.ParamKey.Key_City_ID,cityID);
				bundle.putString(Constants.ParamKey.Key_Pre_Date,item.getPreDate());
				bundle.putString(Constants.ParamKey.Key_Start_Date,item.getCurDate());
				startActivity(TicketQuery.class,bundle);
			}else {
				showShortToast(getString(R.string.home_destination_empty_txt));
			}

		}else if(v == destination_tv){
			startActivityForRet(CitySelect.class, null, Constants.ActionCode.ACT_CITY_SELECT);
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK)
			return;
		switch (requestCode) {
			// 城市选择回来
			case Constants.ActionCode.ACT_CITY_SELECT:
				Bundle b = data.getExtras(); // data为B中回传的Intent
				city = b.getString(Constants.ParamKey.Key_City_Name); // 城市名
				cityID = b.getString(Constants.ParamKey.Key_City_ID); // id
				if(!Utility.isNullOrEmpty(city)){
					destination_tv.setText(city);
					// 选择完后改变颜色
					destination_tv.setTextColor(getResources().getColor(R.color.font_color_black));
				}
				break;
			// 周边网点回来切回车票查询状态
			case Constants.ActionCode.ACT_WEBSIT:
				//rgMemu.check(R.id.memu_ticket_query_rb);
				break;
		}
	}

	@Override
	protected void findViewById() {
		destination_tv = (TextView) findViewById(R.id.home_destination_tv_selector);
		
		date_tv_select = (TextView) findViewById(R.id.home_date_tv_select);
		rgMemu = (RadioGroup)findViewById(R.id.tab_home_rg_memu);
		layoutInput = (LinearLayout)findViewById(R.id.tab_home_layout_queryinput);
		layoutSearch = (RelativeLayout)findViewById(R.id.tab_home_layout_search);
	}

	@Override
	protected void setListener() {
		// TODO Auto-generated method stub
		date_tv_select.setOnClickListener(this);
		layoutSearch.setOnClickListener(this);
		destination_tv.setOnClickListener(this);
		rgMemu.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup rg, int checkedId) {
				// 选中车票查询
				if(checkedId == R.id.memu_ticket_query_rb){
					layoutInput.setVisibility(View.VISIBLE);
					layoutSearch.setVisibility(View.VISIBLE);
				} else if(checkedId == R.id.memu_ticket_website_rb) {
					// 选中周边购票网点
					layoutInput.setVisibility(View.GONE);
					layoutSearch.setVisibility(View.GONE);
					
					// 跳转到周边网点
					startActivityForRet(Website.class, null, Constants.ActionCode.ACT_WEBSIT);
					
					// 跳转后 选中车票查询 没办法了只能这么搞，因为跳转前处于选中状态 跳转会来后 周边网点会再次被选中
					rgMemu.check(R.id.memu_ticket_query_rb);
				}
			}
		});
	}
	
	/**
	 * 初期化处理
	 */
	@Override
	protected void init(){
//		// 获取预售票信息
//		showShortToast(getResources().getString(R.string.home_get_predate));
    	// 判断是否有网络
		if (NetWorkHelper.isNetworkAvailable(mActivity)) {
			// 获取预售票信息
			if(item == null){
				PreDateDB db = new PreDateDB(mActivity);
				item = db.queryPreDate(); // 查询预售票信息
				db.dbClose(); // 关闭数据库
			}
			// 每天取一次预售票时间信息
			dateformat = new SimpleDateFormat("yyyyMMdd");
			try {
				String today = dateformat.format(new Date());
				// 日期不相等则请求新的预售票信息
				if(item == null || !today.equals(item.getCurDate())){
					// 请求预售票信息
					DebugLog.d(TAG, "请求预售票时间");
					(new PreDateTsk(mActivity)).execute();
				}
			} catch (Exception e) {
				DebugLog.d(TAG, e.getMessage());
				e.printStackTrace();
			}

		} else {
			// 提示网络没连接
			showLongToast(getResources().getString(R.string.httpisNull));
		}
    }
	
	/**
	 * 获取预售天数和单订单售票张数
	 * 
	 * @author 陈杰
	 * 
	 */
	class PreDateTsk extends BaseAsyncTask {

		public PreDateTsk(BaseActivity activity) {
			super(activity);
		}

		@Override
		protected void onPreExecute() {
			showResultDialog();
		}

		@Override
		protected ResponseBase doInBackground(Void... params) {
			try {
				return new TicketDao(mActivity).getPreDate();
			} catch (Exception e) {
				DebugLog.d(TAG, e.getMessage());
			}
			return null;
		}

		@Override
		protected void onPostExecute(ResponseBase result) {
			dismissResultDialog();
			super.onPostExecute(result);
			if (result != null) {
				if (Constants.CD_SUCCESS.equals(result.getERR_INFO()
						.getERROR_CODE())) {
					ResponsePreDate ret = (ResponsePreDate) result;
					item = ret.getRET_INFO(); // 更新预售票信息
					PreDateDB db = new PreDateDB(mActivity);
					db.updatePreDate(ret.getRET_INFO()); // 更新预售票信息
					db.dbClose(); // 关闭数据库
					
					// 更新默认选择时间
					if(!isFirst){
						isFirst = true;
						settxt();
					}
				}
			}
		}
	}
}

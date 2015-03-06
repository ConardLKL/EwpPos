package com.limahao.ticket.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.limahao.ticket.BaseActivity;
import com.limahao.ticket.BaseAsyncTask;
import com.limahao.ticket.R;
import com.limahao.ticket.config.Constants;
import com.limahao.ticket.dao.TicketDao;
import com.limahao.ticket.entity.ItemCity;
import com.limahao.ticket.entity.ResponseBase;
import com.limahao.ticket.entity.ResponseEndStation;
import com.limahao.ticket.view.city.BladeView;
import com.limahao.ticket.view.city.BladeView.OnItemClickListener;
import com.limahao.ticket.view.city.CityAdapter;
import com.limahao.ticket.view.city.PinnedHeaderListView;
import com.limahao.ticket.view.city.SearchCityAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 
 * @className CityList
 * @author 陈杰
 * @date 2014-07-30上午8:44:17
 * @类描述 站点城市类表界面
 */
public class CitySelect extends BaseActivity implements OnClickListener,
		TextWatcher {
	private LinearLayout layoutBack;
	private EditText mSearchEditText;
	private ImageButton mClearSearchBtn;
	private View mCityContainer;
	private View mSearchContainer;
	private View mSearchBox;
	private PinnedHeaderListView mCityListView;
	private BladeView mLetter;
	private ListView mSearchListView;
	private SearchCityAdapter mSearchCityAdapter;
	private CityAdapter mCityAdapter;
	// 首字母集
	private List<String> mSections;
	// 根据首字母存放数据
	private Map<String, List<ItemCity>> mMap;
	// 首字母位置集
	private List<Integer> mPositions;
	// 首字母对应的位置
	private Map<String, Integer> mIndexer;
	private InputMethodManager mInputMethodManager;
	private static final String FORMAT = "^[a-z,A-Z].*$";
	private List<ItemCity> mCityList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.city_main_view);
		iniTitle();
		initHttpFailView();
		findView();
		initData();
	}

	private void initData() {
		CityAsy cityAsy = new CityAsy(this);
		cityAsy.execute();
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		// 如何搜索字符串长度为0，是否隐藏输入法
		// if(TextUtils.isEmpty(s)){
		// mInputMethodManager.hideSoftInputFromWindow(
		// mSearchEditText.getWindowToken(), 0);
		// }
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub

		mSearchCityAdapter = new SearchCityAdapter(CitySelect.this, mCityList);
		mSearchListView.setAdapter(mSearchCityAdapter);
		mSearchListView.setTextFilterEnabled(true);
		if (mCityList.size() < 1 || TextUtils.isEmpty(s)) {
			mCityContainer.setVisibility(View.VISIBLE);
			mSearchContainer.setVisibility(View.INVISIBLE);
			mClearSearchBtn.setVisibility(View.GONE);
		} else {
			mClearSearchBtn.setVisibility(View.VISIBLE);
			mCityContainer.setVisibility(View.INVISIBLE);
			mSearchContainer.setVisibility(View.VISIBLE);
			mSearchCityAdapter.getFilter().filter(s);
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == layoutBack) {
			if (imm != null && imm.isActive() && getCurrentFocus() != null) {
				imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
			}
			finish();
		} else if (v == mClearSearchBtn) {
			if (!TextUtils.isEmpty(mSearchEditText.getText().toString())) {
				mSearchEditText.setText("");
				mInputMethodManager.hideSoftInputFromWindow(
						mSearchEditText.getWindowToken(), 0);
			}
			// 点击刷新
		} else if (v == layout_http_fail) {
			initData();
		}
	}

	/**
	 * 配置标题栏
	 */
	private void iniTitle() {
		layoutBack = (LinearLayout) findViewById(R.id.include_left_layout);
		layoutBack.setVisibility(View.VISIBLE);
		layoutBack.setOnClickListener(this);
		LinearLayout centerLayout = (LinearLayout) findViewById(R.id.include_center_layout);
		centerLayout.setVisibility(View.VISIBLE);
		TextView tvCenter = (TextView) findViewById(R.id.include_center_tv);
		tvCenter.setVisibility(View.VISIBLE);
		tvCenter.setText(getString(R.string.include_station_search_txt));
	}

	@Override
	protected void findView() {

		mSearchEditText = (EditText) findViewById(R.id.search_edit);
		mSearchEditText.addTextChangedListener(this);
		mClearSearchBtn = (ImageButton) findViewById(R.id.ib_clear_text);
		mClearSearchBtn.setOnClickListener(this);

		mCityContainer = findViewById(R.id.city_content_container);
		mSearchContainer = findViewById(R.id.search_content_container);
		mSearchBox = findViewById(R.id.search_container);
		mSearchBox.setVisibility(View.GONE);
		mCityListView = (PinnedHeaderListView) findViewById(R.id.citys_list);
		mLetter = (BladeView) findViewById(R.id.citys_bladeview);
		mLetter.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(String s) {
				if (mIndexer.get(s) != null) {
					mCityListView.setSelection(mIndexer.get(s));
				}
			}
		});
		mLetter.setVisibility(View.GONE);
		mSearchListView = (ListView) findViewById(R.id.search_list);
		// mSearchListView.setEmptyView(findViewById(R.id.search_empty));
		mSearchContainer.setVisibility(View.GONE);
		mSearchListView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				mInputMethodManager.hideSoftInputFromWindow(
						mSearchEditText.getWindowToken(), 0);
				return false;
			}
		});
		mCityListView
				.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						// L.i(mCityAdapter.getItem(position).toString());
						// 收回键盘
						if (imm != null && imm.isActive() && getCurrentFocus() != null) {
							imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
									InputMethodManager.HIDE_NOT_ALWAYS);
						}
						startActivity(mCityAdapter.getItem(position)
								.getEndStation(), mCityAdapter
								.getItem(position).getEndStationId());
					}
				});

		mSearchListView
				.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						// L.i(mSearchCityAdapter.getItem(position).toString());
						startActivity(mSearchCityAdapter.getItem(position)
								.getEndStation(),
								mSearchCityAdapter.getItem(position)
										.getEndStationId());
					}
				});

	}

	private boolean prepareCityList(ResponseEndStation response) {
		mCityList = response.getRET_INFO().getBUS_TRIP_LIST();
		for (ItemCity city : mCityList) {
			String firstName = city.getFirstLetter();// 第一个字拼音的第一个字母
			if (firstName.matches(FORMAT)) {
				if (mSections.contains(firstName)) {
					mMap.get(firstName).add(city);
				} else {
					mSections.add(firstName);
					List<ItemCity> list = new ArrayList<ItemCity>();
					list.add(city);
					mMap.put(firstName, list);
				}
			} else {
				if (mSections.contains("#")) {
					mMap.get("#").add(city);
				} else {
					mSections.add("#");
					List<ItemCity> list = new ArrayList<ItemCity>();
					list.add(city);
					mMap.put("#", list);
				}
			}
		}
		Collections.sort(mSections);// 按照字母重新排序
		int position = 0;
		for (int i = 0; i < mSections.size(); i++) {
			mIndexer.put(mSections.get(i), position);// 存入map中，key为首字母字符串，value为首字母在listview中位置
			mPositions.add(position);// 首字母在listview中位置，存入list中
			position += mMap.get(mSections.get(i)).size();// 计算下一个首字母在listview的位置
		}
		return true;
	}

	private void startActivity(String city, String id) {// 选中城市后...
		Intent i = new Intent();
		i.putExtra(Constants.ParamKey.Key_City_Name, city);// 城市名
		i.putExtra(Constants.ParamKey.Key_City_ID, id);// id
		setResult(RESULT_OK, i);
		finish();

	}

	private class CityAsy extends BaseAsyncTask {
		public CityAsy(BaseActivity activity) {
			super(activity);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			mCityList = new ArrayList<ItemCity>();
			mSections = new ArrayList<String>();
			mMap = new HashMap<String, List<ItemCity>>();
			mPositions = new ArrayList<Integer>();
			mIndexer = new HashMap<String, Integer>();
			showResultDialog();
		}

		@Override
		protected ResponseBase doInBackground(Void... params) {
			// TODO Auto-generated method stub
			TicketDao dao = new TicketDao(CitySelect.this);
			return dao.getEndStation();
		}

		@Override
		protected void onPostExecute(ResponseBase result) {
			// viewLoading.setVisibility(View.GONE);
			// viewEmpty.setVisibility(View.VISIBLE);
			super.onPostExecute(result);
			if (result != null) {
				if (Constants.CD_SUCCESS.equals(result.getERR_INFO()
						.getERROR_CODE())) {
					
					mSearchBox.setVisibility(View.VISIBLE);
					hideHttpFailView();// 隐藏请求失败界面
					
					ResponseEndStation rt = (ResponseEndStation) result;
					// 转大写
					for (ItemCity city : rt.getRET_INFO().getBUS_TRIP_LIST()) {
						city.toUpperCase();
					}
					prepareCityList(rt);

					mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

					mCityAdapter = new CityAdapter(CitySelect.this, mCityList,
							mMap, mSections, mPositions);
					mCityListView.setAdapter(mCityAdapter);
					mCityListView.setOnScrollListener(mCityAdapter);
					mCityListView.setPinnedHeaderView(LayoutInflater.from(
							CitySelect.this).inflate(R.layout.city_list_head,
							mCityListView, false));
					// mTitleProgressBar.setVisibility(View.GONE);
					mLetter.setVisibility(View.VISIBLE);
				} 
			} else {
				showHttpFailView();// 显示网络失败界面
			}
			dismissResultDialog();
		}
	}
}

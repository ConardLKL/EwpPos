package com.limahao.ticket.view.city;

import java.util.ArrayList;
import java.util.List;
import com.limahao.ticket.R;
import com.limahao.ticket.entity.ItemCity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

public class SearchCityAdapter extends BaseAdapter implements Filterable {

	private List<ItemCity> mAllCities;
	private List<ItemCity> mResultCities;
	private LayoutInflater mInflater;
	private Context mContext;

	// private String mFilterStr;

	public SearchCityAdapter(Context context, List<ItemCity> allCities) {
		mContext = context;
		mAllCities = allCities;
		mResultCities = new ArrayList<ItemCity>();
		mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		return mResultCities.size();
	}

	@Override
	public ItemCity getItem(int position) {
		return mResultCities.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.city_search_city_item, null);
		}
//		TextView provinceTv = (TextView) convertView
//				.findViewById(R.id.search_province);
		//provinceTv.setText(mResultCities.get(position).getProvince());
		TextView cityTv = (TextView) convertView
				.findViewById(R.id.column_title);
		cityTv.setText(mResultCities.get(position).getEndStation());
		return convertView;
	}

	@Override
	public Filter getFilter() {
		Filter filter = new Filter() {
			protected void publishResults(CharSequence constraint,
					FilterResults results) {
				mResultCities = (ArrayList<ItemCity>) results.values;
				if (results.count > 0) {
					notifyDataSetChanged();
				} else {
					notifyDataSetInvalidated();
				}
			}

			protected FilterResults performFiltering(CharSequence s) {
				String str = s.toString().trim().toUpperCase();
				// mFilterStr = str;
				FilterResults results = new FilterResults();
				ArrayList<ItemCity> cityList = new ArrayList<ItemCity>();
				if (mAllCities != null && mAllCities.size() != 0) {
					for (ItemCity cb : mAllCities) {
						// 匹配全屏、首字母、和城市名中文
						if (
								cb.getStopLetter().indexOf(str) > -1
								||
								cb.getFirstLetter().indexOf(str)>-1||
								cb.getSpell().indexOf(str) > -1
								|| cb.getEndStation().indexOf(str) > -1) {
							cityList.add(cb);
						}
					}
				}
				results.values = cityList;
				results.count = cityList.size();
				return results;
			}
		};
		return filter;
	}

}

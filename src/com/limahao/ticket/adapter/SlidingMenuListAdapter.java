package com.limahao.ticket.adapter;

import com.limahao.ticket.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * @Title: SlidingMenuListAdapter.java
 * 
 * @author email: <a href="cj@ewppay.com">陈杰</a>
 * @date 2014-7-31 下午5:18:03
 * @version :
 * @Description:
 */
public class SlidingMenuListAdapter extends AdapterBase<String> {

	public SlidingMenuListAdapter(Context context) {
		super(context);
	}

	@Override
	protected View getExView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;

		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.slidingmenu_list_item, null);
			holder = new ViewHolder();
			holder.tv = (TextView)convertView.findViewById(R.id.slidingmenu_item_tv);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.tv.setText((String)getItem(position));
		if (position == selectedItem) {
			holder.tv.setEnabled(false);
		}else{
			holder.tv.setEnabled(true);
		}
		
		return convertView;
	}

	@Override
	protected void onReachBottom() {

	}

	class ViewHolder {
		TextView tv;
	}
}

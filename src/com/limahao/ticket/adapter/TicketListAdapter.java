package com.limahao.ticket.adapter;

import java.text.NumberFormat;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.limahao.ticket.R;
import com.limahao.ticket.entity.ItemTicker;
import com.limahao.ticket.utils.Utility;

public class TicketListAdapter extends AdapterBase<ItemTicker> {
	private NumberFormat numberFormat;
	public TicketListAdapter(Context context) {
		super(context);
		numberFormat = Utility.getMoneyFormt();
	}
	
	@Override
	protected View getExView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.ticket_query_item,null);
			holder = new ViewHolder();
			holder.leave_date = (TextView) convertView.findViewById(R.id.leave_date_tv);
			holder.start_station = (TextView) convertView.findViewById(R.id.start_station_tv);
			holder.end_station = (TextView) convertView.findViewById(R.id.end_station_tv);
			holder.ticket_price = (TextView) convertView.findViewById(R.id.ticket_price_tv);
			holder.bus_type = (TextView) convertView.findViewById(R.id.bus_type_tv);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		ItemTicker item = (ItemTicker)getItem(position);
		holder.leave_date.setText(item.getLeaveTime());
		String routename = item.getRoutename();
		String[] start_station = routename.split("-");
		holder.start_station.setText(start_station[0]);
		holder.end_station.setText(start_station[1]);
		holder.ticket_price.setText("￥"+ numberFormat.format(Integer.valueOf(item.getFullPrice()) / 100)); // 票价单位分，转化为显示用的元
		holder.bus_type.setText(item.getBusType());

		return convertView;
	}

	@Override
	protected void onReachBottom() {
		// TODO Auto-generated method stub

	}

	private class ViewHolder {
		private TextView leave_date;
		private TextView start_station;
		private TextView end_station;
		private TextView ticket_price;
		private TextView bus_type;
	}
}

package com.limahao.ticket.adapter;

import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.limahao.ticket.R;
import com.limahao.ticket.config.Constants;
import com.limahao.ticket.entity.ItemTickerOrder;

public class TicketOrderListAdapter extends AdapterBase<ItemTickerOrder> {
	public TicketOrderListAdapter(Context context) {
		super(context);
	}

	public TicketOrderListAdapter(Context context,
			List<ItemTickerOrder> tickerorderItem) {
		super(context);
		appendToList(tickerorderItem);
	}

	@Override
	protected View getExView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.ticket_order_item,
					null);
			holder = new ViewHolder();
			holder.order_date_tv = (TextView) convertView
					.findViewById(R.id.order_date_tv);
			holder.order_start_station_tv = (TextView) convertView
					.findViewById(R.id.order_start_station_tv);
			holder.order_end_station_tv = (TextView) convertView
					.findViewById(R.id.order_end_station_tv);
			holder.order_tickernum_tv = (TextView) convertView
					.findViewById(R.id.order_tickernum_tv);
			holder.order_status_tv = (TextView) convertView
					.findViewById(R.id.order_status_tv);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final ItemTickerOrder item = (ItemTickerOrder) getItem(position);
		//SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd  HH:mm");
		//String dateString = formatter.format(new Date(item.getOrderTime()));
		holder.order_date_tv.setText(item.getLeaveDate() + "  "
				+ item.getLeaveTime());

		String routename = item.getRoutename();
		String[] start_station = routename.split("-");
		holder.order_start_station_tv.setText(start_station[0]);
		holder.order_end_station_tv.setText(start_station[1]);

		StringBuffer buffer = new StringBuffer();
		buffer.append(mContext.getResources().getString(
				R.string.person_order_num));
		buffer.append(item.getOrderNum());

		SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(
				buffer.toString());
		spannableStringBuilder.setSpan(new ForegroundColorSpan(mContext
				.getResources().getColor(R.color.ticket_query_price)), 4,
				buffer.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		holder.order_tickernum_tv.setText(spannableStringBuilder);

		// 等待付款 0 成功订单 1 失败订单3 异常订单 9 （这个不用筛选，查询全部的时候查询出来就可以）
		if (item.getStatus().equals(Constants.OrderStatus.Status_Order_Waiting)) {
			Resources resources = mContext.getResources();
			Drawable drawable = resources
					.getDrawable(R.drawable.ticket_btntv_yellow_bg_selector);
			holder.order_status_tv.setBackgroundDrawable(drawable);
			holder.order_status_tv.setText(mContext.getResources().getString(
					R.string.person_order_pay));
			holder.order_status_tv.setTextColor(mContext.getResources()
					.getColor(R.color.font_color_white));
			holder.order_status_tv.setEnabled(true);
		} else if (item.getStatus().equals(
				Constants.OrderStatus.Status_Order_Success)) {
			holder.order_status_tv.setBackgroundDrawable(null);
			holder.order_status_tv.setText(mContext.getResources().getString(
					R.string.person_order_success));
			holder.order_status_tv.setTextColor(mContext.getResources()
					.getColor(R.color.include_txt_color));
			holder.order_status_tv.setEnabled(false);
		} else if (item.getStatus().equals(
				Constants.OrderStatus.Status_Order_Fail)
				|| item.getStatus().equals(
						Constants.OrderStatus.Status_Order_Exception)) {
			holder.order_status_tv.setBackgroundDrawable(null);
			if(item.getStatus().equals( // 显示订票异常
					Constants.OrderStatus.Status_Order_Fail)){
				holder.order_status_tv.setText(mContext.getResources().getString(
						R.string.person_order_fail));
			} else { // 显示订票异常
				holder.order_status_tv.setText(mContext.getResources().getString(
						R.string.person_order_exception));
			}
			holder.order_status_tv.setTextColor(mContext.getResources()
					.getColor(R.color.person_info_txt));
			holder.order_status_tv.setEnabled(false);
		}
		return convertView;
	}

	/**
	 * 是否有等等支付订单
	 * 
	 * @return
	 */
	public boolean isWaitingPay() {
		for (ItemTickerOrder item : getList()) {
			// 等待付款 0
			if (Constants.OrderStatus.Status_Order_Waiting.equals(item
					.getStatus())) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected void onReachBottom() {
		// TODO Auto-generated method stub

	}

	private class ViewHolder {
		private TextView order_date_tv;
		private TextView order_start_station_tv;
		private TextView order_end_station_tv;
		private TextView order_tickernum_tv;
		private TextView order_status_tv;
	}
}

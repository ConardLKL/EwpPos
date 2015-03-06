package com.limahao.ticket.adapter;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class AdapterBase<T> extends BaseAdapter {
	
	private final List<T> mList = new LinkedList<T>();
	protected LayoutInflater layoutInflater;
	protected Context mContext;
	protected int selectedItem = -1;
	
	
	public AdapterBase(Context context) {
		layoutInflater = LayoutInflater.from(context);
		mContext = context;
	}
	
	public List<T> getList(){
		return mList;
	}
	
	public void remove(int postion){
		if ( (postion > (mList.size() - 1)) || postion < 0) {
			return;
		}
		mList.remove(postion);
		this.selectedItem = -1;
		notifyDataSetChanged();
	}
	
	public void appendToList(List<T> list) {
		if (list == null) {
			return;
		}
		mList.addAll(list);
		notifyDataSetChanged();
	}

	public void appendToTopList(List<T> list) {
		if (list == null) {
			return;
		}
		mList.addAll(0, list);
		notifyDataSetChanged();
	}

	public void clear() {
		mList.clear();
		notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		if(position > mList.size()-1){
			return null;
		}
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (position == getCount() - 1) {
			onReachBottom();
		}
		return getExView(position, convertView, parent);
	}
	
	public void setSelectedItem(int selectedItem){
		this.selectedItem = selectedItem;
		notifyDataSetInvalidated();
	}

	protected abstract View getExView(int position, View convertView, ViewGroup parent);
	protected abstract void onReachBottom();


}

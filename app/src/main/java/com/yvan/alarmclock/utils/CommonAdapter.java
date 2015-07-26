package com.yvan.alarmclock.utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

public abstract class CommonAdapter<T> extends BaseAdapter {
	protected Context mContext;
	protected List<T> mData;
	protected int mDataLayout;

	public CommonAdapter(Context context, List<T> mData, int dataLayout) {
		super();
		this.mContext = context;
		this.mData = mData;
		this.mDataLayout = dataLayout;
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public T getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = ViewHolder.get(mContext, convertView, parent,
				mDataLayout, position);
		convert(viewHolder, getItem(position));
		return viewHolder.getConvertView();
	}



	public abstract void convert(ViewHolder viewHolder, T t);

}

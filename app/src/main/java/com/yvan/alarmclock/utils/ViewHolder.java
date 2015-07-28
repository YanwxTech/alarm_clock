package com.yvan.alarmclock.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewHolder {
    private SparseArray<View> mViews;
    private int mPosition;
    private View mConvertView;

    public ViewHolder(Context context, ViewGroup parent, int layoutId,
                      int position) {
        this.mPosition = position;
        this.mViews = new SparseArray<>();
        this.mConvertView = LayoutInflater.from(context).inflate(layoutId,
                parent, false);
        this.mConvertView.setTag(this);
    }

    public static ViewHolder get(Context context, View convertView,
                                 ViewGroup parent, int layoutId, int position) {
        if (convertView == null) {
            return new ViewHolder(context, parent, layoutId, position);
        } else {
            ViewHolder viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.mPosition = position;
            return viewHolder;
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public ViewHolder setText(int viewId, String content) {
        ((TextView) getView(viewId)).setText(content);
        return this;
    }

/*    public void setImage(int viewId, Bitmap bitmap) {
        ((ImageView) getView(viewId)).setImageBitmap(bitmap);
    }

    public void setImage(int viewId, int imageId) {
        ((ImageView) getView(viewId)).setImageResource(imageId);
    }*/

    public int getPosition() {
        return mPosition;
    }

    public View getConvertView() {
        return mConvertView;
    }
}

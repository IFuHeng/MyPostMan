package com.changhong.chpostman.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.changhong.chpostman.R;
import com.changhong.chpostman.model.ParamBeen;

import java.util.List;

public class KeyValue2Adapter extends BaseAdapter {

    private Context mContext;
    private List<ParamBeen> mData;

    public KeyValue2Adapter(Context context, List<ParamBeen> data) {
        this.mContext = context;
        this.mData = data;
    }

    @Override
    public int getCount() {
        if (mData == null)
            return 0;
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        if (mData == null)
            return null;
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_key_value2, null, false);
        }

        ParamBeen param = mData.get(position);


        TextView tvKey = convertView.findViewById(android.R.id.text1);
        TextView tvValue = convertView.findViewById(android.R.id.text2);
        tvKey.setText(param.getKey());
        tvValue.setText(param.getValue());
        return convertView;
    }
}

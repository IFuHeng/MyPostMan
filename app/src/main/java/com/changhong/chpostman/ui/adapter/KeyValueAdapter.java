package com.changhong.chpostman.ui.adapter;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.Observer;

import com.changhong.chpostman.R;
import com.changhong.chpostman.model.ParamBeen;
import com.changhong.chpostman.ui.view.KeyValueInputDialog;

import java.util.List;

public class KeyValueAdapter extends BaseAdapter implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, Observer<Pair<String, String>> {

    private Context mContext;
    private List<ParamBeen> mData;
    private Observer<List<ParamBeen>> mObserver;
    private KeyValueInputDialog mDialog;
    private int mTempEditParam;
    private View mBtnNewAdd;


    public KeyValueAdapter(Context context, List<ParamBeen> data, Observer<List<ParamBeen>> observer) {
        this.mContext = context;
        this.mData = data;
        this.mObserver = observer;

        ImageView imageView = new ImageView(context);
        imageView.setImageResource(android.R.drawable.ic_input_add);
        mBtnNewAdd = imageView;
        imageView.setOnClickListener(this);
    }

    @Override
    public int getCount() {
        if (mData == null)
            return 1;
        return mData.size() + 1;
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

        if (position == mData.size()) {
            return mBtnNewAdd;
        }

        if (convertView == null || convertView == mBtnNewAdd) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_key_value, null, false);
        }

        ParamBeen param = mData.get(position);

        CheckBox checkBox = convertView.findViewById(android.R.id.checkbox);
        checkBox.setOnCheckedChangeListener(null);
        checkBox.setChecked(param.isChecked());
        checkBox.setTag(param);
        checkBox.setOnCheckedChangeListener(this);

        TextView tvKey = convertView.findViewById(android.R.id.text1);
        TextView tvValue = convertView.findViewById(android.R.id.text2);
        tvKey.setText(param.getKey());
        tvValue.setText(param.getValue());

        View btnDel = convertView.findViewById(android.R.id.button1);
        btnDel.setOnClickListener(this);
        btnDel.setTag(param);
        View btnEdit = convertView.findViewById(android.R.id.button2);
        btnEdit.setOnClickListener(this);
        btnEdit.setTag(param);

        return convertView;
    }

    @Override
    public void onClick(View v) {
        if (v == mBtnNewAdd) {
            mData.add(new ParamBeen(true, null, null));
            mObserver.onChanged(mData);
            return;
        }

        switch (v.getId()) {
            case android.R.id.button1:
                mData.remove(v.getTag());
                mObserver.onChanged(mData);
                break;
            case android.R.id.button2:
                startEditDialog((ParamBeen) v.getTag());
                break;
        }
    }

    private void startEditDialog(ParamBeen been) {
        if (mDialog == null) {
            mDialog = new KeyValueInputDialog(mContext, this);
            mDialog.setTitle(R.string.input_new_key_and_value);
        }

        mTempEditParam = mData.indexOf(been);

        mDialog.setMessage(been.getKey(), been.getValue());
        mDialog.show();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        ((ParamBeen) buttonView.getTag()).setChecked(isChecked);
        mObserver.onChanged(mData);
    }

    @Override
    public void onChanged(Pair<String, String> stringStringPair) {
        if (mTempEditParam < 0 || mTempEditParam >= mData.size())
            Toast.makeText(mContext, "mTempEditParam = " + mTempEditParam + " , out of bound size : mData size is " + getCount(), Toast.LENGTH_SHORT).show();
        else {
            ParamBeen been = mData.get(mTempEditParam);
            been.setKey(stringStringPair.first);
            been.setValue(stringStringPair.second);
            mObserver.onChanged(mData);
        }
    }
}

package com.changhong.chpostman.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.changhong.chpostman.R;
import com.changhong.chpostman.model.ParamBeen;
import com.changhong.chpostman.ui.adapter.KeyValueAdapter;
import com.changhong.chpostman.utils.CommUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HeadersFragment2 extends ListContentFragment<String> implements Observer<List<ParamBeen>> {
    protected List<ParamBeen> mData;
    protected ListView mListView;
    protected KeyValueAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().containsKey(Intent.EXTRA_DATA_REMOVED)) {
                mData = turnString2ParamList(getArguments().getString(Intent.EXTRA_DATA_REMOVED));
            }
        }
        if (mData == null)
            mData = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.part_params, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvPartTitle = view.findViewById(R.id.part_title);
        tvPartTitle.setText(R.string.part_header_title);

        mListView = view.findViewById(R.id.listView01);

        mAdapter = new KeyValueAdapter(mActivity, mData, this);
        mListView.setAdapter(mAdapter);
    }


    @Override
    public void onChanged(List<ParamBeen> paramBeens) {
        CommUtil.expandListView(mListView);
        mAdapter.notifyDataSetChanged();
        callBack(getValue());
    }

    public String getValue() {
        return getTableLayoutData(mData);
    }

    public void notifyChanged(String data) {
        List<ParamBeen> list = turnString2ParamList(data);
        for (int i = 0; i < mData.size(); i++) {
            if (mData.get(i).isChecked())
                mData.remove(i--);
        }
        mData.addAll(list);
//        Collections.sort(mData);
        CommUtil.expandListView(mListView);
        mAdapter.notifyDataSetChanged();
    }

    public List<ParamBeen> getData() {
        return mData;
    }
}

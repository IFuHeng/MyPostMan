package com.changhong.chpostman.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.changhong.chpostman.R;
import com.changhong.chpostman.model.ParamBeen;
import com.changhong.chpostman.utils.CommUtil;

import java.util.ArrayList;
import java.util.List;

public class ParamsFragment2 extends HeadersFragment2 {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvPartTitle = view.findViewById(R.id.part_title);
        tvPartTitle.setText(R.string.part_params_title);
    }

    public void notifyChanged(String data) {
        List<ParamBeen> list = turnUrl2ParamList(data);
        for (int i = 0; i < mData.size(); i++) {
            if (mData.get(i).isChecked())
                mData.remove(i--);
        }
        mData.addAll(list);
//        Collections.sort(mData);
        CommUtil.expandListView(mListView);
        mAdapter.notifyDataSetChanged();
    }

    private List<ParamBeen> turnUrl2ParamList(String url) {
        ArrayList<ParamBeen> result = new ArrayList<>();
        if (url == null)
            return result;

        int indexOfColon = url.indexOf('?');
        if (url.length() == 0 || indexOfColon == -1) {
            return result;
        }
        String params = url.substring(indexOfColon + 1);
        return turnString2ArrParamsInAndEqualFormat(params);
    }

    public String getValue() {
        return turnArrParams2StringInAndEqualFormat(mData);
    }
}

package com.changhong.chpostman.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.changhong.chpostman.R;

public class HeadersFragment extends TableContentFragment<String> implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, TextView.OnEditorActionListener {
    private TableLayout mTableLayout;
    private String mData;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().containsKey(Intent.EXTRA_DATA_REMOVED))
                mData = getArguments().getString(Intent.EXTRA_DATA_REMOVED);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.part_header, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTableLayout = view.findViewById(R.id.tableLayout01);
        TextView tvPartTitle = view.findViewById(R.id.part_title);
        tvPartTitle.setText(R.string.part_header_title);

        if (mData != null && !mData.isEmpty()) {
            setTableLayout(mTableLayout, mData, R.layout.item_header, ARRAY_TABLE_ROW_RES_ID, this, this, this);
        } else {
            mTableLayout.addView(createTableRow(R.layout.item_header, ARRAY_TABLE_ROW_RES_ID, false, null, null, this, this, this));
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_del) {
            deleteTableRow(mTableLayout, v);

            if (mTableLayout.getChildCount() < 2) {
                mTableLayout.addView(createTableRow(R.layout.item_header, ARRAY_TABLE_ROW_RES_ID, false, null, null, this, this, this));
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        ViewGroup parent = (ViewGroup) buttonView.getParent();
        TextView tvKey = (TextView) parent.getChildAt(1);
        TextView tvValue = (TextView) parent.getChildAt(2);
        boolean haveContent = tvKey.getText().length() > 0 || tvValue.getText().length() > 0;
        if (isChecked && !haveContent) {
            buttonView.setOnCheckedChangeListener(null);
            buttonView.toggle();
            buttonView.setOnCheckedChangeListener(this);
            return;
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        ViewGroup grandParent = (ViewGroup) v.getParent().getParent();
        ViewGroup parent = (ViewGroup) v.getParent();
        int index = grandParent.indexOfChild(parent);
        TextView tvKey = (TextView) parent.getChildAt(1);
        boolean haveContent = tvKey.getText().length() > 0 && v.getText().length() > 0;
        if (index == grandParent.getChildCount() - 1 && haveContent) {
            mTableLayout.addView(createTableRow(R.layout.item_header, ARRAY_TABLE_ROW_RES_ID, false, null, null, this, this, this));
            ((ViewGroup) grandParent.getChildAt(grandParent.getChildCount() - 1)).getChildAt(1).requestFocus();
        }

        return false;
    }

    public String getValue() {
        return getTableLayoutData(mTableLayout);
    }

}

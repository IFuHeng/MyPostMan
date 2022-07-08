package com.changhong.chpostman.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.changhong.chpostman.R;
import com.changhong.chpostman.model.ParamBeen;

import java.util.ArrayList;

public class ParamsFragment extends BaseFragment<ArrayList<ParamBeen>> implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, TextView.OnEditorActionListener {
    private TableLayout mTableLayout;
    private ArrayList<ParamBeen> mData;
    private String mTempValue;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().containsKey(Intent.EXTRA_DATA_REMOVED))
                mData = getArguments().getParcelableArrayList(Intent.EXTRA_DATA_REMOVED);
        }
        if (mData == null)
            mData = new ArrayList<>();
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
            for (ParamBeen mDatum : mData) {
                View row = createTableRow(mDatum);
                mTableLayout.addView(row);
            }
        } else {
            addOneRow(mTableLayout);
        }
        mTempValue = getValue();
    }

    private View createTableRow(ParamBeen param) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.item_params, null, false);
        view.setTag(param);

        View btn_del = view.findViewById(R.id.btn_del);
        CheckBox cb_choose = view.findViewById(R.id.cb_choose);
        EditText et_key = view.findViewById(R.id.et_key);
        EditText et_value = view.findViewById(R.id.et_value);

        cb_choose.setChecked(param.isChecked());
        et_key.setText(param.getKey());
        et_value.setText(param.getValue());

        et_value.addTextChangedListener(new MyTextWatcher(et_value, param));
        et_key.addTextChangedListener(new MyTextWatcher(et_key, param));
        et_value.setOnEditorActionListener(this);
        cb_choose.setOnCheckedChangeListener(this);
        btn_del.setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_del) {
            ViewGroup parent = (ViewGroup) v.getParent();
            ViewGroup grandParent = (ViewGroup) parent.getParent();

            ParamBeen paramBeen = (ParamBeen) parent.getTag();
            grandParent.removeView(parent);
            mData.remove(paramBeen);
            grandParent.postInvalidate();

            if (grandParent.getChildCount() < 3) {
                addOneRow(grandParent);
            }
            callBack(mData);
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

        ParamBeen paramBeen = (ParamBeen) parent.getTag();
        paramBeen.setChecked(isChecked);
        callBack(mData);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        ViewGroup grandParent = (ViewGroup) v.getParent().getParent();
        ViewGroup parent = (ViewGroup) v.getParent();
        int index = grandParent.indexOfChild(parent);
        TextView tvKey = (TextView) parent.getChildAt(1);
        boolean haveContent = tvKey.getText().length() > 0 && v.getText().length() > 0;
        if (index == grandParent.getChildCount() - 1 && haveContent) {
            addOneRow(grandParent);
            ((ViewGroup) grandParent.getChildAt(grandParent.getChildCount() - 1)).getChildAt(1).requestFocus();
        }

        return false;
    }

    private void addOneRow(ViewGroup parent) {
        ParamBeen been = new ParamBeen();
        mData.add(been);
        parent.addView(createTableRow(been));
    }

    public void notifyChanged(String url) {
        int indexOfColon = url.indexOf('?');
        String params = indexOfColon == -1 ? "" : url.substring(indexOfColon + 1);

        if (mTempValue != null && mTempValue.equals(params))
            return;

        for (int i = 2; i < mTableLayout.getChildCount(); i++) {
            View view = mTableLayout.getChildAt(i);
            ParamBeen been = (ParamBeen) view.getTag();
            if (been.isChecked()
                    || ((been.getKey() == null || been.getKey().length() == 0) && (been.getValue() == null || been.getValue().length() == 0))) {
                mData.remove(been);
                mTableLayout.removeView(view);
                i--;
            }
        }

        if (indexOfColon == -1) {
            addOneRow(mTableLayout);
            return;
        }

        if (params.length() == 0) {
            addOneRow(mTableLayout);
            return;
        }

        String[] arrParams = params.split("&");
        for (String param : arrParams) {
            ParamBeen been = new ParamBeen();
            been.setChecked(true);
            indexOfColon = param.indexOf('=');
            if (indexOfColon == -1)
                been.setKey(param);
            else {
                been.setKey(param.substring(0, indexOfColon));
                been.setValue(param.substring(indexOfColon + 1));
            }
            mTableLayout.addView(createTableRow(been));
        }

//        addOneRow(mTableLayout);

        mTempValue = getValue();
    }

    private class MyTextWatcher implements android.text.TextWatcher {
        private final ViewGroup parent;
        private final ParamBeen paramBeen;
        private final EditText editText;
        private final View btnDel;
        private final CheckBox checkBox;
        private final EditText editTextOther;

        public MyTextWatcher(EditText editText, ParamBeen paramBeen) {
            this.paramBeen = paramBeen;
            this.editText = editText;
            parent = (ViewGroup) editText.getParent();
            checkBox = (CheckBox) parent.getChildAt(0);
            btnDel = parent.getChildAt(3);
            if (editText.getId() == R.id.et_key)
                editTextOther = (EditText) parent.getChildAt(2);
            else
                editTextOther = (EditText) parent.getChildAt(1);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (editText.getId() == R.id.et_key) {
                paramBeen.setKey(s.toString());
            } else
                paramBeen.setValue(s.toString());

//            boolean hasCallback = false;//是否需要反馈

//            if (checkBox.isChecked()) {
//                if (s.length() == 0 && editTextOther.getText().length() == 0) {//1、 key and value both empty
//                    checkBox.setChecked(false);
//                    hasCallback = true;
//                }
//            } else {
//                if (s.length() != 0 || editTextOther.getText().length() != 0) {//2、key or value not empty but checkbox not checked
//                    checkBox.setChecked(true);
//                    hasCallback = true;
//                }
//            }

            // default、if checkbox is checked, callback and refresh


//            if (!hasCallback) {
            if (paramBeen.isChecked())
                callBack(mData);
//            }
        }
    }

    private String getValue() {
        StringBuilder sb = new StringBuilder();

        for (com.changhong.chpostman.model.ParamBeen data : mData) {
            if (!data.isChecked())
                continue;

            if (sb.length() > 0) {
                sb.append('&');

                sb.append(data.getKey() == null ? "" : data.getKey());
                sb.append('=');
                sb.append(data.getValue() == null ? "" : data.getValue());
            }

        }
        return sb.toString();
    }

    @Override
    protected void callBack(ArrayList<ParamBeen> paramBeens) {
        mTempValue = getValue();
        super.callBack(paramBeens);
    }
}

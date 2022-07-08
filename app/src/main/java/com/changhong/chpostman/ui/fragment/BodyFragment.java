package com.changhong.chpostman.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.changhong.chpostman.R;
import com.changhong.chpostman.model.BodyBeen;
import com.changhong.chpostman.utils.UriUtils;


public class BodyFragment extends TableContentFragment<BodyBeen> implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, TextView.OnEditorActionListener, AdapterView.OnItemSelectedListener {
    private static final int REQUEST_CODE_SELECT_FILE = 0x123;
    private TableLayout mTableLayout;
    private BodyBeen mData;
    private Spinner mSpinnerType;
    private ViewFlipper mViewFlipper;
    private EditText mEtBodyRow;
    private View mBtnSelectFile;
    private TextView mTvFileName;
    private Uri mUri;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().containsKey(Intent.EXTRA_DATA_REMOVED))
                mData = getArguments().getParcelable(Intent.EXTRA_DATA_REMOVED);
        }
        if (mData == null)
            mData = new BodyBeen();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.part_body, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSpinnerType = view.findViewById(R.id.spinner_body_type);
        mTableLayout = view.findViewById(R.id.tableLayout01);
        mViewFlipper = view.findViewById(R.id.viewFlipper_body);
        mEtBodyRow = view.findViewById(R.id.et_body_raw);
        mBtnSelectFile = view.findViewById(R.id.btn_select_file);
        mTvFileName = view.findViewById(R.id.tvFileName);

        if (mData != null) {
            analysisData(mData);
        }

        mSpinnerType.setOnItemSelectedListener(this);
        mBtnSelectFile.setOnClickListener(this);
        mTvFileName.setOnClickListener(this);

    }

    public void analysisData(BodyBeen data) {
        clearTableLayout(mTableLayout);

        if (data == null) {
            mSpinnerType.setSelection(0);
            mEtBodyRow.setText(null);
            mTvFileName.setVisibility(View.GONE);
            mTvFileName.setText(null);
            mBtnSelectFile.setVisibility(View.VISIBLE);
            return;
        }

        mSpinnerType.setSelection(data.getType());
        if (data.getType() == 1 || data.getType() == 2) {
            String value = data.getType() == 1 ? data.getFormData() : data.getX_www_form_urlencoded();
            setTableLayout(mTableLayout, value, R.layout.item_header, ARRAY_TABLE_ROW_RES_ID, this, this, this);
        }

        mEtBodyRow.setText(data.getRaw());

        boolean isBinaryNotEmpty = data.getBinary() != null && data.getBinary().length() > 0;
        mBtnSelectFile.setVisibility(isBinaryNotEmpty ? View.GONE : View.VISIBLE);
        mTvFileName.setVisibility(isBinaryNotEmpty ? View.VISIBLE : View.GONE);
        mTvFileName.setText(isBinaryNotEmpty ? data.getBinary() : null);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_del) {
            ViewGroup parent = (ViewGroup) v.getParent();
            ViewGroup grandParent = (ViewGroup) parent.getParent();

            grandParent.removeView(parent);
            grandParent.postInvalidate();

            if (grandParent.getChildCount() < 3) {
                grandParent.addView(createTableRow(R.layout.item_header, ARRAY_TABLE_ROW_RES_ID, false, null, null, this, this, this));
            }
            callBack(mData);
        } else if (v.getId() == R.id.btn_select_file) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, REQUEST_CODE_SELECT_FILE);
        } else if (v.getId() == R.id.tvFileName) {
            v.setVisibility(View.GONE);
            ((TextView) v).setText(null);
            mBtnSelectFile.setVisibility(View.VISIBLE);
            mData.setBinary(null);
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

    public BodyBeen getPartData() {

        mData.setType((byte) mSpinnerType.getSelectedItemPosition());


        switch (mData.getType()) {
            case 1:
                mData.setFormData(getTableLayoutData(mTableLayout));
                break;
            case 2:
                mData.setX_www_form_urlencoded(getTableLayoutData(mTableLayout));
                break;
            case 3:
                mData.setRaw(mEtBodyRow.getText().toString());
                break;
            case 4:
                if (!TextUtils.isEmpty(mTvFileName.getText().toString()))
                    mData.setBinary(mTvFileName.getText().toString());
                break;
        }

        return mData;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int index = mViewFlipper.getDisplayedChild();
        int oldIndex = mData.getType();
        switch (oldIndex) {
            case 1:
                mData.setFormData(getTableLayoutData(mTableLayout));
                break;
            case 2:
                mData.setX_www_form_urlencoded(getTableLayoutData(mTableLayout));
                break;
//            case 3:
//                mData.setRaw(mEtBodyRow.getText().toString());
//                break;
        }
        switch (position) {
            case 0:
                if (index > 0) {
                    setViewFlipperTranslateLeft2Right(true);
                    mViewFlipper.setDisplayedChild(0);
                }
                break;
            case 1:
            case 2:
                if (index != 1) {
                    setViewFlipperTranslateLeft2Right(index > 1);
                    mViewFlipper.setDisplayedChild(1);
                }
                setTableLayout(mTableLayout, position == 1 ? mData.getFormData() : mData.getX_www_form_urlencoded(), R.layout.item_header, ARRAY_TABLE_ROW_RES_ID, this, this, this);
                break;
            case 3:
                if (2 != index) {
                    setViewFlipperTranslateLeft2Right(index > 2);
                    mViewFlipper.setDisplayedChild(2);
                }
                mEtBodyRow.requestFocus();
            default:
                if (position - 1 != index) {
                    setViewFlipperTranslateLeft2Right(index > position - 1);
                    mViewFlipper.setDisplayedChild(position - 1);
                }
                break;
        }
        mData.setType((byte) position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void setViewFlipperTranslateLeft2Right(boolean isL2R) {
        if (isL2R) {
            mViewFlipper.setInAnimation(mActivity, android.R.anim.slide_in_left);
            mViewFlipper.setOutAnimation(mActivity, android.R.anim.slide_out_right);
        } else {
            mViewFlipper.setInAnimation(mActivity, R.anim.slide_in_right);
            mViewFlipper.setOutAnimation(mActivity, R.anim.slide_out_left);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_SELECT_FILE && resultCode == Activity.RESULT_OK) {
            mBtnSelectFile.setVisibility(View.GONE);
            mTvFileName.setVisibility(View.VISIBLE);
            mUri = data.getData();
            String filePath = new UriUtils().getPath(mActivity, mUri);
            mTvFileName.setText(filePath);
            mData.setBinary(filePath);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

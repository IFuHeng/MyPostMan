package com.changhong.chpostman.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.changhong.chpostman.R;
import com.changhong.chpostman.model.BodyBeen;
import com.changhong.chpostman.model.ParamBeen;
import com.changhong.chpostman.ui.adapter.KeyValueAdapter;
import com.changhong.chpostman.utils.CommUtil;
import com.changhong.chpostman.utils.TextTools;
import com.changhong.chpostman.utils.UriUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;


public class BodyFragment2 extends ListContentFragment<BodyBeen> implements View.OnClickListener, AdapterView.OnItemSelectedListener, Observer<List<ParamBeen>> {
    private static final int REQUEST_CODE_SELECT_FILE = 0x123;
    private BodyBeen mData;
    private Spinner mSpinnerType, mSpinnerRawType;
    private ViewFlipper mViewFlipper;
    private EditText mEtBodyRow;
    private View mBtnSelectFile;
    private TextView mTvFileName;
    private Uri mUri;
    private ListView mListView1, mListView2;
    private List<ParamBeen> mArrParams1 = new ArrayList<>();
    private List<ParamBeen> mArrParams2 = new ArrayList<>();
    private KeyValueAdapter mAdapter1;
    private KeyValueAdapter mAdapter2;
    private TextView mBtnBeauty;

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
        return inflater.inflate(R.layout.part_body2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSpinnerType = view.findViewById(R.id.spinner_body_type);
        mSpinnerRawType = view.findViewById(R.id.spinner_raw_type);
        mListView1 = view.findViewById(R.id.listView_form_data);
        mListView2 = view.findViewById(R.id.listView_x_www_form_urlencoded);
        mViewFlipper = view.findViewById(R.id.viewFlipper_body);
        mEtBodyRow = view.findViewById(R.id.et_body_raw);
        mBtnSelectFile = view.findViewById(R.id.btn_select_file);
        mTvFileName = view.findViewById(R.id.tvFileName);

        mAdapter1 = new KeyValueAdapter(mActivity, mArrParams1, this);
        mListView1.setAdapter(mAdapter1);
        mAdapter2 = new KeyValueAdapter(mActivity, mArrParams2, this);
        mListView2.setAdapter(mAdapter2);
        mBtnBeauty = view.findViewById(R.id.btn_beautify);

        if (mData != null) {
            analysisData(mData);
        }

        mSpinnerType.setOnItemSelectedListener(this);
        mSpinnerRawType.setOnItemSelectedListener(this);
        mBtnSelectFile.setOnClickListener(this);
        mTvFileName.setOnClickListener(this);

        mBtnBeauty.getPaint().setUnderlineText(true);
        mBtnBeauty.setOnClickListener(this);
    }

    private void analysisData(BodyBeen data) {

        if (data == null) {
            mSpinnerType.setSelection(0);
            mSpinnerRawType.setSelection(0);
            mEtBodyRow.setText(null);
            mTvFileName.setVisibility(View.GONE);
            mTvFileName.setText(null);
            mBtnSelectFile.setVisibility(View.VISIBLE);
            mBtnBeauty.setVisibility(View.INVISIBLE);

            mArrParams1.clear();
            CommUtil.expandListView(mListView1);
            mAdapter1.notifyDataSetChanged();

            mArrParams2.clear();
            CommUtil.expandListView(mListView2);
            mAdapter2.notifyDataSetChanged();

            return;
        }

        mSpinnerType.setSelection(data.getType());
        mBtnBeauty.setVisibility(data.getRawType() > 1 ? View.VISIBLE : View.INVISIBLE);
        String value = data.getFormData();
        mArrParams1.clear();
        mArrParams1.addAll(turnString2ParamList(value));
        CommUtil.expandListView(mListView1);
        mAdapter1.notifyDataSetChanged();

        value = data.getX_www_form_urlencoded();
        mArrParams2.clear();
        mArrParams2.addAll(turnString2ParamList(value));
        CommUtil.expandListView(mListView2);
        mAdapter2.notifyDataSetChanged();

        mSpinnerRawType.setSelection(data.getRawType());
        if (mEtBodyRow.getText().length() == 0 || mEtBodyRow.getText().toString().trim().length() == 0 || data.getRawType() != 2) {
            mEtBodyRow.setText(data.getRaw());
        } else
            mEtBodyRow.setText(TextTools.turnObjectToCharSequence(data.getRaw()));

        boolean isBinaryNotEmpty = data.getBinary() != null && data.getBinary().length() > 0;
        mBtnSelectFile.setVisibility(isBinaryNotEmpty ? View.GONE : View.VISIBLE);
        mTvFileName.setVisibility(isBinaryNotEmpty ? View.VISIBLE : View.GONE);
        mTvFileName.setText(isBinaryNotEmpty ? data.getBinary() : null);
    }

    public void notifyChanged(String data) {
        BodyBeen been = data == null ? null : new Gson().fromJson(data, BodyBeen.class);
        analysisData(been);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_select_file) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, REQUEST_CODE_SELECT_FILE);
        } else if (v.getId() == R.id.tvFileName) {
            v.setVisibility(View.GONE);
            ((TextView) v).setText(null);
            mBtnSelectFile.setVisibility(View.VISIBLE);
            mData.setBinary(null);
        } else if (v.getId() == R.id.btn_beautify) {
            v.startAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.button_click));
            if (mEtBodyRow.getText().length() == 0 || mEtBodyRow.getText().toString().trim().length() == 0) {
                return;
            } else {
                switch (mSpinnerRawType.getSelectedItemPosition()) {
                    case 2:
                        mEtBodyRow.setText(TextTools.turnObjectToCharSequence(mEtBodyRow.getText().toString()));
                        break;
                    case 3:
                        if (TextTools.guessTextType(mEtBodyRow.getText().toString()) == TextTools.TextContentType.HTML)
                            mEtBodyRow.setText(TextTools.turnHtmlToCharSequence(mEtBodyRow.getText().toString()));
                        break;
                    case 4:
                        mEtBodyRow.setText(TextTools.turnXmlToCharSequence(mEtBodyRow.getText().toString()));
                        break;
                }
            }
        }
    }

    public BodyBeen getPartData() {

        mData.setType((byte) mSpinnerType.getSelectedItemPosition());

        switch (mData.getType()) {
            case 1:
                mData.setFormData(getTableLayoutData(mArrParams1));
                break;
            case 2:
                mData.setX_www_form_urlencoded(turnArrParams2StringInAndEqualFormat(mArrParams2));
                break;
            case 3:
                mData.setRaw(mEtBodyRow.getText().toString());
                break;
            case 4:
                if (!TextUtils.isEmpty(mTvFileName.getText().toString()))
                    mData.setBinary(mTvFileName.getText().toString());
                break;
        }
        mData.setRawType((byte) mSpinnerRawType.getSelectedItemPosition());

        return mData;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.spinner_body_type) {
            int index = mViewFlipper.getDisplayedChild();
            setViewFlipperTranslateLeft2Right(index > position);
            mViewFlipper.setDisplayedChild(position);
        } else if (parent.getId() == R.id.spinner_raw_type) {
            mBtnBeauty.setVisibility(position > 1 ? View.VISIBLE : View.INVISIBLE);
        }
//        switch (position) {
//            case 0:
//                if (index > 0) {
//                    setViewFlipperTranslateLeft2Right(true);
//                    mViewFlipper.setDisplayedChild(0);
//                }
//                break;
//            case 1:
//            case 2:
//                if (index != 1) {
//                    setViewFlipperTranslateLeft2Right(index > 1);
//                    mViewFlipper.setDisplayedChild(1);
//                }
//                mArrParams.clear();
//                mArrParams.addAll(turnString2ParamList(position == 1 ? mData.getFormData() : mData.getX_www_form_urlencoded()));
//                CommUtil.expandListView(mListView);
//                mAdapter.notifyDataSetChanged();
//                break;
//            case 3:
//                if (2 != index) {
//                    setViewFlipperTranslateLeft2Right(index > 2);
//                    mViewFlipper.setDisplayedChild(2);
//                }
//                mEtBodyRow.requestFocus();
//            default:
//                if (position - 1 != index) {
//                    setViewFlipperTranslateLeft2Right(index > position - 1);
//                    mViewFlipper.setDisplayedChild(position - 1);
//                }
//                break;
//        }
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

    @Override
    public void onChanged(List<ParamBeen> paramBeens) {
        if (paramBeens == mArrParams1) {
            CommUtil.expandListView(mListView1);
            mAdapter1.notifyDataSetChanged();
        } else if (paramBeens == mArrParams2) {
            CommUtil.expandListView(mListView2);
            mAdapter2.notifyDataSetChanged();
        }
    }
}

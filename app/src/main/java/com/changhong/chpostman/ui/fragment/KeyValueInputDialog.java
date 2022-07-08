package com.changhong.chpostman.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.changhong.chpostman.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class KeyValueInputDialog extends BaseDialogFragment<Pair<String, String>> implements View.OnClickListener, TextWatcher {

    public static final String KEY_CANCEL_ABLE = "dialog cancel enable";
    public static final String KEY_HINT_K = "hint_k";
    public static final String KEY_HINT_V = "hint_v";
    private EditText mEtKey, mEtValue;
    private View mBtnCancel;
    private View mBtnConfirm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null
                && getArguments().containsKey(KEY_CANCEL_ABLE))
            setCancelable(getArguments().getBoolean(KEY_CANCEL_ABLE));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_input_kv, null, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mBtnCancel = view.findViewById(android.R.id.button1);
        mBtnConfirm = view.findViewById(android.R.id.button2);
        mEtKey = view.findViewById(android.R.id.text1);
        mEtValue = view.findViewById(android.R.id.text2);

        TextView textView = view.findViewById(android.R.id.title);
        String title = getArguments().getString(Intent.EXTRA_TEXT);
        if (TextUtils.isEmpty(title))
            textView.setVisibility(View.GONE);
        else
            textView.setText(title);

        mEtKey.addTextChangedListener(this);
        mEtValue.addTextChangedListener(this);

        String hintk = getArguments().getString(KEY_HINT_K);
        if (!TextUtils.isEmpty(hintk)) {
            mEtKey.setHint(hintk);
            mEtKey.setText(hintk);
        }

        String hintV = getArguments().getString(KEY_HINT_V);
        if (!TextUtils.isEmpty(hintV)) {
            mEtValue.setHint(hintV);
            mEtValue.setText(hintV);
        }


        mBtnCancel.setOnClickListener(this);
        mBtnConfirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (onFragmentLifeListener != null) {
            switch (view.getId()) {
                case android.R.id.button1:
                    onBackPressed();
                    break;
                case android.R.id.button2:
                    if (isKVEmpty())
                        onBackPressed();
                    else
                        callBack(new Pair<String, String>(mEtKey.getText().toString(), mEtValue.getText().toString()));
                    break;
            }

        }
        destroyMyself();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (mBtnConfirm.isEnabled() == isKVEmpty())
            mBtnConfirm.setEnabled(!mBtnConfirm.isEnabled());
    }

    private boolean isKVEmpty() {
        return TextUtils.isEmpty(mEtKey.getText()) && TextUtils.isEmpty(mEtValue.getText());
    }
}

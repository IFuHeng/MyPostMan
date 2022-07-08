package com.changhong.chpostman.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.changhong.chpostman.R;
import com.changhong.chpostman.ui.fragment.BaseDialogFragment;

import static com.changhong.chpostman.ui.fragment.KeyValueInputDialog.KEY_HINT_K;
import static com.changhong.chpostman.ui.fragment.KeyValueInputDialog.KEY_HINT_V;


public class KeyValueInputDialog extends Dialog implements View.OnClickListener, TextWatcher, TextView.OnEditorActionListener {

    private TextView mTextView;
    private EditText mEtKey, mEtValue;
    private View mBtnCancel;
    private View mBtnConfirm;
    private Observer<Pair<String, String>> mObserver;

    public KeyValueInputDialog(@NonNull Context context, Observer<Pair<String, String>> observer) {
        super(context, R.style.DialogStyle);
        mObserver = observer;
        init();
    }

    public KeyValueInputDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener, Observer<Pair<String, String>> observer) {
        super(context, cancelable, cancelListener);
        mObserver = observer;
        init();
    }

    private void init() {
        setContentView(R.layout.dialog_input_kv);
        mBtnCancel = findViewById(android.R.id.button1);
        mBtnConfirm = findViewById(android.R.id.button2);
        mEtKey = findViewById(android.R.id.text1);
        mEtValue = findViewById(android.R.id.text2);
        mEtValue.setOnEditorActionListener(this);
        mTextView = findViewById(android.R.id.title);


        mEtKey.addTextChangedListener(this);
        mEtValue.addTextChangedListener(this);

        mBtnCancel.setOnClickListener(this);
        mBtnConfirm.setOnClickListener(this);
    }

    public void setTitle(CharSequence title) {
        super.setTitle(title);
        if (TextUtils.isEmpty(title))
            mTextView.setVisibility(View.GONE);
        else
            mTextView.setText(title);
    }

    public void setTitle(int titleStringResId) {
        super.setTitle(titleStringResId);
        if (titleStringResId == 0)
            mTextView.setVisibility(View.GONE);
        else
            mTextView.setText(titleStringResId);
    }

    public void setMessage(CharSequence key, CharSequence value) {
        mEtKey.setHint(key);
        mEtKey.setText(key);

        mEtValue.setHint(value);
        mEtValue.setText(value);
    }

    @Override
    public void show() {
        if (!isShowing())
            mEtKey.requestFocus();
        super.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case android.R.id.button1:
                onBackPressed();
                break;
            case android.R.id.button2:
                if (isKVEmpty())
                    onBackPressed();
                else {
                    mObserver.onChanged(new Pair<>(mEtKey.getText().toString(), mEtValue.getText().toString()));
                    dismiss();
                }
                break;
        }
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

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            mObserver.onChanged(new Pair<>(mEtKey.getText().toString(), mEtValue.getText().toString()));
            dismiss();
            return true;
        }
        return false;
    }
}

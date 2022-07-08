package com.changhong.chpostman.ui.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.lifecycle.Observer;

import com.changhong.chpostman.R;
import com.changhong.chpostman.model.ParamBeen;
import com.changhong.chpostman.model.RequestParamsBeen;

import java.util.List;

public class CanDelRowAdapter extends BaseAdapter implements View.OnClickListener {

    private Context mContext;
    private List<RequestParamsBeen> mData;
    private Observer<RequestParamsBeen> mObserver;
    private AlertDialog mDialog;
    private final String[] ARR_METHOD;

    private final float[] temp_hsv = new float[]{1, 1, 1};

    public CanDelRowAdapter(Context context, String[] ARR_METHOD, List<RequestParamsBeen> data, Observer<RequestParamsBeen> observer) {
        this.mContext = context;
        this.mData = data;
        this.mObserver = observer;
        this.ARR_METHOD = ARR_METHOD;
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_tab, null, false);
        }

        RequestParamsBeen param = mData.get(position);

        TextView tvKey = convertView.findViewById(android.R.id.text1);
        tvKey.setText(turnRequestParamBeen2CharSequence(param));

        View btnDel = convertView.findViewById(android.R.id.button1);
        btnDel.setOnClickListener(this);
        btnDel.setTag(param);

        return convertView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case android.R.id.button1:
                startEditDialog((RequestParamsBeen) v.getTag());
//                mData.remove(v.getTag());
//                mObserver.onChanged(mData);
                break;
        }
    }

    private CharSequence turnRequestParamBeen2CharSequence(RequestParamsBeen requestParamsBeen) {
        String method = ARR_METHOD[requestParamsBeen.getRequestType()];
        SpannableString ss = new SpannableString(requestParamsBeen.getName() + "  " + method + "  " + '\n' + "    " + requestParamsBeen.getUrl());
        ss.setSpan(new BackgroundColorSpan(0x88666666), requestParamsBeen.getName().length() + 1,
                requestParamsBeen.getName().length() + 4 + method.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        ss.setSpan(new RelativeSizeSpan(0.7f), requestParamsBeen.getName().length() + 1,
                requestParamsBeen.getName().length() + 4 + method.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        temp_hsv[0] = Math.round(Math.random() * 180);
        ss.setSpan(new ForegroundColorSpan(Color.HSVToColor(temp_hsv)), requestParamsBeen.getName().length() + 1,
                requestParamsBeen.getName().length() + 4 + method.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        temp_hsv[0] = Math.round(Math.random() * 180 + 180);
        ss.setSpan(new ForegroundColorSpan(Color.HSVToColor(temp_hsv)),
                ss.length() - requestParamsBeen.getUrl().length(), ss.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        return ss;
    }

    private void startEditDialog(final RequestParamsBeen been) {
        temp_hsv[0] = Math.round(Math.random() * 180 + 180);

        String name = been.getName();
        String message = String.format(mContext.getString(R.string.ask_confirm_del), name);
        int start = message.indexOf(name);
        SpannableString ss = new SpannableString(message);
        ss.setSpan(new UnderlineSpan(), start, start + name.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        ss.setSpan(new ForegroundColorSpan(Color.HSVToColor(temp_hsv)), start, start + name.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        if (mDialog == null) {
            mDialog = new AlertDialog.Builder(mContext)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(R.string.warning)
                    .setMessage(ss)
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            mObserver.onChanged(been);
                        }
                    }).setNegativeButton(R.string.cancel, null).create();
        }
        mDialog.setMessage(ss);
        mDialog.show();
    }

}

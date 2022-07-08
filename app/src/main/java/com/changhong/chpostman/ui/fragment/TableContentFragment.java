package com.changhong.chpostman.ui.fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.Size;

import com.changhong.chpostman.R;

public class TableContentFragment<T> extends BaseFragment<T> {

    protected static final int[] ARRAY_TABLE_ROW_RES_ID = {R.id.cb_choose, R.id.et_key, R.id.et_value, R.id.btn_del};

    protected void clearTableLayout(TableLayout tableLayout) {
        if (tableLayout.getChildCount() > 1) {
            tableLayout.removeViewAt(1);
        }
    }

    protected String getTableLayoutData(TableLayout tableLayout) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < tableLayout.getChildCount(); i++) {
            ViewGroup tabRow = (ViewGroup) tableLayout.getChildAt(i);
            CheckBox cb = (CheckBox) tabRow.getChildAt(0);
            TextView tvKey = (TextView) tabRow.getChildAt(1);
            TextView tvValue = (TextView) tabRow.getChildAt(2);

            if (!cb.isChecked() && TextUtils.isEmpty(tvKey.getText()) && TextUtils.isEmpty(tvValue.getText()))//如果未选择并且为填入value和key，不写入
                continue;

            if (sb.length() > 0)
                sb.append('\n');
            if (!cb.isChecked())
                sb.append("//");
            sb.append(tvKey.getText()).append(':').append(tvValue.getText());
        }
        return sb.toString();
    }

    protected void setTableLayout(TableLayout tableLayout, String data, int resViewId, @Size(value = 4) int[] resIds,
                                  TextView.OnEditorActionListener onEditorActionListener,
                                  View.OnClickListener onClickListener,
                                  CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
        clearTableLayout(tableLayout);
        if (data == null || data.length() == 0) {
            tableLayout.addView(createTableRow(resViewId, resIds,
                    false, null, null,
                    onEditorActionListener, onClickListener, onCheckedChangeListener));
            return;
        }

        String[] tempArr = data.split("\n");
        for (String s : tempArr) {
            boolean isChecked = !s.startsWith("//");
            String value = null;
            String key = null;
            if (!isChecked)
                s = s.substring(2);
            int indexOfColon = s.indexOf(':');
            if (indexOfColon == -1)
                key = s;
            else {
                if (indexOfColon > 0)
                    key = s.substring(0, indexOfColon);
                if (indexOfColon < s.length() - 1)
                    value = s.substring(indexOfColon + 1);
            }
            tableLayout.addView(createTableRow(resViewId, resIds,
                    isChecked, key, value,
                    onEditorActionListener, onClickListener, onCheckedChangeListener));
        }
    }

    /**
     * @param resViewId               table row view resid
     * @param resIds                  id{checkBox,textView of key , textView of value, button of delete}
     * @param isChecked
     * @param key
     * @param value
     * @param onEditorActionListener
     * @param onClickListener
     * @param onCheckedChangeListener
     * @return
     */
    protected View createTableRow(final int resViewId, @Size(value = 4) final int[] resIds,
                                  boolean isChecked, String key, String value,
                                  TextView.OnEditorActionListener onEditorActionListener,
                                  View.OnClickListener onClickListener,
                                  CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
        View view = LayoutInflater.from(mActivity).inflate(resViewId, null, false);
        CheckBox cb_choose = view.findViewById(resIds[0]);
        EditText et_key = view.findViewById(resIds[1]);
        EditText et_value = view.findViewById(resIds[2]);
        View btn_del = view.findViewById(resIds[3]);

        cb_choose.setChecked(isChecked);
        et_key.setText(key);
        et_value.setText(value);

        et_value.setOnEditorActionListener(onEditorActionListener);
        cb_choose.setOnCheckedChangeListener(onCheckedChangeListener);
        btn_del.setOnClickListener(onClickListener);

        return view;
    }

    protected void deleteTableRow(TableLayout tableLayout, View btnDel) {
        ViewGroup parent = (ViewGroup) btnDel.getParent();
        ViewGroup grandParent = (ViewGroup) parent.getParent();
        grandParent.removeView(parent);
    }

}

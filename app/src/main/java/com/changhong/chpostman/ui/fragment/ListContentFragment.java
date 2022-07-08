package com.changhong.chpostman.ui.fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.Size;

import com.changhong.chpostman.R;
import com.changhong.chpostman.model.ParamBeen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListContentFragment<T> extends BaseFragment<T> {
    protected String getTableLayoutData(List<ParamBeen> data) {
        return turnArrParams2String(data);
    }

    public static final List<ParamBeen> turnString2ParamList(String data) {
        ArrayList<ParamBeen> result = new ArrayList<>();
        if (data == null || data.length() == 0) {
            return result;
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
            result.add(new ParamBeen(isChecked, key, value));
        }
        return result;
    }

    public static String turnArrParams2String(List<ParamBeen> data) {
        if (data == null || data.isEmpty())
            return null;
        StringBuilder sb = new StringBuilder();
        for (ParamBeen item : data) {
            if (sb.length() > 0)
                sb.append('\n');
            if (!item.isChecked())
                sb.append("//");

            if (!TextUtils.isEmpty(item.getKey()))
                sb.append(item.getKey());
            sb.append(':');
            if (!TextUtils.isEmpty(item.getValue()))
                sb.append(item.getValue());
        }
        return sb.toString();
    }

    public static String turnArrParams2StringInAndEqualFormat(List<ParamBeen> data) {
        StringBuilder sb = new StringBuilder();

        for (ParamBeen item : data) {
            if (!item.isChecked())
                continue;

            if (sb.length() > 0) {
                sb.append('&');
            }
            sb.append(item.getKey() == null ? "" : item.getKey());
            sb.append('=');
            sb.append(item.getValue() == null ? "" : item.getValue());
        }
        return sb.toString();
    }

    public static List<ParamBeen> turnString2ArrParamsInAndEqualFormat(String params) {
        ArrayList<ParamBeen> result = new ArrayList<>();
        int indexOfColon = 0;
        String[] arrParams = params.split("&");
        for (String param : arrParams) {
            ParamBeen been = new ParamBeen();
            been.setChecked(true);
            if (param == null || param.length() == 0) {
                result.add(been);
                continue;
            }
            indexOfColon = param.indexOf('=');
            if (indexOfColon == -1)
                been.setKey(param);
            else if (indexOfColon == param.length() - 1)
                been.setKey(param.substring(0, indexOfColon));
            else if (indexOfColon == 0)
                been.setValue(param.substring(1));
            else {
                been.setKey(param.substring(0, indexOfColon));
                been.setValue(param.substring(indexOfColon + 1));
            }
            result.add(been);
        }

        if (params.endsWith("&"))
            result.add(new ParamBeen(true, null, null));
        return result;
    }

}

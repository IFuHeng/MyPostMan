package com.changhong.chpostman.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class BaseFragment<T> extends Fragment {
    private Toast mToast;
    protected FragmentActivity mActivity;
    private SimpleDateFormat mSimDateFormat;
    protected OnFragmentLifeListener<T> onFragmentLifeListener;
    private ProgressDialog mProgressDialog;
    private WifiManager mWifiManager;

    private AlertDialog alertDilaog;

    private HashMap<String, AsyncTask> mHashTask;

    private PopupWindow mPopupWindow;
    private AlertDialog mChooseAlertDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        mHashTask = new HashMap<>();
        if (getArguments() != null) {
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(getClass().getSimpleName(), "====~ onResume");
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        Log.d(getClass().getSimpleName(), "====~ onHiddenChanged(" + hidden + ")");
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onPause() {
        Log.d(getClass().getSimpleName(), "====~ onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        closeIME(mActivity);
        Log.d(getClass().getSimpleName(), "====~ onStop");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        closeIME(mActivity);
        Log.d(getClass().getSimpleName(), "====~ onDestroy");
        if (alertDilaog != null)
            alertDilaog.hide();
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
        stopAllTask();
        super.onDestroy();
    }

    protected void showToast(CharSequence charSequence) {
        if (mToast == null)
            mToast = Toast.makeText(mActivity, charSequence, Toast.LENGTH_SHORT);
        else
            mToast.setText(charSequence);
        mToast.show();
    }

    protected void showToast(int resid) {
        if (mToast == null)
            mToast = Toast.makeText(mActivity, resid, Toast.LENGTH_SHORT);
        else
            mToast.setText(resid);
        mToast.show();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mPopupWindow != null && mPopupWindow.isShowing()) {
                mPopupWindow.dismiss();
                return true;
            }
        }
        return false;
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return false;
    }

    protected String getCurrentTime() {
        if (mSimDateFormat == null)
            mSimDateFormat = new SimpleDateFormat("HH:mm:ss");

        return mSimDateFormat.format(new Date()) + ':' + (System.currentTimeMillis() % 1000);
    }

    public void setOnFragmentLifeListener(OnFragmentLifeListener<T> onFragmentLifeListener) {
        this.onFragmentLifeListener = onFragmentLifeListener;
    }

    protected void showProgressDialog(CharSequence cs, boolean cancelable, DialogInterface.OnCancelListener listener) {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(mActivity, null, cs, true, cancelable, listener);
        } else
            mProgressDialog.setMessage(cs);
        mProgressDialog.setCancelable(cancelable);
        mProgressDialog.setOnCancelListener(listener);
        mProgressDialog.setCanceledOnTouchOutside(cancelable);

        if (!mProgressDialog.isShowing())
            mProgressDialog.show();
    }

    protected void showDialogChoose(CharSequence[] choices, DialogInterface.OnClickListener listener) {
        mChooseAlertDialog = new AlertDialog.Builder(mActivity).setItems(choices, listener).create();
        mChooseAlertDialog.show();
    }

    /**
     * @param et      密码显示的输入框
     * @param isShown 是否显示
     */
    protected void isShowPassword(EditText et, boolean isShown) {
        if (isShown)
            et.setTransformationMethod(PasswordTransformationMethod.getInstance());
        else
            et.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

        if (et.getText().length() > 0) {
            et.setSelection(et.getText().length());
        }
    }

    protected WifiManager getWifiManager() {
        if (mWifiManager == null)
            mWifiManager = (WifiManager) mActivity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        return mWifiManager;
    }

    /**
     * 切换输入框的隐藏与显示
     *
     * @param et
     * @param isVisible
     */
    protected void togglePasswordState(EditText et, boolean isVisible) {
        if (isVisible) {
            et.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            et.setSingleLine();
            et.setMaxLines(1);
        } else
            et.setTransformationMethod(PasswordTransformationMethod.getInstance());

        if (et.getText().length() > 0) {
            et.setSelection(et.getText().length());
        }
    }

    protected void showAlert(CharSequence charSequence, CharSequence txBtn1, DialogInterface.OnClickListener listener1, CharSequence txBtn2, DialogInterface.OnClickListener listener2, boolean cancelEnable) {
        showAlert(charSequence, txBtn1, listener1, txBtn2, listener2, cancelEnable, null);
    }

    protected void showAlert(CharSequence charSequence,
                             CharSequence txBtn1, DialogInterface.OnClickListener listener1,
                             CharSequence txBtn2, DialogInterface.OnClickListener listener2,
                             boolean cancelEnable, DialogInterface.OnCancelListener cancelListener) {
        if (alertDilaog != null) {
            alertDilaog.setMessage(charSequence);
            alertDilaog.setButton(AlertDialog.BUTTON_POSITIVE, txBtn1, listener1);
            alertDilaog.setButton(AlertDialog.BUTTON_NEGATIVE, txBtn2, listener2);
            alertDilaog.getButton(AlertDialog.BUTTON_NEGATIVE).setVisibility(View.VISIBLE);
        } else
            alertDilaog = new AlertDialog.Builder(mActivity).setMessage(charSequence)
                    .setPositiveButton(txBtn1, listener1).
                            setNegativeButton(txBtn2, listener2)
                    .setCancelable(cancelEnable)
                    .setCancelable(cancelListener != null)
                    .setOnCancelListener(cancelListener).create();
        alertDilaog.setCanceledOnTouchOutside(cancelEnable);
        alertDilaog.show();
        alertDilaog.setCancelable(cancelEnable);
        alertDilaog.setOnCancelListener(cancelListener);
        if (!alertDilaog.isShowing())
            alertDilaog.show();
    }

    protected void showAlert(CharSequence charSequence, CharSequence txBtn,
                             DialogInterface.OnClickListener listener, boolean cancelEnable,
                             DialogInterface.OnCancelListener cancelListener) {
        if (alertDilaog != null) {
            alertDilaog.setMessage(charSequence);
            alertDilaog.setButton(AlertDialog.BUTTON_POSITIVE, txBtn, listener);
            alertDilaog.getButton(AlertDialog.BUTTON_NEGATIVE).setVisibility(View.GONE);
        } else
            alertDilaog = new AlertDialog.Builder(mActivity).setMessage(charSequence).setPositiveButton(txBtn, listener)
                    .setCancelable(cancelEnable)
                    .setOnCancelListener(cancelListener).create();
        alertDilaog.setCanceledOnTouchOutside(cancelEnable);
        alertDilaog.show();
        alertDilaog.setCancelable(cancelEnable);
        alertDilaog.setOnCancelListener(cancelListener);
        if (!alertDilaog.isShowing())
            alertDilaog.show();
    }

    protected void showAlert(CharSequence charSequence, CharSequence txBtn, DialogInterface.OnClickListener listener) {
        showAlert(charSequence, txBtn, listener, false, null);
    }

    public void hideAlert() {
        if (alertDilaog != null)
            alertDilaog.dismiss();
        if (mChooseAlertDialog != null)
            mChooseAlertDialog.dismiss();
    }

    protected void addTask(@NonNull AsyncTask task) {
        if (mHashTask.containsKey(task.getClass())) {
            AsyncTask obj = mHashTask.remove(task.getClass().getName());
            obj.cancel(true);
            mHashTask.put(task.getClass().getName(), task);
        }
    }

    //java.lang.Exception: Unable to resolve host "test.chwliot.com": No address associated with hostname
    protected void stopAllTask() {
        if (!mHashTask.isEmpty()) {
            Iterator<Map.Entry<String, AsyncTask>> iterator = mHashTask.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, AsyncTask> item = iterator.next();
                mHashTask.remove(item.getKey()).cancel(true);
            }
        }
    }

    /**
     * @param clazz 线程终止
     */
    protected void stopTask(Class<? extends AsyncTask> clazz) {
        if (!mHashTask.isEmpty()) {
            Iterator<Map.Entry<String, AsyncTask>> iterator = mHashTask.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, AsyncTask> item = iterator.next();
                if (item.getKey().equals(clazz.getName()))
                    mHashTask.remove(item.getKey()).cancel(true);
            }
        }
    }

    protected static final DialogInterface.OnClickListener DEFAULT_DIALOG_ONCLICK_LISTENER = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    };


    protected String _getString(int resId) {
        try {
            return getString(resId);
        } catch (IllegalStateException e) {
            e.printStackTrace();
            Log.d(getClass().getSimpleName(), "====~context of fragment is Invalid in getString()");
            return mActivity.getString(resId);
        }
    }

    protected Resources _getResources() {
        try {
            return getResources();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            Log.d(getClass().getSimpleName(), "====~context of fragment is Invalid in getResources()");
            return mActivity.getResources();
        }
    }

    protected void marth_parent(View view) {
        int height = mActivity.getWindow().getDecorView().getHeight();
        if (view.getHeight() < height)
            view.setMinimumHeight(height);
    }

    /**
     * @param networkInfo
     * @return 子节点执行后，true让主节点中断后续操作
     */
    public boolean onNetworkChange(NetworkInfo networkInfo) {
        return false;
    }

    protected void onBackPressed() {
        callBack(null);
    }

    protected void callBack(T t) {
        if (onFragmentLifeListener != null)
            onFragmentLifeListener.onChanged(t);
    }

    /**
     * 关闭输入法
     *
     * @param context
     */
    protected void closeIME(Activity context) {
        if (context != null && context.getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

}

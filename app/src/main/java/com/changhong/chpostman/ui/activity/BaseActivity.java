package com.changhong.chpostman.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.changhong.chpostman.ui.fragment.BaseFragment;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2020/7/24.
 */
public class BaseActivity extends AppCompatActivity {
    private AlertDialog alertDilaog;
    private Toast mToast;
    protected ProgressDialog mProgressDialog;
    //    protected BaseFragment mCurFragment;
    protected HashMap<Integer, BaseFragment> mHashFragment = new HashMap<>();
    private HashMap<String, AsyncTask> mHashTask;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHashTask = new HashMap<>();
        transparencyBar(this);
        Log.d(getClass().getSimpleName(), "====~onCreate :Language = " + getResources().getConfiguration().locale.getLanguage());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(getClass().getSimpleName(), "====~ onResume");
    }

    @Override
    protected void onPause() {
        Log.d(getClass().getSimpleName(), "====~ onPause");
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        stopAllTask();
        super.onDestroy();
    }

    protected void showProgressDialog(CharSequence cs, boolean cancelable, DialogInterface.OnCancelListener listener) {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(this, null, cs, true, cancelable, listener);
        } else
            mProgressDialog.setMessage(cs);

        mProgressDialog.setCancelable(cancelable);
        mProgressDialog.setCanceledOnTouchOutside(cancelable);
        mProgressDialog.setOnCancelListener(listener);

        if (!mProgressDialog.isShowing())
            mProgressDialog.show();
    }

    protected void hideProgressDialog() {
        if (mProgressDialog != null)
            mProgressDialog.dismiss();
    }

    protected void showToast(CharSequence cs) {
        if (mToast == null)
            mToast = Toast.makeText(this, cs, Toast.LENGTH_SHORT);
        else
            mToast.setText(cs);
        mToast.show();
    }

    protected void showToast(int resId) {
        if (mToast == null)
            mToast = Toast.makeText(this, resId, Toast.LENGTH_SHORT);
        else
            mToast.setText(resId);
        mToast.show();
    }


    protected void showAlert(CharSequence charSequence, CharSequence txBtn, DialogInterface.OnClickListener listener, boolean cancelEnable) {
        if (alertDilaog != null) {
            alertDilaog.setMessage(charSequence);
            alertDilaog.setButton(AlertDialog.BUTTON_POSITIVE, txBtn, listener);
        } else
            alertDilaog = new AlertDialog.Builder(this).setMessage(charSequence).setPositiveButton(txBtn, listener).setCancelable(cancelEnable).create();
        alertDilaog.setCancelable(cancelEnable);
//        if (alertDilaog.isShowing())
//            alertDilaog.dismiss();
        if (!alertDilaog.isShowing())
            alertDilaog.show();
    }


    protected boolean isDialogShown() {

        if (mProgressDialog != null && mProgressDialog.isShowing())
            return true;

        if (alertDilaog != null && alertDilaog.isShowing())
            return true;

        return false;
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//
//        Iterator<Map.Entry<Integer, BaseFragment>> iterator = mHashFragment.entrySet().iterator();
//        while (iterator.hasNext()) {
//            Map.Entry<Integer, BaseFragment> next = iterator.next();
//            BaseFragment fragment = next.getValue();
//
//        }
//
//        if (mCurFragment != null) {
//            if (mCurFragment.onKeyDown(keyCode, event))
//                return true;
//            if (keyCode == KeyEvent.KEYCODE_BACK) {
//                backFragment();
//                return true;
//            }
//        }
//
//        return super.onKeyDown(keyCode, event);
//    }
//
//    @Override
//    public boolean onKeyUp(int keyCode, KeyEvent event) {
//        if (mCurFragment != null && mCurFragment.onKeyUp(keyCode, event))
//            return true;
//
//        return super.onKeyUp(keyCode, event);
//    }
//
//    protected void startFragment(BaseFragment fragment, int viewResId) {
//        if (fragment == null)
//            return;
//        if (mCurFragment != null && mCurFragment.getClass() == fragment.getClass())
//            return;
//        getSupportFragmentManager().beginTransaction().add(viewResId, fragment).addToBackStack(null).commit();
//        if (mCurFragment != null && !mCurFragment.isHidden()) {
//            getSupportFragmentManager().beginTransaction().hide(mCurFragment).commit();
//        }
//        mCurFragment = fragment;
//    }
//
//    protected void backFragment() {
//        if (mCurFragment != null) {
//            if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
//                List<Fragment> list = getSupportFragmentManager().getFragments();
//                mCurFragment = (BaseFragment) list.get(list.size() - 2);
//                if (mCurFragment != null && mCurFragment.isHidden())
//                    getSupportFragmentManager().beginTransaction().show(mCurFragment).commit();
//                getSupportFragmentManager().popBackStack();
//            } else
//                finish();
//        } else finish();
//    }

    protected void addTask(@NonNull AsyncTask task) {
        if (mHashTask.containsKey(task.getClass())) {
            AsyncTask obj = mHashTask.remove(task.getClass().getName());
            obj.cancel(true);
            mHashTask.put(task.getClass().getName(), task);
        }
    }

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
     * 修改状态栏为全透明
     *
     * @param activity
     */
    public static void transparencyBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }
}

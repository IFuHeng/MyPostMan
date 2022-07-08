package com.changhong.chpostman.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Observer;
import androidx.slidingpanelayout.widget.SlidingPaneLayout;

import com.changhong.chpostman.R;
import com.changhong.chpostman.database.MyDBHelper;
import com.changhong.chpostman.model.BodyBeen;
import com.changhong.chpostman.model.ParamBeen;
import com.changhong.chpostman.model.RequestParamsBeen;
import com.changhong.chpostman.model.ResponseBeen;
import com.changhong.chpostman.preference.KeyConfig;
import com.changhong.chpostman.preference.Preferences;
import com.changhong.chpostman.task.GenericTask;
import com.changhong.chpostman.task.RequestTask;
import com.changhong.chpostman.task.TaskListener;
import com.changhong.chpostman.task.TaskResult;
import com.changhong.chpostman.ui.adapter.CanDelRowAdapter;
import com.changhong.chpostman.ui.adapter.KeyValue2Adapter;
import com.changhong.chpostman.ui.fragment.BodyFragment2;
import com.changhong.chpostman.ui.fragment.HeadersFragment2;
import com.changhong.chpostman.ui.fragment.InputDialog;
import com.changhong.chpostman.ui.fragment.OnFragmentLifeListener;
import com.changhong.chpostman.ui.fragment.ParamsFragment2;
import com.changhong.chpostman.utils.CommUtil;
import com.changhong.chpostman.utils.ConnectUtils;
import com.changhong.chpostman.utils.TextTools;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener, AdapterView.OnItemClickListener, TextWatcher {

    private ViewFlipper mViewFlipper01, mViewFlipper02;
    private EditText mEtUrl;
    private TextView mTvTitle;
    private TextView mTvResponseStatus;
    private ListView mListViewResponseHeaders, mListViewResponseCookies;

    private ParamsFragment2 mParamFragment;
    private HeadersFragment2 mHeadersFragment;
    private BodyFragment2 mBodyFragment;

    private Spinner mSpinnerType;
    private RadioGroup radioGroupParams, radioGroupResponse;
    private TextView mTextResult;
    private ListView mListViewRecode;
    private BaseAdapter mAdapterRecode;
    /**
     * 数据库管理
     */
    private MyDBHelper myDBHelper;
    private ArrayList<RequestParamsBeen> mArrRequestParam = new ArrayList<>();

    private String[] ARR_METHOD;
    private final float[] temp_hsv = new float[]{1, 1, 1};
    private SlidingPaneLayout mSlidingPaneLayout;

    private WebView mWebPreview;
    private View mRbPreview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ARR_METHOD = getResources().getStringArray(R.array.request_type);

        setContentView(R.layout.activity_postman);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NestedScrollView nestedScrollView01 = findViewById(R.id.nestedScrollView01);
        nestedScrollView01.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                Log.d(getClass().getSimpleName(), "====~" + String.format("scrollX= %d, scrollY = %d, oldScrollX = %d, oldScrollY = %d", scrollX, scrollY, oldScrollX, oldScrollY));
                if (scrollY != 0)
                    ViewCompat.setTranslationZ(mSpinnerType, scrollY);
            }
        });

        mTvTitle = findViewById(R.id.tvTitle);
        mEtUrl = findViewById(R.id.et_url);
        mEtUrl.addTextChangedListener(this);
        mEtUrl.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    saveLocalRequestParamsBeen();
                    doSend();
                    return true;
                }
                return false;
            }
        });

        mSpinnerType = findViewById(R.id.spinner_httpType);
        mSpinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0 || position == 5 || position == 6 || position == 8 || position == 9 || position == 10 || position == 12 || position == 14) {
                    radioGroupParams.getChildAt(2).setEnabled(false);
                    if (radioGroupParams.getCheckedRadioButtonId() == radioGroupParams.getChildAt(2).getId()) {
                        radioGroupParams.check(radioGroupParams.getChildAt(0).getId());
                    }
                } else
                    radioGroupParams.getChildAt(2).setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        View btnEditName = findViewById(R.id.btn_editName);
        btnEditName.setOnClickListener(this);

        View btnSend = findViewById(R.id.btn_send);
        btnSend.setOnClickListener(this);

        radioGroupParams = findViewById(R.id.radiGroupParam);
        radioGroupParams.check(radioGroupParams.getChildAt(0).getId());
        radioGroupParams.setOnCheckedChangeListener(this);
        mViewFlipper01 = findViewById(R.id.viewFlipper01);
        initParamPart(null);
        initHeaderPart(null);
        initBodyPart(null);

        radioGroupResponse = findViewById(R.id.radiGroupResponse);
        radioGroupResponse.check(radioGroupResponse.getChildAt(0).getId());
        radioGroupResponse.setOnCheckedChangeListener(this);
        mViewFlipper02 = findViewById(R.id.viewFlipper02);

        mTextResult = findViewById(R.id.container4);
        mTvResponseStatus = findViewById(R.id.tv_response_status);

        //侧边栏
        mSlidingPaneLayout = findViewById(R.id.slidingPaneLayout);
        mListViewRecode = findViewById(R.id.listViewRecode);
//        mAdapterRecode = new ArrayAdapter(this, android.R.layout.simple_list_item_1, mArrRecords);
        mAdapterRecode = new CanDelRowAdapter(this, ARR_METHOD, mArrRequestParam, new Observer<RequestParamsBeen>() {
            @Override
            public void onChanged(RequestParamsBeen been) {
                myDBHelper.deleteRow(been);
                mArrRequestParam.remove(been);
                mAdapterRecode.notifyDataSetChanged();
            }
        });
        mListViewRecode.setAdapter(mAdapterRecode);
        mListViewRecode.setOnItemClickListener(this);
        mListViewRecode.setEmptyView(findViewById(R.id.tvNoContent));

        mListViewResponseCookies = findViewById(R.id.container5);
        mListViewResponseHeaders = findViewById(R.id.container6);

        findViewById(R.id.btn_info).setOnClickListener(this);

        mWebPreview = findViewById(R.id.webView01);
        mWebPreview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mWebPreview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        mWebPreview.getSettings().setDisplayZoomControls(false);
        mWebPreview.getSettings().setSupportZoom(true);
        mWebPreview.getSettings().setBuiltInZoomControls(true);
        mWebPreview.getSettings().setUseWideViewPort(true);
        mWebPreview.getSettings().setLoadWithOverviewMode(true);
        mWebPreview.getSettings().setJavaScriptEnabled(true);
        mWebPreview.getSettings().setAllowFileAccess(true); //设置可以访问文件
        mWebPreview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        mWebPreview.getSettings().setLoadsImagesAutomatically(true); //支持自动加载图片
        mWebPreview.getSettings().setDefaultTextEncodingName("utf-8");//设置编码格式
        mWebPreview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                //设定加载结束的操作
                mEtUrl.setText(url);
            }
        });

        mRbPreview = findViewById(R.id.rb_preview);

        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setCurrentEditData(loadLocalRequestParamsBeen());
        mWebPreview.onResume();
        mWebPreview.resumeTimers();
    }

    @Override
    protected void onPause() {
        saveLocalRequestParamsBeen();
        mWebPreview.onPause();
        mWebPreview.pauseTimers();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        saveLocalRequestParamsBeen();
        mWebPreview.destroy();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (mSlidingPaneLayout.isOpen()) {
            mSlidingPaneLayout.close();
            return;
        }

        if (radioGroupResponse.indexOfChild(radioGroupResponse.findViewById(radioGroupResponse.getCheckedRadioButtonId())) == 3) {
            if (mWebPreview.canGoBack()) {
                mWebPreview.goBack();
                return;
            }
        }

        finish();
//        super.onBackPressed();
    }

    private void initBodyPart(BodyBeen data) {

        mBodyFragment = new BodyFragment2();
        if (data != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(Intent.EXTRA_DATA_REMOVED, data);
            mBodyFragment.setArguments(bundle);
        }
        mBodyFragment.setOnFragmentLifeListener(new OnFragmentLifeListener<BodyBeen>() {
            @Override
            public void onChanged(@Nullable BodyBeen been) {
                //TODO
            }
        });
        getSupportFragmentManager().beginTransaction().add(R.id.container3, mBodyFragment).addToBackStack(null).commit();
    }

    private void initHeaderPart(ArrayList<com.changhong.chpostman.model.ParamBeen> data) {

        mHeadersFragment = new HeadersFragment2();
        if (data != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(Intent.EXTRA_DATA_REMOVED, data);
            mHeadersFragment.setArguments(bundle);
        }
        mHeadersFragment.setOnFragmentLifeListener(new OnFragmentLifeListener<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //TODO
            }
        });
        getSupportFragmentManager().beginTransaction().add(R.id.container2, mHeadersFragment).addToBackStack(null).commit();
    }

    private void initParamPart(ArrayList<com.changhong.chpostman.model.ParamBeen> data) {
        mParamFragment = new ParamsFragment2();
        if (data != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(Intent.EXTRA_DATA_REMOVED, data);
            mParamFragment.setArguments(bundle);
        }
        mParamFragment.setOnFragmentLifeListener(new OnFragmentLifeListener<String>() {
            @Override
            public void onChanged(String string) {
                Log.d(getClass().getSimpleName(), "====~on PARAM PART callback: " + string);
                mEtUrl.removeTextChangedListener(HomeActivity.this);

                String url = mEtUrl.getText().toString();
                int indexOfColon = url.indexOf('?');
                boolean isEmptyOfParams = string == null || string.length() == 0;
                Editable editable = mEtUrl.getText();
                if (isEmptyOfParams) {
                    if (indexOfColon != -1)
                        editable.delete(indexOfColon, editable.length());
                } else if (indexOfColon == -1) {
                    editable.append('?').append(string);
                } else {
                    editable.delete(indexOfColon + 1, editable.length()).append(string);
                }
                mEtUrl.addTextChangedListener(HomeActivity.this);
            }
        });
        getSupportFragmentManager().beginTransaction().add(R.id.container1, mParamFragment).addToBackStack(null).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_menu) {
            mSlidingPaneLayout.open();
            return true;
        }
        if (id == R.id.action_save) {
            saveRequestParamsBeen();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveRequestParamsBeen() {
        String name = mTvTitle.getText().toString();
        if (name == null || name.length() == 0) {
            doInputString(new OnFragmentLifeListener<String>() {
                @Override
                public void onChanged(String o) {
                    if (!TextUtils.isEmpty(o)) {
                        mTvTitle.setText(o);
                        saveRequestParamsBeen();
                    }
                }
            });
            return;
        }

        RequestParamsBeen row = myDBHelper.queryRowByName(name);
        if (row == null) {
            RequestParamsBeen rpb = new RequestParamsBeen();
            rpb.setName(name);
            rpb.setHeaders(mHeadersFragment.getData());
            rpb.setParams(mParamFragment.getData());
            rpb.setUrl(mEtUrl.getText().toString());
            rpb.setIndexOfPHB((byte) radioGroupParams.indexOfChild(radioGroupParams.findViewById(radioGroupParams.getCheckedRadioButtonId())));
            rpb.setRequestType((byte) mSpinnerType.getSelectedItemPosition());
            rpb.setBodyBeen(mBodyFragment.getPartData());
            myDBHelper.recordRow(rpb);
        } else {
            row.setName(name);
            row.setHeaders(mHeadersFragment.getData());
            row.setParams(mParamFragment.getData());
            row.setUrl(mEtUrl.getText().toString());
            row.setIndexOfPHB((byte) radioGroupParams.indexOfChild(radioGroupParams.findViewById(radioGroupParams.getCheckedRadioButtonId())));
            row.setRequestType((byte) mSpinnerType.getSelectedItemPosition());
            row.setBodyBeen(mBodyFragment.getPartData());
            myDBHelper.recordRow(row);
        }
        showToast("Save completed.");
        initData();
    }

    private void saveLocalRequestParamsBeen() {
        String name = mTvTitle.getText().toString();
        RequestParamsBeen rpb = new RequestParamsBeen();
        rpb.setName(name);
        rpb.setHeaders(mHeadersFragment.getData());
        rpb.setParams(mParamFragment.getData());
        rpb.setUrl(mEtUrl.getText().toString());
        rpb.setIndexOfPHB((byte) radioGroupParams.indexOfChild(radioGroupParams.findViewById(radioGroupParams.getCheckedRadioButtonId())));
        rpb.setRequestType((byte) mSpinnerType.getSelectedItemPosition());
        rpb.setBodyBeen(mBodyFragment.getPartData());
        Preferences.getInstance(this).saveString(KeyConfig.KEY_LOCAL, new Gson().toJson(rpb));
    }

    private RequestParamsBeen loadLocalRequestParamsBeen() {
        String local = Preferences.getInstance(this).getString(KeyConfig.KEY_LOCAL);
        if (local == null || local.length() == 0)
            return null;
        return new Gson().fromJson(local, RequestParamsBeen.class);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        int index = checkedId == -1 ? -1 : group.indexOfChild(group.findViewById(checkedId));
        if (index == -1)
            return;
        if (group.getId() == R.id.radiGroupParam) {
            if (mViewFlipper01.getDisplayedChild() != index) {
                setTranslateL2R(mViewFlipper01, mViewFlipper01.getDisplayedChild() > index);
                mViewFlipper01.setDisplayedChild(index);
            }
        } else if (group.getId() == R.id.radiGroupResponse) {
            if (mViewFlipper02.getDisplayedChild() != index) {
                setTranslateL2R(mViewFlipper02, mViewFlipper02.getDisplayedChild() > index);
                mViewFlipper02.setDisplayedChild(index);
            }
        }
    }

    private void setTranslateL2R(ViewFlipper viewFlipper, boolean isLeft2Right) {
        if (isLeft2Right) {
            viewFlipper.setInAnimation(this, android.R.anim.slide_in_left);
            viewFlipper.setOutAnimation(this, android.R.anim.slide_out_right);
        } else {
            viewFlipper.setInAnimation(this, R.anim.slide_in_right);
            viewFlipper.setOutAnimation(this, R.anim.slide_out_left);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_editName) {
            doInputString(new OnFragmentLifeListener<String>() {
                @Override
                public void onChanged(String o) {
                    if (!TextUtils.isEmpty(o)) {
                        mTvTitle.setText(o);
                    }
                }
            });
        } else if (v.getId() == R.id.btn_send) {
            saveLocalRequestParamsBeen();
            doSend();
        } else if (v.getId() == R.id.btn_info) {
            doShowWlanInfo();
        }
    }

    private void doShowWlanInfo() {
        if (ConnectUtils.isWifiNetwork(this)) {
            showAlert(ConnectUtils.getWifiInfo(this.getApplicationContext()), getString(R.string.confirm), null, true);
        }
    }

    private void doInputString(OnFragmentLifeListener<String> fragmentListener) {
        InputDialog dialog = new InputDialog();
        Bundle bundle = new Bundle();
        bundle.putBoolean(InputDialog.EXTRA_PASSWORD, false);
        bundle.putBoolean(InputDialog.KEY_CANCEL_ABLE, true);
        bundle.putString(Intent.EXTRA_TEXT, getString(R.string.notice_input_new_name));
        dialog.setArguments(bundle);
        dialog.setFragmentListener(fragmentListener);
        dialog.show(getSupportFragmentManager(), "password_input");
    }

    private void doSend() {
        String url = mEtUrl.getText().toString();
        int index = url.indexOf('?');
        if (index != -1)
            try {
                index += 1;
                url = url.substring(0, index) + URLEncoder.encode(url.substring(url.indexOf('?') + 1), Charset.defaultCharset().name());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        Log.d(getClass().getSimpleName(), "====~url = " + url);
        if (!URLUtil.isNetworkUrl(url)) {
            url = URLUtil.guessUrl(url);
            if (URLUtil.isNetworkUrl(url)) {
                mEtUrl.setText(url);
            } else {
                showToast("The URL is not valid and cannot be loaded.");
                return;
            }
        }
        RequestParamsBeen rpb = new RequestParamsBeen();
        rpb.setName(mTvTitle.getText().toString());
        rpb.setHeaders(mHeadersFragment.getData());
        rpb.setParams(mParamFragment.getData());
        rpb.setUrl(url);
        rpb.setIndexOfPHB((byte) radioGroupParams.indexOfChild(radioGroupParams.findViewById(radioGroupParams.getCheckedRadioButtonId())));
        rpb.setRequestType((byte) mSpinnerType.getSelectedItemPosition());
        rpb.setBodyBeen(mBodyFragment.getPartData());
        addTask(
                new RequestTask().execute(rpb, ARR_METHOD, new TaskListener<ResponseBeen>() {
                    @Override
                    public String getName() {
                        return null;
                    }

                    @Override
                    public void onPreExecute(GenericTask task) {
                        if (mRbPreview.getVisibility() == View.VISIBLE) {
                            mRbPreview.setVisibility(View.GONE);
                            radioGroupResponse.check(radioGroupParams.getChildAt(0).getId());
                            if (mWebPreview.canGoBack()) {
                                mWebPreview.goBack();
                                mWebPreview.clearHistory();
                                mWebPreview.clearSslPreferences();
                                mWebPreview.clearCache(true);
                                mWebPreview.clearFormData();
                                mWebPreview.clearMatches();
                            }
                        }
                        showProgressDialog("Sending request……", true, null);
                    }

                    @Override
                    public void onPostExecute(GenericTask task, TaskResult result) {
                        hideProgressDialog();
                        if (result != TaskResult.OK)
                            showToast(task.getException().getMessage());
                    }

                    @Override
                    public void onProgressUpdate(GenericTask task, ResponseBeen param) {
                        setResponse(param);
                    }

                    @Override
                    public void onCancelled(GenericTask task) {
                        hideProgressDialog();
                    }
                })
        );
    }

    private void setResponse(ResponseBeen param) {
        if (param == null) {
            mTextResult.setText(null);
            mTvResponseStatus.setText(null);
            setListViewResponseCookies(new ArrayList<ParamBeen>());
            setListViewResponseHeaders(new ArrayList<ParamBeen>());
            return;
        }
        String responseContent = param.getBodyString();
        TextTools.TextContentType guessContentType = TextTools.guessTextType(responseContent);
        if (!param.isSuccessful()) {
            mTextResult.setText(param.getMsg());
        } else if (guessContentType == TextTools.TextContentType.JSON) {
            mTextResult.setText(TextTools.turnObjectToCharSequence(param.getBody()));
        } else if (guessContentType == TextTools.TextContentType.XML) {
            mTextResult.setText(TextTools.turnXmlToCharSequence(responseContent));
        } else if (guessContentType == TextTools.TextContentType.HTML) {
            String charset = TextTools.getCharsetFormHtml(responseContent);
            if (!charset.equals(Charset.defaultCharset().name()) && Charset.isSupported(charset))
                try {
                    responseContent = new String(param.getBody(), charset);
                    mRbPreview.setVisibility(View.VISIBLE);
//                    mWebPreview.loadData(responseContent, "text/html", charset);
                    mWebPreview.loadDataWithBaseURL(mEtUrl.getText().toString(), responseContent, "text/html", charset, null);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
//            mTextResult.setText(responseContent);
            mTextResult.setText(TextTools.turnHtmlToCharSequence(responseContent));
        } else
            mTextResult.setText(responseContent);

        SpannableStringBuilder ssb = new SpannableStringBuilder();
        {
            Drawable drawable = getResources().getDrawable(R.drawable.ic_internet_blue_80dp);
            int size = Math.round(mTvResponseStatus.getTextSize());
            drawable.setBounds(0, 0, size, size);
            ssb.append(TextTools.string2CharSequence("<img/>", new ImageSpan(drawable)));
        }
        ssb.append("  ").append("Status").append(':').append(' ');
        ssb.append(TextTools.string2CharSequence(String.valueOf(param.getCode()) + ' ' + param.getMsg()
                , new ForegroundColorSpan(param.isSuccessful() ? 0xFF2BBC32 : 0xFFFF6B47)));
        ssb.append("  ").append("Time").append(':').append(' ');
        ssb.append(TextTools.string2CharSequence(param.getCostTime() + " ms", new ForegroundColorSpan(0xFF2BBC32)));
        ssb.append("  ").append("Size").append(':').append(' ');
        ssb.append(TextTools.string2CharSequence(String.valueOf(param.getLength() + param.getHeadersLength()), new ForegroundColorSpan(0xFF2BBC32)));

        mTvResponseStatus.setText(ssb);

        setListViewResponseCookies(param.getCookies());
        setListViewResponseHeaders(param.getHeaders());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        mBodyFragment.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setListViewResponseCookies(List<ParamBeen> cookies) {
        mListViewResponseCookies.setAdapter(new KeyValue2Adapter(this, cookies));
        CommUtil.expandListView(mListViewResponseCookies);
        ((TextView) radioGroupResponse.getChildAt(1)).setText(R.string.cookies);
        if (cookies != null && !cookies.isEmpty()) {
            SpannableString ss = new SpannableString(String.format("(%d)", cookies.size()));
            ss.setSpan(new RelativeSizeSpan(0.8f), 1, ss.length() - 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            temp_hsv[0] = Math.round(Math.random() * (360 - 180) + 180);
            ss.setSpan(new ForegroundColorSpan(Color.HSVToColor(temp_hsv)), 1, ss.length() - 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            ((TextView) radioGroupResponse.getChildAt(1)).append(ss);
        }
    }

    private void setListViewResponseHeaders(List<ParamBeen> cookies) {
        mListViewResponseHeaders.setAdapter(new KeyValue2Adapter(this, cookies));
        CommUtil.expandListView(mListViewResponseHeaders);
        ((TextView) radioGroupResponse.getChildAt(2)).setText(R.string.headers);
        if (cookies != null && !cookies.isEmpty()) {
            SpannableString ss = new SpannableString(String.format("(%d)", cookies.size()));
            ss.setSpan(new RelativeSizeSpan(0.8f), 1, ss.length() - 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            temp_hsv[0] = Math.round(Math.random() * (360 - 180) + 180);
            ss.setSpan(new ForegroundColorSpan(Color.HSVToColor(temp_hsv)), 1, ss.length() - 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            ((TextView) radioGroupResponse.getChildAt(2)).append(ss);
        }
    }

    private void initData() {
        myDBHelper = new MyDBHelper(this);
        mArrRequestParam.clear();
        mArrRequestParam.addAll(myDBHelper.queryAll());
        mAdapterRecode.notifyDataSetChanged();
    }

    private void setCurrentEditData(RequestParamsBeen been) {
        mEtUrl.removeTextChangedListener(this);
        if (been == null) {
            mEtUrl.setText(null);
            mSpinnerType.setSelection(0);
            mTvTitle.setText(null);
            mBodyFragment.notifyChanged(null);
            mParamFragment.notifyChanged(null);
            mHeadersFragment.notifyChanged(null);
            radioGroupParams.check(radioGroupParams.getChildAt(0).getId());
        } else {
            mEtUrl.setText(been.getUrl());
            mSpinnerType.setSelection(been.getRequestType());
            mTvTitle.setText(been.getName());
            mBodyFragment.notifyChanged(been.getBodyBeenString());
            mParamFragment.onChanged(been.getParams());
            mHeadersFragment.onChanged(been.getHeaders());
            radioGroupParams.check(radioGroupParams.getChildAt(been.getIndexOfPHB()).getId());
        }
        mEtUrl.addTextChangedListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        setCurrentEditData(mArrRequestParam.get(position));
        setResponse(null);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        mParamFragment.notifyChanged(s.toString());
    }
}
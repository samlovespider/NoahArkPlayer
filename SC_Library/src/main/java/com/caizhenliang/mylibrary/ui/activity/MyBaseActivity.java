package com.caizhenliang.mylibrary.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.caizhenliang.mylibrary.Imp.MyBaseActivityImp;
import com.caizhenliang.mylibrary.Imp.MyBaseBusImp;
import com.caizhenliang.mylibrary.Imp.MyBaseHttpImp;
import com.caizhenliang.mylibrary.Imp.MyClickImp;
import com.caizhenliang.mylibrary.Imp.MyLogImp;
import com.caizhenliang.mylibrary.ui.view.MyAlertDialogTool;
import com.caizhenliang.mylibrary.util.SCBus;
import com.caizhenliang.mylibrary.util.SCLogHelper;
import com.caizhenliang.mylibrary.util.base.ACache;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.greenrobot.eventbus.Subscribe;

/**
 * @author caizhenliang
 */
abstract public class MyBaseActivity extends AppCompatActivity implements MyBaseActivityImp, MyClickImp, MyLogImp, MyBaseHttpImp, MyBaseBusImp {

    //
    protected String TAG = getClass().getSimpleName();
    //
    protected ActionBar mActionBar;
    protected MyAlertDialogTool mAlertDialogTool;//use to create alertdialog
    protected AsyncHttpClient mHttpClient;
    protected ACache mCache;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // init AlertDialog
        mAlertDialogTool = new MyAlertDialogTool(getBaseContext());
        // init ActionBar
        mActionBar = getSupportActionBar();
        // init Eventbus
        SCBus.getInstance().register(this);
        // init AsyncHttpClient
        mHttpClient = new AsyncHttpClient();
        // init Cache
        mCache = ACache.get(getBaseContext());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SCBus.getInstance().unregister(this);
    }

    //-- MyBaseActivityImp **--**--**--**--**--**--**--**--**--**--**--**--**--**--**--**

    @Subscribe
    public void onEvent(Object o) {
    }

    @Override
    public void gotoActivity(Class<?> paramClass) {
        doGotoActivity(paramClass, null);
    }

    @Override
    public void gotoActivity(Class<?> paramClass, Bundle bundle) {
        doGotoActivity(paramClass, bundle);
    }

    @Override
    public void gotoActivityNoHistory(Class<?> paramClass, Bundle bundle) {
        doGotoActivity(paramClass, bundle, Intent.FLAG_ACTIVITY_NO_HISTORY);
    }

    @Override
    public void gotoActivityForResult(Class<?> paramClass, Bundle bundle, int paramInt) {
        Intent lIntent = new Intent(this.getBaseContext(), paramClass);
        if (bundle != null) {
            lIntent.putExtras(bundle);
            lIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        }
        startActivityForResult(lIntent, paramInt);
    }

    /**
     * 跳转Activity，带有Bundle参数，如果该Activity已启动过，则不启动新的；
     *
     * @param paramClass Activity参数
     * @param bundle     Bundle参数
     */
    private void doGotoActivity(Class<?> paramClass, Bundle bundle) {
        Intent lIntent = new Intent(this.getBaseContext(), paramClass);
        if (bundle != null) {
            lIntent.putExtras(bundle);
            lIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        }
        startActivity(lIntent);
    }

    /**
     * 跳转Activity，带有Bundle参数，可以设置启动模式；
     *
     * @param paramClass Activity参数
     * @param bundle     Bundle参数
     * @param intentFlag 　启动模式
     * @see Intent
     */
    private void doGotoActivity(Class<?> paramClass, Bundle bundle, int intentFlag) {
        Intent lIntent = new Intent(this.getBaseContext(), paramClass);
        if (bundle != null) {
            lIntent.putExtras(bundle);
            lIntent.addFlags(intentFlag);
        }
        startActivity(lIntent);
    }

    //-- MyBaseHttpImp **--**--**--**--**--**--**--**--**--**--**--**--**--**--**--**

    @Override
    public void get(String url, RequestParams requestParams, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        mHttpClient.get(this, url, requestParams, asyncHttpResponseHandler);
    }

    @Override
    public void post(String url, RequestParams requestParams, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        mHttpClient.post(this, url, requestParams, asyncHttpResponseHandler);
    }

    //-- MyBaseBusImp **--**--**--**--**--**--**--**--**--**--**--**--**--**--**--**

    @Override
    public void post(Object o) {
        SCBus.getInstance().post(o);
    }

    //-- MyLogImp **--**--**--**--**--**--**--**--**--**--**--**--**--**--**--**

    @Override
    public void logW(Object o) {
        SCLogHelper.w(TAG, o);
    }

    @Override
    public void logW(String title, Object o) {
        SCLogHelper.w(TAG, title, o);
    }
}

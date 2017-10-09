package com.caizhenliang.mylibrary.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.caizhenliang.mylibrary.Imp.MyBaseBusImp;
import com.caizhenliang.mylibrary.Imp.MyBaseFragmentImp;
import com.caizhenliang.mylibrary.Imp.MyBaseHttpImp;
import com.caizhenliang.mylibrary.Imp.MyClickImp;
import com.caizhenliang.mylibrary.Imp.MyLogImp;
import com.caizhenliang.mylibrary.ui.view.MyAlertDialogTool;
import com.caizhenliang.mylibrary.util.ACache;
import com.caizhenliang.mylibrary.util.SCBus;
import com.caizhenliang.mylibrary.util.SCLogHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.greenrobot.eventbus.Subscribe;

/**
 * Created by caizhenliang on 2017/10/9.
 * samcai
 */
abstract public class MyBaseFragment extends Fragment implements MyBaseFragmentImp, MyClickImp, MyLogImp, MyBaseHttpImp, MyBaseBusImp {

    //
    protected String TAG = getClass().getSimpleName();
    //
    protected MyAlertDialogTool mAlertDialogTool;//use to create alertdialog
    protected ACache mACache;
    protected AsyncHttpClient mHttpClient;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // init AlertDialog
        mAlertDialogTool = new MyAlertDialogTool(getActivity().getBaseContext());
        // init Eventbus
        SCBus.getInstance().register(this);
        // init ACache
        mACache = ACache.get(getActivity().getBaseContext());
        // init AsyncHttpClient
        mHttpClient = new AsyncHttpClient();
    }

    @Override
    public void onDestroy() {
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
        Intent lIntent = new Intent(getActivity().getBaseContext(), paramClass);
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
        Intent lIntent = new Intent(getActivity().getBaseContext(), paramClass);
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
        Intent lIntent = new Intent(getActivity().getBaseContext(), paramClass);
        if (bundle != null) {
            lIntent.putExtras(bundle);
            lIntent.addFlags(intentFlag);
        }
        startActivity(lIntent);
    }

    //-- MyBaseHttpImp **--**--**--**--**--**--**--**--**--**--**--**--**--**--**--**

    @Override
    public void get(String url, RequestParams requestParams, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        mHttpClient.get(getActivity(), url, requestParams, asyncHttpResponseHandler);
    }

    @Override
    public void post(String url, RequestParams requestParams, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        mHttpClient.post(getActivity(), url, requestParams, asyncHttpResponseHandler);
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

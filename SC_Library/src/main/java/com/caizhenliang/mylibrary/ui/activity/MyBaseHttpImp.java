package com.caizhenliang.mylibrary.ui.activity;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by caizhenliang on 2017/9/11.
 */

public interface MyBaseHttpImp {

    void get(String url, RequestParams requestParams, AsyncHttpResponseHandler asyncHttpResponseHandler);

    void post(String url, RequestParams requestParams, AsyncHttpResponseHandler asyncHttpResponseHandler);


}

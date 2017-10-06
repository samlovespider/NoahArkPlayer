package com.noahark.noaharkplayer.base.ui;

import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;

import com.caizhenliang.mylibrary.ui.activity.MyBaseActivity;
import com.noahark.noaharkplayer.BuildConfig;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

/**
 * Created by caizhenliang on 2017/8/18.
 */
@EActivity
public abstract class BaseActivity extends MyBaseActivity {

    @AfterViews
    @Override
    abstract public void initData();

    @AfterViews
    @Override
    abstract public void initView();

    @Override
    public void logW(Object o) {
        if (BuildConfig.DEBUG) {
            super.logW(o);
        }
    }

    @Override
    public void logW(String title, Object o) {
        if (BuildConfig.DEBUG) {
            super.logW(title, o);
        }
    }

    @Override
    public void initMenuItem(MenuItem menuItem) {

    }

    @Override
    public void initClick(View view) {

    }

    @Override
    public void initItemClick(int position) {

    }

    @Override
    public void initItemClick(Object object) {

    }

    @Override
    public void initItemLongClick(int position) {

    }

    @Override
    public void initItemLongClick(Object object) {

    }

    @Override
    public void initCheckedChange(CompoundButton button, boolean isChecked) {

    }
}

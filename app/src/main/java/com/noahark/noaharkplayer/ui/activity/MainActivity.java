package com.noahark.noaharkplayer.ui.activity;

import android.view.View;

import com.noahark.noaharkplayer.R;
import com.noahark.noaharkplayer.base.ui.BaseActivity;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

@EActivity(R.layout.activity_main)
public class MainActivity extends BaseActivity {

    @Override
    public void initData() {

    }

    @Override
    public void initView() {

    }

    @Click(R.id.btn)
    @Override
    public void initClick(View view) {
        gotoActivity(PlayingActivity_.class);
    }

}

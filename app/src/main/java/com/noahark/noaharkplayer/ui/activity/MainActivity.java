package com.noahark.noaharkplayer.ui.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Window;

import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.noahark.noaharkplayer.R;
import com.noahark.noaharkplayer.base.ui.BaseActivity;
import com.noahark.noaharkplayer.ui.fragment.PlayingFragment;
import com.noahark.noaharkplayer.ui.fragment.PlayingFragment_;
import com.noahark.noaharkplayer.ui.fragment.PlayingListFragment;
import com.noahark.noaharkplayer.ui.fragment.PlayingListFragment_;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.WindowFeature;

import java.util.ArrayList;

/**
 * Peter Chiu, 08569541
 * Yuanda Huo, 16417122
 * Zhenliang Cai, 17108093
 */
@WindowFeature(Window.FEATURE_NO_TITLE)
@EActivity(R.layout.activity_main)
public class MainActivity extends BaseActivity {

    public static final int CODE_FOR_WRITE_PERMISSION = 1;
    //
    @ViewById(R.id.stlLay)
    SegmentTabLayout stlLay;
    @ViewById(R.id.vpPlaying)
    ViewPager vpPlaying;
    //
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private PlayingListFragment mPlayingListFragment;

    @Override
    public void initView() {

        //
        PlayingFragment playingFragment = new PlayingFragment_();
        mPlayingListFragment = new PlayingListFragment_();
        mFragments.add(playingFragment);
        mFragments.add(mPlayingListFragment);

        MyPagerAdapter mMyPagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), mFragments);
        vpPlaying.setAdapter(mMyPagerAdapter);

        stlLay.setTabData(new String[]{"Playing", "List"});
        stlLay.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                vpPlaying.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {
            }
        });

        vpPlaying.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                stlLay.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        vpPlaying.setCurrentItem(0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CODE_FOR_WRITE_PERMISSION) {
            if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //
                mPlayingListFragment.initView();
            } else {
                finish();
            }
        }
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        private ArrayList<Fragment> mFragments;

        private MyPagerAdapter(FragmentManager fm, ArrayList<Fragment> sFragments) {
            super(fm);
            mFragments = sFragments;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }
}

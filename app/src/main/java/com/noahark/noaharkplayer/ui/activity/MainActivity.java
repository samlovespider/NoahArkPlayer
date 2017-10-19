package com.noahark.noaharkplayer.ui.activity;

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


@WindowFeature(Window.FEATURE_NO_TITLE)
@EActivity(R.layout.activity_main)
public class MainActivity extends BaseActivity {

    //
    @ViewById(R.id.stlLay)
    SegmentTabLayout stlLay;
    @ViewById(R.id.vpPlaying)
    ViewPager vpPlaying;
    //
    private ArrayList<Fragment> mFragments = new ArrayList<>();

    @Override
    public void initView() {

        //
        PlayingFragment playingFragment = new PlayingFragment_();
//        PlayingFragment playingFragment = PlayingFragment_.builder().mMusicModel(model).build();
        PlayingListFragment playingListFragment = new PlayingListFragment_();
        mFragments.add(playingFragment);
        mFragments.add(playingListFragment);

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

    private class MyPagerAdapter extends FragmentPagerAdapter {
        private ArrayList<Fragment> mFragments;

        public MyPagerAdapter(FragmentManager fm, ArrayList<Fragment> sFragments) {
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

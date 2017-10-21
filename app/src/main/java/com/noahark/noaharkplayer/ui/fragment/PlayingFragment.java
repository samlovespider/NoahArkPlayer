package com.noahark.noaharkplayer.ui.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.noahark.noaharkplayer.R;
import com.noahark.noaharkplayer.base.ui.BaseFragment;
import com.noahark.noaharkplayer.model.MusicModel;
import com.noahark.noaharkplayer.service.MusicService;
import com.shinelw.library.ColorArcProgressBar;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

@EFragment(R.layout.fragment_playing)
public class PlayingFragment extends BaseFragment {

    //
    @ViewById(R.id.pbPlayProgress)
    ColorArcProgressBar pbPlayProgress;
    @ViewById(R.id.tv_music_name)
    TextView tvMusicName;
    @ViewById(R.id.tv_Artist)
    TextView tvArtist;
    @ViewById(R.id.ibPlay)
    ImageButton ibPlay;
    @ViewById(R.id.ib_repeat)
    ImageButton ib_repeat;
    @ViewById(R.id.ib_shuffle)
    ImageButton ib_shuffle;
    @ViewById(R.id.ib_favorite)
    ImageButton ib_favorite;
    @ViewById(R.id.tvCurTime)
    TextView tvCurTime;
    @ViewById(R.id.tvTotalTime)
    TextView tvTotalTime;
    //
    private PlayingReceiver mPlayingReceiver;  //自定义的广播接收器
    private MusicModel mMusicModel;
    //
    private int mRepeatState = MusicService.REPEAT_NORMAL;
    private int mOrderState = MusicService.ORDER_BY_ORDER;
    private int mLastPosition = -1;

    /**
     * initialise Playing Fragment view
     */
    @Override
    public void initView() {
        // get cache model\position\repeat\order
        getCache();
        // set display info
        setMusicInfo(mMusicModel);
        //
        setReceiver();
    }

    /**
     * get cache as music service object
     * set model, position, order and repeat state
     */
    private void getCache() {
        if (mCache.getAsObject(MusicService.CACHE_MODEL) != null) {
            mMusicModel = (MusicModel) mCache.getAsObject(MusicService.CACHE_MODEL);
        }
        if (mCache.getAsObject(MusicService.CACHE_POSITION) != null) {
            mLastPosition = (int) mCache.getAsObject(MusicService.CACHE_POSITION);
        }
        if (mCache.getAsObject(MusicService.CACHE_ORDER) != null) {
            mOrderState = (int) mCache.getAsObject(MusicService.CACHE_ORDER);
        }
        if (mCache.getAsObject(MusicService.CACHE_REPEAT) != null) {
            mRepeatState = (int) mCache.getAsObject(MusicService.CACHE_REPEAT);
        }
    }

    /**
     * get and set music info to display
     * set song name and song artist
     * set playing, repeat, shuffle, my favourite song state
     * set song progress to beginning of song and display total duration of song
     * @param musicModel a song
     */
    private void setMusicInfo(MusicModel musicModel) {

        if (musicModel == null) {
            return;
        }

        tvMusicName.setText(musicModel.mName);
        tvArtist.setText(musicModel.mArtist);

        if (musicModel.isPlaying) {
            ibPlay.setBackgroundResource(R.drawable.pic_music_play);
        } else {
            ibPlay.setBackgroundResource(R.drawable.pic_music_play_pause);
        }

        switch (mRepeatState) {
            case MusicService.REPEAT_NORMAL:
                ib_repeat.setBackgroundResource(R.drawable.ic_music_repeat_close);
                break;
            case MusicService.REPEAT_LOOP_ALL:
                ib_repeat.setBackgroundResource(R.drawable.ic_music_repeat_all);
                break;
            case MusicService.REPEAT_SINGLE:
                ib_repeat.setBackgroundResource(R.drawable.ic_music_repeat_one);
                break;
        }

        if (mOrderState == MusicService.ORDER_BY_RANDOM) {
            ib_shuffle.setBackgroundResource(R.drawable.ic_music_shuffle_pressed);
        } else {
            ib_shuffle.setBackgroundResource(R.drawable.ic_music_shuffle_normal);
        }

        if (musicModel.isFavorite) {
            ib_favorite.setBackgroundResource(R.drawable.ic_music_favorite_pressed);
        } else {
            ib_favorite.setBackgroundResource(R.drawable.ic_music_favorite_normal);
        }

        pbPlayProgress.setMaxValues(Integer.parseInt(musicModel.mDuration));
        pbPlayProgress.setCurrentValues(0);

        tvCurTime.setText("00:00");
        tvTotalTime.setText(formatTime(Integer.parseInt(musicModel.mDuration)));
    }

    /**
     * set receiver to get info from service
     * set intent filters for receiver
     */
    private void setReceiver() {
        mPlayingReceiver = new PlayingReceiver();
        // 创建IntentFilter
        IntentFilter filter = new IntentFilter();
        // 指定BroadcastReceiver监听的Action
        filter.addAction(MusicService.BROADCAST_ACTION_NOW_PLAYING);
        filter.addAction(MusicService.BROADCAST_ACTION_CURRENT_TIME);
        // 注册BroadcastReceiver
        mContext.registerReceiver(mPlayingReceiver, filter);
    }

    /**
     * create appropriate Playing Fragment view based on what user has clicked on
     * @param view
     */
    @Click({R.id.ibPlay, R.id.ib_favorite, R.id.ib_repeat, R.id.ib_shuffle})
    @Override
    public void initClick(View view) {

        if (mMusicModel == null) {
            return;
        }

        switch (view.getId()) {
            case R.id.ibPlay:
                if (mMusicModel.isPlaying) {
                    mMusicModel.isPlaying = false;
                    ibPlay.setBackgroundResource(R.drawable.pic_music_play_pause);
                    pause();
                } else {
                    mMusicModel.isPlaying = true;
                    ibPlay.setBackgroundResource(R.drawable.pic_music_play);
                    play(mLastPosition);
                }
                break;
            case R.id.ib_repeat:
                switch (mRepeatState) {
                    case MusicService.REPEAT_NORMAL:
                        mRepeatState = MusicService.REPEAT_LOOP_ALL;
                        view.setBackgroundResource(R.drawable.ic_music_repeat_all);
                        break;
                    case MusicService.REPEAT_LOOP_ALL:
                        mRepeatState = MusicService.REPEAT_SINGLE;
                        view.setBackgroundResource(R.drawable.ic_music_repeat_one);
                        break;
                    case MusicService.REPEAT_SINGLE:
                        mRepeatState = MusicService.REPEAT_NORMAL;
                        view.setBackgroundResource(R.drawable.ic_music_repeat_close);
                        break;
                }
                sendBroadcastToService();
                break;
            case R.id.ib_favorite:
                if (mMusicModel.isFavorite) {
                    mMusicModel.isFavorite = false;
                    view.setBackgroundResource(R.drawable.ic_music_favorite_normal);
                } else {
                    mMusicModel.isFavorite = true;
                    view.setBackgroundResource(R.drawable.ic_music_favorite_pressed);
                }
                sendBroadcastToService();
                break;
            case R.id.ib_shuffle:
                if (mOrderState == MusicService.ORDER_BY_RANDOM) {
                    mOrderState = MusicService.ORDER_BY_ORDER;
                    view.setBackgroundResource(R.drawable.ic_music_shuffle_normal);
                } else {
                    mOrderState = MusicService.ORDER_BY_RANDOM;
                    view.setBackgroundResource(R.drawable.ic_music_shuffle_pressed);
                }
                sendBroadcastToService();
                break;
        }
    }

    /**
     * intent - pause song
     */
    private void pause() {
        startIntentToService(mLastPosition, MusicService.PLY_PAUSE);
    }

    /**
     * intent - play song
     * @param position
     */
    private void play(int position) {
        startIntentToService(position, MusicService.PLY_PLAY);
    }

    /**
     * create new intent with song's different states and package as bundle
     * send bundle as broadcast to service
     */
    private void sendBroadcastToService() {
        Intent intent = new Intent(MusicService.BROADCAST_ACTION_CHANGE_STATUS);
        Bundle bundle = new Bundle();
        bundle.putInt(MusicService.REPEAT, mRepeatState);
        bundle.putInt(MusicService.ORDER, mOrderState);
        bundle.putBoolean(MusicService.MUSIC_FAVORITE, mMusicModel.isFavorite);
        intent.putExtras(bundle);
        mContext.sendBroadcast(intent);
    }

    /**
     * create new intent with song info and state
     * start service with intent
     * @param position
     * @param action
     */
    private void startIntentToService(int position, int action) {
        Intent intent = new Intent(mContext, MusicService.class);
        intent.setAction(MusicService.SERVICE_ACTION);
        intent.putExtra(MusicService.MUSIC_POSITION, position);
        intent.putExtra(MusicService.MUSIC_ACTION, action);
        mContext.startService(intent);
    }

    /**
     * Adjust to proper time format
     * @param time
     * @return
     */
    private String formatTime(long time) {
        String min = time / (1000 * 60) + "";
        String sec = time % (1000 * 60) + "";
        if (min.length() < 2) {
            min = "0" + time / (1000 * 60) + "";
        } else {
            min = time / (1000 * 60) + "";
        }
        if (sec.length() == 4) {
            sec = "0" + (time % (1000 * 60)) + "";
        } else if (sec.length() == 3) {
            sec = "00" + (time % (1000 * 60)) + "";
        } else if (sec.length() == 2) {
            sec = "000" + (time % (1000 * 60)) + "";
        } else if (sec.length() == 1) {
            sec = "0000" + (time % (1000 * 60)) + "";
        }
        return min + ":" + sec.trim().substring(0, 2);
    }

    /**
     * Destroy the receiver
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mContext.unregisterReceiver(mPlayingReceiver);
    }

    private class PlayingReceiver extends BroadcastReceiver {

        /**
         * On receiving intent from service
         * Pass info to set intent as either new song playing state or song on pause state
         * @param context
         * @param intent
         */
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (action.equals(MusicService.BROADCAST_ACTION_NOW_PLAYING)) {
                mMusicModel = (MusicModel) intent.getExtras().getSerializable(MusicService.MUSIC_MODEL);
                mLastPosition = intent.getExtras().getInt(MusicService.MUSIC_POSITION, mLastPosition);
                //
                pbPlayProgress.setMaxValues(Integer.parseInt(mMusicModel.mDuration));
                //
                setMusicInfo(mMusicModel);

            } else if (action.equals(MusicService.BROADCAST_ACTION_CURRENT_TIME)) {
                int curTime = intent.getIntExtra(MusicService.PLY_CURRENT_TIME, 0);
                pbPlayProgress.setCurrentValues(curTime);
                //
                tvCurTime.setText(formatTime(curTime));
            }
        }
    }
}

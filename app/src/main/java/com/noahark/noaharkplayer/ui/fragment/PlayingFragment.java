package com.noahark.noaharkplayer.ui.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.caizhenliang.mylibrary.util.base.ACache;
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
    //
    private PlayingReceiver mPlayingReceiver;  //自定义的广播接收器
    private MusicModel mMusicModel;
    //
    private boolean isFavorite = false;
    private int mRepeatState = MusicService.REPEAT_NORAML;
    private int mOrderState = MusicService.ORDER_BY_ORDER;
    private int mLastPosition = -1;

    @Override
    public void initView() {
        // get cache model\position\repeat\order
        getCache();
        // set diaplay info
        setMusicInfo(mMusicModel);
        //
        setReceiver();
    }

    private void setReceiver() {
        mPlayingReceiver = new PlayingReceiver();
        // 创建IntentFilter
        IntentFilter filter = new IntentFilter();
        // 指定BroadcastReceiver监听的Action
        filter.addAction(MusicService.BROADCAST_ACTION_NEXT_SONGS);
        // 注册BroadcastReceiver
        mContext.registerReceiver(mPlayingReceiver, filter);
    }

    private void getCache() {
        if (ACache.get(getActivity().getBaseContext()).getAsObject(MusicService.CACHE_MODEL) != null) {
            mMusicModel = (MusicModel) ACache.get(getActivity().getBaseContext()).getAsObject(MusicService.CACHE_MODEL);
        }
        if (ACache.get(getActivity().getBaseContext()).getAsObject(MusicService.CACHE_POSITION) != null) {
            mLastPosition = (int) ACache.get(getActivity().getBaseContext()).getAsObject(MusicService.CACHE_POSITION);
        }
        if (ACache.get(getActivity().getBaseContext()).getAsObject(MusicService.CACHE_ORDER) != null) {
            mOrderState = (int) ACache.get(getActivity().getBaseContext()).getAsObject(MusicService.CACHE_ORDER);
        }
        if (ACache.get(getActivity().getBaseContext()).getAsObject(MusicService.CACHE_REPEAT) != null) {
            mRepeatState = (int) ACache.get(getActivity().getBaseContext()).getAsObject(MusicService.CACHE_REPEAT);
        }
    }

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
            case MusicService.REPEAT_NORAML:
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
    }

    @Click({R.id.ibPlay, R.id.ib_favorite, R.id.ib_repeat, R.id.ib_shuffle})
    @Override
    public void initClick(View view) {
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
                    case MusicService.REPEAT_NORAML:
                        mRepeatState = MusicService.REPEAT_LOOP_ALL;
                        view.setBackgroundResource(R.drawable.ic_music_repeat_all);
                        break;
                    case MusicService.REPEAT_LOOP_ALL:
                        mRepeatState = MusicService.REPEAT_SINGLE;
                        view.setBackgroundResource(R.drawable.ic_music_repeat_one);
                        break;
                    case MusicService.REPEAT_SINGLE:
                        mRepeatState = MusicService.REPEAT_NORAML;
                        view.setBackgroundResource(R.drawable.ic_music_repeat_close);
                        break;
                }
                sendBroadcastToService();
                break;
            case R.id.ib_favorite:
                if (isFavorite) {
                    view.setBackgroundResource(R.drawable.ic_music_favorite_normal);
                    isFavorite = false;
                } else {
                    view.setBackgroundResource(R.drawable.ic_music_favorite_pressed);
                    isFavorite = true;
                }
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

    private void pause() {
        startIntentToService(mLastPosition, MusicService.PLY_PAUSE);
    }

    private void play(int position) {
        startIntentToService(position, MusicService.PLY_PLAY);
    }

    private void sendBroadcastToService() {
        Intent intent = new Intent(MusicService.BROADCAST_ACTION_CHANGE_STATUS);
        Bundle bundle = new Bundle();
        bundle.putInt(MusicService.REPEAT, mRepeatState);
        bundle.putInt(MusicService.ORDER, mOrderState);
        intent.putExtras(bundle);
        mContext.sendBroadcast(intent);
    }

    private void startIntentToService(int position, int action) {
        Intent intent = new Intent(mContext, MusicService.class);
        intent.setAction(MusicService.SERVICE_ACTION);
        intent.putExtra(MusicService.MUSIC_POSITION, position);
        intent.putExtra(MusicService.MUSIC_ACTION, action);
        mContext.startService(intent);
    }

    private class PlayingReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (action.equals(MusicService.BROADCAST_ACTION_NEXT_SONGS)) {
                mMusicModel = (MusicModel) intent.getExtras().getSerializable(MusicService.MUSIC_MODEL);
                mLastPosition = intent.getExtras().getInt(MusicService.MUSIC_POSITION, -1);

            } else if (action.equals(MusicService.BROADCAST_ACTION_CURRENT_TIME)) {
                // TODO: 2017/10/19
                int curTime = intent.getIntExtra(MusicService.PLY_CURRENT_TIME, -1);

            }


            if (action.equals(MusicService.ACTION_MUSIC_CURRENT)) {
//                //currentTime代表当前播放的时间
//                mCurrentTime = intent.getIntExtra(MusicService.PLY_CURRENT_TIME, -1);
//                tvCurrentTime.setText(mCurrentTime);
//
            } else if (action.equals(MusicService.ACTION_MUSIC_DURATION)) {
//                mDuration = intent.getIntExtra(MusicService.PLY_DURATION, -1);
//
            } else if (action.equals(MusicService.ACTION_UPDATE_ACTION)) {
//                //获取Intent中的current消息，current代表当前正在播放的歌曲
//                mListPosition = intent.getIntExtra(MusicService.PLY_POSITION, -1);
//                if (mListPosition >= 0) {
//                    musicTitle.setText(mp3Infos.get(mListPosition).getTitle());
//                }
            } else if (action.equals(MusicService.ACTION_REPEAT_ACTION)) {
//                mRepeatState = intent.getIntExtra(MusicService.STATUS, -1);
//                switch (mRepeatState) {
//                    case MusicService.STATUS_SINGLE: // 单曲循环
//                        repeatBtn.setBackgroundResource(R.drawable.repeat_current_selector);
//                        shuffleBtn.setClickable(false);
//                        break;
//                    case MusicService.STATUS_LOOP_ALL: // 全部循环
//                        repeatBtn.setBackgroundResource(R.drawable.repeat_all_selector);
//                        shuffleBtn.setClickable(false);
//                        break;
//                    case MusicService.STATUS_BY_ORDER: // 无重复
//                        repeatBtn.setBackgroundResource(R.drawable.repeat_none_selector);
//                        shuffleBtn.setClickable(true);
//                        break;
//                }
            } else if (action.equals(MusicService.ACTION_SHUFFLE_ACTION)) {
//                isShuffle = intent.getBooleanExtra("shuffleState", false);
//                if (isShuffle) {
//                    isNoneShuffle = false;
//                    shuffleBtn.setBackgroundResource(R.drawable.shuffle_selector);
//                    repeatBtn.setClickable(false);
//                } else {
//                    isNoneShuffle = true;
//                    shuffleBtn.setBackgroundResource(R.drawable.shuffle_none_selector);
//                    repeatBtn.setClickable(true);
//                }
            }
        }
    }
}

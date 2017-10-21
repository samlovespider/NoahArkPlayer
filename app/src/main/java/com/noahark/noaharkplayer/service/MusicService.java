package com.noahark.noaharkplayer.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.alibaba.fastjson.JSONArray;
import com.noahark.noaharkplayer.BuildConfig;
import com.noahark.noaharkplayer.base.util.ACache;
import com.noahark.noaharkplayer.base.util.SCLogHelper;
import com.noahark.noaharkplayer.model.MusicModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.noahark.noaharkplayer.ui.fragment.PlayingListFragment.MUSICLIST;

/**
 * @author caizhenliang
 */
public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    //
    public static final String MUSIC_ACTION = "action";
    public static final String MUSIC_POSITION = "position";
    public static final String MUSIC_MODEL = "model";
    public static final String MUSIC_FAVORITE = "favorite";
    //
    public static final int PLY_PLAY = 1;
    public static final int PLY_PAUSE = 2;
    public static final int PLY_CONTINUE = 3;
    public static final int PLY_PRIVIOUS = 4;
    public static final int PLY_NEXT = 5;
    //
    public static final String REPEAT = "repeat";
    public static final int REPEAT_NORMAL = 0;
    public static final int REPEAT_SINGLE = 1;
    public static final int REPEAT_LOOP_ALL = 2;
    //
    public static final String ORDER = "order";
    public static final int ORDER_BY_ORDER = 3;
    public static final int ORDER_BY_RANDOM = 4;
    //
    public static final String CACHE_MODEL = "cache_model";
    public static final String CACHE_POSITION = "cache_position";
    public static final String CACHE_REPEAT = "cache_repeat";
    public static final String CACHE_ORDER = "cache_order";
    public static final String CACHE_COUNT = "cache_count";
    //
    public static final String SERVICE_ACTION = "com.noahark.musicservice";
    public static final String BROADCAST_ACTION_CHANGE_STATUS = "com.noahark.action.change_status";
    public static final String BROADCAST_ACTION_NEXT_SONGS = "com.noahark.action.next_songs";
    public static final String BROADCAST_ACTION_CURRENT_TIME = "com.noahark.action.current_time";
    public static final String BROADCAST_ACTION_NOW_PLAYING = "com.noahark.action.now_playing";
    //----------------------------------------------------------------
    public static final String PLY_CURRENT_TIME = "currenttime";
    //
    private static final String TAG = "MusicService";

    //--------------------
    private Handler mHandler;// use handler to receive the messages，to send the refresh duration of broadcast
    private MyReceiver mReceiver;
    private MediaPlayer mPlayer;
    private List<MusicModel> mList;
    private List<Integer> mPrePositions;
    private MusicModel mMusicModel;
    private int mCurrPosition = -1;
    private int mRepeatState = REPEAT_NORMAL;
    private int mOrderState = ORDER_BY_ORDER;
    private int mCurrentTime = 0;
    private int mAction;
    private int mCount = 0;// count how much songs already played

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //
        mPlayer = new MediaPlayer();
        mPrePositions = new ArrayList<>();
        //
        getCache();
        //
        setReceiver();
        // send broadcast to PlayingListFragment
        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if (message.what == 1) {
                    if (mPlayer != null) {
                        mCurrentTime = mPlayer.getCurrentPosition(); // gain the position of music 获取当前音乐播放的位置
                        Intent intent = new Intent();
                        intent.setAction(BROADCAST_ACTION_CURRENT_TIME);
                        intent.putExtra(PLY_CURRENT_TIME, mCurrentTime);
                        sendBroadcast(intent); // send Broadcast to MusicActivity 给MusicActivity发送广播
                        mHandler.sendEmptyMessageDelayed(1, 1000);
                    }
                }
                return true;
            }
        });
        mPlayer.setOnCompletionListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent == null) {
            return START_STICKY;
        }

        mCurrPosition = intent.getIntExtra(MUSIC_POSITION, -1);
        mAction = intent.getIntExtra(MUSIC_ACTION, -1);

        switch (mAction) {
            case PLY_PLAY:
                play(mCurrPosition);
                break;
            case PLY_PAUSE:
                pause();
                break;
            case PLY_CONTINUE:
                resume();
                break;
            case PLY_PRIVIOUS:
                int position = mCurrPosition;
                if (mPrePositions.size() > 0) {
                    mPrePositions.remove(mPrePositions.size() - 1);
                }
                if (mPrePositions.size() > 0) {
                    position = mPrePositions.get(mPrePositions.size() - 1);
                    mCurrPosition = position;
                }
                previous(position);
                break;
            case PLY_NEXT:
                getNextPosition(mCurrPosition);
                next(mCurrPosition);
                break;
        }
        return START_STICKY;
    }

    private void setReceiver() {
        mReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCAST_ACTION_CHANGE_STATUS);
        registerReceiver(mReceiver, filter);
    }

    private void play(int position) {
        try {
            mPlayer.reset();
            //
            mMusicModel = mList.get(position);
            mPlayer.setDataSource(mMusicModel.mData);
            //
            mMusicModel.isPlaying = true;
            //
            mPlayer.prepare();
            mPlayer.setOnPreparedListener(this);// Register a listener
            //
            setCache();
            //
            mHandler.sendEmptyMessage(1);
            sendBroadcastToPlaying(mMusicModel);
            sendBroadcastToList(mMusicModel);
            //
            if (mCurrPosition != -1 && mAction != PLY_PRIVIOUS) {
                mPrePositions.add(mCurrPosition);
                if (BuildConfig.DEBUG) {
                    SCLogHelper.w(TAG, "play mPrePositions", mPrePositions);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void pause() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
            mMusicModel.isPlaying = false;
            sendBroadcastToPlaying(mMusicModel);
            sendBroadcastToList(mMusicModel);
            mHandler.removeMessages(1);
            //
            setCache();
        }
    }

    private void resume() {
        if (!mPlayer.isPlaying()) {
            mPlayer.start();
        }
    }

    private void previous(int position) {
        if (position >= 0) {
            play(position);
            mMusicModel = mList.get(position);
            sendBroadcastToList(mMusicModel);
        } else {
            mCurrPosition++;
            stop();
        }
    }

    private void next(int position) {
        if (position <= mList.size() - 1) {
            play(position);
            mMusicModel = mList.get(position);
            sendBroadcastToList(mMusicModel);
        } else {
            mCurrPosition--;
            stop();
        }
    }

    private void setCache() {
        if (mMusicModel != null) {
            ACache.get(getBaseContext()).put(CACHE_MODEL, mMusicModel);
        }
        if (mCurrPosition != -1) {
            ACache.get(getBaseContext()).put(CACHE_POSITION, mCurrPosition);
        }
        ACache.get(getBaseContext()).put(CACHE_ORDER, mOrderState);
        ACache.get(getBaseContext()).put(CACHE_REPEAT, mRepeatState);
        ACache.get(getBaseContext()).put(CACHE_COUNT, mCount);
    }

    private void getCache() {
        if (ACache.get(getBaseContext()).getAsString(MUSICLIST) != null) {
            String lists = ACache.get(getBaseContext()).getAsString(MUSICLIST);
            mList = JSONArray.parseArray(lists, MusicModel.class);
            if (BuildConfig.DEBUG) {
                SCLogHelper.w(TAG, "onCreate MusicList", mList);
            }
        }
        if (ACache.get(getBaseContext()).getAsObject(MusicService.CACHE_MODEL) != null) {
            mMusicModel = (MusicModel) ACache.get(getBaseContext()).getAsObject(MusicService.CACHE_MODEL);
        }
        if (ACache.get(getBaseContext()).getAsObject(MusicService.CACHE_POSITION) != null) {
            mCurrPosition = (int) ACache.get(getBaseContext()).getAsObject(MusicService.CACHE_POSITION);
            mPrePositions.add(mCurrPosition);
        }
        if (ACache.get(getBaseContext()).getAsObject(MusicService.CACHE_ORDER) != null) {
            mOrderState = (int) ACache.get(getBaseContext()).getAsObject(MusicService.CACHE_ORDER);
        }
        if (ACache.get(getBaseContext()).getAsObject(MusicService.CACHE_REPEAT) != null) {
            mRepeatState = (int) ACache.get(getBaseContext()).getAsObject(MusicService.CACHE_REPEAT);
        }
    }

    private void stop() {
        if (mPlayer != null) {
            mPlayer.pause();
            mPlayer.stop();
            mPrePositions.clear();
            mMusicModel.isPlaying = false;
            sendBroadcastToPlaying(mMusicModel);
            sendBroadcastToList(mMusicModel);
            mHandler.removeMessages(1);
            //
            setCache();
        }
    }

    private int getRandomIndex(int size) {
        Random random = new Random(new Random().nextLong());
        return random.nextInt(size);
    }

    private void sendBroadcastToPlaying(MusicModel model) {
        Intent intent = new Intent(BROADCAST_ACTION_NOW_PLAYING);
        Bundle bundle = new Bundle();
        bundle.putSerializable(MUSIC_MODEL, model);
        bundle.putInt(MUSIC_POSITION, mCurrPosition);
        intent.putExtras(bundle);
        sendBroadcast(intent);
    }

    private void sendBroadcastToList(MusicModel model) {
        Intent intent = new Intent(MusicService.BROADCAST_ACTION_NEXT_SONGS);
        Bundle bundle = new Bundle();
        bundle.putSerializable(MusicService.MUSIC_MODEL, model);
        bundle.putInt(MusicService.MUSIC_POSITION, mCurrPosition);
        intent.putExtras(bundle);
        sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
        //
        mHandler.removeMessages(1);
        setCache();
        unregisterReceiver(mReceiver);
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        mCount++;
        if (mCount == mList.size()) {
            if (mRepeatState == REPEAT_LOOP_ALL) {
                mCurrPosition = 0;
                mCount = 0;
                getNextPosition(mCurrPosition);
                next(mCurrPosition);

            } else if (mRepeatState == REPEAT_SINGLE) {
                mCount--;
                next(mCurrPosition);

            } else if (mRepeatState == REPEAT_NORMAL) {
                mCount = 0;
                stop();
            }
        } else {
            if (mRepeatState == REPEAT_LOOP_ALL) {
                mCurrPosition = 0;
                mCount = 0;
                getNextPosition(mCurrPosition);
                next(mCurrPosition);

            } else if (mRepeatState == REPEAT_SINGLE) {
                mCount--;
                next(mCurrPosition);

            } else if (mRepeatState == REPEAT_NORMAL) {
                getNextPosition(mCurrPosition);
                next(mCurrPosition);
            }
        }
        sendBroadcastToList(mMusicModel);
        sendBroadcastToPlaying(mMusicModel);
    }

    private void getNextPosition(int curPosition) {
        if (mOrderState == ORDER_BY_ORDER) {
            mCurrPosition++;
        } else {
            mCurrPosition = getRandomIndex(mList.size());
            while (mCurrPosition == curPosition) {
                mCurrPosition = getRandomIndex(mList.size());
            }
        }
    }

    public class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            mRepeatState = intent.getIntExtra(REPEAT, REPEAT_NORMAL);
            mOrderState = intent.getIntExtra(ORDER, ORDER_BY_ORDER);
            mMusicModel.isFavorite = intent.getBooleanExtra(MUSIC_FAVORITE, false);
            setCache();
        }
    }
}

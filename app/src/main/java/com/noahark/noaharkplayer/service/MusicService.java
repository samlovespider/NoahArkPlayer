package com.noahark.noaharkplayer.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.alibaba.fastjson.JSONArray;
import com.caizhenliang.mylibrary.util.SCLogHelper;
import com.caizhenliang.mylibrary.util.base.ACache;
import com.noahark.noaharkplayer.BuildConfig;
import com.noahark.noaharkplayer.model.MusicModel;

import java.util.List;
import java.util.Random;

import static com.noahark.noaharkplayer.ui.activity.MainActivity.MUSICLIST;

/**
 * @author caizhenliang
 */
public class MusicService extends Service implements MediaPlayer.OnPreparedListener {

    //
    public static final String MUSIC_ACTION = "action";
    public static final String MUSIC_POSITION = "position";
    public static final String MUSIC_MODEL = "model";
    //
    public static final int PLY_PLAY = 1;
    public static final int PLY_PAUSE = 2;
    public static final int PLY_CONTINUE = 3;
    public static final int PLY_PRIVIOUS = 4;
    public static final int PLY_NEXT = 5;
    //
    public static final String STATUS = "status";
    public static final int STATUS_SINGLE = 1;
    public static final int STATUS_LOOP_ALL = 2;
    public static final int STATUS_BY_ORDER = 3;
    public static final int STATUS_BY_RANDOM = 4;
    //
    public static final String CACHE_MODEL = "cache_model";
    public static final String CACHE_POSITION = "cache_position";
    //
    public static final String SERVICE_ACTION = "com.noahark.musicservice";
    public static final String BROADCAST_ACTION_CHANGE_STATUS = "com.noahark.action.change_status";
    public static final String BROADCAST_ACTION_NEXT_SONGS = "com.noahark.action.next_songs";
    //----------------------------------------------------------------
    public static final String MUSIC_DATA = "data";
    public static final String PLY_CURRENT_TIME = "currenttime";
    public static final int PLY_STOP = 3;
    public static final int PLY_PROGRESS = 7;
    public static final int PLY_PLAYING = 8;
    //BroadcastReceiver
    public static final String ACTION_UPDATE_ACTION = "com.noahark.action.UPDATE_ACTION";  //更新动作
    public static final String ACTION_CTL_ACTION = "com.noahark.action.CTL_ACTION";        //控制动作
    public static final String ACTION_MUSIC_CURRENT = "com.noahark.action.MUSIC_CURRENT";  //当前音乐播放时间更新动作
    public static final String ACTION_MUSIC_DURATION = "com.noahark.action.MUSIC_DURATION";//新音乐长度更新动作
    public static final String ACTION_REPEAT_ACTION = "com.noahark.action.REPEAT_ACTION";
    public static final String ACTION_SHUFFLE_ACTION = "com.noahark.action.SHUFFLE_ACTION";
    //
    private static final String TAG = "MusicService";
    //
//    private Handler handler;// handler用来接收消息，来发送广播更新播放时间
//    //
//    private int mCurrentTime;
//    private int mDuration;


    //--------------------
    private MyReceiver mReceiver;
    private MediaPlayer mPlayer;
    private List<MusicModel> mList;
    private int mCurrPosition;
    private int mAction;
    private int mRepeatState = STATUS_BY_ORDER;


    //--------------------

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
        //
        String lists = ACache.get(getBaseContext()).getAsString(MUSICLIST);
        mList = JSONArray.parseArray(lists, MusicModel.class);
        if (BuildConfig.DEBUG) {
            SCLogHelper.w(TAG, "onCreate MusicList", mList);
        }
        //
        setReceiver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

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
                previous();
                break;
            case PLY_NEXT:
                next();
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
            String data = mList.get(position).mData;
            mPlayer.setDataSource(data);
            //
            mPlayer.prepare();
            mPlayer.setOnPreparedListener(this);// 注册一个监听器
            //
            setCache();
//            handler.sendEmptyMessage(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void pause() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
        }
    }

    private void resume() {
        if (!mPlayer.isPlaying()) {
            mPlayer.start();
        }
    }

    private void previous() {
        mCurrPosition--;
        if (mCurrPosition >= 0) {
            play(mCurrPosition);
            sendBroadcastToHome(mList.get(mCurrPosition));
        } else {
            stop();
        }
    }

    private void next() {
        mCurrPosition++;
        if (mCurrPosition <= mList.size() - 1) {
            play(mCurrPosition);
            sendBroadcastToHome(mList.get(mCurrPosition));
        } else {
            stop();
        }
    }

    private void setCache() {
        ACache.get(getBaseContext()).put(CACHE_MODEL, mList.get(mCurrPosition));
        ACache.get(getBaseContext()).put(CACHE_POSITION, mCurrPosition);
    }

    private void getCache() {
        ACache.get(getBaseContext()).getAsObject(CACHE_MODEL);
        mCurrPosition = (int) ACache.get(getBaseContext()).getAsObject(CACHE_POSITION);
    }

    private void stop() {


        if (mPlayer != null) {
            mPlayer.stop();
            try {
                mPlayer.prepare();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private int getRandomIndex(int size) {
        Random random = new Random(new Random().nextLong());
        return random.nextInt(size);
    }

    private void sendBroadcastToHome(MusicModel model) {
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
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }

    public class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            mRepeatState = intent.getIntExtra(STATUS, STATUS_BY_ORDER);
        }
    }
}

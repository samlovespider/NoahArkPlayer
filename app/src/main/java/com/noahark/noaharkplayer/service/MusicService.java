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
import com.caizhenliang.mylibrary.util.SCLogHelper;
import com.caizhenliang.mylibrary.util.base.ACache;
import com.noahark.noaharkplayer.BuildConfig;
import com.noahark.noaharkplayer.model.MusicModel;

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
    public static final String MUSIC_REPEAT_STATUS = "music_repeat_status";
    public static final String MUSIC_ORDER_STATUS = "music_order_status";
    //
    public static final int PLY_PLAY = 1;
    public static final int PLY_PAUSE = 2;
    public static final int PLY_CONTINUE = 3;
    public static final int PLY_PRIVIOUS = 4;
    public static final int PLY_NEXT = 5;
    //
    public static final String REPEAT = "repeat";
    public static final int REPEAT_NORAML = 0;
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
    //
    public static final String SERVICE_ACTION = "com.noahark.musicservice";
    public static final String BROADCAST_ACTION_CHANGE_STATUS = "com.noahark.action.change_status";
    public static final String BROADCAST_ACTION_NEXT_SONGS = "com.noahark.action.next_songs";
    public static final String BROADCAST_ACTION_CURRENT_TIME = "com.noahark.action.current_time";
    public static final String BROADCAST_ACTION_NOW_PLAYING = "com.noahark.action.now_playing";
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
//    //
//    private int mCurrentTime;
//    private int mDuration;

    //--------------------
    private Handler mHandler;// handler用来接收消息，来发送广播更新播放时间
    private MyReceiver mReceiver;
    private MediaPlayer mPlayer;
    private List<MusicModel> mList;
    private int mCurrPosition;
    private int mAction;
    private int mRepeatState = REPEAT_NORAML;
    private int mOrderState = ORDER_BY_ORDER;
    private int mCurrentTime;


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
        // send broadcast to PlayingListFragment
        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if (message.what == 1) {
                    if (mPlayer != null) {
                        mCurrentTime = mPlayer.getCurrentPosition(); // 获取当前音乐播放的位置
                        Intent intent = new Intent();
                        intent.setAction(BROADCAST_ACTION_CURRENT_TIME);
                        intent.putExtra(PLY_CURRENT_TIME, mCurrentTime);
                        sendBroadcast(intent); // 给MusicActivity发送广播
                        mHandler.sendEmptyMessageDelayed(1, 1000);
                    }
                }
                return true;
            }
        });
        //
        mPlayer.setOnCompletionListener(this);
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
            //
            mHandler.sendEmptyMessage(1);
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
            sendBroadcastToList(mList.get(mCurrPosition));
        } else {
            stop();
        }
    }

    private void next() {
        mCurrPosition++;
        if (mCurrPosition <= mList.size() - 1) {
            play(mCurrPosition);
            sendBroadcastToList(mList.get(mCurrPosition));
        } else {
            stop();
        }
    }

    private void setCache() {
        ACache.get(getBaseContext()).put(CACHE_ORDER, mOrderState);
        ACache.get(getBaseContext()).put(CACHE_REPEAT, mRepeatState);
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

    private void sendBroadcastToPlaying(MusicModel model) {
        Intent intent = new Intent(MusicService.BROADCAST_ACTION_NOW_PLAYING);
        Bundle bundle = new Bundle();
        bundle.putSerializable(MusicService.MUSIC_MODEL, model);
        bundle.putInt(MusicService.MUSIC_POSITION, mCurrPosition);
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
        setCache();
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }

    public class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            mRepeatState = intent.getIntExtra(REPEAT, REPEAT_NORAML);
            mOrderState = intent.getIntExtra(ORDER, ORDER_BY_ORDER);
        }
    }
}

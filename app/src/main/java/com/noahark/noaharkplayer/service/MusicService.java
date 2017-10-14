package com.noahark.noaharkplayer.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.caizhenliang.mylibrary.util.SCCache;
import com.noahark.noaharkplayer.model.MusicModel;

import org.androidannotations.annotations.EService;

import java.util.List;
import java.util.Random;

import static com.noahark.noaharkplayer.ui.activity.MainActivity.MUSICLIST;

/**
 * @author caizhenliang
 */
@EService
public class MusicService extends Service {

    public static final String PLY_ACTION = "action";
    public static final String PLY_DATA = "data";
    public static final String PLY_POSITION = "position";
    public static final String PLY_CURRENT_TIME = "currenttime";
    public static final String PLY_DURATION = "duration";
    public static final int PLY_PLAY = 1;
    public static final int PLY_PAUSE = 2;
    public static final int PLY_STOP = 3;
    public static final int PLY_CONTINUE = 4;
    public static final int PLY_PRIVIOUS = 5;
    public static final int PLY_NEXT = 6;
    public static final int PLY_PROGRESS = 7;
    public static final int PLY_PLAYING = 8;
    //
    public static final String STATUS = "status";
    public static final int STATUS_SINGLE = 1;
    public static final int STATUS_LOOP_ALL = 2;
    public static final int STATUS_BY_ORDER = 3;
    public static final int STATUS_BY_RANDOM = 4;
    //
    public static final String ACTION_UPDATE_ACTION = "com.noahark.action.UPDATE_ACTION";  //更新动作
    public static final String ACTION_CTL_ACTION = "com.noahark.action.CTL_ACTION";        //控制动作
    public static final String ACTION_MUSIC_CURRENT = "com.noahark.action.MUSIC_CURRENT";  //当前音乐播放时间更新动作
    public static final String ACTION_MUSIC_DURATION = "com.noahark.action.MUSIC_DURATION";//新音乐长度更新动作
    public static final String ACTION_REPEAT_ACTION = "com.noahark.action.REPEAT_ACTION";
    public static final String ACTION_SHUFFLE_ACTION = "com.noahark.action.SHUFFLE_ACTION";
    //
    private MediaPlayer mMediaPlayer;
    private MyReceiver mReceiver;
    private List<MusicModel> mMusicList;
    private Handler handler;// handler用来接收消息，来发送广播更新播放时间
    //
    private int mCurrentPosition;
    private int mCurrentTime;
    private int mPlayStatus = STATUS_BY_ORDER;
    private int mDuration;
    private String mData;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if (message.what == 1) {
                    if (mMediaPlayer != null) {
                        mCurrentTime = mMediaPlayer.getCurrentPosition(); // 获取当前音乐播放的位置
                        Intent intent = new Intent();
                        intent.setAction(ACTION_MUSIC_CURRENT);
                        intent.putExtra(PLY_CURRENT_TIME, mCurrentTime);
                        sendBroadcast(intent); // 给MusicActivity发送广播
                        handler.sendEmptyMessageDelayed(1, 1000);
                    }
                }
                return true;
            }
        });

        mMediaPlayer = new MediaPlayer();
        mMusicList = SCCache.getInstance(getBaseContext()).get(MUSICLIST, MusicModel.class);

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                switch (mPlayStatus) {
                    case STATUS_SINGLE:
                        mediaPlayer.start();
                        break;
                    case STATUS_LOOP_ALL:
                        if (++mCurrentPosition > mMusicList.size() - 1) {  //变为第一首的位置继续播放
                            mCurrentPosition = 0;
                        }
                        doWhenComplete(mCurrentPosition);
                        break;
                    case STATUS_BY_ORDER:
                        if (++mCurrentPosition <= mMusicList.size() - 1) {
                            doWhenComplete(mCurrentPosition);
                        } else {
                            mediaPlayer.seekTo(0);
                            mCurrentPosition = 0;
                            Intent sendIntent = new Intent(ACTION_UPDATE_ACTION);
                            sendIntent.putExtra(PLY_POSITION, mCurrentPosition);
                            sendBroadcast(sendIntent);
                        }
                        break;
                    case STATUS_BY_RANDOM:
                        mCurrentPosition = getRandomIndex(mMusicList.size() - 1);
                        doWhenComplete(mCurrentPosition);
                        break;
                }
            }
        });

        //
        setReceiver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mData = intent.getStringExtra(PLY_DATA);
        mCurrentPosition = intent.getIntExtra(PLY_POSITION, -1);
        //
        int action = intent.getIntExtra(PLY_ACTION, 0);
        switch (action) {
            case PLY_PLAY:
                play(0);
                break;
            case PLY_PAUSE:
                pause();
                break;
            case PLY_STOP:
                stop();
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
            case PLY_PROGRESS:
                mCurrentTime = intent.getIntExtra(PLY_CURRENT_TIME, -1);
                play(mCurrentTime);
                break;
            case PLY_PLAYING:
                handler.sendEmptyMessage(1);
                break;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private void doWhenComplete(int currentPosition) {
        Intent sendIntent = new Intent(ACTION_UPDATE_ACTION);
        sendIntent.putExtra(PLY_POSITION, currentPosition);
        // 发送广播，将被Activity组件中的BroadcastReceiver接收到
        sendBroadcast(sendIntent);
        mData = mMusicList.get(currentPosition).mData;
        play(0);
    }

    private void setReceiver() {
        mReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_CTL_ACTION);
        registerReceiver(mReceiver, filter);
    }

    private int getRandomIndex(int end) {
        Random random = new Random(new Random().nextLong());
        return random.nextInt() * end;
    }

    private void play(int currentTime) {
        try {
            mMediaPlayer.reset();// 把各项参数恢复到初始状态
            mMediaPlayer.setDataSource(mData);
            mMediaPlayer.prepare(); // 进行缓冲
            mMediaPlayer.setOnPreparedListener(new PreparedListener(currentTime));// 注册一个监听器
            handler.sendEmptyMessage(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void pause() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
    }

    private void resume() {
        if (!mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
        }
    }

    private void previous() {
        Intent sendIntent = new Intent(ACTION_UPDATE_ACTION);
        sendIntent.putExtra(PLY_POSITION, mCurrentPosition);
        // 发送广播，将被Activity组件中的BroadcastReceiver接收到
        sendBroadcast(sendIntent);
        play(0);
    }

    private void next() {
        Intent sendIntent = new Intent(ACTION_UPDATE_ACTION);
        sendIntent.putExtra(PLY_POSITION, mCurrentPosition);
        // 发送广播，将被Activity组件中的BroadcastReceiver接收到
        sendBroadcast(sendIntent);
        play(0);
    }

    private void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            try {
                mMediaPlayer.prepare(); // 在调用stop后如果需要再次通过start进行播放,需要之前调用prepare函数
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private class PreparedListener implements MediaPlayer.OnPreparedListener {

        private int currentTime;

        private PreparedListener(int currentTime) {
            this.currentTime = currentTime;
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            mMediaPlayer.start(); // 开始播放
            if (currentTime > 0) { // 如果音乐不是从头播放
                mMediaPlayer.seekTo(currentTime);
            }
            Intent intent = new Intent();
            intent.setAction(ACTION_MUSIC_DURATION);
            intent.putExtra(PLY_DURATION, mMediaPlayer.getDuration());  //通过Intent来传递歌曲的总长度
            sendBroadcast(intent);
        }
    }

    public class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            mPlayStatus = intent.getIntExtra(STATUS, -1);
        }
    }
}

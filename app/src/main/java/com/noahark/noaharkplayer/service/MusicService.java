package com.noahark.noaharkplayer.service;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.Nullable;

import com.noahark.noaharkplayer.model.MusicModel;

import org.androidannotations.annotations.EService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by caizhenliang on 2017/10/8.
 */

@EService
public class MusicService extends Service {

    public static final String ACTION = "action";
    public static final int PLAY = 1;       //播放
    public static final int PAUSE = 2;      //暂停
    public static final int STOP = 3;       //停止
    public static final int CONTINUE = 4;   //继续
    public static final int PRIVIOUS = 5;   //上一首
    public static final int NEXT = 6;       //下一首
    public static final int PROGRESS = 7;//进度改变
    public static final int PLAYING = 8;    //正在播放

    private MediaPlayer mMediaPlayer;
    private List<MusicModel> mMusicList;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mMediaPlayer = new MediaPlayer();
        mMusicList = getMusics();

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        int action = intent.getIntExtra(ACTION, 0);
        switch (action) {
            case PLAY:
                break;
            case PAUSE:
                break;
            case STOP:
                break;
            case CONTINUE:
                break;
            case PRIVIOUS:
                break;
            case NEXT:
                break;
            case PROGRESS:
                break;
            case PLAYING:
                break;
        }

        return super.onStartCommand(intent, flags, startId);
    }


    private void playMusic(String data) {
        if (mMediaPlayer == null) {
            //调用MediaPlayer的静态方法create
            mMediaPlayer = new MediaPlayer();
        }
        try {
            mMediaPlayer.setDataSource(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.start();
    }

    private void stopPlay() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();//停止
            mMediaPlayer.reset();//重置
            mMediaPlayer.release();//释放资源
            mMediaPlayer = null;//重新赋值为空
        }
    }

    private void pausePlay() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
    }


    /**
     * get musics' information from MediaStore.Audio.AudioColumns
     *
     * @return List<MusicModel>
     */
    private List<MusicModel> getMusics() {

        //- get cursor -------------
        List<MusicModel> musicModelList = new ArrayList<>();
        String[] projection = {
                MediaStore.Audio.AudioColumns._ID, //
                MediaStore.Audio.AudioColumns.DATA, //
                MediaStore.Audio.AudioColumns.DISPLAY_NAME, //
                MediaStore.Audio.AudioColumns.ALBUM, //
                MediaStore.Audio.AudioColumns.ALBUM_ID,//
                MediaStore.Audio.AudioColumns.ARTIST, //
                MediaStore.Audio.AudioColumns.DURATION, //
                MediaStore.MediaColumns.SIZE //
        };
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        Cursor cursor = getContentResolver().query(uri, projection, null, null, MediaStore.Audio.Media.DATE_ADDED + " desc");
        if (cursor == null) {
            return null;
        }

        //- get list ---------------------------
        String id;
        String data;
        String name;
        String album;
        String albumid;
        String artist;
        String duration;
        String size;
        //
        cursor.moveToNext();
        while (!cursor.isAfterLast()) {
            //
            id = cursor.getString(0) == null ? "" : cursor.getString(0);
            data = cursor.getString(1) == null ? "" : cursor.getString(1);
            name = cursor.getString(2) == null ? "" : cursor.getString(2);
            album = cursor.getString(3) == null ? "" : cursor.getString(3);
            albumid = cursor.getString(4) == null ? "" : cursor.getString(4);
            artist = cursor.getString(5) == null ? "" : cursor.getString(5);
            duration = cursor.getString(6) == null ? "" : cursor.getString(6);
            size = cursor.getString(7) == null ? "" : cursor.getString(7);
            //
            MusicModel tmp = new MusicModel(id, data, name, album, albumid, artist, duration, size);
            musicModelList.add(tmp);
            //
            cursor.moveToNext();
        }
        cursor.close();
        return musicModelList;
    }
}

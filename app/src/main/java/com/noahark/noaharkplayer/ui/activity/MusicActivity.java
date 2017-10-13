package com.noahark.noaharkplayer.ui.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.noahark.noaharkplayer.R;
import com.noahark.noaharkplayer.adapter.MusicListAdapter;
import com.noahark.noaharkplayer.base.ui.BaseActivity;
import com.noahark.noaharkplayer.model.MusicModel;
import com.noahark.noaharkplayer.service.MusicService;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

/**
 * @author caizhenliang
 */
@EActivity(R.layout.activity_music)
public class MusicActivity extends BaseActivity {

    //
    public static final String MUSICLIST = "musiclist";
    private static final int CODE_FOR_WRITE_PERMISSION = 1;
    //
    @ViewById(R.id.lvMusics)
    ListView lvMusics;
    @ViewById(R.id.tvCurrentTime)
    TextView tvCurrentTime;
    @ViewById(R.id.tvTotalTime)
    TextView tvTotalTime;
    @ViewById(R.id.sbTime)
    SeekBar sbTime;
    //
    private MusicListAdapter mMusicListAdapter;
    private List<MusicModel> mMusicList;
    private int mLastPaly = -1;
    private int mRepeatState = MusicService.STATUS_SINGLE;
    private HomeReceiver mHomeReceiver;  //自定义的广播接收器
    private int mCurrentTime;
    private int mDuration;
    private int mListPosition = 0;   //标识列表位置

    @Override
    public void initView() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    CODE_FOR_WRITE_PERMISSION);
            return;
        }
        setList();
        setReceiver();
    }

    @ItemClick(R.id.lvMusics)
    @Override
    public void initItemClick(int position) {
        MusicModel musicModel = mMusicList.get(position);
        if (musicModel.isPlaying) {
            musicModel.isPlaying = false;
            pausePlay();
        } else {
            musicModel.isPlaying = true;
            //
            if (mLastPaly != position) {
                mLastPaly = position;
                startPlay(musicModel.mData, position);
            } else {
                resumePlay();
            }
        }
        mMusicListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CODE_FOR_WRITE_PERMISSION) {
            if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //
                initView();
            } else {
                finish();
            }
        }
    }

    private void setReceiver() {
        mHomeReceiver = new HomeReceiver();
        // 创建IntentFilter
        IntentFilter filter = new IntentFilter();
        // 指定BroadcastReceiver监听的Action
        filter.addAction(MusicService.ACTION_UPDATE_ACTION);
        filter.addAction(MusicService.ACTION_MUSIC_CURRENT);
        filter.addAction(MusicService.ACTION_MUSIC_DURATION);
        filter.addAction(MusicService.ACTION_REPEAT_ACTION);
        filter.addAction(MusicService.ACTION_SHUFFLE_ACTION);
        // 注册BroadcastReceiver
        registerReceiver(mHomeReceiver, filter);
    }

    private void setList() {
        mMusicList = getMusics();
        mMusicListAdapter = new MusicListAdapter(this, mMusicList);
        lvMusics.setAdapter(mMusicListAdapter);
    }

    /**
     * get musics' information from MediaStore.Audio.AudioColumns
     *
     * @return List<MusicModel>
     */
    private List<MusicModel> getMusics() {

        //- get cursor -------------
        List<MusicModel> imageList = new ArrayList<>();
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
        cursor.moveToNext();
        while (!cursor.isAfterLast()) {
            //
            String id = cursor.getString(0) == null ? "" : cursor.getString(0);
            String data = cursor.getString(1) == null ? "" : cursor.getString(1);
            String name = cursor.getString(2) == null ? "" : cursor.getString(2);
            String album = cursor.getString(3) == null ? "" : cursor.getString(3);
            String albumid = cursor.getString(4) == null ? "" : cursor.getString(4);
            String artist = cursor.getString(5) == null ? "" : cursor.getString(5);
            String duration = cursor.getString(6) == null ? "" : cursor.getString(6);
            String size = cursor.getString(7) == null ? "" : cursor.getString(7);
            //
            duration = formatTime(Long.parseLong(duration));
            //
            MusicModel tmp = new MusicModel(id, data, name, album, albumid, artist, duration, size);
            imageList.add(tmp);
            cursor.moveToNext();
        }
        cursor.close();
        //
        setCache(imageList);
        return imageList;
    }

    private void setCache(List<MusicModel> imageList) {
        mCache.put(MUSICLIST, imageList);
    }

    /**
     * 格式化时间，将毫秒转换为分:秒格式
     *
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

    private void startPlay(String data, int position) {
        startIntent(MusicService.PLY_PLAY, data, position);
    }

    private void resumePlay() {
        startIntent(MusicService.PLY_CONTINUE, "", -1);
    }

    private void pausePlay() {
        startIntent(MusicService.PLY_PAUSE, "", -1);
    }

    private void stopPlay() {
        startIntent(MusicService.PLY_STOP, "", -1);
    }

    private void startIntent(int action, String data, int position) {
        Intent intent = new Intent(this, MusicService.class);
        intent.putExtra(MusicService.PLY_ACTION, action);
        intent.putExtra(MusicService.PLY_DATA, data);
        intent.putExtra(MusicService.PLY_POSITION, position);
        startService(intent);
    }

    //自定义的BroadcastReceiver，负责监听从Service传回来的广播
    public class HomeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (action.equals(MusicService.ACTION_MUSIC_CURRENT)) {
//                //currentTime代表当前播放的时间
//                mCurrentTime = intent.getIntExtra(MusicService.PLY_CURRENT_TIME, -1);
//                tvCurrentTime.setText(mCurrentTime);
//
//            } else if (action.equals(MusicService.ACTION_MUSIC_DURATION)) {
//                mDuration = intent.getIntExtra(MusicService.PLY_DURATION, -1);
//
//            } else if (action.equals(MusicService.ACTION_UPDATE_ACTION)) {
//                //获取Intent中的current消息，current代表当前正在播放的歌曲
//                mListPosition = intent.getIntExtra(MusicService.PLY_POSITION, -1);
//                if (mListPosition >= 0) {
//                    musicTitle.setText(mp3Infos.get(mListPosition).getTitle());
//                }
//            } else if (action.equals(MusicService.ACTION_REPEAT_ACTION)) {
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
//            } else if (action.equals(MusicService.ACTION_SHUFFLE_ACTION)) {
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
//            }
        }
    }
}

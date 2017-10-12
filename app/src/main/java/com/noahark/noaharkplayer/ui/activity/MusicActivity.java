package com.noahark.noaharkplayer.ui.activity;

import android.Manifest;
import android.content.Intent;
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
import com.noahark.noaharkplayer.base.ui.BaseActivity;
import com.noahark.noaharkplayer.model.MusicModel;
import com.noahark.noaharkplayer.service.MusicService;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

/**
 * @author caizhenliang
 */
@EActivity(R.layout.activity_music)
public class MusicActivity extends BaseActivity {

    @ViewById(R.id.lvMusics)
    ListView lvMusics;
    @ViewById(R.id.tvCurrentTime)
    TextView tvCurrentTime;
    @ViewById(R.id.tvTotalTime)
    TextView tvTotalTime;
    @ViewById(R.id.sbTime)
    SeekBar sbTime;

    private static final int CODE_FOR_WRITE_PERMISSION = 1;
    private List<MusicModel> mMusicList;

    @Override
    public void initView() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    CODE_FOR_WRITE_PERMISSION);
        }

//        MusicListAdapter musicListAdapter = new MusicListAdapter(this, mMusicList);
//        lvMusics.setAdapter(musicListAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CODE_FOR_WRITE_PERMISSION) {
            if (!permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                finish();
            }
        }
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
            logW(tmp);
            imageList.add(tmp);
            //
            cursor.moveToNext();
        }
        cursor.close();
        return imageList;
    }

    private void playMusic() {
        startIntent(MusicService.PLAY);
    }

    private void stopPlay() {
        startIntent(MusicService.STOP);
    }

    private void pausePlay() {
        startIntent(MusicService.PAUSE);
    }

    private void startIntent(int action) {
        Intent intent = new Intent(this, MusicService.class);
        intent.putExtra(MusicService.ACTION, action);
        startService(intent);
    }
}

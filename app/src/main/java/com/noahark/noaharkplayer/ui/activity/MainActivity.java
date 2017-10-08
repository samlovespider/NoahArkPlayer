package com.noahark.noaharkplayer.ui.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.noahark.noaharkplayer.R;
import com.noahark.noaharkplayer.adapter.MusicListAdapter;
import com.noahark.noaharkplayer.base.ui.BaseActivity;
import com.noahark.noaharkplayer.model.MusicModel;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

@EActivity(R.layout.activity_main)
public class MainActivity extends BaseActivity {


    @ViewById(R.id.lvMusics)
    ListView lvMusics;
    @ViewById(R.id.linController)
    LinearLayout linController;
    @ViewById(R.id.tvCurrentTime)
    TextView tvCurrentTime;
    @ViewById(R.id.tvTotalTime)
    TextView tvTotalTime;
    @ViewById(R.id.sbTime)
    SeekBar sbTime;

    public static final String MUSIC_TIME = "com.laowang.music";
    private static final int CODE_FOR_WRITE_PERMISSION = 1;

    private List<MusicModel> mMusicList;

    @Override
    public void initData() {
        mMusicList = getMusics();
    }


    @Override
    public void initView() {
        MusicListAdapter musicListAdapter = new MusicListAdapter(this, mMusicList);
        lvMusics.setAdapter(musicListAdapter);
    }

    @Override
    public void initClick(View view) {
        gotoActivity(PlayingActivity_.class);
    }


    /**
     * get Images' information from MediaStore.Images.Media
     *
     * @return
     */
    private List<MusicModel> getMusics() {

        //- get cursor -------------
        List<MusicModel> imageList = new ArrayList<>();
        String[] projection = {
                MediaStore.Audio.AudioColumns._ID, //
                MediaStore.Audio.AudioColumns.DATA, //
                MediaStore.Audio.AudioColumns.DISPLAY_NAME, //
                MediaStore.Audio.AudioColumns.ALBUM, //
                MediaStore.Audio.AudioColumns.ARTIST, //
                MediaStore.Audio.AudioColumns.DURATION, //
                MediaStore.MediaColumns.SIZE //
        };
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        // requestPermissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MainActivity.CODE_FOR_WRITE_PERMISSION);
            return null;
        }

        Cursor cursor = getContentResolver().query(uri, projection, null, null, MediaStore.Audio.Media.DATE_ADDED + " desc");
        if (cursor == null) {
            return null;
        }

        //- get list ---------------------------
        String id;
        String data;
        String name;
        String album;
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
            artist = cursor.getString(4) == null ? "" : cursor.getString(4);
            duration = cursor.getString(5) == null ? "" : cursor.getString(5);
            size = cursor.getString(6) == null ? "" : cursor.getString(6);
            //
            MusicModel tmp = new MusicModel(id, data, name, album, artist, duration, size);
            logW(tmp);
            imageList.add(tmp);
            //
            cursor.moveToNext();
        }
        cursor.close();
        return imageList;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MainActivity.CODE_FOR_WRITE_PERMISSION) {
            if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getMusics();
            } else {
                // failed
                finish();
            }
        }
    }
}

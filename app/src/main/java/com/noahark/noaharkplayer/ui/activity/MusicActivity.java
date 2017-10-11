package com.noahark.noaharkplayer.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import org.androidannotations.annotations.ViewById;

import java.util.List;

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

        MusicListAdapter musicListAdapter = new MusicListAdapter(this, mMusicList);
        lvMusics.setAdapter(musicListAdapter);
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

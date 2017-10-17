package com.noahark.noaharkplayer.ui.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.noahark.noaharkplayer.R;
import com.noahark.noaharkplayer.base.ui.BaseFragment;
import com.noahark.noaharkplayer.model.MusicModel;
import com.noahark.noaharkplayer.service.MusicService;
import com.shinelw.library.ColorArcProgressBar;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

@EFragment(R.layout.fragment_playing)
public class PlayingFragment extends BaseFragment {

    //
    private static final int REPEAT_CLOSE = 1;
    private static final int REPEAT_ALL = 2;
    private static final int REPEAT_ONE = 3;
    //
    @FragmentArg
    MusicModel mMusicModel;
    //
    @ViewById(R.id.pbPlayProgress)
    ColorArcProgressBar pbPlayProgress;
    @ViewById(R.id.tv_music_name)
    TextView tvMusicName;
    @ViewById(R.id.tv_Artist)
    TextView tvArtist;
    @ViewById(R.id.ibPlay)
    ImageButton ibPlay;
    //
    private boolean isfavorite = false;
    private boolean isSuffle = false;
    private int mRepeatStatus = REPEAT_CLOSE;

    @Override
    public void initView() {
        checkIsPlaying(mMusicModel);
    }

    private void checkIsPlaying(MusicModel musicModel) {
        if (musicModel != null && musicModel.isPlaying) {
            ibPlay.setBackgroundResource(R.drawable.pic_music_play);
        } else {
            ibPlay.setBackgroundResource(R.drawable.pic_music_play_pause);
        }
    }

    @Click({R.id.ibPlay, R.id.ib_favorite, R.id.ib_repeat, R.id.ib_shuffle})
    @Override
    public void initClick(View view) {
        switch (view.getId()) {
            case R.id.ibPlay:
                //TODO implement
                break;
            case R.id.ib_repeat:
                switch (mRepeatStatus) {
                    case REPEAT_CLOSE:
                        mRepeatStatus = REPEAT_ALL;
                        view.setBackgroundResource(R.drawable.ic_music_repeat_all);
                        break;
                    case REPEAT_ALL:
                        mRepeatStatus = REPEAT_ONE;
                        view.setBackgroundResource(R.drawable.ic_music_repeat_one);
                        break;
                    case REPEAT_ONE:
                        mRepeatStatus = REPEAT_CLOSE;
                        view.setBackgroundResource(R.drawable.ic_music_repeat_close);
                        break;
                }
                break;
            case R.id.ib_favorite:
                if (isfavorite) {
                    view.setBackgroundResource(R.drawable.ic_music_favorite_normal);
                    isfavorite = false;
                } else {
                    view.setBackgroundResource(R.drawable.ic_music_favorite_pressed);
                    isfavorite = true;
                }
                break;
            case R.id.ib_shuffle:
                if (isSuffle) {
                    view.setBackgroundResource(R.drawable.ic_music_shuffle_normal);
                    isSuffle = false;
                } else {
                    view.setBackgroundResource(R.drawable.ic_music_shuffle_pressed);
                    isSuffle = true;
                }
                break;
        }
    }

    private class PlayingReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (action.equals(MusicService.BROADCAST_ACTION_NEXT_SONGS)) {

                MusicModel model = (MusicModel) intent.getExtras().getSerializable(MusicService.MUSIC_MODEL);
                int position = intent.getExtras().getInt(MusicService.MUSIC_POSITION, -1);

            } else if (action.equals(MusicService.BROADCAST_ACTION_CURRENT_TIME)) {
                int curTime = intent.getIntExtra(MusicService.PLY_CURRENT_TIME, -1);

            }
        }
    }

    private void setMusicInfo(MusicModel model){
        if (model.isPlaying) {
            ibPlay.setBackgroundResource(R.drawable.pic_music_play);
        } else {
            ibPlay.setBackgroundResource(R.drawable.pic_music_play_pause);
        }
    }
}

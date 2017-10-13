package com.noahark.noaharkplayer.ui.fragment;


import android.view.View;
import android.widget.Toast;

import com.noahark.noaharkplayer.R;
import com.noahark.noaharkplayer.base.ui.BaseFragment;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;

@EFragment(R.layout.fragment_playing)
public class PlayingFragment extends BaseFragment {

    @Override
    public void initView() {

    }


    private boolean isPlaying = false;

    @Click({R.id.ib_favorite,R.id.ib_repeat,R.id.ib_shuffle})
    @Override
    public void initClick(View view) {
        Toast.makeText(getContext(), "play:" + isPlaying, Toast.LENGTH_SHORT).show();

        switch (view.getId()) {
            case R.id.ib_repeat:
                if (isPlaying) {
                    view.setBackgroundResource(R.drawable.ic_music_repeat_normal);
                    isPlaying = false;
                } else {
                    view.setBackgroundResource(R.drawable.ic_music_repeat_pressed);
                    isPlaying = true;
                }
                break;
            case R.id.ib_favorite:
                if (isPlaying) {
                    view.setBackgroundResource(R.drawable.ic_music_favorite_normal);
                    isPlaying = false;
                } else {
                    view.setBackgroundResource(R.drawable.ic_music_favorite_pressed);
                    isPlaying = true;
                }
                break;
            case R.id.ib_shuffle:
                if (isPlaying) {
                    view.setBackgroundResource(R.drawable.ic_music_shuffle_normal);
                    isPlaying = false;
                } else {
                    view.setBackgroundResource(R.drawable.ic_music_shuffle_pressed);
                    isPlaying = true;
                }
                break;
        }
    }

}

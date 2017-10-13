package com.noahark.noaharkplayer.ui.fragment;


import android.view.View;
import android.widget.Toast;

import com.noahark.noaharkplayer.R;
import com.noahark.noaharkplayer.base.ui.BaseFragment;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;

import java.io.InterruptedIOException;

@EFragment(R.layout.fragment_playing)
public class PlayingFragment extends BaseFragment {

    @Override
    public void initView() {

    }


    private boolean isfavorite = false;
    private boolean isSuffle =false;
    private int i=0;
    @Click({R.id.ib_favorite,R.id.ib_repeat,R.id.ib_shuffle})
    @Override
    public void initClick(View view) {
        //Toast.makeText(getContext(), "play:" + isPlaying, Toast.LENGTH_SHORT).show();

        switch (view.getId()) {
            case R.id.ib_repeat:
                    if (i == 0) {
                        view.setBackgroundResource(R.drawable.ic_music_repeat_normal);
                        i++;
                    }else
                    if (i == 1) {
                        view.setBackgroundResource(R.drawable.ic_music_repeat_pressed);
                        i++;
                    }else
                    if (i == 2) {
                        view.setBackgroundResource(R.drawable.ic_music_repeat_one_pressed);
                        i = 0;
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

}

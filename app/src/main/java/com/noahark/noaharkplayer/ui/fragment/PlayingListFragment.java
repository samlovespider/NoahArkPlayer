package com.noahark.noaharkplayer.ui.fragment;


import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.widget.Adapter;
import android.widget.ListView;

import com.noahark.noaharkplayer.R;
import com.noahark.noaharkplayer.adapter.MusicMenuListAdapter;
import com.noahark.noaharkplayer.base.ui.BaseFragment;
import com.noahark.noaharkplayer.model.MusicMenuModel;
import com.noahark.noaharkplayer.model.MusicModel;
import com.noahark.noaharkplayer.ui.activity.MainActivity;
import com.noahark.noaharkplayer.ui.activity.PlayingActivity;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

@EFragment(R.layout.fragment_play_list)
public class PlayingListFragment extends BaseFragment {

    @ViewById(R.id.lvMenuMusics)
    ListView lvMenuMusics;

    private MusicMenuListAdapter mMusicMenuListAdapter;
    private List<MusicMenuModel> mMusicList;

    @Override
    public void initView() {
        mMusicList = new ArrayList<>();
        makeLists();
        mMusicMenuListAdapter = new MusicMenuListAdapter(getContext(), mMusicList);
        lvMenuMusics.setAdapter(mMusicMenuListAdapter);
    }


    private void makeLists() {
        for (int i = 0; i < 20; i++) {
            MusicMenuModel model = new MusicMenuModel("Name" + i, "Artist" + i, "Duration" + i);
            mMusicList.add(model);
        }
        return;
    }


}
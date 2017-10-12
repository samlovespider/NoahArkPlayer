package com.noahark.noaharkplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.noahark.noaharkplayer.R;
import com.noahark.noaharkplayer.model.MusicMenuModel;

import java.util.List;

/**
 * Created by Frank Huo on 17/10/10.
 */

public class MusicMenuListAdapter extends BaseAdapter {
    private List<MusicMenuModel> mMusicList;
    private Context mContext;

    public MusicMenuListAdapter(Context sContext, List<MusicMenuModel> sMusicList) {
        mMusicList = sMusicList;
        mContext = sContext;
    }


    @Override
    public int getCount() {
        return mMusicList.size();
    }

    @Override
    public Object getItem(int position) {
        return mMusicList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        MusicMenuListAdapter.MusicItemHolder holder;

        if (view == null) {
            holder = new MusicMenuListAdapter.MusicItemHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.fragment_play_list_item, null);
            holder.tv_music_name = view.findViewById(R.id.tv_music_name);
            holder.tv_Artist = view.findViewById(R.id.tv_Artist);
            holder.tv_Duration = view.findViewById(R.id.tv_Duration);
            view.setTag(holder);
        } else {
            holder = (MusicMenuListAdapter.MusicItemHolder) view.getTag();
        }

        MusicMenuModel model = mMusicList.get(position);

        holder.tv_music_name.setText(model.mName);
        holder.tv_Artist.setText(model.mArtist);
        holder.tv_Duration.setText(model.mDuration);

        return view;
    }

    private class MusicItemHolder {
        TextView tv_music_name;
        TextView tv_Artist;
        TextView tv_Duration;
    }
}

package com.noahark.noaharkplayer.adapter;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.noahark.noaharkplayer.R;
import com.noahark.noaharkplayer.model.MusicModel;
import com.noahark.noaharkplayer.ui.activity.PlayingActivity;

import java.util.List;

/**
 * Created by caizhenliang on 2017/10/8.
 */

public class MusicListAdapter extends BaseAdapter {

    private List<MusicModel> mMusicList;
    private Context mContext;

    public MusicListAdapter(Context sContext, List<MusicModel> sMusicList) {
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

        MusicItemHolder holder;

        if (view == null) {
            holder = new MusicItemHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.activity_main_list_item, null);
            holder.imgPic = view.findViewById(R.id.imgPic);
            holder.tvSongName = view.findViewById(R.id.tvSongName);
            holder.tvAuthor = view.findViewById(R.id.tvAuthor);
            holder.ivPlaying= view.findViewById(R.id.ivPlaying);
            view.setTag(holder);
        } else {
            holder = (MusicItemHolder) view.getTag();
        }

        MusicModel musicModel = mMusicList.get(position);
        if (musicModel.mBitmap != null) {
            holder.imgPic.setImageBitmap(musicModel.mBitmap);
        } else {
            holder.imgPic.setImageResource(R.drawable.ic_empty_music_album);
        }
        holder.tvSongName.setText(musicModel.mName);
        holder.tvAuthor.setText(musicModel.mArtist);

        if (musicModel.isPlaying) {
            holder.ivPlaying.setVisibility(View.VISIBLE);
        }else{
            holder.ivPlaying.setVisibility(View.INVISIBLE);
        }

        return view;
    }

    private class MusicItemHolder {
        ImageView imgPic;
        TextView tvSongName;
        TextView tvAuthor;
        ImageView ivPlaying;
    }
}

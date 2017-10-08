package com.noahark.noaharkplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.noahark.noaharkplayer.R;
import com.noahark.noaharkplayer.model.MusicModel;

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
            holder.imgPic = (ImageView) view.findViewById(R.id.imgPic);
            holder.tvName = (TextView) view.findViewById(R.id.tvName);
            holder.tvAuther = (TextView) view.findViewById(R.id.tvAuther);
            view.setTag(holder);
        } else {
            holder = (MusicItemHolder) view.getTag();
        }

        MusicModel photoModel = mMusicList.get(position);
//        holder.imgPic.setImageBitmap(photoModel.mBitmap);
        holder.tvName.setText(photoModel.mName);
        holder.tvAuther.setText(photoModel.mArtist);

        return view;
    }

    private class MusicItemHolder {
        ImageView imgPic;
        TextView tvName;
        TextView tvAuther;
    }
}

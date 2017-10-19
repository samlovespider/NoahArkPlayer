package com.noahark.noaharkplayer.adapter;

import android.content.Context;
import android.graphics.Bitmap;
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
 * @author caizhenliang
 */
public class MusicListAdapter extends BaseAdapter {

    private List<MusicModel> mMusicList;
    private Context mContext;

    public MusicListAdapter(Context context, List<MusicModel> sMusicList) {
        mMusicList = sMusicList;
        mContext = context;
    }

    public void refreshAlbum(List<MusicModel> musicList) {
        for (int i = 0; i < mMusicList.size(); i++) {
            if (musicList.get(i).mBitmap != null) {
                mMusicList.get(i).mBitmap = Bitmap.createBitmap(musicList.get(i).mBitmap);
            }
        }
        notifyDataSetChanged();
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

        ViewHolder holder;

        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.fragment_play_list_item, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        MusicModel musicModel = mMusicList.get(position);

        if (musicModel.mBitmap != null) {
            holder.ivAlbum.setImageBitmap(musicModel.mBitmap);
        } else {
            holder.ivAlbum.setImageResource(R.drawable.ic_empty_music_album);
        }
        holder.tvMusicName.setText(musicModel.mName);
        holder.tvAuthor.setText(musicModel.mArtist);

        if (musicModel.isPlaying) {
            holder.ivPlaying.setVisibility(View.VISIBLE);
        } else {
            holder.ivPlaying.setVisibility(View.INVISIBLE);
        }

        return view;
    }

    private class ViewHolder {
        private ImageView ivAlbum;
        private ImageView ivPlaying;
        private TextView tvMusicName;
        private TextView tvAuthor;

        private ViewHolder(View view) {
            ivAlbum = (ImageView) view.findViewById(R.id.ivAlbum);
            ivPlaying = (ImageView) view.findViewById(R.id.ivPlaying);
            tvMusicName = (TextView) view.findViewById(R.id.tvMusicName);
            tvAuthor = (TextView) view.findViewById(R.id.tvAuthor);
        }
    }
}

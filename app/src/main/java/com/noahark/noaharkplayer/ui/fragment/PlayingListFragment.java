package com.noahark.noaharkplayer.ui.fragment;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.noahark.noaharkplayer.R;
import com.noahark.noaharkplayer.adapter.MusicListAdapter;
import com.noahark.noaharkplayer.base.ui.BaseFragment;
import com.noahark.noaharkplayer.base.util.ACache;
import com.noahark.noaharkplayer.model.MusicModel;
import com.noahark.noaharkplayer.service.MusicService;
import com.noahark.noaharkplayer.ui.activity.MainActivity;
import com.noahark.noaharkplayer.util.LoadTaskListener;
import com.noahark.noaharkplayer.util.MusicAlbumLoadTask;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

@EFragment(R.layout.fragment_play_list)
public class PlayingListFragment extends BaseFragment implements LoadTaskListener {

    //
    public static final String MUSICLIST = "musiclist";
    //
    @ViewById(R.id.tvEmpty)
    TextView tvEmpty;
    @ViewById(R.id.lvMusics)
    ListView lvMusics;
    @ViewById(R.id.ivAlbum)
    ImageView ivAlbum;
    @ViewById(R.id.tvMusicName)
    TextView tvMusicName;
    @ViewById(R.id.tvAuthor)
    TextView tvAuthor;
    @ViewById(R.id.ibPlay)
    ImageButton ibPlay;
    //
    private MusicListAdapter mMusicListAdapter;
    private List<MusicModel> mMusicList;
    private MusicModel mMusicModel;
    private HomeReceiver mHomeReceiver;  //customize the receiver of broadcast
    private Intent mServiceIntent;
    private int mLastPosition = -1;

    @Override
    public void initView() {
        // request permissions
        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MainActivity.CODE_FOR_WRITE_PERMISSION);
            return;
        }
        // get music list
        setList();
        //
        if (mMusicList == null || mMusicList.size() < 1) {
            lvMusics.setVisibility(View.INVISIBLE);
            tvEmpty.setVisibility(View.VISIBLE);
        }
        // set boradcast to change UI
        setReceiver();
        // get last song played
        getCache();
        if (mLastPosition != -1 && mLastPosition < mMusicList.size()) {
            setPlayBarInfo(mMusicList.get(mLastPosition));
        }
        // load music album
        if (mMusicList != null && mMusicList.size() > 0) {
            MusicAlbumLoadTask task = new MusicAlbumLoadTask(mContext, mMusicList);
            task.setLoadTaskListener(this);
            task.execute(mMusicList.toArray());
        }
        //
        startServiceNow();
        //
        if (mMusicModel != null && mLastPosition < mMusicList.size()) {
            setPlayingIcon(mLastPosition, mMusicModel.isPlaying);
            setPlayBarInfo(mMusicModel);
        }
    }

    private void setList() {
        mMusicList = getMusics();
        mMusicListAdapter = new MusicListAdapter(mContext, mMusicList);
        lvMusics.setAdapter(mMusicListAdapter);
    }

    private void setReceiver() {
        mHomeReceiver = new HomeReceiver();
        // Creater IntentFilter
        IntentFilter filter = new IntentFilter();
        // set the listener's of BroadcastReceiver
        filter.addAction(MusicService.BROADCAST_ACTION_NEXT_SONGS);
        filter.addAction(MusicService.BROADCAST_ACTION_NOW_PLAYING);
        // Register BroadcastReceiver
        mContext.registerReceiver(mHomeReceiver, filter);
    }

    private void getCache() {
        if (mCache.getAsObject(MusicService.CACHE_MODEL) != null) {
            mMusicModel = (MusicModel) ACache.get(getActivity().getBaseContext()).getAsObject(MusicService.CACHE_MODEL);
        }
        if (mCache.getAsObject(MusicService.CACHE_POSITION) != null) {
            mLastPosition = (int) ACache.get(getActivity().getBaseContext()).getAsObject(MusicService.CACHE_POSITION);
        }
    }

    @Click({R.id.ibPreviouse, R.id.ibNext, R.id.ibPlay, R.id.relPlayBar})
    @Override
    public void initClick(View view) {
        if (mMusicList == null || mMusicModel == null) {
            return;
        }
        switch (view.getId()) {
            case R.id.ibPreviouse:
                previous();
                break;
            case R.id.ibNext:
                next();
                break;
            case R.id.ibPlay:
                if (mMusicModel.isPlaying) {
                    pause();
                } else {
                    play(mLastPosition);
                }
                break;
            case R.id.relPlayBar:
//                if (mLastPosition != -1) {
//                    MusicModel model = mMusicList.get(mLastPosition);
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable(PlayingActivity_.MUSIC_MODEL_EXTRA, model);
//                    gotoActivity(PlayingActivity_.class, bundle);
//                } else {
//                    gotoActivity(PlayingActivity_.class);
//                }
                break;
        }
    }

    @ItemClick(R.id.lvMusics)
    @Override
    public void initItemClick(int position) {
        if (mLastPosition == position) {
            mLastPosition = -1;
            setPlayingIcon(position, false);
            setPlayBarInfo(mMusicList.get(position));
            pause();
        } else {
            if (mLastPosition != -1) {
                mMusicList.get(mLastPosition).isPlaying = false;
            }
            mLastPosition = position;
            setPlayingIcon(position, true);
            play(mLastPosition);
        }
    }

    /**
     * get musics' information from MediaStore.Audio.AudioColumns
     *
     * @return List<MusicModel>
     */
    private List<MusicModel> getMusics() {

        //- get cursor -------------
        List<MusicModel> imageList = new ArrayList<>();
        String[] projection = {
                MediaStore.Audio.AudioColumns._ID, //
                MediaStore.Audio.AudioColumns.DATA, //
                MediaStore.Audio.AudioColumns.TITLE, //
                MediaStore.Audio.AudioColumns.ALBUM, //
                MediaStore.Audio.AudioColumns.ALBUM_ID,//
                MediaStore.Audio.AudioColumns.ARTIST, //
                MediaStore.Audio.AudioColumns.DURATION, //
                MediaStore.MediaColumns.SIZE //
        };
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        Cursor cursor = mContext.getContentResolver().query(uri, projection, null, null, MediaStore.Audio.Media.DATE_ADDED + " desc");
        if (cursor == null) {
            return null;
        }

        //- get list ---------------------------
        cursor.moveToNext();
        while (!cursor.isAfterLast()) {
            //
            String id = cursor.getString(0) == null ? "" : cursor.getString(0);
            String data = cursor.getString(1) == null ? "" : cursor.getString(1);
            String name = cursor.getString(2) == null ? "" : cursor.getString(2);
            String album = cursor.getString(3) == null ? "" : cursor.getString(3);
            String albumid = cursor.getString(4) == null ? "" : cursor.getString(4);
            String artist = cursor.getString(5) == null ? "" : cursor.getString(5);
            String duration = cursor.getString(6) == null ? "" : cursor.getString(6);
            String size = cursor.getString(7) == null ? "" : cursor.getString(7);
            //
            MusicModel tmp = new MusicModel(id, data, name, album, albumid, artist, duration, size);
            imageList.add(tmp);
            cursor.moveToNext();
        }
        cursor.close();
        //
        setCache(imageList);
        return imageList;
    }

    private void setCache(List<MusicModel> imageList) {
        mCache.put(MUSICLIST, JSONArray.toJSONString(imageList));
    }

    private void play(int position) {
        MusicModel model = mMusicList.get(position);
        setPlayBarInfo(model);
        startIntent(position, MusicService.PLY_PLAY);
    }

    private void pause() {
        startIntent(mLastPosition, MusicService.PLY_PAUSE);
    }

    private void previous() {
        startIntent(mLastPosition, MusicService.PLY_PRIVIOUS);
    }

    private void next() {
        startIntent(mLastPosition, MusicService.PLY_NEXT);
    }

    private void startIntent(int position, int action) {
        mServiceIntent = new Intent(mContext, MusicService.class);
        mServiceIntent.setAction(MusicService.SERVICE_ACTION);
        mServiceIntent.putExtra(MusicService.MUSIC_POSITION, position);
        mServiceIntent.putExtra(MusicService.MUSIC_ACTION, action);
        mContext.startService(mServiceIntent);
    }

    private void startServiceNow() {
        mServiceIntent = new Intent(mContext, MusicService.class);
        mServiceIntent.setAction(MusicService.SERVICE_ACTION);
        mContext.startService(mServiceIntent);
    }

    private Bitmap createAlbumPic(String sAlbumID) {
        if (!sAlbumID.isEmpty()) {
            String albumFile = getAlbumArt(sAlbumID);
            return BitmapFactory.decodeFile(albumFile);
        } else {
            return null;
        }
    }

    private String getAlbumArt(String sAlbumID) {
        String mUriAlbums = "content://media/external/audio/albums";
        String[] projection = new String[]{"album_art"};
        Cursor cur = mContext.getContentResolver().query(
                Uri.parse(mUriAlbums + "/" + sAlbumID),
                projection, null, null, null);
        String album_art = null;
        if (cur != null && cur.getCount() > 0 && cur.getColumnCount() > 0) {
            cur.moveToNext();
            album_art = cur.getString(0);
            cur.close();
        }
        return album_art;
    }

    @Override
    public void loadTask(List<MusicModel> musicModels) {
        mMusicListAdapter.refreshAlbum(mMusicList);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContext.unregisterReceiver(mHomeReceiver);
    }

    private void setPlayBarInfo(MusicModel model) {
        if (model == null) {
            return;
        }

        if (model.mAlbumID != null && createAlbumPic(model.mAlbumID) != null) {
            Bitmap bitmap = createAlbumPic(model.mAlbumID);
            ivAlbum.setImageBitmap(bitmap);
        } else {
            ivAlbum.setImageResource(R.drawable.ic_empty_music_album);
        }

        tvMusicName.setText(model.mName);
        tvAuthor.setText(model.mArtist);

        if (model.isPlaying) {
            ibPlay.setBackgroundResource(R.drawable.ic_activity_main_bar_pause_normal);
        } else {
            ibPlay.setBackgroundResource(R.drawable.ic_activity_main_bar_play_normal);
        }
    }

    private void setPlayingIcon(int position, boolean isPlaying) {
        if (position != -1) {
            mMusicListAdapter.setPlayingIcon(position, isPlaying);
        }
    }

    private class HomeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (action.equals(MusicService.BROADCAST_ACTION_NEXT_SONGS)) {
                mMusicModel = (MusicModel) intent.getExtras().getSerializable(MusicService.MUSIC_MODEL);
                int position = intent.getExtras().getInt(MusicService.MUSIC_POSITION, mLastPosition);
                //
                setPlayBarInfo(mMusicModel);
                //Refresh the bar information
                setPlayingIcon(mLastPosition, false);
                setPlayingIcon(position, mMusicModel.isPlaying);
                //Refresh the playing Icon
                mLastPosition = position;

            } else if (action.equals(MusicService.BROADCAST_ACTION_NOW_PLAYING)) {
//                mMusicModel = (MusicModel) intent.getExtras().getSerializable(MusicService.MUSIC_MODEL);
//                mLastPosition = intent.getExtras().getInt(MusicService.MUSIC_POSITION, mLastPosition);
            }
        }
    }
}
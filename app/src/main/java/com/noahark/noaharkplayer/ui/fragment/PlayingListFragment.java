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
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.caizhenliang.mylibrary.util.base.ACache;
import com.noahark.noaharkplayer.R;
import com.noahark.noaharkplayer.adapter.MusicListAdapter;
import com.noahark.noaharkplayer.base.ui.BaseFragment;
import com.noahark.noaharkplayer.model.MusicModel;
import com.noahark.noaharkplayer.service.MusicService;
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
    private static final int CODE_FOR_WRITE_PERMISSION = 1;
    //
    @ViewById(R.id.lvMusics)
    ListView lvMusics;
    // playbar
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
    private HomeReceiver mHomeReceiver;  //自定义的广播接收器
    private Intent mServiceIntent;
    private int mLastPosition = -1;

    @Override
    public void initView() {
        // request permissions
        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    CODE_FOR_WRITE_PERMISSION);
            return;
        }
        // get music list
        setList();
        // set boradcast to change UI
        setReceiver();
        // get last song played
        getCache();
        if (mLastPosition != -1) {
            setPlayBarInfo(mMusicList.get(mLastPosition));
        }
        // load music album
        if (mMusicList != null) {
            MusicAlbumLoadTask task = new MusicAlbumLoadTask(mContext, mMusicList);
            task.setLoadTaskListener(this);
            task.execute(mMusicList.toArray());
        }
    }

    @Click({R.id.ibPreviouse, R.id.ibNext, R.id.ibPlay, R.id.relPlayBar})
    @Override
    public void initClick(View view) {
        switch (view.getId()) {
            case R.id.ibPreviouse:
                previous();
                break;
            case R.id.ibNext:
                next();
                break;
            case R.id.ibPlay:
                if (mMusicList.get(mLastPosition).isPlaying) {
                    setPlayingIcon(mLastPosition, false);
                    view.setBackgroundResource(R.drawable.ic_activity_main_bar_play_normal);
                    pause();
                } else {
                    setPlayingIcon(mLastPosition, true);
                    view.setBackgroundResource(R.drawable.ic_activity_main_bar_pause_normal);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CODE_FOR_WRITE_PERMISSION) {
            if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //
                initView();
            } else {
                getActivity().finish();
            }
        }
    }

    private void setReceiver() {
        mHomeReceiver = new HomeReceiver();
        // 创建IntentFilter
        IntentFilter filter = new IntentFilter();
        // 指定BroadcastReceiver监听的Action
        filter.addAction(MusicService.BROADCAST_ACTION_NEXT_SONGS);
        //
//        filter.addAction(MusicService.ACTION_UPDATE_ACTION);
//        filter.addAction(MusicService.ACTION_MUSIC_CURRENT);
//        filter.addAction(MusicService.ACTION_MUSIC_DURATION);
//        filter.addAction(MusicService.ACTION_REPEAT_ACTION);
//        filter.addAction(MusicService.ACTION_SHUFFLE_ACTION);
        // 注册BroadcastReceiver
        mContext.registerReceiver(mHomeReceiver, filter);
    }

    private void setList() {
        mMusicList = getMusics();
        mMusicListAdapter = new MusicListAdapter(mContext, mMusicList);
        lvMusics.setAdapter(mMusicListAdapter);
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
                MediaStore.Audio.AudioColumns.DISPLAY_NAME, //
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
            duration = formatTime(Long.parseLong(duration));
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

    private String formatTime(long time) {
        String min = time / (1000 * 60) + "";
        String sec = time % (1000 * 60) + "";
        if (min.length() < 2) {
            min = "0" + time / (1000 * 60) + "";
        } else {
            min = time / (1000 * 60) + "";
        }
        if (sec.length() == 4) {
            sec = "0" + (time % (1000 * 60)) + "";
        } else if (sec.length() == 3) {
            sec = "00" + (time % (1000 * 60)) + "";
        } else if (sec.length() == 2) {
            sec = "000" + (time % (1000 * 60)) + "";
        } else if (sec.length() == 1) {
            sec = "0000" + (time % (1000 * 60)) + "";
        }
        return min + ":" + sec.trim().substring(0, 2);
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

    private void setPlayBarInfo(MusicModel model) {

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

    private void startIntent(int position, int action) {
        mServiceIntent = new Intent(mContext, MusicService.class);
        mServiceIntent.setAction(MusicService.SERVICE_ACTION);
        mServiceIntent.putExtra(MusicService.MUSIC_POSITION, position);
        mServiceIntent.putExtra(MusicService.MUSIC_ACTION, action);
        mContext.startService(mServiceIntent);
    }

    private void getCache() {
//        ACache.get(getActivity().getBaseContext()).getAsObject(MusicService.CACHE_MODEL);
        if (ACache.get(getActivity().getBaseContext()).getAsObject(MusicService.CACHE_POSITION) != null) {
            mLastPosition = (int) ACache.get(getActivity().getBaseContext()).getAsObject(MusicService.CACHE_POSITION);
        }
    }

//    private void setBroadcast(int status) {
//        Intent intent = new Intent(MusicService.BROADCAST_ACTION_CHANGE_STATUS);
//        intent.putExtra(MusicService.STATUS, status);
//        mContext.sendBroadcast(intent);
//    }
//
//    private void resume() {
//        startIntent(-1, MusicService.PLY_CONTINUE);
//    }
//
//    private void repeat_one() {
//        setBroadcast(MusicService.STATUS_SINGLE);
//    }
//
//    private void repeat_all() {
//        setBroadcast(MusicService.STATUS_LOOP_ALL);
//    }
//
//    private void repeat_none() {
//        setBroadcast(MusicService.STATUS_BY_ORDER);
//    }
//
//    private void shuffleMusic() {
//        setBroadcast(MusicService.STATUS_BY_RANDOM);
//    }
//
//    private void stopService() {
//        mContext.stopService(mServiceIntent);
//    }

    private void setPlayingIcon(int oldPosition, int newPosition) {
        if (oldPosition != -1) {
            mMusicList.get(oldPosition).isPlaying = false;
        }
        if (newPosition != -1) {
            mMusicList.get(newPosition).isPlaying = true;
        }
        mMusicListAdapter.notifyDataSetChanged();
    }

    private void setPlayingIcon(int position, boolean isPlaying) {
        if (position != -1) {
            mMusicList.get(position).isPlaying = isPlaying;
            mMusicListAdapter.notifyDataSetChanged();
        }
    }

    /**
     * @param sAlbumID
     * @return
     */
    private Bitmap createAlbumPic(String sAlbumID) {
        if (!sAlbumID.isEmpty()) {
            String albumFile = getAlbumArt(sAlbumID);
            return BitmapFactory.decodeFile(albumFile);
        } else {
            return null;
        }
    }

    /**
     * @param sAlbumID
     * @return
     */
    private String getAlbumArt(String sAlbumID) {
        String mUriAlbums = "content://media/external/audio/albums";
        String[] projection = new String[]{"album_art"};
        Cursor cur = mContext.getContentResolver().query(
                Uri.parse(mUriAlbums + "/" + sAlbumID),
                projection, null, null, null);
        String album_art = null;
        if (cur.getCount() > 0 && cur.getColumnCount() > 0) {
            cur.moveToNext();
            album_art = cur.getString(0);
        }
        cur.close();
        cur = null;
        return album_art;
    }

    @Override
    public void loadTask(List<MusicModel> musicModels) {
        mMusicListAdapter.refreshAlbum(mMusicList);
    }

    private class HomeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (action.equals(MusicService.BROADCAST_ACTION_NEXT_SONGS)) {
                MusicModel model = (MusicModel) intent.getExtras().getSerializable(MusicService.MUSIC_MODEL);
                int position = intent.getExtras().getInt(MusicService.MUSIC_POSITION, mLastPosition);

                model.isPlaying = true;
                setPlayBarInfo(model);
                setPlayingIcon(mLastPosition, position);
                mLastPosition = position;
            }
        }
    }

}
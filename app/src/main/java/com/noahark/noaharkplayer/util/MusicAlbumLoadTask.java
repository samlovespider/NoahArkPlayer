package com.noahark.noaharkplayer.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;

import com.noahark.noaharkplayer.model.MusicModel;

import java.util.List;

/**
 * Created by caizhenliang on 2017/8/31.
 */
public class MusicAlbumLoadTask extends AsyncTask<Object, Integer, Integer> {

    //
    private List<MusicModel> mModelList;
    private LoadTaskListener mLoadTaskListener;
    private Context mContext;

    public MusicAlbumLoadTask(Context sContext, List<MusicModel> sModelList) {
        mModelList = sModelList;
        mContext = sContext;
    }

    public void setLoadTaskListener(LoadTaskListener sLoadTaskListener) {
        this.mLoadTaskListener = sLoadTaskListener;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onPostExecute(Integer result) {
        if (mLoadTaskListener != null) {
            mLoadTaskListener.loadTask(mModelList);
        }
    }

    @Override
    protected Integer doInBackground(Object... params) {
        for (int i = 0; i < params.length; i++) {
            MusicModel musicModel = (MusicModel) params[i];

            if (musicModel.mAlbumID != null) {
                if (createAlbumPic(musicModel.mAlbumID) != null) {
                    Bitmap bitmap = createAlbumPic(musicModel.mAlbumID);
                    mModelList.get(i).setmBitmap(bitmap);
                }
            }
        }
        return 1;
    }

    /**
     * @param sAlbumID
     * @return
     */
    private Bitmap createAlbumPic(String sAlbumID) {
        if (!sAlbumID.isEmpty()) {
            String albumFile = getAlbumArt(sAlbumID);
            if (albumFile == null || albumFile.isEmpty()) {
                return null;
            } else {
                return BitmapFactory.decodeFile(albumFile);
            }
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

}

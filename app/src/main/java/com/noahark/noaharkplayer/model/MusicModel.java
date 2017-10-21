package com.noahark.noaharkplayer.model;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by caizhenliang on 2017/10/8.
 */

public class MusicModel implements Serializable {

    public String mID;
    public String mData;
    public String mName;
    public String mAlbum;
    public String mAlbumID;
    public String mArtist;
    public String mDuration;
    public String mSize;
    public Bitmap mBitmap;
    public boolean isPlaying;
    public boolean isFavorite;

    public MusicModel() {
    }

    public MusicModel(String ID, String data, String name, String album, String albumID, String artist, String duration, String size) {
        this.mID = ID;
        this.mData = data;
        this.mName = name;
        this.mAlbum = album;
        this.mAlbumID = albumID;
        this.mArtist = artist;
        this.mDuration = duration;
        this.mSize = size;
    }

    @Override
    public String toString() {
        return "MusicModel{" +
                "mID='" + mID + '\'' +
                ", mData='" + mData + '\'' +
                ", mName='" + mName + '\'' +
                ", mAlbum='" + mAlbum + '\'' +
                ", mAlbumID='" + mAlbumID + '\'' +
                ", mArtist='" + mArtist + '\'' +
                ", mDuration='" + mDuration + '\'' +
                ", mSize='" + mSize + '\'' +
                '}';
    }

    public void setmBitmap(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
    }
}

package com.noahark.noaharkplayer.model;

import java.io.Serializable;

/**
 * Created by caizhenliang on 2017/10/8.
 */

public class MusicModel implements Serializable {

    public String mID;
    public String mData;
    public String mName;
    public String mAlbum;
    public String mArtist;
    public String mDuration;
    public String mSize;

    public MusicModel(String mID, String mData, String mName, String mAlbum, String mArtist, String mDuration, String mSize) {
        this.mID = mID;
        this.mData = mData;
        this.mName = mName;
        this.mAlbum = mAlbum;
        this.mArtist = mArtist;
        this.mDuration = mDuration;
        this.mSize = mSize;
    }

    @Override
    public String toString() {
        return "MusicModel{" +
                "mID='" + mID + '\'' +
                ", mData='" + mData + '\'' +
                ", mName='" + mName + '\'' +
                ", mAlbum='" + mAlbum + '\'' +
                ", mArtist='" + mArtist + '\'' +
                ", mDuration='" + mDuration + '\'' +
                ", mSize='" + mSize + '\'' +
                '}';
    }
}

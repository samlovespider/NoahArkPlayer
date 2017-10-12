package com.noahark.noaharkplayer.model;


import java.io.Serializable;

/**
 * Created by Frank Huo on 17/10/10.
 */

public class MusicMenuModel implements Serializable {
    public String mName;
    public String mArtist;
    public String mDuration;

    public MusicMenuModel(String mName, String mArtist, String mDuration) {
        this.mName = mName;
        this.mArtist = mArtist;
        this.mDuration = mDuration;
    }

    @Override
    public String toString() {
        return "MusicMenuModel{" +
                "mName='" + mName + '\'' +
                ", mArtist='" + mArtist + '\'' +
                ", mDuration='" + mDuration + '\'' +
                '}';
    }
}

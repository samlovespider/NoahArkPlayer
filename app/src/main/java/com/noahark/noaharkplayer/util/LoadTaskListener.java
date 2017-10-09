package com.noahark.noaharkplayer.util;

import com.noahark.noaharkplayer.model.MusicModel;

import java.util.List;

/**
 * Created by caizhenliang on 2017/8/31.
 */
public interface LoadTaskListener {

    /**
     * when task finshed, notify all
     *
     * @param musicModels
     */
    void loadTask(List<MusicModel> musicModels);
}

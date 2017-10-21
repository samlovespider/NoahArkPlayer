package com.noahark.noaharkplayer.base.util;

import android.util.Log;

/**
 * Created by caizhenliang on 2017/9/16.
 */

public class SCLogHelper {

    /**
     * @param tag
     * @param o
     */
    public static void w(String tag, Object o) {
        if (o != null) {
            Log.w(tag, o.toString());
        } else {
            Log.w(tag, null + "");
        }
    }


    /**
     * @param tag
     * @param title
     * @param o
     */
    public static void w(String tag, String title, Object o) {
        if (o != null) {
            Log.w(tag, title + ":  " + o.toString());
        } else {
            Log.w(tag, null + "");
        }
    }
}

package com.caizhenliang.mylibrary.ui.activity;

import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;

/**
 * Created by caizhenliang on 2017/7/26.
 */

public interface MyClickImp {

    /**
     * @param menuItem
     */
    void initMenuItem(MenuItem menuItem);

    /**
     * @param view
     */
    void initClick(View view);

    /**
     * @param event
     */
//    void initEvent(Object event);

    /**
     * @param position
     */
    void initItemClick(int position);

    /**
     * @param object
     */
    void initItemClick(Object object);

    /**
     * @param position
     */
    void initItemLongClick(int position);

    /**
     * @param object
     */
    void initItemLongClick(Object object);


    /**
     * @param button
     * @param isChecked
     */
    void initCheckedChange(CompoundButton button, boolean isChecked);
}

package com.caizhenliang.mylibrary.ui.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;

/**
 * Created by caizhenliang on 2017/7/20.
 */
public class MyAlertDialogTool {

    private Context mContext;
    private AlertDialog.Builder mBuilder;

    public MyAlertDialogTool(Context context) {
        mContext = context;
    }

    /**
     * show a basic alertdialog
     *
     * @param message
     */
    public void show(String message) {
        initBuilder("", message);
        AlertDialog dialog = mBuilder.create();
        dialog.show();
        //
        mBuilder = null;
    }

    /**
     * show a basic alertdialog
     *
     * @param message
     */
    public void show(int message) {
        initBuilder(-1, message);
        AlertDialog dialog = mBuilder.create();
        dialog.show();
        //
        mBuilder = null;
    }

    /**
     * show a basic alertdialog
     *
     * @param title
     * @param message
     */
    public void show(String title, String message) {
        initBuilder(title, message);
        AlertDialog dialog = mBuilder.create();
        dialog.show();
        //
        mBuilder = null;
    }

    /**
     * show a basic alertdialog
     *
     * @param title
     * @param message
     */
    public void show(int title, int message) {
        initBuilder(title, message);
        AlertDialog dialog = mBuilder.create();
        dialog.show();
        //
        mBuilder = null;
    }

    /**
     * show a alertdialog with icon
     *
     * @param title
     * @param message
     * @param icon
     */
    public void show(String title, String message, int icon) {
        initBuilder(title, message, icon);
        AlertDialog dialog = mBuilder.create();
        dialog.show();
        //
        mBuilder = null;
    }

    /**
     * show a alertdialog with icon
     *
     * @param title
     * @param message
     * @param icon
     */
    public void show(int title, int message, int icon) {
        initBuilder(title, message, icon);
        AlertDialog dialog = mBuilder.create();
        dialog.show();
        //
        mBuilder = null;
    }

    /**
     * show a alertdialog with icon
     *
     * @param title
     * @param message
     * @param icon
     */
    public void show(String title, String message, Drawable icon) {
        initBuilder(title, message, icon);
        AlertDialog dialog = mBuilder.create();
        dialog.show();
        //
        mBuilder = null;
    }

    /**
     * show a alertdialog with icon
     *
     * @param title
     * @param message
     * @param icon
     */
    public void show(int title, int message, Drawable icon) {
        initBuilder(title, message, icon);
        AlertDialog dialog = mBuilder.create();
        dialog.show();
        //
        mBuilder = null;
    }

    public AlertDialog.Builder getBuilder() {
        return mBuilder;
    }

    //------------------------------------------------------------------------------------------------------

    /**
     * @param title
     * @param message
     */
    private void initBuilder(int title, int message) {
        //
        mBuilder = new AlertDialog.Builder(mContext);
        //
        if (title != -1) {
            mBuilder.setTitle(title);
        }
        //
        mBuilder.setMessage(message);
    }

    /**
     * @param title
     * @param message
     */
    private void initBuilder(String title, String message) {
        //
        mBuilder = new AlertDialog.Builder(mContext);
        //
        mBuilder.setTitle(title);
        //
        mBuilder.setMessage(message);
    }

    /**
     * @param title
     * @param message
     * @param icon
     */
    private void initBuilder(int title, int message, int icon) {
        //
        initBuilder(title, message);
        //
        if (icon != -1) {
            mBuilder.setIcon(icon);
        }
    }

    /**
     * @param title
     * @param message
     * @param icon
     */
    private void initBuilder(String title, String message, Drawable icon) {
        //
        initBuilder(title, message);
        //
        if (icon != null) {
            mBuilder.setIcon(icon);
        }
    }

    /**
     * @param title
     * @param message
     * @param icon
     */
    private void initBuilder(String title, String message, int icon) {
        //
        initBuilder(title, message);
        //
        if (icon != -1) {
            mBuilder.setIcon(icon);
        }
    }

    /**
     * @param title
     * @param message
     * @param icon
     */
    private void initBuilder(int title, int message, Drawable icon) {
        //
        initBuilder(title, message);
        //
        if (icon != null) {
            mBuilder.setIcon(icon);
        }
    }

}
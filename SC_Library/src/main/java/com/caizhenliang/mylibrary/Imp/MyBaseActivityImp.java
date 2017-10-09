package com.caizhenliang.mylibrary.Imp;

import android.os.Bundle;

/**
 * Created by caizhenliang on 2017/7/19.
 */
public interface MyBaseActivityImp {

    /**
     * 跳转Activity；
     *
     * @param paramClass Activity参数
     */
    void gotoActivity(Class<?> paramClass);

    /**
     * 跳转Activity，带有Bundle参数；
     *
     * @param paramClass Activity参数
     * @param bundle     Bundle参数
     */
    void gotoActivity(Class<?> paramClass, Bundle bundle);

    /**
     * 跳转Activity，带有Bundle参数，并且该Activity不会压入栈中，返回后自动关闭；
     *
     * @param paramClass Activity参数
     * @param bundle     Bundle参数
     */
    void gotoActivityNoHistory(Class<?> paramClass, Bundle bundle);

    /**
     * @param paramClass
     * @param bundle
     * @param paramInt
     */
    void gotoActivityForResult(Class<?> paramClass, Bundle bundle, int paramInt);

    /**
     * init View
     */
    void initView();

    /**
     *
     */
    void onEvent(Object o);
}

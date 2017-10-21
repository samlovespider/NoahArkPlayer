package com.noahark.noaharkplayer.base.Imp;

import android.os.Bundle;

/**
 * Created by caizhenliang on 2017/7/19.
 */
public interface MyBaseFragmentImp {

    /**
     * jump into Activity；
     *
     * @param paramClass Activity parameter
     */
    void gotoActivity(Class<?> paramClass);

    /**
     * jump into Activity, with Bundle param；
     *
     * @param paramClass Activity parameter
     * @param bundle     Bundle parameter
     */
    void gotoActivity(Class<?> paramClass, Bundle bundle);

    /**
     * jump into Activity, with Bundle param, and Activity will not into stack, after returning and close automatically
     *
     * @param paramClass Activity parameter
     * @param bundle     Bundle parameter
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
}

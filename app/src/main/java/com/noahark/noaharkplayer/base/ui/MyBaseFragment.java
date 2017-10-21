package com.noahark.noaharkplayer.base.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.noahark.noaharkplayer.base.Imp.MyBaseFragmentImp;
import com.noahark.noaharkplayer.base.Imp.MyClickImp;
import com.noahark.noaharkplayer.base.Imp.MyLogImp;
import com.noahark.noaharkplayer.base.util.ACache;
import com.noahark.noaharkplayer.base.util.SCLogHelper;


/**
 * Created by caizhenliang on 2017/10/9.
 * samcai
 */
abstract public class MyBaseFragment extends Fragment implements MyBaseFragmentImp, MyClickImp, MyLogImp {

    //
    protected String TAG = getClass().getSimpleName();
    //
    protected ACache mCache;
    protected Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
        mContext = getContext();
        // init ACache
        mCache = ACache.get(getActivity().getBaseContext());
    }

    //-- MyBaseActivityImp **--**--**--**--**--**--**--**--**--**--**--**--**--**--**--**

    @Override
    public void gotoActivity(Class<?> paramClass) {
        doGotoActivity(paramClass, null);
    }

    @Override
    public void gotoActivity(Class<?> paramClass, Bundle bundle) {
        doGotoActivity(paramClass, bundle);
    }

    @Override
    public void gotoActivityNoHistory(Class<?> paramClass, Bundle bundle) {
        doGotoActivity(paramClass, bundle, Intent.FLAG_ACTIVITY_NO_HISTORY);
    }

    @Override
    public void gotoActivityForResult(Class<?> paramClass, Bundle bundle, int paramInt) {
        Intent lIntent = new Intent(getActivity().getBaseContext(), paramClass);
        if (bundle != null) {
            lIntent.putExtras(bundle);
            lIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        }
        startActivityForResult(lIntent, paramInt);
    }

    /**
     *  jump into Activity, with Bundle parameter, if the Activity is started up, it will not start new one; 已启动过，则不启动新的；
     *
     * @param paramClass Activity parameter
     * @param bundle     Bundle parameter
     */
    private void doGotoActivity(Class<?> paramClass, Bundle bundle) {
        Intent lIntent = new Intent(getActivity().getBaseContext(), paramClass);
        if (bundle != null) {
            lIntent.putExtras(bundle);
            lIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        }
        startActivity(lIntent);
    }

    /**
     *      * jump into Activity, with Bundle parameter，it could set start mode 可以设置启动模式；
     *
     * @param paramClass Activity parameter
     * @param bundle     Bundle parameter
     * @param intentFlag 　start the mode
     * @see Intent
     */
    private void doGotoActivity(Class<?> paramClass, Bundle bundle, int intentFlag) {
        Intent lIntent = new Intent(getActivity().getBaseContext(), paramClass);
        if (bundle != null) {
            lIntent.putExtras(bundle);
            lIntent.addFlags(intentFlag);
        }
        startActivity(lIntent);
    }

    //-- MyLogImp **--**--**--**--**--**--**--**--**--**--**--**--**--**--**--**

    @Override
    public void logW(Object o) {
        SCLogHelper.w(TAG, o);
    }

    @Override
    public void logW(String title, Object o) {
        SCLogHelper.w(TAG, title, o);
    }
}

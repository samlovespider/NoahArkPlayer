package com.noahark.noaharkplayer.base.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.noahark.noaharkplayer.base.Imp.MyBaseActivityImp;
import com.noahark.noaharkplayer.base.Imp.MyClickImp;
import com.noahark.noaharkplayer.base.Imp.MyLogImp;
import com.noahark.noaharkplayer.base.util.ACache;
import com.noahark.noaharkplayer.base.util.SCLogHelper;


/**
 * @author caizhenliang
 */
abstract public class MyBaseActivity extends AppCompatActivity implements MyBaseActivityImp, MyClickImp, MyLogImp {

    //
    protected String TAG = getClass().getSimpleName();
    //
    protected ActionBar mActionBar;
    protected ACache mCache;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // init ActionBar
        mActionBar = getSupportActionBar();
        // init Cache
        mCache = ACache.get(getBaseContext());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
        Intent lIntent = new Intent(this.getBaseContext(), paramClass);
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
        Intent lIntent = new Intent(this.getBaseContext(), paramClass);
        if (bundle != null) {
            lIntent.putExtras(bundle);
            lIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        }
        startActivity(lIntent);
    }

    /**
     * jump into Activity, with Bundle parameter，it could set start mode 可以设置启动模式；
     *
     * @param paramClass Activity parameter
     * @param bundle     Bundle parameter
     * @param intentFlag 　start the mode
     * @see Intent
     */
    private void doGotoActivity(Class<?> paramClass, Bundle bundle, int intentFlag) {
        Intent lIntent = new Intent(this.getBaseContext(), paramClass);
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

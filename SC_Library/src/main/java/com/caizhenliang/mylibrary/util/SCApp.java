package com.caizhenliang.mylibrary.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

/**
 * Created by caizhenliang on 2017/9/23.
 */

public class SCApp {

    /**
     * @param context
     * @param packageName like "com.tencent.mm"
     * @param appWebSite  like "http://weixin.qq.com/"
     */
    public static void openApp(Context context, String packageName, String appWebSite) {
        try {
            PackageManager packageManager = context.getPackageManager();
            // check whether app is installed
            packageManager.getPackageInfo(packageName, 0);
            Intent intent = packageManager.getLaunchIntentForPackage(packageName);
            context.startActivity(intent);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(appWebSite));
            context.startActivity(viewIntent);
        }
    }
}

package com.bozhon.sdk.next.one;

import android.app.Application;

import com.lib.sdk.next.NextSDKHelper;

/**
 * FileName: NextApplication
 * Author: zhikai.jin
 * Date: 2021/6/3 11:46
 * Description:
 */
public class NextApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        NextSDKHelper.getInstance().init(NextApplication.this);
    }
}

package com.bozhon.sdk.next.one;

import android.app.Application;

import com.github.moduth.blockcanary.BlockCanary;
import com.lib.sdk.next.INextInitCallBack;
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
        BlockCanary.install(this, new AppBlockCanaryContext()).start();
        NextSDKHelper.getInstance().init(NextApplication.this);
    }
}

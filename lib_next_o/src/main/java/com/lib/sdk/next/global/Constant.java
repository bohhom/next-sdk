/*
Copyright 2021 kino jin
zhikai.jin@bozhon.com
This file is part of next-sdk.
next-sdk is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.
next-sdk is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.
You should have received a copy of the GNU Lesser General Public License
along with next-sdk.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.lib.sdk.next.global;

import android.os.Environment;

import java.io.File;

/**
 * Created by maqing on 2017/8/11.
 * Email:2856992713@qq.com
 */
public class Constant {
    /**
     * 应用名
     */
    public static final String APP_NAME = "BoZhongRobot";

    /**
     * SharePreferences文件名
     */
    public static final String SP_NAME = APP_NAME;

    /**
     * 用户信息
     */
    public static final String USER = "user";

    /**
     * SDCard绝对路径
     */
    public static final String SDCARD_ROOT_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    /**
     * SDCard文件夹
     */
    public static final String SDCARD_PATH = SDCARD_ROOT_PATH + File.separator + APP_NAME;

    /**
     * 项目图片保存路径
     */
    public static final String PICTURE_PATH = SDCARD_ROOT_PATH + File.separator + APP_NAME + File.separator + "picture";

    /**
     * APK保存路径
     */
    public static final String APK_SAVE_PATH = SDCARD_ROOT_PATH + File.separator + APP_NAME + ".apk";

    /**
     * 腾讯Bugly AppID
     */
    public static final String TECENT_BUGGLY_APP_ID = "43136785db";

    /**
     * 默认ip
     */
    public static final String IP_DEFAULT = "192.168.168.10";

}

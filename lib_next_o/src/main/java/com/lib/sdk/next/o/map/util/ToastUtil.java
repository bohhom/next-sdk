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

package com.lib.sdk.next.o.map.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by licrynoob on 2016/guide_2/12 <br>
 * Copyright (C) 2016 <br>
 * Email:licrynoob@gmail.com <p>
 * Toast工具类
 * 不连续弹出Toast
 * 需初始化sContext
 */
public class ToastUtil {

    private static Toast sToast = null;

    /**
     * 短时间显示Toast
     *
     * @param context context
     * @param message 信息
     */
    public static void showShort(Context context, CharSequence message) {
        if (sToast == null) {
            sToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        } else {
            sToast.setText(message);
        }
        sToast.show();
    }

    /**
     * 短时间显示Toast
     *
     * @param context context
     * @param message 信息
     */
    public static void showShort(Context context, int message) {
        if (sToast == null) {
            sToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        } else {
            sToast.setText(message);
        }
        sToast.show();
    }

    /**
     * 长时间显示Toast
     *
     * @param context context
     * @param message 信息
     */
    public static void showLong(Context context, CharSequence message) {
        if (sToast == null) {
            sToast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        } else {
            sToast.setText(message);
        }
        sToast.show();
    }

    /**
     * 长时间显示Toast
     *
     * @param context context
     * @param message 信息
     */
    public static void showLong(Context context, int message) {
        if (sToast == null) {
            sToast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        } else {
            sToast.setText(message);
        }
        sToast.show();
    }

    /**
     * 自定义显示Toast时间
     *
     * @param context  context
     * @param message  信息
     * @param duration 时长
     */
    public static void showDuration(Context context, CharSequence message, int duration) {
        if (sToast == null) {
            sToast = Toast.makeText(context, message, duration);
        } else {
            sToast.setText(message);
        }
        sToast.show();
    }

    /**
     * 自定义显示Toast时间
     *
     * @param context  context
     * @param message  信息
     * @param duration 时长
     */
    public static void showDuration(Context context, int message, int duration) {
        if (sToast == null) {
            sToast = Toast.makeText(context, message, duration);
        } else {
            sToast.setText(message);
        }
        sToast.show();
    }




}

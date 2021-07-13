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
package com.lib.sdk.next.util;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;

import androidx.appcompat.app.AlertDialog;

import com.lib.sdk.next.o.R;

/**
 * Created by licrynoob on 2016/guide_2/26 <br>
 * Copyright (C) 2016 <br>
 * Email:licrynoob@gmail.com <p>
 * 弹窗工具类
 */
public class DialogUtil {

    /**
     * 进度弹窗
     */
    private static ProgressDialog sProgressDialog = null;

    /**
     * 显示进度弹窗
     *
     * @param context 上下文
     * @param message message
     */
    public static void showProgress(Context context, String message) {
        if (sProgressDialog == null) {
            sProgressDialog = new ProgressDialog(context);
        }
        if (!TextUtils.isEmpty(message)) {
            sProgressDialog.setMessage(message);
        }
        sProgressDialog.show();
    }

    /**
     * 显示进度弹窗 默认显示 加载中
     *
     * @param context 上下文
     */
    public static void showProgress(Context context) {
        showProgress(context, "加载中");
    }

    /**
     * 显示一个无法被取消的弹窗
     *
     * @param context 上下文
     * @param message 信息
     */
    public static void showUnCancelableProgress(Context context, String message) {
        if (sProgressDialog == null) {
            sProgressDialog = new ProgressDialog(context);
        }
        if (!TextUtils.isEmpty(message)) {
            sProgressDialog.setMessage(message);
        }
        //窗口外无法取消
        sProgressDialog.setCanceledOnTouchOutside(false);
        //返回键无法取消
        sProgressDialog.setCancelable(false);
        sProgressDialog.show();
    }

    /**
     * 隐藏进度弹窗
     */
    public static void hideProgress() {
        if (sProgressDialog != null) {
            sProgressDialog.dismiss();
            sProgressDialog = null;
        }
    }

    /**
     * 获取Builder
     *
     * @param context 上下文
     * @return Builder
     */
    public static AlertDialog.Builder getBuilder(Context context) {
        return new AlertDialog.Builder(context);
    }

    /**
     * 显示文本弹窗
     *
     * @param context 上下文
     * @param message 信息
     */
    public static void showMDialog(Context context, String message) {
        getBuilder(context)
                .setMessage(message)
                .show();
    }

    /**
     * 显示确认弹窗
     *
     * @param context   context
     * @param message   信息
     * @param pListener 确认
     */
    public static void showPDialog(Context context, String message, DialogInterface.OnClickListener pListener) {
        getBuilder(context)
                .setMessage(message)
                .setPositiveButton(context.getString(R.string.confirm), pListener)
                .show();
    }

    /**
     * 显示确认弹窗
     *
     * @param context   context
     * @param message   信息
     * @param pListener 确认
     */
    public static void showUnCancelableDialog(Context context, String message, DialogInterface.OnClickListener pListener) {
        getBuilder(context)
                .setMessage(message)
                .setPositiveButton(context.getString(R.string.confirm), pListener)
                .setCancelable(false)
                .show();
    }


    /**
     * 显示确认取消弹窗
     *
     * @param context   context
     * @param message   信息
     * @param pListener 确认
     */
    public static void showUnCancelablePNDialog(Context context, String message, DialogInterface.OnClickListener pListener) {
        getBuilder(context).setMessage(message).setPositiveButton(context.getString(R.string.confirm), pListener)
                .setCancelable(false)
                .show();
    }

    public static void showPNDialog(
            Context context,
            String message,
            DialogInterface.OnClickListener pListener,
            DialogInterface.OnClickListener nListener) {
        getBuilder(context)
                .setMessage(message)
                .setPositiveButton(context.getString(R.string.confirm), pListener)
                .setNegativeButton(context.getString(R.string.cancel), nListener)
                .show();
    }


    /**
     * 显示弹窗
     *
     * @param context   context
     * @param title     标题
     * @param message   信息
     * @param positive  确认
     * @param pListener 确认接口
     * @param negative  取消
     * @param nListener 取消接口
     */
    public static void showDialog(
            Context context,
            String title,
            String message,
            String positive, DialogInterface.OnClickListener pListener,
            String negative, DialogInterface.OnClickListener nListener) {

        AlertDialog.Builder builder = getBuilder(context);
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }
        if (!TextUtils.isEmpty(message)) {
            builder.setMessage(message);
        }
        if (!TextUtils.isEmpty(positive) && pListener != null) {
            builder.setPositiveButton(positive, pListener);
        }
        if (!TextUtils.isEmpty(negative) && nListener != null) {
            builder.setNegativeButton(negative, nListener);
        }
        builder.show();
    }


    /**
     * 显示弹窗
     *
     * @param context   context
     * @param title     标题
     * @param message   信息
     * @param positive  确认
     * @param pListener 确认接口
     * @param negative  取消
     * @param nListener 取消接口
     */
    public static void showUnCancelableDialog(
            Context context,
            String title,
            String message,
            String positive, DialogInterface.OnClickListener pListener,
            String negative, DialogInterface.OnClickListener nListener) {

        AlertDialog.Builder builder = getBuilder(context);
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }
        if (!TextUtils.isEmpty(message)) {
            builder.setMessage(message);
        }
        if (!TextUtils.isEmpty(positive) && pListener != null) {
            builder.setPositiveButton(positive, pListener).setCancelable(false);
        }
        if (!TextUtils.isEmpty(negative) && nListener != null) {
            builder.setNegativeButton(negative, nListener).setCancelable(false);
        }
        builder.show();
    }


}

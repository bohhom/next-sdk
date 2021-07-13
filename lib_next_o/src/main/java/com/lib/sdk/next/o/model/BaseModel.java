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
package com.lib.sdk.next.o.model;

import android.os.Handler;
import android.os.Looper;

import com.lib.sdk.next.o.config.UnitSystemConfig;
import com.lib.sdk.next.o.http.GsonHelper;
import com.lib.sdk.next.o.http.NovateHelper;
import com.tamic.novate.Throwable;
import com.tamic.novate.callback.RxStringCallback;
import com.tamic.novate.download.DownLoadCallBack;
import com.tamic.novate.exception.NovateException;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by aruba on 2018/2/6.
 */

public abstract class BaseModel {
    //数据请求参数
    protected HashMap<String, Object> mParams = new HashMap<>();

    protected JSONObject jsonObject;

    public BaseModel() {
        this.mParams = mParams;
    }

    /**
     * 设置数据请求参数
     *
     * @param mParams 参数数组
     */
    public BaseModel params(HashMap<String, Object> mParams) {
        this.mParams = mParams;
        return this;
    }

    public BaseModel params(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
        return this;
    }

    public void execute(String url, Object rxResultCallback) {

    }

    public void execute(String url, String fileName, Object rxResultCallback) {

    }

    // 执行Get网络请求，此类看需求由自己选择写与不写
    public void requestGetAPI(String url, RxStringCallback rxResultCallback) {
        NovateHelper.getInstance().getNovate().rxGet(url, url, mParams, rxResultCallback);
    }

    // 执行Post网络请求，此类看需求由自己选择写与不写
    public void requestPostAPI(String url, RxStringCallback rxResultCallback) {
        NovateHelper.getInstance().getNovate().rxPost(url, url, mParams, rxResultCallback);
    }

    public void requestJsonAPI(String url, RxStringCallback rxResultCallback) {
        NovateHelper.getInstance().getNovate().rxJson(url, url, GsonHelper.getInstance().getGson().toJson(mParams), rxResultCallback);
    }

    public void requestJsonAPIJ(String url, RxStringCallback rxResultCallback) {
        NovateHelper.getInstance().getNovate().rxJson(url, url, jsonObject.toString(), rxResultCallback);
    }

    protected Object tag;

    public void requestFileAPI(String url, final RxStringCallback rxResultCallback) {
        tag = url;
        MultipartBody.Builder builder = new MultipartBody.Builder();
        Set<Map.Entry<String, Object>> entrySet = mParams.entrySet();

        for (Map.Entry<String, Object> entry : entrySet) {
            if (entry.getValue() instanceof File) {
                File file = (File) entry.getValue();
                builder.addFormDataPart(entry.getKey(), file.getName(),
                        RequestBody.create(MediaType.parse("multipart/form-data"), file));//添加文件
            } else {
                if (entry.getValue() != null)
                    builder.addFormDataPart(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }

        //构建body
        RequestBody requestBody = builder
                .build();

        final okhttp3.Request request = new okhttp3.Request.Builder()
                .url(UnitSystemConfig.base_url + url)
                .addHeader("Content-Type", "application/json")
                .addHeader("Token", UnitSystemConfig.token)
                .post(requestBody)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        rxResultCallback.onError(tag, NovateException.handleException(e));
                    }
                });
            }

            @Override
            public void onResponse(final Call call, final Response response) throws IOException {
                if (call.isCanceled()) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            rxResultCallback.onCancel(tag, new Throwable(null, -200, "已取消"));
                        }
                    });
                }

                if (rxResultCallback.isReponseOk(tag, response.body())) {

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                rxResultCallback.onNext(tag, rxResultCallback.onHandleResponse(response.body()));
                            } catch (Exception e) {
                                e.printStackTrace();
                                rxResultCallback.onError(tag, NovateException.handleException(e));
                            }
                        }
                    });

                }
            }
        });

//        NovateHelper.getInstance().getNovate().rxBody(url, url, requestBody, rxResultCallback);
    }


    public void downloadFileAPI(String url, String fileName, DownLoadCallBack downLoadCallBack) {
        String name = (String) mParams.get(fileName);
        NovateHelper.getInstance().getNovate().download(url, name, downLoadCallBack);
    }

}

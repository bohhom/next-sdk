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

package com.lib.sdk.next.socket;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.Nullable;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;


/**
 * WebSocket请求Service
 */
public class WebSocketService extends Service {
    /**
     * Socket请求的Url
     */
    protected String mUrl;

    private OkHttpClient mOkHttpClient;
    private Request mRequest;

    protected WebSocket mWebSocket;

    protected CustomSocketListener mCustomSocketListener;

    public static final String EXTRA_URL = "url";
    public static final String EXTRA_ACTION = "action";

    public static final String ACTION_START = "start";
    public static final String ACTION_STOP = "stop";

    private String mAction = "";
    protected final String TAG = "WebSocketService";

    private PowerManager.WakeLock mWakeLock;

    private WebSocketListener mWebSocketListener = new WebSocketListener() {
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            super.onOpen(webSocket, response);
            mWebSocket = webSocket;
            Log.e(TAG, "onOpen：" + response);
            if (mCustomSocketListener != null) {
                mCustomSocketListener.onOpen(webSocket, response);
            }
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            super.onMessage(webSocket, text);
//            LogUtil.e(TAG, "onMessage" + text);
            if (mCustomSocketListener != null) {
                mCustomSocketListener.onMessage(webSocket, text);
            }
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            super.onClosed(webSocket, code, reason);
            Log.e(TAG, "onClosed");
            mWebSocket = null;
            if (mCustomSocketListener != null) {
                mCustomSocketListener.onClosed(webSocket, code, reason);
            }
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            super.onFailure(webSocket, t, response);
            Log.e(TAG, "onFailure");
            mWebSocket = null;
            if (mCustomSocketListener != null) {
                mCustomSocketListener.onFailure(webSocket, t, response);
            }

            if (!ACTION_STOP.equals(mAction)) {
                //建立连接
                if (mOkHttpClient != null && mRequest != null) {
                    Log.e(TAG, "重连..........");
                    mOkHttpClient.newWebSocket(mRequest, mWebSocketListener);
                }
            }

        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "systemService");
        mWakeLock.acquire();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            mAction = intent.getStringExtra(EXTRA_ACTION);
            if (mWebSocket == null && ACTION_START.equals(mAction)) {
                mUrl = intent.getStringExtra(EXTRA_URL);
                startSocket();
            } else {
                if (mAction.equals(ACTION_STOP)) {
                    if (mWebSocket != null) {
                        mOkHttpClient.dispatcher().cancelAll();
//                        mMapWebSocket.cancel();
//                        mMapWebSocket=null;
                    }
                    stopSelf();
                }
            }
        }
        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 开始Socket请求
     */
    private void startSocket() {
        mOkHttpClient = new OkHttpClient.Builder()
                .build();
        //构造request对象
        mRequest = new Request.Builder()
                .url(mUrl)
                .build();
        //建立连接
        mOkHttpClient.newWebSocket(mRequest, mWebSocketListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mWakeLock)
        {
            mWakeLock.release();
            mWakeLock = null;
        }

    }
}

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
package com.lib.sdk.next.robot.service;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.lib.sdk.next.NextException;
import com.lib.sdk.next.livedata.EventConstant;
import com.lib.sdk.next.livedata.LiveDataBus;
import com.lib.sdk.next.o.map.event.RobotErrorEvent;
import com.lib.sdk.next.o.map.event.RobotStatusEvent;
import com.lib.sdk.next.robot.bean.RobotErrorStatusBean;
import com.lib.sdk.next.robot.constant.RobotConstant;
import com.lib.sdk.next.socket.CustomSocketListener;
import com.lib.sdk.next.socket.WebSocketService;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Response;
import okhttp3.WebSocket;
import okio.ByteString;

/**
 * Created by maqing 2018/11/29 10:56
 * Email：2856992713@qq.com
 * 通过EventBus 将事件类型为RobotErrorStatusEvent发布出去
 */
public class RobotErrorStatusService extends WebSocketService {
    private static final String TAG = "RobotErrorStatusService";

    @Override
    public void onCreate() {
        super.onCreate();

        mCustomSocketListener = new CustomSocketListener() {

            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                Log.d(TAG, "onOpen：" + response);
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                try {
                    JSONObject res = new JSONObject(text);
                    int code = res.getInt("result_code");
                    if (code == 0) {
                        JSONObject data = res.getJSONObject("data");
                        RobotConstant.mRobotErrorStatusBean.setErrorCode(data.getInt("error_level"));
                        RobotConstant.mRobotErrorStatusBean.setErrorStatus(data.getString("error_msg"));
                        RobotErrorEvent robotErrorEvent = new RobotErrorEvent();
                        robotErrorEvent.setCode(NextException.CODE_NEXT_SUCCESS);
                        robotErrorEvent.setmRobotErrorStatusBean(RobotConstant.mRobotErrorStatusBean);
                        EventBus.getDefault().post(robotErrorEvent);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                Log.e(TAG, "onMessage");
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                Log.e(TAG, "onClosing");

            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                Log.e(TAG, "webSocket");
                RobotErrorEvent robotErrorEvent = new RobotErrorEvent();
                robotErrorEvent.setCode(NextException.CODE_SOCKET_CLOSED);
                robotErrorEvent.setmRobotErrorStatusBean(RobotConstant.mRobotErrorStatusBean);
                EventBus.getDefault().post(robotErrorEvent);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                Log.e(TAG, "onFailure");
                //连接失败
                RobotErrorEvent robotErrorEvent = new RobotErrorEvent();
                robotErrorEvent.setCode(NextException.CODE_SOCKET_FAIL);
                robotErrorEvent.setmRobotErrorStatusBean(RobotConstant.mRobotErrorStatusBean);
                EventBus.getDefault().post(robotErrorEvent);
            }
        };
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {

        }
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
    }

    public static void start(Context context, String url, String name) {
        Intent intent = new Intent(context, RobotErrorStatusService.class);
        intent.putExtra(EXTRA_URL, url + name);
        intent.putExtra(EXTRA_ACTION, "start");
        context.startService(intent);
    }

}

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
import com.lib.sdk.next.o.map.event.RobotNavigationStatusEvent;
import com.lib.sdk.next.robot.bean.RobotNavigationStatusBean;
import com.lib.sdk.next.socket.CustomSocketListener;
import com.lib.sdk.next.socket.WebSocketService;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Response;
import okhttp3.WebSocket;
import okio.ByteString;

/**
 * Created by maqing 2018/11/29 15:07
 * Email：2856992713@qq.com
 * 机器人导航状态Service
 * 通过EventBus 将事件类型为 robotNavigationStatusEvent 发布出去
 */
public class RobotNavigationStatusService extends WebSocketService {
    private static final String TAG = RobotNavigationStatusService.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();

        mCustomSocketListener = new CustomSocketListener() {

            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                Log.e(TAG, "onOpen：" + response);
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                Log.e(TAG, "onMessage" + text);
                try {
                    JSONObject res = new JSONObject(text);
                    int code = res.getInt("result_code");
                    if (code == 0) {
                        final JSONObject data = res.getJSONObject("data");
                        RobotNavigationStatusBean robotNavigationStatusBean = new RobotNavigationStatusBean();
                        robotNavigationStatusBean.setStatusCode(data.getString("status_code"));
                        robotNavigationStatusBean.setStatusMsg(data.getString("status_msg"));
                        RobotNavigationStatusEvent  navigationStatusEvent = new RobotNavigationStatusEvent();
                        navigationStatusEvent.setCode(NextException.CODE_NEXT_SUCCESS);
                        navigationStatusEvent.setRobotNavigationStatusBean(robotNavigationStatusBean);
                        EventBus.getDefault().post(navigationStatusEvent);

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

                RobotNavigationStatusEvent  navigationStatusEvent = new RobotNavigationStatusEvent();
                navigationStatusEvent.setCode(NextException.CODE_SOCKET_CLOSED);
                navigationStatusEvent.setRobotNavigationStatusBean(new RobotNavigationStatusBean());
                EventBus.getDefault().post(navigationStatusEvent);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                Log.e(TAG, "onFailure");
                RobotNavigationStatusEvent  navigationStatusEvent = new RobotNavigationStatusEvent();
                navigationStatusEvent.setCode(NextException.CODE_SOCKET_FAIL);
                navigationStatusEvent.setRobotNavigationStatusBean(new RobotNavigationStatusBean());
                EventBus.getDefault().post(navigationStatusEvent);
            }
        };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
    }

    public static void start(Context context, String robotIp,String name) {
        Intent intent = new Intent(context, RobotNavigationStatusService.class);
        intent.putExtra(EXTRA_URL, robotIp + name);
        intent.putExtra(EXTRA_ACTION, "start");
        context.startService(intent);
    }
}

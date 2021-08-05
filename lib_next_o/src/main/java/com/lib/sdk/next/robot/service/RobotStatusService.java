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
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;


import com.lib.sdk.next.NextException;
import com.lib.sdk.next.livedata.EventConstant;
import com.lib.sdk.next.livedata.LiveDataBus;
import com.lib.sdk.next.o.R;
import com.lib.sdk.next.global.GlobalOperate;
import com.lib.sdk.next.o.map.event.RobotStatusEvent;
import com.lib.sdk.next.robot.bean.RobotPostionBean;
import com.lib.sdk.next.robot.bean.RobotStatusBean;
import com.lib.sdk.next.robot.constant.RobotConstant;
import com.lib.sdk.next.socket.CustomSocketListener;
import com.lib.sdk.next.socket.WebSocketService;
import com.lib.sdk.next.util.DialogUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.NoSuchElementException;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Response;
import okhttp3.WebSocket;
import okio.ByteString;

/**
 * Created by maqing 2018/11/15 15:35
 * Email：2856992713@qq.com
 * 通过EventBus 将事件类型为 robotStatusEvent 发布出去
 */
public class RobotStatusService extends WebSocketService {
    private static final String TAG = "RobotStatusService";

    private int mDisConnectTime = 0;
    private Timer mTimer;
    private TimerTask mTimerTask = new TimerTask() {
        @Override
        public void run() {
            mDisConnectTime++;
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        mCustomSocketListener = new CustomSocketListener() {

            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                mDisConnectTime = 0;
                Log.d(TAG, "onOpen：" + response);
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                mDisConnectTime = 0;
                try {
                    JSONObject res = new JSONObject(text);
                    int code = res.getInt("result_code");
                    if (code == 0) {
                        JSONObject data = res.getJSONObject("data");

                        RobotConstant.mRobotStatusBean.setBattery(data.getInt("battery"));
                        RobotConstant.mRobotStatusBean.setDiagnose(data.getInt("diagnose"));
                        RobotConstant.mRobotStatusBean.setEmergence(data.getInt("emergence"));
                        RobotConstant.mRobotStatusBean.setGlobalStatusCode(data.getInt("global_status_code"));
                        RobotConstant.mRobotStatusBean.setGlobalStatusMsg(data.getString("global_status_msg"));
                        RobotConstant.mRobotStatusBean.setProjectId(data.getString("project_id"));
                        RobotConstant.mRobotStatusBean.setTask(data.getString("task"));
                        RobotConstant.mRobotStatusBean.setPositionJson(data.getString("position"));
                        RobotConstant.mRobotStatusBean.setBatteryState(data.getInt("battery_state"));
                        RobotConstant.mRobotStatusBean.setIoStatusJson(new JSONObject(data.optString("io_status")));
                        RobotConstant.mRobotStatusBean.setBoard_state(data.optInt("board_state"));
                        RobotConstant.mRobotStatusBean.setEdge_switch(data.optInt("edge_switch"));

                        try {
                            JSONObject positionObject = data.getJSONObject("position");
                            double worldX = positionObject.getDouble("x");
                            double worldY = positionObject.getDouble("y");
                            double theta = positionObject.getDouble("theta");
                            RobotPostionBean robotPostionBean = new RobotPostionBean();
                            robotPostionBean.setWorldX(worldX);
                            robotPostionBean.setWorldY(worldY);
                            robotPostionBean.setTheta(theta);
                            RobotConstant.mRobotStatusBean.setRobotPostionBean(robotPostionBean);
                        } catch (JSONException jsonException) {
                            jsonException.printStackTrace();
                        }

                        try {
                            JSONObject speedObject = data.getJSONObject("speed");
                            double speedTheta = speedObject.getDouble("speed_theta");
                            double speedX = speedObject.getDouble("speed_x");
                            RobotConstant.mRobotStatusBean.setAngularSpeed(speedTheta);
                            RobotConstant.mRobotStatusBean.setLineSpeed(speedX);
                        } catch (JSONException jsonException) {
                            jsonException.printStackTrace();
                        }





                        RobotStatusEvent statusEvent = new RobotStatusEvent();
                        statusEvent.setCode(NextException.CODE_NEXT_SUCCESS);
                        statusEvent.setRobotStatusBean(RobotConstant.mRobotStatusBean);
                        EventBus.getDefault().post(statusEvent);
                       // LiveDataBus.get().with(EventConstant.NEXT_MAP_ROBOT_STATUS).postValue(mRobotStatusBean);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                Log.d(TAG, "onMessage");
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                Log.d(TAG, "onClosing");
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                Log.d(TAG, "webSocket");
                RobotStatusEvent statusEvent = new RobotStatusEvent();
                statusEvent.setCode(NextException.CODE_SOCKET_CLOSED);
                statusEvent.setRobotStatusBean(RobotConstant.mRobotStatusBean);
                EventBus.getDefault().post(statusEvent);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                if (mDisConnectTime > 5) {
                    try {
                        mDisConnectTime = 0;
                        RobotStatusEvent statusEvent = new RobotStatusEvent();
                        statusEvent.setCode(NextException.CODE_SOCKET_FAIL);
                        statusEvent.setRobotStatusBean(RobotConstant.mRobotStatusBean);
                        EventBus.getDefault().post(statusEvent);

                    } catch (NoSuchElementException e) {
                        Log.e(TAG, "activity destory");
                    }
                }

                if (mTimer == null ) {
                    mTimer = new Timer();
                    mTimer.schedule(mTimerTask, 0, 1000);
                }

            }
        };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
    }

    /**
     * 启动
     * @param context
     */
    public static void start(Context context,String robotIp,String name) {
        Intent intent = new Intent(context, RobotStatusService.class);
        intent.putExtra(EXTRA_URL, robotIp + name);
        intent.putExtra(EXTRA_ACTION, "start");
        context.startService(intent);


    }

}

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


import com.lib.sdk.next.livedata.EventConstant;
import com.lib.sdk.next.livedata.LiveDataBus;
import com.lib.sdk.next.livedata.RobotLaserEvent;
import com.lib.sdk.next.o.map.manager.ProjectCacheManager;
import com.lib.sdk.next.o.map.util.PositionUtil;
import com.lib.sdk.next.robot.bean.RobotLaserDataBean;
import com.lib.sdk.next.robot.constant.RobotConstant;
import com.lib.sdk.next.socket.CustomSocketListener;
import com.lib.sdk.next.socket.WebSocketService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;
import okhttp3.WebSocket;
import okio.ByteString;

/**
 * Created by maqing 2018/11/29 17:28
 * Email：2856992713@qq.com
 * 通过EventBus 将事件类型为 robotLaserEvent 发布出去
 * 机器人激光数据
 */
public class RobotLaserDataService extends WebSocketService {

    private static final String TAG = "RobotLaserDataService";

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
               // Log.d(TAG, "onMessage" + text);
                try {
                    JSONObject res = new JSONObject(text);
                    int code = res.getInt("result_code");
                    if (code == 0) {
                        JSONObject data = res.getJSONObject("data");
                        JSONArray laserDataArray = data.getJSONArray("laser_data");
                        List<RobotLaserDataBean> laserList = new ArrayList<>();
                        try {
                            double resolution = ProjectCacheManager.getMapResolution(getApplicationContext(), RobotConstant.mRobotStatusBean.getProjectId());
                            double originX = ProjectCacheManager.getMapOriginX(getApplicationContext(), RobotConstant.mRobotStatusBean.getProjectId());
                            double originY = ProjectCacheManager.getMapOriginY(getApplicationContext(), RobotConstant.mRobotStatusBean.getProjectId());
                            for (int i = 0; i < laserDataArray.length(); i++) {
                                JSONObject laserItem = laserDataArray.getJSONObject(i);
                                RobotLaserDataBean robotLaserDataBean = new RobotLaserDataBean();
                                robotLaserDataBean.setmServerX(laserItem.getDouble("x"));
                                robotLaserDataBean.setmServerY(laserItem.getDouble("y"));
                                float x = (float) PositionUtil.serverToLocalX(
                                        laserItem.getDouble("x"),
                                        resolution,
                                        originX
                                );
                                float y = (float) PositionUtil.serverToLocalY(laserItem.getDouble("y"),
                                        resolution,
                                        originY);
                                robotLaserDataBean.setMapX(x);
                                robotLaserDataBean.setMapY(y);
                                laserList.add(robotLaserDataBean);
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                            for (int i = 0; i < laserDataArray.length(); i++) {
                                JSONObject laserItem = laserDataArray.getJSONObject(i);
                                RobotLaserDataBean robotLaserDataBean = new RobotLaserDataBean();
                                robotLaserDataBean.setmServerX(laserItem.getDouble("x"));
                                robotLaserDataBean.setmServerY(laserItem.getDouble("y"));
                                laserList.add(robotLaserDataBean);
                            }
                        }
                        RobotLaserEvent robotLaserEvent = new RobotLaserEvent();
                        robotLaserEvent.setLaserDataBeans(laserList);
                        LiveDataBus.get().with(EventConstant.NEXT_MAP_ROBOT_LASER).postValue(robotLaserEvent);
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
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                Log.d(TAG, "onFailure");
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
        Intent intent = new Intent(context,RobotLaserDataService.class);
        intent.putExtra(EXTRA_URL, url + name);
        intent.putExtra(EXTRA_ACTION, "start");
        context.startService(intent);
    }
}

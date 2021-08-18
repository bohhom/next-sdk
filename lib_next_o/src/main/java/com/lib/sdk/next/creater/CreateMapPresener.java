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
package com.lib.sdk.next.creater;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.bozh.logger.Logger;
import com.lib.sdk.next.livedata.EventConstant;
import com.lib.sdk.next.livedata.LiveDataBus;
import com.lib.sdk.next.livedata.RobotCreateEvent;
import com.lib.sdk.next.o.http.BaseDataCallback;
import com.lib.sdk.next.o.http.HttpResponse;
import com.lib.sdk.next.global.GlobalOperate;
import com.lib.sdk.next.o.map.manager.ProjectCacheManager;
import com.lib.sdk.next.o.map.manager.RequestManager;
import com.lib.sdk.next.o.map.model.CustomModel;
import com.lib.sdk.next.o.map.net.SocketRequestInterface;
import com.lib.sdk.next.o.map.presenter.HttpPresenter;
import com.lib.sdk.next.o.map.util.FileUtils;
import com.lib.sdk.next.o.map.util.ImageCache;
import com.lib.sdk.next.o.model.DataModel;
import com.lib.sdk.next.robot.constant.RobotConstant;
import com.lib.sdk.next.tag.NextTag;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

/**
 * FileName: CreateMapPresener
 * Author: zhikai.jin
 * Date: 2021/5/26 10:49
 * Description: 创建地图逻辑处理类
 */
public class CreateMapPresener extends HttpPresenter {
    private final String TAG = CreateMapPresener.class.getSimpleName();

    private Handler mHandler = new Handler();

    private OkHttpClient mMapOkHttpClient;

    private Request mMapRequest;

    private WebSocket mMapWebSocket;

    private Context mContext;

    protected DisplayMetrics displayMetrics;

    private OkHttpClient mRockerOkHttpClient;

    private WebSocket mRockerSocket;

    /**
     * 虚拟手柄当前角度
     */
    private double mCurrentAngle = 0;
    /**
     * 虚拟手柄当前等级
     */
    private double mCurrentDistanceLevel = 0;

    private Timer mRockerDataTimer;
    private TimerTask mRockerDataTimerTask = new TimerTask() {
        @Override
        public void run() {
            if (mRockerSocket != null
                    && !(mCurrentAngle == 0 && mCurrentDistanceLevel == 0)
            ) { //连接没有中断，且摇杆不在原点，则发送数据到服务器
                JSONObject params = new JSONObject();
                try {
                    params.put("position", mCurrentAngle);
                    params.put("offset", mCurrentDistanceLevel);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "mRockerDataTimerTask:" + params.toString());
                mRockerSocket.send(params.toString());
            }
        }
    };

    public CreateMapPresener() {

    }


    public void setContext(@NonNull Context context) {
        this.mContext = context;
    }

    public void serverBackMapData() {

        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        displayMetrics.heightPixels += displayMetrics.heightPixels * 2;
        displayMetrics.widthPixels += displayMetrics.widthPixels * 2;

        mMapOkHttpClient = new OkHttpClient.Builder()
                .build();
        //构造request对象
        mMapRequest = new Request.Builder().url(RequestManager.mSocketBaseUrl + SocketRequestInterface.SERVER_BACK_MAP).build();
        //建立连接
        mMapOkHttpClient.newWebSocket(mMapRequest, mMapWebSocketListener);
        rockerDataSocket();
        mRockerDataTimer = new Timer();
        mRockerDataTimer.schedule(mRockerDataTimerTask, 0, 50);
    }

    private WebSocketListener mMapWebSocketListener = new WebSocketListener() {
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            super.onOpen(webSocket, response);
            mMapWebSocket = webSocket;
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            super.onMessage(webSocket, text);
            loadMapData(text);
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            super.onClosed(webSocket, code, reason);
            mMapWebSocket = null;
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            super.onFailure(webSocket, t, response);

            mMapWebSocket = null;
            //建立连接
            if (mMapOkHttpClient != null && mMapRequest != null) {
                Logger.t(NextTag.TAG).e("onFailure：" + t + "," + response);
                mMapOkHttpClient.newWebSocket(mMapRequest, mMapWebSocketListener);
            }

        }
    };


    /**
     * 加载地图数据
     *
     * @param response
     */
    private synchronized void loadMapData(String response) {
        try {
            JSONObject res = new JSONObject(response);
            JSONObject data = res.getJSONObject("data");

            FileUtils.OriginSize mOriginSize = new FileUtils.OriginSize();

            ProjectCacheManager.updateMapPictureFile(mContext, "-1", data.getString("map_data"));
            Bitmap newBitmap;
            try {
                newBitmap = FileUtils.decodeSampledBitmapFromResource(ProjectCacheManager.getMapPictureFilePath(mContext, "-1")
                        , displayMetrics.widthPixels / 2, displayMetrics.heightPixels / 2, mOriginSize);
            } catch (OutOfMemoryError error) {
                error.printStackTrace();
                return;
            }

            double resolution = data.getDouble("resolution");
            double originX = data.getDouble("origin_x");
            double originY = data.getDouble("origin_y");

            RobotCreateEvent createEvent = new RobotCreateEvent();
            createEvent.setNewMapBitMap(newBitmap);
            createEvent.setResolution(resolution);
            createEvent.setOriginX(originX);
            createEvent.setOriginY(originY);
            createEvent.setOriginSize(mOriginSize);
            LiveDataBus.get().with(EventConstant.NEXT_MAP_ROBOT_CREATE).postValue(createEvent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void operateMap(String url, HashMap<String, Object> params) {
        realGetData(DataModel.invoke(CustomModel.class).params(params), url, new BaseDataCallback<HttpResponse>() {
            @Override
            public void dataCallback(HttpResponse data) {
                ((CreateMapHelper) getHelper()).operateMapDataCallBack(data);
            }
        });
    }

    //地图闭环操作
    public void mapCloser(String url, HashMap<String, Object> params) {
        realGetData(DataModel.invoke(CustomModel.class).params(params), url, new BaseDataCallback<HttpResponse>() {
            @Override
            public void dataCallback(HttpResponse data) {
                ((CreateMapHelper) getHelper()).mapCloserDataCallBack(data);
            }
        });
    }


    /**
     * 虚拟摇杆数据
     */
    private void rockerDataSocket() {
        mRockerOkHttpClient = new OkHttpClient.Builder()
                .build();
        //构造request对象
        Request request = new Request.Builder()
                .url(RequestManager.mSocketBaseUrl + SocketRequestInterface.ROCKER_DATA)
                .build();
        //建立连接
        mRockerOkHttpClient.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                Log.d(TAG, "mRockerSocket onOpen：" + response);
                mRockerSocket = webSocket;
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
//                LogUtil.e(TAG, "onMessage：" + text);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
                Log.d(TAG, "mRockerSocket onClosed");
                mRockerSocket = null;
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                super.onFailure(webSocket, t, response);
                Log.d(TAG, "mRockerSocket onFailure");
                mRockerSocket = null;
            }
        });
    }

    public void onDestroy() {
        if (mRockerSocket != null) {
            mRockerOkHttpClient.dispatcher().cancelAll();
        }
    }

    public void setRockValue(double currentAngle, double currentDistanceLevel) {

        this.mCurrentAngle = currentAngle;
        this.mCurrentDistanceLevel = currentDistanceLevel;
    }

    public void saveMapToLocal(JSONObject data) {
        try {
            JSONObject projectItem = data.getJSONObject("app_project");

            //第一层project_info
            JSONObject projectInfoObject = projectItem.getJSONObject("project_info");
            final String projectId = projectInfoObject.getString("project_id");
            if (!ProjectCacheManager.projectInfoFileExits(GlobalOperate.getApp(), projectId)) {
                ProjectCacheManager.createProjectInfoFile(GlobalOperate.getApp(), projectId);
            }

            //更新该工程的地图信息数据
            JSONObject mapObject = data.getJSONObject("map_info");
            if (!ProjectCacheManager.mapInfoFileExits(GlobalOperate.getApp(), projectId)) {
                ProjectCacheManager.createMapInfoFile(GlobalOperate.getApp(), projectId);
            }
            //更新地图图片数据
            ProjectCacheManager.updateMapPictureFile(GlobalOperate.getApp(), projectId, data.getString("map_data"));

            mapObject.put("png_name", ProjectCacheManager.getMapPictureFilePath(GlobalOperate.getApp(), projectId));
            ProjectCacheManager.updateMapInfoFile(GlobalOperate.getApp(), projectId, mapObject.toString());
            projectInfoObject.put("map_name", ProjectCacheManager.getMapInfoFileName(GlobalOperate.getApp(), projectId));

            //更新虚拟墙数据
            JSONObject obstaclesObject = projectItem.getJSONObject("obstacles");
            if (!ProjectCacheManager.obstaclesInfoFileExits(GlobalOperate.getApp(), projectId)) {
                ProjectCacheManager.createObstaclesFile(GlobalOperate.getApp(), projectId);
            }
            ProjectCacheManager.updateObstaclesInfoFile(GlobalOperate.getApp(), projectId, obstaclesObject.toString());
            projectInfoObject.put("obstacle_name", ProjectCacheManager.getObstaclesFileName(GlobalOperate.getApp(), projectId));

            //更新工程标记坐标点数据
            JSONObject positionsObject = projectItem.getJSONObject("positions");
            if (!ProjectCacheManager.positionsInfoFileExits(GlobalOperate.getApp(), projectId)) {
                ProjectCacheManager.createPositionsFile(GlobalOperate.getApp(), projectId);
            }
            ProjectCacheManager.updatePositionInfoFile(GlobalOperate.getApp(), projectId, positionsObject.toString());
            projectInfoObject.put("positions_name", ProjectCacheManager.getPositionsFileName(GlobalOperate.getApp(), projectId));

            //更新project_info文件
            ProjectCacheManager.updateProjectInfoFile(GlobalOperate.getApp(), projectId, projectInfoObject.toString());

            ImageCache.getInstance().clearBitmapToMemoryCache();
            RobotConstant.mRobotStatusBean.setProjectId(projectId);
            Logger.d("create map is success , projectId = %s" + projectId);
            Logger.d("create map is success , robotProjectId = %s" + RobotConstant.mRobotStatusBean.getProjectId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}

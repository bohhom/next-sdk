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

package com.lib.sdk.next.project;


import android.os.Handler;

import com.lib.sdk.next.o.http.BaseDataCallback;
import com.lib.sdk.next.o.http.HttpResponse;
import com.lib.sdk.next.global.GlobalOperate;
import com.lib.sdk.next.o.map.manager.ProjectCacheManager;
import com.lib.sdk.next.o.map.model.CustomModel;
import com.lib.sdk.next.o.map.presenter.HttpPresenter;
import com.lib.sdk.next.o.model.DataModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by aruba on 2020/5/11.
 */

class ProjectPresenter extends HttpPresenter {

    private Handler mHandler = new Handler();

    //拉取需要更新的工程
    public void pullNeedUpdateProject(String url, HashMap<String, Object> params) {
        realGetData(DataModel.invoke(CustomModel.class).params(params), url, new BaseDataCallback<HttpResponse>() {
            @Override
            public void dataCallback(HttpResponse data) {
                ((ProjectInfoHelper) getHelper()).updateDataCallBack(data);
            }
        });
    }

    //拉取需要更新的工程的详细数据
    public void PullSyncProjectInfo(String url, HashMap<String, Object> params) {
        realGetData(DataModel.invoke(CustomModel.class).params(params), url, new BaseDataCallback<HttpResponse>() {
            @Override
            public void dataCallback(HttpResponse data) {
                ((ProjectInfoHelper) getHelper()).projectInfoDataCallBack(data);
            }
        });
    }


    /**
     * 同步服务端的"projects"数据到本地文件
     *
     * @param projectArray
     */
    public void syncProjectDataToLocal(final JSONArray projectArray, IProjectSyncLocalBack localBack) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                for (int i = 0; i < projectArray.length(); i++) {

                    try {
                        final JSONObject projectItem = projectArray.getJSONObject(i);

                        //第一层project_info
                        JSONObject projectInfoObject = projectItem.getJSONObject("project_info");
                        final String projectId = projectInfoObject.getString("project_id");
                        if (!ProjectCacheManager.projectInfoFileExits(GlobalOperate.getApp(), projectId)) {
                            ProjectCacheManager.createProjectInfoFile(GlobalOperate.getApp(), projectId);
                        }

                        //更新该工程的地图信息数据
                        JSONObject mapObject = projectItem.getJSONObject("map_info");
                        if (!ProjectCacheManager.mapInfoFileExits(GlobalOperate.getApp(), projectId)) {
                            ProjectCacheManager.createMapInfoFile(GlobalOperate.getApp(), projectId);
                        }
                        //更新地图图片数据
                        ProjectCacheManager.updateMapPictureFile(GlobalOperate.getApp(), projectId, projectItem.getString("map_data"));

                        try {//更新时间戳
                            String projectStamp = projectInfoObject.getString("project_stamp");
                            ProjectCacheManager.updateProjectStamp(GlobalOperate.getApp(), projectStamp);
                        } catch (Exception e) {

                        }

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

                        //更新工程航路数据
                        JSONObject routesObject = projectItem.getJSONObject("paths");
                        if (!ProjectCacheManager.routesInfoFileExits(GlobalOperate.getApp(), projectId)) {
                            ProjectCacheManager.createRoutesFile(GlobalOperate.getApp(), projectId);
                        }
                        ProjectCacheManager.updateRoutesInfoFile(GlobalOperate.getApp(), projectId, routesObject.toString());
                        projectInfoObject.put("paths_name", ProjectCacheManager.getRoutesFileName(GlobalOperate.getApp(), projectId));

                        //更新project_info文件
                        ProjectCacheManager.updateProjectInfoFile(GlobalOperate.getApp(), projectId, projectInfoObject.toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        localBack.onSysncLocal();
                    }
                });
            }
        }.start();
    }


}

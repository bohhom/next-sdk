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
package com.lib.sdk.next.map;

import android.text.TextUtils;

import com.lib.sdk.next.NextException;
import com.lib.sdk.next.NextResultInfo;
import com.lib.sdk.next.base.IBaseHelper;
import com.lib.sdk.next.o.http.HttpResponse;
import com.lib.sdk.next.global.GlobalOperate;
import com.lib.sdk.next.o.map.manager.ProjectCacheManager;
import com.lib.sdk.next.o.map.net.HttpUri;
import com.lib.sdk.next.o.map.widget.MapDrawView;
import com.lib.sdk.next.robot.constant.RobotConstant;
import com.lib.sdk.next.util.ImageUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * FileName: MapHelper
 * Author: zhikai.jin
 * Date: 2021/6/28 17:37
 * Description:地图操作
 */
public class ProjectHelper extends IBaseHelper<MapPresenter> implements IProjectOperateCallBack {

    private String mSelectProjectId = "";

    private String mTempUpdateProjectContent = "";

    private IMapOperResultListener iMmapOperResultListener;

    private final static int SYNC_NAME = 1;

    private final static int SYNC_NULL = 0;

    private int mSyncProjectType = SYNC_NULL;

    private static volatile ProjectHelper mInstance;

    public static ProjectHelper getInstance() {
        if (mInstance == null) {
            synchronized (ProjectHelper.class) {
                if (mInstance == null) {
                    mInstance = new ProjectHelper();
                }
            }
        }
        return mInstance;
    }


    private ProjectHelper() {
        this(new MapPresenter());
    }

    private ProjectHelper(MapPresenter basePresenter) {
        super(basePresenter);
    }

    @Override
    public void showErr(String uri, String msg) {

    }

    @Override
    protected void attachView(MapDrawView drawView) {

    }

    public void setIMapOperResultListener(IMapOperResultListener listener) {
        this.iMmapOperResultListener = listener;
    }

    /**
     * 切换工程
     */
    public void changeProject(String projectId) {
        this.mSelectProjectId = projectId;
        HashMap<String, Object> params = new HashMap();
        params.put("project_id", projectId);

        mPresenter.changeProject(HttpUri.URL_SELECT_PROJECT, params);
    }


    /**
     * 上传工程
     */
    public void uploadProject(String projectId) {
        this.mSelectProjectId = projectId;
        mSyncProjectType = SYNC_NULL;
        syncProject(SYNC_NULL, "");
    }

    /**
     * 更换项目名称
     */
    public void changeProjectName(String newProjectName, String projectId) {
        mSyncProjectType = SYNC_NAME;
        this.mSelectProjectId = projectId;
        syncProject(SYNC_NAME, newProjectName);
    }


    /**
     * 更换项目名称
     */
    public void deleteProject(String projectId) {
        this.mSelectProjectId = projectId;
        HashMap<String, Object> params = new HashMap();
        params.put("project_id", projectId);
        mPresenter.deleteProject(HttpUri.URL_DELETE_PROJECT, params);
    }

    /**
     * 同步工程到机器人(服务器)
     *
     * @param type 操作类型 0:同步整个工程 1：修改工程（地图）名称
     */
    private void syncProject(int type, String mapName) {

        JSONObject params = new JSONObject();
        JSONArray projectArray = new JSONArray();
        try {
            switch (type) {
                case SYNC_NULL:
                    onFormatJsonParams(params, projectArray);
                    break;

                case SYNC_NAME:
                    JSONObject projectObject = new JSONObject();
                    mTempUpdateProjectContent = ProjectCacheManager.getUpdateProjectNameTempContent(GlobalOperate.getApp(), mSelectProjectId, mapName);
                    projectObject.put("project_info", new JSONObject(mTempUpdateProjectContent));
                    projectArray.put(projectObject);
                    params.put("projects", projectArray);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mPresenter.pushSyncProject(HttpUri.URL_PUSH_SYNCPROJECT, params);
    }

    private void onFormatJsonParams(JSONObject params, JSONArray projectArray) throws JSONException {
        List<String> projectInfoJsonList = ProjectCacheManager.getAllProjectCacheFileOriContent(GlobalOperate.getApp());
        for (int i = 0; i < projectInfoJsonList.size(); i++) {

            JSONObject projectObject = new JSONObject();

            //工程基本信息
            if (TextUtils.isEmpty(projectInfoJsonList.get(i))) {
                continue;
            }

            JSONObject projectInfoObject = new JSONObject(projectInfoJsonList.get(i));

            if (projectInfoObject.getString("project_id").equals(mSelectProjectId)) {
                projectObject.put("project_info", projectInfoObject);

                //地图图片数据
                projectObject.put("map_data", ImageUtil.imageToBase64(ProjectCacheManager.getMapPictureFilePath(GlobalOperate.getApp(), projectInfoObject.getString("project_id"))));

                //地图基本信息
                projectObject.put("map_info", new JSONObject(ProjectCacheManager.getMapInfoCacheFileOriContent(GlobalOperate.getApp(), projectInfoObject.getString("project_id"))));

                //虚拟墙信息
                projectObject.put("obstacles", new JSONObject(ProjectCacheManager.getObstacleCacheFileOriContent(GlobalOperate.getApp(), projectInfoObject.getString("project_id"))));

                //位置信息
                projectObject.put("positions", new JSONObject(ProjectCacheManager.getPositionCacheFileOriContent(GlobalOperate.getApp(), projectInfoObject.getString("project_id"))));

                //航路信息
                projectObject.put("paths", new JSONObject(ProjectCacheManager.getRoutesCacheFileOriContent(GlobalOperate.getApp(), projectInfoObject.getString("project_id"))));

                projectArray.put(projectObject);

                break;
            }
        }
        params.put("projects", projectArray);
    }

    @Override
    public void changeProjectDataCallBack(HttpResponse response) {
        String info = response.info;
        int code = response.code;
        if (code == 0) {
            RobotConstant.mRobotStatusBean.setProjectId(mSelectProjectId);
            iMmapOperResultListener.onChangeProjectResult(new NextResultInfo(NextException.CODE_NEXT_SUCCESS, info));
        }
        else if(code == 1){
            iMmapOperResultListener.onChangeProjectResult(new NextResultInfo(NextException.PROJECT_NOT_CHANGE, info));
        }
        else if(code  == 2){
            iMmapOperResultListener.onChangeProjectResult(new NextResultInfo(NextException.PROJECT_LOAD_FAIL, info));
        }
        else if(code == 3){
            iMmapOperResultListener.onChangeProjectResult(new NextResultInfo(NextException.PROJECT_CHANGE_FAIL, info));
        }
        else if(code == 4){
            iMmapOperResultListener.onChangeProjectResult(new NextResultInfo(NextException.PROJECT_TASK_OTHER, info));
        }
        else{
            iMmapOperResultListener.onChangeProjectResult(new NextResultInfo(code, info));
        }

    }

    @Override
    public void pushSyncProjectDataCallBack(HttpResponse response) {

        try {
            String info = response.info;
            int code = response.code;
            switch (mSyncProjectType) {
                case SYNC_NAME:
                    if (code == NextException.CODE_NEXT_SUCCESS) {
                        //更新本地的工程名字
                        ProjectCacheManager.updateProject(GlobalOperate.getApp(), mSelectProjectId, mTempUpdateProjectContent);
                    }
                    iMmapOperResultListener.onChangeProjectName(new NextResultInfo(code, info));
                    break;
                case SYNC_NULL:
                    iMmapOperResultListener.onUploadProject(new NextResultInfo(code, info));
                    break;
                default:
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void deleteProjectDataCallBack(HttpResponse response) {
        String info = response.info;
        int code = response.code;
        if (code == 0 ) {
            //删除本地工程
            ProjectCacheManager.deleteProject(GlobalOperate.getApp(), mSelectProjectId);
            iMmapOperResultListener.onDeleteProject(new NextResultInfo(NextException.CODE_NEXT_SUCCESS, info));
        }
        else if(code == 1){
            iMmapOperResultListener.onDeleteProject(new NextResultInfo(NextException.PROJECT_NOT_DELETE, info));
        }
        else if(code == 2){
            ProjectCacheManager.deleteProject(GlobalOperate.getApp(), mSelectProjectId);
            iMmapOperResultListener.onDeleteProject(new NextResultInfo(NextException.PROJECT_NOT_EXIT, info));
        }
        else if(code == 3){
            iMmapOperResultListener.onDeleteProject(new NextResultInfo(NextException.PROJECT_DELETE_FAIL, info));
        }
        else{
            iMmapOperResultListener.onDeleteProject(new NextResultInfo(code, info));
        }

    }

    public interface IMapOperResultListener {

        void onChangeProjectResult(NextResultInfo resultInfo);

        void onChangeProjectName(NextResultInfo resultInfo);

        void onUploadProject(NextResultInfo resultInfo);

        void onDeleteProject(NextResultInfo resultInfo);

    }
}

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


import com.bozh.logger.Logger;
import com.lib.sdk.next.NextException;
import com.lib.sdk.next.NextResultInfo;
import com.lib.sdk.next.base.IBaseHelper;
import com.lib.sdk.next.callback.IPullProjectCallBack;
import com.lib.sdk.next.o.R;
import com.lib.sdk.next.o.http.HttpResponse;
import com.lib.sdk.next.o.map.bean.ProjectInfoBean;
import com.lib.sdk.next.global.GlobalOperate;
import com.lib.sdk.next.o.map.manager.ProjectCacheManager;
import com.lib.sdk.next.o.map.net.HttpUri;
import com.lib.sdk.next.o.map.resp.SampleProject;
import com.lib.sdk.next.o.map.util.ToastUtil;
import com.lib.sdk.next.o.map.widget.MapDrawView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * FileName: ProjectInfoHelper
 * Author: zhikai.jin
 * Date: 2021/6/2 14:43
 * Description: 工程项
 */
public class ProjectInfoHelper  extends IBaseHelper<ProjectPresenter> implements UpdateProjectCallBack,PullProjectInfoCallBack{


    private static volatile ProjectInfoHelper mInstance;

    private IPullProjectCallBack mIPullProjectCallBack;

    private  ProjectInfoHelper(ProjectPresenter projectPresenser) {
        super(projectPresenser);

    }

    @Override
    public void showErr(String uri, int code, String msg) {
        mIPullProjectCallBack.onHttpError(uri,code,msg);

    }

    public static ProjectInfoHelper getInstance() {
        if (mInstance == null) {
            synchronized (ProjectInfoHelper.class) {
                if (mInstance == null) {
                    mInstance = new ProjectInfoHelper();
                }
            }
        }
        return mInstance;
    }

    private ProjectInfoHelper() {
        this(new ProjectPresenter());
    }



    public void  pullNeedUpdateProject(IPullProjectCallBack callBack){
        if(callBack==null){
            Logger.i("拉取数据的回调为空");
            return;
        }
        this.mIPullProjectCallBack = callBack;
        //获取本地所有的工程
        List<ProjectInfoBean> projectList = ProjectCacheManager.getAllCacheProjectIdStamp(GlobalOperate.getApp());

        HashMap params = new HashMap();
        params.put("type", 1);
        List<SampleProject> projectArray = new ArrayList();
        for (int i = 0; i < projectList.size(); i++) {
            SampleProject projectItem = new SampleProject();
            projectItem.setProjectId(projectList.get(i).getProjectId());
            projectItem.setProjectStamp(projectList.get(i).getProjectStamp());
            projectArray.add(projectItem);
        }
        params.put("projects", projectArray);

        mPresenter.pullNeedUpdateProject(HttpUri.URL_QUERYSYNC_PROJECT, params);
        Logger.i("拉取工程");
    }

    @Override
    public void updateDataCallBack(HttpResponse response) {
        try {
            String info = response.info;
            int code = response.code;
            if (code == 0) {
                Logger.i("拉取工程成功");
                JSONObject data = new JSONObject(response.data);
                JSONArray projectArray = data.optJSONArray("projects");
                if (projectArray != null) {
                    List<SampleProject> needUpdateProject = new ArrayList<>();
                    for (int i = 0; i < projectArray.length(); i++) {
                        JSONObject projectItem = projectArray.getJSONObject(i);
                        SampleProject projectBean = new SampleProject();
                        projectBean.setProjectId(projectItem.getString("project_id"));
                        projectBean.setProjectStamp(projectItem.getString("project_stamp"));
                        needUpdateProject.add(projectBean);
                    }
                    //有更新
                    if (projectArray.length() > 0) {
                        Logger.i("请求工程具体信息");
                        requestProjectData(needUpdateProject);
                        return;
                    }
                }

            } else {
                mIPullProjectCallBack.onPullProjectResult(new NextResultInfo(code,info));

            }
        } catch (JSONException e) {
            e.printStackTrace();
            Logger.e("获取项目更新失败， error = %s",e.getMessage());
            mIPullProjectCallBack.onPullProjectResult(new NextResultInfo(NextException.CODE_NEXT_JSON,e.getMessage()));
        }
    }


    @Override
    public void attachView(MapDrawView drawView) {

    }

    /**
     * 请求项目的详细信息
     * @param projectArray
     */
    private void requestProjectData(List<SampleProject> projectArray) {

        HashMap<String, Object> params = new HashMap();
        params.put("projects", projectArray);
        mPresenter.PullSyncProjectInfo(HttpUri.URL_PULL_SYNC_PROJECT, params);
    }

    @Override
    public void projectInfoDataCallBack(HttpResponse response) {
        try {
            String info = response.info;
            int code = response.code;
            if (code == 0) {
                Logger.i("拉取工程具体数据成功");
                JSONObject data = new JSONObject(response.data);
                JSONArray projectArray = data.optJSONArray("projects");
                mPresenter.syncProjectDataToLocal(projectArray, new IProjectSyncLocalBack() {
                    @Override
                    public void onSysncLocal() {
                        mIPullProjectCallBack.onPullProjectResult(new NextResultInfo(code,info));
                    }
                });
            } else {
                mIPullProjectCallBack.onPullProjectResult(new NextResultInfo(code,info));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            mIPullProjectCallBack.onPullProjectResult(new NextResultInfo(NextException.CODE_NEXT_JSON,e.getMessage()));
            Logger.e("保存项目到本地失败，错误信息，error = %s",e.getMessage());
        }
    }
}

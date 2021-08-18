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

package com.lib.sdk.next.wall;

import android.util.Log;

import com.bozh.logger.Logger;
import com.lib.sdk.next.NextResultInfo;
import com.lib.sdk.next.base.IBaseCallBack;
import com.lib.sdk.next.base.IBaseHelper;
import com.lib.sdk.next.base.NxMap;
import com.lib.sdk.next.o.R;
import com.lib.sdk.next.o.http.HttpResponse;
import com.lib.sdk.next.o.map.bean.VirtualWallBean;
import com.lib.sdk.next.global.GlobalOperate;
import com.lib.sdk.next.o.map.manager.ProjectCacheManager;
import com.lib.sdk.next.o.map.net.HttpUri;
import com.lib.sdk.next.o.map.util.PositionUtil;
import com.lib.sdk.next.o.map.widget.MapDrawView;
import com.lib.sdk.next.point.IPushSyncConfigCallBack;
import com.lib.sdk.next.tag.NextTag;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * FileName: VirtualWallActivity
 * Author: zhikai.jin
 * Date: 2021/6/24 11:34
 * Description: 虚拟墙
 */
public class VirtualWallHelper extends IBaseHelper<VirtualWallPresenter> implements IPushSyncConfigCallBack {

    /**
     * 保存操作
     */
    private static final int TYPE_SAVE = 1;
    /**
     * 删除选中操作（单个删除）
     */
    private static final int TYPE_DELETE = 2;
    /**
     * 删除操作（批量删除）
     */
    private static final int TYPE_DELETE_SELECT = 5;


    private  MapDrawView mMapDrawView;

    private VirtualWallOption mVirtualWallOption;

    private List<VirtualWallBean> mVirtualWallList = new ArrayList<>();

    private String mTempProjectContent = "";

    private IOnVirtualListener mVirtualListener;

    public VirtualWallHelper(NxMap nxMap) {
        super(nxMap, new VirtualWallPresenter());
        onCreate(nxMap);
    }



    @Override
    public void showErr(String uri, int code, String msg) {
        if(mVirtualListener!=null){
            mVirtualListener.onHttpError(uri,code,msg);
        }
        else{
            Logger.e("VirtualWallHelper callback is null");
        }

    }

    @Override
    public void attachView(MapDrawView drawView) {
        this.mMapDrawView = drawView;
    }

    private void updateVirtualWallName() {
        for (int i = 0; i < mVirtualWallList.size(); i++) {
            mVirtualWallList.get(i).setName(GlobalOperate.getApp().getString(R.string.virtual_wall) + (i + 1));
            if (mVirtualWallOption.getCurrentWall() == mVirtualWallList.get(i)) {
                mVirtualWallOption.getCurrentWall().setName(GlobalOperate.getApp().getString(R.string.virtual_wall) + (i + 1));
            }
        }

    }

    @Override
    public void onCreate(NxMap nxMap) {
        super.onCreate(nxMap);
        mVirtualWallList = ProjectCacheManager.getObstacleInfo(GlobalOperate.getApp(),getProjectId());
        initEvent();

    }

    private void initEvent() {
        mMapDrawView.setOnVirtualWallClickListener(new MapDrawView.OnVirtualWallClickListener() {
            @Override
            public void onClick(VirtualWallBean virtualWallBean) {
                Log.d(NextTag.TAG, "setOnVirtualWallClickListener：" + virtualWallBean);
                //点击地图上的虚拟墙使得虚拟墙被选中
                mMapDrawView.setCurrentOperateWall(virtualWallBean);
            }
        });

        mMapDrawView.setOnEditedMapListener(new MapDrawView.OnEditedMapListener() {
            @Override
            public void onEditedMap() {
                switch (mMapDrawView.getEditType()) {
                    case MapDrawView.TYPE_EDIT_TYPE_VIRTUAL_WALL:
                        break;
                }
            }
        });
    }

    /**
     * 添加虚拟墙信息
     */
    public void addVirtualWall(){
        mMapDrawView.setEditType(MapDrawView.TYPE_EDIT_TYPE_VIRTUAL_WALL);
        mVirtualWallOption = new VirtualWallOption();
        mVirtualWallOption.setCurrentWall(mMapDrawView.getCurrentOperateWall());
        mVirtualWallOption.setOperateType(TYPE_SAVE);

        if (mVirtualWallOption.getCurrentWall() != null) {
            //保存到服务器
            List<VirtualWallBean> updateVirtualWallList = new ArrayList<>();
            for (VirtualWallBean virtualWallBean : mVirtualWallList) {
                updateVirtualWallList.add(virtualWallBean);
            }
            updateVirtualWallList.add(mVirtualWallOption.getCurrentWall());
            pushSyncConfig(updateVirtualWallList);
        }
    }


    /**
     * 删除虚拟墙
     */
    public void deleteVirtualWall(VirtualWallBean currentOperateWall){

        mMapDrawView.setEditType(MapDrawView.TYPE_EDIT_TYPE_VIRTUAL_WALL);
        mVirtualWallOption = new VirtualWallOption();
        mVirtualWallOption.setCurrentWall(currentOperateWall);
        mVirtualWallOption.setOperateType(TYPE_DELETE);

        if (currentOperateWall != null) {
            List<VirtualWallBean> updateVirtualList = new ArrayList<>();
            for (VirtualWallBean wallBean : mVirtualWallList) {
                if (mVirtualWallOption.getCurrentWall() != null && !mVirtualWallOption.getCurrentWall().getName().equals(wallBean.getName())) {
                    VirtualWallBean newVirtualWall = new VirtualWallBean();
                    newVirtualWall.setName(wallBean.getName());
                    newVirtualWall.setNeedShowName(wallBean.isNeedShowName());
                    newVirtualWall.setPath(wallBean.getPath());
                    newVirtualWall.setWallPointList(wallBean.getWallPointList());
                    updateVirtualList.add(newVirtualWall);
                }
            }
            for (int i = 0; i < updateVirtualList.size(); i++) {
                updateVirtualList.get(i).setName(GlobalOperate.getApp().getString(R.string.virtual_wall) + (i + 1));
            }
            //保存到服务器
            pushSyncConfig(updateVirtualList);
        }

    }


    /**
     * 虚拟墙保存至服务器
     */
    private void pushSyncConfig(final List<VirtualWallBean> updateVirtualList) {


        JSONObject params = new JSONObject();
        try {
            mTempProjectContent = ProjectCacheManager.getUpdateProjectStampContent(GlobalOperate.getApp(), getProjectId());
            params.put("project_info", new JSONObject(mTempProjectContent));
            params.put("obstacles", new JSONObject(PositionUtil.obstacleListToServerJsonStr(GlobalOperate.getApp(), getProjectId(), updateVirtualList)));
            params.put("positions", new JSONObject(ProjectCacheManager.getPositionCacheFileOriContent(GlobalOperate.getApp(), getProjectId())));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        mPresenter.pushSyncConfig(HttpUri.URL_PUSH_SYNC_CONFIG, params);
    }


    @Override
    public void pushSyncConfigDataCallBack(HttpResponse response) {
        if (response.code == 0) {
            //本地更新标记点信息
            String obstacleInfoJson;
            switch (mVirtualWallOption.getOperateType()){

                case TYPE_SAVE:
                    mVirtualWallList.add(mVirtualWallOption.getCurrentWall());
                    //更新地图显示
                    mMapDrawView.initVirtualWall(mVirtualWallList);
                    if (mMapDrawView.getCurrentOperateWall() != null) {
                        mMapDrawView.getCurrentOperateWall().setNeedShowName(false);
                    }

                    //本地更新标记点信息
                    obstacleInfoJson = PositionUtil.obstacleListToServerJsonStr(GlobalOperate.getApp(),getProjectId(), mVirtualWallList);
                    ProjectCacheManager.updateObstaclesInfoFile(GlobalOperate.getApp(), getProjectId(), obstacleInfoJson);
                    ProjectCacheManager.updateProject(GlobalOperate.getApp(), getProjectId(), mTempProjectContent);
                    if(mVirtualListener!=null){
                        mVirtualListener.onSave(new NextResultInfo(response.code, response.info),mVirtualWallOption.getCurrentWall());
                    }
                    break;

                case TYPE_DELETE:
                    if (mMapDrawView.getCurrentOperateWall() != null) {
                        //删除该虚拟墙
                        mVirtualWallList.remove(mMapDrawView.getCurrentOperateWall());
                    }

                    //更新名字
                    updateVirtualWallName();

                    //更新地图显示
                    mMapDrawView.setCurrentOperateWall(null);
                    mMapDrawView.initVirtualWall(mVirtualWallList);

                    //本地更新标记点信息
                    obstacleInfoJson = PositionUtil.obstacleListToServerJsonStr(GlobalOperate.getApp(), getProjectId(), mVirtualWallList);
                    ProjectCacheManager.updateObstaclesInfoFile(GlobalOperate.getApp(), getProjectId(), obstacleInfoJson);
                    ProjectCacheManager.updateProject(GlobalOperate.getApp(), getProjectId(), mTempProjectContent);
                    if(mVirtualListener!=null){
                        mVirtualListener.onDelete(new NextResultInfo(response.code, response.info));
                    }
                    break;
            }
        }
        else {
            switch (mVirtualWallOption.getOperateType()){

                case TYPE_SAVE:
                    if(mVirtualListener!=null){
                        mVirtualListener.onSave(new NextResultInfo(response.code, response.info),null);
                    }
                    break;

                case TYPE_DELETE:
                    if (mVirtualListener != null) {
                        mVirtualListener.onDelete(new NextResultInfo(response.code, response.info));
                    }
                    break;
            }
        }
    }

    /**
     * 开启虚拟墙操作
     */
    public void openVirtualWallOperate(){
        mMapDrawView.setEditType(MapDrawView.TYPE_EDIT_TYPE_VIRTUAL_WALL);
    }


    /**
     * 关闭虚拟墙操作
     */
    public void closeVirtualWallOperate(){
        mMapDrawView.setEditType(MapDrawView.TYPE_EDIT_INIT);
    }


    public void setVirtualListener(IOnVirtualListener virtualListener){
        this.mVirtualListener = virtualListener;
    }

  public  abstract static class IOnVirtualListener implements IBaseCallBack {

      public abstract void onSave(NextResultInfo resultInfo,VirtualWallBean virtualWallBean);

      public abstract void onDelete(NextResultInfo resultInfo);

  }
}

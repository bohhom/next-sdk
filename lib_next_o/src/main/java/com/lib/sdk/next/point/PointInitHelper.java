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
package com.lib.sdk.next.point;

import com.lib.sdk.next.base.IBaseHelper;
import com.lib.sdk.next.base.NxMap;
import com.lib.sdk.next.o.http.HttpResponse;
import com.lib.sdk.next.o.map.bean.PositionPointBean;
import com.lib.sdk.next.global.GlobalOperate;
import com.lib.sdk.next.o.map.manager.ProjectCacheManager;
import com.lib.sdk.next.o.map.net.HttpUri;
import com.lib.sdk.next.o.map.util.PositionUtil;
import com.lib.sdk.next.o.map.widget.MapDrawView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * FileName: PointInitHelper
 * Author: zhikai.jin
 * Date: 2021/6/17 14:27
 * Description: 初始点
 */
public class PointInitHelper extends IBaseHelper<PointInitPresenter> implements IPushSyncConfigCallBack {
    private MapDrawView mMapDrawView;

    private int mCurrentOperateType = -1;

    private PositionPointBean mOperatePositionPoint;

    private String mUpdatePointName= "";


    /**
     * 当前初始点
     */

    private List<PositionPointBean> mNavigationList = null;

    /**
     * 所有的点
     */
    private List<PositionPointBean> mAllTypePointList = null;


    public PointInitHelper(NxMap nxMap) {
        super(nxMap, new PointInitPresenter());
        onCreate(nxMap);
    }




    @Override
    public void showErr(String uri, int code, String msg) {

    }

    @Override
    public void attachView(MapDrawView drawView) {
        this.mMapDrawView = drawView;
    }

    @Override
    public void onCreate(NxMap nxMap) {
        super.onCreate(nxMap);
        mNavigationList = new ArrayList<>();
        mAllTypePointList = ProjectCacheManager.getAllPositionInfo(GlobalOperate.getApp(), getProjectId());
        for (int i = 0; i < mAllTypePointList.size(); i++) {
            if (mAllTypePointList.get(i).getType() == PositionPointBean.TYPE_INIT_POINT) {
                mNavigationList.add(mAllTypePointList.get(i));
            }
        }
        initEvent();
    }

    private void initEvent() {


        //编辑地图 --点击地图--  监听事件  Log.e("showSidebar" ,"侧边栏 + 点击事件");
        mMapDrawView.setOnEditedMapListener(new MapDrawView.OnEditedMapListener() {
            @Override
            public void onEditedMap() {

            }

            @Override
            public boolean onEditedMapIntercept() {
                if(mCurrentOperateType == PointOperateType.TYPE_SAVE){
                    return false;
                }
                return true;

            }
        });

        //对MapDrawView地图上的点的点击监听
        mMapDrawView.setOnPositionPointClickListener(new MapDrawView.OnPositionPointClickListener() {
            @Override
            public void onClick(PositionPointBean positionPointBean) {
                mOperatePositionPoint  = positionPointBean;
                mMapDrawView.setEditType(MapDrawView.TYPE_EDIT_TYPE_EDIT_POINT);
                mMapDrawView.setEditOperatePoint(positionPointBean);
            }
        });



        //对MapDrawView 地图上的导航点的编辑完成的监听
        mMapDrawView.setOnEditNavigationPointFinishListener(new MapDrawView.OnEditNavigationPointFinishListener() {
            @Override
            public void onFinish(PositionPointBean positionPointBean) {

                PositionPointBean operatePointOrigin = PositionUtil.getPositionPointByName(mMapDrawView.getPositionPointList(), positionPointBean);

                if (PositionUtil.positionPointChanged(positionPointBean, operatePointOrigin)) {

                    PositionPointBean editNavigationPoint = mMapDrawView.getCurrentOperatePoint();
                    List<PositionPointBean> positionPointBeanList = mMapDrawView.getPositionPointList();

                    if (editNavigationPoint != null) {
                        for (int i = 0; i < positionPointBeanList.size(); i++) {
                            if (positionPointBeanList.get(i).getPointName().equals(editNavigationPoint.getPointName()) && editNavigationPoint.getType() == positionPointBeanList.get(i).getType()) {
                                positionPointBeanList.set(i, editNavigationPoint);
                                mAllTypePointList.set(i, editNavigationPoint);
                                break;
                            }
                        }
                    }

                    for (int i = 0; i < mNavigationList.size(); i++) {
                        if (mNavigationList.get(i).getPointName().equals(editNavigationPoint.getPointName())
                                && editNavigationPoint.getType() == mNavigationList.get(i).getType()) {
                            mNavigationList.set(i, editNavigationPoint);
                        }
                    }


                    //更新地图显示
                    mMapDrawView.setEditOperatePoint(positionPointBean);
                    mMapDrawView.initPositionPointList(mAllTypePointList);


                    //保存到服务器
                    pushSyncConfig(PointOperateType.TYPE_EDIT_FINISH, mAllTypePointList, editNavigationPoint, "");
                }
            }
        });

    }

    /**
     * 添加导航点
     * @param pointName
     */
    public void onAddPoint(String pointName) {
        mCurrentOperateType = PointOperateType.TYPE_SAVE;
        mMapDrawView.setEditType(MapDrawView.TYPE_EDIT_TYPE_ADD_INIT_POINT);
        mMapDrawView.setCurrentOperatePoint(null);
        mMapDrawView.setCurrentOperatePointName(pointName);
        //清楚地图不可编辑
        for (PositionPointBean positionPointBean : mMapDrawView.getPositionPointList()) {
            positionPointBean.setNeedShowName(false);
        }
        PositionPointBean robotPoint = robotToPoint();
        mMapDrawView.setCurrentOperatePoint(null);
        mMapDrawView.setTouchPosition(robotPoint.getBitmapX(), robotPoint.getBitmapY());
        mMapDrawView.setPositionLocalTheta(robotPoint.getTheta());
        mMapDrawView.simulateClick();
        mMapDrawView.refresh();
        PositionPointBean addNavigationPoint = mMapDrawView.getCurrentOperatePoint();

        if (addNavigationPoint != null) {

            //保存到服务器的点
            List<PositionPointBean> updatePositionList = new ArrayList<>();
            for (PositionPointBean positionPointBean : mAllTypePointList) {
                updatePositionList.add(positionPointBean);
            }
            updatePositionList.add(addNavigationPoint);

            pushSyncConfig(PointOperateType.TYPE_SAVE, updatePositionList,addNavigationPoint,"");
        }
    }



    /**
     * 更改导航点名称
     * @param pointName
     */
    public void onUpdatePointName(String pointName) {

        if (mOperatePositionPoint == null) {
            return;
        }

        if (!PositionUtil.isPositionPointNameExist(pointName, mAllTypePointList) || mOperatePositionPoint.getPointName().equals(pointName))
        {
            List<PositionPointBean> updatePositionList = new ArrayList<>();
            for (PositionPointBean pointBean : mAllTypePointList) {
                PositionPointBean newPositionPoint = new PositionPointBean();
                newPositionPoint.setPointName(pointBean.getPointName());
                newPositionPoint.setType(pointBean.getType());
                newPositionPoint.setBitmapX(pointBean.getBitmapX());
                newPositionPoint.setBitmapY(pointBean.getBitmapY());
                newPositionPoint.setTheta(pointBean.getTheta());
                updatePositionList.add(newPositionPoint);
            }

            //修改该点名字
            for (PositionPointBean pointBean : updatePositionList) {
                if (pointBean.getPointName().equals(mOperatePositionPoint.getPointName())) {
                    //0:导航点 1:初始点 2：充电点 3：待命点
                    if (pointBean.getType() == PositionPointBean.TYPE_CHARGE_POINT)
                        pointBean.setPointName(pointName);
                        break;
                    }

                }

            pushSyncConfig(PointOperateType.TYPE_UPDATE_NAME, updatePositionList, mOperatePositionPoint,pointName);
        }

    }


    /**
     * 删除点
     * @param
     */
    public void onDeletePoint() {

        if (mOperatePositionPoint == null) {
            return;
        }

        List<PositionPointBean> updatePositionList = new ArrayList<>();
        for (PositionPointBean pointBean : mAllTypePointList) {
            updatePositionList.add(pointBean);
        }
        PositionUtil.deletePositionPoint(mOperatePositionPoint, updatePositionList);
        pushSyncConfig(PointOperateType.TYPE_DELETE, updatePositionList, mOperatePositionPoint,  "");

    }





    /**
     * 航点保存至服务器
     */
    private void pushSyncConfig(int operateType, final List<PositionPointBean> updatePositionList, PositionPointBean operatePositionPoint,final String pointName) {
        this.mCurrentOperateType = operateType;
        this.mOperatePositionPoint = operatePositionPoint;
        this.mUpdatePointName = pointName;
        JSONObject params = new JSONObject();
        try {
            String mTempProjectContent = ProjectCacheManager.getUpdateProjectStampContent(GlobalOperate.getApp(), getProjectId());
            params.put("project_info", new JSONObject(mTempProjectContent));
            params.put("obstacles", new JSONObject(ProjectCacheManager.getObstacleCacheFileOriContent(GlobalOperate.getApp(), getProjectId())));
            params.put("positions", new JSONObject(PositionUtil.positionPointListToServerJsonStr(GlobalOperate.getApp(), getProjectId(), updatePositionList)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mPresenter.pushSyncConfig(HttpUri.URL_PUSH_SYNC_CONFIG, params);
    }


    @Override
    public void pushSyncConfigDataCallBack(HttpResponse response) {
        String mTempProjectContent = ProjectCacheManager.getUpdateProjectStampContent(GlobalOperate.getApp(), getProjectId());
        if (response.code == 0) {
            switch (mCurrentOperateType){
                case PointOperateType.TYPE_SAVE:
                    PositionPointBean addNavigationPoint = mOperatePositionPoint;
                    mAllTypePointList.add(addNavigationPoint);
                    //更新地图显示
                    mMapDrawView.setCurrentOperatePoint(null);
                    mMapDrawView.initPositionPointList(mAllTypePointList);
                    //本地更新标记点信息
                    String positionInfoJson = PositionUtil.positionPointListToServerJsonStr(GlobalOperate.getApp(), getProjectId(), mAllTypePointList);
                    ProjectCacheManager.updatePositionInfoFile(GlobalOperate.getApp(), getProjectId(), positionInfoJson);
                    ProjectCacheManager.updateProject(GlobalOperate.getApp(), getProjectId(), mTempProjectContent);
                    break;

                case PointOperateType.TYPE_EDIT_FINISH:
                    //本地更新标记点信息
                    positionInfoJson = PositionUtil.positionPointListToServerJsonStr(GlobalOperate.getApp(), getProjectId(), mAllTypePointList);
                    ProjectCacheManager.updatePositionInfoFile(GlobalOperate.getApp(), getProjectId(), positionInfoJson);
                    ProjectCacheManager.updateProject(GlobalOperate.getApp(), getProjectId(), mTempProjectContent);
                    break;

                case PointOperateType.TYPE_UPDATE_NAME:
                    //如果地图当前操作点，就是该点，则需要更新当前操作点名字
                    if (mMapDrawView.getCurrentOperatePoint() != null) {
                        if (mMapDrawView.getCurrentOperatePoint().getPointName().equals(mOperatePositionPoint.getPointName())) {
                            if (mMapDrawView.getCurrentOperatePoint().getType() == PositionPointBean.TYPE_INIT_POINT) {
                                mMapDrawView.setCurrentOperatePointName(mUpdatePointName);
                            }
                        }
                    }

                    //修改该点名字
                    for (PositionPointBean pointBean : mAllTypePointList) {
                        if (pointBean.getPointName().equals(mOperatePositionPoint.getPointName())) {
                            if (mMapDrawView.getCurrentOperatePoint().getType() == PositionPointBean.TYPE_INIT_POINT) {
                                pointBean.setPointName(mUpdatePointName);
                                break;
                            }
                        }
                    }
                    //本地更新标记点信息
                    positionInfoJson = PositionUtil.positionPointListToServerJsonStr(GlobalOperate.getApp(), getProjectId(), mAllTypePointList);
                    ProjectCacheManager.updatePositionInfoFile(GlobalOperate.getApp(), getProjectId(), positionInfoJson);
                    ProjectCacheManager.updateProject(GlobalOperate.getApp(), getProjectId(), mTempProjectContent);
                    //更新地图显示
                    mMapDrawView.refresh();
                    break;

                case PointOperateType.TYPE_DELETE:
                    if (mOperatePositionPoint != null
                            && mOperatePositionPoint.getPointName().equals(mOperatePositionPoint.getPointName())
                            && mOperatePositionPoint.getType() == mOperatePositionPoint.getType()) {

                        //删除该点
                        PositionUtil.deletePositionPoint(mOperatePositionPoint, mAllTypePointList);
                        PositionUtil.deletePositionPoint(mOperatePositionPoint, mNavigationList);

                        //更新地图显示
                        mMapDrawView.setCurrentOperatePoint(null);
                        mMapDrawView.initPositionPointList(mAllTypePointList);

                        //本地更新标记点信息
                        positionInfoJson = PositionUtil.positionPointListToServerJsonStr(GlobalOperate.getApp(), getProjectId(), mAllTypePointList);
                        ProjectCacheManager.updatePositionInfoFile(GlobalOperate.getApp(), getProjectId(), positionInfoJson);
                        ProjectCacheManager.updateProject(GlobalOperate.getApp(), getProjectId(), mTempProjectContent);
                        mOperatePositionPoint = null;
                    }
                    break;


                default:break;
            }
        }
    }

    /**
     * 关闭操作
     */
    public void closeOperate(){
        mMapDrawView.setEditType(MapDrawView.TYPE_EDIT_INIT);
    }
}

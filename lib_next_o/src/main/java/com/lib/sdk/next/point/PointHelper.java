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

import android.util.Log;

import com.lib.sdk.next.NextResultInfo;
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
 * FileName: PoinitHelper
 * Author: zhikai.jin
 * Date: 2021/6/22 9:17
 * Description:
 */
public class PointHelper extends IBaseHelper<PointPresenter> implements IPushSyncConfigCallBack {

    private int mPointType = POINT_NAV_TYPE;


    private int mMapEdType = -1;

    private INavPointListener mINavPointListener;


    /**
     * 初始点
     */
    public static final int POINT_INIT_TYPE = 0;


    /**
     * 充电点
     */
    public static final int POINT_CHARGE_TYPE = 1;


    /**
     * 导航点
     */
    public static final int POINT_NAV_TYPE = 2;


    /**
     * 待命点
     */
    public static final int POINT_STANDBY_TYPE = 3;


    /**
     * 当前操作的类型点
     */
    private List<PositionPointBean> mNavigationList = new ArrayList();

    /**
     * 所有的点
     */
    private List<PositionPointBean> mAllTypePointList = new ArrayList<>();


    private MapDrawView mMapDrawView;

    private PointOption mPointOption = new PointOption();


    public PointHelper(NxMap nxMap, int type) {
        super(nxMap, new PointPresenter());
        setPointType(type);
        setEdType(type);
        onCreate(nxMap);
    }


    @Override
    public void showErr(String uri, String msg) {

    }

    @Override
    public void attachView(MapDrawView drawView) {
        this.mMapDrawView = drawView;
    }

    @Override
    public void onCreate(NxMap nxMap) {
        super.onCreate(nxMap);
        mNavigationList.clear();
        mAllTypePointList = ProjectCacheManager.getAllPositionInfo(GlobalOperate.getApp(), getProjectId());
        for (int i = 0; i < mAllTypePointList.size(); i++) {
            for (PositionPointBean positionPointBean : mAllTypePointList) {
                Log.e("pointtype", "type =" + positionPointBean.getType());
                if (getPointType() == POINT_NAV_TYPE && positionPointBean.getType() == PositionPointBean.TYPE_NAVIGATION_POINT) {
                    mNavigationList.add(positionPointBean);
                } else if (getPointType() == POINT_INIT_TYPE && positionPointBean.getType() == PositionPointBean.TYPE_INIT_POINT) {
                    mNavigationList.add(positionPointBean);
                } else if (getPointType() == POINT_CHARGE_TYPE && positionPointBean.getType() == PositionPointBean.TYPE_CHARGE_POINT) {
                    mNavigationList.add(positionPointBean);
                } else if (getPointType() == POINT_STANDBY_TYPE && positionPointBean.getType() == PositionPointBean.TYPE_STANDBY_POINT) {
                    mNavigationList.add(positionPointBean);
                }
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
                if (mPointOption.getOperateType() != PointOperateType.TYPE_SAVE) {
                    return false;
                }

                if (getPointType() == POINT_NAV_TYPE || getPointType() == POINT_STANDBY_TYPE) {
                    if (mPointOption.getAction() == PointOption.ACTION_TOUCH) {
                        mMapDrawView.simulateClick();
                        mMapDrawView.refresh();
                        requestUploadPoints();
                    }
                }
                return true;
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
                    //更新当前使用的点
                    mPointOption.setCurrentOperatePoint(positionPointBean);
                    //保存到服务器
                    pushSyncConfig(mAllTypePointList);
                }
            }
        });

    }

    /**
     * 添加导航点
     *
     * @param pointName
     */
    protected void onAddPointByRobot(String pointName) {
        mPointOption = new PointOption();
        mPointOption.setAction(PointOption.ACTION_ROBOT);
        mPointOption.setOperateType(PointOperateType.TYPE_SAVE);
        mPointOption.setEdType(getMapEdType());
        mPointOption.setPointName(pointName);
        mPointOption.setCurrentOperatePoint(null);
        setMapDrawPoint();
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


        requestUploadPoints();
    }


    /**
     * 通过触摸添加点
     *
     * @param pointName
     */
    protected void onAddPointByTouch(String pointName) {

        mPointOption = new PointOption();
        mPointOption.setAction(PointOption.ACTION_TOUCH);
        mPointOption.setOperateType(PointOperateType.TYPE_SAVE);
        mPointOption.setEdType(getMapEdType());
        mPointOption.setPointName(pointName);
        mPointOption.setCurrentOperatePoint(null);
        setMapDrawPoint();
    }


    /**
     * 编辑点
     */
    public void edPoint(PositionPointBean pointBean) {
        mPointOption = new PointOption();
        mPointOption.setOperateType(PointOperateType.TYPE_EDIT_FINISH);
        mPointOption.setEdType(MapDrawView.TYPE_EDIT_TYPE_EDIT_POINT);
        mPointOption.setPointName("");
        mPointOption.setAction(PointOption.ACTION_NULL);
        mPointOption.setCurrentOperatePoint(pointBean);
        mMapDrawView.setEditType(MapDrawView.TYPE_EDIT_TYPE_EDIT_POINT);
    }


    /**
     * 删除某个点
     *
     * @param pointBean
     */
    protected void deletePoint(PositionPointBean pointBean) {
        mPointOption = new PointOption();
        mPointOption.setOperateType(PointOperateType.TYPE_DELETE);
        mPointOption.setEdType(MapDrawView.TYPE_EDIT_INIT);
        mPointOption.setPointName("");
        mPointOption.setAction(PointOption.ACTION_NULL);
        mPointOption.setCurrentOperatePoint(pointBean);
        mMapDrawView.setEditType(MapDrawView.TYPE_EDIT_INIT);
        List<PositionPointBean> updatePositionList = new ArrayList<>();
        for (PositionPointBean pointEntry : mAllTypePointList) {
            updatePositionList.add(pointEntry);
        }
        PositionUtil.deletePositionPoint(pointBean, updatePositionList);
        pushSyncConfig(updatePositionList);
    }


    /**
     * 更改点的名称
     *
     * @param pointBean
     * @param pointName
     */
    protected void updatePointName(PositionPointBean pointBean, String pointName) {
        mPointOption = new PointOption();
        mPointOption.setOperateType(PointOperateType.TYPE_UPDATE_NAME);
        mPointOption.setEdType(MapDrawView.TYPE_EDIT_INIT);
        mPointOption.setPointName(pointName);
        mPointOption.setAction(PointOption.ACTION_NULL);
        mPointOption.setCurrentOperatePoint(pointBean);
        mMapDrawView.setEditType(MapDrawView.TYPE_EDIT_INIT);
        List<PositionPointBean> updatePositionList = new ArrayList<>();
        for (PositionPointBean pointEntry : mAllTypePointList) {
            updatePositionList.add(pointEntry);
        }
        for (PositionPointBean pointEntry : mAllTypePointList) {
            PositionPointBean newPositionPoint = new PositionPointBean();
            newPositionPoint.setPointName(pointEntry.getPointName());
            newPositionPoint.setType(pointEntry.getType());
            newPositionPoint.setBitmapX(pointEntry.getBitmapX());
            newPositionPoint.setBitmapY(pointEntry.getBitmapY());
            newPositionPoint.setTheta(pointEntry.getTheta());
            updatePositionList.add(newPositionPoint);
        }

        //修改该点名字
        for (PositionPointBean pointEntry : updatePositionList) {

            if (pointEntry.getPointName().equals(pointBean.getPointName())) {
                //0:导航点 1:初始点 2：充电点 3：待命点
                if ((getPointType() == POINT_NAV_TYPE && pointEntry.getType() == PositionPointBean.TYPE_NAVIGATION_POINT) ||
                        (getPointType() == POINT_INIT_TYPE && pointEntry.getType() == PositionPointBean.TYPE_INIT_POINT) ||
                        (getPointType() == POINT_CHARGE_TYPE && pointEntry.getType() == PositionPointBean.TYPE_CHARGE_POINT) ||
                        getPointType() == POINT_STANDBY_TYPE && pointEntry.getType() == PositionPointBean.TYPE_STANDBY_POINT) {
                    pointEntry.setPointName(mPointOption.getPointName());
                    break;
                }

            }
        }
        pushSyncConfig(updatePositionList);
    }


    /**
     * 请求上传所有的点
     */
    private void requestUploadPoints() {
        if (!PositionUtil.isPositionPointNameExist(mPointOption.getPointName(), mAllTypePointList)) {
            mPointOption.setCurrentOperatePoint(mMapDrawView.getCurrentOperatePoint());
            if (mPointOption.getCurrentOperatePoint() != null) {
                //保存到服务器的点
                List<PositionPointBean> updatePositionList = new ArrayList<>();
                for (PositionPointBean positionPointBean : mAllTypePointList) {
                    updatePositionList.add(positionPointBean);
                }
                updatePositionList.add(mPointOption.getCurrentOperatePoint());
                pushSyncConfig(updatePositionList);
            }
        }
    }

    private void setMapDrawPoint() {
        mMapDrawView.setEditType(mPointOption.getEdType());
        mMapDrawView.setCurrentOperatePoint(mPointOption.getCurrentOperatePoint());
        mMapDrawView.setCurrentOperatePointName(mPointOption.getPointName());
    }


    /**
     * 航点保存至服务器
     */
    private void pushSyncConfig(final List<PositionPointBean> updatePositionList) {
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

    private void setPointType(int type) {
        mPointType = type;
    }

    public int getPointType() {
        return mPointType;
    }

    private void setEdType(int pointType) {
        int edType = 0;
        switch (pointType) {
            case POINT_NAV_TYPE:
                edType = MapDrawView.TYPE_EDIT_TYPE_ADD_NAVIGATION;
                break;
            case POINT_INIT_TYPE:
                edType = MapDrawView.TYPE_EDIT_TYPE_ADD_INIT_POINT;
                break;

            case POINT_CHARGE_TYPE:
                edType = MapDrawView.TYPE_EDIT_TYPE_ADD_CHARGE;
                break;

            case POINT_STANDBY_TYPE:
                edType = MapDrawView.TYPE_EDIT_TYPE_ADD_STANDBY_POINT;
                break;
        }

        mMapEdType = edType;
    }

    private int getMapEdType() {
        return mMapEdType;
    }


    @Override
    public void pushSyncConfigDataCallBack(HttpResponse response) {
        String mTempProjectContent = ProjectCacheManager.getUpdateProjectStampContent(GlobalOperate.getApp(), getProjectId());
        if (response.code == 0) {
            String positionInfoJson = "";
            switch (mPointOption.getOperateType()) {
                case PointOperateType.TYPE_SAVE:
                    mAllTypePointList.add(mPointOption.getCurrentOperatePoint());
                    mNavigationList.add(mPointOption.getCurrentOperatePoint());

                    //更新地图显示
                    mMapDrawView.setCurrentOperatePoint(null);
                    mMapDrawView.initPositionPointList(mAllTypePointList);

                    //本地更新标记点信息
                    positionInfoJson = PositionUtil.positionPointListToServerJsonStr(GlobalOperate.getApp(), getProjectId(), mAllTypePointList);
                    ProjectCacheManager.updatePositionInfoFile(GlobalOperate.getApp(), getProjectId(), positionInfoJson);
                    ProjectCacheManager.updateProject(GlobalOperate.getApp(), getProjectId(), mTempProjectContent);

                    if(mINavPointListener!=null){
                        mINavPointListener.onCreateNavPointSuccess(mPointOption.getCurrentOperatePoint());
                    }

                    break;
                case PointOperateType.TYPE_EDIT_FINISH:
                    //本地更新标记点信息
                    positionInfoJson = PositionUtil.positionPointListToServerJsonStr(GlobalOperate.getApp(), getProjectId(), mAllTypePointList);
                    ProjectCacheManager.updatePositionInfoFile(GlobalOperate.getApp(), getProjectId(), positionInfoJson);
                    String mTempProjectContent1 = ProjectCacheManager.getUpdateProjectStampContent(GlobalOperate.getApp(), getProjectId());
                    ProjectCacheManager.updateProject(GlobalOperate.getApp(), getProjectId(), mTempProjectContent1);

                    if(mINavPointListener!=null){
                        mINavPointListener.onEdNavPointSuccess(mPointOption.getCurrentOperatePoint());
                    }
                    break;
                case PointOperateType.TYPE_DELETE:
                    //删除该点
                    PositionUtil.deletePositionPoint(mPointOption.getCurrentOperatePoint(), mAllTypePointList);
                    PositionUtil.deletePositionPoint(mPointOption.getCurrentOperatePoint(), mNavigationList);


                    //更新地图显示
                    mMapDrawView.setCurrentOperatePoint(null);
                    mMapDrawView.initPositionPointList(mAllTypePointList);
                    //本地更新标记点信息
                    positionInfoJson = PositionUtil.positionPointListToServerJsonStr(GlobalOperate.getApp(), getProjectId(), mAllTypePointList);
                    ProjectCacheManager.updatePositionInfoFile(GlobalOperate.getApp(), getProjectId(), positionInfoJson);
                    ProjectCacheManager.updateProject(GlobalOperate.getApp(), getProjectId(), mTempProjectContent);

                    if(mINavPointListener!=null){
                        mINavPointListener.onDeleteSuccess();
                    }
                    break;
                case PointOperateType.TYPE_UPDATE_NAME:
                    //如果地图当前操作点，就是该点，则需要更新当前操作点名字

                    if (mMapDrawView.getCurrentOperatePoint() != null) {
                        if (mMapDrawView.getCurrentOperatePoint().getPointName().equals(mPointOption.getCurrentOperatePoint().getPointName())) {
                            if ((getPointType() == POINT_NAV_TYPE && mMapDrawView.getCurrentOperatePoint().getType() == PositionPointBean.TYPE_NAVIGATION_POINT)
                                    || (getPointType() == POINT_INIT_TYPE && mMapDrawView.getCurrentOperatePoint().getType() == PositionPointBean.TYPE_INIT_POINT)
                                    || (getPointType() == POINT_CHARGE_TYPE && mMapDrawView.getCurrentOperatePoint().getType() == PositionPointBean.TYPE_CHARGE_POINT)
                                    || (getPointType() == POINT_STANDBY_TYPE && mMapDrawView.getCurrentOperatePoint().getType() == PositionPointBean.TYPE_STANDBY_POINT)) {
                                mMapDrawView.setCurrentOperatePointName(mPointOption.getPointName());
                            }
                        }
                    }

                    //修改该点名字
                    for (PositionPointBean pointBean : mAllTypePointList) {
                        if (pointBean.getPointName().equals(mPointOption.getCurrentOperatePoint().getPointName())) {
                            if ((getPointType() == POINT_NAV_TYPE && mMapDrawView.getCurrentOperatePoint().getType() == PositionPointBean.TYPE_NAVIGATION_POINT)
                                    || (getPointType() == POINT_INIT_TYPE && mMapDrawView.getCurrentOperatePoint().getType() == PositionPointBean.TYPE_INIT_POINT)
                                    || (getPointType() == POINT_CHARGE_TYPE && mMapDrawView.getCurrentOperatePoint().getType() == PositionPointBean.TYPE_CHARGE_POINT)
                                    || (getPointType() == POINT_STANDBY_TYPE && mMapDrawView.getCurrentOperatePoint().getType() == PositionPointBean.TYPE_STANDBY_POINT)) {
                                pointBean.setPointName(mPointOption.getPointName());
                                break;
                            }
                        }
                    }

                    //更新地图显示
                    mMapDrawView.refresh();

                    //本地更新标记点信息
                    positionInfoJson = PositionUtil.positionPointListToServerJsonStr(GlobalOperate.getApp(), getProjectId(), mAllTypePointList);
                    ProjectCacheManager.updatePositionInfoFile(GlobalOperate.getApp(), getProjectId(), positionInfoJson);
                    ProjectCacheManager.updateProject(GlobalOperate.getApp(), getProjectId(), mTempProjectContent);

                    mPointOption.getCurrentOperatePoint().setPointName(mPointOption.getPointName());
                    if(mINavPointListener!=null){
                        mINavPointListener.onUpdateNameSuccess(mPointOption.getCurrentOperatePoint());
                    }
                    break;
            }

        }
        else{
            switch (mPointOption.getOperateType()) {
                case PointOperateType.TYPE_SAVE:
                    if(mINavPointListener!=null){
                        mINavPointListener.onCreateNavPointFail(new NextResultInfo(response.code,response.info));
                    }
                    break;
                case PointOperateType.TYPE_EDIT_FINISH:
                    //本地更新标记点信息
                    if(mINavPointListener!=null){
                        mINavPointListener.onEdNavPointFail(new NextResultInfo(response.code,response.info));
                    }
                    break;
                case PointOperateType.TYPE_DELETE:
                    //删除该点
                    if(mINavPointListener!=null){
                        mINavPointListener.onDeleteFail(new NextResultInfo(response.code,response.info));
                    }
                    break;
                case PointOperateType.TYPE_UPDATE_NAME:
                    if(mINavPointListener!=null){
                        mINavPointListener.onUpdateNameFail(new NextResultInfo(response.code,response.info));
                    }
                    break;
            }
        }

        mMapDrawView.setEditType(MapDrawView.TYPE_EDIT_INIT);
        mPointOption = null;
    }

    public void setNavPointListener(INavPointListener navPointListener){
        this.mINavPointListener = navPointListener;
    }

    public interface INavPointListener{

        void onCreateNavPointSuccess(PositionPointBean pointBean);

        void onCreateNavPointFail(NextResultInfo resultInfo);

        void onEdNavPointSuccess(PositionPointBean pointBean);

        void onEdNavPointFail(NextResultInfo resultInfo);

        void onUpdateNameSuccess(PositionPointBean pointBean);

        void onUpdateNameFail(NextResultInfo resultInfo);

        void onDeleteSuccess();

        void onDeleteFail(NextResultInfo resultInfo);

    }
}

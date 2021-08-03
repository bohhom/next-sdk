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
package com.lib.sdk.next.gps;

import com.bozh.logger.Logger;
import com.lib.sdk.next.NextResultInfo;
import com.lib.sdk.next.base.IBaseHelper;
import com.lib.sdk.next.base.NxMap;
import com.lib.sdk.next.o.R;
import com.lib.sdk.next.o.http.HttpResponse;
import com.lib.sdk.next.o.map.bean.PositionPointBean;
import com.lib.sdk.next.global.GlobalOperate;
import com.lib.sdk.next.o.map.manager.ProjectCacheManager;
import com.lib.sdk.next.o.map.net.HttpUri;
import com.lib.sdk.next.o.map.util.PositionUtil;
import com.lib.sdk.next.o.map.util.ToastUtil;
import com.lib.sdk.next.o.map.widget.MapDrawView;
import com.lib.sdk.next.point.PointPresenter;

import java.util.HashMap;

/**
 * FileName: GpsOptions
 * Author: zhikai.jin
 * Date: 2021/6/11 11:25
 * Description: 定位模块
 */
public class LocationHelper extends IBaseHelper<RobotPresenter> implements  InitLocationCallBack{
    //初始化定位变量  0 :智能初始化定位 1:强制初始化 2：选点初始化 （由于布局第一个为智能初始化定位，所以默认值设置为0）
    private int mInitLocationType = SMART ;

    /**
     * 智能初始化定位
     */
    public static final int  SMART = 0;

    /**
     * 强制初始化
     */
    public static final int  ENFORCE = 1;

    /**
     * 选点初始化
     */
    public static final int  SELECT = 2;

    private OnLocationListener mLocationListener;


    public LocationHelper(NxMap nxMap) {
        super(nxMap, new RobotPresenter());
        onCreate(nxMap);
        Logger.d("创建初始化定位操作");
    }

    public LocationHelper setInitPointType(int type){
        this.mInitLocationType = type;
        return this;
    }

    public int getInitPointType(){
        return  mInitLocationType;
    }


    /**
     * 强制点操作
     * @param projectId
     * @param positionPointBean
     */
    public void  initLocationForce(String projectId,PositionPointBean positionPointBean){
        Logger.d("强制初始化定位操作");
        CoordEntry entry =  initCoord(projectId,positionPointBean);
        HashMap<String, Object> params = new HashMap<>();
        params.put("x", entry.getX());
        params.put("y", entry.getY());
        params.put("theta", entry.getTheta());
        mPresenter.initLocationForce(HttpUri.URL_INIT_LOCATION_FORCE, params);
    }



    @Override
    public void showErr(String uri, int code, String msg) {

    }

    @Override
    public void attachView(MapDrawView drawView) {

    }

    @Override
    public void initLocationDataCallBack(HttpResponse data) {
        try {
            if (data.code == 0) {
                mLocationListener.onSuccess(mInitLocationType);
            } else {
                mLocationListener.onFail(mInitLocationType, new NextResultInfo(data.code, data.info));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * 初始化定位
     */
    public void initLocation(String projectId,PositionPointBean positionPointBean) {
        Logger.d("智能初始化定位操作");
        CoordEntry entry =  initCoord(projectId,positionPointBean);
        HashMap<String, Object> params = new HashMap<>();
        params.put("x", entry.getX());
        params.put("y", entry.getY());
        params.put("theta", entry.getTheta());
        mPresenter.initLocation(HttpUri.URL_INIT_LOCATION, params);
    }


    /**
     * 选点初始化定位
     * @param positionPointBean
     */
    public void initLocationChoose(String projectId,PositionPointBean positionPointBean){
        Logger.d("选点初始化定位操作");
        CoordEntry entry =  initCoord(projectId,positionPointBean);
        HashMap<String, Object> params = new HashMap<>();
        params.put("x", entry.getX());
        params.put("y", entry.getY());
        params.put("theta", entry.getTheta());
        mPresenter.initLocationForce(HttpUri.URL_INIT_LOCATION_FORCE, params);
    }


    /**
     * 获取坐标值
     * @param projectId
     * @param positionPointBean
     * @return
     */
    private CoordEntry initCoord(String projectId,PositionPointBean positionPointBean) {
        CoordEntry entry = new CoordEntry();
        double resolution = ProjectCacheManager.getMapResolution(GlobalOperate.getApp(),projectId);
        double originX = ProjectCacheManager.getMapOriginX(GlobalOperate.getApp(), projectId);
        double originY = ProjectCacheManager.getMapOriginY(GlobalOperate.getApp(), projectId);

        double serverTheta = 0;
        if (positionPointBean.getTheta() >= -Math.PI / 2 && positionPointBean.getTheta() <= 0) {
            serverTheta = Math.PI / 2 - positionPointBean.getTheta();
        }

        if (positionPointBean.getTheta() > 0 && positionPointBean.getTheta() <= Math.PI) {
            serverTheta = Math.PI / 2 - positionPointBean.getTheta();
        }

        if (positionPointBean.getTheta() >= -Math.PI && positionPointBean.getTheta() <= -Math.PI / 2) {
            serverTheta = -Math.PI * 3f / 2 - positionPointBean.getTheta();
        }
        double x = PositionUtil.localToServerX(positionPointBean.getBitmapX(), resolution, originX);
        double y = PositionUtil.localToServerY(positionPointBean.getBitmapY(), resolution, originY);
        entry.setX(x);
        entry.setY(y);
        entry.setTheta(serverTheta);
        return entry;
    }


    public void setLocationListener(LocationHelper.OnLocationListener locationListener) {
        this.mLocationListener = locationListener;
    }
    public interface OnLocationListener {
        void onSuccess(int type);

        void onFail(int type,NextResultInfo info);
    }
}

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
package com.lib.sdk.next.base;
import com.lib.sdk.next.IMapDrawListener;
import com.lib.sdk.next.IMapTouchListener;
import com.lib.sdk.next.global.GlobalOperate;
import com.lib.sdk.next.gps.LocationHelper;
import com.lib.sdk.next.o.map.bean.PositionPointBean;
import com.lib.sdk.next.o.map.bean.VirtualWallBean;
import com.lib.sdk.next.o.map.manager.ProjectCacheManager;

import java.util.List;

/**
 * FileName: NxMap
 * Author: zhikai.jin
 * Date: 2021/6/11 13:49
 * Description:
 */
public class NxMap extends MapDelegate {
    private INmap iNmap;

    private IBaseHelper helper;

    private String mProjectId = "";


    public NxMap(INmap paramINmap) {
        this.iNmap = paramINmap;
    }

    private INmap iNmap() {

        return iNmap;
    }

    /**
     * 展示页面
     *
     * @param projectId
     */
    public void onShowMapView(String projectId) {
        iNmap().showMap(projectId);
        setProjectId(projectId);
    }

    public void setRobotPosition(LocationHelper helper) {
        iNmap().setRobotPosition(helper);
    }

    public void setRobotHelper(IBaseHelper helper) {

        this.helper = helper;
    }

    @Override
    public void bindHelper(IBaseHelper helper) {
        helper.attachView(iNmap.getMapDrawView());
    }

    private void setProjectId(String projectId) {
        this.mProjectId = projectId;
    }

    /**
     * 获取当前工程id
     *
     * @return
     */
    public String getProjectId() {
        return mProjectId;
    }

    /**
     * 获取选中的点
     *
     * @return
     */
    public PositionPointBean getPoint() {
        return iNmap().getPoint();
    }

    /**
     * 获取选中的虚拟墙
     *
     * @return
     */
    public VirtualWallBean getVirtualWall() {
        return iNmap().getVirtualWall();
    }

    public void onRockerViewVisible() {
        iNmap().onRockViewVisible();
    }

    public void onRockerViewHide() {
        iNmap().onRockViewHide();
    }

    public void setOnDrawListener(IMapDrawListener drawListener){
        iNmap().setOnMapDrawListener(drawListener);
    }

    public void setOnMapTouchListener(IMapTouchListener mapTouchListener){
        iNmap().setOnMapTouchListener(mapTouchListener);
    }

    /**
     * 获取当前工程所有点
     *
     * @return
     */
    public List<PositionPointBean> getCurrentAllPositions() {
        return ProjectCacheManager.getAllPositionInfo(GlobalOperate.getApp(), getProjectId());
    }
}

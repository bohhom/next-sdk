
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

import com.lib.sdk.next.base.NxMap;
import com.lib.sdk.next.o.map.bean.PositionPointBean;

import java.util.List;

/**
 * FileName: PointNavHelper
 * Author: zhikai.jin
 * Date: 2021/6/22 10:30
 * Description: 导航点
 */
public class PointNavHelper extends PointHelper{
    public PointNavHelper(NxMap nxMap) {
        super(nxMap,POINT_NAV_TYPE);
    }

    public void onAddPointTouch(String pointName){
        onAddPointByTouch(pointName);
    }

    public void onSavePointTouch(){
        savePointByTouch();
    }

    public void onAddPointRobot(String pointName){
        onAddPointByRobot(pointName);
    }

    public void onEdPoint(PositionPointBean pointBean){
        edPoint(pointBean);
    }

    public void onDeletePoint(List<PositionPointBean> pointBeans){
        deletePoint(pointBeans);
    }

    public void onUpdatePoint(PositionPointBean pointBean,String pointName){
        updatePointName(pointBean,pointName);
    }


}

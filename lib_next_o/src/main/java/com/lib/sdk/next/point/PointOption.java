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

import com.lib.sdk.next.o.map.bean.PositionPointBean;

import java.util.List;

/**
 * FileName: PointOption
 * Author: zhikai.jin
 * Date: 2021/6/22 14:02
 * Description: 点的配置
 */
public class PointOption {

    public final static  int ACTION_ROBOT = 0;

    public final static  int ACTION_TOUCH = 1;

    public final static  int ACTION_NULL = -1;


    private int action = ACTION_NULL;

    private String pointName ="";

    private int operateType = -1;

    private int edType = -1;

    private PositionPointBean currentOperatePoint;

    private List<PositionPointBean> currentOperatePointList;

    public List<PositionPointBean> getCurrentOperatePointList() {
        return currentOperatePointList;
    }

    public void setCurrentOperatePointList(List<PositionPointBean> currentOperatePointList) {
        this.currentOperatePointList = currentOperatePointList;
    }

    public PositionPointBean getCurrentOperatePoint() {
        return currentOperatePoint;
    }

    public void setCurrentOperatePoint(PositionPointBean currentOperatePoint) {
        this.currentOperatePoint = currentOperatePoint;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public String getPointName() {
        return pointName;
    }

    public void setPointName(String pointName) {
        this.pointName = pointName;
    }

    public int getOperateType() {
        return operateType;
    }

    public void setOperateType(int operateType) {
        this.operateType = operateType;
    }

    public int getEdType() {
        return edType;
    }

    public void setEdType(int edType) {
        this.edType = edType;
    }
}

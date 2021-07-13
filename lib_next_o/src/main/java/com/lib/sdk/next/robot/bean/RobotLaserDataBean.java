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
package com.lib.sdk.next.robot.bean;
/**
 * Created by maqing 2018/11/29 17:29
 * Email：2856992713@qq.com
 * 激光点数据
 */
public class RobotLaserDataBean {
    /**
     * 显示在地图上坐标X
     */
    private float mMapX = 0;
    /**
     * 显示在地图上坐标Y
     */
    private float mMapY = 0;

    /**
     * 服务器上的坐标
     */
    private double mServerX= 0.0;

    private double mServerY =0.0;

    public double getmServerX() {
        return mServerX;
    }

    public void setmServerX(double mServerX) {
        this.mServerX = mServerX;
    }

    public double getmServerY() {
        return mServerY;
    }

    public void setmServerY(double mServerY) {
        this.mServerY = mServerY;
    }



    public float getMapX() {
        return mMapX;
    }

    public void setMapX(float mapX) {
        mMapX = mapX;
    }

    public float getMapY() {
        return mMapY;
    }

    public void setMapY(float mapY) {
        mMapY = mapY;
    }

    @Override
    public String toString() {
        return "RobotLaserDataBean{" +
                "mMapX=" + mMapX +
                ", mMapY=" + mMapY +
                '}';
    }
}


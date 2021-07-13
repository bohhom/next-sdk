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
package com.lib.sdk.next.livedata;

import android.graphics.Bitmap;

import com.lib.sdk.next.o.map.util.FileUtils;

/**
 * FileName: RobotCreateBean
 * Author: zhikai.jin
 * Date: 2021/6/9 13:42
 * Description: 机器人创建地图信息
 */
public class RobotCreateEvent {

    private Bitmap newMapBitMap;

    private double   resolution = 0.0;

    private double   originX = 0.0;

    private double   originY = 0.0;

    private FileUtils.OriginSize originSize;

    public Bitmap getNewMapBitMap() {
        return newMapBitMap;
    }

    public FileUtils.OriginSize getOriginSize() {
        return originSize;
    }

    public void setOriginSize(FileUtils.OriginSize originSize) {
        this.originSize = originSize;
    }

    public void setNewMapBitMap(Bitmap newMapBitMap) {
        this.newMapBitMap = newMapBitMap;
    }

    public double getResolution() {
        return resolution;
    }

    public void setResolution(double resolution) {
        this.resolution = resolution;
    }

    public double getOriginX() {
        return originX;
    }

    public void setOriginX(double originX) {
        this.originX = originX;
    }

    public double getOriginY() {
        return originY;
    }

    public void setOriginY(double originY) {
        this.originY = originY;
    }
}

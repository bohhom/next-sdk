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

/**
 * FileName: RobotRockerEvent
 * Author: zhikai.jin
 * Date: 2021/6/9 17:59
 * Description:
 */
public class RobotRockerEvent {

    /**
     * 虚拟手柄当前角度
     */
    private double currentAngle = 0;
    /**
     * 虚拟手柄当前等级
     */
    private double urrentDistanceLevel = 0;

    public double getCurrentAngle() {
        return currentAngle;
    }

    public void setCurrentAngle(double currentAngle) {
        this.currentAngle = currentAngle;
    }

    public double getUrrentDistanceLevel() {
        return urrentDistanceLevel;
    }

    public void setUrrentDistanceLevel(double urrentDistanceLevel) {
        this.urrentDistanceLevel = urrentDistanceLevel;
    }
}

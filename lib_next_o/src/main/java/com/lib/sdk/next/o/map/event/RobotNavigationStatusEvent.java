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
package com.lib.sdk.next.o.map.event;


import com.lib.sdk.next.robot.bean.RobotNavigationStatusBean;

/**
 * Created by maqing 2018/11/29 15:43
 * Email：2856992713@qq.com
 */
public class RobotNavigationStatusEvent {

    private int  code;
    private RobotNavigationStatusBean mRobotNavigationStatusBean;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public RobotNavigationStatusBean getRobotNavigationStatusBean() {
        return mRobotNavigationStatusBean;
    }

    public void setRobotNavigationStatusBean(RobotNavigationStatusBean robotNavigationStatusBean) {
        mRobotNavigationStatusBean = robotNavigationStatusBean;
    }

}

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
package com.lib.sdk.next.robot.constant;

import com.lib.sdk.next.robot.bean.RobotErrorStatusBean;
import com.lib.sdk.next.robot.bean.RobotStatusBean;

/**
 * FileName: RobotConstant
 * Author: zhikai.jin
 * Date: 2021/6/3 16:01
 * Description:
 */
public class RobotConstant {
    /**
     * 机器人当前状态
     */
    public static RobotStatusBean mRobotStatusBean = new RobotStatusBean();
    /**
     * 机器人错误状态
     */
    public static RobotErrorStatusBean mRobotErrorStatusBean=new RobotErrorStatusBean();

    public static boolean mIsShowDisconnectDialog=false;
}

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

package com.lib.sdk.next.robot;

/**
 * FileName: IRobotStatusCallBack
 * Author: zhikai.jin
 * Date: 2021/6/7 17:45
 * Description: 机器人状态信息
 */
public interface IRobotStatusCallBack {
    /**
     * 机器人状态
     * @param robotStatusInfo
     */
    void onRobotStatus(int code,RobotStatusInfo robotStatusInfo);

    /**
     * 机器人错误码
     * @param robotErrorInfo
     */
    void onRobotError(int code,RobotErrorStatusInfo robotErrorInfo);


    /**
     * 机器人导航状态
     * @param robotNavInfo
     */
    void onRobotNav(int code,RobotNavigationStatusInfo robotNavInfo);
}

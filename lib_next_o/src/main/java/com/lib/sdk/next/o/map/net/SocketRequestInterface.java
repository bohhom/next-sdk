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

package com.lib.sdk.next.o.map.net;

/**
 * Created by maqing 2018/11/21 14:07
 * Email：2856992713@qq.com
 * WebSocket请求接口
 */
public class SocketRequestInterface {
    /**
     * 机器人状态
     * /api/v1/socket/robot/Status
     */
    public static final String ROBOT_STATUS = "api/v1/socket/robot/Status";

    /**
     * 机器人错误状态
     */
    public static final String ROBOT_ERROR_STATUS = "api/socket/robot/ErrorCodeStatus";

    /**
     * 机器人导航状态
     */
    public static final String ROBOT_NAVIGATION_STATUS = "api/socket/robot/NavigationStatus";
    /**
     * 机器人激光数据
     */
    public static final String ROBOT_LASER_STATUS = "api/socket/robot/Laser";

    /**
     * (建地图时)服务器回传地图数据
     */
    public static final String SERVER_BACK_MAP = "api/socket/map/GenerateMap";

    /**
     * 虚拟摇杆数据传给服务器
     */
    public static final String ROCKER_DATA = "api/socket/robot/JoystickNavigation";
}

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
 * Created by aruba on 2018/7/30
 */
public class HttpUri {
    //登录
    public static final String URL_LOGIN = "api/user/Login";
    //拉取需要更新的工程
    public static final String URL_QUERYSYNC_PROJECT = "api/sync/QuerySyncProject";
    //取需要更新的工程的详细数据
    public static final String URL_PULL_SYNC_PROJECT = "api/sync/PullSyncProject";
    //获取机器人速度
    public static final String URL_GET_SPEED = "api/setting/GetSpeed";
    //设置机器人速度
    public static final String URL_SET_SPEED = "api/setting/Speed";
    //获取电量阈值
    public static final String URL_GET_BATTERY = "api/v1/setting/GetBattery";
    //设置机器低电量阈值
    public static final String URL_SET_BATTERY = "api/v1/setting/SetBattery";
    //获取机器人版本号
    public static final String URL_GET_ROBOTVERSION = "api/v1/robot/GetRobotVersion";
    //操作地图接口：建图、拓展、保存、取消
    public static final String URL_GENERATE_MAP = "api/map/GenerateMap";
    //地图闭环操作
    public static final String URL_MAP_CLOSER = "api/map/MapCloser";
    //app同步工程到机器人(服务器)
    public static final String URL_PUSH_SYNCPROJECT = "api/sync/PushSyncProject";
    //删除指定工程
    public static final String URL_DELETE_PROJECT = "api/robot/DeleteProject";
    //删除批量工程
    public static final String URL_BATCH_DELETE_PROJECT = "/api/robot/DeleteProjects";
    //切换工程
    public static final String URL_SELECT_PROJECT = "api/robot/SelectProject";
    //获取日志 
    public static final String URL_GET_LOG = "api/v1/log/GetLogContent";
    //GPO 设置 
    public static final String URL_SET_GPO_VALUE = "api/v1/setting/SetGPOValue";
    //4.3.8	去除噪点 v1
    public static final String URL_UPDATE_MAP_PIX = "api/v1/map/UpdateMapPixmap";
    //初始化定位
    public static final String URL_INIT_LOCATION = "api/robot/InitPose";
    //强制初始化定位
    public static final String URL_INIT_LOCATION_FORCE = "api/robot/ForceInitPose";
    //航点、充电点、初始点、可行区域保存至服务器
    public static final String URL_PUSH_SYNC_CONFIG = "api/sync/PushSyncConfig";
    //路
    // 径规划点保存至服务器
    public static final String URL_PUSH_SYNC_SCRIPT_CONFIG = "api/sync/PushSyncScriptConfig";
    //从服务器查询路径规划点
    public static final String URL_PULL_SYNC_SCRIPT_CONFIG = "api/sync/PullSyncScriptConfig";
    //执行脚本
    public static final String URL_RUN_TASK_COMMAND = "api/task/RunTaskCommand";
    //获取所有脚本名
    public static final String URL_GET_SCRIPT_NAMELIST = "api/task/GetScriptNameList";
    //获取状态
    public static final String URL_GET_TASK_LAST_RUNSTATUS = "api/task/GetTaskLastRunStatus";
    //4.5.11	一键充电
    public static final String URL_RUN_ROBOT_CHARGE = "api/v1/task/RunRobotCharge";
    //4.6.10	获取GPIO 标签v1
    public static final String URL_GET_GPIO_LABEL = "api/v1/setting/GetGPIOLabel";
    //4.6.11	设置GPI 标签v1
    public static final String URL_SET_GPI_LABEL = "api/v1/setting/SetGPILabel";
    //4.6.12	设置GPO 标签v1
    public static final String URL_Set_GPO_Label = "api/v1/setting/SetGPOLabel";
    //获取定时任务列表
    public static final String URL_TIMED_TASK = "api/v1/task/GetScheduleTask";
    //添加计划任务
    public static final String URL_ADD_TIMED_TASK = "/api/v1/task/AddScheduleTask";
    //删除计划任务
    public static final String URL_DEL_TIMED_TASK = "/api/v1/task/RemoveScheduleTask";
    //控制计划任务
    public static final String URL_CONTROL_TIMED_TASK = "/api/v1/task/RunScheduleTask";
    //获取工作模式list
    public static final String URL_GET_WORKMODELS = "/api/v1/task/GetTaskWorkMode";
    //删除工作模式
    public static final String URL_DEl_WORKMODEL_TASK = "/api/v1/task/RemoveTaskWorkMode";
    //新增工作模式
    public static final String URL_ADD_WORKMODEL_TASK = "/api/v1/task/AddTaskWorkMode";
    // 获取巡航脚本 v1
    public static final String URL_GET_CRUISE_SCRIPT = "/api/v1/sync/PullSyncScriptConfig" ;
    // 同步巡航脚本 v1
    public static final String URL_SYNC_CRUISE_SCRIPT = "/api/v1/sync/PushSyncScriptConfig" ;
    //导航
    public static final String URL_SYNC_2DNavGoal = "/api/robot/Set2DNavGoal" ;

    //取消2d导航
    public static final String URL_SYNC_2DNavCANCEL = "/api/robot/CancelNavGoal" ;


    
}

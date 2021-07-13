
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
package com.lib.sdk.next;

/**
 * FileName: NextException
 * Author: zhikai.jin
 * Date: 2021/6/29 17:38
 * Description: next的错误信息
 */
public class NextException extends Exception {

    /**
     * 成功
     */
    public static final int CODE_NEXT_SUCCESS = 0;

    /**
     * 失败
     */
    public static final int CODE_NEXT_FAIL = -1;


    /**
     * 参数错误
     */
    public static final int CODE_NEXT_PARAMS = -2;


    /**
     *  序列号错误
     */
    public static final int CODE_NEXT_SERIAL = -3;



    /**
     *  json解析错误
     */
    public static final int CODE_NEXT_JSON = -4;

    /**
     *  socket连接失败
     */
    public static final int CODE_SOCKET_FAIL = -5;


    /**
     *  socket连接关闭
     */
    public static final int CODE_SOCKET_CLOSED = -6;



    /**
     * 相同工程不能切换
     */
    public static final int PROJECT_NOT_CHANGE = 2001;

    /**
     * 工程加载失败
     */
    public static final int PROJECT_LOAD_FAIL = 2002;

    /**
     * 切换工程失败
     */
    public static final int PROJECT_CHANGE_FAIL = 2003;

    /**
     * 正在建图
     */
    public static final int PROJECT_TASK_OTHER = 2004;


    /**
     * 当前工程不能删除
     */
    public static final int PROJECT_NOT_DELETE = 3001;

    /**
     * 服务器不存在该共工程
     */
    public static final int PROJECT_NOT_EXIT = 3002;


    /**
     * 工程删除失败
     */
    public static final int PROJECT_DELETE_FAIL = 3003;


    /**
     * 建图失败
     */
    public static final int CREATE_PROJECT_FAIL = 4001;

    /**
     * 导航失败
     */
    public static final int CREATE_NAV_FAIL = 4002;

    /**
     * 切换工程失败
     */
    public static final int CREATE_CHANGE_FAIL = 4003;

    /**
     * 正在执行任务建图失败
     */
    public static final int CREATE_ACTION_FAIL = 4004;

    /**
     * 拓展建图数据错误
     */
    public static final int CREATE_EXPAND_PROJECT_FAIL = 4005;

    /**
     * 拓展建图数据错误
     */
    public static final int CREATE_EXPAND_DATA_FAIL = 4006;


    /**
     * 清除建图数据错误
     */
    public static final int CREATE_CLEAR_FAIL = 4007;


    /**
     * 任务参数错误
     */
    public static final int COMMAND_PARAM_ERROR = 9001;


    /**
     * 任务开关错误
     */
    public static final int COMMAND_ONOFF_ERROR = 9002;


    /**
     * 任务UUID错误
     */
    public static final int COMMAND_UUID_ERROR = 9003;


    /**
     * 任务输入脚本内容为空
     */
    public static final int COMMAND_CONTEXT_ERROR = 9004;


    /**
     * 任务输入脚本不存在
     */
    public static final int COMMAND_NOT_EXIT = 9005;


    /**
     * 任务输入脚本为空
     */
    public static final int COMMAND_INPUT_NULL = 9006;


    /**
     * 任务生成错误
     */
    public static final int COMMAND_TASK_ERROR = 9007;

    /**
     * 任务解析错误
     */
    public static final int COMMAND_AL_ERROR = 9008;

    /**
     * 任务非空闲状态
     */
    public static final int COMMAND_BUSY_ERROR = 9009;


    /**
     * 任务低电量错误
     */
    public static final int COMMAND_LOW_ERROR = 9010;
}


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

import org.json.JSONObject;

/**
 * Created by maqing 2018/11/19 10:46
 * Email：2856992713@qq.com
 * 机器人状态实体类
 */
public class RobotStatusBean {
    /**
     * 机器人电量百分比
     */
    private int mBattery = -1;
    /**
     * 电池状态 4,5,6
     * 0.未定义
     * 1.正常(非充电)
     * 2.需要充电
     * 3.等待充电
     * 4.充电中
     * 5.充电中(可跑车)
     * 6.充电满
     * 7.电量75
     * 8.电量50
     */
    private int mBatteryState = -1;
    /**
     * 机器人当前任务状态
     */
    private String mTask = "";
    /**
     * 机器人当前急停状态
     */
    private int mEmergence =-1;
    /**
     * 机器人当前诊断状态
     */
    private int mDiagnose = -1;
    /**
     * 机器人当前工程ID
     */
    private String mProjectId = "";

    /**
     * 机器人全局执行状态码
     */
    private int mGlobalStatusCode = -1;
    /**
     * 机器人全局执行状态
     */
    private String mGlobalStatusMsg = "";
    /**
     * 机器人线速度(百分比)
     */
    private double mLineSpeed = 0;
    /**
     * 机器人角速度(百分比)
     */
    private double mAngularSpeed = 0;
    /**
     * 电量阈值
     */
    private int mBatteryThreshold = 0;
    /**
     * 是否预约充电
     */
    private boolean mReservationBattery = false;

    /**
     * 预约充电开始时间
     */
    private String mReservationStartTime = "0";
    /**
     * 预约充电结束时间
     */
    private String mReservationEndTime = "0";

    /**
     * 机器人位置服务器Json数据
     */
    private String positionJson = "";

    /**
     * 当前机器人嵌入式主板状态
     */
    private int  board_state = 0;

    /**
     *  触边急停状态
     */
    private int  edge_switch = 0;

    public int getBoard_state() {
        return board_state;
    }

    public void setBoard_state(int board_state) {
        this.board_state = board_state;
    }

    public int getEdge_switch() {
        return edge_switch;
    }

    public void setEdge_switch(int edge_switch) {
        this.edge_switch = edge_switch;
    }

    /**
     * 机器人位置服务器Json数据
     */
    private RobotPostionBean robotPostionBean;

    public RobotPostionBean getRobotPostionBean() {
        return robotPostionBean;
    }

    public void setRobotPostionBean(RobotPostionBean robotPostionBean) {
        this.robotPostionBean = robotPostionBean;
    }

    /**
     * io状态
     */
    private JSONObject ioStatusJson;

    public int getBattery() {
        return mBattery;
    }

    public void setBattery(int battery) {
        mBattery = battery;
    }

    public String getTask() {
        return mTask;
    }

    public void setTask(String task) {
        mTask = task;
    }

    public int getEmergence() {
        return mEmergence;
    }

    public void setEmergence(int emergence) {
        mEmergence = emergence;
    }

    public int getDiagnose() {
        return mDiagnose;
    }

    public void setDiagnose(int diagnose) {
        mDiagnose = diagnose;
    }

    public String getProjectId() {
        return mProjectId;
    }

    public void setProjectId(String projectId) {
        mProjectId = projectId;
    }

    public int getGlobalStatusCode() {
        return mGlobalStatusCode;
    }

    public void setGlobalStatusCode(int globalStatusCode) {
        mGlobalStatusCode = globalStatusCode;
    }

    public String getGlobalStatusMsg() {
        return mGlobalStatusMsg;
    }

    public void setGlobalStatusMsg(String globalStatusMsg) {
        mGlobalStatusMsg = globalStatusMsg;
    }

    public double getLineSpeed() {
        return mLineSpeed;
    }

    public void setLineSpeed(double lineSpeed) {
        mLineSpeed = lineSpeed;
    }

    public double getAngularSpeed() {
        return mAngularSpeed;
    }

    public void setAngularSpeed(double angularSpeed) {
        mAngularSpeed = angularSpeed;
    }


    public int getBatteryThreshold() {
        return mBatteryThreshold;
    }

    public void setBatteryThreshold(int batteryThreshold) {
        mBatteryThreshold = batteryThreshold;
    }

    public String getReservationStartTime() {
        return mReservationStartTime;
    }

    public void setReservationStartTime(String reservationStartTime) {
        mReservationStartTime = reservationStartTime;
    }

    public String getReservationEndTime() {
        return mReservationEndTime;
    }

    public void setReservationEndTime(String reservationEndTime) {
        mReservationEndTime = reservationEndTime;
    }

    public boolean isReservationBattery() {
        return mReservationBattery;
    }

    public void setReservationBattery(boolean reservationBattery) {
        mReservationBattery = reservationBattery;
    }

    public String getPositionJson() {
        return positionJson;
    }

    public void setPositionJson(String positionJson) {
        this.positionJson = positionJson;
    }

    public int getBatteryState() {
        return mBatteryState;
    }

    public void setBatteryState(int batteryState) {
        mBatteryState = batteryState;
    }

    public JSONObject getIoStatusJson() {
        return ioStatusJson;
    }

    public void setIoStatusJson(JSONObject ioStatusJson) {
        this.ioStatusJson = ioStatusJson;
    }
}



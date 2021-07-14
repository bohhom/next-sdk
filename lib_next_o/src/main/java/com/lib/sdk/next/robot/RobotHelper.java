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


import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.bozh.logger.Logger;
import com.lib.sdk.next.life.LifecycleListener;
import com.lib.sdk.next.livedata.EventConstant;
import com.lib.sdk.next.livedata.LiveDataBus;
import com.lib.sdk.next.livedata.RobotLaserEvent;
import com.lib.sdk.next.global.GlobalOperate;
import com.lib.sdk.next.o.map.event.RobotErrorEvent;
import com.lib.sdk.next.o.map.event.RobotNavigationStatusEvent;
import com.lib.sdk.next.o.map.event.RobotStatusEvent;
import com.lib.sdk.next.o.map.manager.RequestManager;
import com.lib.sdk.next.o.map.net.SocketRequestInterface;
import com.lib.sdk.next.robot.bean.RobotErrorStatusBean;
import com.lib.sdk.next.robot.bean.RobotNavigationStatusBean;
import com.lib.sdk.next.robot.bean.RobotStatusBean;
import com.lib.sdk.next.robot.service.RobotErrorStatusService;
import com.lib.sdk.next.robot.service.RobotLaserDataService;
import com.lib.sdk.next.robot.service.RobotNavigationStatusService;
import com.lib.sdk.next.robot.service.RobotStatusService;
import com.lib.sdk.next.util.BeanPropertiesUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * FileName: RobotHelper
 * Author: zhikai.jin
 * Date: 2021/6/3 15:30
 * Description: 机器人信息,
 */
public class RobotHelper{

    private  IRobotLaserCallBack iRobotLaserCallBack;

    private static LinkedHashMap<String, IRobotStatusCallBack> pendingHashMap = new LinkedHashMap<>();

    public RobotHelper() {
    }



    /**
     * 注册机器人状态
     */
    public  void registerRobotStatus(Context context, IRobotStatusCallBack callBack){
        String singleKey =   context.getClass().getSimpleName();
        if (!isExit(singleKey)) {
            EventBus.getDefault().register(this);
            Logger.i("RobotHelper register key is  %s,robot is register",singleKey);
        }
        pendingHashMap.put(context.getClass().getSimpleName(),callBack);

        RobotErrorStatusService.start(GlobalOperate.getApp(), RequestManager.mSocketBaseUrl ,SocketRequestInterface.ROBOT_ERROR_STATUS);
        RobotNavigationStatusService.start(GlobalOperate.getApp(), RequestManager.mSocketBaseUrl ,SocketRequestInterface.ROBOT_NAVIGATION_STATUS);
        RobotStatusService.start(GlobalOperate.getApp(), RequestManager.mSocketBaseUrl ,SocketRequestInterface.ROBOT_STATUS);
    }


    public void destroyRobotStatus(Context context){
        try {
            pendingHashMap.remove(context.getClass().getSimpleName());
            EventBus.getDefault().unregister(this);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * 机器人激光点信息
     */
    public void registerRobotLaserInfo(IRobotLaserCallBack callBack){
        this.iRobotLaserCallBack = callBack;
        RobotLaserDataService.start(GlobalOperate.getApp(), RequestManager.mSocketBaseUrl ,SocketRequestInterface.ROBOT_LASER_STATUS);
        LiveDataBus.get().with(EventConstant.NEXT_MAP_ROBOT_LASER, RobotLaserEvent.class).observeForever(this::updateLaser);
    }

    /**
     * 机器人状态信息
     * @param data
     */
    @Subscribe( threadMode = ThreadMode.MAIN)
    public void updateStatus(RobotStatusEvent data) {
        RobotStatusInfo robotStatusInfo = new RobotStatusInfo();
        try {
            BeanPropertiesUtil.copyProperties(data.getRobotStatusBean(),robotStatusInfo);

        } catch (Exception e) {
            e.printStackTrace();
        }
        getTail(pendingHashMap).getValue().onRobotStatus(data.getCode(),robotStatusInfo);
    }


    /**
     * 机器人错误信息
     * @param data
     */
    @Subscribe( threadMode = ThreadMode.MAIN)
    public void updateError(RobotErrorEvent data) {
        RobotErrorStatusInfo robotErrorStatusInfo = new RobotErrorStatusInfo();
        try {
            BeanPropertiesUtil.copyProperties(data.getmRobotErrorStatusBean(),robotErrorStatusInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
         getTail(pendingHashMap).getValue().onRobotError(data.getCode(),robotErrorStatusInfo);
    }

    /**
     * 激光点
     * @param data
     */
    private void updateLaser(RobotLaserEvent data) {
        RobotLaserDataInfo robotLaserDataInfo = new RobotLaserDataInfo();
        try {
            BeanPropertiesUtil.copyProperties(data,robotLaserDataInfo);

        } catch (Exception e) {
            e.printStackTrace();
        }
        iRobotLaserCallBack.onRobotLaser(robotLaserDataInfo);
    }


    /**
     * 机器人导航信息
     * @param data
     */
    @Subscribe( threadMode = ThreadMode.MAIN)
    public void updateNav(RobotNavigationStatusEvent data) {
        RobotNavigationStatusBean  navigationStatusBean = data.getRobotNavigationStatusBean();
        RobotNavigationStatusInfo robotNavInfo = new RobotNavigationStatusInfo();
        try {
            BeanPropertiesUtil.copyProperties(navigationStatusBean,robotNavInfo);

        } catch (Exception e) {
            e.printStackTrace();
        }
        getTail(pendingHashMap).getValue().onRobotNav(data.getCode(),robotNavInfo);
    }



    /**
     * 取消激光点注册
     */
    public void unRegisterRobotLaser(){
        LiveDataBus.get().with(EventConstant.NEXT_MAP_ROBOT_LASER, RobotLaserEvent.class).removeObserver(this::updateLaser);
    }




    private <K, V> Map.Entry<K, V> getHead(LinkedHashMap<K, V> map) {
        return map.entrySet().iterator().next();
    }

    private <K, V> Map.Entry<K, V> getTail(LinkedHashMap<K, V> map) {
        Iterator<Map.Entry<K, V>> iterator = map.entrySet().iterator();
        Map.Entry<K, V> tail = null;
        while (iterator.hasNext()) {
            tail = iterator.next();
        }
        return tail;
    }

    private boolean isExit(String key){
        Iterator<Map.Entry<String, IRobotStatusCallBack>> iterator = pendingHashMap.entrySet().iterator();

        while(iterator.hasNext())
        {
            Map.Entry entry = iterator.next();
            if(entry.getKey().equals(key)){
                return true;
            }
        }
        return false;
    }
}

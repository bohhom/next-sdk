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
package com.lib.sdk.next.operate;

import com.bozh.logger.Logger;
import com.lib.sdk.next.NextException;
import com.lib.sdk.next.NextResultInfo;
import com.lib.sdk.next.base.IBaseHelper;
import com.lib.sdk.next.gps.CoordEntry;
import com.lib.sdk.next.gps.InitLocationCallBack;
import com.lib.sdk.next.o.http.HttpResponse;
import com.lib.sdk.next.o.map.net.HttpUri;
import com.lib.sdk.next.o.map.widget.MapDrawView;

import java.util.HashMap;

/**
 * FileName: NextOperateHelper
 * Author: zhikai.jin
 * Date: 2021/6/30 11:44
 * Description: 任务操作
 */
public class NextOperateHelper extends IBaseHelper<NextOperatePresenter> implements INextOperateCallBack, InitLocationCallBack {


    private static volatile NextOperateHelper mInstance;

    private IRobotOperateListener mIRobotOperateListener;

    private IRobotLocationListener mIRobotLocationListener;

    private int mCurrentType = COMMAND_LOAD_SCRIPT_NAME;

    /**
     * 装载执行脚本名
     */
    public final static int COMMAND_LOAD_SCRIPT_NAME = 0;

    /**
     * 装载执行脚本内容
     */
    public final static int COMMAND_LOAD_SCRIPT_CONTEXT = 1;

    /**
     * 执行
     */
    public final static int COMMAND_ACTION = 2;

    /**
     * 复位
     */
    public final static int COMMAND_REST = 3;

    /**
     * 停止
     */
    public final static int COMMAND_STOP = 4;

    /**
     * 停止并复位
     */
    public final static int COMMAND_STOP_REST = 5;

    /**
     * 单步
     */
    public final static int COMMAND_STEP = 6;

    /**
     * 推送最新Feedback
     */
    public final static int COMMAND_FEED = 7;



    private int mInitLocationType = LOCATION_SMART  ;

    /**
     * 智能初始化定位
     */
    public static final int  LOCATION_SMART = 0;

    /**
     * 强制初始化
     */
    public static final int  LOCATION_ENFORCE = 1;



    public static NextOperateHelper getInstance() {
        if (mInstance == null) {
            synchronized (NextOperateHelper.class) {
                if (mInstance == null) {
                    mInstance = new NextOperateHelper();
                }
            }
        }
        return mInstance;
    }


    private NextOperateHelper() {
        this(new NextOperatePresenter());
    }

    private NextOperateHelper(NextOperatePresenter basePresenter) {
        super(basePresenter);
    }

    @Override
    public void showErr(String uri, String msg) {

    }

    @Override
    protected void attachView(MapDrawView drawView) {

    }

    @Override
    protected void onCreate() {
        super.onCreate();
    }

    @Override
    public void onSet2DNavGoalCallBack(HttpResponse data) {
        if (mIRobotOperateListener != null) {
            mIRobotOperateListener.onSet2DNavResult(new NextResultInfo(data.code, data.info));
        }
    }

    @Override
    public void onCancel2DNavGoalCallBack(HttpResponse data) {
        if (mIRobotOperateListener != null) {
            mIRobotOperateListener.onCancel2DNavResult(new NextResultInfo(data.code, data.info));
        }
    }

    @Override
    public void startTaskDataCallBack(HttpResponse data) {
        if (mIRobotOperateListener != null) {
            if (data.code == 0) {
                mIRobotOperateListener.onCommandResult(mCurrentType,new NextResultInfo(data.code, data.info));
            } else {
                switch (data.code) {
                    case 1:
                        mIRobotOperateListener.onCommandResult(mCurrentType,new NextResultInfo(NextException.COMMAND_PARAM_ERROR, data.info));
                        break;
                    case 2:
                        mIRobotOperateListener.onCommandResult(mCurrentType,new NextResultInfo(NextException.COMMAND_ONOFF_ERROR, data.info));
                        break;
                    case 3:
                        mIRobotOperateListener.onCommandResult(mCurrentType,new NextResultInfo(NextException.COMMAND_UUID_ERROR, data.info));
                        break;
                    case 4:
                        mIRobotOperateListener.onCommandResult(mCurrentType,new NextResultInfo(NextException.COMMAND_CONTEXT_ERROR, data.info));
                        break;
                    case 5:
                        mIRobotOperateListener.onCommandResult(mCurrentType,new NextResultInfo(NextException.COMMAND_NOT_EXIT, data.info));
                        break;
                    case 6:
                        mIRobotOperateListener.onCommandResult(mCurrentType,new NextResultInfo(NextException.COMMAND_INPUT_NULL, data.info));
                        break;
                    case 7:
                        mIRobotOperateListener.onCommandResult(mCurrentType,new NextResultInfo(NextException.COMMAND_TASK_ERROR, data.info));
                        break;
                    case 8:
                        mIRobotOperateListener.onCommandResult(mCurrentType,new NextResultInfo(NextException.COMMAND_AL_ERROR, data.info));
                        break;
                    case 9:
                        mIRobotOperateListener.onCommandResult(mCurrentType,new NextResultInfo(NextException.COMMAND_BUSY_ERROR, data.info));
                        break;

                    case 10:
                        mIRobotOperateListener.onCommandResult(mCurrentType,new NextResultInfo(NextException.COMMAND_LOW_ERROR, data.info));
                        break;
                    default:
                        mIRobotOperateListener.onCommandResult(mCurrentType,new NextResultInfo(data.code, data.info));
                        break;
                }
            }
        }
        mCurrentType = COMMAND_LOAD_SCRIPT_NAME;
    }

    /**
     * 停止并且复位
     */
    public void onRestAndStopCommand() {
        mCurrentType = COMMAND_STOP_REST;
        HashMap<String, Object> params = new HashMap<>();
        params.put("script_name", "");
        params.put("type", COMMAND_STOP_REST);
        mPresenter.startTask(HttpUri.URL_RUN_TASK_COMMAND, params);
    }


    /**
     * 机器人复位
     */
    public void onRestCommand() {
        mCurrentType = COMMAND_REST;
        HashMap<String, Object> params = new HashMap<>();
        params.put("script_name", "");
        params.put("type", COMMAND_REST);
        mPresenter.startTask(HttpUri.URL_RUN_TASK_COMMAND, params);
    }

    /**
     * 2d导航
     *
     * @param worldX 世界坐标
     * @param worldY
     * @param theta  方向角标
     */
    public void on2DNavGoal(double worldX, double worldY, double theta) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("x", worldX);
        params.put("y", worldY);
        params.put("theta", theta);
        mPresenter.request2DNavGoal(HttpUri.URL_SYNC_2DNavGoal, params);
    }

    /**
     * 取消2d导航
     */
    public void on2DNavCancel() {
        HashMap<String, Object> params = new HashMap<>();
        mPresenter.request2DNavCancel(HttpUri.URL_SYNC_2DNavCANCEL, params);
    }

    public void setRobotOperateListener(IRobotOperateListener robotOperateListener) {
        this.mIRobotOperateListener = robotOperateListener;
    }

    /**
     * 智能初始化定位
     * @param worldX
     * @param worldY
     * @param theta
     */
    public void onSmartLocation(double worldX,double worldY,double theta){
        Logger.d("智能初始化定位操作");
        mInitLocationType = LOCATION_SMART;
        HashMap<String, Object> params = new HashMap<>();
        params.put("x", worldX);
        params.put("y", worldY);
        params.put("theta", theta);
        mPresenter.initLocation(HttpUri.URL_INIT_LOCATION, params);
    }



    /**
     * 强制初始化定位
     * @param worldX
     * @param worldY
     * @param theta
     */
    public void onEnforceLocation(double worldX,double worldY,double theta){
        Logger.d("强制初始化定位操作");
        mInitLocationType = LOCATION_ENFORCE;
        HashMap<String, Object> params = new HashMap<>();
        params.put("x", worldX);
        params.put("y", worldY);
        params.put("theta", theta);
        mPresenter.initLocationForce(HttpUri.URL_INIT_LOCATION_FORCE, params);
    }

    public void setRobotLocationListener( IRobotLocationListener locationListener) {
        this.mIRobotLocationListener = locationListener;
    }

    @Override
    public void initLocationDataCallBack(HttpResponse data) {
        mIRobotLocationListener.onLocationResult(mInitLocationType,new NextResultInfo(data.code,data.info));
    }

    public interface IRobotOperateListener {
        void onSet2DNavResult(NextResultInfo resultInfo);

        void onCancel2DNavResult(NextResultInfo resultInfo);

        void onCommandResult(int type,NextResultInfo resultInfo);
    }


    /**
     * 初始化定位回调
     */
    public interface IRobotLocationListener {
        void onLocationResult(int type,NextResultInfo resultInfo);
    }
}

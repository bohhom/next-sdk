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

package com.lib.sdk.next.setting;

import com.lib.sdk.next.NextResultInfo;
import com.lib.sdk.next.base.IBaseCallBack;
import com.lib.sdk.next.base.IBaseHelper;
import com.lib.sdk.next.creater.CreateMapHelper;
import com.lib.sdk.next.o.http.HttpResponse;
import com.lib.sdk.next.o.map.net.HttpUri;
import com.lib.sdk.next.o.map.widget.MapDrawView;
import com.lib.sdk.next.robot.constant.RobotConstant;
import com.lib.sdk.next.util.NumberUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * FileName: SettingHelper
 * Author: zhikai.jin
 * Date: 2021/6/28 14:25
 * Description: 机器人设置
 */
public class SettingHelper extends IBaseHelper<SettingPresenter> implements SettingDataCallback {

    private static volatile SettingHelper mInstance;

    private double mLineSpeed = 0;

    private double mAngularSpeed = 0;

    private IRobotSpeedListener mRobotSpeedListener;

    public static SettingHelper getInstance() {
        if (mInstance == null) {
            synchronized (CreateMapHelper.class) {
                if (mInstance == null) {
                    mInstance = new SettingHelper();
                }
            }
        }
        return mInstance;
    }


    private SettingHelper() {
        this(new SettingPresenter());
    }

    private SettingHelper(SettingPresenter presener) {
        super(presener);
    }

    @Override
    public void showErr(String uri, int code, String msg) {
        mRobotSpeedListener.onHttpError(uri,code,msg);
    }



    @Override
    public void attachView(MapDrawView drawView) {

    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void setSpeedDataCallBack(HttpResponse response) {
        int code = response.code;
        if (code == 0) {
            RobotConstant.mRobotStatusBean.setLineSpeed((float) NumberUtil.keep2Precision(mLineSpeed / 100f));
            RobotConstant.mRobotStatusBean.setAngularSpeed((float) NumberUtil.keep2Precision(mAngularSpeed / 100f));
        }

        if (mRobotSpeedListener != null) {
            mRobotSpeedListener.onSetRobotSpeed(new NextResultInfo(code, response.info));
        }
    }

    @Override
    public void speedDataCallBack(HttpResponse response) {
        try {
            String info = response.info;
            int code = response.code;
            if (code == 0) {
                JSONObject data = new JSONObject(response.data);
                RobotConstant.mRobotStatusBean.setLineSpeed((float) data.getDouble("speed_x"));
                RobotConstant.mRobotStatusBean.setAngularSpeed((float) data.getDouble("speed_theta"));
                if (mRobotSpeedListener != null) {
                    mRobotSpeedListener.onGetSuccessRobotSpeed(data.getDouble("speed_x"), data.getDouble("speed_theta"));
                }
            } else {
                if (mRobotSpeedListener != null) {
                    mRobotSpeedListener.onGetFailedRobotSpeed(new NextResultInfo(code, info));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取机器人速度
     */
    public void requestRobotSpeed() {
        mPresenter.getRobotSpeed(HttpUri.URL_GET_SPEED);
    }


    /**
     * 设置机器人速度
     *
     * @param lineSpeed    线速度
     * @param angularSpeed
     */
    public void setRobotSpeed(double lineSpeed, double angularSpeed) {
        this.mLineSpeed = lineSpeed;
        this.mAngularSpeed = angularSpeed;

        HashMap<String, Object> params = new HashMap();
        params.put("speed_x", NumberUtil.keep2Precision(lineSpeed / 100f));
        params.put("speed_theta", NumberUtil.keep2Precision(angularSpeed / 100f));
        mPresenter.setRobotSpeed(HttpUri.URL_SET_SPEED, params);
    }

    public void setRobotSpeedListener(IRobotSpeedListener robotSpeedListener) {
        this.mRobotSpeedListener = robotSpeedListener;
    }

    public static abstract class IRobotSpeedListener implements IBaseCallBack {

        public abstract void onSetRobotSpeed(NextResultInfo resultInfo);

        public abstract void onGetSuccessRobotSpeed(double speedX, double speedTheta);

        public abstract void onGetFailedRobotSpeed(NextResultInfo resultInfo);
    }


}

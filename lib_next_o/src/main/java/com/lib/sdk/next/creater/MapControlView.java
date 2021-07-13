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
package com.lib.sdk.next.creater;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lib.sdk.next.livedata.EventConstant;
import com.lib.sdk.next.livedata.LiveDataBus;
import com.lib.sdk.next.livedata.RobotRockerEvent;
import com.lib.sdk.next.o.R;
import com.lib.sdk.next.o.map.bean.PositionPointBean;
import com.lib.sdk.next.o.map.bean.ProjectInfoBean;
import com.lib.sdk.next.o.map.util.FileUtils;
import com.lib.sdk.next.o.map.widget.MapDrawView;
import com.lib.sdk.next.robot.bean.RobotLaserDataBean;
import com.lib.sdk.next.util.NumberUtil;

import java.util.List;

import static com.lib.sdk.next.creater.RockerView.DirectionMode.DIRECTION_8;

/**
 * FileName: MapControl
 * Author: zhikai.jin
 * Date: 2021/6/8 16:32
 * Description: 地图控制页面
 */
public class MapControlView extends FrameLayout {

    private  static final String TAG = MapControlView.class.getSimpleName();

    private MapDrawView mMapDrawView;

    private RockerView mRockerView;


    public MapControlView(@NonNull Context context) {
        super(context);
    }

    public MapControlView(@NonNull  Context context, @Nullable  AttributeSet attrs) {
        super(context, attrs);
    }

    public MapControlView(@NonNull  Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MapControlView(@NonNull  Context context, @Nullable  AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    {
        LayoutInflater.from(getContext()).inflate(R.layout.robot_map_ctrl_view, this, true);
        mMapDrawView = findViewById(R.id.robot_ctrl_mapview);
        mMapDrawView.setEnableBitmapChache(false);
        mRockerView = findViewById(R.id.robot_rocker_view);

        mRockerView.setOnShakeListener(DIRECTION_8, new RockerView.OnShakeListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void direction(RockerView.Direction direction) {
                if (direction == RockerView.Direction.DIRECTION_CENTER) {
                    Log.d("rocker", "当前方向：中心");
                } else if (direction == RockerView.Direction.DIRECTION_DOWN) {
                    Log.d("rocker" ,"当前方向：下");
                } else if (direction == RockerView.Direction.DIRECTION_LEFT) {
                    Log.d("rocker","当前方向：左");
                } else if (direction == RockerView.Direction.DIRECTION_UP) {
                    Log.d("rocker" ,"当前方向：上");
                } else if (direction == RockerView.Direction.DIRECTION_RIGHT) {
                    Log.d("rocker","当前方向：右");
                } else if (direction == RockerView.Direction.DIRECTION_DOWN_LEFT) {
                    Log.d("rocker","当前方向：左下");
                } else if (direction == RockerView.Direction.DIRECTION_DOWN_RIGHT) {
                    Log.d("rocker","当前方向：右下");
                } else if (direction == RockerView.Direction.DIRECTION_UP_LEFT) {
                    Log.d("rocker", "当前方向：左上");
                } else if (direction == RockerView.Direction.DIRECTION_UP_RIGHT) {
                    Log.d("rocker","当前方向：右上");
                }
            }

            @Override
            public void onFinish() {

            }
        });

        mRockerView.setOnAngleDistanceChangeListener(new RockerView.OnAngleDistanceChangeListener() {
            @Override
            public void OnAngleDistanceChange(double angle, int level) {

                double mCurrentAngle = 0;
                double mCurrentDistanceLevel = 0;
                if (angle == 0 && level == 0) { //虚拟摇杆回到原点
                    mCurrentAngle = 0;
                    mCurrentDistanceLevel = 0;
                } else {
                    double changeAngle = 0;
                    if (angle + 90 > 360) {
                        changeAngle = 360 - angle + 270;
                    } else {
                        changeAngle = (360 - (angle + 90));
                    }
                    double changeLevel = NumberUtil.keep1Precision(level * 1.0 / 10);

                    if (mCurrentAngle == 0 && mCurrentDistanceLevel == 0) {
                        mCurrentAngle = changeAngle;
                        mCurrentDistanceLevel = changeLevel;
                    } else {
                        if (Math.abs(mCurrentAngle - changeAngle) >= 10f
                                || Math.abs(mCurrentDistanceLevel - changeLevel) >= 0.1
                        ) {
                            mCurrentAngle = changeAngle;
                            mCurrentDistanceLevel = changeLevel;
                        }
                    }

                }
                RobotRockerEvent robotRockerEvent = new RobotRockerEvent();
                robotRockerEvent.setCurrentAngle(mCurrentAngle);
                robotRockerEvent.setUrrentDistanceLevel(mCurrentDistanceLevel);
                LiveDataBus.get().with(EventConstant.NEXT_MAP_ROBOT_ROCKER).postValue(robotRockerEvent);
            }

        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

    }





    public void setBitmap(Bitmap bitmap, FileUtils.OriginSize originSize){
        mMapDrawView.setBitmap(bitmap,originSize);
    }

    public void initRobotLaserData(List<RobotLaserDataBean> robotLaserDataBeanList) {
        mMapDrawView.initRobotLaserData(robotLaserDataBeanList);
    }

    public void initRobotPosition(PositionPointBean positionPointBean) {
        mMapDrawView.initRobotPosition(positionPointBean);
    }


}

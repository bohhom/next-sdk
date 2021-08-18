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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.lib.sdk.next.creater.RockerView;
import com.lib.sdk.next.base.INmap;
import com.lib.sdk.next.gps.LocationHelper;
import com.lib.sdk.next.base.NxMap;
import com.lib.sdk.next.o.R;
import com.lib.sdk.next.o.map.bean.PositionPointBean;
import com.lib.sdk.next.o.map.bean.VirtualWallBean;
import com.lib.sdk.next.global.GlobalOperate;
import com.lib.sdk.next.o.map.event.RobotStatusEvent;
import com.lib.sdk.next.o.map.manager.ProjectCacheManager;
import com.lib.sdk.next.o.map.manager.RequestManager;
import com.lib.sdk.next.o.map.net.SocketRequestInterface;
import com.lib.sdk.next.o.map.util.ImageCache;
import com.lib.sdk.next.o.map.util.PositionUtil;
import com.lib.sdk.next.o.map.widget.CustomFrameLayout;
import com.lib.sdk.next.o.map.widget.MapDrawView;
import com.lib.sdk.next.robot.IRobotLaserCallBack;
import com.lib.sdk.next.robot.RobotHelper;
import com.lib.sdk.next.robot.RobotLaserDataInfo;
import com.lib.sdk.next.robot.bean.RobotStatusBean;
import com.lib.sdk.next.robot.constant.RobotConstant;
import com.lib.sdk.next.robot.service.RobotStatusService;
import com.lib.sdk.next.tag.NextTag;
import com.lib.sdk.next.util.NumberUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

/**
 * FileName: NextMapView
 * Author: zhikai.jin
 * Date: 2021/6/11 11:47
 * Description: 地图
 */
public class NextMapView extends FrameLayout implements INmap, IRobotLaserCallBack {
    private CustomFrameLayout mCustomFrameLayout;

    private MapDrawView mMapDrawView;

    private NxMap mNextMap;

    private String mCurrentProjectId = "";

    private PositionPointBean mRobotPosition;

    private Timer mRockerDataTimer;

    private WebSocket mRockerSocket;

    /**
     * 虚拟手柄当前角度
     */
    private double mCurrentAngle = 0;
    /**
     * 虚拟手柄当前等级
     */
    private double mCurrentDistanceLevel = 0;

    //摇杆
    private RockerView mRockerView;

    private RobotHelper mRobotHelper;

    private IMapDrawListener mMapDrawListener;

    private IMapTouchListener mMapTouchListener;


    public NextMapView(@NonNull Context context) {
        super(context);
    }

    public NextMapView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NextMapView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    {

        LayoutInflater.from(getContext()).inflate(R.layout.robot_next_map_view, this, true);
        mCustomFrameLayout = findViewById(R.id.custom_view);
        mMapDrawView = findViewById(R.id.map_view);
        mRockerView = findViewById(R.id.robot_rocker_view);
        initEvent();
        mRobotHelper = new RobotHelper();
        mRobotHelper.registerRobotLaserInfo(this);

        mMapDrawView.setOnDrawListener(new MapDrawView.IOnDrawListener() {
            @Override
            public void onDraw(Canvas canvas) {

                if (mMapDrawListener != null) {
                    mMapDrawListener.onDraw(canvas);
                }
            }
        });

        mMapDrawView.setOnViewTouchListener(new MapDrawView.ITouchListener() {
            @Override
            public void onScrollBegin(MotionEvent e, float bitmapX, float bitmapY) {
                if (mMapTouchListener != null) {
                    mMapTouchListener.onScrollBegin(e, bitmapX, bitmapY);
                }
            }

            @Override
            public void onScrollEnd(MotionEvent e, float bitmapX, float bitmapY) {
                if (mMapTouchListener != null) {
                    mMapTouchListener.onScrollEnd(e, bitmapX, bitmapY);
                }
            }

            @Override
            public void onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY, float bitmapX, float bitmapY) {
                if (mMapTouchListener != null) {
                    mMapTouchListener.onScroll(e1, e2, distanceX, distanceY, bitmapX, bitmapY);
                }
            }

            @Override
            public void onSingleTapUp(MotionEvent e, float bitmapX, float bitmapY) {
                if (mMapTouchListener != null) {
                    mMapTouchListener.onSingleTapUp(e, bitmapX, bitmapY);
                }
            }


            @Override
            public void onUpOrCancel(MotionEvent e, float bitmapX, float bitmapY) {
                if (mMapTouchListener != null) {
                    mMapTouchListener.onUpOrCancel(e, bitmapX, bitmapY);
                }
            }

        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EventBus.getDefault().register(this);
        RobotStatusService.start(GlobalOperate.getApp(), RequestManager.mSocketBaseUrl, SocketRequestInterface.ROBOT_STATUS);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateNextRobotStatus(RobotStatusEvent robotStatusEvent) {
        Log.w(NextTag.TAG, "nextMapView robot updateStatus");

        RobotStatusBean robotStatusInfo = robotStatusEvent.getRobotStatusBean();
        Bitmap robotPositionBitmap = ImageCache.getInstance().getIconBitmap(getContext().getResources(), R.drawable.robot_point);

        try {
            JSONObject positionObject = new JSONObject(robotStatusInfo.getPositionJson());
            double resolution = ProjectCacheManager.getMapResolution(GlobalOperate.getApp(), robotStatusInfo.getProjectId());
            double originX = ProjectCacheManager.getMapOriginX(GlobalOperate.getApp(), robotStatusInfo.getProjectId());
            double originY = ProjectCacheManager.getMapOriginY(GlobalOperate.getApp(), robotStatusInfo.getProjectId());

            float x = (float) PositionUtil.serverToLocalX(positionObject.getDouble("x"), resolution, originX);

            float y = (float) PositionUtil.serverToLocalY(positionObject.getDouble("y"), resolution, originY);

            PositionPointBean positionPointBean = new PositionPointBean(robotPositionBitmap, PositionPointBean.TYPE_ROBOT_POINT);

            positionPointBean.setBitmapX(x);
            positionPointBean.setBitmapY(y);
            double serverTheta = positionObject.getDouble("theta");
            double localTheta = 0;

            if (positionPointBean.getTheta() >= -Math.PI / 2 && positionPointBean.getTheta() <= 0) {
                localTheta = Math.PI / 2 - serverTheta;
            }

            if (positionPointBean.getTheta() > 0 && positionPointBean.getTheta() <= Math.PI) {
                localTheta = Math.PI / 2 - serverTheta;
            }

            if (positionPointBean.getTheta() >= -Math.PI && positionPointBean.getTheta() <= -Math.PI / 2) {
                localTheta = -Math.PI * 3f / 2 - serverTheta;
            }

            positionPointBean.setTheta(localTheta);
            positionPointBean.setItemRotate((float) Math.toDegrees(localTheta));
            positionPointBean.setPointName("");
            mMapDrawView.initRobotPosition(positionPointBean);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public NxMap getNxMap() {
        if (mNextMap == null) {
            mNextMap = new NxMap(this);
        }
        return mNextMap;
    }


    @Override
    public View getView() {
        return NextMapView.this;
    }

    /**
     * 展示地图
     *
     * @param projectId
     */
    public void showMap(String projectId) {
        this.mCurrentProjectId = projectId;
        mMapDrawView.post(new Runnable() {
            @Override
            public void run() {
                mMapDrawView.setBitmap(ProjectCacheManager.getMapPictureFilePath(GlobalOperate.getApp(), projectId)
                        , getWidth(), getHeight());
                mMapDrawView.initPositionPointList(ProjectCacheManager.getAllPositionInfo(GlobalOperate.getApp(), projectId));
                mMapDrawView.initVirtualWall(ProjectCacheManager.getObstacleInfo(GlobalOperate.getApp(), projectId));
                mMapDrawView.initRoutes(ProjectCacheManager.getRoutesInfo(GlobalOperate.getApp(), projectId));
                if (projectId.equals(RobotConstant.mRobotStatusBean.getProjectId())) {
                    mMapDrawView.initRobotPosition(null);
                }
            }
        });
        rockerDataSocket();
        if (mRockerDataTimer != null) {
            mRockerDataTimer.cancel();
            mRockerDataTimer = null;
        }
        mRockerDataTimer = new Timer();
        TimerTask mRockerDataTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (mRockerSocket != null
                        && !(mCurrentAngle == 0 && mCurrentDistanceLevel == 0)
                ) { //连接没有中断，且摇杆不在原点，则发送数据到服务器
                    JSONObject params = new JSONObject();
                    try {
                        params.put("position", mCurrentAngle);
                        params.put("offset", mCurrentDistanceLevel);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mRockerSocket.send(params.toString());
                }
            }
        };

        mRockerDataTimer.schedule(mRockerDataTimerTask, 0, 50);//每隔50毫秒发送一次

        //摇杆监听
        mRockerView.setOnAngleDistanceChangeListener(new RockerView.OnAngleDistanceChangeListener() {
            @Override
            public void OnAngleDistanceChange(double angle, int level) {

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
            }
        });
    }


    @Override
    public void setRobotPosition(LocationHelper helper) {

        if (helper.getInitPointType() == LocationHelper.ENFORCE || helper.getInitPointType() == LocationHelper.SMART) {
            mMapDrawView.setChoosen(false);
        } else {
            mMapDrawView.setChoosen(true);
        }
//        mMapDrawView.setEditType(MapDrawView.TYPE_EDIT_TYPE_INIT_LOCATION);
        initLocationPoint(helper);

    }

    @Override
    public MapDrawView getMapDrawView() {
        return mMapDrawView;
    }

    @Override
    public PositionPointBean getPoint() {
        return mMapDrawView.getCurrentOperatePoint();
    }

    @Override
    public VirtualWallBean getVirtualWall() {
        return mMapDrawView.getCurrentOperateWall();
    }

    @Override
    public void onRockViewVisible() {
        mRockerView.setVisibility(VISIBLE);
    }

    @Override
    public void onRockViewHide() {
        mRockerView.setVisibility(GONE);
    }

    @Override
    public void setOnMapDrawListener(IMapDrawListener mapDrawListener) {
        this.mMapDrawListener = mapDrawListener;
    }

    @Override
    public void setOnMapTouchListener(IMapTouchListener mapTouchListener) {
        this.mMapTouchListener = mapTouchListener;
    }


    /**
     * 初始化定位点
     *
     * @param helper
     */
    private void initLocationPoint(LocationHelper helper) {
        mMapDrawView.setOnInitLocationFinishListener(new MapDrawView.OnInitLocationFinishListener() {
            @Override
            public void onFinish(PositionPointBean positionPointBean) {
                Log.e("pointInfo", "界面中的onFinish回调结果：pointname = " + positionPointBean.getPointName() + "\nInitLocationType=" + helper.getClass().getName());
                if (positionPointBean != null && !TextUtils.isEmpty(mCurrentProjectId) && helper instanceof LocationHelper) {

                    if (helper.getInitPointType() == LocationHelper.ENFORCE) {
                        //强制
                        helper.initLocationForce(mCurrentProjectId, positionPointBean);
                    } else if (helper.getInitPointType() == LocationHelper.SMART) {
                        //智能
                        helper.initLocation(mCurrentProjectId, positionPointBean);
                    } else {//选点
                        mMapDrawView.hideNavigationPositionPointName(positionPointBean);
                        //先让点击point显示的弹框消失
                        helper.initLocationChoose(mCurrentProjectId, positionPointBean);
                    }
                }
            }

        });
    }


    private void initEvent() {
        mCustomFrameLayout.setOnRotationListener(new CustomFrameLayout.OnRotationListener() {
            @Override
            public void onRotation(float angle) {
                mMapDrawView.setRotation(angle);
            }
        });

        //最外层地图容器  移动
        mCustomFrameLayout.setOnTranslationListener(new CustomFrameLayout.OnTranslationListener() {
            @Override
            public void onTranslation(float x, float y) {
                mMapDrawView.setTranslationX(mMapDrawView.getTranslationX() + x);
                mMapDrawView.setTranslationY(mMapDrawView.getTranslationY() + y);
            }
        });

        //最外层地图容器  放大缩小
        mCustomFrameLayout.setOnScaleListener(new CustomFrameLayout.OnScaleListener() {
            @Override
            public void onScale(float scale, float x, float y) {
                mMapDrawView.setScale(scale * mMapDrawView.getScale(), mMapDrawView.toX(x), mMapDrawView.toY(y));
            }
        });

        //对MapDrawView地图上的点的点击监听
        mMapDrawView.setOnPositionPointClickListener(new MapDrawView.OnPositionPointClickListener() {
            @Override
            public void onClick(PositionPointBean positionPointBean) {
                mMapDrawView.setEditOperatePoint(positionPointBean);
            }
        });

    }


    @Override
    public void onRobotLaser(RobotLaserDataInfo laserDataInfo) {
        if (laserDataInfo != null) {
            if (mCurrentProjectId.equals(RobotConstant.mRobotStatusBean.getProjectId())) {
                if (mMapDrawView != null) {
                    mMapDrawView.initRobotLaserData(laserDataInfo.getLaserDataBeans());
                }
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mRobotHelper.unRegisterRobotLaser();
        EventBus.getDefault().unregister(this);
        Log.w(NextTag.TAG, "nextMapView robot onDetachedFromWindow");
    }

    /**
     * 虚拟摇杆数据
     */
    private void rockerDataSocket() {
        OkHttpClient client = new OkHttpClient.Builder()
                .build();
        //构造request对象
        Request request = new Request.Builder()
                .url(RequestManager.mSocketBaseUrl + SocketRequestInterface.ROCKER_DATA)
                .build();
        //建立连接
        client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);

                mRockerSocket = webSocket;
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);

            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);

                mRockerSocket = null;
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                super.onFailure(webSocket, t, response);
                mRockerSocket = null;
            }
        });
    }


}

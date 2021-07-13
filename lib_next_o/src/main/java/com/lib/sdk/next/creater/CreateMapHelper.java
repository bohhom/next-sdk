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
import android.util.Log;
import android.view.View;

import com.lib.sdk.next.NextException;
import com.lib.sdk.next.NextResultInfo;
import com.lib.sdk.next.base.IBaseHelper;
import com.lib.sdk.next.livedata.EventConstant;
import com.lib.sdk.next.livedata.LiveDataBus;
import com.lib.sdk.next.livedata.RobotCreateEvent;
import com.lib.sdk.next.livedata.RobotRockerEvent;
import com.lib.sdk.next.o.R;
import com.lib.sdk.next.o.http.HttpResponse;
import com.lib.sdk.next.o.map.bean.PositionPointBean;
import com.lib.sdk.next.o.map.bean.ProjectInfoBean;
import com.lib.sdk.next.o.map.manager.ProjectCacheManager;
import com.lib.sdk.next.o.map.manager.RequestManager;
import com.lib.sdk.next.o.map.net.HttpUri;
import com.lib.sdk.next.o.map.net.SocketRequestInterface;
import com.lib.sdk.next.o.map.util.FileUtils;
import com.lib.sdk.next.o.map.util.ImageCache;
import com.lib.sdk.next.o.map.util.PositionUtil;
import com.lib.sdk.next.o.map.util.ToastUtil;
import com.lib.sdk.next.o.map.widget.MapDrawView;
import com.lib.sdk.next.robot.IRobotLaserCallBack;
import com.lib.sdk.next.robot.IRobotStatusCallBack;
import com.lib.sdk.next.robot.RobotErrorStatusInfo;
import com.lib.sdk.next.robot.RobotHelper;
import com.lib.sdk.next.robot.RobotLaserDataInfo;
import com.lib.sdk.next.robot.RobotNavigationStatusInfo;
import com.lib.sdk.next.robot.RobotStatusInfo;
import com.lib.sdk.next.robot.bean.RobotLaserDataBean;
import com.lib.sdk.next.robot.bean.RobotStatusBean;
import com.lib.sdk.next.robot.constant.RobotConstant;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

/**
 * FileName: CreateMapHelper
 * Author: zhikai.jin
 * Date: 2021/5/26 10:34
 * Description: 创建地图
 */
public class CreateMapHelper extends IBaseHelper<CreateMapPresener> implements ICreateMapCallBack<HttpResponse>, IRobotLaserCallBack, MapCloserCallBack, IRobotStatusCallBack {

    private static volatile CreateMapHelper mInstance;

    private MapControlView mMapControlView;

    private Context mContext;

    private RobotHelper mRobotHelper;

    public static final int CREATE_TYPE_NORMAL = 0;

    public static final int CREATE_TYPE_EXPAND = 1;


    /**
     * 创建地图的参数
     */
    private RobotCreateEvent mRobotCreateEvent;

    /**
     * 地图操作类型
     */
    private int mOperateType = OperateType.NORMAL_TYPE;


    /**
     * 闭环操作类型
     */
    private int mLoopOperateType = LoopOperate.START_LOOP;

    private IOperateListener mOperateListener;


    private CreateMapHelper() {
        this(new CreateMapPresener());
    }

    private CreateMapHelper(CreateMapPresener presener) {
        super(presener);

    }

    public static CreateMapHelper getInstance() {
        if (mInstance == null) {
            synchronized (CreateMapHelper.class) {
                if (mInstance == null) {
                    mInstance = new CreateMapHelper();
                }
            }
        }
        return mInstance;
    }

    public void onCreateView(MapControlView mapControlView, int type, ProjectInfoBean projectInfoBean) {
        this.mMapControlView = mapControlView;
        LiveDataBus.get().with(EventConstant.NEXT_MAP_ROBOT_CREATE, RobotCreateEvent.class).observeForever(this::onDrawData);
        LiveDataBus.get().with(EventConstant.NEXT_MAP_ROBOT_ROCKER, RobotRockerEvent.class).observeForever(this::onRockerListener);
        mRobotHelper = new RobotHelper();
        mRobotHelper.registerRobotLaserInfo(this);
        mRobotHelper.registerRobotStatus(mapControlView.getContext(), this);
        loadMapData(mapControlView.getContext(), type, projectInfoBean);
    }


    /**
     * 设置操作监听
     */
    public void setOperateSource(IOperateListener operateListener) {
        this.mOperateListener = operateListener;
    }


    /**
     * 加载地图
     *
     * @param context
     */
    private synchronized void loadMapData(Context context, int type, ProjectInfoBean projectInfoBean) {
        this.mContext = context;
        mOperateType = OperateType.NORMAL_TYPE;
        mPresenter.setContext(context);
        HashMap<String, Object> params = new HashMap();
        params.put("type", type);
        params.put("project_id", projectInfoBean == null ? "" : projectInfoBean.getProjectId());
        params.put("project_name", projectInfoBean == null ? "" : projectInfoBean.getProjectName());
        params.put("floor", 0);
        mPresenter.operateMap(HttpUri.URL_GENERATE_MAP, params);
    }


    @Override
    public void showErr(String uri, String msg) {

    }


    @Override
    public void attachView(MapDrawView drawView) {

    }

    @Override
    public void operateMapDataCallBack(HttpResponse response) {
        int code = response.code;
        String info = response.info;
        if (code == 0) {
            switch (mOperateType) {
                case OperateType.NORMAL_TYPE:
                    mPresenter.serverBackMapData();
                    break;
                case OperateType.SAVE_TYPE:
                    try {
                        JSONObject data = new JSONObject(response.data);
                        mPresenter.saveMapToLocal(data);
                        mOperateListener.onSave(new NextResultInfo(code, info));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case OperateType.CANCEL_TYPE:
                    mOperateListener.onCancel(new NextResultInfo(code, info));
                    break;

                default:
                    break;
            }
        } else {
            int resultCode = getMsgCode(code);
            switch (mOperateType) {
                case OperateType.SAVE_TYPE:
                    mOperateListener.onSave(new NextResultInfo(resultCode, info));
                    break;
                case OperateType.CANCEL_TYPE:
                    mOperateListener.onCancel(new NextResultInfo(resultCode, info));
                    break;
                default:
                    break;
            }
        }
    }

    private int getMsgCode(int code) {
        int resultCode = -1;
        switch (code) {
            case 1:
                resultCode = NextException.CREATE_PROJECT_FAIL;
                break;
            case 2:
                resultCode = NextException.CREATE_NAV_FAIL;
                break;
            case 3:
                resultCode = NextException.CREATE_CHANGE_FAIL;
                break;
            case 4:
                resultCode = NextException.CREATE_ACTION_FAIL;
                break;

            case 5:
                resultCode = NextException.CREATE_EXPAND_PROJECT_FAIL;
                break;
            case 6:
                resultCode = NextException.CREATE_EXPAND_DATA_FAIL;
                break;
            case 7:
                resultCode = NextException.CREATE_CLEAR_FAIL;
                break;
            default:
                break;
        }
        return resultCode;
    }

    public void onDestroy() {
        LiveDataBus.get().with(EventConstant.NEXT_MAP_ROBOT_CREATE, RobotCreateEvent.class).removeObserver(this::onDrawData);
        LiveDataBus.get().with(EventConstant.NEXT_MAP_ROBOT_ROCKER, RobotRockerEvent.class).removeObserver(this::onRockerListener);
        mRobotHelper.unRegisterRobotLaser();
        mRobotHelper.destroyRobotStatus(mContext);
    }

    private void onDrawData(RobotCreateEvent robotCreateEvent) {
        this.mRobotCreateEvent = robotCreateEvent;
        mMapControlView.setBitmap(robotCreateEvent.getNewMapBitMap(), robotCreateEvent.getOriginSize());
    }

    @Override
    public void onRobotLaser(RobotLaserDataInfo laserDataInfo) {
        try {
            if (mRobotCreateEvent != null && laserDataInfo.getLaserDataBeans() != null) {
                List<RobotLaserDataBean> laserList = new ArrayList<>();
                if (mRobotCreateEvent.getResolution() != 0) {
                    for (int i = 0; i < laserDataInfo.getLaserDataBeans().size(); i++) {
                        RobotLaserDataBean robotLaserDataBean = new RobotLaserDataBean();

                        float x = (float) PositionUtil.serverToLocalX(
                                laserDataInfo.getLaserDataBeans().get(i).getmServerX(),
                                mRobotCreateEvent.getResolution(),
                                mRobotCreateEvent.getOriginX()
                        );
                        float y = (float) PositionUtil.serverToLocalY(laserDataInfo.getLaserDataBeans().get(i).getmServerY(),
                                mRobotCreateEvent.getResolution(),
                                mRobotCreateEvent.getOriginY());
                        robotLaserDataBean.setMapX(x);
                        robotLaserDataBean.setMapY(y);
                        laserList.add(robotLaserDataBean);
                    }

                    mMapControlView.initRobotLaserData(laserList);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void onRockerListener(RobotRockerEvent rockerEvent) {
        mPresenter.setRockValue(rockerEvent.getCurrentAngle(), rockerEvent.getUrrentDistanceLevel());
    }

    /**
     * 开环保存地图
     *
     * @param mapName
     */
    public void onSaveMap(String mapName) {
        mOperateType = OperateType.SAVE_TYPE;
        HashMap<String, Object> params = new HashMap();
        params.put("type", OperateType.SAVE_TYPE);
        params.put("project_id", "");
        params.put("project_name", mapName);
        params.put("floor", 0);
        mPresenter.operateMap(HttpUri.URL_GENERATE_MAP, params);
    }

    public void onCloseOperateMap(int type) {
        mLoopOperateType = type;
        HashMap<String, Object> params = new HashMap();
        params.put("type", type);
        mPresenter.mapCloser(HttpUri.URL_MAP_CLOSER, params);
    }

    public void onCancelMap() {
        mOperateType = OperateType.CANCEL_TYPE;
        HashMap<String, Object> params = new HashMap();
        params.put("type", OperateType.CANCEL_TYPE);
        params.put("project_id", "");
        params.put("project_name", "");
        params.put("floor", 0);
        mPresenter.operateMap(HttpUri.URL_GENERATE_MAP, params);
    }


    @Override
    public void mapCloserDataCallBack(HttpResponse response) {
        String info = response.info;
        int code = response.code;
        if (code == 0) {
            //结束闭环
            if (mLoopOperateType == LoopOperate.END_LOOP) {

            }
            //开始闭环
            else if (mLoopOperateType == LoopOperate.START_LOOP) {

            }
        } else {
            ToastUtil.showShort(mContext, info);
        }
    }

    @Override
    public void onRobotStatus(int code, RobotStatusInfo robotStatusInfo) {
        Bitmap robotPositionBitmap = ImageCache.getInstance().getIconBitmap(mMapControlView.getContext().getResources(), R.drawable.robot_point);

        try {
            JSONObject positionObject = new JSONObject(robotStatusInfo.getPositionJson());
            if (mRobotCreateEvent != null) {
                float x = (float) PositionUtil.serverToLocalX(
                        positionObject.getDouble("x"),
                        mRobotCreateEvent.getResolution(),
                        mRobotCreateEvent.getOriginX()
                );

                float y = (float) PositionUtil.serverToLocalY(positionObject.getDouble("y"),
                        mRobotCreateEvent.getResolution(),
                        mRobotCreateEvent.getOriginY());

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
                mMapControlView.initRobotPosition(positionPointBean);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRobotError(int code, RobotErrorStatusInfo robotErrorInfo) {

    }

    @Override
    public void onRobotNav(int code, RobotNavigationStatusInfo robotNavInfo) {

    }
}

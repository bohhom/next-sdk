
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
package com.lib.sdk.next.base;

import android.graphics.Bitmap;

import com.lib.sdk.next.o.R;
import com.lib.sdk.next.o.map.bean.PositionPointBean;
import com.lib.sdk.next.global.GlobalOperate;
import com.lib.sdk.next.o.map.manager.ProjectCacheManager;
import com.lib.sdk.next.o.map.util.ImageCache;
import com.lib.sdk.next.o.map.util.NetworkUtil;
import com.lib.sdk.next.o.map.util.PositionUtil;
import com.lib.sdk.next.o.map.widget.MapDrawView;
import com.lib.sdk.next.robot.constant.RobotConstant;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * FileName: BaseHelper
 * Author: zhikai.jin
 * Date: 2021/6/2 16:42
 * Description:
 */
public abstract class IBaseHelper<T extends  BasePresenter> {
    protected T mPresenter;

    private String mProjectId = "";

    public IBaseHelper(T basePresenter) {
        this.mPresenter = basePresenter;
        onCreate();
    }

    public IBaseHelper(NxMap nxMap,T basePresenter) {
        this.mPresenter = basePresenter;
        setProjectId(nxMap.getProjectId());
    }

    /**
     * 显示请求错误提示
     */
   public abstract void showErr(String uri, String msg);



    /**
     * 检查网络是否连接
     * @return
     */
   public boolean checkNetWork(){
       if (!NetworkUtil.isConnected()) {
           return false;
       }

       return true;
   }

   protected abstract void attachView(MapDrawView drawView);

   protected void onCreate(NxMap nxMap){
       onCreate();
       nxMap.bindHelper(this);
   }

    protected void onCreate(){
       if(mPresenter!=null){
           mPresenter.attachHelper(this);
       }
    }

    public PositionPointBean robotToPoint(){

        Bitmap robotPositionBitmap = ImageCache.getInstance().getIconBitmap(GlobalOperate.getApp().getResources(), R.drawable.robot_point);
        PositionPointBean positionPointBean = new PositionPointBean(robotPositionBitmap, PositionPointBean.TYPE_ROBOT_POINT);
        try {
            JSONObject positionObject = new JSONObject(RobotConstant.mRobotStatusBean.getPositionJson());
            double resolution = ProjectCacheManager.getMapResolution(GlobalOperate.getApp(), RobotConstant.mRobotStatusBean.getProjectId());
            double originX = ProjectCacheManager.getMapOriginX(GlobalOperate.getApp(), RobotConstant.mRobotStatusBean.getProjectId());
            double originY = ProjectCacheManager.getMapOriginY(GlobalOperate.getApp(), RobotConstant.mRobotStatusBean.getProjectId());

            float x = (float) PositionUtil.serverToLocalX(positionObject.getDouble("x"), resolution, originX);

            float y = (float) PositionUtil.serverToLocalY(positionObject.getDouble("y"), resolution, originY);



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
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return positionPointBean;
    }

    private  void  setProjectId(String projectId){
        this.mProjectId = projectId;
    }

    public String getProjectId(){
       return mProjectId;
    }

}

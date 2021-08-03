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
package com.lib.sdk.next.ease;

import android.graphics.Point;

import com.bozh.logger.Logger;
import com.lib.sdk.next.NextResultInfo;
import com.lib.sdk.next.base.IBaseCallBack;
import com.lib.sdk.next.base.IBaseHelper;
import com.lib.sdk.next.base.NxMap;
import com.lib.sdk.next.o.http.HttpResponse;
import com.lib.sdk.next.global.GlobalOperate;
import com.lib.sdk.next.o.map.manager.ProjectCacheManager;
import com.lib.sdk.next.o.map.net.HttpUri;
import com.lib.sdk.next.o.map.util.ImageCache;
import com.lib.sdk.next.o.map.util.PositionUtil;
import com.lib.sdk.next.o.map.widget.MapDrawView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * FileName: EaseHelper
 * Author: zhikai.jin
 * Date: 2021/6/25 17:41
 * Description: 去噪点
 */
public class EaseHelper extends IBaseHelper<EasePresenter> implements EaseUpdateMapPixCallBack {

    private NxMap mNxMap = null;

    private MapDrawView mMapDrawView;

    private String mCurrentProjectId = "";

    private IEaseListener mEaseListener;

    public EaseHelper(NxMap nxMap) {
        super(nxMap, new EasePresenter());
        onCreate(nxMap);
    }


    @Override
    public void showErr(String uri, int code, String msg) {
        mEaseListener.onHttpError(uri, code, msg);
    }

    @Override
    public void attachView(MapDrawView drawView) {
        this.mMapDrawView = drawView;
    }

    @Override
    public void onCreate(NxMap nxMap) {
        super.onCreate(nxMap);
        this.mNxMap = nxMap;
        mMapDrawView.setEditType(MapDrawView.TYPE_EDIT_ERASE);
        mMapDrawView.setCanUseErase(true);
        nxMap.onRockerViewHide();
    }

    public void setEasePaintWidth(int easePaintWidth) {
        int strokeWidth = (int) (5 + (100 - 5) * ((int) easePaintWidth / 100f));
        mMapDrawView.setErasePaintWidth(strokeWidth);
        mMapDrawView.refresh();
    }

    /**
     * 初始化去噪点服务
     *
     * @param projectId
     */
    public void initEase(String projectId) {
        this.mCurrentProjectId = projectId;
        initMap(true);
    }

    /**
     * 去除噪点
     */
    public void updateMapPixmap() {

        if (mCurrentProjectId.equals("")) {
            Logger.e("橡皮擦未进行初始化");
            return;
        }

        Point point = mMapDrawView.getErasePoint();
        if (point == null) {
            return;
        }

        HashMap<String, Object> params = new HashMap<>();
        params.put("project_id", mCurrentProjectId);
        params.put("floor", 0);
        double resolution = ProjectCacheManager.getMapResolution(GlobalOperate.getApp(), mCurrentProjectId);
        double originX = ProjectCacheManager.getMapOriginX(GlobalOperate.getApp(), mCurrentProjectId);
        double originY = ProjectCacheManager.getMapOriginY(GlobalOperate.getApp(), mCurrentProjectId);
        params.put("x", PositionUtil.localToServerX(
                (double) point.x,
                resolution,
                originX
        ));
        params.put("y", PositionUtil.localToServerY(
                (double) point.y,
                resolution,
                originY
        ));
        params.put("radius", PositionUtil.localToServer(
                mMapDrawView.getErasePaintWidth() / 2f,
                resolution
        ));

        mPresenter.updateMapPixmap(HttpUri.URL_UPDATE_MAP_PIX, params);
    }

    @Override
    public void updateMapPixDataCallBack(HttpResponse response) {

        String info = response.info;
        int code = response.code;
        if (code == 0) {
            //更新本地数据
            try {
                JSONObject jsonObject = new JSONObject(response.data);
                ProjectCacheManager.updateMapPictureFile(GlobalOperate.getApp(), mCurrentProjectId, jsonObject.optString("map_data"));
                String mTempProjectContent = ProjectCacheManager.getUpdateProjectStampContent(GlobalOperate.getApp(), mCurrentProjectId, response.time);
                ProjectCacheManager.updateProject(GlobalOperate.getApp(), mCurrentProjectId, mTempProjectContent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            initMap(true);

        }

        if (mEaseListener != null) {
            mEaseListener.onResult(new NextResultInfo(code, info));
        }
    }

    private void initMap(boolean reloadBitmap) {
        if (reloadBitmap) {
            String path = ProjectCacheManager.getMapPictureFilePath(GlobalOperate.getApp(), mCurrentProjectId);
            ImageCache.getInstance().removeBitmapToMemoryCache(path);
            mMapDrawView.clearErasePoint();
            mNxMap.onShowMapView(mCurrentProjectId);
        }

        mMapDrawView.initPositionPointList(ProjectCacheManager.getAllPositionInfo(GlobalOperate.getApp(), mCurrentProjectId));
        mMapDrawView.initVirtualWall(ProjectCacheManager.getObstacleInfo(GlobalOperate.getApp(), mCurrentProjectId));
        mMapDrawView.initRoutes(ProjectCacheManager.getRoutesInfo(GlobalOperate.getApp(), mCurrentProjectId));

        mMapDrawView.initPositionPointList(ProjectCacheManager.getAllPositionInfo(GlobalOperate.getApp(), mCurrentProjectId));
        mMapDrawView.initVirtualWall(ProjectCacheManager.getObstacleInfo(GlobalOperate.getApp(), mCurrentProjectId));
        mMapDrawView.initRoutes(ProjectCacheManager.getRoutesInfo(GlobalOperate.getApp(), mCurrentProjectId));
    }

    public void setEaseListener(IEaseListener easeListener) {
        this.mEaseListener = easeListener;
    }

    public abstract static class IEaseListener implements IBaseCallBack {

        public abstract void onResult(NextResultInfo resultInfo);
    }

}

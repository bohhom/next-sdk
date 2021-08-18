package com.lib.sdk.next.no.view;

import com.bozh.logger.Logger;
import com.lib.sdk.next.NextException;
import com.lib.sdk.next.NextResultInfo;
import com.lib.sdk.next.base.IBaseCallBack;
import com.lib.sdk.next.base.IBaseHelper;
import com.lib.sdk.next.global.GlobalOperate;
import com.lib.sdk.next.map.MapPresenter;
import com.lib.sdk.next.map.ProjectHelper;
import com.lib.sdk.next.o.http.HttpResponse;
import com.lib.sdk.next.o.map.bean.PositionPointBean;
import com.lib.sdk.next.o.map.manager.ProjectCacheManager;
import com.lib.sdk.next.o.map.net.HttpUri;
import com.lib.sdk.next.o.map.util.PositionUtil;
import com.lib.sdk.next.o.map.widget.MapDrawView;
import com.lib.sdk.next.point.IPushSyncConfigCallBack;
import com.lib.sdk.next.point.PointOperateType;
import com.lib.sdk.next.point.PointPresenter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * FileName: PointNoViewHelper
 * Author: zhikai.jin
 * Date: 2021/8/12 9:07
 * Description: 没有界面的点操作
 */
public class PointNoViewHelper extends IBaseHelper<PointNoViewPresenter> implements IPushSyncConfigCallBack {
    private static volatile PointNoViewHelper mInstance;

    private List<PositionPointBean> mAllTypePointList;

    private int currentOperateType = PointOperateType.TYPE_SAVE;

    private String mProjectId = "";

    private IPointNoViewListener mPointNoViewListener;

    private PointNoViewHelper(PointNoViewPresenter basePresenter) {
        super(basePresenter);
    }


    public static PointNoViewHelper getInstance() {
        if (mInstance == null) {
            synchronized (ProjectHelper.class) {
                if (mInstance == null) {
                    mInstance = new PointNoViewHelper();
                }
            }
        }
        return mInstance;
    }


    private PointNoViewHelper() {
        this(new PointNoViewPresenter());
    }


    @Override
    protected void onCreate() {
        super.onCreate();
    }

    @Override
    public void showErr(String uri, int code, String msg) {
        if (mPointNoViewListener != null) {
            mPointNoViewListener.onHttpError(uri, code, msg);
        } else {
            Logger.e("PointNoViewHelper callback is null");
        }
    }

    @Override
    protected void attachView(MapDrawView drawView) {

    }

    @Override
    public void pushSyncConfigDataCallBack(HttpResponse data) {
        String mTempProjectContent = ProjectCacheManager.getUpdateProjectStampContent(GlobalOperate.getApp(), mProjectId);
        if (data.code == 0) {
            switch (currentOperateType) {
                case PointOperateType.TYPE_SAVE: {
                    //本地更新标记点信息
                    String positionInfoJson = PositionUtil.positionPointListToServerJsonStr(GlobalOperate.getApp(), mProjectId, mAllTypePointList);
                    ProjectCacheManager.updatePositionInfoFile(GlobalOperate.getApp(), mProjectId, positionInfoJson);
                    ProjectCacheManager.updateProject(GlobalOperate.getApp(), mProjectId, mTempProjectContent);
                    if (mPointNoViewListener != null) {
                        mPointNoViewListener.addPoint(new NextResultInfo(NextException.CODE_NEXT_SUCCESS, data.info), mProjectId, mAllTypePointList);
                    }
                }
                break;
                default:
                    break;
            }
        }
        else{
            if (mPointNoViewListener != null) {
                mPointNoViewListener.addPoint(new NextResultInfo(NextException.CODE_NEXT_FAIL, data.info), mProjectId, null);
            }
        }
    }


    /**
     * 添加点
     *
     * @param projectId
     * @param pointBeans
     */
    public void onAddPointNoView(String projectId, List<PositionPointBean> pointBeans) {
        currentOperateType = PointOperateType.TYPE_SAVE;
        mProjectId = projectId;
        mAllTypePointList = ProjectCacheManager.getAllPositionInfo(GlobalOperate.getApp(), projectId);
        if (mAllTypePointList != null) {
            for (int i = 0; i < pointBeans.size(); i++) {
                if (!PositionUtil.isPositionPointNameExist(pointBeans.get(i).getPointName(), mAllTypePointList)) {
                    //保存到服务器的点
                    mAllTypePointList.add(pointBeans.get(i));
                    pushSyncConfig(mAllTypePointList);
                }
            }
        }

    }


    /**
     * 航点保存至服务器
     */
    private void pushSyncConfig(final List<PositionPointBean> updatePositionList) {
        JSONObject params = new JSONObject();
        try {
            String mTempProjectContent = ProjectCacheManager.getUpdateProjectStampContent(GlobalOperate.getApp(), mProjectId);
            params.put("project_info", new JSONObject(mTempProjectContent));
            params.put("obstacles", new JSONObject(ProjectCacheManager.getObstacleCacheFileOriContent(GlobalOperate.getApp(), mProjectId)));
            params.put("positions", new JSONObject(PositionUtil.positionPointListToServerJsonStr(GlobalOperate.getApp(), mProjectId, updatePositionList)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mPresenter.pushSyncConfig(HttpUri.URL_PUSH_SYNC_CONFIG, params);
    }


    public void setOperatePointListener(IPointNoViewListener pointListener){
        this.mPointNoViewListener = pointListener;

    }

    public abstract static class IPointNoViewListener implements IBaseCallBack {
        public abstract  void addPoint(NextResultInfo resultInfo, String projectId,List<PositionPointBean> pointBeans);
    }
}

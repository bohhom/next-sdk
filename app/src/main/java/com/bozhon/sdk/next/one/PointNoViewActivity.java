package com.bozhon.sdk.next.one;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bozhon.sdk.next.one.databinding.ActivityPointNavBinding;
import com.bozhon.sdk.next.one.databinding.ActivityPointNoViewBinding;
import com.lib.sdk.next.NextException;
import com.lib.sdk.next.NextResultInfo;
import com.lib.sdk.next.NextSDKHelper;
import com.lib.sdk.next.base.NxMap;
import com.lib.sdk.next.no.view.PointNoViewHelper;
import com.lib.sdk.next.o.map.bean.PositionPointBean;
import com.lib.sdk.next.o.map.util.PositionUtil;
import com.lib.sdk.next.point.PointNavHelper;
import com.lib.sdk.next.robot.constant.RobotConstant;
import com.lib.sdk.next.tag.NextTag;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * FileName: PointNoViewActivity
 * Author: zhikai.jin
 * Date: 2021/8/12 9:45
 * Description:
 */
public class PointNoViewActivity extends AppCompatActivity {

    private ActivityPointNoViewBinding dataBinding;

    @Override
    protected void onCreate(@Nullable  Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBinding = ActivityPointNoViewBinding.inflate(getLayoutInflater());
        View view = dataBinding.getRoot();
        setContentView(view);
        NxMap nxMap =  dataBinding.nextMapView.getNxMap();
        nxMap.onShowMapView(RobotConstant.mRobotStatusBean.getProjectId());

        PointNoViewHelper.getInstance().setOperatePointListener(new PointNoViewHelper.IPointNoViewListener() {
            @Override
            public void onHttpError(String url, int code, String msg) {

            }

            @Override
            public void addPoint(NextResultInfo resultInfo, String projectId, List<PositionPointBean> pointBeans) {
                if(resultInfo.getResultCode() == NextException.CODE_NEXT_SUCCESS){
                    Log.w(NextTag.TAG, "addPoint success   =" + pointBeans.get(pointBeans.size() - 1).getPointName());
                }
            }
        });

        dataBinding.createByTouchPointButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JSONObject positionObject = null;
                try {
                    positionObject = new JSONObject(RobotConstant.mRobotStatusBean.getPositionJson());
                    double  originX = NextSDKHelper.getInstance().getMapOriginX(PointNoViewActivity.this,RobotConstant.mRobotStatusBean.getProjectId());
                    double  originY = NextSDKHelper.getInstance().getMapOriginY(PointNoViewActivity.this,RobotConstant.mRobotStatusBean.getProjectId());
                    double  resolution = NextSDKHelper.getInstance().getMapResolution(PointNoViewActivity.this,RobotConstant.mRobotStatusBean.getProjectId());
                    float x = (float) PositionUtil.serverToLocalX(positionObject.getDouble("x"), resolution, originX);

                    float y = (float) PositionUtil.serverToLocalY(positionObject.getDouble("y"), resolution, originY);
                    String pointName = String.valueOf(dataBinding.createPointName.getText());

                    PositionPointBean pointBean = new PositionPointBean();
                    pointBean.setType(PositionPointBean.TYPE_NAVIGATION_POINT);
                    pointBean.setPointName(pointName);
                    pointBean.setBitmapX(x);
                    pointBean.setBitmapY(y);
                    List<PositionPointBean> pointBeanList = new ArrayList<>();
                    pointBeanList.add(pointBean);
                    PointNoViewHelper.getInstance().onAddPointNoView(RobotConstant.mRobotStatusBean.getProjectId(),pointBeanList);
                } catch (JSONException jsonException) {
                    jsonException.printStackTrace();
                }


            }
        });
    }
}

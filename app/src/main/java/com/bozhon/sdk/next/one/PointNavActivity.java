package com.bozhon.sdk.next.one;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bozhon.sdk.next.one.databinding.ActivityPointNavBinding;
import com.lib.sdk.next.NextResultInfo;
import com.lib.sdk.next.base.NxMap;
import com.lib.sdk.next.global.GlobalOperate;
import com.lib.sdk.next.o.map.bean.PositionPointBean;
import com.lib.sdk.next.o.map.manager.ProjectCacheManager;
import com.lib.sdk.next.point.PointHelper;
import com.lib.sdk.next.point.PointNavHelper;
import com.lib.sdk.next.robot.constant.RobotConstant;
import com.lib.sdk.next.tag.NextTag;

import java.util.ArrayList;
import java.util.List;

/**
 * FileName: PointNavActivity
 * Author: zhikai.jin
 * Date: 2021/6/22 14:44
 * Description: 导航点
 */
public class PointNavActivity extends AppCompatActivity {
    private ActivityPointNavBinding dataBinding;

    @Override
    protected void onCreate(@Nullable  Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBinding = ActivityPointNavBinding.inflate(getLayoutInflater());
        View view = dataBinding.getRoot();
        setContentView(view);
        NxMap nxMap =  dataBinding.nextMapView.getNxMap();
        nxMap.onShowMapView(RobotConstant.mRobotStatusBean.getProjectId());
        PointNavHelper pointNavHelper = new PointNavHelper(nxMap);

//        pointNavHelper.setNavPointListener(new PointHelper.INavPointListener() {
//            @Override
//            public void onHttpError(String url, int code, String msg) {
//
//            }
//
//
//            @Override
//            public void onCreateNavPointSuccess(PositionPointBean pointBean) {
//                Log.w(NextTag.TAG, "创建点成功"  +  pointBean.getPointName());
//            }
//
//            @Override
//            public void onCreateNavPointFail(NextResultInfo resultInfo) {
//                Log.w(NextTag.TAG, "创建点失败"  +  resultInfo.getResultMsg());
//            }
//
//            @Override
//            public void onEdNavPointSuccess(PositionPointBean pointBean) {
//
//            }
//
//            @Override
//            public void onEdNavPointFail(NextResultInfo resultInfo) {
//
//            }
//
//            @Override
//            public void onUpdateNameSuccess(PositionPointBean pointBean) {
//
//            }
//
//            @Override
//            public void onUpdateNameFail(NextResultInfo resultInfo) {
//
//            }
//
//            @Override
//            public void onDeleteSuccess() {
//
//            }
//
//            @Override
//            public void onDeleteFail(NextResultInfo resultInfo) {
//
//            }
//        });

        dataBinding.createByTouchPointButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = String.valueOf(dataBinding.createPointName.getText());
                pointNavHelper.onAddPointTouch(text);

            }
        });

        dataBinding.saveByTouchPointButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pointNavHelper.onSavePointTouch();
            }
        });

        dataBinding.createByRobotPointButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = String.valueOf(dataBinding.createPointName.getText());
                pointNavHelper.onAddPointRobot(text);

            }
        });

        dataBinding.editPointButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PositionPointBean pointBean =  nxMap.getPoint();
                pointNavHelper.onEdPoint(pointBean);
            }
        });

        dataBinding.deletePointBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               List<PositionPointBean> mAllTypePointList = ProjectCacheManager.getAllPositionInfo(GlobalOperate.getApp(), RobotConstant.mRobotStatusBean.getProjectId());
               List<PositionPointBean> navPositions = new ArrayList<>();
               for (int i = 0; i < mAllTypePointList.size(); i++){
                   if(mAllTypePointList.get(i).getType() == PositionPointBean.TYPE_NAVIGATION_POINT){
                       navPositions.add(mAllTypePointList.get(i));
                   }
               }
               pointNavHelper.onDeletePoint(navPositions);
            }
        });

        dataBinding.editNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = String.valueOf(dataBinding.editNameEd.getText());
                PositionPointBean pointBean =  nxMap.getPoint();
                pointNavHelper.onUpdatePoint(pointBean,text);
            }
        });
    }
}

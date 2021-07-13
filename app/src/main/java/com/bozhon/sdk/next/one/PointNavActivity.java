package com.bozhon.sdk.next.one;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bozhon.sdk.next.one.databinding.ActivityPointNavBinding;
import com.lib.sdk.next.NextResultInfo;
import com.lib.sdk.next.base.NxMap;
import com.lib.sdk.next.o.map.bean.PositionPointBean;
import com.lib.sdk.next.point.PointHelper;
import com.lib.sdk.next.point.PointNavHelper;
import com.lib.sdk.next.robot.constant.RobotConstant;

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

        pointNavHelper.setNavPointListener(new PointHelper.INavPointListener() {
            @Override
            public void onCreateNavPointSuccess(PositionPointBean pointBean) {

            }

            @Override
            public void onCreateNavPointFail(NextResultInfo resultInfo) {

            }

            @Override
            public void onEdNavPointSuccess(PositionPointBean pointBean) {

            }

            @Override
            public void onEdNavPointFail(NextResultInfo resultInfo) {

            }

            @Override
            public void onUpdateNameSuccess(PositionPointBean pointBean) {

            }

            @Override
            public void onUpdateNameFail(NextResultInfo resultInfo) {

            }

            @Override
            public void onDeleteSuccess() {

            }

            @Override
            public void onDeleteFail(NextResultInfo resultInfo) {

            }
        });

        dataBinding.createByTouchPointButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = String.valueOf(dataBinding.createPointName.getText());
                pointNavHelper.onAddPointTouch(text);
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
                PositionPointBean pointBean =  nxMap.getPoint();
                pointNavHelper.onDeletePoint(pointBean);
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

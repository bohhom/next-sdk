package com.bozhon.sdk.next.one;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bozhon.sdk.next.one.databinding.ActivityPointInitBinding;
import com.lib.sdk.next.base.NxMap;
import com.lib.sdk.next.point.PointInitHelper;
import com.lib.sdk.next.robot.constant.RobotConstant;

/**
 * FileName: PointInitActivity
 * Author: zhikai.jin
 * Date: 2021/6/18 11:18
 * Description: 初始点
 */
public class PointInitActivity extends AppCompatActivity {

    private ActivityPointInitBinding dataBinding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBinding = ActivityPointInitBinding.inflate(getLayoutInflater());
        View view = dataBinding.getRoot();
        setContentView(view);
        NxMap nxMap =  dataBinding.nextMapView.getNxMap();
        nxMap.onShowMapView(RobotConstant.mRobotStatusBean.getProjectId());
        PointInitHelper pointInitHelper = new PointInitHelper(nxMap);
        dataBinding.createPointButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pointInitHelper.onAddPoint("你好");
            }
        });
    }
}

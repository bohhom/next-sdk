package com.bozhon.sdk.next.one;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bozhon.sdk.next.one.databinding.ActivityGpsMapBinding;
import com.lib.sdk.next.NextResultInfo;
import com.lib.sdk.next.gps.LocationHelper;
import com.lib.sdk.next.base.NxMap;
import com.lib.sdk.next.robot.IRobotStatusCallBack;
import com.lib.sdk.next.robot.RobotErrorStatusInfo;
import com.lib.sdk.next.robot.RobotHelper;
import com.lib.sdk.next.robot.RobotNavigationStatusInfo;
import com.lib.sdk.next.robot.RobotStatusInfo;
import com.lib.sdk.next.robot.constant.RobotConstant;


/**
 * FileName: GpsActivity
 * Author: zhikai.jin
 * Date: 2021/6/11 15:19
 * Description: 定位
 */
public class GpsActivity extends AppCompatActivity {

    private ActivityGpsMapBinding dataBinding;

    private RobotHelper mRobotHelper = new RobotHelper();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBinding = ActivityGpsMapBinding.inflate(getLayoutInflater());
        View view = dataBinding.getRoot();
        setContentView(view);
        NxMap nxMap = dataBinding.nextMapView.getNxMap();
        nxMap.onShowMapView(RobotConstant.mRobotStatusBean.getProjectId());
        LocationHelper locationHelper = new LocationHelper(nxMap);

        locationHelper.setLocationListener(new LocationHelper.OnLocationListener() {
            @Override
            public void onSuccess(int type) {
                Log.w("LocationHelper","LocationHelper is success + type= " + type);
            }

            @Override
            public void onFail(int type, NextResultInfo info) {
                Log.w("LocationHelper","LocationHelper is success + type= " + type + "fail = "+ info.getResultMsg());
            }
        });


        mRobotHelper.registerRobotStatus(GpsActivity.this, new IRobotStatusCallBack() {

            @Override
            public void onRobotStatus(int code, RobotStatusInfo robotStatusInfo) {

            }

            @Override
            public void onRobotError(int code, RobotErrorStatusInfo robotErrorInfo) {

            }

            @Override
            public void onRobotNav(int code, RobotNavigationStatusInfo robotNavInfo) {

            }
        });


        dataBinding.smartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(GpsActivity.this,"智能初始化",Toast.LENGTH_SHORT).show();
                locationHelper.setInitPointType(LocationHelper.SMART);
                nxMap.setRobotPosition(locationHelper);
            }
        });

        dataBinding.enfoceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(GpsActivity.this,"强制初始化",Toast.LENGTH_SHORT).show();
                locationHelper.setInitPointType(LocationHelper.ENFORCE);
                nxMap.setRobotPosition(locationHelper);
            }
        });

        dataBinding.selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(GpsActivity.this,"选点初始化",Toast.LENGTH_SHORT).show();
                locationHelper.setInitPointType(LocationHelper.SELECT);
                nxMap.setRobotPosition(locationHelper);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRobotHelper.destroyRobotStatus(GpsActivity.this);
    }
}

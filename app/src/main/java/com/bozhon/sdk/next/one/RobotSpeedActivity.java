package com.bozhon.sdk.next.one;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bozhon.sdk.next.one.databinding.ActivityRobotSpeedUiBinding;
import com.lib.sdk.next.NextResultInfo;
import com.lib.sdk.next.base.NxMap;
import com.lib.sdk.next.robot.bean.RobotStatusBean;
import com.lib.sdk.next.robot.constant.RobotConstant;
import com.lib.sdk.next.setting.SettingHelper;

/**
 * FileName: RobotSpeedActivity
 * Author: zhikai.jin
 * Date: 2021/6/28 15:13
 * Description: 设置机器人速度
 */
public class RobotSpeedActivity extends AppCompatActivity {
    private ActivityRobotSpeedUiBinding dataBinding;
    private RobotSpeedViewModel mRobotSpeedViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBinding = ActivityRobotSpeedUiBinding.inflate(getLayoutInflater());
        View view = dataBinding.getRoot();
        setContentView(view);
        NxMap nxMap = dataBinding.nextMapView.getNxMap();
        nxMap.onShowMapView(RobotConstant.mRobotStatusBean.getProjectId());

        SettingHelper.getInstance().setRobotSpeedListener(new SettingHelper.IRobotSpeedListener() {
            @Override
            public void onHttpError(String url, int code, String msg) {
                Toast.makeText(RobotSpeedActivity.this, "报错动作 = +" + url + "|code = " + code + "|msg =" + msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSetRobotSpeed(NextResultInfo resultInfo) {

            }

            @Override
            public void onGetSuccessRobotSpeed(double speedX, double speedTheta) {

            }

            @Override
            public void onGetFailedRobotSpeed(NextResultInfo resultInfo) {

            }
        });

        mRobotSpeedViewModel = new ViewModelProvider(this).get(RobotSpeedViewModel.class);
        dataBinding.robotGetSpeedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingHelper.getInstance().requestRobotSpeed();
                mRobotSpeedViewModel.getRobotStatusBean().setValue(RobotConstant.mRobotStatusBean);

            }
        });

        dataBinding.robotSetSpeedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lineValue = String.valueOf(dataBinding.robotLineValueEd.getText());
                String angularValue = String.valueOf(dataBinding.robotValueEd.getText());
                SettingHelper.getInstance().setRobotSpeed(Double.valueOf(lineValue),Double.valueOf(angularValue));
            }
        });

        mRobotSpeedViewModel.getRobotStatusBean().observe(this, new Observer<RobotStatusBean>() {
            @Override
            public void onChanged(RobotStatusBean robotStatusBean) {
                dataBinding.robotLineValueEd.setText(String.valueOf(robotStatusBean.getLineSpeed()));
                dataBinding.robotValueEd.setText(String.valueOf(robotStatusBean.getAngularSpeed()));
            }
        });
    }
}

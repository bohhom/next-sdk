package com.bozhon.sdk.next.one;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bozhon.sdk.next.one.databinding.ActivityEaseUiBinding;
import com.bozhon.sdk.next.one.databinding.ActivityRobotStatusUiBinding;
import com.lib.sdk.next.robot.IRobotStatusCallBack;
import com.lib.sdk.next.robot.RobotErrorStatusInfo;
import com.lib.sdk.next.robot.RobotHelper;
import com.lib.sdk.next.robot.RobotNavigationStatusInfo;
import com.lib.sdk.next.robot.RobotStatusInfo;

/**
 * FileName: RobotStatusActivity
 * Author: zhikai.jin
 * Date: 2021/7/2 9:57
 * Description: 机器人状态
 */
public class RobotStatusActivity extends AppCompatActivity {
    private ActivityRobotStatusUiBinding dataBinding;
    private RobotHelper robotHelper = new RobotHelper();
    @Override
    protected void onCreate(@Nullable  Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBinding = ActivityRobotStatusUiBinding.inflate(getLayoutInflater());
        View view = dataBinding.getRoot();
        setContentView(view);

        robotHelper.registerRobotStatus(RobotStatusActivity.this,new IRobotStatusCallBack() {
            @Override
            public void onRobotStatus(int code,RobotStatusInfo robotStatusInfo) {
                dataBinding.textView.setText("工程id = " + robotStatusInfo.getProjectId()+"|机器人状态 =" + robotStatusInfo.getPositionJson() +"| =" + System.currentTimeMillis());
            }

            @Override
            public void onRobotError(int code,RobotErrorStatusInfo robotErrorInfo) {

            }

            @Override
            public void onRobotNav(int code,RobotNavigationStatusInfo robotNavInfo) {

            }
        });

        robotHelper.registerRobotStatus(RobotStatusActivity.this,new IRobotStatusCallBack() {
            @Override
            public void onRobotStatus(int code,RobotStatusInfo robotStatusInfo) {

                dataBinding.textView.setText("工程id = " + robotStatusInfo.getProjectId()+"|机器人世界坐标 =" + robotStatusInfo.getRobotPostionBean().getWorldX()+"|Y = " + "|机器人世界坐标 =" + robotStatusInfo.getRobotPostionBean().getWorldY() + "|机器人方向角 ="+robotStatusInfo.getRobotPostionBean().getTheta());

            }

            @Override
            public void onRobotError(int code,RobotErrorStatusInfo robotErrorInfo) {

            }

            @Override
            public void onRobotNav(int code,RobotNavigationStatusInfo robotNavInfo) {

            }
        });


        dataBinding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RobotStatusActivity.this,GpsActivity.class);
                startActivity(intent);

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        robotHelper.destroyRobotStatus(RobotStatusActivity.this);
    }
}

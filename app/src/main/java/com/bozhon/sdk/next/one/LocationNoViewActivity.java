package com.bozhon.sdk.next.one;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.bozhon.sdk.next.one.databinding.ActivityLocationNoViewUiBinding;
import com.lib.sdk.next.NextException;
import com.lib.sdk.next.NextResultInfo;
import com.lib.sdk.next.NextSDKHelper;
import com.lib.sdk.next.global.GlobalOperate;
import com.lib.sdk.next.o.map.bean.PositionPointBean;
import com.lib.sdk.next.o.map.manager.ProjectCacheManager;
import com.lib.sdk.next.o.map.net.HttpUri;
import com.lib.sdk.next.o.map.util.PositionUtil;
import com.lib.sdk.next.operate.NextOperateHelper;
import com.lib.sdk.next.robot.constant.RobotConstant;
import com.lib.sdk.next.tag.NextTag;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * FileName: LocationNoViewActivity
 * Author: zhikai.jin
 * Date: 2021/8/1 13:33
 * Description: 无界面定位
 */
public class LocationNoViewActivity extends AppCompatActivity {

    private ActivityLocationNoViewUiBinding dataBinding;
    @Override
    protected void onCreate(@Nullable  Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBinding = ActivityLocationNoViewUiBinding.inflate(getLayoutInflater());
        View view = dataBinding.getRoot();
        setContentView(view);

        double worldX = RobotConstant.mRobotStatusBean.getRobotPostionBean().getWorldX();
        double worldY = RobotConstant.mRobotStatusBean.getRobotPostionBean().getWorldY();
        double theta = RobotConstant.mRobotStatusBean.getRobotPostionBean().getTheta();


        NextOperateHelper.getInstance().setRobotOperateListener(new NextOperateHelper.IRobotOperateListener() {
            @Override
            public void onSet2DNavResult(NextResultInfo resultInfo) {

            }

            @Override
            public void onCancel2DNavResult(NextResultInfo resultInfo) {

            }

            @Override
            public void onCommandResult(int type, NextResultInfo resultInfo) {

            }

            @Override
            public void onHttpError(String url, int code, String msg) {
                Toast.makeText(LocationNoViewActivity.this,"机器人操作失败",Toast.LENGTH_SHORT).show();
            }
        });

        NextOperateHelper.getInstance().setRobotLocationListener(new NextOperateHelper.IRobotLocationListener() {
            @Override
            public void onLocationResult(int type, NextResultInfo resultInfo) {

            }

            @Override
            public void onHttpError(String url, int code, String msg) {
                Toast.makeText(LocationNoViewActivity.this,"机器人定位失败",Toast.LENGTH_SHORT).show();
            }
        });

        dataBinding.locationEnforceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NextOperateHelper.getInstance().onEnforceLocation(worldX,worldY,theta);
            }
        });

        dataBinding.locationSmart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NextOperateHelper.getInstance().onSmartLocation(worldX,worldY,theta);
            }
        });
    }

}

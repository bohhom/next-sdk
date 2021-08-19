package com.bozhon.sdk.next.one;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bozhon.sdk.next.one.databinding.ActivityCreateExpandUiBinding;
import com.bozhon.sdk.next.one.databinding.ActivityMapOperateUiBinding;
import com.lib.sdk.next.NextResultInfo;
import com.lib.sdk.next.NextSDKHelper;
import com.lib.sdk.next.creater.CreateMapHelper;
import com.lib.sdk.next.creater.IOperateListener;
import com.lib.sdk.next.creater.LoopOperate;
import com.lib.sdk.next.global.GlobalOperate;
import com.lib.sdk.next.o.map.bean.ProjectInfoBean;
import com.lib.sdk.next.o.map.manager.ProjectCacheManager;
import com.lib.sdk.next.robot.constant.RobotConstant;

import java.util.List;


/**
 * FileName: CreateMapExpandActivity
 * Author: zhikai.jin
 * Date: 2021/6/29 15:47
 * Description: 拓展地图
 */
public class CreateMapExpandActivity extends AppCompatActivity {

    private ActivityCreateExpandUiBinding dataBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBinding = ActivityCreateExpandUiBinding.inflate(getLayoutInflater());
        View view = dataBinding.getRoot();
        setContentView(view);

        ProjectInfoBean selectProjectBean = NextSDKHelper.getInstance().getProjectInfo(RobotConstant.mRobotStatusBean.getProjectId());

        CreateMapHelper.getInstance().onCreateView(dataBinding.robotCreateView,CreateMapHelper.CREATE_TYPE_EXPAND, selectProjectBean);
        CreateMapHelper.getInstance().setOperateSource(new IOperateListener() {
            @Override
            public void onHttpError(String url, int code, String msg) {

            }

            @Override
            public void onSave(NextResultInfo resultInfo) {

            }

            @Override
            public void onCancel(NextResultInfo resultInfo) {

            }

            @Override
            public void onClose(int type, NextResultInfo resultInfo) {

            }
        });

        dataBinding.closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateMapHelper.getInstance().onCloseOperateMap(LoopOperate.START_LOOP);
            }
        });

        dataBinding.cancleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateMapHelper.getInstance().onCancelMap();
            }
        });
        dataBinding.closeEndBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateMapHelper.getInstance().onCloseOperateMap(LoopOperate.END_LOOP);
            }
        });
        dataBinding.openBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateMapHelper.getInstance().onSaveMap("sdk 地图");
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CreateMapHelper.getInstance().onDestroy();
    }
}

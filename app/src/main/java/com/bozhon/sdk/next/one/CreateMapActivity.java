package com.bozhon.sdk.next.one;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bozhon.sdk.next.one.databinding.ActivityCreateMapBinding;
import com.lib.sdk.next.NextResultInfo;
import com.lib.sdk.next.creater.CreateMapHelper;
import com.lib.sdk.next.creater.IOperateListener;
import com.lib.sdk.next.creater.LoopOperate;
import com.lib.sdk.next.creater.OperateType;
import com.lib.sdk.next.operate.NextOperateHelper;

/**
 * FileName: CreateMapActivity
 * Author: zhikai.jin
 * Date: 2021/6/8 15:21
 * Description: 创建地图
 */
public class CreateMapActivity extends AppCompatActivity {
    private ActivityCreateMapBinding dataBinding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBinding = ActivityCreateMapBinding.inflate(getLayoutInflater());
        View view = dataBinding.getRoot();
        setContentView(view);
        CreateMapHelper.getInstance().onCreateView(dataBinding.robotCreateView,CreateMapHelper.CREATE_TYPE_NORMAL,null);
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

        dataBinding.restBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NextOperateHelper.getInstance().onRestAndStopCommand();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CreateMapHelper.getInstance().onDestroy();
    }
}

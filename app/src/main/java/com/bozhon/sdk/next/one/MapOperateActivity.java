package com.bozhon.sdk.next.one;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bozhon.sdk.next.one.databinding.ActivityMapOperateUiBinding;
import com.lib.sdk.next.NextException;
import com.lib.sdk.next.NextResultInfo;
import com.lib.sdk.next.NextSDKHelper;
import com.lib.sdk.next.base.NxMap;
import com.lib.sdk.next.map.ProjectHelper;
import com.lib.sdk.next.o.map.bean.ProjectInfoBean;
import com.lib.sdk.next.robot.constant.RobotConstant;

/**
 * FileName: MapOperateActivity
 * Author: zhikai.jin
 * Date: 2021/6/29 10:25
 * Description: 地图操作
 */
public class MapOperateActivity extends AppCompatActivity {
    private ActivityMapOperateUiBinding dataBinding;
    private String otherProjectId = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBinding = ActivityMapOperateUiBinding.inflate(getLayoutInflater());
        View view = dataBinding.getRoot();
        setContentView(view);
        NxMap nextMap = dataBinding.nextMapView.getNxMap();
        nextMap.onShowMapView(RobotConstant.mRobotStatusBean.getProjectId());
        nextMap.onRockerViewHide();


        ProjectHelper.getInstance().setIMapOperResultListener(new ProjectHelper.IMapOperResultListener() {

            @Override
            public void onHttpError(String url, int code, String msg) {
                Toast.makeText(MapOperateActivity.this, "报错动作 = +" + url + "|code = " + code + "|msg =" + msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChangeProjectResult(NextResultInfo resultInfo) {

                if (resultInfo.getResultCode() == NextException.CODE_NEXT_SUCCESS) {
                    Toast.makeText(MapOperateActivity.this, "切换工程成功", Toast.LENGTH_SHORT).show();
                } else if (resultInfo.getResultCode() == NextException.PROJECT_NOT_CHANGE) {
                    Toast.makeText(MapOperateActivity.this, "相同工程不能切换", Toast.LENGTH_SHORT).show();
                } else if (resultInfo.getResultCode() == NextException.PROJECT_LOAD_FAIL) {
                    Toast.makeText(MapOperateActivity.this, "工程加载失败", Toast.LENGTH_SHORT).show();
                } else if (resultInfo.getResultCode() == NextException.PROJECT_CHANGE_FAIL) {
                    Toast.makeText(MapOperateActivity.this, "PROJECT_CHANGE_FAIL", Toast.LENGTH_SHORT).show();
                } else if (resultInfo.getResultCode() == NextException.PROJECT_TASK_OTHER) {
                    Toast.makeText(MapOperateActivity.this, "正在建图", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MapOperateActivity.this, "服务端请求失败", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onChangeProjectName(NextResultInfo resultInfo) {
                if (resultInfo.getResultCode() == NextException.CODE_NEXT_SUCCESS) {
                    Toast.makeText(MapOperateActivity.this, "更换名称成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MapOperateActivity.this, "更换名称失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onUploadProject(NextResultInfo resultInfo) {
                if (resultInfo.getResultCode() == NextException.CODE_NEXT_SUCCESS) {
                    Toast.makeText(MapOperateActivity.this, "上传工程成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MapOperateActivity.this, "上传工程失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onDeleteProject(NextResultInfo resultInfo) {
                if (resultInfo.getResultCode() == NextException.CODE_NEXT_SUCCESS) {
                    Toast.makeText(MapOperateActivity.this, "删除工程成功", Toast.LENGTH_SHORT).show();
                } else if (resultInfo.getResultCode() == NextException.PROJECT_NOT_DELETE) {
                    Toast.makeText(MapOperateActivity.this, "当前工程不能删除", Toast.LENGTH_SHORT).show();
                } else if (resultInfo.getResultCode() == NextException.PROJECT_NOT_EXIT) {
                    Toast.makeText(MapOperateActivity.this, "服务器不存在该共工程", Toast.LENGTH_SHORT).show();
                } else if (resultInfo.getResultCode() == NextException.PROJECT_DELETE_FAIL) {
                    Toast.makeText(MapOperateActivity.this, "工程删除失败", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MapOperateActivity.this, "服务端请求失败", Toast.LENGTH_SHORT).show();
                }

            }
        });


        for (ProjectInfoBean project : NextSDKHelper.getInstance().getAllProject()) {
            otherProjectId = project.getProjectId();
            break;
        }

        dataBinding.changeProjectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProjectHelper.getInstance().changeProject(otherProjectId);
            }
        });

        dataBinding.changeNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProjectHelper.getInstance().changeProjectName("sdk测试模块", RobotConstant.mRobotStatusBean.getProjectId());
            }
        });

        dataBinding.uploadProjectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProjectHelper.getInstance().uploadProject(otherProjectId);
            }
        });
        dataBinding.deleteProjectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProjectHelper.getInstance().deleteProject(otherProjectId);
            }
        });
    }
}

package com.bozhon.sdk.next.one;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bozhon.sdk.next.one.databinding.ActivityTaskUiBinding;
import com.lib.sdk.next.NextException;
import com.lib.sdk.next.NextResultInfo;
import com.lib.sdk.next.NextSDKHelper;
import com.lib.sdk.next.base.NxMap;
import com.lib.sdk.next.o.map.manager.ProjectCacheManager;
import com.lib.sdk.next.o.map.util.PositionUtil;
import com.lib.sdk.next.operate.NextOperateHelper;
import com.lib.sdk.next.robot.constant.RobotConstant;

/**
 * FileName: MapTaskActivity
 * Author: zhikai.jin
 * Date: 2021/7/8 16:11
 * Description:
 */
public class MapTaskActivity extends AppCompatActivity {

    private ActivityTaskUiBinding dataBinding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBinding = ActivityTaskUiBinding.inflate(getLayoutInflater());
        View view = dataBinding.getRoot();
        setContentView(view);
        NxMap nxMap =  dataBinding.nextMapView.getNxMap();
        nxMap.onShowMapView(RobotConstant.mRobotStatusBean.getProjectId());

        NextOperateHelper.getInstance().setRobotOperateListener(new NextOperateHelper.IRobotOperateListener() {
            @Override
            public void onHttpError(String url, int code, String msg) {

            }

            @Override
            public void onSet2DNavResult(NextResultInfo resultInfo) {

            }

            @Override
            public void onCancel2DNavResult(NextResultInfo resultInfo) {

            }

            @Override
            public void onCommandResult(int type, NextResultInfo resultInfo) {
                if(type == NextOperateHelper.COMMAND_REST){
                    if (resultInfo.getResultCode() == NextException.CODE_NEXT_SUCCESS) {
                        Log.w("MapTaskActivity","复位成功");
                    }
                }

            }

        });

        dataBinding.taskNavBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nxMap.getPoint() != null) {
                    double originX = NextSDKHelper.getInstance().getMapOriginX(MapTaskActivity.this, nxMap.getProjectId());
                    double originY = NextSDKHelper.getInstance().getMapOriginY(MapTaskActivity.this, nxMap.getProjectId());
                    double resolution = NextSDKHelper.getInstance().getMapResolution(MapTaskActivity.this, nxMap.getProjectId());
                    double worldY = PositionUtil.localToServerY(nxMap.getPoint().getBitmapY(), resolution, originY);
                    double worldX = PositionUtil.localToServerX(nxMap.getPoint().getBitmapX(), resolution, originX);
                    NextOperateHelper.getInstance().on2DNavGoal(worldX, worldY, nxMap.getPoint().getTheta());
                }
            }
        });

        dataBinding.taskCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NextOperateHelper.getInstance().on2DNavCancel();
            }
        });

        dataBinding.taskRestStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NextOperateHelper.getInstance().onRestAndStopCommand();
            }
        });

        dataBinding.taskRestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //复位
                NextOperateHelper.getInstance().onRestCommand();
            }
        });
    }
}

package com.bozhon.sdk.next.one;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bozhon.sdk.next.one.databinding.ActivityVirtualWallBinding;
import com.lib.sdk.next.NextResultInfo;
import com.lib.sdk.next.base.NxMap;
import com.lib.sdk.next.o.map.bean.VirtualWallBean;
import com.lib.sdk.next.robot.constant.RobotConstant;
import com.lib.sdk.next.wall.VirtualWallHelper;

/**
 * FileName: VirtualWallActivity
 * Author: zhikai.jin
 * Date: 2021/6/25 15:41
 * Description: 虚拟墙
 */
public class VirtualWallActivity extends AppCompatActivity {

    private ActivityVirtualWallBinding dataBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBinding = ActivityVirtualWallBinding.inflate(getLayoutInflater());
        View view = dataBinding.getRoot();
        setContentView(view);
        NxMap nxMap =  dataBinding.nextMapView.getNxMap();
        nxMap.onShowMapView(RobotConstant.mRobotStatusBean.getProjectId());
        VirtualWallHelper virtualWallHelper = new VirtualWallHelper(nxMap);

        virtualWallHelper.setVirtualListener(new VirtualWallHelper.IOnVirtualListener() {
            @Override
            public void onSaveSuccess(VirtualWallBean virtualWallBean) {
                VirtualWallBean currentVirtual = virtualWallBean;
                currentVirtual.getWallPointList();
            }

            @Override
            public void onSaveFailed(NextResultInfo resultInfo) {

            }

            @Override
            public void onDeletedSuccess(NextResultInfo resultInfo) {

            }

            @Override
            public void onDeletedFail(NextResultInfo resultInfo) {

            }
        });

        dataBinding.createWallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                virtualWallHelper.addVirtualWall();
            }
        });
        dataBinding.deleteWallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nxMap.getVirtualWall() == null){
                    Toast.makeText(VirtualWallActivity.this,"请选择虚拟墙后进行删除",Toast.LENGTH_SHORT);
                    return;
                }

                virtualWallHelper.deleteVirtualWall(nxMap.getVirtualWall());
            }
        });

        dataBinding.openWallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                virtualWallHelper.openVirtualWallOperate();
            }
        });

        dataBinding.closeWallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                virtualWallHelper.closeVirtualWallOperate();
            }
        });
    }
}

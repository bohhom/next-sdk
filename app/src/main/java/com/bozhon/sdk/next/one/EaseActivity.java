package com.bozhon.sdk.next.one;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bozhon.sdk.next.one.databinding.ActivityEaseUiBinding;
import com.lib.sdk.next.NextResultInfo;
import com.lib.sdk.next.ease.EaseHelper;
import com.lib.sdk.next.base.NxMap;
import com.lib.sdk.next.robot.constant.RobotConstant;

/**
 * FileName: EaseActivity
 * Author: zhikai.jin
 * Date: 2021/6/25 17:31
 * Description: 橡皮擦
 */
public class EaseActivity extends AppCompatActivity {
    private ActivityEaseUiBinding dataBinding;
    @Override
    protected void onCreate(@Nullable  Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBinding = ActivityEaseUiBinding.inflate(getLayoutInflater());
        View view = dataBinding.getRoot();
        setContentView(view);
        NxMap nxMap =  dataBinding.nextMapView.getNxMap();
        nxMap.onShowMapView(RobotConstant.mRobotStatusBean.getProjectId());

        EaseHelper easeHelper = new EaseHelper(nxMap);
        easeHelper.setEaseListener(new EaseHelper.IEaseListener() {
            @Override
            public void onHttpError(String url, int code, String msg) {
                Toast.makeText(EaseActivity.this, "报错动作 = +" + url + "|code = " + code + "|msg =" + msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResult(NextResultInfo resultInfo) {

            }
        });
        dataBinding.easeInitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                easeHelper.initEase(nxMap.getProjectId());

            }
        });

        dataBinding.easeValueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String num = String.valueOf(dataBinding.easeValueEd.getText());
                int easeValue = num.equals("") ? 10 : Integer.valueOf(num);
                easeHelper.setEasePaintWidth(easeValue);
            }
        });

        dataBinding.editPointButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                easeHelper.updateMapPixmap();
            }
        });

    }
}

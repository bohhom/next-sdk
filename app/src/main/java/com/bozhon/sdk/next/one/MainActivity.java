package com.bozhon.sdk.next.one;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bozhon.sdk.next.one.databinding.ActivityMainBinding;
import com.lib.sdk.next.NextResultInfo;
import com.lib.sdk.next.callback.IPullProjectCallBack;
import com.lib.sdk.next.project.ProjectInfoHelper;
import com.lib.sdk.next.robot.IRobotStatusCallBack;
import com.lib.sdk.next.robot.RobotErrorStatusInfo;
import com.lib.sdk.next.robot.RobotHelper;
import com.lib.sdk.next.robot.RobotNavigationStatusInfo;
import com.lib.sdk.next.robot.RobotStatusInfo;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Button downloadBtn;

    private Button robotBtn;

    private ActivityMainBinding dataBinding;

    private MainAdapter mMainAdapter;

    private List<String> mDatas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBinding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = dataBinding.getRoot();
        setContentView(view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        dataBinding.processRc.setLayoutManager(linearLayoutManager);
        mMainAdapter = new MainAdapter() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent();
                switch (position) {
                    case 0:
                        ProjectInfoHelper.getInstance().pullNeedUpdateProject(new IPullProjectCallBack() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(MainActivity.this,"拉取数据成功，请进行下一步操作" ,Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFail(NextResultInfo info) {
                                Toast.makeText(MainActivity.this,"错误信息" + info.getResultMsg(),Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;

                    case 1:
                        intent.setClass(MainActivity.this,RobotStatusActivity.class);
                        startActivity(intent);
                        break;

                    case 2:
                        intent.setClass(MainActivity.this,CreateMapActivity.class);
                        startActivity(intent);
                        break;

                    case 3:
                        intent.setClass(MainActivity.this,GpsActivity.class);
                        startActivity(intent);
                        break;
                    case 4:
                        intent.setClass(MainActivity.this,PointInitActivity.class);
                        startActivity(intent);
                        break;
                    case 5:
                        intent.setClass(MainActivity.this,PointNavActivity.class);
                        startActivity(intent);
                        break;

                    case 6:
                        intent.setClass(MainActivity.this,VirtualWallActivity.class);
                        startActivity(intent);
                        break;

                    case 7:
                        intent.setClass(MainActivity.this,EaseActivity.class);
                        startActivity(intent);
                        break;
                    case 8:
                        intent.setClass(MainActivity.this,RobotSpeedActivity.class);
                        startActivity(intent);
                        break;
                    case 9:
                        intent.setClass(MainActivity.this,MapOperateActivity.class);
                        startActivity(intent);
                        break;
                    case 10:
                        intent.setClass(MainActivity.this,CreateMapExpandActivity.class);
                        startActivity(intent);
                        break;
                    case 11:
                        intent.setClass(MainActivity.this,MapTaskActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
            }
        };

        mDatas.add("拉取地图");
        mDatas.add("获取机器人状态");
        mDatas.add("创建地图");
        mDatas.add("定位初始点");
        mDatas.add("初始点");
        mDatas.add("导航点");
        mDatas.add("虚拟墙");
        mDatas.add("去除噪点");
        mDatas.add("设置机器人速度");
        mDatas.add("地图操作");
        mDatas.add("拓展地图");
        mDatas.add("执行任务");

        dataBinding.processRc.setAdapter(mMainAdapter);
        mMainAdapter.setmData(mDatas);


    }
}
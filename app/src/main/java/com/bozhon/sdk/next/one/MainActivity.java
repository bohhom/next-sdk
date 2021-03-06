package com.bozhon.sdk.next.one;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bozhon.sdk.next.one.databinding.ActivityMainBinding;
import com.lib.sdk.next.NextException;
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
                            public void onHttpError(String url, int code, String msg) {
                                Toast.makeText(MainActivity.this,"code ==" + code + "|msg = " +msg,Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onPullProjectResult(NextResultInfo resultInfo) {
                                if(resultInfo.getResultCode() == NextException.CODE_NEXT_SUCCESS){
                                    Toast.makeText(MainActivity.this,"?????????????????????????????????????????????" ,Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(MainActivity.this,"????????????" + resultInfo.getResultMsg(),Toast.LENGTH_SHORT).show();
                                }

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

                    case 12:
                        intent.setClass(MainActivity.this,LocationNoViewActivity.class);
                        startActivity(intent);
                        break;

                    case 13:
                        intent.setClass(MainActivity.this,MapRectActivity.class);
                        startActivity(intent);
                        break;
                    case 14:
                        intent.setClass(MainActivity.this,PointNoViewActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
            }
        };

        mDatas.add("????????????");
        mDatas.add("?????????????????????");
        mDatas.add("????????????");
        mDatas.add("???????????????");
        mDatas.add("?????????");
        mDatas.add("?????????");
        mDatas.add("?????????");
        mDatas.add("????????????");
        mDatas.add("?????????????????????");
        mDatas.add("????????????");
        mDatas.add("????????????");
        mDatas.add("????????????");
        mDatas.add("????????????????????????");
        mDatas.add("????????????");
        mDatas.add("??????????????????");

        dataBinding.processRc.setAdapter(mMainAdapter);
        mMainAdapter.setmData(mDatas);


    }
}
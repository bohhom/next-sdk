package com.lib.sdk.next;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bozh.logger.AndroidLogAdapter;
import com.bozh.logger.CsvFormatStrategy;
import com.bozh.logger.DiskLogAdapter;
import com.bozh.logger.DiskLogStrategy;
import com.bozh.logger.FormatStrategy;
import com.bozh.logger.LogStrategy;
import com.bozh.logger.Logger;
import com.bozh.logger.PrettyFormatStrategy;
import com.lib.sdk.next.global.GlobalOperate;
import com.lib.sdk.next.o.map.bean.PositionPointBean;
import com.lib.sdk.next.o.map.bean.ProjectInfoBean;
import com.lib.sdk.next.o.map.manager.ProjectCacheManager;
import com.lib.sdk.next.o.map.manager.RequestManager;
import com.lib.sdk.next.o.map.net.SocketRequestInterface;
import com.lib.sdk.next.robot.service.RobotNavigationStatusService;
import com.lib.sdk.next.tag.NextTag;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

/**
 * FileName: NextSDKHelper
 * Author: zhikai.jin
 * Date: 2021/5/25 16:40
 * Description: 接口入口
 */
public class NextSDKHelper {

    private static volatile NextSDKHelper mInstance;


    private NextSDKHelper() {

    }

    public static NextSDKHelper getInstance() {
        if (mInstance == null) {
            synchronized (NextSDKHelper.class) {
                if (mInstance == null) {
                    mInstance = new NextSDKHelper();
                }
            }
        }
        return mInstance;
    }

    public void init(Application application){
        //初始化 http请求
        GlobalOperate.setApp(application);

        initHttp(application);
        //日志输出
        initLog(application);

        initProjectCacheFile(application);


        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {

            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {

            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {

            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {

            }
        });

    }

    private void initLog(Application application) {
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(true)
                .methodCount(0)
                .tag(NextTag.TAG)
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));
        // 保存在包名路径下的（Android/data/包名/cache/log）
        String folder  = application.getExternalCacheDir().getAbsolutePath() +  File.separator + "log";
        HandlerThread ht = new HandlerThread("AndroidFileLogger." + folder);
        ht.start();
        Handler handler = new DiskLogStrategy.WriteHandler(ht.getLooper(), folder, 500 * 1024);
        LogStrategy logStrategy = new DiskLogStrategy(handler);

        FormatStrategy diskStrategy = CsvFormatStrategy.newBuilder()
                .logStrategy(logStrategy)
                .tag(NextTag.TAG)
                .build();
        Logger.addLogAdapter(new DiskLogAdapter(diskStrategy));
    }

    private void initHttp(Application application) {
        GlobalOperate.initRetrofit(application);
    }


    /**
     * 初始化工程缓存文件
     */
    private void initProjectCacheFile(Application application) {
        ProjectCacheManager.createProjectCacheDir(application);
    }

    /**
     * 获取某个工程
     * @return
     */
    public ProjectInfoBean getProjectInfo(String projectId){
        List<ProjectInfoBean> projectInfoBeans = ProjectCacheManager.getAllCacheProjectInfo(GlobalOperate.getApp());
        for (ProjectInfoBean projectInfoBean :projectInfoBeans){
            if(projectId.equals(projectInfoBean.getProjectId())){
                return projectInfoBean;
            }
        }
        return new ProjectInfoBean();
    }


    /**
     * 获取所有的工程
     * @return
     */
    public List<ProjectInfoBean> getAllProject(){
        List<ProjectInfoBean> projectInfoBeans = ProjectCacheManager.getAllCacheProjectInfo(GlobalOperate.getApp());
        return projectInfoBeans;
    }

    /**
     * 获取地图分辨率
     * @param context
     * @param projectId
     * @return
     */
    public double  getMapResolution(Context context, String projectId) {
       return ProjectCacheManager.getMapResolution(context, projectId);
    }

    /**
     * 获取地图的origin_x
     *
     * @return
     */
    public double  getMapOriginX(Context context, String projectId) {
        return ProjectCacheManager.getMapOriginX(context, projectId);
    }

    /**
     * 获取地图的origin_y
     * @param context
     * @param projectId
     * @return
     */
    public double  getMapOriginY(Context context, String projectId) {
        return ProjectCacheManager.getMapOriginY(context, projectId);
    }


    /**
     * 获取当前工程所有点
     * @return
     */
    public List<PositionPointBean> getCurrentAllPositions(String projectId) {
        return ProjectCacheManager.getAllPositionInfo(GlobalOperate.getApp(), projectId);
    }

}

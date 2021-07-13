/*
Copyright 2021 kino jin
zhikai.jin@bozhon.com
This file is part of next-sdk.
next-sdk is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.
next-sdk is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.
You should have received a copy of the GNU Lesser General Public License
along with next-sdk.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.lib.sdk.next.o.map.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.bozh.logger.Logger;
import com.lib.sdk.next.o.R;
import com.lib.sdk.next.o.map.bean.PositionPointBean;
import com.lib.sdk.next.o.map.bean.ProjectInfoBean;
import com.lib.sdk.next.o.map.bean.RouteBean;
import com.lib.sdk.next.o.map.bean.VirtualWallBean;
import com.lib.sdk.next.o.map.util.DisplayUtil;
import com.lib.sdk.next.o.map.util.FileIOUtil;
import com.lib.sdk.next.o.map.util.FileUtil;
import com.lib.sdk.next.o.map.util.ImageCache;
import com.lib.sdk.next.o.map.util.PositionUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by maqing 2018/11/19 14:23
 * Email：2856992713@qq.com
 * 机器人工程缓存管理类
 */
public class ProjectCacheManager {
    /**
     * 工程文件缓存目录名
     */
    public static final String PROJECT_CACHE_DIR_NAME = "project_cache";

    /**
     * 地图基本信息缓存目录
     */
    public static final String MAP_INFO_CACHE_DIR_NAME = "map_info";

    /**
     * 地图图片数据缓存目录
     */
    public static final String MAP_PICTURE_CACHE_DIR_NAME = "map_picture";
    /**
     * 地图虚拟墙数据缓存目录
     */
    public static final String OBSTACLE_CACHE_DIR_NAME = "obstacle_info";
    /**
     * 地图标记点坐标数据缓存目录
     */
    public static final String POSITION_CACHE_DIR_NAME = "position_info";
    /**
     * 地图标记点坐标数据缓存目录
     */
    public static final String NAVIGATION_ROUTE_CACHE_DIR_NAME = "navigation_route_info";

    /**
     * 路径规划点数据缓存目录
     */
    public static final String TASK_CACHE_DIR_NAME = "task_info";
    /**
     * 地图脚本缓存目录
     */
    public static final String SRCRIPT_CACHE_DIR_NAME = "script_info";

    /**
     * 缓存的工程文件基本名
     */
    public static final String PROJECT_FILE_NAME = "project_info.json";
    /**
     * 缓存的地图信息文件基本名
     */
    public static final String MAP_INFO_FILE_NAME = "map.json";
    /**
     * 缓存的地图图片文件基本名
     */
    public static final String MAP_PICTURE_FILE_NAME = "map_picture.png";

    /**
     * 缓存的虚拟墙文件基本名
     */
    public static final String OBSTACLE_FILE_NAME = "obstacles.json";

    /**
     * 缓存的标记坐标点文件基本名
     */
    public static final String POSITION_FILE_NAME = "positions.json";

    /**
     * 缓存的航路文件基本名
     */
    public static final String ROUTE_FILE_NAME = "path.json";

    /**
     * 路径规划点数据缓存目录
     */
    public static final String TASK_FILE_NAME = "task.json";

    /**
     * 缓存的脚本文件基本名
     */
    public static final String SCRIPT_FILE_NAME = "script.json";
    


    /**
     * 创建工程缓存文件目录
     * <p>
     * Android/data/应用包名/files/project_cache
     * <p>
     * Android/data/应用包名/files/project_cache/map_picture_data
     */
    public static void createProjectCacheDir(Context context) {

        String projectCacheDir = getProjectCacheDirPath(context);
        String mapPictureCacheDir = getMapPictureCacheDirPath(context);
        String mapInfoCacheDir = getMapInfoCacheDirPath(context);
        String obstacleInfoCacheDir = getObstacleInfoCacheDirPath(context);
        String positionInfoCacheDir = getPositionInfoCacheDirPath(context);
        String taskInfoCacheDir = getTaskInfoCacheDirPath(context);

        boolean createProjectDirSuccess = false;
        boolean createMapPictureDirSuccess = false;
        boolean createMapInfoDirSuccess = false;
        boolean createObstacleInfoDirSuccess = false;
        boolean createPositionInfoDirSuccess = false;
        boolean createTaskInfoDirSuccess = false;

        if (!FileUtil.isFileExists(projectCacheDir)) {
            createProjectDirSuccess = FileUtil.createOrExistsDir(projectCacheDir);
            if (createProjectDirSuccess) {
                Logger.i ( "create project cache file dir success");
            } else {
                Logger.i ( "create project cache file dir fail");
            }
        }

        if (!FileUtil.isFileExists(mapPictureCacheDir)) {
            createMapPictureDirSuccess = FileUtil.createOrExistsDir(mapPictureCacheDir);
            if (createMapPictureDirSuccess) {
                Logger.i ( "create map picture cache  dir success");
            } else {
                Logger.i ( "create map picture cache  dir fail");
            }
        }

        if (!FileUtil.isFileExists(mapInfoCacheDir)) {
            createMapInfoDirSuccess = FileUtil.createOrExistsDir(mapInfoCacheDir);
            if (createMapInfoDirSuccess) {
                Logger.i ( "create map info cache  dir success");
            } else {
                Logger.i ( "create map info cache  dir fail");
            }
        }

        if (!FileUtil.isFileExists(obstacleInfoCacheDir)) {
            createObstacleInfoDirSuccess = FileUtil.createOrExistsDir(obstacleInfoCacheDir);
            if (createObstacleInfoDirSuccess) {
                Logger.i ( "create obstacle info cache  dir success");
            } else {
                Logger.i ( "create obstacle info cache  dir fail");
            }
        }

        if (!FileUtil.isFileExists(positionInfoCacheDir)) {
            createPositionInfoDirSuccess = FileUtil.createOrExistsDir(positionInfoCacheDir);
            if (createPositionInfoDirSuccess) {
                Logger.i ( "create position info cache  dir success");
            } else {
                Logger.i ( "create position info cache  dir fail");
            }
        }

        if (!FileUtil.isFileExists(taskInfoCacheDir)) {
            createTaskInfoDirSuccess = FileUtil.createOrExistsDir(taskInfoCacheDir);
            if (createTaskInfoDirSuccess) {
                Logger.i ( "create task info cache  dir success");
            } else {
                Logger.i ( "create task info cache  dir fail");
            }
        }


    }

    /**
     * 获取工程缓存目录路径
     *
     * @param context
     * @return
     */
    public static String getProjectCacheDirPath(Context context) {
        String projectCacheDirPath = context.getExternalFilesDir("").getAbsolutePath() + File.separator + PROJECT_CACHE_DIR_NAME;
        Logger.i ( "getProjectCacheDirPath：" + projectCacheDirPath);
        return projectCacheDirPath;
    }

    /**
     * 获取工程地图图片缓存目录路径
     *
     * @param context
     * @return
     */
    public static String getMapPictureCacheDirPath(Context context) {
        String mapPictureCacheDirPath = getProjectCacheDirPath(context) + File.separator + MAP_PICTURE_CACHE_DIR_NAME;
        Logger.i ( "getMapPictureCacheDirPath：" + mapPictureCacheDirPath);
        return mapPictureCacheDirPath;
    }


    /**
     * 根据工程Id，获取该工程信息缓存文件路径
     *
     * @param context
     * @param projectId
     * @return
     */
    public static String getProjectInfoFilePath(Context context, String projectId) {
        String projectFilePath = getProjectCacheDirPath(context) + File.separator + projectId + "_" + PROJECT_FILE_NAME;
        Logger.i ( "getProjectFilePath：" + projectFilePath);
  
        return projectFilePath;
    }

    /**
     * 判断某个工程的工程文件是否有缓存
     *
     * @param context
     * @param projectId
     * @return
     */
    public static boolean projectInfoFileExits(Context context, String projectId) {
        String projectFilePath = getProjectInfoFilePath(context, projectId);
        return FileUtil.isFileExists(projectFilePath);
    }

    /**
     * 创建工程信息缓存文件
     *
     * @param context
     * @param projectId
     * @return
     */
    public static boolean createProjectInfoFile(Context context, String projectId) {
        String projectFilePath = getProjectInfoFilePath(context, projectId);
        return FileUtil.createOrExistsFile(projectFilePath);
    }

    /**
     * 更新工程信息文件
     *
     * @param context
     * @param projectId
     * @param projectInfoJson
     */
    public static void updateProjectInfoFile(Context context, String projectId, String projectInfoJson) {
        String projectInfoFilePath = getProjectInfoFilePath(context, projectId);
        boolean updateSuccess = FileIOUtil.writeFileFromString(projectInfoFilePath, projectInfoJson);
        if (updateSuccess) {
            Logger.i ( "updateProjectInfoFile success");
        } else {
            Logger.i ( "updateProjectInfoFile fail");
        }
    }

    /**
     * 获取工程地图基本信息缓存目录路径
     *
     * @param context
     * @return
     */
    public static String getMapInfoCacheDirPath(Context context) {
        String mapInfoCacheDirPath = getProjectCacheDirPath(context) + File.separator + MAP_INFO_CACHE_DIR_NAME;
        Logger.i ( "getMapInfoCacheDirPath：" + mapInfoCacheDirPath);
        return mapInfoCacheDirPath;
    }

    /**
     * 根据工程Id，获取该工程地图信息文件是否有缓存
     *
     * @param context
     * @param projectId
     * @return
     */
    public static String getMapInfoFilePath(Context context, String projectId) {
        String mapInfoFilePath = getMapInfoCacheDirPath(context) + File.separator + projectId + "_" + MAP_INFO_FILE_NAME;
        Logger.i ( "getMapInfoFilePath：" + mapInfoFilePath);
        return mapInfoFilePath;
    }


    /**
     * 根据工程Id，获取该工程地图信息文件名字
     *
     * @param context
     * @param projectId
     * @return
     */
    public static String getMapInfoFileName(Context context, String projectId) {
        String fileName = projectId + "_" + MAP_INFO_FILE_NAME;
        Logger.i ( "getMapInfoFileName：" + fileName);
        return fileName;
    }

    /**
     * 根据工程Id，判断该工程地图信息缓存文件
     *
     * @param context
     * @param projectId
     * @return
     */
    public static boolean mapInfoFileExits(Context context, String projectId) {
        String mapInfoFilePath = getMapInfoFilePath(context, projectId);
        return FileUtil.isFileExists(mapInfoFilePath);
    }

    /**
     * 创建工程地图信息缓存文件
     *
     * @param context
     * @param projectId
     * @return
     */
    public static boolean createMapInfoFile(Context context, String projectId) {
        String mapInfoFilePath = getMapInfoFilePath(context, projectId);
        return FileUtil.createOrExistsFile(mapInfoFilePath);
    }

    /**
     * 更新工程地图信息文件
     *
     * @param context
     * @param projectId
     * @param mapInfoJson
     */
    public static void updateMapInfoFile(Context context, String projectId, String mapInfoJson) {
        String mapInfoFilePath = getMapInfoFilePath(context, projectId);
        boolean updateSuccess = FileIOUtil.writeFileFromString(mapInfoFilePath, mapInfoJson);
        if (updateSuccess) {
            Logger.i ( "updateMapInfoFile success");
        } else {
            Logger.i ( "updateMapInfoFile fail");
        }
    }

    /**
     * 根据工程Id，获取该工程地图图片文件路径
     *
     * @param context
     * @param projectId
     * @return
     */
    public static String getMapPictureFilePath(Context context, String projectId) {
        String mapPictureFilePath = getMapPictureCacheDirPath(context) + File.separator + projectId + "_" + MAP_PICTURE_FILE_NAME;
        Logger.i ( "getMapPictureFilePath：" + mapPictureFilePath);
        return mapPictureFilePath;
    }

    /**
     * 创建工程地图图片缓存文件
     *
     * @param context
     * @param projectId
     * @return
     */
    public static boolean createMapPictureFile(Context context, String projectId) {
        String mapPictureFilePath = getMapPictureFilePath(context, projectId);
        return FileUtil.createOrExistsFile(mapPictureFilePath);
    }

    /**
     * 更新工程地图图片数据文件
     *
     * @param context
     * @param projectId
     * @param mapPictureData
     */
    public static void updateMapPictureFile(Context context, String projectId, String mapPictureData) {
        String mapPictureFilePath = getMapPictureFilePath(context, projectId);
        if (FileUtil.isFileExists(mapPictureFilePath)) {
            FileUtil.delete(mapPictureFilePath);
        }
        boolean createSuccess = createMapPictureFile(context, mapPictureFilePath);
        if (createSuccess) {
            byte[] bytes = Base64.decode(mapPictureData, Base64.DEFAULT);
            boolean updateSuccess = FileIOUtil.writeFileFromBytesByStream(mapPictureFilePath, bytes);
            if (updateSuccess) {
                Logger.i ( "updateMapPictureFile success");
            } else {
                Logger.i ( "updateMapPictureFile success");
            }
        }
//        ProjectCacheManager.updateProjectStamp(context, projectId);
    }

    /**
     * 更新工程地图图片数据文件
     *
     * @param context
     * @param projectId
     * @param mapPictureData
     */
    public static void getUpdateMapPictureFile(Context context, String projectId, String mapPictureData) {
        String mapPictureFilePath = getMapPictureFilePath(context, projectId);
        if (FileUtil.isFileExists(mapPictureFilePath)) {
            FileUtil.delete(mapPictureFilePath);
        }
        boolean createSuccess = createMapPictureFile(context, mapPictureFilePath);
        if (createSuccess) {
            byte[] bytes = Base64.decode(mapPictureData, Base64.DEFAULT);
            boolean updateSuccess = FileIOUtil.writeFileFromBytesByStream(mapPictureFilePath, bytes);
            if (updateSuccess) {
                Logger.i ( "updateMapPictureFile success");
            } else {
                Logger.i ( "updateMapPictureFile success");
            }
        }
        ProjectCacheManager.updateProjectStamp(context, projectId);
    }


    /**
     * 根据工程Id，判断该工程虚拟墙信息文件是否缓存
     *
     * @param context
     * @param projectId
     * @return
     */
    public static boolean obstaclesInfoFileExits(Context context, String projectId) {
        String obstaclesFilePath = getObstaclesFilePath(context, projectId);
        return FileUtil.isFileExists(obstaclesFilePath);
    }

    /**
     * 获取工程虚拟墙基本信息缓存目录路径
     *
     * @param context
     * @return
     */
    public static String getObstacleInfoCacheDirPath(Context context) {
        String obstacleInfoCacheDirPath = getProjectCacheDirPath(context) + File.separator + OBSTACLE_CACHE_DIR_NAME;
        Logger.i ( "getObstacleInfoCacheDirPath：" + obstacleInfoCacheDirPath);
        return obstacleInfoCacheDirPath;
    }

    /**
     * 根据工程Id，获取该工程虚拟墙文件路径
     *
     * @param context
     * @param projectId
     * @return
     */
    public static String getObstaclesFilePath(Context context, String projectId) {
        String obstaclesFilePath = getObstacleInfoCacheDirPath(context) + File.separator + projectId + "_" + OBSTACLE_FILE_NAME;
        Logger.i ( "getObstaclesFilePath：" + obstaclesFilePath);
        return obstaclesFilePath;
    }

    /**
     * 根据工程Id，获取该工程虚拟墙文件名字
     *
     * @param context
     * @param projectId
     * @return
     */
    public static String getObstaclesFileName(Context context, String projectId) {
        String obstaclesFileName = projectId + "_" + OBSTACLE_FILE_NAME;
        Logger.i ( "getObstaclesFileName：" + obstaclesFileName);
        return obstaclesFileName;
    }

    /**
     * 创建工程虚拟墙文件缓存文件
     *
     * @param context
     * @param projectId
     * @return
     */
    public static boolean createObstaclesFile(Context context, String projectId) {
        String mapPictureFilePath = getObstaclesFilePath(context, projectId);
        return FileUtil.createOrExistsFile(mapPictureFilePath);
    }

    /**
     * 更新工程航路信息文件
     *
     * @param context
     * @param projectId
     * @param routesInfoJson
     */
    public static void updateRouteInfoFile(Context context, String projectId, String routesInfoJson) {
        String routesFilePath = getRoutesFilePath(context, projectId);
        boolean updateSuccess = FileIOUtil.writeFileFromString(routesFilePath, routesInfoJson);
        if (updateSuccess) {
            Logger.i ( "updateMapInfoFile success");
        } else {
            Logger.i ( "updateMapInfoFile fail");
        }
//        ProjectCacheManager.updateProjectStamp(context, projectId);
    }




    /**
     * 更新工程虚拟墙信息文件
     *
     * @param context
     * @param projectId
     * @param obstaclesInfoJson
     */
    public static void updateObstaclesInfoFile(Context context, String projectId, String obstaclesInfoJson) {
        String obstaclesFilePath = getObstaclesFilePath(context, projectId);
        boolean updateSuccess = FileIOUtil.writeFileFromString(obstaclesFilePath, obstaclesInfoJson);
        if (updateSuccess) {
            Logger.i ( "updateMapInfoFile success");
        } else {
            Logger.i ( "updateMapInfoFile fail");
        }
//        ProjectCacheManager.updateProjectStamp(context, projectId);
    }

    /**
     * 根据工程Id，判断该工程标记坐标点文件是否缓存
     *
     * @param context
     * @param projectId
     * @return
     */
    public static boolean positionsInfoFileExits(Context context, String projectId) {
        String positionFilePath = getPositionsFilePath(context, projectId);
        return FileUtil.isFileExists(positionFilePath);
    }

    /**
     * 根据工程Id，判断该工程航路文件是否缓存
     *
     * @param context
     * @param projectId
     * @return
     */
    public static boolean routesInfoFileExits(Context context, String projectId) {
        String positionFilePath = getRoutesFilePath(context, projectId);
        return FileUtil.isFileExists(positionFilePath);
    }



    /**
     * 获取工程标记点坐标信息缓存目录路径
     *
     * @param context
     * @return
     */
    public static String getPositionInfoCacheDirPath(Context context) {
        String positionInfoCacheDirPath = getProjectCacheDirPath(context) + File.separator + POSITION_CACHE_DIR_NAME;
        Logger.i ( "getPositionInfoCacheDirPath：" + positionInfoCacheDirPath);
        return positionInfoCacheDirPath;
    }

    /**
     * 获取工程航路信息缓存目录路径
     *
     * @param context
     * @return
     */
    public static String getRoutesInfoCacheDirPath(Context context) {
        String routeInfoCacheDirPath = getProjectCacheDirPath(context) + File.separator + NAVIGATION_ROUTE_CACHE_DIR_NAME;
        Logger.i ( "getRouteInfoCacheDirPath：" + routeInfoCacheDirPath);
        return routeInfoCacheDirPath;
    }

    /**
     * 根据工程Id，获取该工程标记坐标点文件路径
     *
     * @param context
     * @param projectId
     * @return
     */
    public static String getPositionsFilePath(Context context, String projectId) {
        String positionFilePath = getPositionInfoCacheDirPath(context) + File.separator + projectId + "_" + POSITION_FILE_NAME;
        Logger.i ( "getPositionsFilePath：" + positionFilePath);
        return positionFilePath;
    }

    /**
     * 根据工程Id，获取该工程航路文件路径
     *
     * @param context
     * @param projectId
     * @return
     */
    public static String getRoutesFilePath(Context context, String projectId) {
        String routeFilePath = getRoutesInfoCacheDirPath(context) + File.separator + projectId + "_" + ROUTE_FILE_NAME;
        Logger.i ( "getRouteFilePath：" + routeFilePath);
        return routeFilePath;
    }



    /**
     * 根据工程Id，获取该工程标记坐标点文件名字
     *
     * @param context
     * @param projectId
     * @return
     */
    public static String getPositionsFileName(Context context, String projectId) {
        String positionFileName = projectId + "_" + POSITION_FILE_NAME;
        Logger.i ( "getPositionsFileName：" + positionFileName);
        Log.e("routeresult" ,"positionFileName()=="+positionFileName);
        return positionFileName;
    }

    /**
     * 根据工程Id，获取该工程航路文件名字
     *
     * @param context
     * @param projectId
     * @return
     */
    public static String getRoutesFileName(Context context, String projectId) {
        String positionFileName = projectId + "_" + ROUTE_FILE_NAME;
        Logger.i ( "getPositionsFileName：" + positionFileName);
        return positionFileName;
    }


    /**
     * 创建工程标记坐标点缓存文件
     *
     * @param context
     * @param projectId
     * @return
     */
    public static boolean createPositionsFile(Context context, String projectId) {
        String positionFilePath = getPositionsFilePath(context, projectId);
        return FileUtil.createOrExistsFile(positionFilePath);
    }

    /**
     * 创建工程航路缓存文件
     *
     * @param context
     * @param projectId
     * @return
     */
    public static boolean createRoutesFile(Context context, String projectId) {
        String routesFilePath = getRoutesFilePath(context, projectId);
        return FileUtil.createOrExistsFile(routesFilePath);
    }


    /**
     * 更新工程标记坐标点文件
     *
     * @param context
     * @param projectId
     * @param positionInfoJson
     */
    public static void updatePositionInfoFile(Context context, String projectId, String positionInfoJson) {
        String positionFilePath = getPositionsFilePath(context, projectId);
        boolean updateSuccess = FileIOUtil.writeFileFromString(positionFilePath, positionInfoJson);
        if (updateSuccess) {
            Logger.i ( "updatePositionInfoFile success");
        } else {
            Logger.i ( "updatePositionInfoFile fail");
        }
        //更新时间戳
//        updateProjectStamp(context, projectId);
    }

    /**
     * 更新航路文件
     *
     * @param context
     * @param projectId
     * @param positionInfoJson
     */
    public static void updateRoutesInfoFile(Context context, String projectId, String positionInfoJson) {
        String positionFilePath = getRoutesFilePath(context, projectId);
        boolean updateSuccess = FileIOUtil.writeFileFromString(positionFilePath, positionInfoJson);
        if (updateSuccess) {
            Logger.i ( "updatePositionInfoFile success");
        } else {
            Logger.i ( "updatePositionInfoFile fail");
        }
        //更新时间戳
//        updateProjectStamp(context, projectId);
    }





    /**
     * 获取路径规划点信息缓存目录路径
     *
     * @param context
     * @return
     */
    public static String getTaskInfoCacheDirPath(Context context) {
        String taskInfoCacheDirPath = getProjectCacheDirPath(context) + File.separator + TASK_CACHE_DIR_NAME;
        Logger.i ( "geTaskInfoCacheDirPath：" + taskInfoCacheDirPath);
        return taskInfoCacheDirPath;
    }

    /**
     * 根据工程Id，获取该工程路径规划点文件路径
     *
     * @param context
     * @param projectId
     * @return
     */
    public static String getTaskFilePath(Context context, String projectId) {
        String taskFilePath = getPositionInfoCacheDirPath(context) + File.separator + projectId + "_" + TASK_FILE_NAME;
        Logger.i ( "getTaskFilePath：" + taskFilePath);
        return taskFilePath;
    }

    /**
     * 创建工程路径规划点缓存文件
     *
     * @param context
     * @param projectId
     * @return
     */
    public static boolean createTaskFile(Context context, String projectId) {
        String taskFilePath = getTaskFilePath(context, projectId);
        return FileUtil.createOrExistsFile(taskFilePath);
    }


    /**
     * 获取缓存的所有工程的工程Id和时间戳
     *
     * @param context
     */
    public static List<ProjectInfoBean> getAllCacheProjectIdStamp(Context context) {
        String projectCacheDirPath = getProjectCacheDirPath(context);
        List<File> fileList = FileUtil.listFilesInDir(projectCacheDirPath, false);
        List<ProjectInfoBean> projectList = new ArrayList<>();
        Logger.i ( fileList.size() + "");
        for (int i = 0; i < fileList.size(); i++) {
            if (FileUtil.isDir(fileList.get(i))) {
                Logger.i ( "isDir" + i);
                continue;
            }

            String projectInfoJson = FileIOUtil.readFile2String(fileList.get(i));
            try {
                ProjectInfoBean projectBean = new ProjectInfoBean();
                JSONObject projectObject = new JSONObject(projectInfoJson);
                projectBean.setProjectId(projectObject.getString("project_id"));
                projectBean.setProjectStamp(projectObject.getString("project_stamp"));
                projectList.add(projectBean);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return projectList;
    }


    /**
     * 获取缓存的所有工程的工程信息
     *
     * @param context
     */
    public static List<ProjectInfoBean> getAllCacheProjectInfo(Context context) {
        String projectCacheDirPath = getProjectCacheDirPath(context);
        List<File> fileList = FileUtil.listFilesInDir(projectCacheDirPath, false);
        List<ProjectInfoBean> projectList = new ArrayList<>();
        Logger.i ( fileList.size() + "");
        for (int i = 0; i < fileList.size(); i++) {
            if (FileUtil.isDir(fileList.get(i))) {
                Logger.i ( "isDir" + i);
                continue;
            }

            String projectInfoJson = FileIOUtil.readFile2String(fileList.get(i));
            try {
                ProjectInfoBean projectBean = new ProjectInfoBean();
                JSONObject projectObject = new JSONObject(projectInfoJson);
                projectBean.setProjectId(projectObject.getString("project_id"));
                projectBean.setProjectStamp(projectObject.getString("project_stamp"));
                projectBean.setProjectName(projectObject.getString("project_name"));
                projectBean.setMapInfoCacheFilePath(projectObject.getString("map_name"));
                projectBean.setObstacleCacheInfoFilePath(projectObject.getString("obstacle_name"));
                projectBean.setPositionCacheInfoFilePath(projectObject.getString("positions_name"));
                projectList.add(projectBean);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return projectList;
    }

    /**
     * 获取缓存的所有工程的工程信息
     *
     * @param context
     */
    public static List<ProjectInfoBean> getAllCacheProjectInfo(Context context, String cond) {
        if (TextUtils.isEmpty(cond)) {
            return getAllCacheProjectInfo(context);
        }

        String projectCacheDirPath = getProjectCacheDirPath(context);
        List<File> fileList = FileUtil.listFilesInDir(projectCacheDirPath, false);
        List<ProjectInfoBean> projectList = new ArrayList<>();
        Logger.i ( fileList.size() + "");

        Pattern p = Pattern
                .compile(".*" + cond + ".*");
        for (int i = 0; i < fileList.size(); i++) {
            if (FileUtil.isDir(fileList.get(i))) {
                Logger.i ( "isDir" + i);
                continue;
            }

            String projectInfoJson = FileIOUtil.readFile2String(fileList.get(i));
            try {
                ProjectInfoBean projectBean = new ProjectInfoBean();
                JSONObject projectObject = new JSONObject(projectInfoJson);
                projectBean.setProjectId(projectObject.getString("project_id"));
                projectBean.setProjectStamp(projectObject.getString("project_stamp"));
                projectBean.setProjectName(projectObject.getString("project_name"));
                Matcher m = p.matcher(projectBean.getProjectName());
                if (!m.matches()) {
                    continue;
                }

                projectBean.setMapInfoCacheFilePath(projectObject.getString("map_name"));
                projectBean.setObstacleCacheInfoFilePath(projectObject.getString("obstacle_name"));
                projectBean.setPositionCacheInfoFilePath(projectObject.getString("positions_name"));
                projectList.add(projectBean);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Collections.sort(projectList, new Comparator<ProjectInfoBean>() {
            @Override
            public int compare(ProjectInfoBean o1, ProjectInfoBean o2) {
                return o1.getProjectName().compareTo(o2.getProjectName());
            }
        });
        return projectList;
    }

    /**
     * 修改工程名字
     *
     * @param context
     * @param projectId
     * @param projectName
     */
    public static void updateProjectName(Context context, String projectId, String projectName) {
        String projectInfoFilePath = getProjectInfoFilePath(context, projectId);
        String projectInfoJson = FileIOUtil.readFile2String(projectInfoFilePath);
        Logger.i ( projectInfoJson);
        try {
            JSONObject projectInfoObject = new JSONObject(projectInfoJson);
            projectInfoObject.put("project_name", projectName);
            //更新时间戳
            Date date = new Date();
            projectInfoObject.put("project_stamp", getISO8601Timestamp(date));
            boolean updateSuccess = FileIOUtil.writeFileFromString(projectInfoFilePath, projectInfoObject.toString());
            if (updateSuccess) {
                Logger.i ( "update project name success");
            } else {
                Logger.i ( "update project name fail");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * 修改工程名字
     *
     * @param context
     * @param projectId
     * @param projectName
     */
    public static String getUpdateProjectNameTempContent(Context context, String projectId, String projectName) {
        String projectInfoFilePath = getProjectInfoFilePath(context, projectId);
        String projectInfoJson = FileIOUtil.readFile2String(projectInfoFilePath);
        Logger.i ( projectInfoJson);
        try {
            JSONObject projectInfoObject = new JSONObject(projectInfoJson);
            projectInfoObject.put("project_name", projectName);
            //更新时间戳
            Date date = new Date();
            projectInfoObject.put("project_stamp", getISO8601Timestamp(date));
            return projectInfoObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 更新本地项目内容
     *
     * @param context
     * @param projectId
     * @param projectInfoContent
     */
    public static void updateProject(Context context, String projectId, String projectInfoContent) {
        String projectInfoFilePath = getProjectInfoFilePath(context, projectId);
        boolean updateSuccess = FileIOUtil.writeFileFromString(projectInfoFilePath, projectInfoContent);
        if (updateSuccess) {
            Logger.i ( "update project success");
        } else {
            Logger.i ( "update project fail");
        }
    }


    /**
     * 更新时间戳
     *
     * @param context
     * @param projectId
     */
    public static void updateProjectStamp(Context context, String projectId) {
        String projectInfoFilePath = getProjectInfoFilePath(context, projectId);
        String projectInfoJson = FileIOUtil.readFile2String(projectInfoFilePath);
        Logger.i ( projectInfoJson);
        try {
            JSONObject projectInfoObject = new JSONObject(projectInfoJson);
            //更新时间戳
            Date date = new Date();
            projectInfoObject.put("project_stamp", getISO8601Timestamp(date));
            boolean updateSuccess = FileIOUtil.writeFileFromString(projectInfoFilePath, projectInfoObject.toString());
            if (updateSuccess) {
                Logger.i ( "update project name success");
            } else {
                Logger.i ( "update project name fail");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * 更新时间戳
     *
     * @param context
     * @param projectId
     */
    public static String getUpdateProjectStampContent(Context context, String projectId) {
        return getUpdateProjectStampContent(context, projectId, new Date());
    }

    public static String getUpdateProjectStampContent(Context context, String projectId, Date date) {
        String projectInfoFilePath = getProjectInfoFilePath(context, projectId);
        String projectInfoJson = FileIOUtil.readFile2String(projectInfoFilePath);
        Logger.i ( projectInfoJson);
        try {
            JSONObject projectInfoObject = new JSONObject(projectInfoJson);
            //更新时间戳
            projectInfoObject.put("project_stamp", getISO8601Timestamp(date));
//            boolean updateSuccess = FileIOUtil.writeFileFromString(projectInfoFilePath, projectInfoObject.toString());
//            if (updateSuccess) {
//                Logger.i ( "update project name success");
//            } else {
//                Logger.i ( "update project name fail");
//            }
            return projectInfoObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getUpdateProjectStampContent(Context context, String projectId, String date) {
        String projectInfoFilePath = getProjectInfoFilePath(context, projectId);
        String projectInfoJson = FileIOUtil.readFile2String(projectInfoFilePath);
        Logger.i ( projectInfoJson);
        try {
            JSONObject projectInfoObject = new JSONObject(projectInfoJson);
            //更新时间戳
            projectInfoObject.put("project_stamp", date);
//            boolean updateSuccess = FileIOUtil.writeFileFromString(projectInfoFilePath, projectInfoObject.toString());
//            if (updateSuccess) {
//                Logger.i ( "update project name success");
//            } else {
//                Logger.i ( "update project name fail");
//            }
            return projectInfoObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 传入Data类型日期，返回字符串类型时间（ISO8601标准时间）
     *
     * @param date
     * @return
     */
    public static String getISO8601Timestamp(Date date) {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
        df.setTimeZone(tz);
        String nowAsISO = df.format(date);
        return nowAsISO;
    }

    /**
     * 获取工程信息缓存文件原内容
     *
     * @param context
     * @param projectId
     * @return
     */
    public static String getProjectCacheFileOriContent(Context context, String projectId) {
        String content = "";
        String projectInfoFilePath = getProjectInfoFilePath(context, projectId);
        content = FileIOUtil.readFile2String(projectInfoFilePath);
        return content;
    }

    /**
     * 获取所有工程信息缓存文件原内容
     *
     * @param context
     * @return
     */
    public static List<String> getAllProjectCacheFileOriContent(Context context) {
        List<String> contentList = new ArrayList<>();

        String projectCacheDirPath = getProjectCacheDirPath(context);
        List<File> fileList = FileUtil.listFilesInDir(projectCacheDirPath, false);
        for (int i = 0; i < fileList.size(); i++) {
            if (FileUtil.isDir(fileList.get(i))) {
                Logger.i ( "isDir" + i);
                continue;
            }
            String projectInfoJson = FileIOUtil.readFile2String(fileList.get(i));
            contentList.add(projectInfoJson);
        }
        return contentList;
    }


    /**
     * 删除指定工程
     *
     * @param context
     * @param projectId
     */
    public static void deleteProject(Context context, String projectId) {
        //工程基本信息文件
        String projectInfoFilePath = ProjectCacheManager.getProjectInfoFilePath(context, projectId);
        //地图基本信息文件
        String mapInfoFilePath = ProjectCacheManager.getMapInfoFilePath(context, projectId);
        //地图图片文件
        String mapPictureFilePath = ProjectCacheManager.getMapPictureFilePath(context, projectId);
        //虚拟墙基本信息文件
        String obstacleInfoFilePath = ProjectCacheManager.getObstaclesFilePath(context, projectId);
        //位置标记点信息文件
        String positionInfoFilePath = ProjectCacheManager.getPositionsFilePath(context, projectId);
        //脚本文件
        String scriptFilePath = ProjectCacheManager.getScriptFilePath(context, projectId);

        FileUtil.delete(projectInfoFilePath);
        FileUtil.delete(mapInfoFilePath);
        FileUtil.delete(mapPictureFilePath);
        FileUtil.delete(obstacleInfoFilePath);
        FileUtil.delete(positionInfoFilePath);
        FileUtil.delete(scriptFilePath);
    }

    /***
     * 删除选中的批量工程
     * @param context
     * @param projectIds
     */
    public static void deleteBatchProject(Context context, List<String> projectIds) {
        for(String projectId : projectIds){
            //工程基本信息文件
            String projectInfoFilePath = ProjectCacheManager.getProjectInfoFilePath(context, projectId);
            //地图基本信息文件
            String mapInfoFilePath = ProjectCacheManager.getMapInfoFilePath(context, projectId);
            //地图图片文件
            String mapPictureFilePath = ProjectCacheManager.getMapPictureFilePath(context, projectId);
            //虚拟墙基本信息文件
            String obstacleInfoFilePath = ProjectCacheManager.getObstaclesFilePath(context, projectId);
            //位置标记点信息文件
            String positionInfoFilePath = ProjectCacheManager.getPositionsFilePath(context, projectId);
            //脚本文件
            String scriptFilePath = ProjectCacheManager.getScriptFilePath(context, projectId);

            FileUtil.delete(projectInfoFilePath);
            FileUtil.delete(mapInfoFilePath);
            FileUtil.delete(mapPictureFilePath);
            FileUtil.delete(obstacleInfoFilePath);
            FileUtil.delete(positionInfoFilePath);
            FileUtil.delete(scriptFilePath);
        }
    }


    /**
     * 获取工程地图信息缓存文件原内容
     *
     * @param context
     * @param projectId
     * @return
     */
    public static String getMapInfoCacheFileOriContent(Context context, String projectId) {
        String content = "";
        String mapInfoFilePath = getMapInfoFilePath(context, projectId);
        content = FileIOUtil.readFile2String(mapInfoFilePath);
        Logger.i ( "getMapInfoCacheFileOriContent：" + content + "");
        return content;
    }

    /**
     * 获取工程虚拟墙信息缓存文件原内容
     *
     * @param context
     * @param projectId
     * @return
     */
    public static String getObstacleCacheFileOriContent(Context context, String projectId) {
        String content = "";
        String obstacleInfoFilePath = getObstaclesFilePath(context, projectId);
        content = FileIOUtil.readFile2String(obstacleInfoFilePath);
        return content;
    }

    /**
     * 获取工程航路信息缓存文件原内容
     *
     * @param context
     * @param projectId
     * @return
     */
    public static String getRoutesCacheFileOriContent(Context context, String projectId) {
        String content = "";
        String routesInfoFilePath = getRoutesFilePath(context, projectId);
        content = FileIOUtil.readFile2String(routesInfoFilePath);
        return content;
    }




    /**
     * 获取工程位置信息缓存文件原内容
     *
     * @param context
     * @param projectId
     * @return
     */
    public static String getPositionCacheFileOriContent(Context context, String projectId) {
        String content = "";
        String positionInfoFilePath = getPositionsFilePath(context, projectId);
        content = FileIOUtil.readFile2String(positionInfoFilePath);
        return content;
    }

    /**
     * 获取工程所有的位置点信息
     *
     * @param context
     * @param projectId
     * @return
     */
    public static List<PositionPointBean> getAllPositionInfo(Context context, String projectId) {
        List<PositionPointBean> positionPointList = new ArrayList<>();
        String positionContent = getPositionCacheFileOriContent(context, projectId);
        try {
            JSONObject positionInfoObject = new JSONObject(positionContent);
            if (!positionInfoObject.getString("positions").equals("null")) {
                JSONArray positionArray = positionInfoObject.getJSONArray("positions");
                double resolution = getMapResolution(context, projectId);
                double originX = getMapOriginX(context, projectId);
                double originY = getMapOriginY(context, projectId);
                for (int i = 0; i < positionArray.length(); i++) {
                    JSONObject positionItem = positionArray.getJSONObject(i);
                    JSONObject positionObject = positionItem.getJSONObject("position");
                    int type = positionItem.getInt("type");
                    Bitmap pointBitmap = null;
                    Bitmap pointBigBitmap = null;
                    Bitmap nameBitmap = null;
                    Bitmap nameCloseBitmap = null;
                    switch (type) {
                        case PositionPointBean.TYPE_INIT_POINT:
                            pointBitmap = ImageCache.getInstance().getIconBitmap(context.getResources(), R.mipmap.initial_point);
                            pointBigBitmap = ImageCache.getInstance().getIconBitmap(context.getResources(), R.mipmap.init_point_big);
                            nameBitmap = ImageCache.getInstance().getIconBitmap(context.getResources(), R.mipmap.init_point_bg);
                            nameCloseBitmap = ImageCache.getInstance().getIconBitmap(context.getResources(), R.mipmap.name_close);
                            break;
                        case PositionPointBean.TYPE_CHARGE_POINT:
                            pointBitmap = ImageCache.getInstance().getIconBitmap(context.getResources(), R.mipmap.charge_point);
                            pointBigBitmap = ImageCache.getInstance().getIconBitmap(context.getResources(), R.mipmap.charge_point_big);
                            nameBitmap = ImageCache.getInstance().getIconBitmap(context.getResources(), R.mipmap.charge_point_bg);
                            nameCloseBitmap = ImageCache.getInstance().getIconBitmap(context.getResources(), R.mipmap.name_close);
                            break;
                        case PositionPointBean.TYPE_NAVIGATION_POINT:
                            pointBitmap = ImageCache.getInstance().getIconBitmap(context.getResources(), R.mipmap.navigation_point);
                            pointBigBitmap = ImageCache.getInstance().getIconBitmap(context.getResources(), R.mipmap.navigation_point_big);
                            nameBitmap = ImageCache.getInstance().getIconBitmap(context.getResources(), R.mipmap.navigation_point_bg);
                            nameCloseBitmap = ImageCache.getInstance().getIconBitmap(context.getResources(), R.mipmap.name_close);
                            break;
                        case PositionPointBean.TYPE_STANDBY_POINT:
                            pointBitmap = ImageCache.getInstance().getIconBitmap(context.getResources(), R.mipmap.standby_point);
                            pointBigBitmap = ImageCache.getInstance().getIconBitmap(context.getResources(), R.mipmap.standby_point_big);
                            nameBitmap = ImageCache.getInstance().getIconBitmap(context.getResources(), R.mipmap.standby_point_bg);
                            nameCloseBitmap = ImageCache.getInstance().getIconBitmap(context.getResources(), R.mipmap.name_close);
                            break;
                    }
                    float x = (float) PositionUtil.serverToLocalX(
                            positionObject.getDouble("world_x"),
                            resolution,
                            originX
                    );
                    float y = (float) PositionUtil.serverToLocalY(positionObject.getDouble("world_y"),
                            resolution,
                            originY);
                    PositionPointBean positionPointBean = new PositionPointBean(pointBitmap, type);
                    positionPointBean.setBigBitmap(pointBigBitmap);
                    positionPointBean.setBitmapX(x);
                    positionPointBean.setBitmapY(y);

                    positionPointBean.setNameBitmap(nameBitmap);
                    positionPointBean.setNameCloseBitmap(nameCloseBitmap);

                    Paint namePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                    namePaint.setColor(Color.WHITE);
                    namePaint.setTextSize(20);
                    positionPointBean.setNamePaint(namePaint);

                    double serverTheta = positionObject.getDouble("theta");
                    double localTheta = 0;

                    if (positionPointBean.getTheta() >= -Math.PI / 2 && positionPointBean.getTheta() <= 0) {
                        localTheta = Math.PI / 2 - serverTheta;
                    }

                    if (positionPointBean.getTheta() > 0 && positionPointBean.getTheta() <= Math.PI) {
                        localTheta = Math.PI / 2 - serverTheta;
                    }

                    if (positionPointBean.getTheta() >= -Math.PI && positionPointBean.getTheta() <= -Math.PI / 2) {
                        localTheta = -Math.PI * 3f / 2 - serverTheta;
                    }

                    positionPointBean.setTheta(localTheta);
                    positionPointBean.setItemRotate((float) Math.toDegrees(localTheta));

                    positionPointBean.setPointName(positionItem.getString("name"));

                    Logger.i ( "getAllPositionInfo：" + serverTheta + "," + positionPointBean.getTheta() + "," + positionPointBean.getItemRotate());
                    positionPointList.add(positionPointBean);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Logger.i ( "getAllPositionInfo：" + e.getMessage());
        }
        catch(NullPointerException e){
            e.printStackTrace();
        }


        return positionPointList;
    }

    /**
     * 获取工程所有的导航点
     *
     * @param context
     * @param projectId
     * @return
     */
    public static List<PositionPointBean> getAllNavigationPosition(Context context, String projectId) {
        List<PositionPointBean> positionPointList = new ArrayList<>();
        String positionContent = getPositionCacheFileOriContent(context, projectId);
        try {
            JSONObject positionInfoObject = new JSONObject(positionContent);
            if (!positionInfoObject.getString("positions").equals("null")) {
                JSONArray positionArray = positionInfoObject.getJSONArray("positions");
                double resolution = getMapResolution(context, projectId);
                double originX = getMapOriginX(context, projectId);
                double originY = getMapOriginY(context, projectId);
                for (int i = 0; i < positionArray.length(); i++) {
                    JSONObject positionItem = positionArray.getJSONObject(i);
                    JSONObject positionObject = positionItem.getJSONObject("position");
                    int type = positionItem.getInt("type");
                    Bitmap pointBitmap = null;
                    Bitmap pointBigBitmap = null;
                    Bitmap nameCloseBitmap = null;
                    switch (type) {
                        case PositionPointBean.TYPE_NAVIGATION_POINT:
                            pointBitmap = ImageCache.getInstance().getIconBitmap(context.getResources(), R.mipmap.navigation_point);
                            pointBigBitmap = ImageCache.getInstance().getIconBitmap(context.getResources(), R.mipmap.navigation_point_big);
                            Bitmap nameBitmap = ImageCache.getInstance().getIconBitmap(context.getResources(), R.mipmap.navigation_point_bg);
                            nameCloseBitmap = ImageCache.getInstance().getIconBitmap(context.getResources(), R.mipmap.name_close);

                            float x = (float) PositionUtil.serverToLocalX(
                                    positionObject.getDouble("world_x"),
                                    resolution,
                                    originX
                            );
                            float y = (float) PositionUtil.serverToLocalY(positionObject.getDouble("world_y"),
                                    resolution,
                                    originY);
                            PositionPointBean positionPointBean = new PositionPointBean(pointBitmap, type);
                            positionPointBean.setBigBitmap(pointBigBitmap);
                            positionPointBean.setBitmapX(x);
                            positionPointBean.setBitmapY(y);

                            positionPointBean.setNameBitmap(nameBitmap);
                            positionPointBean.setNameCloseBitmap(nameCloseBitmap);

                            Paint namePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                            namePaint.setColor(Color.WHITE);
                            namePaint.setTextSize(20);
                            positionPointBean.setNamePaint(namePaint);

                            double serverTheta = positionObject.getDouble("theta");
                            double localTheta = 0;

                            if (serverTheta >= Math.PI / 2 && serverTheta <= Math.PI) {
                                localTheta = Math.PI / 2 - serverTheta;
                            }

                            if (serverTheta >= -Math.PI && serverTheta <= -Math.PI / 2) {
                                localTheta = -Math.PI * 3f / 2 - serverTheta;
                            }

                            positionPointBean.setTheta(localTheta);
                            positionPointBean.setItemRotate((float) Math.toDegrees(localTheta));
                            positionPointBean.setPointName(positionItem.getString("name"));


                            positionPointList.add(positionPointBean);
                            break;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return positionPointList;
    }

    /**
     * 获取工程所有的航路信息
     *
     * @param context
     * @param projectId
     * @return
     */
    public static List<RouteBean> getRoutesInfo(Context context, String projectId){
        List<RouteBean> routesList = new ArrayList<>();
        String RoutesInfoContent = getRoutesCacheFileOriContent(context, projectId);
        Log.e( "getRoutesInfo" ,"RoutesInfoContent = "+RoutesInfoContent);
        try {
            double resolution = getMapResolution(context, projectId);
            double originX = getMapOriginX(context, projectId);
            double originY = getMapOriginY(context, projectId);
            JSONObject routesInfoObject = new JSONObject(RoutesInfoContent);
            JSONArray pathsArry = routesInfoObject.getJSONArray("paths");
            if (!routesInfoObject.equals("null")) {
                for(int i = 0; i < pathsArry.length(); i++ ){
                    JSONObject pointItem = pathsArry.getJSONObject(i);
                    RouteBean routeBean = new RouteBean();
                    routeBean.setDirection(pointItem.getInt("direction"));
                    routeBean.setWidth(pointItem.getDouble("width"));
                    JSONArray positions = pointItem.getJSONArray("positions");
                    List<RouteBean.PositionsDTO>  TwoPositions = new ArrayList<>();
                    for(int j = 0; j < positions.length(); j++){
                        JSONObject positionsItem = positions.getJSONObject(j) ;
                        RouteBean.PositionsDTO navPoint = new RouteBean.PositionsDTO();
                        navPoint.setTheta(positionsItem.getDouble("theta"));
                        double x =  PositionUtil.serverToLocalX(positionsItem.getDouble("world_x"), resolution, originX);
                        navPoint.setWorld_x(x);
                        double y =  PositionUtil.serverToLocalY(positionsItem.getDouble("world_y"), resolution, originY);
                        navPoint.setWorld_y(y);
                        TwoPositions.add(navPoint);
                    }
                    routeBean.setPositions(TwoPositions);
                    routesList.add(routeBean) ;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        Logger.i ("getRoutesInfo", "getrouteInfo：" + routesList.size());
        return routesList;

    }


    /**
     * 获取工程所有的虚拟墙信息
     *
     * @param context
     * @param projectId
     * @return
     */
    public static List<VirtualWallBean> getObstacleInfo(Context context, String projectId) {
        List<VirtualWallBean> virtualWallList = new ArrayList<>();
        String obstacleContent = getObstacleCacheFileOriContent(context, projectId);
        try {
            JSONObject obstacleInfoObject = new JSONObject(obstacleContent);
            JSONObject obstacleObject = obstacleInfoObject.getJSONObject("obstacles");
            if (!obstacleObject.getString("polylines").equals("null")) {
                JSONArray lineArray = obstacleObject.getJSONArray("polylines");
                double resolution = getMapResolution(context, projectId);
                double originX = getMapOriginX(context, projectId);
                double originY = getMapOriginY(context, projectId);
                double mapWidth = ProjectCacheManager.getMapWidth(context, projectId);
                double mapHeight = ProjectCacheManager.getMapHeight(context, projectId);
                double screenWidth = DisplayUtil.getScreenWidth(context);
                double screenHeight = DisplayUtil.getScreenHeight(context);
                Logger.i ( "getObstacleInfo：" + lineArray.length());

                for (int i = 0; i < lineArray.length(); i++) {
                    JSONObject lineItem = lineArray.getJSONObject(i);
                    VirtualWallBean virtualWallBean = new VirtualWallBean();

                    JSONArray pointArray = lineItem.getJSONArray("values");
                    List<VirtualWallBean.WallPoint> wallPointList = new ArrayList<>();
                    for (int j = 0; j < pointArray.length(); j++) {
                        JSONObject pointItem = pointArray.getJSONObject(j);
                        float x = (float) PositionUtil.serverToLocalX(
                                pointItem.getDouble("world_x"),

                                resolution,
                                originX
                        );
                        float y = (float) PositionUtil.serverToLocalY(pointItem.getDouble("world_y"),
                                resolution,
                                originY);

                        VirtualWallBean.WallPoint wallPoint = new VirtualWallBean.WallPoint(
                                x,
                                y
                        );
                        wallPointList.add(wallPoint);
                    }
                    virtualWallBean.setWallPointList(wallPointList);
                    virtualWallList.add(virtualWallBean);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }

        Logger.i ( "getObstacleInfo：" + virtualWallList.size());
        return virtualWallList;
    }


    /**
     * 获取地图的分辨率
     *
     * @return
     */
    public static double getMapResolution(Context context, String projectId) {
        double resolution = 0;
        String content = getMapInfoCacheFileOriContent(context, projectId);
        try {
            JSONObject mapObject = new JSONObject(content);
            JSONObject mapInfoObject = mapObject.getJSONObject("map_info");
            resolution = mapInfoObject.getDouble("resolution");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return resolution;
    }

    /**
     * 获取地图的origin_x
     *
     * @return
     */
    public static double getMapOriginX(Context context, String projectId) {
        double originX = 0;
        String content = getMapInfoCacheFileOriContent(context, projectId);
        try {
            JSONObject mapObject = new JSONObject(content);
            JSONObject mapInfoObject = mapObject.getJSONObject("map_info");
            originX = mapInfoObject.getDouble("origin_x");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return originX;
    }


    /**
     * 获取地图的origin_x
     *
     * @return
     */
    public static double getMapOriginY(Context context, String projectId) {
        double originY = 0;
        String content = getMapInfoCacheFileOriContent(context, projectId);
        try {
            JSONObject mapObject = new JSONObject(content);
            JSONObject mapInfoObject = mapObject.getJSONObject("map_info");
            originY = mapInfoObject.getDouble("origin_y");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return originY;
    }

    /**
     * 获取地图的width
     *
     * @return
     */
    public static double getMapWidth(Context context, String projectId) {
        double mapWidth = 0;
        String content = getMapInfoCacheFileOriContent(context, projectId);
        try {
            JSONObject mapObject = new JSONObject(content);
            JSONObject mapInfoObject = mapObject.getJSONObject("map_info");
            mapWidth = mapInfoObject.getDouble("width");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mapWidth;
    }

    /**
     * 获取地图的高
     *
     * @return
     */
    public static double getMapHeight(Context context, String projectId) {
        double mapHeight = 0;
        String content = getMapInfoCacheFileOriContent(context, projectId);
        try {
            JSONObject mapObject = new JSONObject(content);
            JSONObject mapInfoObject = mapObject.getJSONObject("map_info");
            mapHeight = mapInfoObject.getDouble("height");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mapHeight;
    }

    /**
     * 根据工程Id，判断该工程脚本文件是否缓存
     *
     * @param context
     * @param projectId
     * @returns
     */
    public static boolean scriptInfoFileExits(Context context, String projectId) {
        String scriptFilePath = getScriptFilePath(context, projectId);
        return FileUtil.isFileExists(scriptFilePath);
    }

    /**
     * 根据工程Id，获取该工程脚本文件路径
     *
     * @param context
     * @param projectId
     * @return
     */
    public static String getScriptFilePath(Context context, String projectId) {
        String scriptFilePath = getScriptInfoCacheDirPath(context) + File.separator + projectId + "_" + SCRIPT_FILE_NAME;
        Logger.i ( "getScriptFilePath：" + scriptFilePath);
        return scriptFilePath;
    }


    /**
     * 获取工程脚本缓存目录路径
     *
     * @param context
     * @return
     */
    public static String getScriptInfoCacheDirPath(Context context) {
        String obstacleInfoCacheDirPath = getProjectCacheDirPath(context) + File.separator + SRCRIPT_CACHE_DIR_NAME;
        Logger.i ( "getObstacleInfoCacheDirPath：" + obstacleInfoCacheDirPath);
        return obstacleInfoCacheDirPath;
    }

    /**
     * 创建工程脚本缓存文件
     *
     * @param context
     * @param projectId
     * @return
     */
    public static boolean createScriptFile(Context context, String projectId) {
        String scriptFilePath = getScriptFilePath(context, projectId);
        return FileUtil.createOrExistsFile(scriptFilePath);
    }

    /**
     * 更新工程脚本文件
     *
     * @param context
     * @param projectId
     * @param scriptInfoJson
     */
    public static void updateScriptFile(Context context, String projectId, String scriptInfoJson) {
        String scriptFilePath = getScriptFilePath(context, projectId);
        boolean updateSuccess = FileIOUtil.writeFileFromString(scriptFilePath, scriptInfoJson);
        if (updateSuccess) {
            Logger.i ( "updateScriptFile success");
        } else {
            Logger.i ( "updateScriptFile fail");
        }
    }

    /**
     * 获取地图的脚本
     *
     * @return
     */
    public static double getMapScripts(Context context, String projectId) {
        double resolution = 0;
        String content = getScriptCacheFileOriContent(context, projectId);
        try {
            JSONObject mapObject = new JSONObject(content);
            JSONObject mapInfoObject = mapObject.getJSONObject("map_info");
            resolution = mapInfoObject.getDouble("resolution");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return resolution;
    }

    /**
     * 获取脚本缓存文件原内容
     *
     * @param context
     * @param projectId
     * @return
     */
    public static String getScriptCacheFileOriContent(Context context, String projectId) {
        String content = "";
        String scriptFilePath = getScriptFilePath(context, projectId);
        content = FileIOUtil.readFile2String(scriptFilePath);
        Logger.i ( "getScriptCacheFileOriContent：" + content + "");
        return content;
    }

}

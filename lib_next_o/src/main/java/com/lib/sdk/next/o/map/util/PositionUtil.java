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
package com.lib.sdk.next.o.map.util;

import android.content.Context;
import android.util.Log;

import com.lib.sdk.next.o.map.bean.PositionPointBean;
import com.lib.sdk.next.o.map.bean.VirtualWallBean;
import com.lib.sdk.next.o.map.manager.ProjectCacheManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by maqing 2018/11/26 19:52
 * Email：2856992713@qq.com
 * 手机地图坐标点和服务器坐标点转化
 */
public class PositionUtil {
    private static final String TAG = "PositionUtil";

    /**
     * 屏幕坐标点转地图坐标点
     *
     * @param screenX
     * @param screenWidth
     * @param mapWidth
     * @return
     */
    public static double screenToMapX(double screenX, double screenWidth, double mapWidth) {
        return screenX / screenWidth * mapWidth;
    }

    /**
     * @param screenY
     * @param screenHeight
     * @param mapHeight
     * @return
     */
    public static double screenToMapY(double screenY, double screenHeight, double mapHeight) {
        return screenY / screenHeight * mapHeight;
    }

    /**
     * 本地的X坐标转服务器X坐标
     *
     * @param resolution
     * @param resolution
     * @param originX
     * @return
     */
    public static double localToServerX(double mapX,
                                        double resolution, double originX) {
        return mapX * resolution + originX;
    }

    /**
     * 本地的Y坐标转服务器Y坐标
     *
     * @param mapY
     * @param resolution
     * @param originY
     * @return
     */
    public static double localToServerY(double mapY,
                                        double resolution, double originY) {
        return -mapY * resolution + originY;
    }

    /**
     * 本地的长度转服务器长度
     *
     * @param radius
     * @param resolution
     * @return
     */
    public static double localToServer(double radius,
                                       double resolution) {
        return -radius * resolution;
    }

    /**
     * 服务器的x坐标转本地x坐标
     *
     * @param resolution
     * @param originX
     * @return
     */
    public static double serverToLocalX(double serverX, double resolution, double originX) {
        return (serverX - originX) / resolution;
    }

    /**
     * 服务器的x坐标转本地x坐标
     *
     * @param serverY
     * @param resolution
     * @param originY
     * @return
     */
    public static double serverToLocalY(double serverY, double resolution, double originY) {
        return -(serverY - originY) / resolution;
    }


    /**
     * 标记点列表数据转服务器Json字符串
     *
     * @param context
     * @param projectId
     * @param positionPointList
     * @return
     */
    public static String positionPointListToServerJsonStr(Context context, String projectId, List<PositionPointBean> positionPointList) {
        String positionInfoJson = "";
        JSONObject positionInfoObject = new JSONObject();
        JSONArray positionArray = new JSONArray();

        try {
            for (PositionPointBean positionPointBean : positionPointList) {
                JSONObject positionItem = new JSONObject();
                positionItem.put("name", positionPointBean.getPointName());
                positionItem.put("type", positionPointBean.getType());

                JSONObject positionObject = new JSONObject();
                double resolution = ProjectCacheManager.getMapResolution(context, projectId);
                double originX = ProjectCacheManager.getMapOriginX(context, projectId);
                double originY = ProjectCacheManager.getMapOriginY(context, projectId);

                positionObject.put("world_x", PositionUtil.localToServerX(
                        positionPointBean.getBitmapX(),
                        resolution,
                        originX
                ));

                positionObject.put("world_y", PositionUtil.localToServerY(
                        positionPointBean.getBitmapY(),
                        resolution,
                        originY
                ));

                double serverTheta = 0;
                if (positionPointBean.getTheta() >= -Math.PI / 2 && positionPointBean.getTheta() <= 0) {
                    serverTheta = Math.PI / 2 - positionPointBean.getTheta();
                }

                if (positionPointBean.getTheta() > 0 && positionPointBean.getTheta() <= Math.PI) {
                    serverTheta = Math.PI / 2 - positionPointBean.getTheta();
                }

                if (positionPointBean.getTheta() >= -Math.PI && positionPointBean.getTheta() <= -Math.PI / 2) {
                    serverTheta = -Math.PI * 3f / 2 - positionPointBean.getTheta();
                }

                positionObject.put("theta", serverTheta);

                Log.e(TAG, "positionPointListToServerJsonStr：" + positionPointBean.getTheta() + "," + serverTheta);

                positionItem.put("position", positionObject);
                positionArray.put(positionItem);
            }

            positionInfoObject.put("positions", positionArray);

            positionInfoJson = positionInfoObject.toString();
            Log.e(TAG, "positionPointListToServerJsonStr：" + positionInfoJson);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return positionInfoJson;
    }

    /**
     * 标记点名字是否已经存在了
     *
     * @param name
     * @param positionPointList
     * @return
     */
    public static boolean isPositionPointNameExist(String name, List<PositionPointBean> positionPointList) {
        for (PositionPointBean positionPointBean : positionPointList) {
            if (positionPointBean.getPointName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 删除某标记
     *
     * @param positionPointBean
     * @param positionPointList
     * @return
     */
    public static boolean deletePositionPoint(PositionPointBean positionPointBean, List<PositionPointBean> positionPointList) {
        for (PositionPointBean pointBean : positionPointList) {
            if (pointBean.getPointName().equals(positionPointBean.getPointName()) && positionPointBean.getType() == pointBean.getType()) {
                positionPointList.remove(pointBean);
                return true;
            }
        }
        return false;
    }

    /**
     * 获取初始化点下标
     *
     * @return
     */
    public static int getInitPositionPointIndex(List<PositionPointBean> positionPointList) {
        for (int i = 0; i < positionPointList.size(); i++) {
            if (positionPointList.get(i).getType() == PositionPointBean.TYPE_INIT_POINT) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 获取充电点下标
     *
     * @return
     */
    public static int getChargePositionPointItem(List<PositionPointBean> positionPointList) {
        for (int i = 0; i < positionPointList.size(); i++) {
            if (positionPointList.get(i).getType() == PositionPointBean.TYPE_CHARGE_POINT) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 获取待命点下标
     *
     * @return
     */
    public static int getStandbyPositionPointIndex(List<PositionPointBean> positionPointList) {
        for (int i = 0; i < positionPointList.size(); i++) {
            if (positionPointList.get(i).getType() == PositionPointBean.TYPE_STANDBY_POINT) {
                return i;
            }
        }
        return -1;
    }


    /**
     * 虚拟墙列表数据转服务器Json字符串
     *
     * @param context
     * @param projectId
     * @param virtualWallList
     * @return
     */
    public static String obstacleListToServerJsonStr(Context context, String projectId, List<VirtualWallBean> virtualWallList) {
        String obstacleInfoJson = "";
        JSONObject obstacleInfoObject = new JSONObject();
        JSONObject obstacleObject = new JSONObject();
        try {
            obstacleObject.put("circles", null);
            obstacleObject.put("rectangles", null);
            JSONArray lineArray = new JSONArray();
            double resolution = ProjectCacheManager.getMapResolution(context, projectId);
            double originX = ProjectCacheManager.getMapOriginX(context, projectId);
            double originY = ProjectCacheManager.getMapOriginY(context, projectId);
            double mapWidth = ProjectCacheManager.getMapWidth(context, projectId);
            double mapHeight = ProjectCacheManager.getMapHeight(context, projectId);
            double screenWidth = DisplayUtil.getScreenWidth(context);
            double screenHeight = DisplayUtil.getScreenHeight(context);
            for (VirtualWallBean virtualWallBean : virtualWallList) {
                JSONObject lineItem = new JSONObject();
                List<VirtualWallBean.WallPoint> wallPointList = virtualWallBean.getWallPointList();
                JSONArray pointArray = new JSONArray();
                for (VirtualWallBean.WallPoint wallPoint : wallPointList) {
                    JSONObject pointItem = new JSONObject();
                    pointItem.put("world_x", PositionUtil.localToServerX(wallPoint.getX(), resolution, originX));
                    pointItem.put("world_y", PositionUtil.localToServerY(wallPoint.getY(), resolution, originY));
                    pointArray.put(pointItem);
                }
                lineItem.put("values", pointArray);
                lineArray.put(lineItem);
            }

            if (lineArray.length() == 0) {
                obstacleObject.put("polylines", null);
            } else {
                obstacleObject.put("polylines", lineArray);
            }
            obstacleInfoObject.put("obstacles", obstacleObject);
            obstacleInfoJson = obstacleInfoObject.toString();
            Log.e(TAG, "obstacleListToServerJsonStr：" + obstacleInfoJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obstacleInfoJson;
    }

    /**
     * 路径规划点列表数据转服务器Json字符串
     *
     * @param context
     * @param projectId
     * @param positionPointList
     * @return
     */
    public static String taskListToServerJsonStr(Context context, String projectId, int times, List<PositionPointBean> positionPointList) {
        String taskInfoJson = "";
        JSONObject taskInfoObject = new JSONObject();
        try {
            taskInfoObject.put("task_name", "路径规划");
            JSONObject paramterObject = new JSONObject();
            JSONArray positionArray = new JSONArray();
            double resolution = ProjectCacheManager.getMapResolution(context, projectId);
            double originX = ProjectCacheManager.getMapOriginX(context, projectId);
            double originY = ProjectCacheManager.getMapOriginY(context, projectId);
            double mapWidth = ProjectCacheManager.getMapWidth(context, projectId);
            double mapHeight = ProjectCacheManager.getMapHeight(context, projectId);
            double screenWidth = DisplayUtil.getScreenWidth(context);
            double screenHeight = DisplayUtil.getScreenHeight(context);
            for (PositionPointBean positionPointBean : positionPointList) {
                JSONObject positionItem = new JSONObject();
                positionItem.put("name", positionPointBean.getPointName());
                positionItem.put("type", positionPointBean.getType());

                JSONObject positionObject = new JSONObject();


                Log.e(TAG, resolution + "," + originX + "," + originY
                        + positionPointBean.getBitmapX() + "," + positionPointBean.getBitmapY()
                );

                positionObject.put("world_x", PositionUtil.localToServerX(
                        positionPointBean.getBitmapX(),
                        resolution,
                        originX
                ));

                positionObject.put("world_y", PositionUtil.localToServerY(
                        positionPointBean.getBitmapX(),
                        resolution,
                        originY
                ));

                double serverTheta = 0;
                if (positionPointBean.getTheta() >= -Math.PI / 2 && positionPointBean.getTheta() <= 0) {
                    serverTheta = Math.PI / 2 - positionPointBean.getTheta();
                }
                if (positionPointBean.getTheta() > 0 && positionPointBean.getTheta() <= Math.PI) {
                    serverTheta = Math.PI / 2 - positionPointBean.getTheta();
                }
                if (positionPointBean.getTheta() >= -Math.PI && positionPointBean.getTheta() <= -Math.PI / 2) {
                    serverTheta = -Math.PI * 3f / 2 - positionPointBean.getTheta();
                }
                positionObject.put("theta", serverTheta);

                positionObject.put("theta", serverTheta);

                positionItem.put("position", positionObject);

                Log.e(TAG, positionItem.toString());

                positionArray.put(positionItem);

            }
            paramterObject.put("positions", positionArray);
            taskInfoObject.put("parameters", paramterObject);
            taskInfoObject.put("loop", times);
            taskInfoJson = taskInfoObject.toString();
            Log.e(TAG, "taskListToServerJsonStr：" + taskInfoJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return taskInfoJson;
    }

    /**
     * 设置导航点是否显示名字
     *
     * @param positionPointList       所有标记点列表
     * @param navigationPositionPoint 被选中的导航点
     */
    public static void setPointShowNameEnable(List<PositionPointBean> positionPointList, PositionPointBean navigationPositionPoint) {
        for (PositionPointBean pointBean : positionPointList) {
            if (pointBean.getType() == navigationPositionPoint.getType()
            ) {
                if (pointBean.getPointName().equals(navigationPositionPoint.getPointName()) && pointBean.getType() == navigationPositionPoint.getType()) {
                    pointBean.setNeedShowName(!pointBean.isNeedShowName());
                } else {
                    pointBean.setNeedShowName(false);
                }
            }
        }
    }

    /**
     * 设置导航点是否显示名字
     *
     * @param positionPointList 所有标记点列表
     * @param point
     */
    public static PositionPointBean getPositionPointByName(List<PositionPointBean> positionPointList, PositionPointBean point) {
        for (PositionPointBean positionPointBean : positionPointList) {
            if (positionPointBean.getPointName().equals(point.getPointName()) && point.getType() == positionPointBean.getType()) {
                return positionPointBean;
            }
        }
        return null;
    }

    /**
     * 判断标记点是否改变(theta，bitmapX,bitmapY发生改变，就算是改变)
     *
     * @param pointNow
     * @param pointOrigin
     */
    public static boolean positionPointChanged(PositionPointBean pointNow, PositionPointBean pointOrigin) {
        Log.e(TAG, pointNow + "\n" + pointOrigin);
        if (pointNow.getTheta() != pointOrigin.getTheta()
                || pointNow.getBitmapX() != pointOrigin.getBitmapX() ||
                pointNow.getBitmapY() != pointOrigin.getBitmapY()
        ) {
            return true;
        }
        return false;
    }

    /**
     * 根据导航点更新路径规划点列表
     *
     * @param navigationPositionList
     */
    public static List<PositionPointBean> updateTaskByNavigationPosition(List<PositionPointBean> oldTaskPositionList, List<PositionPointBean> navigationPositionList) {
        //更新路径规划点
        for (int i = 0; i < oldTaskPositionList.size(); i++) {

            boolean found = false;

            for (int j = 0; j < navigationPositionList.size(); j++) {
                if (oldTaskPositionList.get(i).getPointName().equals(
                        navigationPositionList.get(j).getPointName()
                ) && oldTaskPositionList.get(i).getType() == navigationPositionList.get(j).getType()) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                oldTaskPositionList.remove(i);
                i = 0;
            }
        }
        return oldTaskPositionList;
    }

}

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
package com.lib.sdk.next.o.map.bean;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.Log;

import com.lib.sdk.next.o.map.util.DrawUtil;

import java.util.List;

/**
 * 航路 bean
 * {
 * "paths": [
 * {
 * "direction": 0,
 * "positions": [
 * {
 * "theta": 1.5707963267948966,
 * "world_x": -1.781773156713765,
 * "world_y": 0.8556118686554406
 * },
 * {
 * "theta": 0,
 * "world_x": 1.9635042480713913,
 * "world_y": -4.378554848873854
 * }
 * ],
 * "width": 0.3
 * }
 * ]
 * }
 */
public class RouteBean {

    /**
     * 是否被选中 （非对应的属性）
     */
    private boolean isSelcted;

    public void setSelcted(boolean selcted) {
        isSelcted = selcted;
    }

    public boolean getSelctedStatus() {
        return isSelcted;
    }

    /**
     * 航路名称 （非对应属性）
     */
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 航路的路径 （非对应属性）
     */
    private Path mPath;

    public Path getmPath() {
        return mPath;
    }

    public void setmPath(Path mPath) {
        this.mPath = mPath;
    }

    /**
     * 正向画箭头的路径
     */
    private Path mFrontPath;

    /**
     * 反向画箭头的路径
     */
    private Path mBackPath ;

    public Path getmFrontPath() {
        return mFrontPath;
    }

    public Path getmBackPath() {
        return mBackPath;
    }

    public void setmFrontPath(Path mFrontPath) {
        this.mFrontPath = mFrontPath;
    }

    public void setmBackPath(Path mBackPath) {
        this.mBackPath = mBackPath;
    }

    /**
     * 是否需要显示标记点名字背景
     */
    private boolean mNeedShowName;

    public boolean ismNeedShowName() {
        return mNeedShowName;
    }

    public void setmNeedShowName(boolean mNeedShowName) {
        this.mNeedShowName = mNeedShowName;
    }

    /**
     * direction : 0
     * positions : [{"theta":1.5707963267948966,"world_x":-1.781773156713765,"world_y":0.8556118686554406},
     * {"theta":0,"world_x":1.9635042480713913,"world_y":-4.378554848873854}
     * ]
     * width : 0.3
     */

    private Integer direction;
    private List<PositionsDTO> positions;
    private Double width;

    public Integer getDirection() {
        return direction;
    }

    public void setDirection(Integer direction) {
        this.direction = direction;
    }

    public List<PositionsDTO> getPositions() {
        return positions;
    }

    public void setPositions(List<PositionsDTO> positions) {
        this.positions = positions;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public static class PositionsDTO {
        /**
         * theta : 1.5707963267948966
         * world_x : -1.781773156713765
         * world_y : 0.8556118686554406
         */

        private Double theta;
        private Double world_x;
        private Double world_y;

        public PositionsDTO(double x, double y) {
            world_x = x;
            world_y = y;
        }

        public PositionsDTO() {
        }


        public Double getTheta() {
            return theta;
        }

        public void setTheta(Double theta) {
            this.theta = theta;
        }

        public Double getWorld_x() {
            return world_x;
        }

        public void setWorld_x(Double world_x) {
            this.world_x = world_x;
        }

        public Double getWorld_y() {
            return world_y;
        }

        public void setWorld_y(Double world_y) {
            this.world_y = world_y;
        }
    }

    /**
     * 获取航路的最高点(y值越小越高)
     */
    public PositionsDTO getTheHighestPoint() {
        PositionsDTO highestNavgationPoint = positions.get(0);
        for (PositionsDTO routePoint : positions) {
            if (routePoint.getWorld_y() < highestNavgationPoint.getWorld_y()) {
                highestNavgationPoint = routePoint;
            }
        }
        return highestNavgationPoint;
    }


    /**
     * 是否击中该标记点的名字关闭图标
     */
    public boolean containsNameClose(float x, float y) {

        //航路最高点
        PathMeasure measure = new PathMeasure(getmPath(), false);
        float distance = measure.getLength() / 2;
        float[] pos = new float[2];
        float[] tan = new float[2];
        measure.getPosTan(distance, pos, tan);
        float rountX = pos[0]  - mNameRect.width() / 5f + mNameRect.width() - mNameCloseRect.width() / 2;
        float rountY = pos[1] - mNameRect.height() - mNameCloseRect.height() / 2;

        PointF nameCloseLocation = new PointF();
        nameCloseLocation.x = rountX;
        nameCloseLocation.y = rountY;

        //把触摸点转换成在文字坐标系（即以文字起始点作为坐标原点）内的点
        x = x - nameCloseLocation.x;
        y = y - nameCloseLocation.y;

        float pivotX = nameCloseLocation.x + mNameCloseRect.width() / 2;
        float pivotY = nameCloseLocation.y + mNameCloseRect.height() / 2;

        //把变换后相对于矩形的触摸点，还原回未变换前的点，然后判断是否矩形中
        Rect mNameCloseRectTemp = new Rect();

        PointF mTempNameCloseLocation = new PointF();
        mTempNameCloseLocation = rotatePoint(mTempNameCloseLocation, (int) -0, x, y, pivotX - nameCloseLocation.x, pivotY - nameCloseLocation.y);

        mNameCloseRectTemp.set(mNameCloseRect);
        return mNameCloseRectTemp.contains((int) mTempNameCloseLocation.x, (int) mTempNameCloseLocation.y);
    }

    private Rect mNameRect = new Rect();
    private Rect mNameSrcRect = new Rect();
    private Rect mNameDstRect = new Rect();

    private Rect mNameCloseRect = new Rect();
    private Rect mNameCloseSrcRect = new Rect();
    private Rect mNameCloseDstRect = new Rect();

    /**
     * 绘制航路提示框
     * @param canvas
     * @param bitmap
     * @param paint
     * @param name
     * @param nameSize
     * @param path
     */
    public void drawRouteNameBg(Canvas canvas, Bitmap bitmap, Paint paint, String name, float nameSize,Path path) {

        Bitmap textBitmap = DrawUtil.drawTextToBitmap(bitmap, name, paint);

        mNameRect = new Rect();
        mNameRect.set(0, 0, (int) nameSize, (int) (nameSize * textBitmap.getHeight() / textBitmap.getWidth()));

        mNameSrcRect = new Rect();
        mNameSrcRect.set(0, 0, textBitmap.getWidth(), textBitmap.getHeight());

        mNameDstRect = new Rect();
        mNameDstRect.set(0, 0, (int) nameSize, (int) (nameSize * textBitmap.getHeight()) / textBitmap.getWidth());

        PathMeasure measure = new PathMeasure(path, false);
        float distance = measure.getLength() / 2;
        float[] pos = new float[2];
        float[] tan = new float[2];
        measure.getPosTan(distance, pos, tan);
        float rountX = pos[0] - mNameRect.width() / 5f;
        float rountY = pos[1] - mNameRect.height();
        canvas.translate(rountX, rountY);
        canvas.drawBitmap(textBitmap, mNameSrcRect, mNameDstRect, null);

    }

    /**
     * 绘制航路关闭按钮
     *
     * @param canvas
     * @param nameCloseBitmap
     * @param nameCloseSize
     */
    public void drawNameCloseBg(Canvas canvas,Bitmap nameCloseBitmap, float nameCloseSize,Path path) {

        //名字关闭按钮
        mNameCloseRect = new Rect();
        mNameCloseRect.set(0, 0, (int) nameCloseSize, (int) (nameCloseSize * nameCloseBitmap.getHeight() / nameCloseBitmap.getWidth()));

        mNameCloseSrcRect = new Rect();
        mNameCloseSrcRect.set(0, 0, nameCloseBitmap.getWidth(), nameCloseBitmap.getHeight());

        mNameCloseDstRect = new Rect();
        mNameCloseDstRect.set(0, 0, (int) nameCloseSize, (int) (nameCloseSize * nameCloseBitmap.getHeight()) / nameCloseBitmap.getWidth());

        PathMeasure measure = new PathMeasure(path, false);
        float distance = measure.getLength() / 2;
        float[] pos = new float[2];
        float[] tan = new float[2];
        measure.getPosTan(distance, pos, tan);
        float rountX = pos[0]  - mNameRect.width() / 5f + mNameRect.width() - mNameCloseRect.width() / 2;
        float rountY = pos[1] - mNameRect.height() - mNameCloseRect.height() / 2;
        canvas.translate(rountX, rountY);
        canvas.drawBitmap(nameCloseBitmap, mNameCloseSrcRect, mNameCloseDstRect, null);

    }

    // 顺时针旋转
    public static PointF rotatePoint(PointF coords, float degree, float x, float y, float px, float py) {
        if (degree % 360 == 0) {
            coords.x = x;
            coords.y = y;
            return coords;
        }
        /*角度变成弧度*/
        float radian = (float) (degree * Math.PI / 180);
        coords.x = (float) ((x - px) * Math.cos(radian) - (y - py) * Math.sin(radian) + px);
        coords.y = (float) ((x - px) * Math.sin(radian) + (y - py) * Math.cos(radian) + py);

        return coords;
    }


    /**
     * 判断点point是否在p0 和 p1两点构成的线段上
     */
    public static boolean isInLineTwoPoint(PositionsDTO point0, PositionsDTO point1, PositionsDTO point) {
        float maxAllowOffsetLength = 15;
        //通过直线方程的两点式计算出一般式的ABC参数，具体可以自己拿起笔换算一下，很容易
        float A = (float) (point1.getWorld_y() - point0.getWorld_y());
        float B = (float) (point0.getWorld_x() - point1.getWorld_x());
        float C = (float) (point1.getWorld_x() * point0.getWorld_y() - point0.getWorld_x() * point1.getWorld_y());
        //带入点到直线的距离公式求出点到直线的距离dis
        float dis = (float) Math.abs((A * point.getWorld_x() + B * point.getWorld_y() + C) / Math.sqrt(Math.pow(A, 2) + Math.pow(B, 2)));
        if (dis > maxAllowOffsetLength || (dis == Float.POSITIVE_INFINITY)) {
            return false;
        } else {
            //否则我们要进一步判断，投影点是否在线段上，根据公式求出投影点的X坐标jiaoX
            float D = (float) (A * point.getWorld_y() - B * point.getWorld_x());
            float jiaoX = (float) (-(A * C + B * D) / (Math.pow(B, 2) + Math.pow(A, 2)));
            //判断jiaoX是否在线段上，t如果在0~1之间说明在线段上，大于1则说明不在线段且靠近端点p1，小于0则不在线段上且靠近端点p0，这里用了插值的思想
            float t = (float) ((jiaoX - point0.getWorld_x()) / (point1.getWorld_x() - point0.getWorld_x()));
            if (t > 1 || (t == Float.POSITIVE_INFINITY)) {
                //最小距离为到p1点的距离
                dis = XWLengthOfTwoPoint(point1, point);
            } else if (t < 0) {
                //最小距离为到p2点的距离
                dis = XWLengthOfTwoPoint(point0, point);
            }
            //再次判断真正的最小距离是否小于允许值，小于则该点在直线上，反之则不在
            if (dis <= maxAllowOffsetLength) {
                Log.e("locationInfo","在直线上");
                return true;
            } else {
                Log.e("locationInfo","不在直线上");
                return false;
            }
        }
    }

    //这里是求两点距离公式
    public static float XWLengthOfTwoPoint(PositionsDTO point1, PositionsDTO point2) {
        return (float) Math.sqrt(Math.pow(point1.getWorld_x() - point2.getWorld_x(), 2) + Math.pow(point1.getWorld_y() - point2.getWorld_y(), 2));
    }

}


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
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;

import com.lib.sdk.next.o.map.util.DrawUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maqing 2018/11/13 11:16
 * Email：2856992713@qq.com
 * 虚拟墙
 */
public class VirtualWallBean {
    /**
     * 虚拟墙的路径
     */
    private Path mPath;
    /**
     * 虚拟墙上点的集合
     */
    private List<WallPoint> mWallPointList = new ArrayList<>();

    /**
     * 是否需要显示标记点名字背景
     */
    private boolean mNeedShowName;
    /**
     * 虚拟墙的名字
     */
    private String mName = "";

    private Rect mNameRect = new Rect();
    private Rect mNameSrcRect = new Rect();
    private Rect mNameDstRect = new Rect();

    private Rect mNameCloseRect = new Rect();
    private Rect mNameCloseSrcRect = new Rect();
    private Rect mNameCloseDstRect = new Rect();

    private boolean isSelect;

    public Path getPath() {
        return mPath;
    }

    public void setPath(Path path) {
        mPath = path;
    }

    public List<WallPoint> getWallPointList() {
        return mWallPointList;
    }

    public void setWallPointList(List<WallPoint> wallPointList) {
        mWallPointList = wallPointList;
    }

    public static class WallPoint {
        double mX;
        double mY;

        public WallPoint(double x, double y) {
            mX = x;
            mY = y;
        }

        public double getX() {
            return mX;
        }

        public void setX(double x) {
            mX = x;
        }

        public double getY() {
            return mY;
        }

        public void setY(double y) {
            mY = y;
        }

        @Override
        public String toString() {
            return "WallPoint{" +
                    "mX=" + mX +
                    ", mY=" + mY +
                    '}';
        }
    }

    /**
     * 获取虚拟墙中的最高点
     */
    public WallPoint getTheHighestPoint() {
        WallPoint highestWallPoint = mWallPointList.get(0);
        for (WallPoint wallPoint : mWallPointList) {
            if (wallPoint.getY() < highestWallPoint.getY()) {
                highestWallPoint = wallPoint;
            }
        }
        return highestWallPoint;
    }

//    public void drawNameBg(Canvas canvas, Bitmap bitmap, Paint paint, String name, float scale) {
//        WallPoint highestWallPoint = getTheHighestPoint();
//        Bitmap textBitmap = DrawUtil.drawTextToBitmap(bitmap, name, paint);
//        canvas.drawBitmap(textBitmap, ((float) highestWallPoint.getX() * scale)
//                -textBitmap.getWidth()*1f/4
//                , (float) highestWallPoint.getY() * scale-paint.getStrokeWidth()*scale
//                -textBitmap.getHeight()
//                , null);
//    }

    public void drawNameBg(Canvas canvas, Bitmap bitmap, Paint paint, String name, float nameSize) {

        Bitmap textBitmap = DrawUtil.drawTextToBitmap(bitmap, name, paint);

        mNameRect = new Rect();
        mNameRect.set(0, 0, (int) nameSize, (int) (nameSize * textBitmap.getHeight() / textBitmap.getWidth()));

        mNameSrcRect = new Rect();
        mNameSrcRect.set(0, 0, textBitmap.getWidth(), textBitmap.getHeight());

        mNameDstRect = new Rect();
        mNameDstRect.set(0, 0, (int) nameSize, (int) (nameSize * textBitmap.getHeight()) / textBitmap.getWidth());

        WallPoint highestWallPoint = getTheHighestPoint();
        canvas.translate(((float) highestWallPoint.getX()) - mNameRect.width() / 5f, (float) highestWallPoint.getY() - mNameRect.height());
        canvas.drawBitmap(textBitmap, mNameSrcRect, mNameDstRect, null);
    }

    /**
     * 绘制虚拟墙名字关闭标记
     *
     * @param canvas
     * @param nameCloseBitmap
     * @param nameCloseSize
     */
    public void drawNameCloseBg(Canvas canvas, Bitmap bitmap, Paint paint, String name, float nameSize, Bitmap nameCloseBitmap, float nameCloseSize) {


//        //名字
//        Bitmap textBitmap = DrawUtil.drawTextToBitmap(bitmap, name, paint);
//        Rect nameRect = new Rect();
//        nameRect.set(0, 0, (int) nameSize, (int) (nameSize * textBitmap.getHeight() / textBitmap.getWidth()));

        //名字关闭按钮
        mNameCloseRect = new Rect();
        mNameCloseRect.set(0, 0, (int) nameCloseSize, (int) (nameCloseSize * nameCloseBitmap.getHeight() / nameCloseBitmap.getWidth()));

        mNameCloseSrcRect = new Rect();
        mNameCloseSrcRect.set(0, 0, nameCloseBitmap.getWidth(), nameCloseBitmap.getHeight());

        mNameCloseDstRect = new Rect();
        mNameCloseDstRect.set(0, 0, (int) nameCloseSize, (int) (nameCloseSize * nameCloseBitmap.getHeight()) / nameCloseBitmap.getWidth());

        WallPoint highestWallPoint = getTheHighestPoint();

        canvas.translate(((float) highestWallPoint.getX()) - mNameRect.width() / 5f + mNameRect.width() - mNameCloseRect.width() / 2, (float) highestWallPoint.getY() - mNameRect.height() - mNameCloseRect.height() / 2);

        canvas.drawBitmap(nameCloseBitmap, mNameCloseSrcRect, mNameCloseDstRect, null);

    }


    /**
     * 是否击中该标记点的名字关闭图标
     */
    public boolean containsNameClose(float x, float y) {

//        //名字
//        Bitmap textBitmap = DrawUtil.drawTextToBitmap(bitmap, name, paint);
//        Rect nameRect = new Rect();
//        nameRect.set(0, 0, (int) nameSize, (int) (nameSize * textBitmap.getHeight() / textBitmap.getWidth()));
//
//        //名字关闭按钮
//        Rect nameCloseRect = new Rect();
//        nameCloseRect.set(0, 0, (int) nameCloseSize, (int) (nameCloseSize * nameCloseBitmap.getHeight() / nameCloseBitmap.getWidth()));
//
//        Rect nameSrcRect = new Rect();
//        nameSrcRect.set(0, 0, nameCloseBitmap.getWidth(), nameCloseBitmap.getHeight());
//
//        Rect nameDstRect = new Rect();
//        nameDstRect.set(0, 0, (int) nameCloseSize, (int) (nameCloseSize * nameCloseBitmap.getHeight()) / nameCloseBitmap.getWidth());


        //虚拟墙最高点
        WallPoint highestWallPoint = getTheHighestPoint();

        PointF nameCloseLocation = new PointF();
        nameCloseLocation.x = (float) (highestWallPoint.mX - mNameRect.width() / 5f + mNameRect.width() - mNameCloseRect.width() / 2);
        nameCloseLocation.y = (float) (highestWallPoint.mY - mNameRect.height() - mNameCloseRect.height() / 2);

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

//        resetNameCloseBounds(mNameCloseRect);
//
//        PointF location = getNameCloseLocation();
//        // 把触摸点转换成在文字坐标系（即以文字起始点作为坐标原点）内的点
//        x = x - location.x;
//        y = y - location.y;
//        // 把变换后相对于矩形的触摸点，还原回未变换前的点，然后判断是否矩形中
//        mTempNameCloseLocation = rotatePoint(mTempNameCloseLocation, (int) -0, x, y, getNameClosePivotX() - getNameCloseLocation().x, getNameClosePivotY() - getNameCloseLocation().y);
//        mNameCloseRectTemp.set(mNameCloseRect);
//        return mNameCloseRectTemp.contains((int) mTempNameCloseLocation.x, (int) mTempNameCloseLocation.y);

    }


    public boolean pointInPath(Point point) {
        RectF bounds = new RectF();
        mPath.computeBounds(bounds, true);
        Region region = new Region();
        region.setPath(mPath, new Region((int) bounds.left, (int) bounds.top, (int) bounds.right, (int) bounds.bottom));
        return region.contains(point.x, point.y);
    }


    public boolean isNeedShowName() {
        return mNeedShowName;
    }

    public void setNeedShowName(boolean needShowName) {
        mNeedShowName = needShowName;
    }

    /**
     * 判断点point是否在p0 和 p1两点构成的线段上
     */
    public static boolean isInLineTwoPoint(WallPoint point0, WallPoint point1, WallPoint point) {
        float maxAllowOffsetLength = 15;
        //通过直线方程的两点式计算出一般式的ABC参数，具体可以自己拿起笔换算一下，很容易
        float A = (float) (point1.getY() - point0.getY());
        float B = (float) (point0.getX() - point1.getX());
        float C = (float) (point1.getX() * point0.getY() - point0.getX() * point1.getY());
        //带入点到直线的距离公式求出点到直线的距离dis
        float dis = (float) Math.abs((A * point.getX() + B * point.getY() + C) / Math.sqrt(Math.pow(A, 2) + Math.pow(B, 2)));
        if (dis > maxAllowOffsetLength || (dis == Float.POSITIVE_INFINITY)) {
            return false;
        } else {
            //否则我们要进一步判断，投影点是否在线段上，根据公式求出投影点的X坐标jiaoX
            float D = (float) (A * point.getY() - B * point.getX());
            float jiaoX = (float) (-(A * C + B * D) / (Math.pow(B, 2) + Math.pow(A, 2)));
            //判断jiaoX是否在线段上，t如果在0~1之间说明在线段上，大于1则说明不在线段且靠近端点p1，小于0则不在线段上且靠近端点p0，这里用了插值的思想
            float t = (float) ((jiaoX - point0.getX()) / (point1.getX() - point0.getX()));
            if (t > 1 || (t == Float.POSITIVE_INFINITY)) {
                //最小距离为到p1点的距离
                dis = XWLengthOfTwoPoint(point1, point);
            } else if (t < 0) {
                //最小距离为到p2点的距离
                dis = XWLengthOfTwoPoint(point0, point);
            }
            //再次判断真正的最小距离是否小于允许值，小于则该点在直线上，反之则不在
            if (dis <= maxAllowOffsetLength) {
                return true;
            } else {
                return false;
            }
        }
    }


    //这里是求两点距离公式
    public static float XWLengthOfTwoPoint(WallPoint point1, WallPoint point2) {
        return (float) Math.sqrt(Math.pow(point1.getX() - point2.getX(), 2) + Math.pow(point1.getY() - point2.getY(), 2));
    }


    //我们首先提供一个函数，将上述公式转换成代码
    public static WallPoint XWPointOnPowerCurveLine(WallPoint p0, WallPoint p1, WallPoint p2, float t) {
        float x = (float) (Math.pow(1 - t, 2) * p0.getX() + 2 * t * (1 - t) * p1.getX() + Math.pow(t, 2) * p2.getX());
        float y = (float) (Math.pow(1 - t, 2) * p0.getY() + 2 * t * (1 - t) * p1.getY() + Math.pow(t, 2) * p2.getY());
        return new WallPoint(x, y);
    }


    /**
     * 判断点在二阶贝塞尔曲线上
     */
    public static boolean xw_containsPointForCurveLineType(WallPoint p0, WallPoint p1, WallPoint p2, WallPoint point) {
        WallPoint tempPoint1 = p0;
        // 记录采样的每条线段起点，第一次起点就是p0
        WallPoint tempPoint2 = null;
        //记录采样线段终点
        //这里我取了100个点，基本上满足要求了
        for (int i = 1; i < 101; i++) {
            //计算出终点
            tempPoint2 = XWPointOnPowerCurveLine(p0, p1, p2, i / 100.0f);
            //调用我们解决第一种情况的方法，判断点是否在这两点构成的直线上
            if (isInLineTwoPoint(tempPoint1, tempPoint2, point)) {
                //如果在可以认为点在这条贝塞尔曲线上，直接跳出循环返回即可
                return true;
            }
            //如果不在则赋值准备下一次循环
            tempPoint1 = tempPoint2;
        }
        return false;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
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

    @Override
    public String toString() {
        return "VirtualWallBean{" +
                "mPath=" + mPath +
                ", mWallPointList=" + mWallPointList +
                '}';
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}

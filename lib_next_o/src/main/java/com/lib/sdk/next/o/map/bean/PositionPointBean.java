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
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.Log;


import com.lib.sdk.next.o.map.util.DrawUtil;

import java.io.Serializable;


/**
 * Created by maqing on 2018/11/14.
 * Email:2856992713@qq.com
 * 地图上的标记点
 */
public class PositionPointBean implements Serializable {
    /**
     * 标记点图片
     */
    private Bitmap mBitmap;
    /**
     * 标记点大图
     */
    private Bitmap mBigBitmap;

    private float mSize;

    private boolean isSelcted = false;

    public boolean isSelcted() {
        return isSelcted;
    }

    public void setSelcted(boolean selcted) {
        isSelcted = selcted;
    }

    private PointF mLocation = new PointF();
    /**
     * 中心点
     */
    private float mPivotX, mPivotY;
    /**
     * 方向角
     */
    private double mTheta;

    /**
     * item的旋转角度
     */
    private float mItemRotate;
    /**
     * 标记点类型
     */
    private int mType;
    /**
     * 初始点
     */
    public static final int TYPE_INIT_POINT = 0;
    /**
     * 充点电
     */
    public static final int TYPE_CHARGE_POINT = 1;
    /**
     * 导航点
     */
    public static final int TYPE_NAVIGATION_POINT = 2;
    /**
     * 待命点
     */
    public static final int TYPE_STANDBY_POINT = 3;
    /**
     * 机器人位置点
     */
    public static final int TYPE_ROBOT_POINT = 4;
    /**
     * 初始化定位给点
     */
    public static final int TYPE_INIT_LOCATION_POINT = 5;

    private Rect mRect = new Rect();
    private Rect mSrcRect = new Rect();
    private Rect mDstRect = new Rect();

    private PointF mTemp = new PointF();
    private Rect mRectTemp = new Rect();
    /**
     * 图片上的X坐标
     */
    private float mBitmapX;
    /**
     * 图片上的Y坐标
     */
    private float mBitmapY;
    /**
     * 标记点的名字
     */
    private String mPointName;
    /**
     * 是否需要显示标记点名字背景
     */
    private boolean mNeedShowName;

    /**
     * 名字是否可編輯
     */
    private boolean mNameEditable;

    /**
     * 名字的位置
     */
    private PointF mNameLocation = new PointF();
    private Bitmap mNameBitmap;
    private Rect mNameRect = new Rect();
    private Paint mNamePaint;
    private float mNameSize;
    private Rect mNameSrcRect = new Rect();
    private Rect mNameDstRect = new Rect();


    /**
     * 名字关闭图标
     */

    /**
     * 关闭图标的中心点
     */
    private float mNameClosePivotX, mNameClosePivotY;

    private PointF mNameCloseLocation = new PointF();
    private PointF mTempNameCloseLocation = new PointF();

    private Bitmap mNameCloseBitmap;
    private Rect mNameCloseRect = new Rect();
    private Rect mNameCloseRectTemp = new Rect();

    private float mNameCloseSize;
    private Rect mNameCloseSrcRect = new Rect();
    private Rect mNameCloseDstRect = new Rect();

    /**
     * 是否选中
     */
    private boolean isSelect;

    public PositionPointBean() {

    }

    public PositionPointBean(Bitmap bitmap, int type) {
        this.mBitmap = bitmap;
        this.mType = type;
    }

    public PositionPointBean(Bitmap bitmap, int type, float size,
                             float bitmapX, float bitmapY,
                             Bitmap nameBitmap,
                             float nameSize,
                             Paint namePaint,
                             String name,
                             Bitmap nameCloseBitmap,
                             float namCloseSize

    ) {

        //名字
        mNameBitmap = nameBitmap;
        mNameSize = nameSize;
        mNamePaint = namePaint;
        mPointName = name;
        mNameCloseBitmap = nameCloseBitmap;
        mNameCloseSize = namCloseSize;

        resetNameBounds(mNameRect);
        resetNameCloseBounds(mNameCloseRect);

        setLocation(bitmapX, bitmapY, true);
        resetBounds(mRect);

        setNameLocation();
        setNameCloseLocation();

        setPivotX(bitmapX);
        setPivotY(bitmapY);
        this.mBitmap = bitmap;
        this.mType = type;
        setSize(size);
    }


    public void init(float size, float bitmapX, float bitmapY, float nameSize, float nameCloseSize) {
        mNameSize = nameSize;
        mNameCloseSize = nameCloseSize;

        //名字
        resetNameBounds(mNameRect);

        //名字关闭图标
        resetNameCloseBounds(mNameCloseRect);

        mLocation = new PointF();
        setLocation(bitmapX, bitmapY, true);
        setPivotX(bitmapX);
        setPivotY(bitmapY);
        setSize(size);
        resetBounds(mRect);

        setNameLocation();
        setNameCloseLocation();
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    public float getSize() {
        return mSize;
    }

    public void setSize(float size) {
        float oldPivotX = getPivotX();
        float oldPivotY = getPivotY();

        mSize = size;
        resetBounds(mRect);

        setPivotX(mLocation.x + mRect.width() / 2);
        setPivotY(mLocation.y + mRect.height() / 2);
        setLocation(mLocation.x - (getPivotX() - oldPivotX), mLocation.y - (getPivotY() - oldPivotY), true);
    }

    public float getPivotX() {
        return mPivotX;
    }

    public void setPivotX(float pivotX) {
        mPivotX = pivotX;
    }

    public float getPivotY() {
        return mPivotY;
    }

    public void setPivotY(float pivotY) {
        mPivotY = pivotY;
    }

    public Rect getSrcRect() {
        return mSrcRect;
    }

    public void setSrcRect(Rect srcRect) {
        mSrcRect = srcRect;
    }

    public Rect getDstRect() {
        return mDstRect;
    }

    public void setDstRect(Rect dstRect) {
        mDstRect = dstRect;
    }

    public PointF getLocation() {
        return mLocation;
    }

    /**
     * @param x
     * @param y
     * @param changePivot 是否随着移动相应改变中心点的位置
     */
    public void setLocation(float x, float y, boolean changePivot) {
        float diffX = x - mLocation.x, diffY = y - mLocation.y;
        mLocation.x = x;
        mLocation.y = y;

        setNameLocation();
        setNameCloseLocation();

        if (changePivot) {
            mPivotX = mPivotX + diffX;
            mPivotY = mPivotY + diffY;
        }

    }


    public void resetBounds(Rect rect) {
        if (mBitmap == null) {
            return;
        }
        float size = getSize();

       // Log.e(TAG, "resetBounds：" + mBitmap.getHeight() + "," + mBitmap.getWidth() + "," + mBitmap.getHeight() * 1.0 / mBitmap.getWidth());

        rect.set(0, 0, (int) size, (int) (size * mBitmap.getHeight() / mBitmap.getWidth()));

        mSrcRect.set(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
        mDstRect.set(0, 0, (int) size, (int) (size * mBitmap.getHeight()) / mBitmap.getWidth());
    }

    private static final String TAG = "PositionPointBean";


    public void resetNameBounds(Rect rect) {
        if (mNameBitmap == null) {
            return;
        }
        Bitmap textBitmap = DrawUtil.drawTextToBitmap(mNameBitmap, mPointName, mNamePaint);

        rect.set(0, 0, (int) mNameSize, (int) (mNameSize * textBitmap.getHeight() / textBitmap.getWidth()));

        mNameSrcRect = new Rect();
        mNameSrcRect.set(0, 0, textBitmap.getWidth(), textBitmap.getHeight());
        mNameDstRect = new Rect();
        mNameDstRect.set(0, 0, (int) mNameSize, (int) (mNameSize * textBitmap.getHeight()) / textBitmap.getWidth());
    }


    public void resetNameCloseBounds(Rect rect) {

        if (mNameCloseBitmap == null) {
            return;
        }

        rect.set(0, 0, (int) mNameCloseSize, (int) (mNameCloseSize * mNameCloseBitmap.getHeight() / mNameCloseBitmap.getWidth()));

        mNameCloseSrcRect = new Rect();
        mNameCloseSrcRect.set(0, 0, mNameCloseBitmap.getWidth(), mNameCloseBitmap.getHeight());

        mNameCloseDstRect = new Rect();
        mNameCloseDstRect.set(0, 0, (int) mNameCloseSize, (int) (mNameCloseSize * mNameCloseBitmap.getHeight()) / mNameCloseBitmap.getWidth());

    }


    public Bitmap getNameTextBitmap() {
        Bitmap textBitmap = DrawUtil.drawTextToBitmap(mNameBitmap, mPointName, mNamePaint);
        return textBitmap;
    }

    public void drawNameBg(Canvas canvas) {

//        Rect mSrcRect = new Rect();
//        mSrcRect.set(0, 0, textBitmap.getWidth(), textBitmap.getHeight());
//        Rect mDstRect = new Rect();
//        mDstRect.set(0, 0, (int) nameSize, (int) (nameSize * textBitmap.getHeight()) / textBitmap.getWidth());
        canvas.drawBitmap(getNameTextBitmap(), mNameSrcRect, mNameDstRect, null);
//        canvas.drawBitmap(textBitmap, mRect.left - textBitmap.getWidth() / 5 + 20, mRect.top - textBitmap.getHeight(), null);

    }

    public void drawNameCloseBg(Canvas canvas) {
        canvas.drawBitmap(getNameCloseBitmap(), mNameCloseSrcRect, mNameCloseDstRect, null);
    }

    /**
     * 是否击中该标记点
     */
    public boolean contains(float x, float y) {
        resetBounds(mRect);
        PointF location = getLocation();
        // 把触摸点转换成在文字坐标系（即以文字起始点作为坐标原点）内的点
        x = x - location.x;
        y = y - location.y;
        // 把变换后相对于矩形的触摸点，还原回未变换前的点，然后判断是否矩形中
        mTemp = rotatePoint(mTemp, (int) -0, x, y, getPivotX() - getLocation().x, getPivotY() - getLocation().y);
        mRectTemp.set(mRect);
//        mRectTemp.left -= ITEM_PADDING * unit;
//        mRectTemp.top -= ITEM_PADDING * unit;
//        mRectTemp.right += ITEM_PADDING * unit;
//        mRectTemp.bottom += ITEM_PADDING * unit;
        mRectTemp.left -= 15;
        mRectTemp.top -= 15;
        mRectTemp.right += 15;
        mRectTemp.bottom += 15;
        return mRectTemp.contains((int) mTemp.x, (int) mTemp.y);

    }

    /**
     * 是否击中该标记点的名字关闭图标
     */
    public boolean containsNameClose(float x, float y) {

        resetNameCloseBounds(mNameCloseRect);

        PointF location = getNameCloseLocation();
        // 把触摸点转换成在文字坐标系（即以文字起始点作为坐标原点）内的点
        x = x - location.x;
        y = y - location.y;
        // 把变换后相对于矩形的触摸点，还原回未变换前的点，然后判断是否矩形中
        mTempNameCloseLocation = rotatePoint(mTempNameCloseLocation, (int) -0, x, y, getNameClosePivotX() - getNameCloseLocation().x, getNameClosePivotY() - getNameCloseLocation().y);
        mNameCloseRectTemp.set(mNameCloseRect);
//        mRectTemp.left -= ITEM_PADDING * unit;
//        mRectTemp.top -= ITEM_PADDING * unit;
//        mRectTemp.right += ITEM_PADDING * unit;
//        mRectTemp.bottom += ITEM_PADDING * unit;
        return mNameCloseRectTemp.contains((int) mTempNameCloseLocation.x, (int) mTempNameCloseLocation.y);

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


    public double getTheta() {
        return mTheta;
    }

    public void setTheta(double theta) {
        mTheta = theta;
    }

    public float getItemRotate() {
        return mItemRotate;
    }

    public void setItemRotate(float itemRotate) {
        mItemRotate = itemRotate;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
    }

    public float getBitmapX() {
        return mBitmapX;
    }

    public void setBitmapX(float bitmapX) {
        mBitmapX = bitmapX;
    }

    public float getBitmapY() {
        return mBitmapY;
    }

    public void setBitmapY(float bitmapY) {
        mBitmapY = bitmapY;
    }

    public String getPointName() {
        return mPointName;
    }

    public void setPointName(String pointName) {
        mPointName = pointName;
    }


    public boolean isNeedShowName() {
        return mNeedShowName;
    }

    public void setNeedShowName(boolean needShowName) {
        mNeedShowName = needShowName;
    }

    public Bitmap getBigBitmap() {
        return mBigBitmap;
    }

    public void setBigBitmap(Bitmap bigBitmap) {
        mBigBitmap = bigBitmap;
    }

    public boolean isNameEditable() {
        return mNameEditable;
    }

    public void setNameEditable(boolean nameEditable) {
        mNameEditable = nameEditable;
    }

    public PointF getNameLocation() {
        return mNameLocation;
    }

    public void setNameLocation(PointF nameLocation) {
        mNameLocation = nameLocation;
    }

    public Bitmap getNameBitmap() {
        return mNameBitmap;
    }

    public void setNameBitmap(Bitmap nameBitmap) {
        mNameBitmap = nameBitmap;
    }

    public Paint getNamePaint() {
        return mNamePaint;
    }

    public void setNamePaint(Paint namePaint) {
        mNamePaint = namePaint;
    }

    public Rect getNameRect() {
        return mNameRect;
    }

    public void setNameRect(Rect nameRect) {
        mNameRect = nameRect;
    }

    public float getNameSize() {
        return mNameSize;
    }

    public void setNameSize(float nameSize) {
        mNameSize = nameSize;
    }


    public void setNameLocation() {
        mNameLocation.x = mLocation.x - mNameRect.width() * 49f / 276;
        mNameLocation.y = mLocation.y - mNameRect.height();
    }

    public void setNameCloseLocation() {
        mNameCloseLocation.x = mLocation.x + mNameRect.width() - mNameRect.width() * 50f / 276 - mNameCloseRect.width() / 2;
        mNameCloseLocation.y = mLocation.y - mNameRect.height() - mNameCloseRect.height() / 2;

        //名字关闭图标中心点
        mNameClosePivotX = mNameCloseLocation.x + mNameCloseRect.width() / 2;
        mNameClosePivotY = mNameClosePivotY + mNameCloseRect.height() / 2;
    }


    public Bitmap getNameCloseBitmap() {
        return mNameCloseBitmap;
    }

    public void setNameCloseBitmap(Bitmap nameCloseBitmap) {
        mNameCloseBitmap = nameCloseBitmap;
    }

    public float getNameCloseSize() {
        return mNameCloseSize;
    }

    public void setNameCloseSize(float nameCloseSize) {
        mNameCloseSize = nameCloseSize;
    }

    public PointF getNameCloseLocation() {
        return mNameCloseLocation;
    }

    public void setNameCloseLocation(PointF nameCloseLocation) {
        mNameCloseLocation = nameCloseLocation;
    }

    public float getNameClosePivotX() {
        return mNameClosePivotX;
    }

    public void setNameClosePivotX(float nameClosePivotX) {
        mNameClosePivotX = nameClosePivotX;
    }

    public float getNameClosePivotY() {
        return mNameClosePivotY;
    }

    public void setNameClosePivotY(float nameClosePivotY) {
        mNameClosePivotY = nameClosePivotY;
    }

    @Override
    public String toString() {
        return "PositionPointBean{" +
                ", mTheta=" + mTheta +
                ", mItemRotate=" + mItemRotate +
                ", mBitmapX=" + mBitmapX +
                ", mBitmapY=" + mBitmapY +
                '}';
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}

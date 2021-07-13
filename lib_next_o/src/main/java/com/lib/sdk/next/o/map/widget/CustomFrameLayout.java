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
package com.lib.sdk.next.o.map.widget;


import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.lib.sdk.next.o.map.util.ScaleGestureDetectorApi27;

/**
 * Created by maqing on 2018/12/16.
 * Email:2856992713@qq.com
 */
public class CustomFrameLayout extends FrameLayout {
    /**
     * 旋转角度
     */
    private float oldAngle = 0;
    private float oldRotation = 0;
    private float rotate = 0;
    private OnRotationListener mOnRotationListener;

    private ScaleGestureDetectorApi27 mScaleGestureDetectorApi27;

    // 缩放相关
    private Float mLastFocusX;
    private Float mLastFocusY;
    private float mTouchCentreX, mTouchCentreY;

    private float pendingX, pendingY, pendingScale = 1;

    private OnTranslationListener mOnTranslationListener;
    private OnScaleListener mOnScaleListener;
    private static final String TAG = "CustomFrameLayout";

    public CustomFrameLayout(Context context) {
        this(context, null);
    }

    public CustomFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScaleGestureDetectorApi27 = new ScaleGestureDetectorApi27(context, new ScaleGestureDetectorApi27.OnScaleGestureListener() {

            @Override
            public boolean onScale(ScaleGestureDetectorApi27 detector, MotionEvent event) {
                Log.e(TAG, "onScale: ");

                //屏幕上的焦点
                mTouchCentreX = detector.getFocusX();
                mTouchCentreY = detector.getFocusY();

                if (mLastFocusX != null && mLastFocusY != null) {  //焦点改变
                    final float dx = mTouchCentreX - mLastFocusX;
                    final float dy = mTouchCentreY - mLastFocusY;
                    Log.d(TAG, "onScale mTouchCentreX,mTouchCentreY：" + mTouchCentreX + "," + mTouchCentreY);
                    Log.d(TAG, "onScale mLastFocusX,mLastFocusY：" + mLastFocusX + "," + mLastFocusY);
                    Log.d(TAG, "onScale：" + Math.abs(dx) + "," + Math.abs(dy) + ",");
                    //移动图片
                    if (Math.abs(dx) > 1 || Math.abs(dy) > 1) {
                        if (mOnTranslationListener != null) {
                            mOnTranslationListener.onTranslation(dx + pendingX, dy + pendingY);
                        }
                        pendingX = pendingY = 0;
                    } else {
                        pendingX += dx;
                        pendingY += dy;
                    }
                }

                if (Math.abs(1 - detector.getScaleFactor()) > 0.005f) {
                    //缩放图片
                    if (mOnScaleListener != null) {
                        mOnScaleListener.onScale(detector.getScaleFactor() * pendingScale, mTouchCentreX, mTouchCentreY);
                    }
                    pendingScale = 1;
                } else {
                    pendingScale *= detector.getScaleFactor();
                }

                mLastFocusX = mTouchCentreX;
                mLastFocusY = mTouchCentreY;

                return true;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetectorApi27 detector, MotionEvent event) {
                Log.e(TAG, "onScaleBegin: ");
                mLastFocusX = null;
                mLastFocusY = null;
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetectorApi27 detector, MotionEvent event) {

            }
        });

        // 下面两行绘画场景下应该设置间距为大于等于1，否则设为0双指缩放后抬起其中一个手指仍然可以移动
        mScaleGestureDetectorApi27.setMinSpan(1);// 手势前识别为缩放手势的双指滑动最小距离值
        mScaleGestureDetectorApi27.setSpanSlop(1); // 缩放过程中识别为缩放手势的双指最小距离值
    }

    private Matrix mTouchEventMatrix = new Matrix();

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        double delta_x;
        double delta_y;
        double radians;
        switch (e.getAction() & e.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                return false;
            case MotionEvent.ACTION_POINTER_DOWN:
                Log.d(TAG, "onInterceptTouchEvent：ACTION_POINTER_DOWN" + e.getPointerCount());
                delta_x = (e.getX(0) - e.getX(1));
                delta_y = (e.getY(0) - e.getY(1));
                radians = Math.atan2(delta_y, delta_x);
                oldAngle = (float) Math.toDegrees(radians);
                return true;
            default:
                if (e.getPointerCount() >= 2) {
                    return true;
                } else {
                    return false;
                }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        double delta_x;
        double delta_y;
        double radians;
        switch (e.getAction() & e.getActionMasked()) {
            case MotionEvent.ACTION_MOVE:
                Log.e(TAG, "onTouchEvent: ACTION_MOVE" + e);
                if (e.getPointerCount() >= 2) {
                    delta_x = (e.getX(0) - e.getX(1));
                    delta_y = (e.getY(0) - e.getY(1));
                    radians = Math.atan2(delta_y, delta_x);
                    float newAngle = (float) Math.toDegrees(radians);
                    float newRotation = newAngle - oldAngle + oldRotation;
                    rotate = newRotation;
                    oldRotation = newRotation;
                    oldAngle = newAngle;
                    if (mOnRotationListener != null) {
                        mOnRotationListener.onRotation(rotate);
                    }
                }
        }


        MotionEvent transformedEvent = MotionEvent.obtain(e);
        mTouchEventMatrix.reset();
        mTouchEventMatrix.setRotate(-rotate, getWidth() / 2, getHeight() / 2);
        transformedEvent.transform(mTouchEventMatrix);
        boolean ret = mScaleGestureDetectorApi27.onTouchEvent(transformedEvent);

        return ret;
    }

    /**
     * 初始化旋转
     */
    public void initRotate() {
        oldAngle = 0;
        oldRotation = 0;
        rotate = 0;
    }


    public OnRotationListener getOnRotationListener() {
        return mOnRotationListener;
    }

    public void setOnRotationListener(OnRotationListener onRotationListener) {
        mOnRotationListener = onRotationListener;
    }

    public OnTranslationListener getOnTranslationListener() {
        return mOnTranslationListener;
    }

    public void setOnTranslationListener(OnTranslationListener onTranslationListener) {
        mOnTranslationListener = onTranslationListener;
    }

    public OnScaleListener getOnScaleListener() {
        return mOnScaleListener;
    }

    public void setOnScaleListener(OnScaleListener onScaleListener) {
        mOnScaleListener = onScaleListener;
    }

    public interface OnRotationListener {
        void onRotation(float angle);
    }

    public interface OnTranslationListener {
        void onTranslation(float x, float y);
    }

    public interface OnScaleListener {
        void onScale(float scale, float x, float y);
    }

}

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

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;


import com.lib.sdk.next.o.R;
import com.lib.sdk.next.o.map.bean.PositionPointBean;
import com.lib.sdk.next.o.map.bean.RouteBean;
import com.lib.sdk.next.o.map.bean.VirtualWallBean;
import com.lib.sdk.next.o.map.listener.IMapOperateCallback;
import com.lib.sdk.next.o.map.util.AngleUtil;
import com.lib.sdk.next.o.map.util.DisplayUtil;
import com.lib.sdk.next.o.map.util.FileUtils;
import com.lib.sdk.next.o.map.util.ImageCache;
import com.lib.sdk.next.o.map.util.PositionUtil;
import com.lib.sdk.next.o.map.util.ScaleGestureDetectorApi27;
import com.lib.sdk.next.o.map.util.TouchGestureDetector;
import com.lib.sdk.next.o.map.util.VirtualWallUtil;
import com.lib.sdk.next.robot.bean.RobotLaserDataBean;
import com.lib.sdk.next.route.NavigationRouteUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Route;

/**
 * 地图
 * Created by maqing 2018/11/13 11:11
 * Email：2856992713@qq.com
 */
public class MapDrawView extends View {
    /**
     * 上下文对象
     */
    private Context mContext;
    /**
     * 画笔
     */
    private Paint mPaint;
    /**
     * 画笔颜色
     */
    private int mPaintColor = Color.RED;
    /**
     * 画笔粗细
     */
    private float mPainStrokeWidth = DEFAULT_SIZE;
    public final static int DEFAULT_SIZE = 6; // 默认画笔大小
    /**
     * 标记点大小
     */
    private float mPositionPointSize = DEFAULT_POSITION_POINT_SIZE;

    /**
     * 标记点大图大小
     */
    private float mInitPositionPointBigSize = DEFAULT_POSITION_POINT_BIG_SIZE;
    /**
     * 初始化比例
     */
    private float mInitScale;

    /**
     * 默认标记点大小
     */
    public static final float DEFAULT_POSITION_POINT_SIZE = 10;
    /**
     * 标记大点大小
     */
    public static final float DEFAULT_POSITION_POINT_BIG_SIZE = 88;
    /**
     * 名字大小
     */
    private float mNameBgSize = DEFAULT_NAME_BG_SIZE;
    /**
     * 名字默认大小
     */
    public static final float DEFAULT_NAME_BG_SIZE = 130;

    /**
     * 名字关闭图标大小
     */
    private float mNameCloseSize = DEFAULT_NAME_CLOSE_SIZE;
    /**
     * 默认名字关闭图标大小
     */
    public static final float DEFAULT_NAME_CLOSE_SIZE = 20;

    /**
     * 触摸手势监听器
     */
    private TouchGestureDetector mTouchGestureDetector;
    /**
     * 是否是缩放手势
     */
    private boolean mIsScale;

    /**
     * 虚拟墙路径列表
     */
    private List<VirtualWallBean> mVirtualWallList = new ArrayList<>();

    /**
     * 当前绘制的虚拟墙路径
     */
    private Path mCurrentPath;
    /**
     * 当前操作的虚拟墙
     */
    private VirtualWallBean mCurrentOperateWall;
    /**
     * 虚拟墙画笔
     */
    private Paint mVirtualWallPaint;
    private float mVirtualPaintStrokeSize = VIRTUAL_WALL_PAINT_STROKE_WIDTH;
    public static final float VIRTUAL_WALL_PAINT_STROKE_WIDTH = 1.5f;

    /**
     * 航路列表
     */
    private List<RouteBean> mRoutesList = new ArrayList<>();

    /**
     * 当前绘制的航路路径
     */
    private Path mCurrentRoutePath;

    /**
     * 当前操作的航路
     */
    private RouteBean mCurrentOperateRoute;

    /**
     * 航路画笔
     */
    private Paint mRoutePaint;
    private float mRouteStrokeSize = NAVIGATION_ROUTE_PAINT_STROKE_WIDTH;
    public static final float NAVIGATION_ROUTE_PAINT_STROKE_WIDTH = 5.0f;


    private Bitmap mBitmap;
    private int bitmapWidth;
    private int bitmapHeight;

    private float mCenterScale; // 图片适应屏幕时的缩放倍数
    private int mCenterHeight, mCenterWidth;// 图片适应屏幕时的大小（View窗口坐标系上的大小）
    private float mCentreTranX, mCentreTranY;// 图片在适应屏幕时，位于居中位置的偏移（View窗口坐标系上的偏移）

    private float mScale = 1; // 在适应屏幕时的缩放基础上的缩放倍数 （ 图片真实的缩放倍数为 mCenterScale*mScale ）
    private float mTransX = 0, mTransY = 0; // 图片在适应屏幕且处于居中位置的基础上的偏移量（ 图片真实偏移量为mCentreTranX + mTransX，View窗口坐标系上的偏移）
    private float mMinScale = MIN_SCALE; //最小缩放倍数
    private float mMaxScale = MAX_SCALE; //最大缩放倍数
    public final static float MAX_SCALE = 4f; // 最大缩放倍数
    public final static float MIN_SCALE = 0.25f; // 最小缩放倍数

    private float pendingX, pendingY, pendingScale = 1;

    // 触摸的相关信息
    private float mTouchX, mTouchY;
    private float mLastTouchX, mLastTouchY;
    private float mTouchDownX, mTouchDownY;

    // 缩放手势操作相关
    private Float mLastFocusX;
    private Float mLastFocusY;
    private float mTouchCentreX, mTouchCentreY;

    // 动画相关
    private ValueAnimator mScaleAnimator;
    private float mScaleAnimTransX, mScaleAnimTranY;
    private ValueAnimator mTranslateAnimator;
    private float mTransAnimOldY, mTransAnimY;


    /**
     * 点列表
     */
    private List<PositionPointBean> mPositionPointList = new ArrayList<>();
    /**
     * 选中的标记点
     */
    private PositionPointBean mSelectedPoint;

    private float mSelectedPointOriX, mSelectedPointOriY;

    /**
     * 选中的标记点原中心点坐标
     */
    private float mSelectedPointOriPivotX, mSelectedPointOriPivotY;

    /**
     * 当前操作点
     */
    private PositionPointBean mCurrentOperatePoint;

    private float mCurrentPointOriX, mCurrentPointOriY;
    /**
     * 是否点击在当前操作点上
     */
    private boolean mIsClickOnCurrentOperatePoint;

    /**
     * 选中的标记点原中心点坐标
     */
    private float mCurrentPointOriPivotX, mCurrentPointOriPivotY;

    private String mCurrentOperatePointName = "";

    /**
     * 当前操作点放大点
     */
    private PositionPointBean mCurrentOperatePointBig;

    private Bitmap mInitNameBGBitmap;
    private Bitmap mChargeNameBGBitmap;
    private Bitmap mNavigationNameBGBitmap;
    private Bitmap mStandbyNameBGBitmap;
    private Bitmap mVirtualWallNameBGBitmap;
    private Bitmap mRouteNameBGBitmap;
    private Bitmap mVirtualWallNameCloseBgBitmap;
    private Paint mNamePaint;
    public static final int NAME_PAINT_STROKE_WIDTH = 20;

    private float mDoodleSizeUnit = 1; // 长度单位，不同大小的图片的长度单位不一样。该单位的意义同dp的作用类似，独立于图片之外的单位长度

    private boolean mIsmipmapOutside = false; // 触摸时，图片区域外是否绘制涂鸦轨迹

    /**
     * 编辑类型 0：添加初始点
     */
    private int mEditType = TYPE_EDIT_INIT;
    /**
     * 航路类型 0：单向 1 为双向
     */
    private int mRouteType = -1;
    /**
     * 判断是否选择了选点初始化
     */
    private boolean isChoosenPointMode;
    /**
     * 不能编辑状态
     */
    public static final int TYPE_EDIT_INIT = -1;
    /**
     * 添加初始点
     */
    public static final int TYPE_EDIT_TYPE_ADD_INIT_POINT = 0;
    /**
     * 添加导航点
     */
    public static final int TYPE_EDIT_TYPE_ADD_NAVIGATION = 1;
    /**
     * 添加充电点
     */
    public static final int TYPE_EDIT_TYPE_ADD_CHARGE = 2;
    /**
     * 创建虚拟墙
     */
    public static final int TYPE_EDIT_TYPE_VIRTUAL_WALL = 3;
    /**
     * 创建航路
     */
    public static final int TYPE_EDIT_TYPE_NAVIGATION_ROUTE = 9;
    /**
     * 添加待命点
     */
    public static final int TYPE_EDIT_TYPE_ADD_STANDBY_POINT = 8;
    /**
     * 选择导航点作为路径规划
     */
    public static final int TYPE_EDIT_PATH_PLAN = 4;
    /**
     * 初始化定位
     */
    public static final int TYPE_EDIT_TYPE_INIT_LOCATION = 5;
    /**
     * 橡皮擦
     */
    public static final int TYPE_EDIT_ERASE = 6;
    /**
     * 编辑导航点
     */
    public static final int TYPE_EDIT_TYPE_EDIT_POINT = 7;
    /**
     * 橡皮擦画笔
     */
    private Paint mErasePaint;
    /**
     * 橡皮擦宽度(直径)
     */
    private int mErasePaintWidth;
    private int mErasePaintSize = DEFAULT_ERASE_PAINT_WIDTH;
    public static final int DEFAULT_ERASE_PAINT_WIDTH = 5;
    /**
     * 橡皮擦路径列表
     */
//    private List<ErasePathBean> mErasePathList = new ArrayList<>();
    /**
     * 当前的橡皮擦路径
     */
//    private ErasePathBean mCurrentErasePathBean;
    /**
     * 橡皮擦路径
     */
//    private Path mErasePath;
    /**
     * 橡皮擦点
     */
    private Point mErasePoint;

//    private Bitmap mEraseBitmap;

//    private Canvas mEraseCanvas;

    private boolean mCanUseErase = false;

    private Paint mEraseIconPaint;
    private Paint mEraseIconBgPaint;

    private int[] colors = new int[]{Color.parseColor("#00FFFFFF"), Color.WHITE};

    /**
     * 机器人位置
     */
    private PositionPointBean mRobotPositionBean;

    /**
     * 机器人激光点数据
     */
    private List<RobotLaserDataBean> mRobotLaserDataBeanList = new ArrayList<>();
    private Paint mLaserPaint;
    public static final int LASER_PAINT_STROKE_WIDTH = 3;
    /**
     * 标记点点击事件
     */
    private OnPositionPointClickListener mOnPositionPointClickListener;
    /**
     * 初始化定位完成
     */
    private OnInitLocationFinishListener mOnInitLocationFinishListener;
    /**
     * 虚拟墙点击事件
     */
    private OnVirtualWallClickListener mOnVirtualWallClickListener;
    /**
     * 航路点击事件
     */
    private OnRouteClickListener mOnRouteClickListener;
    /**
     * 编辑导航点完成事件
     */
    private OnEditNavigationPointFinishListener mOnEditNavigationPointFinishListener;
    /**
     * 绘制异常接口
     */
    private OnCanvasExceptionListener mOnCanvasExceptionListener;
    /**
     * 对地图进行了编辑操作
     */
    private OnEditedMapListener mOnEditedMapListener;

    /**
     * 自定义长按事件
     */
    private boolean mIsLongPress; //是否触发了长按

    /**
     * 是否移动了
     */
    private boolean mIsMoved;
    /**
     * 是否释放了
     */
    private boolean mIsReleased;
    /**
     * 计数器，防止多次点击导致最后一次形成longpress的时间变短
     */
    private int mCounter;

    private IOnDrawListener mOnDrawListener;

    private ITouchListener mTouchListener;

    /**
     * 长按的runnable
     */
    private Runnable mLongPressRunnable = new Runnable() {
        @Override
        public void run() {
            mCounter--;
            //计数器大于0，说明当前执行的Runnable不是最近一次down产生的。
            if (mCounter > 0 || mIsReleased || mIsMoved) return;
            Log.d(TAG, "长按了");

            if (mCurrentOperatePoint != null) {  //添加导航点时长按选中判断
                if (mCurrentOperatePoint.contains(toX(mTouchX), toY(mTouchY))) {

                    Log.d(TAG, "长按：" + getAllScale() + "," + mInitPositionPointBigSize);

                    float bigPointSize = mInitPositionPointBigSize * mInitScale / mScale;

                    mSelectedPoint = new PositionPointBean(mCurrentOperatePoint.getBigBitmap(), mCurrentOperatePoint.getType(), bigPointSize,
                            mCurrentOperatePoint.getBitmapX(), mCurrentOperatePoint.getBitmapY(),
                            mCurrentOperatePoint.getNameBitmap(),
                            mCurrentOperatePoint.getNameSize(),
                            mCurrentOperatePoint.getNamePaint(),
                            mCurrentOperatePoint.getPointName(),
                            mCurrentOperatePoint.getNameCloseBitmap(),
                            mCurrentOperatePoint.getNameCloseSize()
                    );

                    mSelectedPoint.setPointName(mCurrentOperatePoint.getPointName());
                    mSelectedPoint.setItemRotate(mCurrentOperatePoint.getItemRotate());
                    mSelectedPoint.setTheta(mCurrentOperatePoint.getTheta());

                    mSelectedPointOriX = mSelectedPoint.getLocation().x;
                    mSelectedPointOriY = mSelectedPoint.getLocation().y;
                    mSelectedPointOriPivotX = mSelectedPoint.getPivotX();
                    mSelectedPointOriPivotY = mSelectedPoint.getPivotY();

                    mIsLongPress = true;
                    invalidate();
                }
            } else {

                int mType = -1;

                switch (mEditType) {
                    case TYPE_EDIT_TYPE_ADD_INIT_POINT:
                        mType = PositionPointBean.TYPE_INIT_POINT;
                        break;
                    case TYPE_EDIT_TYPE_ADD_CHARGE:
                        mType = PositionPointBean.TYPE_CHARGE_POINT;
                        break;
                    case TYPE_EDIT_TYPE_ADD_STANDBY_POINT:
                        mType = PositionPointBean.TYPE_STANDBY_POINT;
                        break;
                }

                for (int i = 0; i < mPositionPointList.size(); i++) {
                    PositionPointBean navigationPointBean = mPositionPointList.get(i);
                    if (navigationPointBean.contains(toX(mTouchX), toY(mTouchY))) {
                        if (navigationPointBean.getType() == mType) {

                            float bigPointSize = mInitPositionPointBigSize * mInitScale / mScale;

                            mSelectedPoint = new PositionPointBean(mCurrentOperatePoint.getBigBitmap(), mCurrentOperatePoint.getType(), bigPointSize,
                                    mCurrentOperatePoint.getBitmapX(), mCurrentOperatePoint.getBitmapY(),
                                    mCurrentOperatePoint.getNameBitmap(),
                                    mCurrentOperatePoint.getNameSize(),
                                    mCurrentOperatePoint.getNamePaint(),
                                    mCurrentOperatePoint.getPointName(),
                                    mCurrentOperatePoint.getNameCloseBitmap(),
                                    mCurrentOperatePoint.getNameCloseSize()
                            );
                            mSelectedPoint.setPointName(mCurrentOperatePoint.getPointName());
                            mSelectedPoint.setItemRotate(mCurrentOperatePoint.getItemRotate());
                            mSelectedPoint.setTheta(mCurrentOperatePoint.getTheta());

                            mSelectedPointOriX = mSelectedPoint.getLocation().x;
                            mSelectedPointOriY = mSelectedPoint.getLocation().y;
                            mSelectedPointOriPivotX = mSelectedPoint.getPivotX();
                            mSelectedPointOriPivotY = mSelectedPoint.getPivotY();

                            mIsLongPress = true;
                            invalidate();
                            break;
                        }
                    }
                }
            }
        }
    };

    private MotionEvent mDownEvent;

    private int mOnePointCounter;
    /**
     * 是否有另外一只手指按下
     */
    private boolean mOtherPointDown;

    /**
     * 1根手指按住检测(用于区别不是快速的两只手指按住操作)
     */
    private Runnable mOnePointDownRunnable = new Runnable() {

        @Override
        public void run() {
            Log.e("pointInfo", "mOnePointDownRunnable() 进入run方法");
            mOnePointCounter--;
            //计数器大于0，说明当前执行的Runnable不是最后一次down产生的。
//            if (mOnePointCounter > 0 || mIsReleased || mIsMoved || mOtherPointDown) return;
            if (mOnePointCounter > 0 || mIsReleased || mOtherPointDown) {
                return;
            }

            if (mEditType != TYPE_EDIT_INIT) {
                if (mOnEditedMapListener != null) {
                    mOnEditedMapListener.onEditedMap();
                    if (mOnEditedMapListener.onEditedMapIntercept()) {
                        return;
                    }
                }
            }
            Log.e("pointInfo", "mEditType = " + mEditType);
            switch (mEditType) {

                case TYPE_EDIT_TYPE_NAVIGATION_ROUTE://添加航路
                    mCounter++;
                    mIsReleased = false;
                    mIsMoved = false;
                    createNewRoute();
                    invalidate();
                    break;

                case TYPE_EDIT_TYPE_ADD_INIT_POINT: //添加初始点

                    mCounter++;
                    mIsReleased = false;
                    mIsMoved = false;
                    postDelayed(mLongPressRunnable, ViewConfiguration.getLongPressTimeout());

                    createNewInitPoint();

                    invalidate();
                    break;
                case TYPE_EDIT_TYPE_ADD_STANDBY_POINT:
                    mCounter++;
                    mIsReleased = false;
                    mIsMoved = false;
                    postDelayed(mLongPressRunnable, ViewConfiguration.getLongPressTimeout());

                    createNewStandbyPoint();

                    invalidate();
                    break;
                case TYPE_EDIT_TYPE_ADD_CHARGE:
                    createNewChargePoint();

                    mCounter++;
                    mIsReleased = false;
                    mIsMoved = false;
                    postDelayed(mLongPressRunnable, ViewConfiguration.getLongPressTimeout());

                    invalidate();

                    break;
                case TYPE_EDIT_TYPE_INIT_LOCATION:
                    //初始化定位点
                    Bitmap pointBitmap = getIconBitmap(R.mipmap.init_location_point);
                    Bitmap nameCloseBitmap = getIconBitmap(R.mipmap.name_close);

                    float bigPointSize = (DEFAULT_POSITION_POINT_BIG_SIZE * mDoodleSizeUnit) * mInitScale / mScale;

                    if (isChoosenPointMode) {
                        for (int i = 0; i < mPositionPointList.size(); i++) {
                            PositionPointBean navigationPointBean = mPositionPointList.get(i);
                            if (navigationPointBean.contains(toX(mTouchX), toY(mTouchY))) {
                                if (mCurrentOperatePoint == null) {
                                    Log.e("pointInfo", "mCurrentOperatePoint == null");
                                    mCurrentOperatePoint = new PositionPointBean(pointBitmap, PositionPointBean.TYPE_INIT_LOCATION_POINT, bigPointSize, toX(mTouchX), toY(mTouchY),
                                            null,
                                            mNameBgSize,
                                            mNamePaint,
                                            navigationPointBean.getPointName(),
                                            nameCloseBitmap,
                                            mNameCloseSize
                                    );
                                    mCurrentOperatePoint.setBigBitmap(getIconBitmap(R.mipmap.init_location_point_big));
                                    mCurrentOperatePoint.setPointName(navigationPointBean.getPointName());
                                    mCurrentOperatePoint.setBitmapX(toX(mTouchX));
                                    mCurrentOperatePoint.setBitmapY(toY(mTouchY));
                                    mCurrentOperatePoint.setTheta(navigationPointBean.getTheta());
                                    mCurrentOperatePoint.setItemRotate(navigationPointBean.getItemRotate());

                                }
                            }
                        }
                    } else {
                        mCurrentOperatePoint = new PositionPointBean(pointBitmap, PositionPointBean.TYPE_INIT_LOCATION_POINT, bigPointSize, toX(mTouchX), toY(mTouchY),
                                null,
                                mNameBgSize,
                                mNamePaint,
                                mCurrentOperatePointName,
                                nameCloseBitmap,
                                mNameCloseSize
                        );
                        mCurrentOperatePoint.setBigBitmap(getIconBitmap(R.mipmap.init_location_point_big));
                        mCurrentOperatePoint.setPointName(mCurrentOperatePointName);
                        mCurrentOperatePoint.setBitmapX(toX(mTouchX));
                        mCurrentOperatePoint.setBitmapY(toY(mTouchY));

                        mSelectedPoint = new PositionPointBean(mCurrentOperatePoint.getBigBitmap(), mCurrentOperatePoint.getType(), bigPointSize,
                                mCurrentOperatePoint.getBitmapX(), mCurrentOperatePoint.getBitmapY(),
                                null,
                                mNameBgSize,
                                mNamePaint,
                                mCurrentOperatePointName,
                                mCurrentOperatePoint.getNameCloseBitmap(),
                                mNameCloseSize
                        );
                        mSelectedPoint.setPointName(mCurrentOperatePoint.getPointName());
                        mSelectedPoint.setItemRotate(mCurrentOperatePoint.getItemRotate());
                        mSelectedPoint.setTheta(mCurrentOperatePoint.getTheta());
                        mSelectedPointOriX = mSelectedPoint.getLocation().x;
                        mSelectedPointOriY = mSelectedPoint.getLocation().y;
                        mSelectedPointOriPivotX = mSelectedPoint.getPivotX();
                        mSelectedPointOriPivotY = mSelectedPoint.getPivotY();
                    }
                    mIsClickOnCurrentOperatePoint = true;
                    invalidate();
                    break;

                case TYPE_EDIT_TYPE_ADD_NAVIGATION:  //添加导航点状态
                    createNewNavigationPoint();

                    //长按监听
                    mCounter++;
                    mIsReleased = false;
                    mIsMoved = false;
                    postDelayed(mLongPressRunnable, ViewConfiguration.getLongPressTimeout());

                    invalidate();

                    break;

                case TYPE_EDIT_TYPE_EDIT_POINT:  //编辑导航点状态

                    if (mCurrentOperatePoint != null && mCurrentOperatePoint.contains(toX(mTouchX), toY(mTouchY))) { //点在了选中的导航点上
                        mIsClickOnCurrentOperatePoint = true;
//                        mCurrentOperatePoint.setNeedShowName(!mCurrentOperatePoint.isNeedShowName());
                    }

                    mCounter++;
                    mIsReleased = false;
                    mIsMoved = false;
                    postDelayed(mLongPressRunnable, ViewConfiguration.getLongPressTimeout());
                    break;
                case TYPE_EDIT_ERASE:
                    if (mCanUseErase) {
                        mErasePoint = new Point((int) toX(mTouchX), (int) toY(mTouchY));
                        invalidate();
                    }
                    break;
            }

        }

    };

    private void createNewStandbyPoint() {
        if (mCurrentOperatePoint == null) { //说明没有添加待命点

            Bitmap pointBitmap = getIconBitmap(R.mipmap.standby_point);
            Bitmap nameCloseBitmap = getIconBitmap(R.mipmap.name_close);
            if (toX(mTouchX) > 0 &&
                    toY(mTouchY) > 0) {

                mCurrentOperatePoint = new PositionPointBean(pointBitmap, PositionPointBean.TYPE_STANDBY_POINT, mPositionPointSize, toX(mTouchX), toY(mTouchY),
                        mStandbyNameBGBitmap,
                        mNameBgSize,
                        mNamePaint,
                        mCurrentOperatePointName,
                        nameCloseBitmap,
                        mNameCloseSize
                );
                mCurrentOperatePoint.setPointName(mCurrentOperatePointName);
                mCurrentOperatePoint.setBigBitmap(getIconBitmap(R.mipmap.standby_point_big));
                mCurrentOperatePoint.setBitmapX(toX(mTouchX));
                mCurrentOperatePoint.setBitmapY(toY(mTouchY));
                if (localTheta != 0) {
                    mCurrentOperatePoint.setTheta(localTheta);
                    mCurrentOperatePoint.setItemRotate((float) Math.toDegrees(localTheta));
                }
                localTheta = 0;

                mCurrentPointOriX = mCurrentOperatePoint.getLocation().x;
                mCurrentPointOriY = mCurrentOperatePoint.getLocation().y;
                mCurrentPointOriPivotX = mCurrentOperatePoint.getPivotX();
                mCurrentPointOriPivotY = mCurrentOperatePoint.getPivotY();
            }
        } else {
            if (mCurrentOperatePoint.contains(toX(mTouchX), toY(mTouchY))) {  //点在了待命点上
                mIsClickOnCurrentOperatePoint = true;
            }
        }
    }


    private Bitmap getIconBitmap(int resId) {
        String key = String.valueOf(resId);
        if (ImageCache.getInstance().getBitmapFromMemCache(key) == null) {
            ImageCache.getInstance().addBitmapToMemoryCache(key,
                    BitmapFactory.decodeResource(getResources(), resId).copy(Bitmap.Config.ARGB_8888, true)
            );
        }

        return ImageCache.getInstance().getBitmapFromMemCache(key);
    }


    /**
     * 創建航路
     * <p>
     * 创建航路，
     * 是否为导航点
     * </p>
     */
    private void createNewRoute() {

        for (int i = 0; i < mPositionPointList.size(); i++) {
            PositionPointBean navigationPointBean = mPositionPointList.get(i);
            if (navigationPointBean.contains(toX(mTouchX), toY(mTouchY)) && navigationPointBean.getType() == PositionPointBean.TYPE_NAVIGATION_POINT) {
                //点击了导航点
                Log.w("createNewRoute", "航路点到了导航点");
                if (mCurrentOperateRoute == null) {
                    mCurrentOperateRoute = new RouteBean();
                    if (mCurrentOperateRoute.getPositions() == null) {
                        mCurrentOperateRoute.setPositions(new ArrayList<RouteBean.PositionsDTO>());
                    }
                }

                if (mCurrentOperateRoute.getPositions().size() < 2) {
                    setRouteXY(navigationPointBean);
                }
                //俩个点都有
                else {
                    mCurrentOperateRoute.getPositions().clear();
                    setRouteXY(navigationPointBean);
                }
                //做检查是否是通过一个点
                if (mCurrentOperateRoute.getPositions().size() >= 2
                        && mCurrentOperateRoute.getPositions().get(0).getWorld_x().equals(mCurrentOperateRoute.getPositions().get(1).getWorld_x())) {
                    mCurrentOperateRoute.getPositions().clear();
                    setRouteXY(navigationPointBean);
                }
                break;
            }
        }
        Log.e("createNewRoute", "mRouteType = " + mRouteType);
        if (mRouteType == 0) {
            Log.e("createNewRoute", "单向航路");
            mCurrentOperateRoute.setDirection(0);
        } else if (mRouteType == 1) {
            Log.e("createNewRoute", "双向航路");
            mCurrentOperateRoute.setDirection(1);
        }
        if (mCurrentOperateRoute.getPositions().size() == 2) {
            setRouteArrawParams();
        } else {
            mCurrentOperateRoute.setmPath(null);
        }


    }

    /**
     * 给RouteBean添加箭头绘画参数
     */
    private void setRouteArrawParams() {
        RouteBean.PositionsDTO startPosition = mCurrentOperateRoute.getPositions().get(0);
        RouteBean.PositionsDTO endPosition = mCurrentOperateRoute.getPositions().get(1);
        //选了结尾点 isFirstChoosenPoint == false
        Path path = new Path();
        path.moveTo((float) startPosition.getWorld_x().doubleValue(), (float) startPosition.getWorld_y().doubleValue());//开始点
        path.lineTo((float) endPosition.getWorld_x().doubleValue(), (float) endPosition.getWorld_y().doubleValue());//结束点
        mCurrentOperateRoute.setmPath(path);
        //单箭头
        if (mCurrentOperateRoute.getDirection() == 0) {
            mCurrentOperateRoute.setmFrontPath(setArrowPath(path, (float) startPosition.getWorld_x().doubleValue(), (float) startPosition.getWorld_y().doubleValue()
                    , (float) endPosition.getWorld_x().doubleValue(), (float) endPosition.getWorld_y().doubleValue()));
        }
        //双箭头
        else if (mCurrentOperateRoute.getDirection() == 1) {
            mCurrentOperateRoute.setmBackPath(setArrowPath(path, (float) endPosition.getWorld_x().doubleValue(), (float) endPosition.getWorld_y().doubleValue()
                    , (float) startPosition.getWorld_x().doubleValue(), (float) startPosition.getWorld_y().doubleValue()
            ));
            mCurrentOperateRoute.setmFrontPath(setArrowPath(path, (float) startPosition.getWorld_x().doubleValue(), (float) startPosition.getWorld_y().doubleValue()
                    , (float) endPosition.getWorld_x().doubleValue(), (float) endPosition.getWorld_y().doubleValue()));
        }
    }

    /**
     * 设置航路点坐标
     * @param navigationPointBean
     */
    private void setRouteXY(PositionPointBean navigationPointBean) {
        RouteBean.PositionsDTO positionsDTO = new RouteBean.PositionsDTO();
        positionsDTO.setWorld_x((double) navigationPointBean.getPivotX());
        positionsDTO.setWorld_y((double) navigationPointBean.getPivotY());
        positionsDTO.setTheta(navigationPointBean.getTheta());
        mCurrentOperateRoute.getPositions().add(positionsDTO);
    }

    /**
     * 创建新的初始点
     */
    private void createNewInitPoint() {

        if (mCurrentOperatePoint == null) { //说明没有添加初始点

            Bitmap pointBitmap = getIconBitmap(R.mipmap.initial_point);
            Bitmap nameCloseBitmap = getIconBitmap(R.mipmap.name_close);
            if (toX(mTouchX) > 0 &&
                    toY(mTouchY) > 0) {

                mCurrentOperatePoint = new PositionPointBean(pointBitmap, PositionPointBean.TYPE_INIT_POINT, mPositionPointSize, toX(mTouchX), toY(mTouchY),
                        mInitNameBGBitmap,
                        mNameBgSize,
                        mNamePaint,
                        mCurrentOperatePointName,
                        nameCloseBitmap,
                        mNameCloseSize
                );
                mCurrentOperatePoint.setPointName(mCurrentOperatePointName);
                mCurrentOperatePoint.setBigBitmap(getIconBitmap(R.mipmap.init_point_big));
                mCurrentOperatePoint.setBitmapX(toX(mTouchX));
                mCurrentOperatePoint.setBitmapY(toY(mTouchY));
                if (localTheta != 0) {
                    mCurrentOperatePoint.setTheta(localTheta);
                    mCurrentOperatePoint.setItemRotate((float) Math.toDegrees(localTheta));
                }
                localTheta = 0;

                mCurrentPointOriX = mCurrentOperatePoint.getLocation().x;
                mCurrentPointOriY = mCurrentOperatePoint.getLocation().y;
                mCurrentPointOriPivotX = mCurrentOperatePoint.getPivotX();
                mCurrentPointOriPivotY = mCurrentOperatePoint.getPivotY();
            }
        } else {
            if (mCurrentOperatePoint.contains(toX(mTouchX), toY(mTouchY))) {  //点在了初始点上
                mIsClickOnCurrentOperatePoint = true;
//                            mCurrentOperatePoint.setNeedShowName(!mCurrentOperatePoint.isNeedShowName());
            }
        }
    }

    /**
     * 添加新的充点电
     */
    private void createNewChargePoint() {
        if (mCurrentOperatePoint == null) { //说明没有添加充电点

            Bitmap pointBitmap = getIconBitmap(R.mipmap.charge_point);
            Bitmap nameCloseBitmap = getIconBitmap(R.mipmap.name_close);

            if (toX(mTouchX) > 0 &&
                    toY(mTouchY) > 0) {

                mCurrentOperatePoint = new PositionPointBean(pointBitmap, PositionPointBean.TYPE_CHARGE_POINT, mPositionPointSize, toX(mTouchX), toY(mTouchY),
                        mChargeNameBGBitmap,
                        mNameBgSize,
                        mNamePaint,
                        mCurrentOperatePointName,
                        nameCloseBitmap,
                        mNameCloseSize
                );
                mCurrentOperatePoint.setPointName(mCurrentOperatePointName);
                mCurrentOperatePoint.setBigBitmap(getIconBitmap(R.mipmap.charge_point_big));
                mCurrentOperatePoint.setBitmapX(toX(mTouchX));
                mCurrentOperatePoint.setBitmapY(toY(mTouchY));
                if (localTheta != 0) {
                    mCurrentOperatePoint.setTheta(localTheta);
                    mCurrentOperatePoint.setItemRotate((float) Math.toDegrees(localTheta));
                }
                localTheta = 0;

                mCurrentPointOriX = mCurrentOperatePoint.getLocation().x;
                mCurrentPointOriY = mCurrentOperatePoint.getLocation().y;
                mCurrentPointOriPivotX = mCurrentOperatePoint.getPivotX();
                mCurrentPointOriPivotY = mCurrentOperatePoint.getPivotY();
            }
        } else {
            if (mCurrentOperatePoint.contains(toX(mTouchX), toY(mTouchY))) {  //点在了充电点上
                mIsClickOnCurrentOperatePoint = true;
//                            mCurrentOperatePoint.setNeedShowName(!mCurrentOperatePoint.isNeedShowName());
            }
        }
    }

    /**
     * 创建新的导航点
     */
    private void createNewNavigationPoint() {
        Bitmap pointBitmap;
        Bitmap nameCloseBitmap;
        if (mCurrentOperatePoint == null) { //说明没有添加导航点
            //添加导航点
            pointBitmap = getIconBitmap(R.mipmap.navigation_point);
            nameCloseBitmap = getIconBitmap(R.mipmap.name_close);

            if (toX(mTouchX) > 0 && toY(mTouchY) > 0) {
                mCurrentOperatePoint = new PositionPointBean(pointBitmap, PositionPointBean.TYPE_NAVIGATION_POINT, mPositionPointSize, toX(mTouchX), toY(mTouchY),
                        mNavigationNameBGBitmap,
                        mNameBgSize,
                        mNamePaint,
                        mCurrentOperatePointName,
                        nameCloseBitmap,
                        mNameCloseSize
                );
                mCurrentOperatePoint.setPointName(mCurrentOperatePointName);
                mCurrentOperatePoint.setBigBitmap(getIconBitmap(R.mipmap.navigation_point_big));
                mCurrentOperatePoint.setBitmapX(toX(mTouchX));
                mCurrentOperatePoint.setBitmapY(toY(mTouchY));
                if (localTheta != 0) {
                    mCurrentOperatePoint.setTheta(localTheta);
                    mCurrentOperatePoint.setItemRotate((float) Math.toDegrees(localTheta));
                }
                localTheta = 0;

                mCurrentPointOriX = mCurrentOperatePoint.getLocation().x;
                mCurrentPointOriY = mCurrentOperatePoint.getLocation().y;
                mCurrentPointOriPivotX = mCurrentOperatePoint.getPivotX();
                mCurrentPointOriPivotY = mCurrentOperatePoint.getPivotY();
            }
        } else {

            if (mCurrentOperatePoint != null && mCurrentOperatePoint.contains(toX(mTouchX), toY(mTouchY))) { //点在了新建的导航点上
                mIsClickOnCurrentOperatePoint = true;
//                            mCurrentOperatePoint.setNeedShowName(!mCurrentOperatePoint.isNeedShowName());
            } else { //没有点在新建的导航点上
                mCurrentOperatePoint = null;
                createNewNavigationPoint();
            }

        }
    }

    /**
     * 旋转角度
     */
    private float mRotate = 0;

    private int originScale = 1;
    private FileUtils.OriginSize originSize = new FileUtils.OriginSize();

    private Rect mRect;
    private BitmapFactory.Options mOptions;
    private BitmapRegionDecoder mDecoder;
    private boolean enableBitmapRegion = false;

    private boolean enableBitmapChache = true;

    private int touchSlop;

    private static final String TAG = "MapDrawView";

    public MapDrawView(Context context) {
        this(context, null);
    }

    public MapDrawView(final Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        mContext = context;
        iniPaint();
        initNameBgBitmap();
        initTouchGesture();
        initBitmapRegion();
    }

    private void initBitmapRegion() {
        // 内存复用
        mOptions = new BitmapFactory.Options();
    }


    private void initNameBgBitmap() {
        mInitNameBGBitmap = getIconBitmap(R.mipmap.init_point_bg);
        mChargeNameBGBitmap = getIconBitmap(R.mipmap.charge_point_bg);
        mNavigationNameBGBitmap = getIconBitmap(R.mipmap.navigation_point_bg);
        mVirtualWallNameBGBitmap = getIconBitmap(R.mipmap.virtual_wall_bg);
        mRouteNameBGBitmap = getIconBitmap(R.mipmap.route_bg);
        mStandbyNameBGBitmap = getIconBitmap(R.mipmap.standby_point_bg);
        mVirtualWallNameCloseBgBitmap = getIconBitmap(R.mipmap.name_close);
    }

    private void iniPaint() {
        touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

        mPaint = new Paint();
        mPaint.setColor(mPaintColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mPainStrokeWidth);
        mPaint.setAntiAlias(true);

        mErasePaint = new Paint();
        mErasePaint.setColor(Color.WHITE);
        mErasePaint.setStyle(Paint.Style.STROKE);
        mErasePaint.setStrokeWidth(mErasePaintWidth);
        mErasePaint.setStrokeCap(Paint.Cap.ROUND);
        mErasePaint.setAntiAlias(true);

        mEraseIconBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mEraseIconPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mEraseIconPaint.setColor(Color.parseColor("#9B9B9B"));
        mEraseIconPaint.setStyle(Paint.Style.STROKE);

        mLaserPaint = new Paint();
        mLaserPaint.setColor(ContextCompat.getColor(mContext, R.color.colorRed));
        mLaserPaint.setStrokeWidth(LASER_PAINT_STROKE_WIDTH);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mLaserPaint.setAntiAlias(true);

        mVirtualWallPaint = new Paint();
        mVirtualWallPaint.setColor(ContextCompat.getColor(mContext, R.color.colorBlack));
        mVirtualWallPaint.setStrokeWidth(mVirtualPaintStrokeSize);
        mVirtualWallPaint.setStyle(Paint.Style.STROKE);
        mVirtualWallPaint.setAntiAlias(true);

        mRoutePaint = new Paint();
        mRoutePaint.setColor(ContextCompat.getColor(mContext, R.color.log_warn));
        mRoutePaint.setStrokeWidth(mRouteStrokeSize);
        mRoutePaint.setStyle(Paint.Style.STROKE);
        mRoutePaint.setAntiAlias(true);

        mNamePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mNamePaint.setColor(Color.WHITE);
        mNamePaint.setTextSize(NAME_PAINT_STROKE_WIDTH);
    }


    private void initTouchGesture() {
        mTouchGestureDetector = new TouchGestureDetector(mContext, new TouchGestureDetector.OnTouchGestureListener() {
            @Override
            public boolean onScaleBegin(ScaleGestureDetectorApi27 detector, MotionEvent event) {

//                Log.d(TAG, "onScaleBegin");
                mIsScale = true;
                switch (mEditType) {
                    case TYPE_EDIT_TYPE_INIT_LOCATION:
                        mCurrentOperatePoint = null;
                        mSelectedPoint = null;
                        invalidate();
                        break;
                }
                mLastFocusX = null;
                mLastFocusY = null;
                return true;
            }

            @Override
            public boolean onScale(ScaleGestureDetectorApi27 detector, MotionEvent event) { /* 屏幕上的焦点*/
//                Log.d(TAG, "onScale");
                mTouchCentreX = detector.getFocusX();
                mTouchCentreY = detector.getFocusY();
                if (mLastFocusX != null && mLastFocusY != null) { /* 焦点改变*/
                    float dx = mTouchCentreX - mLastFocusX;
                    float dy = mTouchCentreY - mLastFocusY;  /*移动图片*/
                    if (Math.abs(dx) > 1 || Math.abs(dy) > 1) {
                        setTranslationX(mTransX + dx + pendingX);
                        setTranslationY(mTransY + dy + pendingY);
                        pendingX = pendingY = 0;
                    } else {
                        pendingX += dx;
                        pendingY += dy;
                    }
                }
                /* 缩放图片*/
                if (Math.abs(1 - detector.getScaleFactor()) > 0.005f) { /* 缩放图片*/
                    float scale = mScale * detector.getScaleFactor() * pendingScale;
                    setScale(scale, toX(mTouchCentreX), toY(mTouchCentreY));
                    pendingScale = 1;
                } else{
                    pendingScale *= detector.getScaleFactor();
                }

                mLastFocusX = mTouchCentreX;
                mLastFocusY = mTouchCentreY;

                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetectorApi27 detector, MotionEvent e) {
                super.onScaleEnd(detector, e);
//                Log.d(TAG, "onScaleEnd");
            }

            @Override
            public void onScrollBegin(MotionEvent e) {
                super.onScrollBegin(e);
                if (toX(e.getX()) > 0 && toX(e.getX()) < bitmapWidth
                        &&
                        toY(e.getY()) > 0 && toY(e.getY()) < bitmapHeight
                ) {  //在屏幕范围内有效
                    mLastTouchX = mTouchX = e.getX();
                    mLastTouchY = mTouchY = e.getY();
                    switch (mEditType) {
                        case TYPE_EDIT_TYPE_VIRTUAL_WALL:
                            mCurrentPath = new Path();
                            mCurrentPath.moveTo(toX(mTouchX), toY(mTouchY));
                            mCurrentOperateWall = new VirtualWallBean();
                            mCurrentOperateWall.setPath(mCurrentPath);
                            mCurrentOperateWall.getWallPointList().add(new VirtualWallBean.WallPoint(toX(mTouchX), toY(mTouchY)));
                            invalidate();
                            break;
//                        case TYPE_EDIT_ERASE:
//                            if (mCanUseErase) {
//                                mErasePath = new Path();
//                                mCurrentErasePathBean = new ErasePathBean();
//                                mErasePath.moveTo(toX(mTouchX), toY(mTouchY));
//                                mCurrentErasePathBean.setPath(mErasePath);
//                                mCurrentErasePathBean.setPaintStrokeWidth(mErasePaintWidth);
//                                mErasePathList.add(mCurrentErasePathBean);
//                            }
//                            break;
                    }
                    mLastFocusX = null;
                    mLastFocusY = null;
                }
                if(mTouchListener!=null){
                    mTouchListener.onScrollBegin(e,toX(mTouchX),toY(mTouchY));
                }
            }

            @Override
            public void onScrollEnd(MotionEvent e) {
                super.onScrollEnd(e);
//                Log.d(TAG, "onScrollEnd");
                mIsMoved = false;//取消滚动标志

//                if (toX(e.getX()) > 0 && toX(e.getX()) < mBitmap.getWidth()
//                        &&
//                        toY(e.getY()) > 0 && toY(e.getY()) < mBitmap.getHeight()
//                        ) { //在屏幕范围内有效

                mLastTouchX = mTouchX;
                mLastTouchY = mTouchY;
                mTouchX = e.getX();
                mTouchY = e.getY();

                switch (mEditType) {
                    case TYPE_EDIT_TYPE_ADD_STANDBY_POINT:
                    case TYPE_EDIT_TYPE_ADD_INIT_POINT:
                        if (mCurrentOperatePoint != null && !mIsLongPress) { //不是长按，则是拖动，拖动结束时需改变保存的操作点的位置和中点的值
                            mCurrentPointOriX = mCurrentOperatePoint.getLocation().x;
                            mCurrentPointOriY = mCurrentOperatePoint.getLocation().y;
                            mCurrentPointOriPivotX = mCurrentOperatePoint.getPivotX();
                            mCurrentPointOriPivotY = mCurrentOperatePoint.getPivotY();
                        }
                        break;

                    case TYPE_EDIT_TYPE_ADD_CHARGE:
                        if (mCurrentOperatePoint != null && !mIsLongPress) { //不是长按，则是拖动，拖动结束时需改变保存的操作点的位置和中点的值
                            mCurrentPointOriX = mCurrentOperatePoint.getLocation().x;
                            mCurrentPointOriY = mCurrentOperatePoint.getLocation().y;
                            mCurrentPointOriPivotX = mCurrentOperatePoint.getPivotX();
                            mCurrentPointOriPivotY = mCurrentOperatePoint.getPivotY();
                        }
                        break;

                    case TYPE_EDIT_TYPE_ADD_NAVIGATION:
                        if (mCurrentOperatePoint != null && !mIsLongPress) { //不是长按，则是拖动，拖动结束时需改变保存的操作点的位置和中点的值
                            mCurrentPointOriX = mCurrentOperatePoint.getLocation().x;
                            mCurrentPointOriY = mCurrentOperatePoint.getLocation().y;
                            mCurrentPointOriPivotX = mCurrentOperatePoint.getPivotX();
                            mCurrentPointOriPivotY = mCurrentOperatePoint.getPivotY();
                        }
                        break;

                    case TYPE_EDIT_TYPE_EDIT_POINT:
                        if (mCurrentOperatePoint != null && !mIsLongPress) {//不是长按，则是拖动，拖动结束时需改变保存的操作点的位置和中点的值
                            mCurrentPointOriX = mCurrentOperatePoint.getLocation().x;
                            mCurrentPointOriY = mCurrentOperatePoint.getLocation().y;
                            mCurrentPointOriPivotX = mCurrentOperatePoint.getPivotX();
                            mCurrentPointOriPivotY = mCurrentOperatePoint.getPivotY();
                        }
                        break;
                    case TYPE_EDIT_TYPE_VIRTUAL_WALL:
                        mCurrentPath = null;
                        invalidate();
                        break;
                    case TYPE_EDIT_ERASE:
//                        if (mCanUseErase) {
//                            mErasePath = null;
//                            mCurrentErasePathBean = null;
//                            invalidate();
//                        }
                        break;
                }

                if(mTouchListener!=null){
                    mTouchListener.onScrollEnd(e,toX(mTouchX),toY(mTouchY));
                }
            }


            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                mIsMoved = true; //表示是拖动状态

                if (toX(e2.getX()) > 0 && toX(e2.getX()) < bitmapWidth
                        &&
                        toY(e2.getY()) > 0 && toY(e2.getY()) < bitmapHeight
                ) { //在屏幕范围内有效

                    if (mEditType != TYPE_EDIT_INIT) {
                        if (mOnEditedMapListener != null) {
                            mOnEditedMapListener.onEditedMap();
                        }
                    }

                    mLastTouchX = mTouchX;
                    mLastTouchY = mTouchY;
                    mTouchX = e2.getX();
                    mTouchY = e2.getY();
                    switch (mEditType) {

                        case TYPE_EDIT_TYPE_ADD_STANDBY_POINT:
                        case TYPE_EDIT_TYPE_ADD_INIT_POINT:
                        case TYPE_EDIT_TYPE_ADD_CHARGE:
                        case TYPE_EDIT_TYPE_ADD_NAVIGATION:
                        case TYPE_EDIT_TYPE_EDIT_POINT:

                            if (mSelectedPoint != null && mIsLongPress) {  //长按拖动改变方向
                                mSelectedPoint.setItemRotate(AngleUtil.computeAngle(mSelectedPointOriPivotX, mSelectedPointOriPivotY, toX(mTouchX), toY(mTouchY)));
                                mSelectedPoint.setTheta(AngleUtil.calculateDirectionAngle(mSelectedPointOriPivotX, mSelectedPointOriPivotY, toX(mTouchX), toY(mTouchY)));
                            } else if (mCurrentOperatePoint != null && !mIsLongPress && mIsClickOnCurrentOperatePoint) {  //没有长按，仅仅拖动改变位置
                                mCurrentOperatePoint.setLocation(mCurrentPointOriX + toX(mTouchX) - toX(mTouchDownX),
                                        mCurrentPointOriY + toY(mTouchY) - toY(mTouchDownY), true);
                            }
                            break;

                        case TYPE_EDIT_TYPE_INIT_LOCATION:
                            if (mSelectedPoint != null) {//非选点模式 给mSelectedPoint 赋值角度和旋转偏移
                                Log.e("pointInfo", "mSelectedPoint====setItemRotate &&setTheta");
                                mSelectedPoint.setItemRotate(AngleUtil.computeAngle(mSelectedPointOriPivotX, mSelectedPointOriPivotY, toX(mTouchX), toY(mTouchY)));
                                mSelectedPoint.setTheta(AngleUtil.calculateDirectionAngle(mSelectedPointOriPivotX, mSelectedPointOriPivotY, toX(mTouchX), toY(mTouchY)));
                            }
                            break;

                        case TYPE_EDIT_TYPE_VIRTUAL_WALL: /*添加路径点*/
                            if (mCurrentPath != null) {
                                mCurrentOperateWall.getWallPointList().add(new VirtualWallBean.WallPoint(toX(mTouchX), toY(mTouchY))); /*更新路径*/
//                            mCurrentPath.quadTo(toX(mLastTouchX), toY(mLastTouchY), toX((mTouchX + mLastTouchX) / 2), toY((mTouchY + mLastTouchY) / 2));
                                mCurrentPath.lineTo(toX(mTouchX), toY(mTouchY));
                            } else {
                                mCurrentPath = new Path();
                                mCurrentPath.moveTo(toX(mTouchX), toY(mTouchY));
                                mCurrentOperateWall = new VirtualWallBean();
                                mCurrentOperateWall.setPath(mCurrentPath);
                                mCurrentOperateWall.getWallPointList().add(new VirtualWallBean.WallPoint(toX(mTouchX), toY(mTouchY)));
                            }
                            invalidate();
                            break;

                        case TYPE_EDIT_ERASE:
                            if (mCanUseErase && mErasePoint != null) { /*更新路径*/
//                                mErasePath.quadTo(toX(mLastTouchX), toY(mLastTouchY), toX((mTouchX + mLastTouchX) / 2), toY((mTouchY + mLastTouchY) / 2));
//                                mErasePath.lineTo(toX(mTouchX), toY(mTouchY));
//                                mErasePath.addCircle(toX(mTouchX), toY(mTouchY), mErasePaintWidth, Path.Direction.CW);
                                mErasePoint.x = (int) toX(mTouchX);
                                mErasePoint.y = (int) toY(mTouchY);
                            }
                            break;
                    }

                    if(mTouchListener!=null){
                        mTouchListener.onScroll(e1,e2,distanceX,distanceY,toX(mTouchX),toY(mTouchY));
                    }
                    invalidate();
                }
                return true;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                mDownEvent = e;
                mLastTouchX = mTouchX = mTouchDownX = e.getX();
                mLastTouchY = mTouchY = mTouchDownY = e.getY();

                boolean mIsOperatePointInPointList = false;
                for (int i = 0; i < mPositionPointList.size(); i++) {
                    PositionPointBean positionPointBean = mPositionPointList.get(i);
                    if (mCurrentOperatePoint != null) {
                        Log.e("pointInfo", "进入循环" + mCurrentOperatePoint.getPointName() + "*******" + positionPointBean.getPointName());
                    } else {
                        Log.e("pointInfo", "=======>>>>>>mCurrentOperatePoint == null");
                    }

                    if (mCurrentOperatePoint != null
                            && mCurrentOperatePoint.getPointName().equals(positionPointBean.getPointName())
                            && mCurrentOperatePoint.getType() == positionPointBean.getType()
                    ) {
                        mIsOperatePointInPointList = true;
                        mCurrentOperatePoint.setNeedShowName(positionPointBean.isNeedShowName());
                        Log.e("pointInfo", "mCurrentOperatePoint != null");
                    }

                    if (positionPointBean.containsNameClose(toX(mTouchX), toY(mTouchY))) {
                        positionPointBean.setNeedShowName(false);
                        invalidate();
                    }

                }

                if (!mIsOperatePointInPointList && mCurrentOperatePoint != null &&
                        mCurrentOperatePoint.containsNameClose(toX(mTouchX), toY(mTouchY))
                ) {
                    mCurrentOperatePoint.setNeedShowName(false);
                    invalidate();
                }


                for (VirtualWallBean virtualWallBean : mVirtualWallList) {
                    if (virtualWallBean.containsNameClose(toX(mTouchX), toY(mTouchY))) {
                        virtualWallBean.setNeedShowName(false);
                        invalidate();
                        break;
                    }
                }

                for (RouteBean routeBean : mRoutesList) {
                    if (routeBean.containsNameClose(toX(mTouchX), toY(mTouchY))) {
                        routeBean.setmNeedShowName(false);
                        invalidate();
                        break;
                    }
                }

                if (toX(e.getX()) > 0 && toX(e.getX()) < bitmapWidth
                        &&
                        toY(e.getY()) > 0 && toY(e.getY()) < bitmapHeight
                ) { //在屏幕范围内有效
                    Log.e("pointInfo", "在屏幕范围内有效=====》onePointClick");
                    onePointClick();
                }

                return true;
            }

            @Override
            public void onPointerDown(MotionEvent e) {
                mOtherPointDown = true;
//                double delta_x = (e.getX(0) - e.getX(1));
//                double delta_y = (e.getY(0) - e.getY(1));
//                double radians = Math.atan2(delta_y, delta_x);
//                oldAngle = (float) Math.toDegrees(radians);
            }

            @Override
            public void onPointerMove(MotionEvent e) {
//                Log.d(TAG, "onPointerMove：" + e.getPointerCount() + "");
//                if (e.getPointerCount() == 2) {
//                    double delta_x = (e.getX(0) - e.getX(1));
//                    double delta_y = (e.getY(0) - e.getY(1));
//                    double radians = Math.atan2(delta_y, delta_x);
//                    float newAngle = (float) Math.toDegrees(radians);
//                    float newRotation = newAngle - oldAngle + oldRotation;
//                    mRotate = newRotation;
//                    oldRotation = newRotation;
//                    oldAngle = newAngle;
//
//
//                    if (mRotate % 360 != 0) {
//                        for (PositionPointBean positionPointBean : mPositionPointList) {
//                            positionPointBean.setNeedShowName(false);
//                        }
//                        for (VirtualWallBean virtualWallBean : mVirtualWallList) {
//                            virtualWallBean.setNeedShowName(false);
//                        }
//                        if (mCurrentOperatePoint != null) {
//                            mCurrentOperatePoint.setNeedShowName(false);
//                        }
//                        if (mSelectedPoint != null) {
//                            mSelectedPoint.setNeedShowName(false);
//                        }
//                    }
//
//                    invalidate();
//                }

            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public boolean onSingleTapUp(MotionEvent e) {

//                Log.d(TAG, "onSingleTapUp");
                Log.e("pointInfo", "onSingleTapUp");
                mLastTouchX = mTouchX;
                mLastTouchY = mTouchY;
                mTouchX = e.getX();
                mTouchY = e.getY();

                //当前操作点不标记点列表中
                boolean mIsOperatePointInPointList = false;

                //是否点在了某个标记点上
                boolean isClickOnPositionPoint = false;
                for (int i = 0; i < mPositionPointList.size(); i++) {
                    PositionPointBean positionPointBean = mPositionPointList.get(i);
                    if (positionPointBean.contains(toX(mTouchX), toY(mTouchY))) {
                        isClickOnPositionPoint = true;
//                        if (positionPointBean.getType() == PositionPointBean.TYPE_NAVIGATION_POINT) {

                        PositionUtil.setPointShowNameEnable(mPositionPointList, positionPointBean);
//                        }
//                        else {
//                            positionPointBean.setNeedShowName(!positionPointBean.isNeedShowName());
//                        }
                        if (mOnPositionPointClickListener != null) {
                            mOnPositionPointClickListener.onClick(positionPointBean);
                        }

                        if (mCurrentOperatePoint != null
                                && mCurrentOperatePoint.getPointName().equals(positionPointBean.getPointName())
                        ) {
                            mIsOperatePointInPointList = true;
                            mCurrentOperatePoint.setNeedShowName(positionPointBean.isNeedShowName());
                        }

                        break;
                    }
                }

                if (!mIsOperatePointInPointList && mCurrentOperatePoint != null &&
                        mCurrentOperatePoint.contains(toX(mTouchX), toY(mTouchY))
                ) {
                    mCurrentOperatePoint.setNeedShowName(!mCurrentOperatePoint.isNeedShowName());
                }

                if (isClickOnPositionPoint) {
                    return true;
                }

                for (VirtualWallBean virtualWallBean : mVirtualWallList) {

                    boolean isClickOnVirtualWall = false;

                    List<VirtualWallBean.WallPoint> wallPointList = virtualWallBean.getWallPointList();

                    for (int i = 0; i < wallPointList.size(); i++) {
                        if (i >= 1) {
                            VirtualWallBean.WallPoint startWallPoint = wallPointList.get(i - 1);
                            VirtualWallBean.WallPoint endWallPoint = wallPointList.get(i);
                            if (VirtualWallBean.isInLineTwoPoint(startWallPoint, endWallPoint, new VirtualWallBean.WallPoint(toX(mTouchX), toY(mTouchY)))) {
                                isClickOnVirtualWall = true;
                                break;
                            }
                        }
                    }

                    if (isClickOnVirtualWall) {
                        virtualWallBean.setNeedShowName(!virtualWallBean.isNeedShowName());
                        if (mOnVirtualWallClickListener != null) {
                            mOnVirtualWallClickListener.onClick(virtualWallBean);
                        }
                        VirtualWallUtil.setVirtualWallShowNameEnable(mVirtualWallList, virtualWallBean);
                        break;
                    }
                }

                for (RouteBean routeBean : mRoutesList) {

                    boolean isClickOnRoute = false;

                    List<RouteBean.PositionsDTO> mRoutePointsList = routeBean.getPositions();
                    RouteBean.PositionsDTO startPoint = mRoutePointsList.get(0);
                    RouteBean.PositionsDTO endPoint = mRoutePointsList.get(1);
                    if (RouteBean.isInLineTwoPoint(startPoint, endPoint, new RouteBean.PositionsDTO(toX(mTouchX), toY(mTouchY)))) {
                        isClickOnRoute = true;
                    }

                    if (isClickOnRoute) {
                        routeBean.setmNeedShowName(!routeBean.ismNeedShowName());
                        if (mOnRouteClickListener != null) {
                            mOnRouteClickListener.onClick(routeBean);
                        }
                        NavigationRouteUtil.setRouteShowNameEnable(mRoutesList, routeBean);
                    }
                }

                if (mTouchListener != null) {
                    mTouchListener.onSingleTapUp(e,toX(e.getX()),toY(e.getY()));
                }
                invalidate();
                return true;
            }


            @Override
            public void onUpOrCancel(MotionEvent e) {
                super.onUpOrCancel(e);
                mIsReleased = true;
                mOtherPointDown = false;
                if(mEditType == TYPE_EDIT_TYPE_NAVIGATION_ROUTE){
                    mIsReleased = false;
                }

                if (toX(e.getX()) > 0 && toX(e.getX()) < bitmapWidth
                        &&
                        toY(e.getY()) > 0 && toY(e.getY()) < bitmapHeight
                ) {
                    switch (mEditType) {
                        case TYPE_EDIT_TYPE_INIT_LOCATION:
                            if (mCurrentOperatePoint != null && !mIsScale) {
                                if (mOnInitLocationFinishListener != null) {
                                    if (!isChoosenPointMode) {//非选点模式
                                        mCurrentOperatePoint
                                                .setTheta(
                                                        AngleUtil.calculateDirectionAngle(mSelectedPointOriPivotX, mSelectedPointOriPivotY,
                                                                toX(e.getX()),
                                                                toY(e.getY())
                                                        ));
                                        mCurrentOperatePoint
                                                .setItemRotate(
                                                        AngleUtil.computeAngle(mSelectedPointOriPivotX, mSelectedPointOriPivotY,
                                                                toX(e.getX()),
                                                                toY(e.getY())
                                                        ));
                                    }
                                    mOnInitLocationFinishListener.onFinish(mCurrentOperatePoint);//抛到UI界面
                                }
                                mCurrentOperatePoint = null;
                            }
                            break;

                        case TYPE_EDIT_TYPE_ADD_STANDBY_POINT:
                        case TYPE_EDIT_TYPE_ADD_INIT_POINT:

                            if (mCurrentOperatePoint != null && !mIsScale) {
                                if (mIsLongPress) {
                                    mCurrentOperatePoint
                                            .setTheta(
                                                    AngleUtil.calculateDirectionAngle(mSelectedPointOriPivotX, mSelectedPointOriPivotY,
                                                            toX(e.getX()),
                                                            toY(e.getY())
                                                    ));

                                    mCurrentOperatePoint
                                            .setItemRotate(
                                                    AngleUtil.computeAngle(mSelectedPointOriPivotX, mSelectedPointOriPivotY,
                                                            toX(e.getX()),
                                                            toY(e.getY())
                                                    ));
                                } else {
                                    mCurrentOperatePoint.setBitmapX(mCurrentOperatePoint.getPivotX());
                                    mCurrentOperatePoint.setBitmapY(mCurrentOperatePoint.getPivotY());
                                }
                            }
                            break;

                        case TYPE_EDIT_TYPE_ADD_CHARGE:
                            if (mCurrentOperatePoint != null && !mIsScale) {
                                if (mIsLongPress) {
                                    mCurrentOperatePoint
                                            .setTheta(
                                                    AngleUtil.calculateDirectionAngle(mSelectedPointOriPivotX, mSelectedPointOriPivotY,
                                                            toX(e.getX()),
                                                            toY(e.getY())
                                                    ));

                                    mCurrentOperatePoint
                                            .setItemRotate(
                                                    AngleUtil.computeAngle(mSelectedPointOriPivotX, mSelectedPointOriPivotY,
                                                            toX(e.getX()),
                                                            toY(e.getY())
                                                    ));
                                } else {
                                    mCurrentOperatePoint.setBitmapX(mCurrentOperatePoint.getPivotX());
                                    mCurrentOperatePoint.setBitmapY(mCurrentOperatePoint.getPivotY());
                                }
                            }
                            break;

                        case TYPE_EDIT_TYPE_ADD_NAVIGATION:
                            if (mCurrentOperatePoint != null && !mIsScale) {
                                if (mIsLongPress) {
                                    mCurrentOperatePoint
                                            .setTheta(
                                                    AngleUtil.calculateDirectionAngle(mSelectedPointOriPivotX, mSelectedPointOriPivotY,
                                                            toX(e.getX()),
                                                            toY(e.getY())
                                                    ));

                                    mCurrentOperatePoint
                                            .setItemRotate(
                                                    AngleUtil.computeAngle(mSelectedPointOriPivotX, mSelectedPointOriPivotY,
                                                            toX(e.getX()),
                                                            toY(e.getY())
                                                    ));

                                } else {
                                    mCurrentOperatePoint.setBitmapX(mCurrentOperatePoint.getPivotX());
                                    mCurrentOperatePoint.setBitmapY(mCurrentOperatePoint.getPivotY());
                                }
                            }
                            break;

                        case TYPE_EDIT_TYPE_EDIT_POINT:
                            if (mCurrentOperatePoint != null && !mIsScale) {
                                if (mIsLongPress) {
                                    mCurrentOperatePoint
                                            .setTheta(
                                                    AngleUtil.calculateDirectionAngle(mSelectedPointOriPivotX, mSelectedPointOriPivotY,
                                                            toX(e.getX()),
                                                            toY(e.getY())
                                                    ));
                                    mCurrentOperatePoint
                                            .setItemRotate(
                                                    AngleUtil.computeAngle(mSelectedPointOriPivotX, mSelectedPointOriPivotY,
                                                            toX(e.getX()),
                                                            toY(e.getY())
                                                    ));
                                } else {
                                    mCurrentOperatePoint.setBitmapX(mCurrentOperatePoint.getPivotX());
                                    mCurrentOperatePoint.setBitmapY(mCurrentOperatePoint.getPivotY());
                                }

                                if (mOnEditNavigationPointFinishListener != null) {
                                    mOnEditNavigationPointFinishListener.onFinish(mCurrentOperatePoint);
                                }

                            }
                            break;
                    }

                }

                if (mSelectedPoint != null && mIsLongPress) {
                    mSelectedPoint.setLocation(mSelectedPointOriX, mSelectedPointOriY, true);
                    mSelectedPoint
                            .setTheta(
                                    AngleUtil.calculateDirectionAngle(mSelectedPointOriPivotX, mSelectedPointOriPivotY,
                                            toX(e.getX()),
                                            toY(e.getY())
                                    ));
                    mSelectedPoint
                            .setItemRotate(
                                    AngleUtil.computeAngle(mSelectedPointOriPivotX, mSelectedPointOriPivotY,
                                            toX(e.getX()),
                                            toY(e.getY())
                                    ));
                    mSelectedPoint = null;
                }

//                //当前橡皮擦路径
//                switch (mEditType) {
//                    case TYPE_EDIT_ERASE:
//                        mErasePath = null;
//                        mCurrentErasePathBean = null;
//                        break;
//                }

                mIsScale = false;
                mIsLongPress = false;
                mIsClickOnCurrentOperatePoint = false;

                if (mTouchListener != null) {
                    mTouchListener.onUpOrCancel(e,toX(e.getX()),toY(e.getY()));
                }
                invalidate();
            }
        });

        // 针对涂鸦的手势参数设置
        // 下面两行绘画场景下应该设置间距为大于等于1，否则设为0双指缩放后抬起其中一个手指仍然可以移动
        mTouchGestureDetector.setScaleSpanSlop(1);  // 手势前识别为缩放手势的双指滑动最小距离值
        mTouchGestureDetector.setScaleMinSpan(1);  // 缩放过程中识别为缩放手势的双指最小距离值
        mTouchGestureDetector.setIsLongpressEnabled(false);
        mTouchGestureDetector.setIsScrollAfterScaled(false);
    }


    private void onePointClick() {
        mOnePointCounter++;
        mIsReleased = false;
        mIsMoved = false;
        mOtherPointDown = false;

        postDelayed(mOnePointDownRunnable, 50);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        super.onSizeChanged(width, height, oldw, oldh);
        Log.d(TAG, "onSizeChanged");
        if (mBitmap != null) {
            initBitmap(originSize.orginWidth, originSize.orginWidth);
        }
    }

    private Matrix mTouchEventMatrix = new Matrix();

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        MotionEvent transformedEvent = MotionEvent.obtain(event);
        mTouchEventMatrix.reset();
        mTouchEventMatrix.setRotate(-mRotate, getWidth() / 2, getHeight() / 2);
        transformedEvent.transform(mTouchEventMatrix);

        boolean consumed = mTouchGestureDetector.onTouchEvent(transformedEvent); //由手势识别器处理手势
        if (!consumed) {
            return super.dispatchTouchEvent(transformedEvent);
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        try {

            if (mBitmap == null || mBitmap.isRecycled()) {
                return;
            }

            canvas.save();
            canvas.rotate(mRotate, getWidth() / 2, getHeight() / 2);
            canvas.translate(getAllTranX(), getAllTranY()); //偏移画布
            float scale = getAllScale();
            canvas.scale(scale, scale); //缩放画布
            canvas.save();

            Bitmap drawBitmap;
            if (enableBitmapRegion) {
                // 指定解码区域
                if (mRect == null) {
                    mRect = new Rect(0, 0, canvas.getWidth(), canvas.getHeight());
                }
                drawBitmap = mDecoder.decodeRegion(mRect, mOptions);
            } else {
                drawBitmap = mBitmap;
            }

            //绘制图片
            if (drawBitmap != null) {
                canvas.save();
                canvas.scale((float) originScale, (float) originScale);
                canvas.drawBitmap(drawBitmap, 0, 0, null);
                canvas.restore();
            }

            if (!mIsmipmapOutside && drawBitmap != null) { //裁剪绘制区域为图片区域
                canvas.clipRect(0, 0, bitmapWidth, bitmapHeight);
            }

            switch (mEditType) {
                case TYPE_EDIT_ERASE:

                    //绘制橡皮擦路径
//                    for (ErasePathBean erasePathBean : mErasePathList) {
//                        Log.i("LEOO", "erasePathBean >>" + erasePathBean.getPaintStrokeWidth());
//                        mErasePaint.setStrokeWidth(erasePathBean.getPaintStrokeWidth());
//                        Path erasePath = erasePathBean.getPath();
//                        canvas.drawPath(erasePath, mErasePaint);
//                    }

                    //绘制橡皮擦图标
                    if (mErasePoint != null) {
//                        int radius = mErasePaintWidth / 2;
//                        radius = radius > 50 ? radius - 50 : radius;
//                        canvas.drawCircle(mErasePoint.x, mErasePoint.y, radius, mEraseIconBgPaint);
                        RadialGradient radialGradient = new RadialGradient(mErasePoint.x, mErasePoint.y, mErasePaintWidth / 2, colors, new float[]{0.2f, 1}, Shader.TileMode.CLAMP);
                        mEraseIconBgPaint.setShader(radialGradient);
                        canvas.drawCircle(mErasePoint.x, mErasePoint.y, mErasePaintWidth / 2, mEraseIconBgPaint);
                        canvas.drawCircle(mErasePoint.x, mErasePoint.y, mErasePaintWidth / 2, mEraseIconPaint);
                    }

                    return;

                default:
                    /**
                     * 测试航路的绘制
                     Path testPath = new Path();
                     testPath.moveTo((float)100, (float)100);
                     testPath.lineTo((float)200, (float)200);
                     canvas.drawPath(testPath, mRoutePaint);
                     */
                    //绘制航路
//                    Log.e("routelog", "航路个数= " + mRoutesList.size());
                    for (int i = 0; i < mRoutesList.size(); i++) {
                        canvas.save();
                        RouteBean routeBean = mRoutesList.get(i);
                        //"direction":1, (0:单向航路  ； 1：双向航路 )
                        if (routeBean.getDirection() == 1) {
                            Log.e("routelog", "双向航路");
                            canvas.drawPath(routeBean.getmPath(), mRoutePaint);
                            canvas.drawPath(routeBean.getmFrontPath(), mRoutePaint);
                            canvas.drawPath(routeBean.getmBackPath(), mRoutePaint);
                        } else if (routeBean.getDirection() == 0) {
                            Log.e("routelog", "单向航路");
                            canvas.drawPath(routeBean.getmPath(), mRoutePaint);
                            canvas.drawPath(routeBean.getmFrontPath(), mRoutePaint);

//                            // 测试箭头航路
//                            canvas.drawPath( setArrowPath(routeBean.getmPath(),200,200,50,50) , mRoutePaint);//开始->结束 正向
//                            canvas.drawPath( setArrowPath(routeBean.getmPath(),50,50,200,200) , mRoutePaint);//结束->开始 反向
                        }
                        canvas.restore();
                    }

                    if (mCurrentOperateRoute != null && mCurrentOperateRoute.getmPath() != null) {
                        canvas.drawPath(mCurrentOperateRoute.getmPath(), mRoutePaint);

                        if (mCurrentOperateRoute.getDirection() == 1) {
                            Log.e("routelog", "双向航路");
                            canvas.drawPath(mCurrentOperateRoute.getmPath(), mRoutePaint);
                            canvas.drawPath(mCurrentOperateRoute.getmFrontPath(), mRoutePaint);
                            canvas.drawPath(mCurrentOperateRoute.getmBackPath(), mRoutePaint);
                        } else if (mCurrentOperateRoute.getDirection() == 0) {
                            Log.e("routelog", "单向航路");
                            canvas.drawPath(mCurrentOperateRoute.getmPath(), mRoutePaint);
                            canvas.drawPath(mCurrentOperateRoute.getmFrontPath(), mRoutePaint);
                        }
                    }

                    for (int i = 0; i < mVirtualWallList.size(); i++) {
                        VirtualWallBean virtualWallBean = mVirtualWallList.get(i);
                        canvas.drawPath(virtualWallBean.getPath(), mVirtualWallPaint);
                    }


                    if (mCurrentOperateWall != null) {
                        canvas.drawPath(mCurrentOperateWall.getPath(), mVirtualWallPaint);
                    }

                    for (PositionPointBean positionPointBean : mPositionPointList) {
                        if (mCurrentOperatePoint == null
                                || (mCurrentOperatePoint != null && !mCurrentOperatePoint.getPointName().equals(positionPointBean.getPointName()))
                        ) {

                            canvas.save();
                            canvas.translate(positionPointBean.getLocation().x, positionPointBean.getLocation().y); //偏移，把坐标系平移到item矩形范围
                            canvas.rotate(positionPointBean.getItemRotate(), positionPointBean.getPivotX() - positionPointBean.getLocation().x,
                                    positionPointBean.getPivotY() - positionPointBean.getLocation().y);
                            canvas.drawBitmap(positionPointBean.getBitmap(), positionPointBean.getSrcRect(), positionPointBean.getDstRect(), null);
                            canvas.restore();
                        }
                    }


                    if (mRobotPositionBean != null) {
                        canvas.save();
                        canvas.translate(mRobotPositionBean.getLocation().x, mRobotPositionBean.getLocation().y); //偏移，把坐标系平移到item矩形范围
                        canvas.rotate(mRobotPositionBean.getItemRotate(), mRobotPositionBean.getPivotX() - mRobotPositionBean.getLocation().x,
                                mRobotPositionBean.getPivotY() - mRobotPositionBean.getLocation().y);
                        canvas.drawBitmap(mRobotPositionBean.getBitmap(), mRobotPositionBean.getSrcRect(), mRobotPositionBean.getDstRect(), null);
                        canvas.restore();
                    }

                    //绘制当前操作点
                    if (mCurrentOperatePoint != null) {
                        canvas.save();

                        if (mSelectedPoint != null
                                && mSelectedPoint.getPointName().equals(mCurrentOperatePoint.getPointName())
                                && mSelectedPoint.getType() == mCurrentOperatePoint.getType()
                        ) {
                            canvas.save();
                            canvas.translate(mSelectedPoint.getLocation().x, mSelectedPoint.getLocation().y); //偏移，把坐标系平移到item矩形范围
                            canvas.rotate(mSelectedPoint.getItemRotate(), mSelectedPoint.getPivotX() - mSelectedPoint.getLocation().x,
                                    mSelectedPoint.getPivotY() - mSelectedPoint.getLocation().y); //旋转坐标系

                            canvas.drawBitmap(mSelectedPoint.getBitmap(), mSelectedPoint.getSrcRect(), mSelectedPoint.getDstRect(), null);
                            canvas.restore();
                        } else {

                            canvas.translate(mCurrentOperatePoint.getLocation().x, mCurrentOperatePoint.getLocation().y); // 偏移，把坐标系平移到item矩形范围

                            canvas.save();
                            canvas.rotate(mCurrentOperatePoint.getItemRotate(), mCurrentOperatePoint.getPivotX() - mCurrentOperatePoint.getLocation().x,
                                    mCurrentOperatePoint.getPivotY() - mCurrentOperatePoint.getLocation().y);
                            canvas.drawBitmap(mCurrentOperatePoint.getBitmap(), mCurrentOperatePoint.getSrcRect(), mCurrentOperatePoint.getDstRect(), null);
                            canvas.restore();
                        }
                        canvas.restore();
                    }

                    for (RobotLaserDataBean robotLaserDataBean : mRobotLaserDataBeanList) {
                        canvas.drawPoint(robotLaserDataBean.getMapX(), robotLaserDataBean.getMapY(), mLaserPaint);
                    }

            }

            canvas.restore();
            canvas.restore();

            canvas.save();
            canvas.rotate(mRotate, getWidth() / 2, getHeight() / 2);
            canvas.translate(getAllTranX(), getAllTranY());
            canvas.scale(scale, scale);

            for (PositionPointBean positionPointBean : mPositionPointList) {
                if (mCurrentOperatePoint == null
                        || (mCurrentOperatePoint != null &&
                        !mCurrentOperatePoint.getPointName().equals(positionPointBean.getPointName()))
                ) {

                    canvas.save();
                    canvas.translate(positionPointBean.getNameLocation().x, positionPointBean.getNameLocation().y); // 偏移，把坐标系平移到item矩形范围

                    if (positionPointBean.isNeedShowName()) {
                        //绘制标记点名字
                        switch (positionPointBean.getType()) {
                            case PositionPointBean.TYPE_INIT_POINT:
                                positionPointBean.drawNameBg(canvas);
                                break;
                            case PositionPointBean.TYPE_CHARGE_POINT:
                                positionPointBean.drawNameBg(canvas);
                                break;
                            case PositionPointBean.TYPE_NAVIGATION_POINT:
                                positionPointBean.drawNameBg(canvas);
                                break;
                            case PositionPointBean.TYPE_STANDBY_POINT:
                                positionPointBean.drawNameBg(canvas);
                                break;
                        }

                    }
                    canvas.restore();

                    //绘制名字关闭图标
                    canvas.save();
                    canvas.translate(positionPointBean.getNameCloseLocation().x, positionPointBean.getNameCloseLocation().y); // 偏移，把坐标系平移到item矩形范围
                    if (positionPointBean.isNeedShowName()) {
                        //绘制标记点名字
                        switch (positionPointBean.getType()) {
                            case PositionPointBean.TYPE_INIT_POINT:
                                positionPointBean.drawNameCloseBg(canvas);
                                break;
                            case PositionPointBean.TYPE_CHARGE_POINT:
                                positionPointBean.drawNameCloseBg(canvas);
                                break;
                            case PositionPointBean.TYPE_NAVIGATION_POINT:
                                positionPointBean.drawNameCloseBg(canvas);
                                break;
                            case PositionPointBean.TYPE_STANDBY_POINT:
                                positionPointBean.drawNameCloseBg(canvas);
                                break;
                        }

                    }
                    canvas.restore();
                }
            }

            //绘制当前操作点名字
            if (mCurrentOperatePoint != null
                    && mCurrentOperatePoint.isNeedShowName()
            ) {
                canvas.save();
                canvas.translate(mCurrentOperatePoint.getNameLocation().x, mCurrentOperatePoint.getNameLocation().y); // 偏移，把坐标系平移到item矩形范围
                if (mCurrentOperatePoint.isNeedShowName()) {
                    //绘制标记点名字
                    switch (mCurrentOperatePoint.getType()) {
                        case PositionPointBean.TYPE_INIT_POINT:
                            mCurrentOperatePoint.drawNameBg(canvas);
                            break;
                        case PositionPointBean.TYPE_CHARGE_POINT:
                            mCurrentOperatePoint.drawNameBg(canvas);
                            break;
                        case PositionPointBean.TYPE_NAVIGATION_POINT:
                            mCurrentOperatePoint.drawNameBg(canvas);
                            break;
                        case PositionPointBean.TYPE_STANDBY_POINT:
                            mCurrentOperatePoint.drawNameBg(canvas);
                            break;
                    }
                }
                canvas.restore();

                //绘制名字关闭图标
                canvas.save();
                canvas.translate(mCurrentOperatePoint.getNameCloseLocation().x, mCurrentOperatePoint.getNameCloseLocation().y); // 偏移，把坐标系平移到item矩形范围
                if (mCurrentOperatePoint.isNeedShowName()) {
                    //绘制标记点名字
                    switch (mCurrentOperatePoint.getType()) {
                        case PositionPointBean.TYPE_INIT_POINT:
                            mCurrentOperatePoint.drawNameCloseBg(canvas);
                            break;
                        case PositionPointBean.TYPE_CHARGE_POINT:
                            mCurrentOperatePoint.drawNameCloseBg(canvas);
                            break;
                        case PositionPointBean.TYPE_NAVIGATION_POINT:
                            mCurrentOperatePoint.drawNameCloseBg(canvas);
                            break;
                        case PositionPointBean.TYPE_STANDBY_POINT:
                            mCurrentOperatePoint.drawNameCloseBg(canvas);
                            break;
                    }
                }
                canvas.restore();

            }

            for (int i = 0; i < mVirtualWallList.size(); i++) {
                VirtualWallBean virtualWallBean = mVirtualWallList.get(i);
                if (virtualWallBean.isNeedShowName()) {
                    canvas.save();
                    virtualWallBean.drawNameBg(canvas, mVirtualWallNameBGBitmap, mNamePaint, mContext.getString(R.string.virtual_wall) + (i + 1), mNameBgSize);
                    canvas.restore();
                }
            }

            for (int i = 0; i < mVirtualWallList.size(); i++) {
                VirtualWallBean virtualWallBean = mVirtualWallList.get(i);
                if (virtualWallBean.isNeedShowName()) {
                    canvas.save();
                    virtualWallBean.drawNameCloseBg(canvas,
                            mVirtualWallNameBGBitmap,
                            mNamePaint,
                            mContext.getString(R.string.virtual_wall) + (i + 1),
                            mNameBgSize,
                            mVirtualWallNameCloseBgBitmap, mNameCloseSize);
                    canvas.restore();
                }
            }

            //绘制航路弹框
            for (int i = 0; i < mRoutesList.size(); i++) {
                RouteBean mRouteBean = mRoutesList.get(i);
                if (mRouteBean.ismNeedShowName()) {
                    //绘制航路名字
                    canvas.save();
                    mRouteBean.drawRouteNameBg(canvas, mRouteNameBGBitmap, mNamePaint, mContext.getString(R.string.navigation_route) + (i + 1), mNameBgSize, mRouteBean.getmPath());
                    canvas.restore();
                    canvas.save();
                    mRouteBean.drawNameCloseBg(canvas, mVirtualWallNameCloseBgBitmap, mNameCloseSize,mRouteBean.getmPath());
                    canvas.restore();
                }
            }

            if (mOnDrawListener != null) {
                mOnDrawListener.onDraw(canvas);
            }

            canvas.restore();

        } catch (Exception e) {
            e.printStackTrace();
            if (mOnCanvasExceptionListener != null) {
                mOnCanvasExceptionListener.canvasException();
            }
        }
    }

    public void setBitmap(Bitmap bitmap) {
        if (mBitmap != null) {
            mBitmap.recycle();
        }
        ImageCache.getInstance().addBitmapToMemoryCache("Bitmap", bitmap);
        mBitmap = ImageCache.getInstance().getBitmapFromMemCache("Bitmap");

        originSize = new FileUtils.OriginSize();
        originSize.inSampleSize = 1;
        originSize.originHeight = bitmap.getHeight();
        originSize.orginWidth = bitmap.getWidth();
        initBitmap(bitmap.getWidth(), bitmap.getHeight());
        Log.d(TAG, "setBitmap refresh");
        refresh();
    }

    /**
     * 设置地图信息
     *
     * @param path
     * @param scaleWidth
     * @param scaleHeight
     */
    public void setBitmap(String path, int scaleWidth, int scaleHeight) {
        setBitmap(path, scaleWidth, scaleHeight, true);
    }

    public void setBitmap(String path, int scaleWidth, int scaleHeight, boolean enableBitmapChache) {
        if (path == null || path.isEmpty()) {
            return;
        }

        this.enableBitmapChache = enableBitmapChache;
        if (enableBitmapChache && ImageCache.getInstance().getBitmapFromMemCache(path) != null && !ImageCache.getInstance().getBitmapFromMemCache(path).isRecycled()) {
            originSize = FileUtils.decodeOriginSize(path, scaleWidth, scaleHeight);
            mBitmap = ImageCache.getInstance().getBitmapFromMemCache(path);
        } else {
            Bitmap bitmap;
            try {
                bitmap = FileUtils.decodeSampledBitmapFromResource(path
                        , scaleWidth, scaleHeight, originSize);
                if (bitmap == null) {
                    return;
                }
                ImageCache.getInstance().addBitmapToMemoryCache(path, bitmap);
            } catch (OutOfMemoryError error) {
                return;
            } catch (NullPointerException error) {
                return;
            }

            mBitmap = bitmap;
        }

        performDraw();
    }

    public void setBitmap(Bitmap bitmap, FileUtils.OriginSize originSize) {
        if (bitmap == null) {
            return;
        }
        this.originSize = originSize;
        ImageCache.getInstance().addBitmapToMemoryCache("Bitmap", bitmap);
        mBitmap = bitmap;

        performDraw();
    }

    //开始使用bitmap
    private void performDraw() {
        this.originScale = originSize.inSampleSize;

        initBitmap(originSize.orginWidth, originSize.originHeight);
        Log.d(TAG, "setBitmap refresh");

        //开启图片裁剪加载
        if (enableBitmapRegion) {
            // 开启复用
            mOptions.inMutable = true;
            // 设置格式为RGB565
            mOptions.inPreferredConfig = Bitmap.Config.RGB_565;
            // 真正内存复用   // 复用的bitmap必须跟即将解码的bitmap尺寸一样
            mOptions.inBitmap = mBitmap;
            mOptions.inSampleSize = originSize.inSampleSize;

            // 区域解码器
            try {
                mDecoder = BitmapRegionDecoder.newInstance(originSize.path, false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        refresh();
    }

    private void initBitmap(int w, int h) { //不用resize preview
        if (mBitmap == null || mBitmap.isRecycled()) {
            return;
        }

        bitmapWidth = w;
        bitmapHeight = h;

        mRotate = 0;

        Log.d(TAG, "initBitmap：" + w + "," + h);

        float nw = w * 1f / getWidth();//原图宽与控件宽的比例
        float nh = h * 1f / getHeight();//原图高与控件高的比例
        if (nw > nh) {//缩放使用大的系数，当前为原图宽与控件宽的比例大的情况
            mCenterScale = 1 / nw;
            mCenterWidth = getWidth();
            mCenterHeight = (int) (h * mCenterScale);//高也等比例缩放
        } else {
            mCenterScale = 1 / nh;
            mCenterWidth = (int) (w * mCenterScale);
            mCenterHeight = getHeight();
        }

        //使图片居中
        mCentreTranX = (getWidth() - mCenterWidth) / 2f;
        mCentreTranY = (getHeight() - mCenterHeight) / 2f;

        //求出1dp对应的原图的像素（要乘上缩放比）
        mDoodleSizeUnit = DisplayUtil.dpToPx(mContext, 1) / mCenterScale;

        mPainStrokeWidth = DEFAULT_SIZE * mDoodleSizeUnit;
        mVirtualPaintStrokeSize = VIRTUAL_WALL_PAINT_STROKE_WIDTH * mDoodleSizeUnit;
        mErasePaintWidth = (int) (mErasePaintSize * mDoodleSizeUnit);
        updatePaintStrokeWidth();

        if (mCenterWidth != 0 && mCenterHeight != 0) {

            double mCenterSizeScale = (mCenterWidth * 1.0 / getWidth() <= mCenterHeight * 1.0 / getHeight()) ? (mCenterWidth * 1.0 / getWidth()) : (mCenterHeight * 1.0 / getHeight());

            if (mCenterSizeScale < 0.37
            ) {
                mPositionPointSize = 5 * mDoodleSizeUnit;
                mInitPositionPointBigSize = 60 * mDoodleSizeUnit;
            }

            if (mCenterSizeScale >= 0.37
                    && mCenterSizeScale <= 0.62
            ) {
                mPositionPointSize = 7 * mDoodleSizeUnit;
                mInitPositionPointBigSize = 70 * mDoodleSizeUnit;
            }

            if (mCenterSizeScale >= 0.62) {
                mPositionPointSize = DEFAULT_POSITION_POINT_SIZE * mDoodleSizeUnit;
                mInitPositionPointBigSize = DEFAULT_POSITION_POINT_BIG_SIZE * mDoodleSizeUnit;
            }

        }

        Log.d(TAG, "centerWidth centerHeight width height:" + mCenterWidth + "," + mCenterHeight + "," + getWidth() + "," + getHeight());

//        mPositionPointSize = DEFAULT_POSITION_POINT_SIZE * mDoodleSizeUnit;
//        mInitPositionPointBigSize = DEFAULT_POSITION_POINT_BIG_SIZE * mDoodleSizeUnit;
        Log.d(TAG, "initBitmap" + mCenterScale);
//        mPositionPointSize = DEFAULT_POSITION_POINT_SIZE;
//        mInitPositionPointBigSize = DEFAULT_POSITION_POINT_BIG_SIZE;

        mNameBgSize = DEFAULT_NAME_BG_SIZE * mDoodleSizeUnit;
        mNameCloseSize = DEFAULT_NAME_CLOSE_SIZE * mDoodleSizeUnit;

        mInitScale = mScale;

        //居中适应屏幕
        mTransX = mTransY = 0;
        mScale = 1;

    }

    /**
     * 更新画笔宽度
     */
    private void updatePaintStrokeWidth() {
        mVirtualWallPaint.setStrokeWidth(mVirtualPaintStrokeSize);
        mErasePaint.setStrokeWidth(mErasePaintWidth);
    }

    /**
     * 将屏幕触摸坐标x转换成在图片中的坐标
     */
    public final float toX(float touchX) {
        return (touchX - getAllTranX()) / getAllScale();
    }

    /**
     * 将屏幕触摸坐标y转换成在图片中的坐标
     */
    public final float toY(float touchY) {
        return (touchY - getAllTranY()) / getAllScale();
    }

    public void setScale(float scale, float pivotX, float pivotY) {
        if (scale < mMinScale) {
            scale = mMinScale;
        } else if (scale > mMaxScale) {
            scale = mMaxScale;
        }
        float touchX = toTouchX(pivotX);
        float touchY = toTouchY(pivotY);
        this.mScale = scale;

        // 缩放后，偏移图片，以产生围绕某个点缩放的效果
        mTransX = toTransX(touchX, pivotX);
        mTransY = toTransY(touchY, pivotY);

        refresh();
    }

    /**
     * 将图片坐标x转换成屏幕触摸坐标
     */
    public final float toTouchX(float x) {
        return x * getAllScale() + getAllTranX();
    }

    /**
     * 将图片坐标y转换成屏幕触摸坐标
     */
    public final float toTouchY(float y) {
        return y * getAllScale() + getAllTranY();
    }

    public float getAllScale() {
        return mCenterScale * mScale;
    }

    public float getAllTranX() {
        return mCentreTranX + mTransX;
    }

    public float getAllTranY() {
        return mCentreTranY + mTransY;
    }

    /**
     * 坐标换算
     * （公式由toX()中的公式推算出）
     *
     * @param touchX  触摸坐标
     * @param doodleX 在涂鸦图片中的坐标
     * @return 偏移量
     */
    public final float toTransX(float touchX, float doodleX) {
        return -doodleX * getAllScale() + touchX - mCentreTranX;
    }

    public final float toTransY(float touchY, float doodleY) {
        return -doodleY * getAllScale() + touchY - mCentreTranY;
    }

    public void setTranslation(float transX, float transY) {
        mTransX = transX;
        mTransY = transY;
        refresh();
    }

    @Override
    public void setTranslationX(float transX) {
        this.mTransX = transX;
        refresh();
    }

    @Override
    public void setTranslationY(float transY) {
        this.mTransY = transY;
        refresh();
    }

    @Override
    public float getTranslationX() {
        return mTransX;
    }

    @Override
    public float getTranslationY() {
        return mTransY;
    }

    public float getScale() {
        return mScale;
    }


    public int getEditType() {
        return mEditType;
    }

    public void setChoosen(boolean mode) {
        isChoosenPointMode = mode;
    }


    public void setEditType(int editType) {
        mEditType = editType;
        refresh();
    }

    public void setRouteType(int routeType) {
        mRouteType = routeType;
    }




    public List<PositionPointBean> getPositionPointList() {
        return mPositionPointList;
    }

    public void setPositionPointList(List<PositionPointBean> positionPointList) {
        mPositionPointList = positionPointList;
    }


    public void initPositionPointList(List<PositionPointBean> positionPointList) {
        mPositionPointList = positionPointList;
        for (PositionPointBean positionPointBean : mPositionPointList) {
            positionPointBean.init(mPositionPointSize, positionPointBean.getBitmapX(),
                    positionPointBean.getBitmapY(), mNameBgSize, mNameCloseSize);
        }
        refresh();
    }

    /**
     * 初始化虚拟墙(要在initBitmap之后)
     * 给 mVirtualWallList 赋值
     */
//    public void initVirtualWall(List<VirtualWallBean> virtualWallList) {
//        mVirtualWallList = virtualWallList;
//        for (int k = 0; k < virtualWallList.size(); k++) {
//            VirtualWallBean virtualWallBean = virtualWallList.get(k);
//            List<VirtualWallBean.WallPoint> wallPointList = virtualWallBean.getWallPointList();
//            Path path = new Path();
//
//            for (int i = 0; i < wallPointList.size(); i++) {
//                VirtualWallBean.WallPoint wallPoint = wallPointList.get(i);
//
//                if (i == 0) {
//                    path.moveTo((float) wallPoint.getX(),
//                            (float) wallPoint.getY());
//                }
//
//                if (i > 0) {
//
//                    VirtualWallBean.WallPoint lastWallPoint = wallPointList.get(i - 1);
//
//                    path.quadTo((float) lastWallPoint.getX(), (float) lastWallPoint.getY(),
//                            (float) ((lastWallPoint.getX() + wallPoint.getX()) / 2),
//                            (float) ((lastWallPoint.getY() + wallPoint.getY()) / 2));
//                }
//
//                virtualWallBean.setPath(path);
//            }
//            virtualWallBean.setName(mContext.getString(R.string.virtual_wall) + k);
//        }
//
//        refresh();
//
//    }
    public void initVirtualWall(List<VirtualWallBean> virtualWallList) {
        mVirtualWallList = virtualWallList;
        for (int k = 0; k < virtualWallList.size(); k++) {
            VirtualWallBean virtualWallBean = virtualWallList.get(k);

            List<VirtualWallBean.WallPoint> wallPointList = virtualWallBean.getWallPointList();
            Path path = new Path();

            for (int i = 0; i < wallPointList.size(); i++) {
                VirtualWallBean.WallPoint wallPoint = wallPointList.get(i);

                if (i == 0) {
                    path.moveTo((float) wallPoint.getX(),
                            (float) wallPoint.getY());
                }

                if (i > 0) {

                    path.lineTo((float) wallPoint.getX(), (float) wallPoint.getY());
                }

                virtualWallBean.setPath(path);
            }
            virtualWallBean.setName(mContext.getString(R.string.virtual_wall) + (k + 1)); //虚拟墙从1开始命名
        }

        refresh();

    }

    public List<VirtualWallBean> getVirtualWallList() {
        return mVirtualWallList;
    }


    /**
     * 初始化航路
     *
     * @param RouteBeanList
     */
    public void initRoutes(List<RouteBean> RouteBeanList) {
        mRoutesList = RouteBeanList;
        for (int k = 0; k < RouteBeanList.size(); k++) {
            RouteBean routeBean = RouteBeanList.get(k);

            List<RouteBean.PositionsDTO> RoutePointList = routeBean.getPositions();

            Path path = new Path();
            //开始点
            RouteBean.PositionsDTO startRoutePoint = RoutePointList.get(0);
            path.moveTo((float) startRoutePoint.getWorld_x().doubleValue(), (float) startRoutePoint.getWorld_y().doubleValue());
            //结束点
            RouteBean.PositionsDTO endRoutePoint = RoutePointList.get(1);
            path.lineTo((float) endRoutePoint.getWorld_x().doubleValue(), (float) endRoutePoint.getWorld_y().doubleValue());
            routeBean.setmPath(path);
            if (routeBean.getDirection() == 0) {
                routeBean.setmFrontPath(setArrowPath(path, (float) startRoutePoint.getWorld_x().doubleValue(), (float) startRoutePoint.getWorld_y().doubleValue()
                        , (float) endRoutePoint.getWorld_x().doubleValue(), (float) endRoutePoint.getWorld_y().doubleValue()));
            } else if (routeBean.getDirection() == 1) {
                routeBean.setmBackPath(setArrowPath(path, (float) endRoutePoint.getWorld_x().doubleValue(), (float) endRoutePoint.getWorld_y().doubleValue()
                        , (float) startRoutePoint.getWorld_x().doubleValue(), (float) startRoutePoint.getWorld_y().doubleValue()
                ));
                routeBean.setmFrontPath(setArrowPath(path, (float) startRoutePoint.getWorld_x().doubleValue(), (float) startRoutePoint.getWorld_y().doubleValue()
                        , (float) endRoutePoint.getWorld_x().doubleValue(), (float) endRoutePoint.getWorld_y().doubleValue()));
            }


            routeBean.setName(mContext.getString(R.string.navigation_route) + (k + 1)); //虚拟墙从1开始命名
        }

        refresh();

    }

    public List<RouteBean> getRoutesList() {
        return mRoutesList;
    }


    public void initRobotPosition(PositionPointBean positionPointBean) {
        if (mRobotPositionBean != null && positionPointBean != null) {
            if (mRobotPositionBean.getBitmapX() != positionPointBean.getBitmapY()
                    && mRobotPositionBean.getBitmapX() != positionPointBean.getBitmapY()
            ) {
                mRobotPositionBean = positionPointBean;
                mRobotPositionBean.init(mPositionPointSize, mRobotPositionBean.getBitmapX(),
                        mRobotPositionBean.getBitmapY(), mNameBgSize, mNameCloseSize);
            }
        } else {
            mRobotPositionBean = positionPointBean;
            if (mRobotPositionBean != null) {
                mRobotPositionBean.init(mPositionPointSize, mRobotPositionBean.getBitmapX(),
                        mRobotPositionBean.getBitmapY(), mNameBgSize, mNameCloseSize);
            }
        }
        refresh();
    }


    public void initRobotLaserData(List<RobotLaserDataBean> robotLaserDataBeanList) {
        mRobotLaserDataBeanList = robotLaserDataBeanList;
        refresh();
    }


    public void clearRobotLaserData() {
        if (mRobotLaserDataBeanList != null) {
            mRobotLaserDataBeanList.clear();
        }
        refresh();
    }

    public String getCurrentOperatePointName() {
        return mCurrentOperatePointName;
    }


    public void setCurrentOperatePointName(String currentOperatePointName) {
        mCurrentOperatePointName = currentOperatePointName;
        if (mCurrentOperatePoint != null) {
            mCurrentOperatePoint.setPointName(currentOperatePointName);
        }
        refresh();
    }

    public PositionPointBean getCurrentOperatePoint() {
        return mCurrentOperatePoint;
    }


    public void setCurrentOperatePoint(PositionPointBean currentOperatePoint) {
        mCurrentOperatePoint = currentOperatePoint;
        refresh();
    }

    public void setEditOperatePoint(PositionPointBean point) {
        for (PositionPointBean positionPointBean : mPositionPointList) {
            if (positionPointBean.getPointName().equals(point.getPointName()) && point.getType() == positionPointBean.getType()) {
                mCurrentOperatePoint = new PositionPointBean(positionPointBean.getBitmap(), positionPointBean.getType(),
                        mPositionPointSize, positionPointBean.getBitmapX(), positionPointBean.getBitmapY(),
                        positionPointBean.getNameBitmap(),
                        mNameBgSize,
                        mNamePaint,
                        positionPointBean.getPointName(),
                        positionPointBean.getNameCloseBitmap(),
                        mNameCloseSize
                );
                mCurrentOperatePoint.setPointName(positionPointBean.getPointName());
                mCurrentOperatePoint.setBigBitmap(positionPointBean.getBigBitmap());
                mCurrentOperatePoint.setBitmapX(positionPointBean.getBitmapX());
                mCurrentOperatePoint.setBitmapY(positionPointBean.getBitmapY());
                mCurrentOperatePoint.setTheta(positionPointBean.getTheta());
                mCurrentOperatePoint.setItemRotate(positionPointBean.getItemRotate());
                mCurrentOperatePoint.setNeedShowName(positionPointBean.isNeedShowName());

                mCurrentPointOriX = mCurrentOperatePoint.getLocation().x;
                mCurrentPointOriY = mCurrentOperatePoint.getLocation().y;
                mCurrentPointOriPivotX = mCurrentOperatePoint.getPivotX();
                mCurrentPointOriPivotY = mCurrentOperatePoint.getPivotY();
                refresh();
                break;

            }
        }
    }

    public void removeChargePoint() {
        for (PositionPointBean positionPointBean : mPositionPointList) {

            if (positionPointBean.getType() == PositionPointBean.TYPE_CHARGE_POINT) {
                mPositionPointList.remove(positionPointBean);
                break;

            }
        }
    }

    public VirtualWallBean getCurrentOperateWall() {
        return mCurrentOperateWall;
    }

    public void setCurrentOperateWall(VirtualWallBean currentOperateWall) {
        mCurrentOperateWall = currentOperateWall;
    }

    public RouteBean getmCurrentOperateRoute() {
        return mCurrentOperateRoute;
    }

    public void setmCurrentOperateRoute(RouteBean currentOperatRoute) {
        mCurrentOperateRoute = currentOperatRoute;
    }

    public PositionPointBean getRobotPositionBean() {
        return mRobotPositionBean;
    }

    public void setRobotPositionBean(PositionPointBean robotPositionBean) {
        mRobotPositionBean = robotPositionBean;
    }

    /**
     * 获取橡皮擦的点
     */
    public Point getErasePoint() {
        return mErasePoint;
    }

    public boolean isCanUseErase() {
        return mCanUseErase;
    }

    public void setCanUseErase(boolean canUseErase) {
        mCanUseErase = canUseErase;
    }

    public int getErasePaintWidth() {
        return mErasePaintWidth;
    }

    public void setErasePaintWidth(int erasePaintWidth) {
        mErasePaintSize = erasePaintWidth;
        mErasePaintWidth = (int) (mErasePaintSize * mDoodleSizeUnit);
    }

    /**
     * 清空橡皮擦路径
     */
    public void clearErasePoint() {
//        mErasePathList.clear();
        mErasePoint = null;
    }

    /**
     * 返回上一步
     */
    public void onBackPreviousErasePathList(IMapOperateCallback operateCallback) {
//        if (null != mErasePathList && mErasePathList.size() > 0) {
//            mErasePathList.remove(mErasePathList.size() - 1);
//            refresh();
//            if (null != operateCallback) {
//                operateCallback.onEndOperate();
//            }
//        }
    }


    /**
     * 显示指定导航点名称
     *
     * @param
     */
    public void showNavigationPositionPointName(String navName ,int navType) {
        for (PositionPointBean positionPointBean : mPositionPointList) {
            if (positionPointBean.getPointName().equals(navName) && navType == positionPointBean.getType()) {
                positionPointBean.setNeedShowName(true);
                Log.d(TAG, "showNavigationPositionPointName：" + positionPointBean.getPointName());
            } else {
                positionPointBean.setNeedShowName(false);
            }
        }
        refresh();
    }

    /**
     * 隐藏指定导航点的名称
     */
    public void hideNavigationPositionPointName(PositionPointBean navigationPoint) {
        for (PositionPointBean positionPointBean : mPositionPointList) {
            if (positionPointBean.getPointName().equals(navigationPoint.getPointName()) && navigationPoint.getType() == positionPointBean.getType()) {
                positionPointBean.setNeedShowName(false);
                Log.d(TAG, "showNavigationPositionPointName：" + positionPointBean.getPointName());
            }
        }
        refresh();
    }

    /**
     * 显示指定虚拟墙名称
     *
     * @param virtualWallBean
     */
    public void showVirtualWallName(VirtualWallBean virtualWallBean) {
        for (VirtualWallBean wallBean : mVirtualWallList) {
            if (wallBean.getName().equals(virtualWallBean.getName())) {
                wallBean.setNeedShowName(true);
            } else {
                wallBean.setNeedShowName(false);
            }
        }

        refresh();

    }

    /**
     * 显示指定航路名称
     *
     * @param routerName
     */
    public void showRouteName(String routerName) {
        for (RouteBean routeBean : mRoutesList) {
            if (routeBean.getName().equals(routerName)) {
                routeBean.setmNeedShowName(true);
            } else {
                routeBean.setmNeedShowName(false);
            }
        }

        refresh();

    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    /**
     * 刷新地图
     * 触发 onDraw和computeScroll()
     */
    public void refresh() {
        invalidate();
    }

    @Override
    public void setRotation(float rotate) {
        this.mRotate = rotate;
        refresh();
    }

    public OnPositionPointClickListener getOnPositionPointClickListener() {
        return mOnPositionPointClickListener;
    }

    public void setOnPositionPointClickListener(OnPositionPointClickListener
                                                        onPositionPointClickListener) {
        mOnPositionPointClickListener = onPositionPointClickListener;
    }

    public OnInitLocationFinishListener getOnInitLocationFinishListener() {
        return mOnInitLocationFinishListener;
    }

    public void setOnInitLocationFinishListener(OnInitLocationFinishListener
                                                        onInitLocationFinishListener) {
        mOnInitLocationFinishListener = onInitLocationFinishListener;
    }

    public OnVirtualWallClickListener getOnVirtualWallClickListener() {
        return mOnVirtualWallClickListener;
    }

    public void setOnVirtualWallClickListener(OnVirtualWallClickListener
                                                      onVirtualWallClickListener) {
        mOnVirtualWallClickListener = onVirtualWallClickListener;
    }

    public void setOnRouterClickListener(OnRouteClickListener
                                                 onRouteClickListener) {
        mOnRouteClickListener = onRouteClickListener;
    }

    public OnEditNavigationPointFinishListener getOnEditNavigationPointFinishListener() {
        return mOnEditNavigationPointFinishListener;
    }

    public void setOnEditNavigationPointFinishListener(OnEditNavigationPointFinishListener
                                                               onEditNavigationPointFinishListener) {
        mOnEditNavigationPointFinishListener = onEditNavigationPointFinishListener;
    }

    public OnCanvasExceptionListener getOnCanvasExceptionListener() {
        return mOnCanvasExceptionListener;
    }

    public void setOnCanvasExceptionListener(OnCanvasExceptionListener
                                                     onCanvasExceptionListener) {
        mOnCanvasExceptionListener = onCanvasExceptionListener;
    }

    public OnEditedMapListener getOnEditedMapListener() {
        return mOnEditedMapListener;
    }

    public void setOnEditedMapListener(OnEditedMapListener onEditedMapListener) {
        mOnEditedMapListener = onEditedMapListener;
    }

    public interface OnPositionPointClickListener {
        void onClick(PositionPointBean positionPointBean);
    }

    public interface OnInitLocationFinishListener {
        void onFinish(PositionPointBean positionPointBean);
    }

    public interface OnVirtualWallClickListener {
        void onClick(VirtualWallBean virtualWallBean);
    }

    public interface OnRouteClickListener {
        void onClick(RouteBean routeBean);
    }

    public interface OnEditNavigationPointFinishListener {
        void onFinish(PositionPointBean positionPointBean);
    }

    /**
     * 绘制异常
     */
    public interface OnCanvasExceptionListener {
        void canvasException();
    }

    /**
     * 对地图进行了编辑操作
     */
    public abstract static class OnEditedMapListener {
        public abstract void onEditedMap();

        public boolean onEditedMapIntercept() {
            return false;
        }

    }

    public boolean isEnableBitmapRegion() {
        return enableBitmapRegion;
    }

    public void setEnableBitmapRegion(boolean enableBitmapRegion) {
        this.enableBitmapRegion = enableBitmapRegion;
    }

    public boolean isEnableBitmapChache() {
        return enableBitmapChache;
    }

    public void setEnableBitmapChache(boolean enableBitmapChache) {
        this.enableBitmapChache = enableBitmapChache;
    }


    /**
     * 设置触摸点坐标（目前用于将机器人位置设为导航点）
     *
     * @param bitmapX 图片上的x坐标
     * @param bitmapY 图片上的y坐标
     */
    public void setTouchPosition(float bitmapX, float bitmapY) {
        this.mTouchX = toTouchX(bitmapX);
        this.mTouchY = toTouchY(bitmapY);
    }

    private double localTheta = 0;

    /**
     * 设置角度（目前用于将机器人位置设为导航点）
     *
     * @param localTheta
     */
    public void setPositionLocalTheta(double localTheta) {
        this.localTheta = localTheta;
    }

    /**
     * 模拟点击（目前用于将机器人位置设为导航点|充电点）
     */
    public boolean simulateClick() {
        switch (mEditType) {
            case TYPE_EDIT_TYPE_ADD_CHARGE:
                createNewChargePoint();
                break;
            case TYPE_EDIT_TYPE_ADD_NAVIGATION:
                createNewNavigationPoint();
                break;
            case TYPE_EDIT_TYPE_ADD_INIT_POINT:
                createNewInitPoint();
                break;
            case TYPE_EDIT_TYPE_ADD_STANDBY_POINT:
                createNewStandbyPoint();
                break;
        }

        if (getCurrentOperatePoint() == null) {//没有添加成功
            return false;
        }

        return true;
    }

    /**
     * 画箭头
     *
     * @param startX 起点x
     * @param startY 起点y
     * @param endX   结束点x
     * @param endY   结束点y
     */
    public Path setArrowPath(Path routePath, float startX, float startY, float endX, float endY) {
        double H = 10; // 箭头高度
        double L = 3.5; // 底边的一半

        double angle = Math.atan(L / H); // 箭头角度
        double arrowLength = Math.sqrt(L * L + H * H); // 箭头的长度
        //箭头就是个三角形，我们已经有一个点了，根据箭头的角度和长度，确定另外2个点的位置
        double[] point1 = rotateVec(endX - startX, endY - startY, angle, arrowLength);
        double[] point2 = rotateVec(endX - startX, endY - startY, -angle, arrowLength);
        double point1_x = endX - point1[0];
        double point1_y = endY - point1[1];
        double point2_x = endX - point2[0];
        double point2_y = endY - point2[1];
        int x3 = (int) point1_x;
        int y3 = (int) point1_y;
        int x4 = (int) point2_x;
        int y4 = (int) point2_y;
        // 画线
        routePath.moveTo(endX, endY);
        routePath.lineTo(x3, y3);
        routePath.lineTo(x4, y4);
        routePath.close();//封闭当前Path，连接起点和终点
        return routePath;
    }

    // 计算

    /**
     * @param diffX       X的差值
     * @param diffY       Y的差值
     * @param angle       箭头的角度（箭头三角形的线与直线的角度）
     * @param arrowLength 箭头的长度
     */
    public double[] rotateVec(float diffX, float diffY, double angle, double arrowLength) {
        double arr[] = new double[2];
        // 下面的是公式，得出的是以滑动出的线段末点为中心点旋转angle角度后,线段起点的坐标，这个旋转后的线段也就是“变长了的箭头的三角形的一条边”
        //推导见注释1
        double x = diffX * Math.cos(angle) - diffY * Math.sin(angle);
        double y = diffX * Math.sin(angle) + diffY * Math.cos(angle);
        double d = Math.sqrt(x * x + y * y);
        //根据相似三角形，得出真正的箭头三角形顶点坐标，这里见注释2
        x = x / d * arrowLength;
        y = y / d * arrowLength;
        arr[0] = x;
        arr[1] = y;
        return arr;
    }

    public void  setOnDrawListener(IOnDrawListener iOnDrawListener ){
        this.mOnDrawListener = iOnDrawListener;
    }

    public interface  IOnDrawListener{

       void onDraw(Canvas canvas);
    }


    public void  setOnViewTouchListener(ITouchListener iTouchListener ){
        this.mTouchListener = iTouchListener;
    }



    public interface  ITouchListener{


        void onScrollBegin(MotionEvent e,float bitmapX,float bitmapY);

        void onScrollEnd(MotionEvent e,float bitmapX,float bitmapY);


        void onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY,float bitmapX,float bitmapY);


        void onSingleTapUp(MotionEvent e,float bitmapX,float bitmapY);

        void onUpOrCancel(MotionEvent e,float bitmapX,float bitmapY);
    }
}


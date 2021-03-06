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
 * ??????
 * Created by maqing 2018/11/13 11:11
 * Email???2856992713@qq.com
 */
public class MapDrawView extends View {
    /**
     * ???????????????
     */
    private Context mContext;
    /**
     * ??????
     */
    private Paint mPaint;
    /**
     * ????????????
     */
    private int mPaintColor = Color.RED;
    /**
     * ????????????
     */
    private float mPainStrokeWidth = DEFAULT_SIZE;
    public final static int DEFAULT_SIZE = 6; // ??????????????????
    /**
     * ???????????????
     */
    private float mPositionPointSize = DEFAULT_POSITION_POINT_SIZE;

    /**
     * ?????????????????????
     */
    private float mInitPositionPointBigSize = DEFAULT_POSITION_POINT_BIG_SIZE;
    /**
     * ???????????????
     */
    private float mInitScale;

    /**
     * ?????????????????????
     */
    public static final float DEFAULT_POSITION_POINT_SIZE = 10;
    /**
     * ??????????????????
     */
    public static final float DEFAULT_POSITION_POINT_BIG_SIZE = 88;
    /**
     * ????????????
     */
    private float mNameBgSize = DEFAULT_NAME_BG_SIZE;
    /**
     * ??????????????????
     */
    public static final float DEFAULT_NAME_BG_SIZE = 130;

    /**
     * ????????????????????????
     */
    private float mNameCloseSize = DEFAULT_NAME_CLOSE_SIZE;
    /**
     * ??????????????????????????????
     */
    public static final float DEFAULT_NAME_CLOSE_SIZE = 20;

    /**
     * ?????????????????????
     */
    private TouchGestureDetector mTouchGestureDetector;
    /**
     * ?????????????????????
     */
    private boolean mIsScale;

    /**
     * ?????????????????????
     */
    private List<VirtualWallBean> mVirtualWallList = new ArrayList<>();

    /**
     * ??????????????????????????????
     */
    private Path mCurrentPath;
    /**
     * ????????????????????????
     */
    private VirtualWallBean mCurrentOperateWall;
    /**
     * ???????????????
     */
    private Paint mVirtualWallPaint;
    private float mVirtualPaintStrokeSize = VIRTUAL_WALL_PAINT_STROKE_WIDTH;
    public static final float VIRTUAL_WALL_PAINT_STROKE_WIDTH = 1.5f;

    /**
     * ????????????
     */
    private List<RouteBean> mRoutesList = new ArrayList<>();

    /**
     * ???????????????????????????
     */
    private Path mCurrentRoutePath;

    /**
     * ?????????????????????
     */
    private RouteBean mCurrentOperateRoute;

    /**
     * ????????????
     */
    private Paint mRoutePaint;
    private float mRouteStrokeSize = NAVIGATION_ROUTE_PAINT_STROKE_WIDTH;
    public static final float NAVIGATION_ROUTE_PAINT_STROKE_WIDTH = 5.0f;


    private Bitmap mBitmap;
    private int bitmapWidth;
    private int bitmapHeight;

    private float mCenterScale; // ????????????????????????????????????
    private int mCenterHeight, mCenterWidth;// ?????????????????????????????????View??????????????????????????????
    private float mCentreTranX, mCentreTranY;// ?????????????????????????????????????????????????????????View??????????????????????????????

    private float mScale = 1; // ??????????????????????????????????????????????????? ??? ?????????????????????????????? mCenterScale*mScale ???
    private float mTransX = 0, mTransY = 0; // ????????????????????????????????????????????????????????????????????? ????????????????????????mCentreTranX + mTransX???View??????????????????????????????
    private float mMinScale = MIN_SCALE; //??????????????????
    private float mMaxScale = MAX_SCALE; //??????????????????
    public final static float MAX_SCALE = 4f; // ??????????????????
    public final static float MIN_SCALE = 0.25f; // ??????????????????

    private float pendingX, pendingY, pendingScale = 1;

    // ?????????????????????
    private float mTouchX, mTouchY;
    private float mLastTouchX, mLastTouchY;
    private float mTouchDownX, mTouchDownY;

    // ????????????????????????
    private Float mLastFocusX;
    private Float mLastFocusY;
    private float mTouchCentreX, mTouchCentreY;

    // ????????????
    private ValueAnimator mScaleAnimator;
    private float mScaleAnimTransX, mScaleAnimTranY;
    private ValueAnimator mTranslateAnimator;
    private float mTransAnimOldY, mTransAnimY;


    /**
     * ?????????
     */
    private List<PositionPointBean> mPositionPointList = new ArrayList<>();
    /**
     * ??????????????????
     */
    private PositionPointBean mSelectedPoint;

    private float mSelectedPointOriX, mSelectedPointOriY;

    /**
     * ????????????????????????????????????
     */
    private float mSelectedPointOriPivotX, mSelectedPointOriPivotY;

    /**
     * ???????????????
     */
    private PositionPointBean mCurrentOperatePoint;

    private float mCurrentPointOriX, mCurrentPointOriY;
    /**
     * ?????????????????????????????????
     */
    private boolean mIsClickOnCurrentOperatePoint;

    /**
     * ????????????????????????????????????
     */
    private float mCurrentPointOriPivotX, mCurrentPointOriPivotY;

    private String mCurrentOperatePointName = "";

    /**
     * ????????????????????????
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

    private float mDoodleSizeUnit = 1; // ????????????????????????????????????????????????????????????????????????????????????dp??????????????????????????????????????????????????????

    private boolean mIsmipmapOutside = false; // ???????????????????????????????????????????????????

    /**
     * ???????????? 0??????????????????
     */
    private int mEditType = TYPE_EDIT_INIT;
    /**
     * ???????????? 0????????? 1 ?????????
     */
    private int mRouteType = -1;
    /**
     * ????????????????????????????????????
     */
    private boolean isChoosenPointMode;
    /**
     * ??????????????????
     */
    public static final int TYPE_EDIT_INIT = -1;
    /**
     * ???????????????
     */
    public static final int TYPE_EDIT_TYPE_ADD_INIT_POINT = 0;
    /**
     * ???????????????
     */
    public static final int TYPE_EDIT_TYPE_ADD_NAVIGATION = 1;
    /**
     * ???????????????
     */
    public static final int TYPE_EDIT_TYPE_ADD_CHARGE = 2;
    /**
     * ???????????????
     */
    public static final int TYPE_EDIT_TYPE_VIRTUAL_WALL = 3;
    /**
     * ????????????
     */
    public static final int TYPE_EDIT_TYPE_NAVIGATION_ROUTE = 9;
    /**
     * ???????????????
     */
    public static final int TYPE_EDIT_TYPE_ADD_STANDBY_POINT = 8;
    /**
     * ?????????????????????????????????
     */
    public static final int TYPE_EDIT_PATH_PLAN = 4;
    /**
     * ???????????????
     */
    public static final int TYPE_EDIT_TYPE_INIT_LOCATION = 5;
    /**
     * ?????????
     */
    public static final int TYPE_EDIT_ERASE = 6;
    /**
     * ???????????????
     */
    public static final int TYPE_EDIT_TYPE_EDIT_POINT = 7;
    /**
     * ???????????????
     */
    private Paint mErasePaint;
    /**
     * ???????????????(??????)
     */
    private int mErasePaintWidth;
    private int mErasePaintSize = DEFAULT_ERASE_PAINT_WIDTH;
    public static final int DEFAULT_ERASE_PAINT_WIDTH = 5;
    /**
     * ?????????????????????
     */
//    private List<ErasePathBean> mErasePathList = new ArrayList<>();
    /**
     * ????????????????????????
     */
//    private ErasePathBean mCurrentErasePathBean;
    /**
     * ???????????????
     */
//    private Path mErasePath;
    /**
     * ????????????
     */
    private Point mErasePoint;

//    private Bitmap mEraseBitmap;

//    private Canvas mEraseCanvas;

    private boolean mCanUseErase = false;

    private Paint mEraseIconPaint;
    private Paint mEraseIconBgPaint;

    private int[] colors = new int[]{Color.parseColor("#00FFFFFF"), Color.WHITE};

    /**
     * ???????????????
     */
    private PositionPointBean mRobotPositionBean;

    /**
     * ????????????????????????
     */
    private List<RobotLaserDataBean> mRobotLaserDataBeanList = new ArrayList<>();
    private Paint mLaserPaint;
    public static final int LASER_PAINT_STROKE_WIDTH = 3;
    /**
     * ?????????????????????
     */
    private OnPositionPointClickListener mOnPositionPointClickListener;
    /**
     * ?????????????????????
     */
    private OnInitLocationFinishListener mOnInitLocationFinishListener;
    /**
     * ?????????????????????
     */
    private OnVirtualWallClickListener mOnVirtualWallClickListener;
    /**
     * ??????????????????
     */
    private OnRouteClickListener mOnRouteClickListener;
    /**
     * ???????????????????????????
     */
    private OnEditNavigationPointFinishListener mOnEditNavigationPointFinishListener;
    /**
     * ??????????????????
     */
    private OnCanvasExceptionListener mOnCanvasExceptionListener;
    /**
     * ??????????????????????????????
     */
    private OnEditedMapListener mOnEditedMapListener;

    /**
     * ?????????????????????
     */
    private boolean mIsLongPress; //?????????????????????

    /**
     * ???????????????
     */
    private boolean mIsMoved;
    /**
     * ???????????????
     */
    private boolean mIsReleased;
    /**
     * ??????????????????????????????????????????????????????longpress???????????????
     */
    private int mCounter;

    private IOnDrawListener mOnDrawListener;

    private ITouchListener mTouchListener;

    /**
     * ?????????runnable
     */
    private Runnable mLongPressRunnable = new Runnable() {
        @Override
        public void run() {
            mCounter--;
            //???????????????0????????????????????????Runnable??????????????????down????????????
            if (mCounter > 0 || mIsReleased || mIsMoved) return;
            Log.d(TAG, "?????????");

            if (mCurrentOperatePoint != null) {  //????????????????????????????????????
                if (mCurrentOperatePoint.contains(toX(mTouchX), toY(mTouchY))) {

                    Log.d(TAG, "?????????" + getAllScale() + "," + mInitPositionPointBigSize);

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
     * ?????????????????????????????????
     */
    private boolean mOtherPointDown;

    /**
     * 1?????????????????????(???????????????????????????????????????????????????)
     */
    private Runnable mOnePointDownRunnable = new Runnable() {

        @Override
        public void run() {
            Log.e("pointInfo", "mOnePointDownRunnable() ??????run??????");
            mOnePointCounter--;
            //???????????????0????????????????????????Runnable??????????????????down????????????
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

                case TYPE_EDIT_TYPE_NAVIGATION_ROUTE://????????????
                    mCounter++;
                    mIsReleased = false;
                    mIsMoved = false;
                    createNewRoute();
                    invalidate();
                    break;

                case TYPE_EDIT_TYPE_ADD_INIT_POINT: //???????????????

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
                    //??????????????????
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

                case TYPE_EDIT_TYPE_ADD_NAVIGATION:  //?????????????????????
                    createNewNavigationPoint();

                    //????????????
                    mCounter++;
                    mIsReleased = false;
                    mIsMoved = false;
                    postDelayed(mLongPressRunnable, ViewConfiguration.getLongPressTimeout());

                    invalidate();

                    break;

                case TYPE_EDIT_TYPE_EDIT_POINT:  //?????????????????????

                    if (mCurrentOperatePoint != null && mCurrentOperatePoint.contains(toX(mTouchX), toY(mTouchY))) { //??????????????????????????????
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
        if (mCurrentOperatePoint == null) { //???????????????????????????

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
            if (mCurrentOperatePoint.contains(toX(mTouchX), toY(mTouchY))) {  //?????????????????????
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
     * ????????????
     * <p>
     * ???????????????
     * ??????????????????
     * </p>
     */
    private void createNewRoute() {

        for (int i = 0; i < mPositionPointList.size(); i++) {
            PositionPointBean navigationPointBean = mPositionPointList.get(i);
            if (navigationPointBean.contains(toX(mTouchX), toY(mTouchY)) && navigationPointBean.getType() == PositionPointBean.TYPE_NAVIGATION_POINT) {
                //??????????????????
                Log.w("createNewRoute", "????????????????????????");
                if (mCurrentOperateRoute == null) {
                    mCurrentOperateRoute = new RouteBean();
                    if (mCurrentOperateRoute.getPositions() == null) {
                        mCurrentOperateRoute.setPositions(new ArrayList<RouteBean.PositionsDTO>());
                    }
                }

                if (mCurrentOperateRoute.getPositions().size() < 2) {
                    setRouteXY(navigationPointBean);
                }
                //???????????????
                else {
                    mCurrentOperateRoute.getPositions().clear();
                    setRouteXY(navigationPointBean);
                }
                //?????????????????????????????????
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
            Log.e("createNewRoute", "????????????");
            mCurrentOperateRoute.setDirection(0);
        } else if (mRouteType == 1) {
            Log.e("createNewRoute", "????????????");
            mCurrentOperateRoute.setDirection(1);
        }
        if (mCurrentOperateRoute.getPositions().size() == 2) {
            setRouteArrawParams();
        } else {
            mCurrentOperateRoute.setmPath(null);
        }


    }

    /**
     * ???RouteBean????????????????????????
     */
    private void setRouteArrawParams() {
        RouteBean.PositionsDTO startPosition = mCurrentOperateRoute.getPositions().get(0);
        RouteBean.PositionsDTO endPosition = mCurrentOperateRoute.getPositions().get(1);
        //??????????????? isFirstChoosenPoint == false
        Path path = new Path();
        path.moveTo((float) startPosition.getWorld_x().doubleValue(), (float) startPosition.getWorld_y().doubleValue());//?????????
        path.lineTo((float) endPosition.getWorld_x().doubleValue(), (float) endPosition.getWorld_y().doubleValue());//?????????
        mCurrentOperateRoute.setmPath(path);
        //?????????
        if (mCurrentOperateRoute.getDirection() == 0) {
            mCurrentOperateRoute.setmFrontPath(setArrowPath(path, (float) startPosition.getWorld_x().doubleValue(), (float) startPosition.getWorld_y().doubleValue()
                    , (float) endPosition.getWorld_x().doubleValue(), (float) endPosition.getWorld_y().doubleValue()));
        }
        //?????????
        else if (mCurrentOperateRoute.getDirection() == 1) {
            mCurrentOperateRoute.setmBackPath(setArrowPath(path, (float) endPosition.getWorld_x().doubleValue(), (float) endPosition.getWorld_y().doubleValue()
                    , (float) startPosition.getWorld_x().doubleValue(), (float) startPosition.getWorld_y().doubleValue()
            ));
            mCurrentOperateRoute.setmFrontPath(setArrowPath(path, (float) startPosition.getWorld_x().doubleValue(), (float) startPosition.getWorld_y().doubleValue()
                    , (float) endPosition.getWorld_x().doubleValue(), (float) endPosition.getWorld_y().doubleValue()));
        }
    }

    /**
     * ?????????????????????
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
     * ?????????????????????
     */
    private void createNewInitPoint() {

        if (mCurrentOperatePoint == null) { //???????????????????????????

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
            if (mCurrentOperatePoint.contains(toX(mTouchX), toY(mTouchY))) {  //?????????????????????
                mIsClickOnCurrentOperatePoint = true;
//                            mCurrentOperatePoint.setNeedShowName(!mCurrentOperatePoint.isNeedShowName());
            }
        }
    }

    /**
     * ?????????????????????
     */
    private void createNewChargePoint() {
        if (mCurrentOperatePoint == null) { //???????????????????????????

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
            if (mCurrentOperatePoint.contains(toX(mTouchX), toY(mTouchY))) {  //?????????????????????
                mIsClickOnCurrentOperatePoint = true;
//                            mCurrentOperatePoint.setNeedShowName(!mCurrentOperatePoint.isNeedShowName());
            }
        }
    }

    /**
     * ?????????????????????
     */
    private void createNewNavigationPoint() {
        Bitmap pointBitmap;
        Bitmap nameCloseBitmap;
        if (mCurrentOperatePoint == null) { //???????????????????????????
            //???????????????
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

            if (mCurrentOperatePoint != null && mCurrentOperatePoint.contains(toX(mTouchX), toY(mTouchY))) { //??????????????????????????????
                mIsClickOnCurrentOperatePoint = true;
//                            mCurrentOperatePoint.setNeedShowName(!mCurrentOperatePoint.isNeedShowName());
            } else { //?????????????????????????????????
                mCurrentOperatePoint = null;
                createNewNavigationPoint();
            }

        }
    }

    /**
     * ????????????
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
        // ????????????
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
            public boolean onScale(ScaleGestureDetectorApi27 detector, MotionEvent event) { /* ??????????????????*/
//                Log.d(TAG, "onScale");
                mTouchCentreX = detector.getFocusX();
                mTouchCentreY = detector.getFocusY();
                if (mLastFocusX != null && mLastFocusY != null) { /* ????????????*/
                    float dx = mTouchCentreX - mLastFocusX;
                    float dy = mTouchCentreY - mLastFocusY;  /*????????????*/
                    if (Math.abs(dx) > 1 || Math.abs(dy) > 1) {
                        setTranslationX(mTransX + dx + pendingX);
                        setTranslationY(mTransY + dy + pendingY);
                        pendingX = pendingY = 0;
                    } else {
                        pendingX += dx;
                        pendingY += dy;
                    }
                }
                /* ????????????*/
                if (Math.abs(1 - detector.getScaleFactor()) > 0.005f) { /* ????????????*/
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
                ) {  //????????????????????????
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
                mIsMoved = false;//??????????????????

//                if (toX(e.getX()) > 0 && toX(e.getX()) < mBitmap.getWidth()
//                        &&
//                        toY(e.getY()) > 0 && toY(e.getY()) < mBitmap.getHeight()
//                        ) { //????????????????????????

                mLastTouchX = mTouchX;
                mLastTouchY = mTouchY;
                mTouchX = e.getX();
                mTouchY = e.getY();

                switch (mEditType) {
                    case TYPE_EDIT_TYPE_ADD_STANDBY_POINT:
                    case TYPE_EDIT_TYPE_ADD_INIT_POINT:
                        if (mCurrentOperatePoint != null && !mIsLongPress) { //????????????????????????????????????????????????????????????????????????????????????????????????
                            mCurrentPointOriX = mCurrentOperatePoint.getLocation().x;
                            mCurrentPointOriY = mCurrentOperatePoint.getLocation().y;
                            mCurrentPointOriPivotX = mCurrentOperatePoint.getPivotX();
                            mCurrentPointOriPivotY = mCurrentOperatePoint.getPivotY();
                        }
                        break;

                    case TYPE_EDIT_TYPE_ADD_CHARGE:
                        if (mCurrentOperatePoint != null && !mIsLongPress) { //????????????????????????????????????????????????????????????????????????????????????????????????
                            mCurrentPointOriX = mCurrentOperatePoint.getLocation().x;
                            mCurrentPointOriY = mCurrentOperatePoint.getLocation().y;
                            mCurrentPointOriPivotX = mCurrentOperatePoint.getPivotX();
                            mCurrentPointOriPivotY = mCurrentOperatePoint.getPivotY();
                        }
                        break;

                    case TYPE_EDIT_TYPE_ADD_NAVIGATION:
                        if (mCurrentOperatePoint != null && !mIsLongPress) { //????????????????????????????????????????????????????????????????????????????????????????????????
                            mCurrentPointOriX = mCurrentOperatePoint.getLocation().x;
                            mCurrentPointOriY = mCurrentOperatePoint.getLocation().y;
                            mCurrentPointOriPivotX = mCurrentOperatePoint.getPivotX();
                            mCurrentPointOriPivotY = mCurrentOperatePoint.getPivotY();
                        }
                        break;

                    case TYPE_EDIT_TYPE_EDIT_POINT:
                        if (mCurrentOperatePoint != null && !mIsLongPress) {//????????????????????????????????????????????????????????????????????????????????????????????????
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
                mIsMoved = true; //?????????????????????

                if (toX(e2.getX()) > 0 && toX(e2.getX()) < bitmapWidth
                        &&
                        toY(e2.getY()) > 0 && toY(e2.getY()) < bitmapHeight
                ) { //????????????????????????

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

                            if (mSelectedPoint != null && mIsLongPress) {  //????????????????????????
                                mSelectedPoint.setItemRotate(AngleUtil.computeAngle(mSelectedPointOriPivotX, mSelectedPointOriPivotY, toX(mTouchX), toY(mTouchY)));
                                mSelectedPoint.setTheta(AngleUtil.calculateDirectionAngle(mSelectedPointOriPivotX, mSelectedPointOriPivotY, toX(mTouchX), toY(mTouchY)));
                            } else if (mCurrentOperatePoint != null && !mIsLongPress && mIsClickOnCurrentOperatePoint) {  //???????????????????????????????????????
                                mCurrentOperatePoint.setLocation(mCurrentPointOriX + toX(mTouchX) - toX(mTouchDownX),
                                        mCurrentPointOriY + toY(mTouchY) - toY(mTouchDownY), true);
                            }
                            break;

                        case TYPE_EDIT_TYPE_INIT_LOCATION:
                            if (mSelectedPoint != null) {//??????????????? ???mSelectedPoint ???????????????????????????
                                Log.e("pointInfo", "mSelectedPoint====setItemRotate &&setTheta");
                                mSelectedPoint.setItemRotate(AngleUtil.computeAngle(mSelectedPointOriPivotX, mSelectedPointOriPivotY, toX(mTouchX), toY(mTouchY)));
                                mSelectedPoint.setTheta(AngleUtil.calculateDirectionAngle(mSelectedPointOriPivotX, mSelectedPointOriPivotY, toX(mTouchX), toY(mTouchY)));
                            }
                            break;

                        case TYPE_EDIT_TYPE_VIRTUAL_WALL: /*???????????????*/
                            if (mCurrentPath != null) {
                                mCurrentOperateWall.getWallPointList().add(new VirtualWallBean.WallPoint(toX(mTouchX), toY(mTouchY))); /*????????????*/
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
                            if (mCanUseErase && mErasePoint != null) { /*????????????*/
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
                        Log.e("pointInfo", "????????????" + mCurrentOperatePoint.getPointName() + "*******" + positionPointBean.getPointName());
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
                ) { //????????????????????????
                    Log.e("pointInfo", "????????????????????????=====???onePointClick");
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
//                Log.d(TAG, "onPointerMove???" + e.getPointerCount() + "");
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

                //????????????????????????????????????
                boolean mIsOperatePointInPointList = false;

                //?????????????????????????????????
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
                                    if (!isChoosenPointMode) {//???????????????
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
                                    mOnInitLocationFinishListener.onFinish(mCurrentOperatePoint);//??????UI??????
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

//                //?????????????????????
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

        // ?????????????????????????????????
        // ????????????????????????????????????????????????????????????1???????????????0?????????????????????????????????????????????????????????
        mTouchGestureDetector.setScaleSpanSlop(1);  // ????????????????????????????????????????????????????????????
        mTouchGestureDetector.setScaleMinSpan(1);  // ????????????????????????????????????????????????????????????
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

        boolean consumed = mTouchGestureDetector.onTouchEvent(transformedEvent); //??????????????????????????????
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
            canvas.translate(getAllTranX(), getAllTranY()); //????????????
            float scale = getAllScale();
            canvas.scale(scale, scale); //????????????
            canvas.save();

            Bitmap drawBitmap;
            if (enableBitmapRegion) {
                // ??????????????????
                if (mRect == null) {
                    mRect = new Rect(0, 0, canvas.getWidth(), canvas.getHeight());
                }
                drawBitmap = mDecoder.decodeRegion(mRect, mOptions);
            } else {
                drawBitmap = mBitmap;
            }

            //????????????
            if (drawBitmap != null) {
                canvas.save();
                canvas.scale((float) originScale, (float) originScale);
                canvas.drawBitmap(drawBitmap, 0, 0, null);
                canvas.restore();
            }

            if (!mIsmipmapOutside && drawBitmap != null) { //?????????????????????????????????
                canvas.clipRect(0, 0, bitmapWidth, bitmapHeight);
            }

            switch (mEditType) {
                case TYPE_EDIT_ERASE:

                    //?????????????????????
//                    for (ErasePathBean erasePathBean : mErasePathList) {
//                        Log.i("LEOO", "erasePathBean >>" + erasePathBean.getPaintStrokeWidth());
//                        mErasePaint.setStrokeWidth(erasePathBean.getPaintStrokeWidth());
//                        Path erasePath = erasePathBean.getPath();
//                        canvas.drawPath(erasePath, mErasePaint);
//                    }

                    //?????????????????????
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
                     * ?????????????????????
                     Path testPath = new Path();
                     testPath.moveTo((float)100, (float)100);
                     testPath.lineTo((float)200, (float)200);
                     canvas.drawPath(testPath, mRoutePaint);
                     */
                    //????????????
//                    Log.e("routelog", "????????????= " + mRoutesList.size());
                    for (int i = 0; i < mRoutesList.size(); i++) {
                        canvas.save();
                        RouteBean routeBean = mRoutesList.get(i);
                        //"direction":1, (0:????????????  ??? 1??????????????? )
                        if (routeBean.getDirection() == 1) {
                            Log.e("routelog", "????????????");
                            canvas.drawPath(routeBean.getmPath(), mRoutePaint);
                            canvas.drawPath(routeBean.getmFrontPath(), mRoutePaint);
                            canvas.drawPath(routeBean.getmBackPath(), mRoutePaint);
                        } else if (routeBean.getDirection() == 0) {
                            Log.e("routelog", "????????????");
                            canvas.drawPath(routeBean.getmPath(), mRoutePaint);
                            canvas.drawPath(routeBean.getmFrontPath(), mRoutePaint);

//                            // ??????????????????
//                            canvas.drawPath( setArrowPath(routeBean.getmPath(),200,200,50,50) , mRoutePaint);//??????->?????? ??????
//                            canvas.drawPath( setArrowPath(routeBean.getmPath(),50,50,200,200) , mRoutePaint);//??????->?????? ??????
                        }
                        canvas.restore();
                    }

                    if (mCurrentOperateRoute != null && mCurrentOperateRoute.getmPath() != null) {
                        canvas.drawPath(mCurrentOperateRoute.getmPath(), mRoutePaint);

                        if (mCurrentOperateRoute.getDirection() == 1) {
                            Log.e("routelog", "????????????");
                            canvas.drawPath(mCurrentOperateRoute.getmPath(), mRoutePaint);
                            canvas.drawPath(mCurrentOperateRoute.getmFrontPath(), mRoutePaint);
                            canvas.drawPath(mCurrentOperateRoute.getmBackPath(), mRoutePaint);
                        } else if (mCurrentOperateRoute.getDirection() == 0) {
                            Log.e("routelog", "????????????");
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
                            canvas.translate(positionPointBean.getLocation().x, positionPointBean.getLocation().y); //??????????????????????????????item????????????
                            canvas.rotate(positionPointBean.getItemRotate(), positionPointBean.getPivotX() - positionPointBean.getLocation().x,
                                    positionPointBean.getPivotY() - positionPointBean.getLocation().y);
                            canvas.drawBitmap(positionPointBean.getBitmap(), positionPointBean.getSrcRect(), positionPointBean.getDstRect(), null);
                            canvas.restore();
                        }
                    }


                    if (mRobotPositionBean != null) {
                        canvas.save();
                        canvas.translate(mRobotPositionBean.getLocation().x, mRobotPositionBean.getLocation().y); //??????????????????????????????item????????????
                        canvas.rotate(mRobotPositionBean.getItemRotate(), mRobotPositionBean.getPivotX() - mRobotPositionBean.getLocation().x,
                                mRobotPositionBean.getPivotY() - mRobotPositionBean.getLocation().y);
                        canvas.drawBitmap(mRobotPositionBean.getBitmap(), mRobotPositionBean.getSrcRect(), mRobotPositionBean.getDstRect(), null);
                        canvas.restore();
                    }

                    //?????????????????????
                    if (mCurrentOperatePoint != null) {
                        canvas.save();

                        if (mSelectedPoint != null
                                && mSelectedPoint.getPointName().equals(mCurrentOperatePoint.getPointName())
                                && mSelectedPoint.getType() == mCurrentOperatePoint.getType()
                        ) {
                            canvas.save();
                            canvas.translate(mSelectedPoint.getLocation().x, mSelectedPoint.getLocation().y); //??????????????????????????????item????????????
                            canvas.rotate(mSelectedPoint.getItemRotate(), mSelectedPoint.getPivotX() - mSelectedPoint.getLocation().x,
                                    mSelectedPoint.getPivotY() - mSelectedPoint.getLocation().y); //???????????????

                            canvas.drawBitmap(mSelectedPoint.getBitmap(), mSelectedPoint.getSrcRect(), mSelectedPoint.getDstRect(), null);
                            canvas.restore();
                        } else {

                            canvas.translate(mCurrentOperatePoint.getLocation().x, mCurrentOperatePoint.getLocation().y); // ??????????????????????????????item????????????

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
                    canvas.translate(positionPointBean.getNameLocation().x, positionPointBean.getNameLocation().y); // ??????????????????????????????item????????????

                    if (positionPointBean.isNeedShowName()) {
                        //?????????????????????
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

                    //????????????????????????
                    canvas.save();
                    canvas.translate(positionPointBean.getNameCloseLocation().x, positionPointBean.getNameCloseLocation().y); // ??????????????????????????????item????????????
                    if (positionPointBean.isNeedShowName()) {
                        //?????????????????????
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

            //???????????????????????????
            if (mCurrentOperatePoint != null
                    && mCurrentOperatePoint.isNeedShowName()
            ) {
                canvas.save();
                canvas.translate(mCurrentOperatePoint.getNameLocation().x, mCurrentOperatePoint.getNameLocation().y); // ??????????????????????????????item????????????
                if (mCurrentOperatePoint.isNeedShowName()) {
                    //?????????????????????
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

                //????????????????????????
                canvas.save();
                canvas.translate(mCurrentOperatePoint.getNameCloseLocation().x, mCurrentOperatePoint.getNameCloseLocation().y); // ??????????????????????????????item????????????
                if (mCurrentOperatePoint.isNeedShowName()) {
                    //?????????????????????
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

            //??????????????????
            for (int i = 0; i < mRoutesList.size(); i++) {
                RouteBean mRouteBean = mRoutesList.get(i);
                if (mRouteBean.ismNeedShowName()) {
                    //??????????????????
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
     * ??????????????????
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

    //????????????bitmap
    private void performDraw() {
        this.originScale = originSize.inSampleSize;

        initBitmap(originSize.orginWidth, originSize.originHeight);
        Log.d(TAG, "setBitmap refresh");

        //????????????????????????
        if (enableBitmapRegion) {
            // ????????????
            mOptions.inMutable = true;
            // ???????????????RGB565
            mOptions.inPreferredConfig = Bitmap.Config.RGB_565;
            // ??????????????????   // ?????????bitmap????????????????????????bitmap????????????
            mOptions.inBitmap = mBitmap;
            mOptions.inSampleSize = originSize.inSampleSize;

            // ???????????????
            try {
                mDecoder = BitmapRegionDecoder.newInstance(originSize.path, false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        refresh();
    }

    private void initBitmap(int w, int h) { //??????resize preview
        if (mBitmap == null || mBitmap.isRecycled()) {
            return;
        }

        bitmapWidth = w;
        bitmapHeight = h;

        mRotate = 0;

        Log.d(TAG, "initBitmap???" + w + "," + h);

        float nw = w * 1f / getWidth();//??????????????????????????????
        float nh = h * 1f / getHeight();//??????????????????????????????
        if (nw > nh) {//??????????????????????????????????????????????????????????????????????????????
            mCenterScale = 1 / nw;
            mCenterWidth = getWidth();
            mCenterHeight = (int) (h * mCenterScale);//?????????????????????
        } else {
            mCenterScale = 1 / nh;
            mCenterWidth = (int) (w * mCenterScale);
            mCenterHeight = getHeight();
        }

        //???????????????
        mCentreTranX = (getWidth() - mCenterWidth) / 2f;
        mCentreTranY = (getHeight() - mCenterHeight) / 2f;

        //??????1dp????????????????????????????????????????????????
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

        //??????????????????
        mTransX = mTransY = 0;
        mScale = 1;

    }

    /**
     * ??????????????????
     */
    private void updatePaintStrokeWidth() {
        mVirtualWallPaint.setStrokeWidth(mVirtualPaintStrokeSize);
        mErasePaint.setStrokeWidth(mErasePaintWidth);
    }

    /**
     * ?????????????????????x??????????????????????????????
     */
    public final float toX(float touchX) {
        return (touchX - getAllTranX()) / getAllScale();
    }

    /**
     * ?????????????????????y??????????????????????????????
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

        // ??????????????????????????????????????????????????????????????????
        mTransX = toTransX(touchX, pivotX);
        mTransY = toTransY(touchY, pivotY);

        refresh();
    }

    /**
     * ???????????????x???????????????????????????
     */
    public final float toTouchX(float x) {
        return x * getAllScale() + getAllTranX();
    }

    /**
     * ???????????????y???????????????????????????
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
     * ????????????
     * ????????????toX()????????????????????????
     *
     * @param touchX  ????????????
     * @param doodleX ???????????????????????????
     * @return ?????????
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
     * ??????????????????(??????initBitmap??????)
     * ??? mVirtualWallList ??????
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
            virtualWallBean.setName(mContext.getString(R.string.virtual_wall) + (k + 1)); //????????????1????????????
        }

        refresh();

    }

    public List<VirtualWallBean> getVirtualWallList() {
        return mVirtualWallList;
    }


    /**
     * ???????????????
     *
     * @param RouteBeanList
     */
    public void initRoutes(List<RouteBean> RouteBeanList) {
        mRoutesList = RouteBeanList;
        for (int k = 0; k < RouteBeanList.size(); k++) {
            RouteBean routeBean = RouteBeanList.get(k);

            List<RouteBean.PositionsDTO> RoutePointList = routeBean.getPositions();

            Path path = new Path();
            //?????????
            RouteBean.PositionsDTO startRoutePoint = RoutePointList.get(0);
            path.moveTo((float) startRoutePoint.getWorld_x().doubleValue(), (float) startRoutePoint.getWorld_y().doubleValue());
            //?????????
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


            routeBean.setName(mContext.getString(R.string.navigation_route) + (k + 1)); //????????????1????????????
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
     * ?????????????????????
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
     * ?????????????????????
     */
    public void clearErasePoint() {
//        mErasePathList.clear();
        mErasePoint = null;
    }

    /**
     * ???????????????
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
     * ???????????????????????????
     *
     * @param
     */
    public void showNavigationPositionPointName(String navName ,int navType) {
        for (PositionPointBean positionPointBean : mPositionPointList) {
            if (positionPointBean.getPointName().equals(navName) && navType == positionPointBean.getType()) {
                positionPointBean.setNeedShowName(true);
                Log.d(TAG, "showNavigationPositionPointName???" + positionPointBean.getPointName());
            } else {
                positionPointBean.setNeedShowName(false);
            }
        }
        refresh();
    }

    /**
     * ??????????????????????????????
     */
    public void hideNavigationPositionPointName(PositionPointBean navigationPoint) {
        for (PositionPointBean positionPointBean : mPositionPointList) {
            if (positionPointBean.getPointName().equals(navigationPoint.getPointName()) && navigationPoint.getType() == positionPointBean.getType()) {
                positionPointBean.setNeedShowName(false);
                Log.d(TAG, "showNavigationPositionPointName???" + positionPointBean.getPointName());
            }
        }
        refresh();
    }

    /**
     * ???????????????????????????
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
     * ????????????????????????
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
     * ????????????
     * ?????? onDraw???computeScroll()
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
     * ????????????
     */
    public interface OnCanvasExceptionListener {
        void canvasException();
    }

    /**
     * ??????????????????????????????
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
     * ????????????????????????????????????????????????????????????????????????
     *
     * @param bitmapX ????????????x??????
     * @param bitmapY ????????????y??????
     */
    public void setTouchPosition(float bitmapX, float bitmapY) {
        this.mTouchX = toTouchX(bitmapX);
        this.mTouchY = toTouchY(bitmapY);
    }

    private double localTheta = 0;

    /**
     * ???????????????????????????????????????????????????????????????
     *
     * @param localTheta
     */
    public void setPositionLocalTheta(double localTheta) {
        this.localTheta = localTheta;
    }

    /**
     * ????????????????????????????????????????????????????????????|????????????
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

        if (getCurrentOperatePoint() == null) {//??????????????????
            return false;
        }

        return true;
    }

    /**
     * ?????????
     *
     * @param startX ??????x
     * @param startY ??????y
     * @param endX   ?????????x
     * @param endY   ?????????y
     */
    public Path setArrowPath(Path routePath, float startX, float startY, float endX, float endY) {
        double H = 10; // ????????????
        double L = 3.5; // ???????????????

        double angle = Math.atan(L / H); // ????????????
        double arrowLength = Math.sqrt(L * L + H * H); // ???????????????
        //??????????????????????????????????????????????????????????????????????????????????????????????????????2???????????????
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
        // ??????
        routePath.moveTo(endX, endY);
        routePath.lineTo(x3, y3);
        routePath.lineTo(x4, y4);
        routePath.close();//????????????Path????????????????????????
        return routePath;
    }

    // ??????

    /**
     * @param diffX       X?????????
     * @param diffY       Y?????????
     * @param angle       ????????????????????????????????????????????????????????????
     * @param arrowLength ???????????????
     */
    public double[] rotateVec(float diffX, float diffY, double angle, double arrowLength) {
        double arr[] = new double[2];
        // ??????????????????????????????????????????????????????????????????????????????angle?????????,?????????????????????????????????????????????????????????????????????????????????????????????????????????
        //???????????????1
        double x = diffX * Math.cos(angle) - diffY * Math.sin(angle);
        double y = diffX * Math.sin(angle) + diffY * Math.cos(angle);
        double d = Math.sqrt(x * x + y * y);
        //????????????????????????????????????????????????????????????????????????????????????2
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


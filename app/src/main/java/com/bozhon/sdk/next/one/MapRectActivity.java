package com.bozhon.sdk.next.one;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bozhon.sdk.next.one.databinding.ActivityMapRectDrawBinding;
import com.lib.sdk.next.IMapDrawListener;
import com.lib.sdk.next.IMapTouchListener;
import com.lib.sdk.next.NextSDKHelper;
import com.lib.sdk.next.base.NxMap;
import com.lib.sdk.next.o.map.util.PositionUtil;
import com.lib.sdk.next.robot.constant.RobotConstant;
import com.lib.sdk.next.tag.NextTag;

/**
 * FileName: MapRectActivity
 * Author: zhikai.jin
 * Date: 2021/8/10 16:10
 * Description:
 */
public class MapRectActivity extends AppCompatActivity {
    private ActivityMapRectDrawBinding dataBinding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBinding = ActivityMapRectDrawBinding.inflate(getLayoutInflater());
        View view = dataBinding.getRoot();
        setContentView(view);

        float mRouteStrokeSize = 5.0f;
        Paint mRoutePaint = new Paint();
        mRoutePaint.setColor(ContextCompat.getColor(MapRectActivity.this,R.color.log_warn));
        mRoutePaint.setStrokeWidth(mRouteStrokeSize);
        mRoutePaint.setStyle(Paint.Style.STROKE);
        mRoutePaint.setAntiAlias(true);

        NxMap nxMap = dataBinding.nextMapView.getNxMap();
        nxMap.onShowMapView(RobotConstant.mRobotStatusBean.getProjectId());
        nxMap.setOnDrawListener(new IMapDrawListener() {
            @Override
            public void onDraw(Canvas canvas) {
                double  originX = NextSDKHelper.getInstance().getMapOriginX(MapRectActivity.this,RobotConstant.mRobotStatusBean.getProjectId());
                double  originY = NextSDKHelper.getInstance().getMapOriginY(MapRectActivity.this,RobotConstant.mRobotStatusBean.getProjectId());
                double  resolution = NextSDKHelper.getInstance().getMapResolution(MapRectActivity.this,RobotConstant.mRobotStatusBean.getProjectId());
                canvas.drawLine((float) NextSDKHelper.getInstance().getMapOriginX(MapRectActivity.this,RobotConstant.mRobotStatusBean.getProjectId()), 500, 100, 40, mRoutePaint);
                canvas.drawLine(110, 40, 190, 80, mRoutePaint);
                canvas.drawLine(110, 40, 200, 80, mRoutePaint);
                canvas.drawLine(500, 60, 300, 80, mRoutePaint);
                canvas.drawLine(70, 40, 500, 80, mRoutePaint);
            }
        });

        nxMap.setOnMapTouchListener(new IMapTouchListener() {
            @Override
            public void onScrollBegin(MotionEvent e, float bitmapX, float bitmapY) {

            }

            @Override
            public void onScrollEnd(MotionEvent e, float bitmapX, float bitmapY) {

            }

            @Override
            public void onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY, float bitmapX, float bitmapY) {

            }

            @Override
            public void onSingleTapUp(MotionEvent e, float bitmapX, float bitmapY) {
                Log.w(NextTag.TAG,"bitmapX ===" + bitmapX + "|bitmapY ==" + bitmapY);

                double  originX = NextSDKHelper.getInstance().getMapOriginX(MapRectActivity.this,RobotConstant.mRobotStatusBean.getProjectId());
                double  originY = NextSDKHelper.getInstance().getMapOriginY(MapRectActivity.this,RobotConstant.mRobotStatusBean.getProjectId());
                double  resolution = NextSDKHelper.getInstance().getMapResolution(MapRectActivity.this,RobotConstant.mRobotStatusBean.getProjectId());

                double worldY = PositionUtil.localToServerY(bitmapY, resolution, originY);
                double worldX = PositionUtil.localToServerX(bitmapX, resolution, originX);
                Log.w(NextTag.TAG,"worldY ===" + worldY + "|worldX ==" + worldX);
            }

            @Override
            public void onUpOrCancel(MotionEvent e, float bitmapX, float bitmapY) {

            }

        });
    }
}

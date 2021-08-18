package com.lib.sdk.next;

import android.view.MotionEvent;

/**
 * FileName: IMapTouchListener
 * Author: zhikai.jin
 * Date: 2021/8/11 17:10
 * Description:
 */
public interface IMapTouchListener {

    void onScrollBegin(MotionEvent e, float bitmapX, float bitmapY);

    void onScrollEnd(MotionEvent e,float bitmapX,float bitmapY);


    void onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY,float bitmapX,float bitmapY);


    void onSingleTapUp(MotionEvent e,float bitmapX,float bitmapY);

    void onUpOrCancel(MotionEvent e,float bitmapX,float bitmapY);
}

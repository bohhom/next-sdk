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

import android.util.Log;

/**
 * Created by maqing 2018/11/26 10:16
 * Email：2856992713@qq.com
 */
public class AngleUtil {
    private static final String TAG = "AngleUtil";
    /**
     * 计算两点的方向角
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public static double calculateDirectionAngle(float x1, float y1, float x2, float y2) {
        double theta = 0;
        theta = Math.atan2((y2 - y1), (x2 - x1));
        Log.e(TAG,"calculateDirectionAngle："+x1+","+x2+","+y1+","+y2);
        return theta;
    }

    /**
     * 计算点p2绕p1顺时针旋转的角度
     *
     * @param px1
     * @param py1
     * @param px2
     * @param py2
     * @return 旋转的角度
     */
    public static float computeAngle(float px1, float py1, float px2, float py2) {

        float x = px2 - px1;
        float y = py2 - py1;

        float arc = (float) Math.atan(y / x);

        float angle = (float) (arc / (Math.PI * 2) * 360);

        if (x >= 0 && y == 0) {
            angle = 0;
        } else if (x < 0 && y == 0) {
            angle = 180;
        } else if (x == 0 && y > 0) {
            angle = 90;
        } else if (x == 0 && y < 0) {
            angle = 270;
        } else if (x > 0 && y > 0) { // 1

        } else if (x < 0 && y > 0) { //2
            angle = 180 + angle;
        } else if (x < 0 && y < 0) { //3
            angle = 180 + angle;
        } else if (x > 0 && y < 0) { //4
            angle = 360 + angle;
        }

        Log.e("hzw", "[" + px1 + "," + py1 + "]:[" + px2 + "," + py2 + "] = " + angle);

        return angle;
    }


}

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

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by maqing 2018/12/1 17:35
 * Email：2856992713@qq.com
 */
public class DrawUtil {

    /**
     * 将文本绘制到Bitmap居中位置
     *
     * @param bitmap
     * @param text
     * @return
     */
    public static Bitmap drawTextToBitmap(Bitmap bitmap, String text, Paint paint) {

        Bitmap.Config bitmapConfig =
                bitmap.getConfig();
        // set default bitmap config if none
        if (bitmapConfig == null) {
            bitmapConfig = Bitmap.Config.ARGB_8888;
        }
        // resource bitmaps are imutable,
        // so we need to convert it to mutable one
        bitmap = bitmap.copy(bitmapConfig, true);

        Canvas canvas = new Canvas(bitmap);
//        // new antialised Paint
//        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        // text color - #3D3D3D
//        paint.setColor(Color.WHITE);
//        // text size in pixels
        paint.setTextSize(25);
//        // text shadow
//        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);

        // draw text to the Canvas center
        Rect bounds = new Rect();

        paint.getTextBounds(text, 0, text.length(), bounds);
        int x = (bitmap.getWidth() - bounds.width()) / 2;
        int y = (bitmap.getHeight() - 20 + bounds.height()) / 2;

        canvas.drawText(text, x, y, paint);

        return bitmap;
    }

}

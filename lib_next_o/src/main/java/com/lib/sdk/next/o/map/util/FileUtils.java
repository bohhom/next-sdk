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

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Environment;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 将bitmap转化图片文件到本地
 *
 * @author qhc
 */
public class FileUtils {

    public static String SDPATH = Environment.getExternalStorageDirectory() + "/Photo_LJ/";
    //    public static String SDPATH2 = App.getInstance().getFilesDir() + "/";
    public static String SDPATH2 = "/";

    public static boolean hasSDCard() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    public static File saveBitmap(Bitmap bm, String picName) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 80;// 个人喜欢�?80�?�?,
        if (bm == null) {
            return null;
        }
        bm.compress(Bitmap.CompressFormat.JPEG, options, baos);
        // while (baos.toByteArray().length / 1024 > 100) {
        // baos.reset();
        // options -= 10;
        // bm.compress(Bitmap.CompressFormat.JPEG, options, baos);
        // }

        File f = null;
        try {
            if (hasSDCard()) {
                if (!isFileExist(picName)) {
                    File tempf = createSDDir("");
                }

                f = new File(SDPATH, picName);
            } else {
                f = new File(SDPATH2, picName);
            }
            if (f.exists()) {
                f.delete();
            }
            FileOutputStream out = new FileOutputStream(f);
            // bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.write(baos.toByteArray());
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return f;
    }

    public static File saveBitmap(Bitmap bm, String dirPath, String picName) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 80;// 个人喜欢�?80�?�?,
        if (bm == null) {
            return null;
        }
        bm.compress(Bitmap.CompressFormat.JPEG, options, baos);
        // while (baos.toByteArray().length / 1024 > 100) {
        // baos.reset();
        // options -= 10;
        // bm.compress(Bitmap.CompressFormat.JPEG, options, baos);
        // }

        File f = null;
        try {
            File file = new File(dirPath);
            if (!file.exists()) {
                file.mkdirs();
            }

            f = new File(dirPath, picName);

            if (f.exists()) {
                f.delete();
            }
            FileOutputStream out = new FileOutputStream(f);
            // bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.write(baos.toByteArray());
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return f;
    }

    /**
     * 图片的长和宽缩小味原来的inSampleSize�?
     *
     * @param inSampleSize
     * @return
     */
    public static Options getBitmapOption(int inSampleSize) {
        System.gc();
        Options options = new Options();
        options.inPurgeable = true;
        options.inSampleSize = inSampleSize;
        return options;
    }

    /**
     * 下面的方法可以根据传入的宽和高，计算出合适的inSampleSize值：
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(Options options, int reqWidth, int reqHeight) {
        // 源图片的高度和宽�?
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // 计算出实际宽高和目标宽高的比�?
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // 选择宽和高中�?小的比率作为inSampleSize的�?�，这样可以保证�?终图片的宽和�?
            // �?定都会大于等于目标的宽和高�??
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    // public static float calculateInSampleSize(BitmapFactory.Options options,
    // int reqWidth, int reqHeight) {
    // // 源图片的高度和宽�?
    // final int height = options.outHeight;
    // final int width = options.outWidth;
    // float inSampleSize = 1.0f;
    // if (height > reqHeight || width > reqWidth) {
    // // 计算出实际宽高和目标宽高的比�?
    // final float heightRatio = Math.round((float) height
    // / (float) reqHeight)
    // + (float) height % (float) reqHeight;
    // final float widthRatio = Math.round((float) width
    // / (float) reqWidth)
    // + (float) width % (float) reqWidth;
    // // 选择宽和高中�?小的比率作为inSampleSize的�?�，这样可以保证�?终图片的宽和�?
    // // �?定都会大于等于目标的宽和高�??
    // inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
    // }
    // return inSampleSize;
    // }

    public static class OriginSize implements Serializable {
        public Integer orginWidth;
        public Integer originHeight;
        public int inSampleSize = 1;
        public String path;
    }

    public static Bitmap decodeSampledBitmapFromResource(String path, int reqWidth, int reqHeight) {
        return decodeSampledBitmapFromResource(path, reqWidth, reqHeight, null);
    }

    /**
     * 根据宽度和高度，得到压缩后的图片了�??
     *
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap decodeSampledBitmapFromResource(String path, int reqWidth, int reqHeight, OriginSize orginSize) {
//        Options options = new Options();
////        ARGB_8888
//        options.inPreferredConfig = Config.ARGB_8888;
//        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
//        orginSize.inSampleSize = 1;
//        orginSize.originHeight = bitmap.getHeight();
//        orginSize.orginWidth = bitmap.getWidth();
//        orginSize.path = path;
//        return bitmap;
//        
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final Options options = new Options();
//        ARGB_8888
        options.inPreferredConfig = Config.RGB_565;
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        if (orginSize != null) {
            orginSize.originHeight = options.outHeight;
            orginSize.orginWidth = options.outWidth;
            orginSize.path = path;
        }
        // 调用上面定义的方法计算inSampleSize�?
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        if (orginSize != null)
            orginSize.inSampleSize = options.inSampleSize;
        // 使用获取到的inSampleSize值再次解析图�?
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    public static OriginSize decodeOriginSize(String path, int reqWidth, int reqHeight) {
//        Options options = new Options();
//        options.inPreferredConfig = Config.ARGB_8888;
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(path, options);
//        OriginSize orginSize = new OriginSize();
//        orginSize.inSampleSize = 1;
//        orginSize.originHeight = options.outHeight;
//        orginSize.orginWidth = options.outWidth;
//        orginSize.path = path;
//        return orginSize;


        OriginSize orginSize = new OriginSize();
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final Options options = new Options();
        options.inPreferredConfig = Config.RGB_565;
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        orginSize.originHeight = options.outHeight;
        orginSize.orginWidth = options.outWidth;
        orginSize.path = path;
        // 调用上面定义的方法计算inSampleSize�?
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        orginSize.inSampleSize = options.inSampleSize;
        return orginSize;
    }

    public static Bitmap decodeSampledBitmapFromStream(InputStream is, int reqWidth,
                                                       int reqHeight) {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final Options options = new Options();
        options.inPreferredConfig = Config.RGB_565;
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, options);
        // 调用上面定义的方法计算inSampleSize�?
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // 使用获取到的inSampleSize值再次解析图�?
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeStream(is, null, options);
    }

    // public static Bitmap decodeSampledBitmapFromResource(String path,
    // int reqWidth, int reqHeight) {
    // // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
    // final BitmapFactory.Options options = new BitmapFactory.Options();
    // options.inJustDecodeBounds = true;
    // BitmapFactory.decodeFile(path, options);
    // // 调用上面定义的方法计算inSampleSize�?
    // float inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
    // options.inSampleSize = Math.round(inSampleSize);
    // // 使用获取到的inSampleSize值再次解析图�?
    // options.inJustDecodeBounds = false;
    // if (inSampleSize < 1) {
    // return change(BitmapFactory.decodeFile(path, options), inSampleSize);
    // } else {
    // return BitmapFactory.decodeFile(path, options);
    // }
    //
    // }

    // public static Bitmap change(Bitmap bitmap, float inSampleSize) {
    // Matrix matrix = new Matrix();
    // matrix.postScale(1 / inSampleSize, 1 / inSampleSize); // 长和宽放大缩小的比例
    // Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
    // bitmap.getHeight(), matrix, true);
    // return resizeBmp;
    // }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth,
                                                         int reqHeight) {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        // 调用上面定义的方法计算inSampleSize�?
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // 使用获取到的inSampleSize值再次解析图�?
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static File createSDDir(String dirName) throws IOException {
        File dir = new File(SDPATH + dirName);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            System.out.println("createSDDir:" + dir.getAbsolutePath());
            System.out.println("createSDDir:" + dir.mkdir());
        }
        return dir;
    }

    public static boolean isFileExist(String fileName) {
        File file = new File(SDPATH + fileName);
        file.isFile();
        return file.exists();
    }

    public static void delFile(String fileName) {
        File file = new File(SDPATH + fileName);
        if (file.isFile()) {
            file.delete();
        }
        file.exists();
    }

    public static void deleteDir() {
        File dir = new File(SDPATH);
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;

        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete();
            else if (file.isDirectory())
                deleteDir();
        }
        dir.delete();
    }

    public static boolean fileIsExists(String path) {
        try {
            File f = new File(path);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {

            return false;
        }
        return true;
    }

    /**
     * encodeBase64File:(将文件转成base64 字符串). <br/>
     *
     * @param path 文件路径
     * @return
     * @throws Exception
     * @author guhaizhou@126.com
     * @since JDK 1.6
     */
    public static String encodeBase64File(String path) throws Exception {
        File file = new File(path);
        FileInputStream inputFile = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];
        inputFile.read(buffer);
        inputFile.close();
        return Base64.encodeToString(buffer, Base64.DEFAULT);
    }

    /**
     * encodeBase64File:(将文件转成base64 byte). <br/>
     *
     * @param path 文件路径
     * @return
     * @throws Exception
     * @author guhaizhou@126.com
     * @since JDK 1.6
     */
    public static byte[] encodeBase64ByteFile(String path) throws Exception {
        File file = new File(path);
        FileInputStream inputFile = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];
        inputFile.read(buffer);
        inputFile.close();
        return Base64.encodeToString(buffer, Base64.DEFAULT).getBytes();
    }

    /**
     * encodeBase64File:(将文件转成字符串). <br/>
     *
     * @param path 文件路径
     * @return
     * @throws Exception
     * @author guhaizhou@126.com
     * @since JDK 1.6
     */
    public static String encodeStringFile(String path) throws Exception {
        File file = new File(path);
        FileInputStream inputFile = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];
        inputFile.read(buffer);
        inputFile.close();
        return new String(buffer);
    }


    /**
     * 将图片打上时间水印
     *
     * @param src
     * @return
     */
    public static Bitmap getTimeBitmap(Bitmap src) {
        if (src == null) {
            return null;
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Bitmap bitMap = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Config.RGB_565);
        Canvas canvas = new Canvas(bitMap);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(35.0f);
        String time = simpleDateFormat.format(new Date());
        canvas.drawBitmap(src, 0, 0, paint);
        canvas.drawText(time, src.getWidth() / 2 - paint.measureText(time) / 2, src.getHeight() * 9 / 10, paint);
        canvas.save();
        canvas.restore();

        //释放元图片内存,如果不要释放，注释掉
        src.recycle();
        return bitMap;

    }

    /**
     * 将图片合成
     *
     * @param src 目标图片
     * @param dst 背景图片
     * @return
     */
    public static Bitmap getComposeBitmap(Bitmap src, Bitmap dst) {
        if (src == null) {
            return null;
        }

        Bitmap bitMap = Bitmap.createBitmap(dst.getWidth(), dst.getHeight(), Config.RGB_565);
        Canvas canvas = new Canvas(bitMap);
        Paint paint = new Paint();
        canvas.drawBitmap(dst, 0, 0, paint);
        canvas.drawBitmap(src, dst.getWidth() / 2 - src.getWidth() / 2, dst.getHeight() - src.getHeight() / 2, paint);
        canvas.save();
        canvas.restore();

        //释放元图片内存,如果不要释放，注释掉
        src.recycle();
        dst.recycle();
        return bitMap;
    }
}

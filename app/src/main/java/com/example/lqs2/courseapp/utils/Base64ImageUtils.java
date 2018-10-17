package com.example.lqs2.courseapp.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * base64 工具类
 *
 * @author lqs2
 */
public class Base64ImageUtils {


    /**
     * bit 2 base64 str
     *
     * @param bitmap bitmap
     * @return str base64字符串
     */
    public static String bitmapToBase64Str(Bitmap bitmap) {
        // 将Bitmap转换成字符串
        String string = null;
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bStream);
        byte[] bytes = bStream.toByteArray();
        string = Base64.encodeToString(bytes, Base64.DEFAULT);
        return string;
    }

    /**
     * base64 转 bit
     *
     * @param string 字符串
     * @return bit
     */
    public static Bitmap base64StrToBitmap(String string) {
        //将字符串转换成Bitmap类型
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 判断指定的字符串是否是一张图片的base64
     *
     * @param str 字符串
     * @return 是/否
     */
    public static boolean isPicPath(String str) {
        return str != null && str.length() > 30;
    }

}

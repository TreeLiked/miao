package com.example.lqs2.courseapp.utils;

import android.app.Activity;
import android.content.Intent;

/**
 * 裁剪工具类
 *
 * @author lqs2
 */
public class CropUtils {

    /**
     * 打开相机并进行裁剪
     *
     * @param activity 当前活动
     */
    public static void openAlbumAndCrop(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(intent, Constant.IMAGE_REQUEST_CODE_CROP);
    }
}

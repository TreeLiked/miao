package com.example.lqs2.courseapp.utils;

import android.app.Activity;
import android.content.Intent;

public class CropUtils {

    public static void openAlbumAndCrop(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(intent, Constant.IMAGE_REQUEST_CODE_CROP);
    }

    public static void openAlbum(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(intent, Constant.IMAGE_REQUEST_CODE);
    }

}

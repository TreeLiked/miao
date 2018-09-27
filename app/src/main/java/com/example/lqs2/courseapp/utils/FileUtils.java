package com.example.lqs2.courseapp.utils;

import android.text.TextUtils;

import java.io.File;
import java.text.DecimalFormat;

public class FileUtils {


    /**
     * 将文件大小转换成字节
     */

    public static String FormetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString;
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    public static boolean renameFile(String bucket_id, String filename, String downloadDir) {
        if (TextUtils.isEmpty(downloadDir) || TextUtils.isEmpty(bucket_id) || TextUtils.isEmpty(filename)) {
            return false;
        }
        File file = new File(downloadDir + File.separator + bucket_id);
        return file.renameTo(new File(downloadDir + File.separator + filename));

    }
}

package com.example.lqs2.courseapp.utils;

import android.text.TextUtils;

import java.io.File;
import java.text.DecimalFormat;

/**
 * 文件工具类
 *
 * @author lqs2
 */
public class FileUtils {


    /**
     * 将文件大小转换成字节
     *
     * @param fileS file len
     * @return 带单位
     */
    public static String formatFileSize(long fileS) {
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

    /**
     * 重命名文件
     *
     * @param bucketId    bucket编号
     * @param filename    文件名
     * @param downloadDir 下载路径
     * @return 成功 / 失败
     */
    public static boolean renameFile(String bucketId, String filename, String downloadDir) {
        if (TextUtils.isEmpty(downloadDir) || TextUtils.isEmpty(bucketId) || TextUtils.isEmpty(filename)) {
            return false;
        }
        File file = new File(downloadDir + File.separator + bucketId);
        return file.renameTo(new File(downloadDir + File.separator + filename));

    }
}

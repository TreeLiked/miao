package com.example.lqs2.courseapp.utils;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.lqs2.courseapp.MyApplication;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class VersionUtils {


    /*
     * 获取当前程序的版本名
     */
    public static String getVersionName() {
        PackageManager packageManager = MyApplication.getContext().getPackageManager();
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(MyApplication.getContext().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        assert packInfo != null;
        return packInfo.versionName;
    }


    /*
     * 获取当前程序的版本号
     */
    public static int getVersionCode() {
        //获取packagemanager的实例
        PackageManager packageManager = MyApplication.getContext().getPackageManager();
        //getPackageName(),当前类的包名，0代表获取版本信息
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(MyApplication.getContext().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        assert packInfo != null;
        return packInfo.versionCode;
    }

    public static void checkUpdate(Context context, Activity activity, boolean autoDetect, View view) {


        HttpUtil.checkUpdate(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ToastUtils.showToastOnMain(context, activity, "连接异常，请稍后重试", Toast.LENGTH_SHORT);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {


                    String resp = response.body().string();
                    if (!"-1".equals(resp)) {
                        String currentVersionName = getVersionName();
                        String[] info = resp.split("&");
                        StringBuilder builder = new StringBuilder();

                        String latestVersionName = info[1];
                        if (!TextUtils.isEmpty(latestVersionName)) {
                            if (newReleaseVersion(currentVersionName, latestVersionName)) {
                                if (autoDetect) {
                                    try {
                                        Thread.sleep(5000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                                builder.append("当前版本：").append(currentVersionName).append("\n");
                                builder.append("最新版本：").append(latestVersionName).append("\n");
                                builder.append("发布日期：").append(info[2]).append("\n");
                                builder.append("更新内容：").append("\n");
                                if (null != info[3]) {
                                    String[] updateDetail = info[3].split("-");
                                    for (byte i = 0; i < updateDetail.length; i++) {
                                        builder.append("\t·\t").append(updateDetail[i]).append("\n");
                                    }
                                }
                                activity.runOnUiThread(() -> MaterialDialogUtils.showYesOrNoDialogWithBackground(context, new String[]{"检测到新版本", builder.toString(), "为我更新", "忽略"}, -1, new MaterialDialogUtils.DialogOnConfirmClickListener() {
                                    @Override
                                    public void onConfirmButtonClick() {
                                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                            long time = System.currentTimeMillis();//当前时间的毫秒数
                                            String installPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/" + time + "-" + "miao-" + latestVersionName + ".apk";
                                            ProgressDialog pd;
                                            pd = new ProgressDialog(context);
                                            pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                            pd.setMessage("正在下载更新");
                                            pd.setCancelable(false);
                                            pd.setCanceledOnTouchOutside(false);
                                            pd.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", (dialog, which) -> {
                                                dialog.dismiss();
                                                File file = new File(installPath);
                                                if (file.exists()) {
                                                    file.delete();
                                                }
                                            });
                                            pd.show();

                                            new Thread(() -> {
                                                File file = null;
                                                try {
                                                    file = getUpdate(pd, installPath);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                                if (null != file) {
                                                    installApk(context, file, view);
                                                }
                                            }).start();

                                        } else {
                                            checkPermission(context, activity, PermissionUtils.CODE_WRITE_EXTERNAL_STORAGE);
                                        }
                                    }
                                }, true));
                            } else {
                                if (!autoDetect) {
                                    ToastUtils.showToastOnMain(context, activity, "当前版本已经是最新", Toast.LENGTH_SHORT);
                                }
                            }
                        } else {
                            if (!autoDetect) {
                                ToastUtils.showToastOnMain(context, activity, "系统出错，请稍后重试", Toast.LENGTH_SHORT);
                            }
                        }
                    } else {
                        if (!autoDetect) {
                            ToastUtils.showToastOnMain(context, activity, "服务异常，请稍后重试", Toast.LENGTH_SHORT);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static boolean newReleaseVersion(String current, String latest) {
        String[] split1 = current.split("\\.");
        String[] split2 = latest.split("\\.");
        return Integer.parseInt(split1[0]) == Integer.parseInt(split2[0]) && Integer.parseInt(split1[1]) == Integer.parseInt(split2[1]) && Integer.parseInt(split1[2]) < Integer.parseInt(split2[2]) || Integer.parseInt(split1[0]) == Integer.parseInt(split2[0]) && Integer.parseInt(split1[1]) < Integer.parseInt(split2[1]) || Integer.parseInt(split1[0]) < Integer.parseInt(split2[0]);
    }

    private static File getUpdate(ProgressDialog pd, String path) throws Exception {
        //相等表示当前的sdcard挂载在手机上并且是可用的
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream fops = new FileOutputStream(file);
            Response response = HttpUtil.pushUpdate();
            assert response.body() != null;
            InputStream ips = response.body().byteStream();

            BufferedInputStream bips = new BufferedInputStream(ips);

            int length = Integer.parseInt(response.header("Content-Length"));
            pd.setMax(length);

            byte[] buffer = new byte[1024];
            int len;
            int total = 0;

            while ((len = bips.read(buffer)) != -1) {
                fops.write(buffer, 0, len);
                total += len;
                pd.setProgress(total);
            }
            pd.dismiss();
            return file;

        } else {
            return null;
        }
    }

    private static void installApk(Context context, File file, View view) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(context, "com.example.lqs2.courseapp.fileProvider", file);

            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                boolean hasInstallPermission = context.getPackageManager().canRequestPackageInstalls();
                if (!hasInstallPermission) {
//                    ToastUtils.showToast(context, "无法获得安装权限", Toast.LENGTH_SHORT);
                    Snackbar.make(view, "无法获得安装权限", Snackbar.LENGTH_LONG).setAction("前往打开", v -> startInstallPermissionSettingActivity(context)).show();
                    return;
                }
            }

        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }


//        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }


    private static void checkPermission(Context context, Activity activity, int code) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, code);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void startInstallPermissionSettingActivity(Context context) {
        //8.0
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


}

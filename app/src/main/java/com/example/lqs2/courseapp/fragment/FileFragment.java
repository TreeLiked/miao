package com.example.lqs2.courseapp.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.lqs2.courseapp.BuildConfig;
import com.example.lqs2.courseapp.ObjectSample.DeleteObjectSample;
import com.example.lqs2.courseapp.ObjectSample.GetObjectSample;
import com.example.lqs2.courseapp.ObjectSample.PutObjectSample;
import com.example.lqs2.courseapp.R;
import com.example.lqs2.courseapp.adapters.FileAdapter;
import com.example.lqs2.courseapp.common.QServiceCfg;
import com.example.lqs2.courseapp.entity.File;
import com.example.lqs2.courseapp.utils.Constant;
import com.example.lqs2.courseapp.utils.FilePathResolver;
import com.example.lqs2.courseapp.utils.FileUtils;
import com.example.lqs2.courseapp.utils.HttpUtil;
import com.example.lqs2.courseapp.utils.PermissionUtils;
import com.example.lqs2.courseapp.utils.SharedPreferenceUtil;
import com.example.lqs2.courseapp.utils.UsualSharedPreferenceUtil;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

@SuppressWarnings({"NullableProblems", "ConstantConditions"})
public class FileFragment extends Fragment implements View.OnClickListener {
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {

        @SuppressLint("ShowToast")
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.SHOW_TOAST:
                    if (toast == null && null != getActivity()) {
                        toast = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);
                    }
                    toast.setText((String) msg.obj);
                    toast.setDuration(msg.arg1);
                    toast.show();
                    refreshLayout.setRefreshing(false);
                    break;
                case Constant.CENTER_FILE:
                    fileAdapter.setData(fileList);
                    break;
                case Constant.LOCAL_FILE:
                    fileAdapter.setData(fileList);
                    break;
                case Constant.TURN_PROGRESS_BAR_ON:
                    pBar.setVisibility(View.VISIBLE);
                    break;
                case Constant.TURN_PROGRESS_BAR_OFF:
                    pBar.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }
    };

    private static final String[][] MATCH_ARRAY = {
            //{后缀名，    文件类型}
            {".3gp", "video/3gpp"},
            {".apk", "application/vnd.android.package-archive"},
            {".asf", "video/x-ms-asf"},
            {".avi", "video/x-msvideo"},
            {".bin", "application/octet-stream"},
            {".bmp", "image/bmp"},
            {".c", "text/plain"},
            {".class", "application/octet-stream"},
            {".conf", "text/plain"},
            {".cpp", "text/plain"},
            {".doc", "application/msword"},
            {".exe", "application/octet-stream"},
            {".gif", "image/gif"},
            {".gtar", "application/x-gtar"},
            {".gz", "application/x-gzip"},
            {".h", "text/plain"},
            {".htm", "text/html"},
            {".html", "text/html"},
            {".jar", "application/java-archive"},
            {".java", "text/plain"},
            {".jpeg", "image/jpeg"},
            {".jpg", "image/jpeg"},
            {".js", "application/x-javascript"},
            {".log", "text/plain"},
            {".m3u", "audio/x-mpegurl"},
            {".m4a", "audio/mp4a-latm"},
            {".m4b", "audio/mp4a-latm"},
            {".m4p", "audio/mp4a-latm"},
            {".m4u", "video/vnd.mpegurl"},
            {".m4v", "video/x-m4v"},
            {".mov", "video/quicktime"},
            {".mp2", "audio/x-mpeg"},
            {".mp3", "audio/x-mpeg"},
            {".mp4", "video/mp4"},
            {".mpc", "application/vnd.mpohun.certificate"},
            {".mpe", "video/mpeg"},
            {".mpeg", "video/mpeg"},
            {".mpg", "video/mpeg"},
            {".mpg4", "video/mp4"},
            {".mpga", "audio/mpeg"},
            {".msg", "application/vnd.ms-outlook"},
            {".ogg", "audio/ogg"},
            {".pdf", "application/pdf"},
            {".png", "image/png"},
            {".pps", "application/vnd.ms-powerpoint"},
            {".ppt", "application/vnd.ms-powerpoint"},
            {".prop", "text/plain"},
            {".rar", "application/x-rar-compressed"},
            {".rc", "text/plain"},
            {".rmvb", "audio/x-pn-realaudio"},
            {".rtf", "application/rtf"},
            {".sh", "text/plain"},
            {".tar", "application/x-tar"},
            {".tgz", "application/x-compressed"},
            {".txt", "text/plain"},
            {".wav", "audio/x-wav"},
            {".wma", "audio/x-ms-wma"},
            {".wmv", "audio/x-ms-wmv"},
            {".wps", "application/vnd.ms-works"},
            {".xml", "text/plain"},
            {".z", "application/x-compress"},
            {".zip", "application/zip"},
            {"", "*/*"}
    };

    private SwipeRefreshLayout refreshLayout;
    //读文件权限请求码
    private int REQUEST_PERMISSION_CODE_READ = 1;
    //写文件权限请求码
    private int REQUEST_PERMISSION_CODE_WRITE = 2;
    public static boolean isCloud = true;

    private ProgressBar pBar;
    private Toast toast;
    private static final int NOTIFICATION_ID = 0x3;

    private RecyclerView fileRecycleView;
    private FileAdapter fileAdapter;
    private List<File> fileList;

    private Gson gson;


    private NotificationManager notificationManager;
    private NotificationCompat.Builder builder;

    public static String un;
    private FloatingActionsMenu menu;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_file, container, false);
        fileAdapter = new FileAdapter(getContext(), FileFragment.this);
        gson = new Gson();
        fileRecycleView = view.findViewById(R.id.file_recycle_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        fileRecycleView.setLayoutManager(layoutManager);
        fileRecycleView.setAdapter(fileAdapter);
        menu = view.findViewById(R.id.file_float_menu);
        FloatingActionButton showCloudFile = view.findViewById(R.id.file_choice_showCloudFile);
        FloatingActionButton showLocalFile = view.findViewById(R.id.file_choice_showLocalFile);
        FloatingActionButton searchFile = view.findViewById(R.id.file_choice_searchFile);
        FloatingActionButton uploadFile = view.findViewById(R.id.file_choice_uploadFile);
        FloatingActionButton logOut = view.findViewById(R.id.file_choice_logOut);


        showCloudFile.setOnClickListener(this);
        showLocalFile.setOnClickListener(this);
        searchFile.setOnClickListener(this);
        uploadFile.setOnClickListener(this);
        logOut.setOnClickListener(this);


        pBar = view.findViewById(R.id.file_load_progress_bar);
        notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String id = "channel_1";
            String description = "143";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(id, description, importance);
            notificationManager.createNotificationChannel(channel);
        }


        builder = new NotificationCompat.Builder(getActivity(), "channel_1");
        refreshLayout = view.findViewById(R.id.file_swipe_flush);
        refreshLayout.setColorSchemeResources(R.color.r3);

        refreshLayout.setOnRefreshListener(() -> {
            showLoadBar(true);
            if (isCloud) {
                showCloudFile(false, false);
            } else {
                showLocalDownloadDir(false, false);
            }
        });
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.file_choice_showCloudFile:
                menu.collapseImmediately();
                isCloud = true;
                showCloudFile(true, true);
                break;
            case R.id.file_choice_showLocalFile:
                menu.collapseImmediately();
                isCloud = false;
                showLoadBar(true);
                showLocalDownloadDir(true, true);
                break;
            case R.id.file_choice_searchFile:
                menu.collapseImmediately();
                new MaterialDialog.Builder(getActivity())
                        .title("搜索")
                        .content("输入四位文件编号")
                        .input("", "", (dialog1, input) -> {
                            String no = input.toString();
//                            showToast("您输入的编号是： "+ no, 1);
                            if (null != no && no.length() == 4) {
                                searchFile(no);
                            } else {
                                showToast("文件编号不正确", Toast.LENGTH_SHORT);
                            }
                            dialog1.dismiss();
                        })
                        .positiveColor(Color.parseColor("#00BFFF"))
                        .negativeColor(Color.GRAY)
                        .inputType(InputType.TYPE_CLASS_TEXT)
                        .positiveText("查找")
                        .negativeText("关闭")
                        .onNegative((dialog12, which) -> dialog12.dismiss())
                        .autoDismiss(true)
                        .show();
                break;
            case R.id.file_choice_uploadFile:
                menu.collapseImmediately();
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                    checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_PERMISSION_CODE_READ);
                }
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, Constant.GET_CONTENT);
                break;
            case R.id.file_choice_logOut:
                menu.collapseImmediately();
                pleaseForgetMe();
                getActivity().finish();
                break;
//            case R.id.file_upload:
//
//                break;
//            case R.id.file_loacl_dir:
//
//                break;
//            case R.id.file_search:
//
//            case R.id.file_cloud:
//
//                break;
            default:
                break;
        }
    }

    private void pleaseForgetMe() {
        SharedPreferenceUtil.put(getContext(), "remember_password_darkme", false);
        SharedPreferenceUtil.put(getContext(), "darkme_un", "");
        SharedPreferenceUtil.put(getContext(), "darkme_pwd", "");
    }

    private void searchFile(String no) {
        HttpUtil.searchFileByNo(un, no, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                showToast("查询错误，呜～", Toast.LENGTH_SHORT);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String r = response.body().string();
                if (!"0".equals(r)) {
                    if (!"null".equals(r)) {
                        if (!"no".equals(r)) {
                            try {
                                getActivity().runOnUiThread(() -> {
                                    File file = new Gson().fromJson(r, File.class);
                                    final me.drakeet.materialdialog.MaterialDialog mMaterialDialog = new me.drakeet.materialdialog.MaterialDialog(getActivity());
                                    mMaterialDialog
                                            .setTitle(file.getFile_name())
                                            .setMessage("大       小：" + file.getFile_size()
                                                    + "\n" + "编       号：" + file.getFile_bring_id()
                                                    + "\n" + "归       属：" + file.getFile_post_author()
                                                    + "\n" + "去       向：" + file.getFile_destination()
                                                    + "\n" + "备       注：" + file.getFile_attach()
                                                    + "\n" + "上传日期：" + file.getFile_post_date()
                                                    + "\n" + "有   效   期：" + file.getFile_save_days() + "天")
                                            .setNegativeButton("删除", v1 -> {
                                                HttpUtil.deleteOneFile(un, String.valueOf(file.getId()), new Callback() {
                                                    @Override
                                                    public void onFailure(Call call1, IOException e) {
                                                        showToast("删除失败，请稍后重试", 0);
                                                    }

                                                    @Override
                                                    public void onResponse(Call call1, Response response1) throws IOException {
                                                        if ("1".equals(response1.body().string())) {
                                                            QServiceCfg qServiceCfg = QServiceCfg.instance(getActivity());
                                                            qServiceCfg.setUploadCosPath(file.getFile_bucket_id());
                                                            DeleteObjectSample deleteObjectSample = new DeleteObjectSample(qServiceCfg, FileFragment.this);
                                                            deleteObjectSample.startAsync();
                                                        } else {
                                                            showToast("删除失败，呜～", 0);
                                                        }
                                                    }
                                                });
                                                mMaterialDialog.dismiss();
                                            })
                                            .setPositiveButton("下载", v1 -> {
                                                mMaterialDialog.dismiss();
                                                downloadFile(file.getFile_bucket_id(), file.getFile_name());
                                            })
                                            .setCanceledOnTouchOutside(true);
                                    mMaterialDialog.show();
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            showToast("你没有权限下载此文件", Toast.LENGTH_SHORT);
                        }
                    } else {
                        showToast("不存在此编号的文件 || 文件已失效", Toast.LENGTH_SHORT);
                    }
                } else {
                    showToast("身份验证失败", Toast.LENGTH_SHORT);
                }
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        un = UsualSharedPreferenceUtil.getDarkmeAccount(getActivity());
        showLoadBar(true);
        showCloudFile(true, true);
    }

    public void showCloudFile(boolean init, boolean showHaveShown) {
        clearFileList();
        HttpUtil.showMyFile(un, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (!init) {
                    showToast("刷新失败，呜～", Toast.LENGTH_SHORT);
                } else {
                    if (showHaveShown) {
                        showToast("已显示网盘文件", Toast.LENGTH_SHORT);
                    }
                }
                e.printStackTrace();
                showLoadBar(false);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String data = response.body().string();
                if (!"0".equals(data)) {
                    fileList = gson.fromJson(data, new TypeToken<List<File>>() {
                    }.getType());
                    if (fileList != null && fileList.size() > 0) {
                        showFileList(true);
                        if (!init) {
                            showToast("刷新成功，喵～", Toast.LENGTH_SHORT);
                        } else {
                            if (showHaveShown) {
                                showToast("已显示网盘文件", Toast.LENGTH_SHORT);
                            }
                        }
                    } else {
                        showToast("您还没有存储东西到网盘", Toast.LENGTH_LONG);
                    }
                    showLoadBar(false);
                } else {
                    showToast("用户身份验证失败", Toast.LENGTH_SHORT);
                }
            }
        });
    }

    public void showToast(String c, int t) {
        Message msg = new Message();
        msg.what = Constant.SHOW_TOAST;
        msg.obj = c;
        msg.arg1 = t;
        handler.sendMessage(msg);
    }

    private void showLoadBar(boolean turnOn) {
        Message msg = new Message();
        if (turnOn) {
            msg.what = Constant.TURN_PROGRESS_BAR_ON;
        } else {
            msg.what = Constant.TURN_PROGRESS_BAR_OFF;
        }
        handler.sendMessage(msg);
    }

    private void showFileList(boolean isCloud) {
        Message msg = new Message();
        if (isCloud) {
            msg.what = Constant.CENTER_FILE;
        } else {
            msg.what = Constant.LOCAL_FILE;
        }
        handler.sendMessage(msg);
    }

    private void showLocalDownloadDir(boolean isInit, boolean showHaveShown) {
        clearFileList();
        try {
            if (PermissionUtils.checkReadExtraStoragePermission(getContext())) {
                java.io.File[] listFiles = new java.io.File(Constant.DOWNLOAD_DIR).listFiles();

                // storage/emulated/0/Download
                Calendar cal = Calendar.getInstance();
                if (listFiles != null && listFiles.length > 0) {
                    for (java.io.File file : listFiles) {
                        if (file.isFile()) {
                            File file1 = new File();
                            file1.setFile_name(file.getName());
                            cal.setTimeInMillis(file.lastModified());
                            file1.setFile_post_date(cal.getTime().toLocaleString());
                            file1.setFile_size(FileUtils.FormetFileSize(new FileInputStream(Constant.DOWNLOAD_DIR + java.io.File.separator + file.getName()).available()));
                            fileList.add(file1);
                            showLoadBar(false);
                            showFileList(false);
                            if (!isInit) {
                                showToast("刷新成功，喵～", Toast.LENGTH_SHORT);
                            } else {
                                if (showHaveShown) {
                                    showToast("已显示本地下载文件夹", Toast.LENGTH_SHORT);
                                }
                            }
                        }
                    }
                } else {
                    showLoadBar(false);
                    showToast("空目录无法显示", Toast.LENGTH_LONG);
//                    if (!isInit) {
//                        showFileList(false);
//                        showToast("刷新成功，喵～", Toast.LENGTH_SHORT);
//                    } else {
//                        if (showHaveShown) {
//                            showToast("已显示本地下载文件夹", Toast.LENGTH_SHORT);
//                        }
//                    }
                }
            } else {
                showLoadBar(false);
                PermissionUtils.requestReadPermission(getContext(), getActivity(), PermissionUtils.CODE_READ_EXTERNAL_STORAGE);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Constant.GET_CONTENT:
                    Uri uri = data.getData();
                    String url = FilePathResolver.getPathFormUri(getActivity(), uri);
                    if (url != null) {
                        doShowUploadChoice(url);
                    } else {
                        showToast("文件路径不正确", Toast.LENGTH_SHORT);
                    }
                    break;
                case PermissionUtils.CODE_READ_EXTERNAL_STORAGE:
                    menu.collapseImmediately();
                    isCloud = false;
                    showLoadBar(true);
                    showLocalDownloadDir(true, true);
                default:
                    break;
            }
        }
    }

    private void doShowUploadChoice(String url) {
        View view = null;
        try {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            view = inflater.inflate(R.layout.file_upload_choice, null);
        } catch (InflateException e) {
            e.printStackTrace();
        }
        View finalView = view;
        new MaterialDialog.Builder(getContext())
                .title("文件上传选项")
                .customView(view, true)
                .positiveColor(getResources().getColor(R.color.green_light))
                .negativeColor(getResources().getColor(R.color.T_B6))
                .positiveText("上传")
                .negativeText("取消")
                .onAny((dialog1, which) -> {
                    if (which == DialogAction.POSITIVE) {
                        EditText e_text1 = finalView.findViewById(R.id.file_choice_dest);
                        EditText e_text2 = finalView.findViewById(R.id.file_choice_atta);
                        String t1 = e_text1.getText().toString();
                        String t2 = e_text2.getText().toString();
                        if (t2.length() <= 45) {
                            if (!TextUtils.isEmpty(t1)) {
                                try {
                                    HttpUtil.hasMatcherUser(t1, new Callback() {
                                        @Override
                                        public void onFailure(Call call, IOException e) {
                                            dialog1.dismiss();
                                            showToast("服务异常", Toast.LENGTH_SHORT);
                                            e.printStackTrace();
                                        }

                                        @Override
                                        public void onResponse(Call call, Response response) throws IOException {
                                            if (!"0".equals(response.body().string())) {
                                                dialog1.dismiss();
                                                doUpload(url, t1, t2);
                                            } else {
                                                showToast("用户名【 " + t1 + " 】不存在，不指定请留空", Toast.LENGTH_SHORT);
                                            }
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                dialog1.dismiss();
                                doUpload(url, t1, t2);
                            }
                        } else {
                            showToast("备注限制在45字以内", Toast.LENGTH_SHORT);
                        }
                    } else if (which == DialogAction.NEGATIVE) {
                        dialog1.dismiss();
                    }
                })
                .autoDismiss(false)
                .show();
    }

    private void doUpload(String absUrl, String dest, String atta) {

        String fileSize = "未知大小";
//        String fileName = absUrl.substring(absUrl.lastIndexOf("/") + 1);
        try {
            fileSize = FileUtils.FormetFileSize(new FileInputStream(new java.io.File(absUrl)).available());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String filename = getFilenameThroughPath(absUrl);
        String finalFileSize = fileSize;
        HttpUtil.generateNewFile(filename, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                showToast("服务异常", Toast.LENGTH_SHORT);
                showLoadBar(false);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
//                return r1.toString() + "/" + r1 + "." + suffix;
                String d = response.body().string();
                if (!"0".equals(d)) {
                    QServiceCfg qServiceCfg = QServiceCfg.instance(getActivity());
                    qServiceCfg.setUploadFileUrl(absUrl);
                    qServiceCfg.setUploadCosPath(d.substring(d.lastIndexOf("/")));
                    PutObjectSample putObjectSample = new PutObjectSample(qServiceCfg, FileFragment.this);
                    putObjectSample.startAsync(filename);
                    insertNewFileRecord(un, d.substring(0, d.indexOf("/")), filename, atta, dest, finalFileSize);
                }
            }
        });
    }

    //    public String  insertFileRecord(String file_post_author, int fileNo, String fileName, String attachment, String destination, String fileSize)
    private void insertNewFileRecord(String file_post_author, String fileNo, String fileName, String attachment, String destination, String fileSize) {
        HttpUtil.insertFileRecord(file_post_author, fileNo, fileName, attachment, destination, fileSize, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                showToast("上传失败", Toast.LENGTH_SHORT);

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if ("1".equals(response.body().string())) {
                    showToast("文件编号：" + fileNo + "（ 上传进度见通知栏 ）", Toast.LENGTH_LONG);
                    showCloudFile(true, false);
                } else {
                    showToast("上传失败", Toast.LENGTH_SHORT);
                }
            }
        });

    }

    public void startProgress(String title, String content, int t) {
        if (t < 0) {
            builder.setSmallIcon(R.mipmap.up);
        } else {
            builder.setSmallIcon(R.mipmap.down);
        }
        builder.setProgress(100, 0, true);
        builder.setContentTitle(title);
        builder.setContentText(content);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    public void publishProgress(int per, String t) {
        builder.setProgress(100, per, false);
        builder.setContentTitle(t);
        builder.setContentText("已完成： " + per + "%");
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    public void sayGoodbyeToProgress(String title, String content, int t) {
//        设置关闭通知栏
        notificationManager.cancel(NOTIFICATION_ID);
        builder = new NotificationCompat.Builder(getActivity(), "channel_1");
        if (t < 0) {
            builder.setSmallIcon(R.mipmap.up);
        } else {
            builder.setSmallIcon(R.mipmap.down);
        }
        builder.setContentTitle(title);
        builder.setContentText(content);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    }

    public void checkPermission(String permissionType, int code) {
        if (ActivityCompat.checkSelfPermission(getContext(), permissionType) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{permissionType}, code);
        }
    }

    public void downloadFile(String bucket_id, String filename) {
        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_PERMISSION_CODE_WRITE);
        doDownload(bucket_id, filename);
    }

    public void doDownload(String bucket_id, String filename) {
        QServiceCfg qServiceCfg = QServiceCfg.instance(getActivity());
        qServiceCfg.setDownloadDir(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath());
        System.out.println(qServiceCfg.getDownloadDir());
        qServiceCfg.setGetCosPath(bucket_id);
        GetObjectSample getObjectRequest = new GetObjectSample(qServiceCfg, this);
        getObjectRequest.startAsync(bucket_id, filename);
    }

    public String getFilenameThroughPath(String s) {
        return s.substring(s.lastIndexOf("/") + 1);
    }


    public void openFileByPath(Context context, String path) {
        if (context == null || path == null)
            return;
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //设置intent的Action属性
        intent.setAction(Intent.ACTION_VIEW);
        //文件的MIME类型
        String type = "";
        for (String[] aMATCH_ARRAY : MATCH_ARRAY) {
            if (path.contains(aMATCH_ARRAY[0])) {
                type = aMATCH_ARRAY[1];
                break;
            }
        }
        try {
            //设置intent的data和Type属性
            java.io.File file = new java.io.File(path);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri fileUri = FileProvider.getUriForFile(context.getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", file);//android 7.0以上
                intent.setDataAndType(fileUri, type);
                grantUriPermission(context, fileUri, intent);
            } else {
                intent.setDataAndType(Uri.fromFile(file), type);
            }
            //跳转
            context.startActivity(intent);
        } catch (Exception e) {
            showToast("无法打开该格式的文件", Toast.LENGTH_SHORT);
            e.printStackTrace();
        }
    }

    private static void grantUriPermission(Context context, Uri fileUri, Intent intent) {
        List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            context.grantUriPermission(packageName, fileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
    }

    public void deleteFileInDownloadDir(String file_name) {
        if (TextUtils.isEmpty(file_name)) {
            showToast("删除失败", Toast.LENGTH_SHORT);
            return;
        }
        try {
            java.io.File file = new java.io.File(Constant.DOWNLOAD_DIR + java.io.File.separator + file_name);
            boolean delete = file.delete();
            if (delete) {
                showToast("删除成功", Toast.LENGTH_SHORT);
                showLocalDownloadDir(true, false);
            } else {
                showToast("删除失败", Toast.LENGTH_SHORT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearFileList() {
        if (null != fileList) {
            fileList.clear();
        }
    }
}
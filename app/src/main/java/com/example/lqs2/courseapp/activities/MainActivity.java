package com.example.lqs2.courseapp.activities;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.bumptech.glide.Glide;
import com.example.lqs2.courseapp.R;
import com.example.lqs2.courseapp.adapters.TweetAdapter;
import com.example.lqs2.courseapp.entity.Course;
import com.example.lqs2.courseapp.entity.Tweet;
import com.example.lqs2.courseapp.utils.Base64ImageUtils;
import com.example.lqs2.courseapp.utils.Constant;
import com.example.lqs2.courseapp.utils.CropUtils;
import com.example.lqs2.courseapp.utils.HtmlCodeExtractUtil;
import com.example.lqs2.courseapp.utils.HttpUtil;
import com.example.lqs2.courseapp.utils.ImageTools;
import com.example.lqs2.courseapp.utils.MaterialDialogUtils;
import com.example.lqs2.courseapp.utils.SharedPreferenceUtil;
import com.example.lqs2.courseapp.utils.StatusBarUtils;
import com.example.lqs2.courseapp.utils.ToastUtils;
import com.example.lqs2.courseapp.utils.Tools;
import com.example.lqs2.courseapp.utils.UsualSharedPreferenceUtil;
import com.example.lqs2.courseapp.utils.VersionUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yalantis.ucrop.UCrop;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import at.favre.lib.dali.Dali;
import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.blurry.Blurry;
import me.drakeet.materialdialog.MaterialDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

import static com.example.lqs2.courseapp.MyApplication.getContext;

public class MainActivity extends ActivityCollector implements View.OnClickListener {


    public static final String TAG = "MainActivity";

    public static int weekToday = Tools.getWeek();
    public int weekNow;


    //    是否用户修改了信息
    public static boolean userInfoChangeFlag = false;
    //    drawer第一次打开查找view
    private boolean initFlag = true;
    //    自己写的也忘了
    public boolean flag2 = true;

    List<Course> dayCourseList = new LinkedList<>();

    //    flag3, 是否还需要显示今日课程
    public static boolean flag3 = true;
    boolean hasGetCourse;
    private String data;
    private Toast toast;


    private TextView whole_id;
    private CircleImageView whole_head_image;
    private ImageView head_image_bg;

    public static MainActivity activity;
    public static Context context;


    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView bing_main_pic;
    TextView main_course_name;
    TextView main_course_location;
    TextView main_course_state;
    private SwipeRefreshLayout recyclerView_layout;
    private SwipeMenuRecyclerView recyclerView;
    RelativeLayout layout;
    private TweetAdapter tweetAdapter;
    private Calendar calendar;
    private List<Tweet> tweetList = new ArrayList<>();
    private static Gson gson = new Gson();
    public static int TWEET_START_POSITION = 0;

    private MainBgChangedReceiver bgChangedReceiver;
    public static LocalBroadcastManager localBroadcastManager;

    public static int bgType = 0;


//    private DownloadService.DownloadBinder downloadBinder;


    private String darkme_un = "";
//    private ServiceConnection connection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            downloadBinder = (DownloadService.DownloadBinder) service;
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//
//        }
//    };

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.SHOW_TOAST:
                    if (toast == null) {
                        toast = Toast.makeText(getContext(), "", Toast.LENGTH_SHORT);
                    }
                    toast.setText((String) msg.obj);
                    toast.setDuration(msg.arg1);
                    toast.show();
//                    refreshLayout.setRefreshing(false);
                    break;

                case 0:
//                    recyclerView_layout.endRefreshing();
                    recyclerView_layout.setRefreshing(false);
                    Toast.makeText(MainActivity.this, "Failed to load tweets", Toast.LENGTH_LONG).show();
                    break;
                case 1:
//                    swipeRefreshLayout.setRefreshing(false);
//                    tweetAdapter = new TweetAdapter(newsMainList);
//                    recyclerView.setAdapter(tweetAdapter);
//                    tweetAdapter.notifyDataSetChanged();
                    Toast.makeText(MainActivity.this, "喵～", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StatusBarUtils.setStatusTransparent(this);
        context = this;
        activity = this;
//        StatusBarUtils.setStatusBarTransparentAndTextColorBlack(this);

        IntentFilter intentFilter = new IntentFilter();
        //每分钟变化
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        TimeChangeReceiver timeChangeReceiver = new TimeChangeReceiver();
        registerReceiver(timeChangeReceiver, intentFilter);


        localBroadcastManager = LocalBroadcastManager.getInstance(MainActivity.this);
        bgChangedReceiver = new MainBgChangedReceiver();
        IntentFilter intentFilter1 = new IntentFilter();
        intentFilter1.addAction(Constant.ACTION1);
        intentFilter1.addAction(Constant.ACTION2);
        intentFilter1.addAction(Constant.ACTION3);
        localBroadcastManager.registerReceiver(bgChangedReceiver, intentFilter1);


        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        bing_main_pic = findViewById(R.id.main_pic);
        main_course_name = findViewById(R.id.main_course_name);
        main_course_location = findViewById(R.id.main_course_location);
        main_course_state = findViewById(R.id.main_course_state);
        layout = findViewById(R.id.main_course_layout);


        showBg();
        checkDarkMode();
        checkUpdate();


        darkme_un = (String) SharedPreferenceUtil.get(MainActivity.this, "darkme_un", "");


        initRefreshLayout();

        weatherToShowCourse();
        displayTweets(TWEET_START_POSITION, false, false);


        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);

        }

//        单定义
        navigationView.setCheckedItem(R.id.nav_course);
        final com.getbase.floatingactionbutton.FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(this);
        fab.setOnLongClickListener(v -> {
            MaterialDialog[] dialog = new MaterialDialog[1];
            dialog[0] = MaterialDialogUtils.getItemListDialog(MainActivity.this, "连续互通", (parent, view, position, id) -> {
                switch (position) {
                    case 0:
                        dialog[0].dismiss();
                        if (!TextUtils.isEmpty(darkme_un)) {
                            if (checkHandoffFun()) {
                                checkHandoffText(false);
                            } else {
                                ToastUtils.showToast(this, "请在设置里开启此功能", Toast.LENGTH_SHORT);
                            }
                        } else {
                            showNoLoginDarkmeInfo(true, "，无法使用此功能");
                        }
                        break;
                    case 1:
                        dialog[0].dismiss();
                        if (!TextUtils.isEmpty(darkme_un)) {
                            if ((boolean) SharedPreferenceUtil.get(this, "toggle_handoff", false)) {
                                String clipText = getTextFromClipboard();
                                if (!TextUtils.isEmpty(clipText)) {
                                    MaterialDialogUtils.showYesOrNoDialogWithBothSthTodo(this, new String[]{"检测到文本", clipText, "直接推送", "打开推送页面"}, new MaterialDialogUtils.DialogBothDoSthOnClickListener() {
                                        @Override
                                        public void onConfirmButtonClick() {
                                            pushHandoffText(darkme_un, clipText);
                                        }

                                        @Override
                                        public void onCancelButtonClick() {
                                            openPushHandoffTextPage();
                                        }
                                    }, true);
                                } else {
                                    openPushHandoffTextPage();
                                }
                            } else {
                                ToastUtils.showToast(this, "请在设置里开启此功能", Toast.LENGTH_SHORT);
                            }

                        } else {
//                            ToastUtils.showToast(MainActivity.this, "您还没有登录[darkme.cn]", Toast.LENGTH_SHORT);
                            showNoLoginDarkmeInfo(true, "，无法使用此功能");
                        }
                        break;
                    default:
                        break;
                }
            }, new ArrayList<String>() {{
                add("获取文本 [ Fetch Text ] ");
                add("推送文本 [ Push Text ] ");
            }});
            dialog[0].show();
            return true;
        });


//        动作监听
        layout.setOnClickListener(v -> goCourseActivity());
        drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                if (initFlag || userInfoChangeFlag) {
                    initFlag = false;
                    userInfoChangeFlag = false;
                    whole_id = findViewById(R.id.whole_id);
                    whole_head_image = findViewById(R.id.whole_head_image);
                    head_image_bg = findViewById(R.id.head_image_background);
                    displayUserInfo(3);
                    whole_head_image.setOnClickListener(v -> {
                        darkme_un = (String) SharedPreferenceUtil.get(MainActivity.this, "darkme_un", "");
                        if (!TextUtils.isEmpty(darkme_un)) {
                            CropUtils.openAlbumAndCrop(MainActivity.this);
                        } else {
                            showNoLoginDarkmeInfo(false, "，无法上传头像");
                        }
                    });
                    whole_id.setOnClickListener(v -> {
                        Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                        startActivity(intent);
                    });
                }
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        navigationView.setNavigationItemSelectedListener((MenuItem item) -> {
            switch (item.getItemId()) {
                case R.id.nav_course:
                    goCourseActivity();
                    break;
//                case R.id.nav_e_card:
//                    boolean hasLogin_ECARD = (boolean) SharedPreferenceUtil.get(MainActivity.this, "hasLoginECARD", false);
//                    boolean hasRememberPW_ECARED = (boolean) SharedPreferenceUtil.get(MainActivity.this, "remember_password_ecard", false);
//                    if (!hasLogin_ECARD) {
//                        Intent intent = new Intent(MainActivity.this, Login_ECARD_Activity.class);
//                        startActivity(intent);
//                    } else if (!hasRememberPW_ECARED) {
//                        //TODO
//                    }
//                    break;
                case R.id.nav_school_notice:
                    goActivity(NoticeActivity.class, null);
                    break;

                case R.id.nav_library:
                    goActivity(LibraryActivity.class, null);
                    break;
                case R.id.nav_grade:
                    final MaterialDialog dialog[] = new MaterialDialog[1];
                    dialog[0] = MaterialDialogUtils.getItemListDialog(this, "查询选项", (parent, view, position, id) -> {
                        dialog[0].dismiss();
                        switch (position) {
                            case 0:
                                Toast.makeText(MainActivity.this, Constant.gradeQueryWelcome, Toast.LENGTH_SHORT).show();
                                goActivity(LoginNJITActivity.class, new HashMap<String, String>() {{
                                    put("TODO", "GRADE");
                                }});
//                                Intent intent = new Intent(MainActivity.this, LoginNJITActivity.class);
//                                intent.putExtra("TODO", "GRADE");
//                                startActivity(intent);
                                break;
                            case 1:
                                Toast.makeText(MainActivity.this, Constant.creditQueryWelcome, Toast.LENGTH_SHORT).show();
                                goActivity(LoginNJITActivity.class, new HashMap<String, String>() {{
                                    put("TODO", "CREDIT");
                                }});
                                break;
                            case 2:
                                break;
                            default:
                                break;
                        }
                    }, new ArrayList<String>() {{
                        add(Constant.gradeQueryItem1);
                        add(Constant.gradeQueryItem2);
                        add(Constant.gradeQueryItem3);
                    }});
                    dialog[0].show();
                    break;
//                case R.id.nav_comment:
//                    Intent intent2 = new Intent(MainActivity.this, LoginNJITActivity.class);
//                    intent2.putExtra("TODO", "COMMENT");
//                    startActivity(intent2);
//                    break;
//                case R.id.nav_network:
//                    @SuppressWarnings("ConstantConditions")
//                    boolean hasSavePwd = (Boolean) SharedPreferenceUtil.get(MainActivity.this, "remember_password_jw", false);
//                    if (!hasSavePwd) {
//                        Intent intent = new Intent(MainActivity.this, LoginNJITActivity.class);
//                        intent.putExtra("TODO", "NETWORK");
//                        startActivity(intent);
//                    } else {
//                        Intent intent = new Intent(MainActivity.this, NetworkActivity.class);
//                        startActivity(intent);
//                    }
//                    break;


                case R.id.nav_cloud_file:

                    if (checkHasLoginDarkme()) {
                        goFileActivity(darkme_un);
                    } else {
                        showNoLoginDarkmeInfo(false, "");
                    }

                    break;
                case R.id.nav_friends:
                    if (checkHasLoginDarkme()) {
                        goActivity(FriendActivity.class, null);
                    } else {
                        showNoLoginDarkmeInfo(false, "");
                    }
                    break;
                case R.id.nav_home:
                    if (!checkHasLoginDarkme()) {
                        boolean isRemember = (boolean) SharedPreferenceUtil.get(MainActivity.this, "remember_password_darkme", false);
                        if (isRemember) {
                            String un = (String) SharedPreferenceUtil.get(MainActivity.this, "darkme_un", "");
                            String pwd = (String) SharedPreferenceUtil.get(MainActivity.this, "darkme_pwd", "");
//                        goFileActivity(un, pwd);
                        } else {
                            View view = null;
                            try {
                                LayoutInflater inflater = LayoutInflater.from(this);
                                view = inflater.inflate(R.layout.darkme_login, null);
                            } catch (InflateException e) {
                                e.printStackTrace();
                            }
                            View finalView = view;
                            assert finalView != null;
                            CheckBox rem = finalView.findViewById(R.id.darkme_rememberme);
                            EditText e_text1 = finalView.findViewById(R.id.darkme_login_un);
                            EditText e_text2 = finalView.findViewById(R.id.darkme_login_pwd);
                            e_text1.setText((String) SharedPreferenceUtil.get(this, "darkme_un", ""));

                            new com.afollestad.materialdialogs.MaterialDialog.Builder(this)
                                    .title("登录 [ darkme.cn ] ")
                                    .customView(view, true)
                                    .positiveColor(getResources().getColor(R.color.r4))
                                    .positiveText("登录")
                                    .negativeColor(getResources().getColor(R.color.r7))
                                    .negativeText("注册")
                                    .onAny((dialog1, which) -> {
                                        if (which == DialogAction.POSITIVE) {
                                            String t1 = e_text1.getText().toString();
                                            String t2 = e_text2.getText().toString();
                                            if (!TextUtils.isEmpty(t1)) {
                                                try {
                                                    HttpUtil.userValidateDarkMe(t1, t2, new Callback() {
                                                        @Override
                                                        public void onFailure(Call call, IOException e) {
                                                            dialog1.dismiss();
                                                            showToast("服务异常", Toast.LENGTH_SHORT);
                                                            e.printStackTrace();
                                                        }

                                                        @Override
                                                        public void onResponse(Call call, Response response) throws IOException {
                                                            if ("1".equals(response.body().string())) {
                                                                SharedPreferenceUtil.put(MainActivity.this, "darkme_un", t1);
                                                                if (rem.isChecked()) {
                                                                    SharedPreferenceUtil.put(MainActivity.this, "darkme_pwd", t2);
                                                                    SharedPreferenceUtil.put(MainActivity.this, "remember_password_darkme", true);
                                                                }
                                                                userInfoChangeFlag = true;
                                                                showToast("登录成功，开启精彩之旅吧", Toast.LENGTH_SHORT);

                                                                dialog1.dismiss();
                                                            } else {
                                                                showToast("用户名【 " + t1 + " 】不存在或密码错误", Toast.LENGTH_SHORT);
                                                            }
                                                        }
                                                    });
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            } else {
                                                dialog1.dismiss();
                                            }
                                        } else if (which == DialogAction.NEGATIVE) {
                                            dialog1.dismiss();
                                            View view1;
                                            LayoutInflater inflater = LayoutInflater.from(this);
                                            view1 = inflater.inflate(R.layout.darkme_register, null);
                                            EditText e1 = view1.findViewById(R.id.darkme_register_un);
                                            EditText e2 = view1.findViewById(R.id.darkme_register_pwd);
                                            EditText e3 = view1.findViewById(R.id.darkme_register_mailbox);
                                            final String[] isMan = {"1"};
                                            RadioGroup sexGroup = view1.findViewById(R.id.darkme_register_radio_group_sex);
                                            sexGroup.setOnCheckedChangeListener((group, checkedId) -> {
                                                switch (checkedId) {
                                                    case R.id.darkme_register_male:
                                                        isMan[0] = "1";
                                                        break;
                                                    case R.id.darkme_register_female:
                                                        isMan[0] = "0";
                                                        break;
                                                }
                                            });
                                            final MaterialDialog[] dialog2 = new MaterialDialog[1];
                                            dialog2[0] = MaterialDialogUtils.showYesOrNoDialogWithAll(this, new String[]{"注册 [ darkme.cn ] ", "", "注册", "取消"}, view1, -1, new MaterialDialogUtils.DialogBothDoSthOnClickListener() {
                                                @Override
                                                public void onConfirmButtonClick() {

                                                    String t1 = e1.getText().toString();
                                                    String t2 = e2.getText().toString();
                                                    String t3 = e3.getText().toString();
                                                    if (!TextUtils.isEmpty(t1) && !TextUtils.isEmpty(t2)) {

                                                        if (t1.length() <= 15 && !t1.contains("@") && t2.length() <= 16) {
                                                            new Thread(() -> {
                                                                try {
                                                                    Response response = HttpUtil.userRegisterDarkMe(t1, t2, t3, isMan[0]);
                                                                    String resp = response.body().string();
                                                                    if (!TextUtils.isEmpty(resp)) {
                                                                        if ("1".equals(resp)) {
                                                                            dialog2[0].dismiss();
                                                                            runOnUiThread(() -> {
                                                                                ToastUtils.showToast(MainActivity.this, "注册成功[" + t1 + "]", Toast.LENGTH_LONG);
                                                                            });
                                                                        } else if ("0".equals(resp)) {
                                                                            runOnUiThread(() -> {
                                                                                ToastUtils.showToast(MainActivity.this, "[" + t1 + "]已经被使用", Toast.LENGTH_LONG);
                                                                            });
                                                                        } else {
                                                                            runOnUiThread(() -> {
                                                                                ToastUtils.showToast(MainActivity.this, "服务器发生异常", Toast.LENGTH_SHORT);

                                                                            });
                                                                        }
                                                                    }
                                                                } catch (IOException e) {
                                                                    e.printStackTrace();
                                                                    ToastUtils.showToast(MainActivity.this, "异常终止", Toast.LENGTH_SHORT);
                                                                }
                                                            }).start();
                                                        } else {
                                                            ToastUtils.showToast(MainActivity.this, "用户名和密码参数不正确", Toast.LENGTH_LONG);
                                                        }

                                                    } else {
                                                        ToastUtils.showToast(MainActivity.this, "用户名和密码都不用填的么", Toast.LENGTH_SHORT);
                                                    }
                                                }

                                                @Override
                                                public void onCancelButtonClick() {
                                                    dialog2[0].dismiss();
                                                }
                                            }, false);

                                        }
                                    })
                                    .autoDismiss(false)
                                    .show();
                        }
                    } else {
                        MaterialDialogUtils.showYesOrNoDialogWithBothSthTodo(this, new String[]{"已经登录的账号", "[" + darkme_un + "]", "关闭", "注销登录"}, new MaterialDialogUtils.DialogBothDoSthOnClickListener() {
                            @Override
                            public void onConfirmButtonClick() {

                            }

                            @Override
                            public void onCancelButtonClick() {
                                userInfoChangeFlag = true;
                                UsualSharedPreferenceUtil.loginOutDarkMe(MainActivity.this);
                                ToastUtils.showToast(MainActivity.this, "注销成功", Toast.LENGTH_SHORT);
                            }
                        }, true);
                    }
                    break;
                case R.id.nav_addNews:
                    if (checkHasLoginDarkme()) {
                        Intent intent3 = new Intent(MainActivity.this, NewTweetActivity.class);
                        intent3.putExtra("darkme_un", darkme_un);
                        startActivity(intent3);
                    } else {
                        showNoLoginDarkmeInfo(false, "，暂时不能发布");
                    }
                    break;
                case R.id.nav_school_calendar:
                    Intent intent5 = new Intent(MainActivity.this, SchoolCalendarActivity.class);
                    startActivity(intent5);
                    break;
                case R.id.nav_should_do:
                    if (checkHasLoginDarkme()) {
                        Intent intent1 = new Intent(MainActivity.this, MemoActivity.class);
                        intent1.putExtra("darkme_un", darkme_un);
                        startActivity(intent1);
                    } else {
                        showNoLoginDarkmeInfo(false, "，无法使用备忘录功能");
                    }
                    break;

                case R.id.nav_settings:
                    Intent set_intent = new Intent(MainActivity.this, SettingActivity.class);
                    startActivity(set_intent);
                    break;
//                case R.id.nav_login_out:
//                    final MaterialDialog mMaterialDialog = new MaterialDialog(MainActivity.this);
//                    mMaterialDialog
//                            .setTitle("确认登出")
//                            .setMessage("此操作不可撤销")
//                            .setPositiveButton("确认", v -> {
////                                deleteFileNow("courseSourceCode");
//                                SharedPreferenceUtil.clear(MainActivity.this);
//                                Snackbar.make(navigationView, "已清除所有信息", Snackbar.LENGTH_LONG).show();
//                                mMaterialDialog.dismiss();
//                                finish();
//                                finishAll();
//                            })
//                            .setNegativeButton("取消", v -> mMaterialDialog.dismiss());
//                    mMaterialDialog.show();
//                    break;
                default:
                    break;
            }
            drawerLayout.closeDrawers();
            return true;
        });
//        swipeRefreshLayout.setOnRefreshListener(this::showMainNews);

    }

    private void openPushHandoffTextPage() {
        View view1;
        LayoutInflater inflater = LayoutInflater.from(this);
        view1 = inflater.inflate(R.layout.darkme_post_handoff_text, null);
        EditText editText = view1.findViewById(R.id.darkme_post_handoff_text_edit);
        MaterialDialogUtils.showYesOrNoDialogWithCustomView(MainActivity.this, new String[]{"推送文本到[darkme.cn]", "", "确认", "退出"}, view1, new MaterialDialogUtils.DialogOnConfirmClickListener() {
            @Override
            public void onConfirmButtonClick() {
                String str = editText.getText().toString();
                if (!TextUtils.isEmpty(str)) {

                } else {
                    ToastUtils.showToast(MainActivity.this, "未检测到文本" + str, Toast.LENGTH_LONG);
                }
            }
        }, true);
    }

    private void pushHandoffText(String un, String text) {
        HttpUtil.setUserHandoffText(un, text, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                showToastOnMainThread("连接错误", Toast.LENGTH_SHORT);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                if ("1".equals(resp)) {
                    showToastOnMainThread("推送成功", Toast.LENGTH_LONG);
                } else if ("0".equals(resp)) {
                    showToastOnMainThread("用户身份异常", Toast.LENGTH_SHORT);
                } else if ("-1".equals(resp)) {
                    showToastOnMainThread("服务器发生异常", Toast.LENGTH_SHORT);
                } else {
                    showToastOnMainThread("未知错误", Toast.LENGTH_SHORT);
                }
            }
        });

    }

    private void checkUpdate() {
        if ((boolean) SharedPreferenceUtil.get(this, "toggle_auto_detect_update", false)) {
            VersionUtils.checkUpdate(this, this, true, main_course_state);
        }
    }

    private void checkDarkMode() {

        toggleDarkMode(UsualSharedPreferenceUtil.isDarkModeOn(this));
    }

    public void showNoLoginDarkmeInfo(boolean onMainThread, String extra) {
        String str = "您还没有登录[darkme.cn]" + extra;
        if (onMainThread) {
//            showToastOnMainThread(str, Toast.LENGTH_SHORT);
            ToastUtils.showToastOnMain(this, MainActivity.this, str, Toast.LENGTH_SHORT);
        } else {
            Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();
            ToastUtils.showToast(this, str, Toast.LENGTH_SHORT);
        }
    }

    private void goActivity(Class destClass, HashMap<String, String> extraData) {
        Intent intent = new Intent(MainActivity.this, destClass);
        if (extraData != null) {
            for (String key : extraData.keySet()) {
                intent.putExtra(key, extraData.get(key));
            }
        }
        startActivity(intent);
    }

    public boolean checkHasLoginDarkme() {
        darkme_un = (String) SharedPreferenceUtil.get(MainActivity.this, "darkme_un", "");
        return !TextUtils.isEmpty(darkme_un);
    }

    private void checkHandoffText(boolean autoDetect) {
        HttpUtil.getUserHandoffText(darkme_un, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                showToastOnMainThread("连接异常", Toast.LENGTH_SHORT);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                if (!TextUtils.isEmpty(resp)) {
                    if (!"-1".equals(resp)) {
                        showUserHandoffTextOnMain(resp);
                    } else {
                        if (!autoDetect) {
                            showToastOnMainThread("未检测到推送文本", Toast.LENGTH_SHORT);
                        }
                    }
                } else {
                    showToastOnMainThread("数据返回异常", Toast.LENGTH_SHORT);
                }
            }
        });
    }

    private void copyTextToClipboard(String text) {

        runOnUiThread(() -> {
            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData mClipData = ClipData.newPlainText("handoff_text", text);         //‘Label’这是任意文字标签
            assert cm != null;
            cm.setPrimaryClip(mClipData);
        });
    }

    private String getTextFromClipboard() {
        ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        assert cm != null;
        if (cm.hasPrimaryClip()) {
            ClipData data = cm.getPrimaryClip();
            ClipData.Item item = data.getItemAt(0);
            return item.getText().toString();
        }
        return null;
    }

    private void initRefreshLayout() {


        tweetAdapter = new TweetAdapter(this, this);
        recyclerView_layout = findViewById(R.id.main_recycle_view_refresh_layout);

        recyclerView = findViewById(R.id.main_recycle_view);
//        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.HORIZONTAL));
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setSwipeItemClickListener((itemView, position) -> {

            Tweet tweet = tweetList.get(position);
            tweetAdapter.showTweetDetail(tweet);
        });
        recyclerView_layout.setOnRefreshListener(() -> {
            TWEET_START_POSITION = 0;
            displayTweets(0, true, false);
        });
        recyclerView.useDefaultLoadMore();
//            recyclerView.setAutoLoadMore(true);
        recyclerView.setLoadMoreListener(() -> {
            TWEET_START_POSITION += 5;
            displayTweets(TWEET_START_POSITION, false, true);
        });
//
//
//
//        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
//        recyclerView.setLayoutManager(layoutManager);


        recyclerView.setAdapter(tweetAdapter);
    }

    private void displayUserInfo(int type) {
        switch (type) {
            case 1:
                showUserProfileSig();
                break;
            case 2:
                showUserProfilePicture();
                break;
            case 3:
                showUserProfilePicture();
                showUserProfileSig();
                break;
            default:
                break;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 2:
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "拒绝权限将无法使用程序", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }

//    @Override
//    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
//        displayTweets(0);
//    }
//
//    @Override
//    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
//        displayTweets(TWEET_START_POSITION);
//        return true;
//    }


    private void weatherToShowCourse() {
        hasGetCourse = (boolean) SharedPreferenceUtil.get(MainActivity.this, "hasGetCourse", false);
        if (hasGetCourse) {
            new Thread(() -> {
                weekNow = (int) SharedPreferenceUtil.get(MainActivity.this, "weekNow", 0);
                weatherAddWeek();
                data = (String) SharedPreferenceUtil.get(this, "courseSourceCode", "");
                dayCourseList.clear();
                dayCourseList = HtmlCodeExtractUtil.getCourseList(data, weekNow, weekToday);
                if (dayCourseList.size() > 0) {
                    sortCourseList(dayCourseList);
                    showNowCourse();
                } else {
                    runOnUiThread(() -> {
                        setCourseAreaContent("", "今天没有课，快去休息一下吧", "", true);
                    });
                }
            }).start();
        } else {
            setCourseAreaContent("", "NO NJIT LOGIN DETECTED", "", true);
        }
    }

    private void weatherAddWeek() {
        int weekToday = Tools.getWeek();
        Calendar cal = Calendar.getInstance();
        String key = cal.get(Calendar.YEAR) + "" + (cal.get(Calendar.MONTH) + 1) + "" + cal.get(Calendar.DAY_OF_MONTH);
        if (weekToday == 1) {
            boolean added = (boolean) SharedPreferenceUtil.get(this, key, false);
            if (!added) {
                SharedPreferenceUtil.put(this, key, true);
                weekNow++;
            }
        }
    }

    private void goFileActivity(String un) {
        Intent intent = new Intent(MainActivity.this, FileActivity.class);
        intent.putExtra("darkme_un", un);
//        intent.putExtra("darkme_pwd", pwd);
        startActivity(intent);
    }

    private void goCourseActivity() {
        if (!hasGetCourse) {
            Intent intent = new Intent(MainActivity.this, LoginNJITActivity.class);
            intent.putExtra("TODO", "COURSE");
            intent.putExtra("year", "");
            intent.putExtra("team", "");
            startActivity(intent);
        } else {
            Intent intent = new Intent(MainActivity.this, CourseActivity.class);
            intent.putExtra("sourceCode", data);
            startActivity(intent);
        }
    }

    //    对HomeAsUp按钮增加监听
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        return super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
                break;
        }
    }


    //    加载toolbar.xml
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
//        if (checkHasLoginDarkme()) {
//            menu.findItem(R.id.nav_home).setVisible(false);
//        }
        return true;
    }


    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
//        unbindService(connection);
        if (!(boolean) SharedPreferenceUtil.get(MainActivity.this, "remember_password_darkme", false)) {
            SharedPreferenceUtil.put(MainActivity.this, "darkme_un", "");
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (checkHandoffFun()) {
            checkHandoffText(true);
        }
        Intent intent = getIntent();
        String toDo = intent.getStringExtra("TODO");
//        if ("UPDATE_COURSE".equals(toDo)) {
//            weatherToShowCourse();
//        }
//        } else if ("FLUSH_TWEETS".equals(toDo)) {
//            String result = intent.getStringExtra("RESULT");
//            if ("1".equals(result)) {
//                showToast("发布成功", Toast.LENGTH_LONG);
//                displayTweets();
//            } else if ("0".equals(result)) {
//                showToast("发布失败，服务器异常", Toast.LENGTH_LONG);
//            } else if ("-1".equals(result)) {
//                showToast("网络错误，请稍后重试", Toast.LENGTH_LONG);
//            } else {
//                showToast("未知错误", Toast.LENGTH_LONG);
//            }
//        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constant.IMAGE_REQUEST_CODE_CROP:
                    ImageTools.cropRawPhoto(data.getData(), MainActivity.this, this, true);
                    break;
                case UCrop.REQUEST_CROP:
                    Uri uri = UCrop.getOutput(data);
                    try {

                        Luban.with(this) // 初始化
                                .load(uri) // 要压缩的图片
                                .setCompressListener(new OnCompressListener() {
                                    @Override
                                    public void onStart() {
                                        // 压缩开始前调用 可以在方法内启动loading UI
                                    }

                                    @Override
                                    public void onSuccess(File file) {
                                        // 压缩成功后调用，返回压缩后的图片文件
                                        Bitmap bitmap = ImageTools.getSmallBitmap(file.getAbsolutePath(), true);
                                        Glide.with(MainActivity.this).load(bitmap).into(whole_head_image);
                                        saveAndUploadUserHeadImage(bitmap, darkme_un);
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        // 压缩过程中出现异常
                                    }
                                }).launch(); // 启动压缩


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }


    private void showUserProfilePicture() {
        darkme_un = (String) SharedPreferenceUtil.get(MainActivity.this, "darkme_un", "");
        if (!"".equals(darkme_un)) {
//            Bitmap bitmap = SharedPreferenceUtil.getImage(this, "profilePic");
//            Glide.with(MainActivity.this).load(ImageTools.compressImage(bitmap)).into(whole_head_image);
            HttpUtil.getUserProfilePicture(darkme_un, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String resp = response.body().string();
//                    response.
                    if (!TextUtils.isEmpty(resp)) {
                        if (!"0".equals(resp)) {
                            Bitmap bitmap = ImageTools.compressImage(Base64ImageUtils.base64StrToBitmap(resp));
                            runOnUiThread(() -> {
                                Glide.with(MainActivity.this).load(bitmap).into(whole_head_image);
//                                Dali.create(MainActivity.this).load(bitmap).blurRadius(5).into(head_image_bg);
                            });
                        } else {
                            runOnUiThread(() -> Glide.with(MainActivity.this).load(R.drawable.default_head).into(whole_head_image));

                        }
                    }
                }
            });
        } else {
            Glide.with(MainActivity.this).load(R.drawable.default_head).into(whole_head_image);
        }
    }

    private void saveAndUploadUserHeadImage(Bitmap bitmap, String darkme_un) {
        SharedPreferenceUtil.put(MainActivity.this, "diy_head_image", true);
        SharedPreferenceUtil.putImage(MainActivity.this, "profilePic", Objects.requireNonNull(bitmap));
        HttpUtil.setUserProfilePicture(darkme_un, bitmap, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                if ("1".equals(resp)) {
                    showToast("头像更新成功", Toast.LENGTH_LONG);
                } else {
                    showToast("头像更新失败", Toast.LENGTH_SHORT);
                }
                userInfoChangeFlag = true;
            }
        });
    }

    private void showUserProfileSig() {
//        userInfoChangeFlag = true;
        String sig = (String) SharedPreferenceUtil.get(MainActivity.this, "signature", "");
        if (!"".equals(sig)) {
            whole_id.setText(sig);
        } else {
            whole_id.setText(Constant.no_sig_info);
            whole_id.setTextColor(Color.BLACK);
        }
    }


    private boolean checkHandoffFun() {
        return checkHasLoginDarkme() && (boolean) SharedPreferenceUtil.get(this, "toggle_handoff", false);
    }

    private void showBg() {
        new Thread(() -> {
            if (!(boolean) SharedPreferenceUtil.get(MainActivity.this, "toggle_diy_main_bg", false)) {
                showDefaultBg();
            } else {
                Bitmap bitmap = SharedPreferenceUtil.getImage(MainActivity.this, "main_bg");
                if (bitmap != null) {
                    Bitmap bitmap1 = ImageTools.compressImage(bitmap);
                    if ((boolean) SharedPreferenceUtil.get(MainActivity.this, "blurBackground", false)) {
                        runOnUiThread(() -> Dali.create(MainActivity.this).load(bitmap1).blurRadius(8).into(bing_main_pic));
                    } else {
                        runOnUiThread(() -> Glide.with(MainActivity.this).load(bitmap1).into(bing_main_pic));
                    }
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(MainActivity.this, "自定义背景加载失败", Toast.LENGTH_SHORT).show();
                        SharedPreferenceUtil.put(MainActivity.this, "diy_main_bg", false);
                        showDefaultBg();
                    });
                }
            }
        }).start();
    }


    private void toggleDarkMode(boolean on) {
        TextView tweetTitle = findViewById(R.id.main_tweet_title);
        TextView courseTitle = findViewById(R.id.main_course_title);
        RelativeLayout courseLayout = findViewById(R.id.course_recent);

        if (on) {

            tweetTitle.setTextColor(getResources().getColor(R.color.white));
            courseTitle.setTextColor(getResources().getColor(R.color.white));
            courseLayout.setBackgroundResource(0);
            courseTitle.setBackgroundResource(0);

            main_course_name.setTextColor(getResources().getColor(R.color.white));
            main_course_location.setTextColor(getResources().getColor(R.color.white));
            main_course_state.setTextColor(getResources().getColor(R.color.white));

        } else {

        }

    }


    public void showToast(String c, int t) {
        Message msg = new Message();
        msg.what = Constant.SHOW_TOAST;
        msg.obj = c;
        msg.arg1 = t;
        handler.sendMessage(msg);
    }

    public void showDefaultBg() {
        AtomicInteger blurDegree = new AtomicInteger();
        AtomicBoolean useGlideFlag = new AtomicBoolean(false);
        AtomicBoolean loadDirectly = new AtomicBoolean(false);
        new Thread(() -> {
            Bitmap mainBit = null;
            int type = (int) SharedPreferenceUtil.get(MainActivity.this, "default_main_bg_num", 0);
            switch (type) {
                case 0:
                    blurDegree.set(7);
                    mainBit = ImageTools.srcPicToBitmap(getResources(), R.drawable.main_bg0, false);
                    break;
                case 1:
                    loadDirectly.set(true);
                    runOnUiThread(() -> Glide.with(MainActivity.this).load(R.drawable.main_bg1).into(bing_main_pic));
                    break;
                case 2:
                    useGlideFlag.set(true);
                    mainBit = ImageTools.srcPicToBitmap(getResources(), R.drawable.main_bg2, true);
                    break;
                case 3:
                    blurDegree.set(8);
                    mainBit = ImageTools.srcPicToBitmap(getResources(), R.drawable.main_bg3, false);
//                    loadDirectly.set(true);
//                    runOnUiThread(() -> Glide.with(MainActivity.this).load(R.drawable.main_bg3).into(bing_main_pic));
                    break;
                case 4:
//                    blurDegree.set(8);
                    useGlideFlag.set(true);
                    mainBit = ImageTools.srcPicToBitmap(getResources(), R.drawable.main_bg4, false);
                    break;
                default:
                    blurDegree.set(8);
//                    useGlideFlag.set(true);
                    mainBit = ImageTools.srcPicToBitmap(getResources(), R.drawable.main_bg0, false);
                    break;
            }
            Bitmap finalMainBit = mainBit;
            runOnUiThread(() -> {
                if (!loadDirectly.get()) {
                    if (!useGlideFlag.get()) {
                        Blurry.with(MainActivity.this).radius(blurDegree.get()).from(finalMainBit).into(bing_main_pic);
                    } else {
                        Glide.with(MainActivity.this).load(finalMainBit).into(bing_main_pic);
                    }
                }
            });

        }).start();
    }


    //    按照课程顺序冒泡排序
    private void sortCourseList(List<Course> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = 0; j < list.size() - 1; j++) {
                if (list.get(j).getClsNum() > list.get(j + 1).getClsNum()) {
                    Course course = list.get(j);
                    list.set(j, list.get(j + 1));
                    list.set(j + 1, course);
                }
            }
        }
    }

    private void setCourseAreaContent(String s1, String s2, String s3, boolean isCenter) {

        runOnUiThread(() -> {
            if (isCenter) {
                main_course_name.setGravity(Gravity.CENTER);
                main_course_location.setGravity(Gravity.CENTER);
                main_course_state.setGravity(Gravity.CENTER);
                main_course_name.setPadding(0, 0, 0, 0);
                main_course_location.setPadding(0, 0, 0, 0);
                main_course_state.setPadding(0, 0, 0, 0);
            } else {
                main_course_name.setGravity(Gravity.LEFT);
                main_course_location.setGravity(Gravity.LEFT);
                main_course_state.setGravity(Gravity.LEFT);

                main_course_name.setPadding(30, 0, 0, 0);
                main_course_name.setPadding(30, 0, 0, 0);
                main_course_location.setPadding(30, 0, 0, 0);
                main_course_state.setPadding(30, 0, 0, 0);
            }
            main_course_name.setText(s1);
            main_course_location.setText(s2);
            main_course_state.setText(s3);
        });
    }

    private void displayTweets(int startPosition, boolean refresh, boolean loadMore) {
        if (refresh) {
            recyclerView_layout.setRefreshing(true);
        }
        HttpUtil.getAllTweets(startPosition, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, IOException e) {
                Message msg = new Message();
                msg.what = 0;
                handler.sendMessage(msg);
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String data = response.body().string();
                if (!"0".equals(data)) {
                    List<Tweet> tweets = gson.fromJson(data, new TypeToken<List<Tweet>>() {
                    }.getType());
                    if (refresh) {
                        tweetList.clear();
                    }
                    tweetList.addAll(tweets);
                    runOnUiThread(() -> {
                        tweetAdapter.setData(tweetList);
                        recyclerView.loadMoreFinish(false, true);
                    });
                } else {
                    runOnUiThread(() -> {
                        recyclerView.loadMoreFinish(true, false);
                    });
                }
                runOnUiThread(() -> {
                    if (refresh) {
                        recyclerView_layout.setRefreshing(false);
                    }
                });
            }
        });
    }


    private void showNowCourse() {
        new Thread(() -> {
            if (hasGetCourse) {
                if (null != dayCourseList && dayCourseList.size() > 0) {
                    calendar = Calendar.getInstance();
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);
                    int dayOfMinute = hour * 60 + minute;

                    final Course course = dayCourseList.get(0);
                    int[] times = decideWhatToShow(course.getClsNum());
                    System.out.println(Arrays.toString(times));
                    System.out.println(dayOfMinute);
                    final StringBuilder state = new StringBuilder();
                    if (dayOfMinute < times[0]) {
                        state.append("状态：未开始, 距离上课: ").append((times[0] - dayOfMinute) / 60).append(" h ").append((times[0] - dayOfMinute) % 60).append(" min");
                    } else if (dayOfMinute < times[1]) {
                        state.append("状态：进行第-1-节课, 距离下课: ").append(times[1] - dayOfMinute).append(" min");
                    } else if (dayOfMinute < times[2]) {
                        state.append("状态：课间休息, 距离上课: ").append(times[2] - dayOfMinute).append(" min");
                    } else if (dayOfMinute < times[3]) {
                        state.append("状态：进行第-2-节课, 距离下课: ").append(times[3] - dayOfMinute).append(" min");
                    } else if (times[4] == 0) {
                        dayCourseList.remove(0);
                        if (dayCourseList.size() > 0) {
                            showNowCourse();
                        } else {
                            Thread.interrupted();
                            setCourseAreaContent("", Constant.main_no_course_left_info, "", true);
                            flag3 = false;
                            return;
                        }
                    } else if (dayOfMinute >= times[3] && dayOfMinute < times[4]) {
                        state.append("状态：课间休息, 距离上课：").append(times[4] - dayOfMinute).append(" min");
                    } else if (dayOfMinute >= times[4] && dayOfMinute < times[5]) {
                        state.append("状态：进行第-3-节课, 距离下课: ").append(times[5] - dayOfMinute).append(" min");
                    } else if (dayOfMinute >= times[5]) {
                        dayCourseList.remove(0);
                        if (dayCourseList.size() > 0) {
                            showNowCourse();
                        } else {
                            setCourseAreaContent("", Constant.main_no_course_left_info, "", true);
                            return;
                        }
                    }
                    setCourseAreaContent("名称：" + course.getDialog_name(), "地点：" + course.getDialog_location(), state.toString(), false);
                } else {
                    setCourseAreaContent("", Constant.main_no_course_left_info, "", true);
                    flag3 = false;
                }
            }

        }).start();
    }

    private int[] decideWhatToShow(int when) {

        int[] time = new int[6];
        switch (when) {
            case 1:
                time[0] = 8 * 60;
                time[1] = 8 * 60 + 45;
                time[2] = 8 * 60 + 55;
                time[3] = 9 * 60 + 40;
                time[4] = 0;
                time[5] = 0;
                break;
            case 3:
                time[0] = 10 * 60 + 10;
                time[1] = 10 * 60 + 55;
                time[2] = 11 * 60 + 5;
                time[3] = 11 * 60 + 50;
                time[4] = 0;
                time[5] = 0;
                break;
            case 5:
                time[0] = 13 * 60 + 40;
                time[1] = 14 * 60 + 25;
                time[2] = 14 * 60 + 35;
                time[3] = 15 * 60 + 20;
                time[4] = 0;
                time[5] = 0;
                break;
            case 7:
                time[0] = 15 * 60 + 40;
                time[1] = 16 * 60 + 25;
                time[2] = 16 * 60 + 35;
                time[3] = 17 * 60 + 20;
                time[4] = 0;
                time[5] = 0;
                break;
            case 9:
                time[0] = 18 * 60 + 30;
                time[1] = 19 * 60 + 15;
                time[2] = 19 * 60 + 25;
                time[3] = 20 * 60 + 10;
                time[4] = 20 * 60 + 20;
                time[5] = 21 * 60 + 5;
                break;
            default:
                break;
        }
        return time;
    }

//    public void beginRefreshing() {
//        recyclerView_layout.beginRefreshing();
//    }
//
//    // 通过代码方式控制进入加载更多状态
//    public void beginLoadingMore() {
//        recyclerView_layout.beginLoadingMore();
//    }

    private void showUserHandoffTextOnMain(String text) {
        runOnUiThread(() -> showUserHandoffText(text));
    }

    private void showUserHandoffText(String text) {
        MaterialDialogUtils.showYesOrNoDialogWithBothSthTodo(this, new String[]{"连续互通", "来自darkme.cn[ user: " + darkme_un + " ]\n" + text, "复制到粘贴板", "忽略"}, new MaterialDialogUtils.DialogBothDoSthOnClickListener() {
            @Override
            public void onConfirmButtonClick() {
                copyTextToClipboard(text);
                ToastUtils.showToast(MainActivity.this, "复制成功", Toast.LENGTH_LONG);
            }

            @Override
            public void onCancelButtonClick() {
            }
        }, true);

        new Thread(() -> HttpUtil.haveReceivedHandOffText(darkme_un)).start();
    }

    class TimeChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (Objects.requireNonNull(intent.getAction())) {
                case Intent.ACTION_TIME_TICK:
                    if (flag3) {
                        showNowCourse();
                    }
                    if (!TextUtils.isEmpty(darkme_un) && checkHandoffFun()) {
                        checkHandoffText(true);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    class MainBgChangedReceiver extends BroadcastReceiver {
        //        各种操作
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constant.ACTION1)) {
                SharedPreferenceUtil.put(MainActivity.this, "diy_main_bg", true);
                runOnUiThread(() -> {
                    Bitmap bitmap = SharedPreferenceUtil.getImage(MainActivity.this, "main_bg");
                    if (bitmap != null) {
                        if ((boolean) SharedPreferenceUtil.get(MainActivity.this, "blurBackground", false)) {
                            Dali.create(MainActivity.this).load(bitmap).blurRadius(6).into(bing_main_pic);
                        } else {
                            bing_main_pic.setImageBitmap(bitmap);
                        }
                        Toast.makeText(MainActivity.this, "更换成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "更换失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            if (action.equals(Constant.ACTION2)) {
//                SharedPreferenceUtil.put(MainActivity.this, "main_bg", null);
                runOnUiThread(MainActivity.this::showDefaultBg);
            }

            if (action.equals(Constant.ACTION3)) {
//                SharedPreferenceUtil.put(MainActivity.this, "main_bg", null);
                flag3 = true;
                weatherToShowCourse();
            }
        }
    }

    public void showToastOnMainThread(String text, int t) {
        runOnUiThread(() -> ToastUtils.showToast(MainActivity.this, text, t));
    }

}
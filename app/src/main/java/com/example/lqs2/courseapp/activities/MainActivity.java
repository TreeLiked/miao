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
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
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
import com.example.lqs2.courseapp.global.GlideApp;
import com.example.lqs2.courseapp.global.ThreadPoolExecutorFactory;
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
import java.util.Calendar;
import java.util.Date;
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

/**
 * 主页
 *
 * @author lqs2
 */
@SuppressWarnings("AlibabaUndefineMagicConstant")
public class MainActivity extends ActivityCollector implements View.OnClickListener {


    public static final String TAG = "MainActivity";

    public static int weekToday = Tools.getWeek();
    public int weekNow;


    /**
     * 是否用户修改了信息
     */
    public static boolean userInfoChangeFlag = false;
    /**
     * 是否还需要显示今日课程
     */
    public static boolean flag3 = true;
    List<Course> dayCourseList = new LinkedList<>();
    /**
     * 当前动态的起始位置
     */
    public static int TWEET_START_POSITION = 0;
    /**
     * 广播
     */
    public static LocalBroadcastManager localBroadcastManager;
    /**
     * 背景的类型，是否切换暗色系列
     */
    public static int bgType = 0;
    /**
     * drawer第一次打开查找view
     */
    private boolean initFlag = true;
    private boolean hasGetCourse;
    private String data;
    private Toast toast;
    private TextView wholeId;
    private CircleImageView wholeHeadImage;

    public static MainActivity activity;
    public static Context context;


    private DrawerLayout drawerLayout;
    private ImageView bingMainPic;
    private TextView mainCourseName;
    private TextView mainCourseLocation;
    private TextView mainCourseState;
    private SwipeRefreshLayout recyclerViewLayout;
    private SwipeMenuRecyclerView recyclerView;
    private TweetAdapter tweetAdapter;
    private Calendar calendar;
    private List<Tweet> tweetList = new ArrayList<>();
    private static Gson gson = new Gson();


    private String darkmeUn = "";

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {

        @SuppressLint("ShowToast")
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
                    break;
                case 0:
                    recyclerViewLayout.setRefreshing(false);
                    Toast.makeText(MainActivity.this, "Failed to load tweets", Toast.LENGTH_LONG).show();
                    break;
                case 1:
                    Toast.makeText(MainActivity.this, "喵～", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }
    };

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StatusBarUtils.setStatusTransparent(this);
        context = this;
        activity = this;

        IntentFilter intentFilter = new IntentFilter();
        //每分钟变化
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        TimeChangeReceiver timeChangeReceiver = new TimeChangeReceiver();
        registerReceiver(timeChangeReceiver, intentFilter);


        localBroadcastManager = LocalBroadcastManager.getInstance(MainActivity.this);
        MainBgChangedReceiver bgChangedReceiver = new MainBgChangedReceiver();
        IntentFilter intentFilter1 = new IntentFilter();
        intentFilter1.addAction(Constant.ACTION1);
        intentFilter1.addAction(Constant.ACTION2);
        intentFilter1.addAction(Constant.ACTION3);
        localBroadcastManager.registerReceiver(bgChangedReceiver, intentFilter1);


        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        bingMainPic = findViewById(R.id.main_pic);
        mainCourseName = findViewById(R.id.main_course_name);
        mainCourseLocation = findViewById(R.id.main_course_location);
        mainCourseState = findViewById(R.id.main_course_state);
        RelativeLayout layout = findViewById(R.id.main_course_layout);


        showBg();
        checkDarkMode();
        checkUpdate();


        darkmeUn = (String) SharedPreferenceUtil.get(MainActivity.this, "darkme_un", "");

        initRefreshLayout();

        weatherToShowCourse();
        displayTweets(TWEET_START_POSITION, false, false);


        navigationView.setCheckedItem(R.id.nav_course);
        final com.getbase.floatingactionbutton.FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(this);
        fab.setOnLongClickListener(v -> {
            MaterialDialog[] dialog = new MaterialDialog[1];
            dialog[0] = MaterialDialogUtils.getItemListDialog(MainActivity.this, "连续互通", (parent, view, position, id) -> {
                switch (position) {
                    case 0:
                        dialog[0].dismiss();
                        if (!TextUtils.isEmpty(darkmeUn)) {
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
                        if (!TextUtils.isEmpty(darkmeUn)) {
                            if ((boolean) SharedPreferenceUtil.get(this, "toggle_handoff", false)) {
                                String clipText = getTextFromClipboard();
                                if (!TextUtils.isEmpty(clipText)) {
                                    MaterialDialogUtils.showYesOrNoDialogWithBothSthTodo(this, new String[]{"检测到文本", clipText, "直接推送", "打开推送页面"}, new MaterialDialogUtils.AbstractDialogBothDoSthOnClickListener() {
                                        @Override
                                        public void onConfirmButtonClick() {
                                            pushHandoffText(darkmeUn, clipText);
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
                    wholeId = findViewById(R.id.whole_id);
                    wholeHeadImage = findViewById(R.id.whole_head_image);
                    displayUserInfo(3);
                    wholeHeadImage.setOnClickListener(v -> {
                        darkmeUn = (String) SharedPreferenceUtil.get(MainActivity.this, "darkme_un", "");
                        if (!TextUtils.isEmpty(darkmeUn)) {
                            CropUtils.openAlbumAndCrop(MainActivity.this);
                        } else {
                            showNoLoginDarkmeInfo(false, "，无法上传头像");
                        }
                    });
                    wholeId.setOnClickListener(v -> {
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
                case R.id.nav_school_notice:
                    goActivity(NoticeActivity.class, null);
                    break;

                case R.id.nav_library:
                    goActivity(LibraryActivity.class, null);
                    break;
                case R.id.nav_grade:
                    final MaterialDialog[] dialog = new MaterialDialog[1];
                    dialog[0] = MaterialDialogUtils.getItemListDialog(this, "查询选项", (parent, view, position, id) -> {
                        dialog[0].dismiss();
                        switch (position) {
                            case 0:
                                Toast.makeText(MainActivity.this, Constant.GRADE_QUERY_WELCOME, Toast.LENGTH_SHORT).show();
                                goActivity(LoginNjitActivity.class, new HashMap<String, String>() {{
                                    put("TODO", "GRADE");
                                }});
                                break;
                            case 1:
                                Toast.makeText(MainActivity.this, Constant.CREDIT_QUERY_WELCOME, Toast.LENGTH_SHORT).show();
                                goActivity(LoginNjitActivity.class, new HashMap<String, String>() {{
                                    put("TODO", "CREDIT");
                                }});
                                break;
                            case 3:
                                break;
                            default:
                                break;
                        }
                    }, new ArrayList<String>() {{
                        add(Constant.GRADE_QUERY_ITEM1);
                        add(Constant.GRADE_QUERY_ITEM2);
//                        add(Constant.GRADE_QUERY_ITEM3);
                        add(Constant.GRADE_QUERY_ITEM4);
                    }});
                    dialog[0].show();
                    break;
                case R.id.nav_cloud_file:

                    if (checkHasLoginDarkme()) {
                        goFileActivity(darkmeUn);
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
                        View finalView = loadView(R.layout.darkme_login);
                        assert finalView != null;
                        CheckBox rem = finalView.findViewById(R.id.darkme_rememberme);
                        EditText eText1 = finalView.findViewById(R.id.darkme_login_un);
                        EditText eText2 = finalView.findViewById(R.id.darkme_login_pwd);
                        eText1.setText((String) SharedPreferenceUtil.get(this, "darkme_un", ""));

                        new com.afollestad.materialdialogs.MaterialDialog.Builder(this)
                                .title("登录 [ darkme.cn ] ")
                                .customView(finalView, true)
                                .positiveColor(ContextCompat.getColor(context, R.color.r4))
                                .positiveText("登录")
                                .negativeColor(getResources().getColor(R.color.r7))
                                .negativeText("注册")
                                .onAny((dialog1, which) -> {
                                    if (which == DialogAction.POSITIVE) {
                                        String t1 = eText1.getText().toString();
                                        String t2 = eText2.getText().toString();
                                        if (!TextUtils.isEmpty(t1)) {
                                            try {
                                                HttpUtil.userValidateDarkMe(t1, t2, new Callback() {
                                                    @Override
                                                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                                        dialog1.dismiss();
                                                        showToast("服务异常", Toast.LENGTH_SHORT);
                                                        e.printStackTrace();
                                                    }

                                                    @Override
                                                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                                        assert response.body() != null;
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
                                            } catch (Exception e) {
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
                                                default:
                                                    break;
                                            }
                                        });
                                        final MaterialDialog[] dialog2 = new MaterialDialog[1];
                                        dialog2[0] = MaterialDialogUtils.showYesOrNoDialogWithAll(this, new String[]{"注册 [ darkme.cn ] ", "", "注册", "取消"}, view1, -1, new MaterialDialogUtils.AbstractDialogBothDoSthOnClickListener() {
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
                                                                assert response.body() != null;
                                                                String resp = response.body().string();
                                                                if (!TextUtils.isEmpty(resp)) {
                                                                    if ("1".equals(resp)) {
                                                                        dialog2[0].dismiss();
                                                                        runOnUiThread(() -> ToastUtils.showToast(MainActivity.this, "注册成功[" + t1 + "]", Toast.LENGTH_LONG));
                                                                    } else if ("0".equals(resp)) {
                                                                        runOnUiThread(() -> ToastUtils.showToast(MainActivity.this, "[" + t1 + "]已经被使用", Toast.LENGTH_LONG));
                                                                    } else {
                                                                        runOnUiThread(() -> ToastUtils.showToast(MainActivity.this, "服务器发生异常", Toast.LENGTH_SHORT));
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

                    } else {
                        MaterialDialogUtils.showYesOrNoDialogWithBothSthTodo(this, new String[]{"已经登录的账号", "[" + darkmeUn + "]", "关闭", "注销登录"}, new MaterialDialogUtils.AbstractDialogBothDoSthOnClickListener() {
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
                        intent3.putExtra("darkme_un", darkmeUn);
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
                        intent1.putExtra("darkme_un", darkmeUn);
                        startActivity(intent1);
                    } else {
                        showNoLoginDarkmeInfo(false, "，无法使用备忘录功能");
                    }
                    break;

                case R.id.nav_settings:
                    Intent setIntent = new Intent(MainActivity.this, SettingActivity.class);
                    startActivity(setIntent);
                    break;
                default:
                    break;
            }
            drawerLayout.closeDrawers();
            return true;
        });

    }

    /**
     * 打开handoff功能页面
     */
    private void openPushHandoffTextPage() {
        View view1;
        LayoutInflater inflater = LayoutInflater.from(this);
        view1 = inflater.inflate(R.layout.darkme_post_handoff_text, null);
        EditText editText = view1.findViewById(R.id.darkme_post_handoff_text_edit);
        MaterialDialogUtils.showYesOrNoDialogWithCustomView(MainActivity.this, new String[]{"推送文本到[darkme.cn]", "", "确认", "退出"}, view1, new MaterialDialogUtils.AbstractDialogOnConfirmClickListener() {
            @Override
            public void onConfirmButtonClick() {
                String str = editText.getText().toString();
                if (TextUtils.isEmpty(str)) {
                    ToastUtils.showToast(MainActivity.this, "未检测到文本" + str, Toast.LENGTH_LONG);
                }
            }
        }, true);
    }

    /**
     * 推送handoff文本
     *
     * @param un   账户名
     * @param text 需要推送的文本
     */
    private void pushHandoffText(String un, String text) {
        HttpUtil.setUserHandoffText(un, text, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                showToast("连接错误", Toast.LENGTH_SHORT);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                String resp = response.body().string();
                if ("1".equals(resp)) {
                    showToast("推送成功", Toast.LENGTH_LONG);
                } else if ("0".equals(resp)) {
                    showToast("用户身份异常", Toast.LENGTH_SHORT);
                } else if ("-1".equals(resp)) {
                    showToast("服务器发生异常", Toast.LENGTH_SHORT);
                } else {
                    showToast("未知错误", Toast.LENGTH_SHORT);
                }
            }
        });

    }

    /**
     * 检查更新
     */
    private void checkUpdate() {
        if ((boolean) SharedPreferenceUtil.get(this, "toggle_auto_detect_update", false)) {
            VersionUtils.checkUpdate(this, this, true, mainCourseState);
        }
    }

    /**
     * 检查是否开启了黑暗模式
     */
    private void checkDarkMode() {

        toggleDarkMode(UsualSharedPreferenceUtil.isDarkModeOn(this));
    }

    /**
     * 显示未登录darkme toast
     *
     * @param onMainThread 是否切换到主线程
     * @param extra        附加信息
     */
    public void showNoLoginDarkmeInfo(boolean onMainThread, String extra) {
        String str = "您还没有登录[darkme.cn]" + extra;
        if (onMainThread) {
//            showToast(str, Toast.LENGTH_SHORT);
            ToastUtils.showToastOnMain(this, MainActivity.this, str, Toast.LENGTH_SHORT);
        } else {
            Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();
            ToastUtils.showToast(this, str, Toast.LENGTH_SHORT);
        }
    }

    /**
     * 前往指定的活动
     *
     * @param destClass 目的活动
     * @param extraData intent中需要携带的数据
     */
    private void goActivity(Class destClass, HashMap<String, String> extraData) {
        Intent intent = new Intent(MainActivity.this, destClass);
        if (extraData != null) {
            for (String key : extraData.keySet()) {
                intent.putExtra(key, extraData.get(key));
            }
        }
        startActivity(intent);
    }

    /**
     * 检查是否登录了darkme
     *
     * @return 是/否
     */
    public boolean checkHasLoginDarkme() {
        darkmeUn = (String) SharedPreferenceUtil.get(MainActivity.this, "darkme_un", "");
        return !TextUtils.isEmpty(darkmeUn);
    }

    /**
     * 如果打开了handoff功能，则检查是否有推送到手机的文本
     *
     * @param autoDetect 是否自动/手动，如果true，则显示toast
     */
    private void checkHandoffText(boolean autoDetect) {
        HttpUtil.getUserHandoffText(darkmeUn, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                showToast("连接异常", Toast.LENGTH_SHORT);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                String resp = response.body().string();
                if (!TextUtils.isEmpty(resp)) {
                    if (!"-1".equals(resp)) {
                        showUserHandoffTextOnMain(resp);
                    } else {
                        if (!autoDetect) {
                            showToast("未检测到推送文本", Toast.LENGTH_SHORT);
                        }
                    }
                } else {
                    showToast("数据返回异常", Toast.LENGTH_SHORT);
                }
            }
        });
    }

    /**
     * 复制文本到粘贴板
     *
     * @param text 所复制的文本
     */
    private void copyTextToClipboard(String text) {

        runOnUiThread(() -> {
            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData mClipData = ClipData.newPlainText("handoff_text", text);
            assert cm != null;
            cm.setPrimaryClip(mClipData);
        });
    }

    /**
     * 从粘贴板获取文本
     *
     * @return 粘贴板的文本
     */
    private String getTextFromClipboard() {
        ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        assert cm != null;
        if (cm.hasPrimaryClip()) {
            ClipData data = cm.getPrimaryClip();
            assert data != null;
            ClipData.Item item = data.getItemAt(0);
            return item.getText().toString();
        }
        return null;
    }

    /**
     * 初始化recyclerView的外层布局
     */
    private void initRefreshLayout() {


        tweetAdapter = new TweetAdapter(this, this);
        recyclerViewLayout = findViewById(R.id.main_recycle_view_refresh_layout);

        recyclerView = findViewById(R.id.main_recycle_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setSwipeItemClickListener((itemView, position) -> {

            Tweet tweet = tweetList.get(position);
            tweetAdapter.showTweetDetail(tweet);
        });
        recyclerViewLayout.setOnRefreshListener(() -> {
            TWEET_START_POSITION = 0;
            displayTweets(0, true, false);
        });
        recyclerView.useDefaultLoadMore();
        recyclerView.setLoadMoreListener(() -> {
            TWEET_START_POSITION += 5;
            displayTweets(TWEET_START_POSITION, false, true);
        });

        recyclerView.setAdapter(tweetAdapter);
    }

    /**
     * 用户修改信息后调用
     *
     * @param type 信息的类型
     */
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


    /**
     * 是否需要展示课程
     */
    private void weatherToShowCourse() {
        hasGetCourse = (boolean) SharedPreferenceUtil.get(MainActivity.this, "hasGetCourse", false);
        if (hasGetCourse) {
            ThreadPoolExecutorFactory.getThreadPoolExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    weekNow = (int) SharedPreferenceUtil.get(MainActivity.this, "weekNow", 0);
                    weatherAddWeek();
                    data = (String) SharedPreferenceUtil.get(MainActivity.this, "courseSourceCode", "");
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
                }
            });
        } else {
            setCourseAreaContent("", "No NJIT LOGIN DETECTED", "", true);
        }
    }

    /**
     * 如果周一则增加课程
     */
    private void weatherAddWeek() {
        int weekToday = Tools.getWeek();
        Calendar cal = Calendar.getInstance();
        String key = cal.get(Calendar.YEAR) + "" + (cal.get(Calendar.MONTH) + 1) + "" + cal.get(Calendar.DAY_OF_MONTH);
        if (weekToday == 1) {
            boolean added = (boolean) SharedPreferenceUtil.get(this, key, false);
            if (!added) {
                SharedPreferenceUtil.put(this, key, true);
                weekNow++;
                SharedPreferenceUtil.put(this, "weekNow", weekNow);
            }
        } else {
            Date weekMonday = Tools.getThisWeekMonday(new Date());
            cal.setTime(weekMonday);
            String key1 = cal.get(Calendar.YEAR) + "" + (cal.get(Calendar.MONTH) + 1) + "" + cal.get(Calendar.DAY_OF_MONTH);
            boolean added = (boolean) SharedPreferenceUtil.get(this, key1, false);
            if (!added) {
                SharedPreferenceUtil.put(this, key1, true);
                weekNow++;
                SharedPreferenceUtil.put(this, "weekNow", weekNow);
            }
        }
    }

    /**
     * 前往文件活动
     *
     * @param un 账户名
     */
    private void goFileActivity(String un) {
        Intent intent = new Intent(MainActivity.this, FileActivity.class);
        intent.putExtra("darkme_un", un);
        startActivity(intent);
    }

    /**
     * 前往课程
     */
    private void goCourseActivity() {
        if (!hasGetCourse) {
            Intent intent = new Intent(MainActivity.this, LoginNjitActivity.class);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
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

                        Luban.with(this)
                                .load(uri)
                                .setCompressListener(new OnCompressListener() {
                                    @Override
                                    public void onStart() {
                                        // 压缩开始前调用 可以在方法内启动loading UI
                                    }

                                    @Override
                                    public void onSuccess(File file) {
                                        // 压缩成功后调用，返回压缩后的图片文件
                                        Bitmap bitmap = ImageTools.getSmallBitmap(file.getAbsolutePath(), true);
                                        GlideApp.with(MainActivity.this).load(bitmap).into(wholeHeadImage);
                                        saveAndUploadUserHeadImage(bitmap, darkmeUn);
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        // 压缩过程中出现异常
                                    }
                                }).launch();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    }


    /**
     * 展示用户头像
     */
    private void showUserProfilePicture() {
        darkmeUn = (String) SharedPreferenceUtil.get(MainActivity.this, "darkme_un", "");
        if (!"".equals(darkmeUn)) {
            HttpUtil.getUserProfilePicture(darkmeUn, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    assert response.body() != null;
                    String resp = response.body().string();
//                    response.
                    if (!TextUtils.isEmpty(resp)) {
                        if (!"0".equals(resp)) {
                            Bitmap bitmap = ImageTools.compressImage(Base64ImageUtils.base64StrToBitmap(resp));
                            runOnUiThread(() -> {
                                GlideApp.with(MainActivity.this).load(bitmap).into(wholeHeadImage);
                            });
                        } else {
                            runOnUiThread(() -> Glide.with(MainActivity.this).load(R.drawable.default_head).into(wholeHeadImage));
                        }
                    }
                }
            });
        } else {
            GlideApp.with(MainActivity.this).load(R.drawable.default_head).into(wholeHeadImage);
        }
    }

    /**
     * 保存并且上传用户头像
     *
     * @param bitmap   头像位图
     * @param darkmeUn 账户名
     */
    private void saveAndUploadUserHeadImage(Bitmap bitmap, String darkmeUn) {
        SharedPreferenceUtil.put(MainActivity.this, "diy_head_image", true);
        SharedPreferenceUtil.putImage(MainActivity.this, "profilePic", Objects.requireNonNull(bitmap));
        HttpUtil.setUserProfilePicture(darkmeUn, bitmap, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
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

    /**
     * 显示用户签名
     */
    private void showUserProfileSig() {
//        userInfoChangeFlag = true;
        String sig = (String) SharedPreferenceUtil.get(MainActivity.this, "signature", "");
        if (!"".equals(sig)) {
            wholeId.setText(sig);
        } else {
            wholeId.setText(Constant.NO_SIG_INFO);
            wholeId.setTextColor(Color.BLACK);
        }
    }


    /**
     * 检查handoff功能是否打开
     *
     * @return 是/否
     */
    private boolean checkHandoffFun() {
        return checkHasLoginDarkme() && (boolean) SharedPreferenceUtil.get(this, "toggle_handoff", false);
    }

    /**
     * 显示主页背景
     */
    private void showBg() {
        ThreadPoolExecutorFactory.getThreadPoolExecutor().execute(() -> {
            if (!(boolean) SharedPreferenceUtil.get(MainActivity.this, "toggle_diy_main_bg", false)) {
                showDefaultBg();
            } else {
                Bitmap bitmap = SharedPreferenceUtil.getImage(MainActivity.this, "main_bg");
                if (bitmap != null) {
                    Bitmap bitmap1 = ImageTools.compressImage(bitmap);
                    if ((boolean) SharedPreferenceUtil.get(MainActivity.this, "blurBackground", false)) {
                        runOnUiThread(() -> Dali.create(MainActivity.this).load(bitmap1).blurRadius(8).into(bingMainPic));
                    } else {
                        runOnUiThread(() -> GlideApp.with(MainActivity.this).load(bitmap1).into(bingMainPic));
                    }
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(MainActivity.this, "自定义背景加载失败", Toast.LENGTH_SHORT).show();
                        SharedPreferenceUtil.put(MainActivity.this, "diy_main_bg", false);
                        showDefaultBg();
                    });
                }
            }
        });
    }


    /**
     * 是否打开暗色系列
     *
     * @param on 打开/不打开
     */
    private void toggleDarkMode(boolean on) {
        TextView tweetTitle = findViewById(R.id.main_tweet_title);
        TextView courseTitle = findViewById(R.id.main_course_title);
        RelativeLayout courseLayout = findViewById(R.id.course_recent);
        if (on) {
            tweetTitle.setTextColor(getResources().getColor(R.color.white));
            courseTitle.setTextColor(getResources().getColor(R.color.white));
            courseLayout.setBackgroundResource(0);
            courseTitle.setBackgroundResource(0);
            mainCourseName.setTextColor(getResources().getColor(R.color.white));
            mainCourseLocation.setTextColor(getResources().getColor(R.color.white));
            mainCourseState.setTextColor(getResources().getColor(R.color.white));
        }
    }


    /**
     * 显示toast
     *
     * @param c 内容
     * @param t 时长
     */
    public void showToast(String c, int t) {
        Message msg = new Message();
        msg.what = Constant.SHOW_TOAST;
        msg.obj = c;
        msg.arg1 = t;
        handler.sendMessage(msg);
    }

    /**
     * 显示默认的壁纸
     */
    public void showDefaultBg() {
        AtomicInteger blurDegree = new AtomicInteger();
        AtomicBoolean useGlideFlag = new AtomicBoolean(false);
        AtomicBoolean loadDirectly = new AtomicBoolean(false);

        ThreadPoolExecutorFactory.getThreadPoolExecutor().execute(() -> {
                    Bitmap mainBit = null;
                    int type = (int) SharedPreferenceUtil.get(MainActivity.this, "default_main_bg_num", 0);
                    switch (type) {
                        case 0:
                            blurDegree.set(7);
                            mainBit = ImageTools.srcPicToBitmap(getResources(), R.drawable.main_bg3, false);
                            break;
                        case 1:
                            loadDirectly.set(true);
                            runOnUiThread(() -> GlideApp.with(MainActivity.this).load(R.drawable.main_bg1).into(bingMainPic));
                            break;
                        case 2:
                            useGlideFlag.set(true);
                            mainBit = ImageTools.srcPicToBitmap(getResources(), R.drawable.main_bg2, true);
                            break;
                        case 3:
                            blurDegree.set(8);
                            mainBit = ImageTools.srcPicToBitmap(getResources(), R.drawable.main_bg0, false);
                            break;
                        case 4:
                            useGlideFlag.set(true);
                            mainBit = ImageTools.srcPicToBitmap(getResources(), R.drawable.main_bg4, false);
                            break;
                        default:
                            blurDegree.set(8);
                            mainBit = ImageTools.srcPicToBitmap(getResources(), R.drawable.main_bg3, false);
                            break;
                    }
                    Bitmap finalMainBit = mainBit;
                    runOnUiThread(() -> {
                        if (!loadDirectly.get()) {
                            if (!useGlideFlag.get()) {
                                Blurry.with(MainActivity.this).radius(blurDegree.get()).from(finalMainBit).into(bingMainPic);
                            } else {
                                GlideApp.with(MainActivity.this).load(finalMainBit).into(bingMainPic);
                            }
                        }
                    });
                }
        );
    }

    /**
     * 课程冒泡排序
     *
     * @param list 课程list
     */
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

    /**
     * 显示课程去的文本
     *
     * @param s1       当前textView
     * @param s2       地点textView
     * @param s3       状态textView
     * @param isCenter 是否居中显示
     */
    private void setCourseAreaContent(String s1, String s2, String s3, boolean isCenter) {

        runOnUiThread(() -> {
            if (isCenter) {
                mainCourseName.setGravity(Gravity.CENTER);
                mainCourseLocation.setGravity(Gravity.CENTER);
                mainCourseState.setGravity(Gravity.CENTER);
                mainCourseName.setPadding(0, 0, 0, 0);
                mainCourseLocation.setPadding(0, 0, 0, 0);
                mainCourseState.setPadding(0, 0, 0, 0);
            } else {
                mainCourseName.setGravity(Gravity.LEFT);
                mainCourseLocation.setGravity(Gravity.LEFT);
                mainCourseState.setGravity(Gravity.LEFT);

                mainCourseName.setPadding(30, 0, 0, 0);
                mainCourseName.setPadding(30, 0, 0, 0);
                mainCourseLocation.setPadding(30, 0, 0, 0);
                mainCourseState.setPadding(30, 0, 0, 0);
            }
            mainCourseName.setText(s1);
            mainCourseLocation.setText(s2);
            mainCourseState.setText(s3);
        });
    }

    /**
     * 加载动态
     *
     * @param startPosition adapter起始位置
     * @param refresh       是否刷新
     * @param loadMore      是否加载更多
     */
    private void displayTweets(int startPosition, boolean refresh, boolean loadMore) {
        if (refresh) {
            recyclerViewLayout.setRefreshing(true);
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
                assert response.body() != null;
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
                        recyclerViewLayout.setRefreshing(false);
                    }
                });
            }
        });
    }


    /**
     * 显示当前课程
     */
    private void showNowCourse() {
        ThreadPoolExecutorFactory.getThreadPoolExecutor().execute(new Runnable() {
            @Override
            public void run() {
                if (hasGetCourse) {
                    if (null != dayCourseList && dayCourseList.size() > 0) {
                        calendar = Calendar.getInstance();
                        int hour = calendar.get(Calendar.HOUR_OF_DAY);
                        int minute = calendar.get(Calendar.MINUTE);
                        int dayOfMinute = hour * 60 + minute;

                        final Course course = dayCourseList.get(0);
                        int[] times = decideWhatToShow(course.getClsNum());
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
                                setCourseAreaContent("", Constant.MAIN_NO_COURSE_LEFT_INFO, "", true);
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
                                setCourseAreaContent("", Constant.MAIN_NO_COURSE_LEFT_INFO, "", true);
                                return;
                            }
                        }
                        setCourseAreaContent("名称：" + course.getDialogName(), "地点：" + course.getDialogLocation(), state.toString(), false);
                    } else {
                        setCourseAreaContent("", Constant.MAIN_NO_COURSE_LEFT_INFO, "", true);
                        flag3 = false;
                    }
                }
            }
        });


    }

    /**
     * 计算当前的课程时间的区间
     *
     * @param when 第几节课
     * @return 时间区间
     */
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


    /**
     * 在主线程显示handoff文本
     *
     * @param text 内容
     */
    private void showUserHandoffTextOnMain(String text) {
        runOnUiThread(() -> showUserHandoffText(text));
    }

    /**
     * 显示handoff文本
     *
     * @param text 文本
     */
    private void showUserHandoffText(String text) {
        MaterialDialogUtils.showYesOrNoDialogWithBothSthTodo(this, new String[]{"连续互通", "来自darkme.cn[ user: " + darkmeUn + " ]\n" + text, "复制到粘贴板", "忽略"}, new MaterialDialogUtils.AbstractDialogBothDoSthOnClickListener() {
            @Override
            public void onConfirmButtonClick() {
                copyTextToClipboard(text);
                ToastUtils.showToast(MainActivity.this, "复制成功", Toast.LENGTH_LONG);
            }

            @Override
            public void onCancelButtonClick() {
            }
        }, true);

        ThreadPoolExecutorFactory.getThreadPoolExecutor().execute(() -> HttpUtil.haveReceivedHandOffText(darkmeUn));
    }

    /**
     * 加载指定资源id的布局
     *
     * @param resourceId
     * @return 视图dialog
     */
    private View loadView(int resourceId) {
        return LayoutInflater.from(context).inflate(resourceId, null);

    }

    /**
     * 时间变化接收广播
     *
     * @author lqs2
     */
    class TimeChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (Objects.requireNonNull(intent.getAction())) {
                case Intent.ACTION_TIME_TICK:
                    if (flag3) {
                        showNowCourse();
                    }
                    if (!TextUtils.isEmpty(darkmeUn) && checkHandoffFun()) {
                        checkHandoffText(true);
                    }
                    break;
                default:
                    break;
            }
        }
    }


    /**
     * 首页背景变化接收广播
     *
     * @author lqs2
     */
    class MainBgChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            assert action != null;
            if (action.equals(Constant.ACTION1)) {
                SharedPreferenceUtil.put(MainActivity.this, "diy_main_bg", true);
                runOnUiThread(() -> {
                    Bitmap bitmap = SharedPreferenceUtil.getImage(MainActivity.this, "main_bg");
                    if (bitmap != null) {
                        if ((boolean) SharedPreferenceUtil.get(MainActivity.this, "blurBackground", false)) {
                            Dali.create(MainActivity.this).load(bitmap).blurRadius(6).into(bingMainPic);
                        } else {
                            bingMainPic.setImageBitmap(bitmap);
                        }
                        Toast.makeText(MainActivity.this, "更换成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "更换失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            if (action.equals(Constant.ACTION2)) {
                runOnUiThread(MainActivity.this::showDefaultBg);
            }
            if (action.equals(Constant.ACTION3)) {
                flag3 = true;
                weatherToShowCourse();
            }
        }
    }
}
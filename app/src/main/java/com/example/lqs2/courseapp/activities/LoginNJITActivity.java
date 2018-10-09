package com.example.lqs2.courseapp.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.lqs2.courseapp.R;
import com.example.lqs2.courseapp.utils.Constant;
import com.example.lqs2.courseapp.utils.HttpUtil;
import com.example.lqs2.courseapp.utils.SharedPreferenceUtil;
import com.example.lqs2.courseapp.utils.StatusBarUtils;
import com.example.lqs2.courseapp.utils.ToastUtils;
import com.github.ybq.android.spinkit.style.Wave;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginNJITActivity extends ActivityCollector {


    private static String VIEWSTATE;
    //    注册广播
    private IntentFilter intentFilter;
    private NetworkChangeReceiver networkChangeReceiver;
    public static boolean isOnline = false;


    private EditText accountText;
    private EditText passwordText;
    private CheckBox rememberPass;
    private Button loginButton;
    private EditText checkCode;
    private ImageView codeView;
    //    private boolean isLoginSuccess = false;
    private Context context;
    public ProgressBar progressBar;

//    private static String txtUserName;
//    private static String TextBox2;

    private static String cookie = "";
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.SERVER_ERROR:
                    Toast.makeText(context, "服务器错误 " + msg.obj, Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
//                    flushCheckCode();
                    loginButton.setEnabled(true);
                    break;
                case Constant.INFO_ERROR:
                    Toast.makeText(context, (String) msg.obj, Toast.LENGTH_SHORT).show();
                    flushCheckCode();
                    progressBar.setVisibility(View.GONE);
                    loginButton.setEnabled(true);
                    checkCode.setText("");
                    break;
                case Constant.TRANSFER_CODE:
                    byte[] image = (byte[]) msg.obj;
                    Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                    codeView.setImageBitmap(bitmap);
                    codeView.setScaleType(ImageView.ScaleType.FIT_START);
                    break;
                case Constant.TURN_PROGRESS_BAR_ON:
                    progressBar.setVisibility(View.VISIBLE);
                    loginButton.setEnabled(false);
                    break;
                case Constant.TURN_PROGRESS_BAR_OFF:
                    progressBar.setVisibility(View.GONE);
                    loginButton.setEnabled(true);
                    break;
                case Constant.SHOW_TOAST:
                    Toast.makeText(context, (String) msg.obj, Toast.LENGTH_SHORT).show();
                default:
                    break;
            }
        }
    };
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        if (Build.VERSION.SDK_INT >= 21) {
//            View decorView = getWindow().getDecorView();
//            int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
//            decorView.setSystemUiVisibility(option);
//            getWindow().setNavigationBarColor(Color.TRANSPARENT);
//            getWindow().setStatusBarColor(Color.TRANSPARENT);
//        }
        context = this;
        activity = this;

        StatusBarUtils.setStatusTransparent(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.hide();


        Intent intent = getIntent();
        final String type = intent.getStringExtra("TODO");


//        监听网络变化
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, intentFilter);

//        申请使用网络权限
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.INTERNET}, 1);
        }

        accountText = findViewById(R.id.xh);
        passwordText = findViewById(R.id.xm);
        rememberPass = findViewById(R.id.remember_pass);
        loginButton = findViewById(R.id.login_button);
        checkCode = findViewById(R.id.checkCode);
        codeView = findViewById(R.id.codeView);

        progressBar = findViewById(R.id.progress_bar);
        Wave wave = new Wave();
        progressBar.setIndeterminateDrawable(wave);
        progressBar.setVisibility(View.GONE);


//        初次加载验证码
        if (isOnline) {
            flushCheckCode();
        } else {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.image_fail);
            codeView.setImageBitmap(bitmap);
            codeView.setScaleType(ImageView.ScaleType.FIT_XY);
        }

        boolean isRemember = (boolean) SharedPreferenceUtil.get(context, "remember_password_jw", false);
        if (isRemember) {
            String xh = (String) SharedPreferenceUtil.get(context, "xh", "");
            String xm = (String) SharedPreferenceUtil.get(context, "xm", "");
            accountText.setText(xh);
            passwordText.setText(xm);
            rememberPass.setChecked(true);
        }

        boolean hasLogin = (boolean) SharedPreferenceUtil.get(context, "hasLoginJW", false);
        if (hasLogin) {
            String xh = (String) SharedPreferenceUtil.get(context, "xh", "");
            accountText.setText(xh);
        }

        codeView.setOnClickListener(v -> {
            if (isOnline) {
                flushCheckCode();
            } else {
                showToast("请检查网络连接");
            }
        });

        loginButton.setOnClickListener(v -> {
            sendMessage(Constant.TURN_PROGRESS_BAR_ON, null);

            loginButton.setEnabled(false);
//            Message msg = new Message();
//            msg.what = Constant.TURN_PROGRESS_BAR_ON;
//            handler.sendMessage(msg);
//                progressBar.setVisibility(View.VISIBLE);
            final String xh = accountText.getText().toString().trim();
            final String xm = passwordText.getText().toString();
            final String code = checkCode.getText().toString().trim();

            if (isOnline) {
                if (cookie != null) {
                    if (!(TextUtils.isEmpty(xh) || TextUtils.isEmpty(xm) || TextUtils.isEmpty(code))) {
                        HttpUtil.get_login_VIEWSTATE(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                sendMessage(Constant.SERVER_ERROR, Constant.server_off);
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String html = response.body().string();
                                Document doc = Jsoup.parse(html);
                                String __VIEWSTATE = doc.select("input[name='__VIEWSTATE']").val();
                                VIEWSTATE = __VIEWSTATE;
                                HttpUtil.login(xh, xm, code, cookie, __VIEWSTATE, new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        e.printStackTrace();
                                        sendMessage(Constant.SERVER_ERROR, "登录失败");
                                    }

                                    @Override
                                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                        final int responseCode = response.code();
                                        if (responseCode == 200) {
                                            String loginResponse = response.body().string();

                                            if (loginResponse.contains(Constant.login_jw_success)) {
                                                HttpUtil.redirect(xh, cookie, new Callback() {
                                                    @Override
                                                    public void onFailure(Call call, IOException e) {
                                                        e.printStackTrace();
                                                    }

                                                    @Override
                                                    public void onResponse(Call call, Response response) throws IOException {

                                                        String redirectHtml = response.body().string();

                                                        SharedPreferenceUtil.put(context, "xh", accountText.getText().toString());

                                                        if (rememberPass.isChecked()) {
                                                            SharedPreferenceUtil.put(context, "xh", accountText.getText().toString());
                                                            SharedPreferenceUtil.put(context, "xm", passwordText.getText().toString());
                                                            SharedPreferenceUtil.put(context, "remember_password_jw", true);
                                                        } else {
                                                            SharedPreferenceUtil.remove(context, "xm");
                                                            SharedPreferenceUtil.put(context, "remember_password_jw", false);
                                                        }
                                                        SharedPreferenceUtil.put(context, "hasLoginJW", true);

                                                        switch (type) {
                                                            case "COURSE":
                                                                String year = intent.getStringExtra("year");
                                                                String team = intent.getStringExtra("team");
                                                                Boolean isXn = intent.getBooleanExtra("isXn", false);
                                                                HttpUtil.queryCourse(context, xh, xm, year, team, cookie, isXn, new Callback() {
                                                                    @Override
                                                                    public void onFailure(Call call, IOException e) {
                                                                        e.printStackTrace();
                                                                        sendMessage(Constant.SERVER_ERROR, "查询失败");
                                                                    }

                                                                    @Override
                                                                    public void onResponse(Call call, Response response) throws IOException {
                                                                        sendMessage(Constant.TURN_PROGRESS_BAR_OFF, null);
                                                                        String sourceCode = response.body().string();
                                                                        System.out.println(sourceCode);

                                                                        if (!sourceCode.contains(Constant.login_error_noComment)) {
                                                                            sendMessage(Constant.SHOW_TOAST, Constant.login_success_info);
                                                                            SharedPreferenceUtil.put(context, "hasGetCourse", true);
                                                                            Document doc = Jsoup.parse(sourceCode);
                                                                            Element xnString = doc.getElementById("xnd");
                                                                            Element xqString = doc.getElementById("xqd");
                                                                            Elements xns = xnString.select("option");
                                                                            Elements xqs = xqString.select("option");
                                                                            StringBuilder xnStrings = new StringBuilder();
                                                                            StringBuilder xqStrings = new StringBuilder();
                                                                            String xnText = null;
                                                                            String xqText = null;
                                                                            for (int i = 0; i < xns.size(); i++) {
                                                                                Element e = xns.get(i);
                                                                                String text = e.text();
                                                                                if (e.hasAttr("selected")) {
                                                                                    xnText = text;
                                                                                }
                                                                                xnStrings.append(text).append("\t");
                                                                            }
                                                                            for (int i = 0; i < xqs.size(); i++) {
                                                                                Element e = xqs.get(i);
                                                                                String text = e.text();
                                                                                if (e.hasAttr("selected")) {
                                                                                    xqText = text;
                                                                                }
                                                                                xqStrings.append(text).append("\t");
                                                                            }
                                                                            if (!"".equals(year) || !"".equals(team)) {
                                                                                if (!"".equals(year)) {
                                                                                    xnText = year;
                                                                                }
                                                                                if (!"".equals(team)) {
                                                                                    xqText = team;
                                                                                }
                                                                            }
                                                                            SharedPreferenceUtil.put(context, "courseSourceCode", sourceCode);
                                                                            SharedPreferenceUtil.put(context, "xns", xnStrings.substring(0, xnStrings.lastIndexOf("\t")));
                                                                            SharedPreferenceUtil.put(context, "xqs", xqStrings.substring(0, xqStrings.lastIndexOf("\t")));
                                                                            Intent intent1 = new Intent(context, CourseActivity.class);
//                                                            intent.putExtra("cookie", cookie);
                                                                            intent1.putExtra("xnc", xnText);
                                                                            intent1.putExtra("xqc", xqText);
                                                                            intent1.putExtra("sourceCode", sourceCode);
                                                                            SharedPreferenceUtil.put(context, "xnc", xnText);
                                                                            SharedPreferenceUtil.put(context, "xqc", xqText);
                                                                            if ("".equals(year) && "".equals(team)) {
                                                                                SharedPreferenceUtil.put(context, "xncr", xnText);
                                                                                SharedPreferenceUtil.put(context, "xqcr", xqText);
                                                                            }
                                                                            startActivity(intent1);
                                                                            finish();
                                                                        } else {
                                                                            sendMessage(Constant.SERVER_ERROR, Constant.login_error_noComment);
                                                                        }
                                                                    }
                                                                });
                                                                break;
                                                            case "GRADE":
                                                                HttpUtil.queryGradeInit(xh, xm, cookie, new Callback() {
                                                                    @Override
                                                                    public void onFailure(Call call, IOException e) {
                                                                        e.printStackTrace();
                                                                    }

                                                                    @Override
                                                                    public void onResponse(Call call, Response response) throws IOException {
                                                                        String code = response.body().string();
                                                                        if (!code.contains(Constant.login_error_noComment)) {
                                                                            Document doc = Jsoup.parse(code);
                                                                            String __VIEWSTATE = doc.select("input[name='__VIEWSTATE']").val();
                                                                            Element xnString = doc.getElementById("ddlxn");
                                                                            Element xqString = doc.getElementById("ddlxq");
                                                                            Elements options1 = xnString.select("option");
                                                                            Elements options2 = xqString.select("option");
                                                                            ArrayList<String> xnList = new ArrayList<>();
                                                                            ArrayList<String> xqList = new ArrayList<>();
                                                                            for (int i = 0; i < options1.size(); i++) {
                                                                                if (i == 0) {
                                                                                    xnList.add(options1.get(i).val());
                                                                                } else {
                                                                                    xnList.add(options1.get(options1.size() - i).val());
                                                                                }
                                                                            }
                                                                            for (int i = 0; i < options2.size(); i++) {
                                                                                if (i == 0) {
                                                                                    xqList.add(options2.get(i).val());
                                                                                } else {
                                                                                    xqList.add(options2.get(i).val());
                                                                                }
                                                                            }
                                                                            sendMessage(Constant.TURN_PROGRESS_BAR_OFF, null);
                                                                            Intent intent1 = new Intent(context, GradeActivity.class);
                                                                            intent1.putExtra("__VIEWSTATE", __VIEWSTATE);
                                                                            intent1.putExtra("cookie", cookie);
                                                                            intent1.putExtra("xh", xh);
                                                                            intent1.putExtra("xm", xm);
                                                                            intent1.putStringArrayListExtra("xnList", xnList);
                                                                            intent1.putStringArrayListExtra("xqList", xqList);
                                                                            startActivity(intent1);
                                                                            finish();
                                                                        } else {
                                                                            sendMessage(Constant.SERVER_ERROR, Constant.login_error_noComment);
                                                                        }
                                                                    }
                                                                });
                                                                break;
                                                            case "CREDIT":
                                                                HttpUtil.queryCreditInit(xh, xm, cookie, new Callback() {
                                                                    @Override
                                                                    public void onFailure(Call call, IOException e) {
                                                                        e.printStackTrace();
                                                                    }

                                                                    @Override
                                                                    public void onResponse(Call call, Response response) throws IOException {
                                                                        byte[] bytes = response.body().bytes();
                                                                        String resp = new String(bytes, "gb2312");
                                                                        // 只是一个成功的标志位
                                                                        if (resp.contains("至今未通过课程成绩")) {
                                                                            Document doc = Jsoup.parse(resp);
                                                                            String __VIEWSTATE = doc.select("input[name='__VIEWSTATE']").val();
                                                                            System.out.println("---" + __VIEWSTATE);
                                                                            HttpUtil.queryCredit(xh, xm, __VIEWSTATE, cookie, new Callback() {
                                                                                @Override
                                                                                public void onFailure(Call call, IOException e) {
                                                                                    e.printStackTrace();
                                                                                }

                                                                                @Override
                                                                                public void onResponse(Call call, Response response) throws IOException {
                                                                                    byte[] bytes = response.body().bytes();
                                                                                    String resp = new String(bytes, "gb2312");
                                                                                    Intent intent1 = new Intent(context, CreditActivity.class);
                                                                                    intent1.putExtra("html", resp);
                                                                                    startActivity(intent1);
                                                                                    finish();
                                                                                }
                                                                            });
                                                                        } else {
                                                                            ToastUtils.showToastOnMain(context, activity, "服务出错", Toast.LENGTH_SHORT);
                                                                        }
                                                                    }
                                                                });
                                                                break;

                                                            default:
                                                                break;
                                                        }
//                                                        if ("COMMENT".equals(type)) {
//                                                            System.out.println("欢迎使用评教功能");
////                                                    System.out.println(redirectHtml);
//                                                            Document doc = Jsoup.parse(redirectHtml);
//                                                            Elements uls = doc.select("ul.sub");
//                                                            final boolean[] flag = {false};
//                                                            for (int i = 0; i < uls.size(); i++) {
//                                                                Element ul = uls.get(i);
//                                                                Elements lis = ul.select("li");
//                                                                for (int j = 0; j < lis.size(); j++) {
//                                                                    Element li = lis.get(j);
//                                                                    Elements as = li.select("a");
//                                                                    Element a = as.first();
//                                                                    String link = a.attr("href");
//                                                                    if (link.contains("N12141")) {
//                                                                        flag[0] = true;
//                                                                        System.out.println(link);
//                                                                        String abs_link = "http://jwjx.njit.edu.cn/" + link;
//                                                                        String pjkc = abs_link.substring(abs_link.indexOf("=") + 1, abs_link.indexOf("&"));
//                                                                        HttpUtil.getOneKeyComment_VIEWSTATE(abs_link, cookie, xh, new Callback() {
//                                                                            @Override
//                                                                            public void onFailure(Call call, IOException e) {
//                                                                                System.out.println("!!!!!!!!!!!!");
//                                                                                e.printStackTrace();
//                                                                            }
//
//                                                                            @Override
//                                                                            public void onResponse(Call call, Response response) throws IOException {
//
//                                                                                String s = response.body().string();
//                                                                                System.out.println(s);
////                                                                        Document doc = Jsoup.parse(s);
////                                                                        String __VIEWSTATE = doc.select("input[name='__VIEWSTATE']").val();
//
////                                                                        HttpUtil.OneKeyComment(abs_link, cookie, pjkc, new Callback() {
////                                                                            @Override
////                                                                            public void onFailure(Call call, IOException e) {
////                                                                                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!");
//////                                                                                e.printStackTrace();
////                                                                            }
////
////                                                                            @Override
////                                                                            public void onResponse(Call call, Response response) throws IOException {
////                                                                                flag[0] = true;
////                                                                                System.out.println(response.body().string());
////                                                                            }
////                                                                        });
//
//                                                                                new Thread(() -> System.out.println(HttpUtil.oneKeyComment_unRecommended(abs_link, cookie, pjkc))).start();
//                                                                            }
//                                                                        });
////                                                    String oneKeyComment_viewstate = HttpUtil.getOneKeyComment_VIEWSTATE_unRecommended(abs_link, cookie, xh);
////                                                    Document doc = Jsoup.parse(oneKeyComment_viewstate);
////                                                    String __VIEWSTATE = doc.select("input[name='__VIEWSTATE']").val();
////                                                    String pjkc2 = abs_link.substring(abs_link.indexOf("=") + 1, abs_link.indexOf("&"));
////                                                    String s = HttpUtil.oneKeyComment_unRecommended(abs_link, cookie, pjkc2);
////                                                    System.out.println("000000000\n"+s);
////                                                               break;
//                                                                    }//if
//                                                                    if (flag[0]) {
//                                                                        break;
//                                                                    }
//                                                                }//for
//
//                                                                if (flag[0]) {
//                                                                    break;
//                                                                }
//                                                            }//for
//                                                            sendMessage(Constant.TURN_PROGRESS_BAR_OFF, null);
//                                                            System.out.println("平角成功");
//                                                        } else if ("NOTHING".equals(type)) {
//                                                            runOnUiThread(() -> Toast.makeText(context, "登录成功", Toast.LENGTH_LONG).show());
//                                                            Intent intent1 = new Intent(context, MainActivity.class);
//                                                            startActivity(intent1);
//                                                        }
                                                    }
                                                });
                                            } else if (loginResponse.contains(Constant.login_error_cord)) {
                                                sendMessage(Constant.INFO_ERROR, Constant.login_error_cord);

                                            } else if (loginResponse.contains(Constant.login_error_pw)) {
                                                sendMessage(Constant.INFO_ERROR, Constant.login_error_pw);

                                            } else if (loginResponse.contains(Constant.login_error_xh)) {
                                                sendMessage(Constant.INFO_ERROR, Constant.login_error_xh);

                                            }
                                        }//responseCode = 200
                                    } //onResponse
                                });
                            }
                        });

                    } else {
                        showToast("账户名 | 密码 | 验证码为空");
                        sendMessage(Constant.TURN_PROGRESS_BAR_OFF, null);
                    }
                } else {
                    sendMessage(Constant.SERVER_ERROR, "可能开启了内网访问限制");
                }
            } else {
                showToast("请检查网络连接");
                sendMessage(Constant.TURN_PROGRESS_BAR_OFF, null);
            }
        });
    }

    private void sendMessage(int what, Object obj) {
        synchronized (this) {
            Message msg = new Message();
            msg.what = what;
            msg.obj = obj;
            handler.sendMessage(msg);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 || !(grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    showToast("网络使用权限被拒绝");
                }
                break;
            default:
                break;
        }
    }

    private void showToast(String info) {
        Toast.makeText(context, info, Toast.LENGTH_SHORT).show();
    }

    private void flushCheckCode() {
        HttpUtil.getCheckCode(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message message = new Message();
                message.what = Constant.SERVER_ERROR;
                message.obj = Constant.code_flush_fail;
                handler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                byte[] byte_image = response.body().bytes();
                cookie = response.header("Set-Cookie");
                sendMessage(Constant.TRANSFER_CODE, byte_image);
            }
        });
    }

    class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (!(networkInfo != null && networkInfo.isAvailable())) {
                isOnline = false;
                showToast(Constant.network_off);
            } else {
                isOnline = true;
                flushCheckCode();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangeReceiver);
    }
}
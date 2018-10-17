package com.example.lqs2.courseapp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.example.lqs2.courseapp.common.StringUtils;
import com.example.lqs2.courseapp.global.ThreadPoolExecutorFactory;
import com.luck.picture.lib.entity.LocalMedia;

import org.jsoup.Jsoup;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;


/**
 * 网络工具类
 *
 * @author lqs2
 */
public class HttpUtil {

    private static volatile OkHttpClient client = null;
    private static String TAG = "HttpUtil";

    /**
     * 单例获取client对象
     *
     * @return client对象
     */
    private static OkHttpClient getInstance() {
        if (client == null) {
            synchronized (HttpUtil.class) {
                if (client == null) {
                    client = new OkHttpClient.Builder()
                            .writeTimeout(30, TimeUnit.SECONDS)
                            .connectTimeout(20, TimeUnit.SECONDS)
                            .readTimeout(30, TimeUnit.SECONDS)
                            .hostnameVerifier((hostname, session) -> true)
                            .build();
                }
            }
        }
        return client;
    }

    /**
     * 输出request 参数
     *
     * @param request request对象
     * @return 参数字符串
     */
    private static String outputPostParams(Request request) {
        StringBuilder sb = new StringBuilder();
        String method = request.method();
        if ("POST".equals(method)) {
            if (request.body() instanceof FormBody) {
                FormBody body = (FormBody) request.body();
                for (int i = 0; i < body.size(); i++) {
                    sb.append(body.encodedName(i)).append("=").append(body.encodedValue(i)).append("\n");
                }
                sb.delete(sb.length() - 1, sb.length());
            }
        }
        return sb.toString();
    }

    /**
     * 获取教务网验证码
     *
     * @param c 回调接口
     */
    public static void getCheckCode(Callback c) {
        doAsynGetRequest(Constant.CHECK_CODE_URL, null, null, c);
    }

    /**
     * 获取登录状态
     *
     * @param c 回调接口
     */
    public static void getLoginViewstate(Callback c) {
        doAsynGetRequest(Constant.MAIN_URL, null, null, c);
    }

    /**
     * 登录教务网
     *
     * @param xh        学号
     * @param xm        姓名
     * @param code      验证码
     * @param cookie    cookie
     * @param viewstate 标志
     * @param c         回调接口
     */
    public static void login(String xh, String xm, String code, String cookie, String viewstate, Callback c) {
        FormBody formBody = generateFormBody(new HashMap<String, String>(9) {{
            put("__VIEWSTATE", viewstate);
            put("txtUserName", xh);
            put("TextBox2", xm);
            put("txtSecretCode", code);
            put("RadioButtonList1", "学生");
            put("Button1", "");
            put("lbLanguage", "");
            put("hidPdrs", "");
            put("hidsc", "");
        }});
        doAsynPostRequest(Constant.MAIN_URL, null, new HashMap<String, String>(3) {{
            put("contentType", "GB2312");
            put("Referer", Constant.REFER_URL);
            put("Cookie", cookie);
        }}, formBody, c);
    }

    /**
     * main重定向
     *
     * @param xh     学号
     * @param cookie cookie
     * @param c      回调接口
     */
    public static void redirect(String xh, String cookie, Callback c) {
        doAsynGetRequest(Constant.FORWARD_URL + xh, null, new HashMap<String, String>(2) {{
            put("Referer", Constant.REFER_URL);
            put("Cookie", cookie);
        }}, c);
    }


    /**
     * 查询课程，写的什么玩意儿
     *
     * @param context 上下文
     * @param xh      学号
     * @param xm      姓名
     * @param year    学年
     * @param team    学期
     * @param cookie  cookie
     * @param isXn    是否学年
     * @param c       回调接口
     * @throws IOException 异常
     */
    public static void queryCourse(Context context, String xh, String xm, String year, String team, String cookie, boolean isXn, Callback c) throws IOException {
        client = getInstance();
        Request request;
        String xncr = (String) SharedPreferenceUtil.get(context, "xncr", "");
        String xqcr = (String) SharedPreferenceUtil.get(context, "xqcr", "");

        if ((xncr.equals(year) && xqcr.equals(team)) || ("".equals(year) && "".equals(team))) {
            request = new Request.Builder()
                    .addHeader("contentType", "GB2312")
                    .addHeader("Referer", Constant.FORWARD_URL + xh)
                    .addHeader("Cookie", cookie)
                    .url("http://jwjx.njit.edu.cn/xskbcx.aspx?xh=" + xh + "&xm=" + xm + "&gnmkdm=N121603")
                    .build();
        } else {
            request = new Request.Builder()
                    .addHeader("contentType", "GB2312")
                    .addHeader("Referer", Constant.FORWARD_URL + xh)
                    .addHeader("Cookie", cookie)
                    .url("http://jwjx.njit.edu.cn/xskbcx.aspx?xh=" + xh + "&xm=" + xm + "&gnmkdm=N121603")
                    .build();
            Response response = client.newCall(request).execute();
            String html = response.body().string();
            String viewstate = Jsoup.parse(html).select("input[name=__VIEWSTATE]").get(0).attr("value");

            FormBody builder;
            if (isXn) {
                builder = new FormBody.Builder()
                        .add("xnd", year)
                        .add("xqd", team)
                        .add("__VIEWSTATE", viewstate)
                        .add("__EVENTARGUMENT", "")
                        .add("__EVENTTARGET", "xnd")
                        .build();
            } else {
                builder = new FormBody.Builder()
                        .add("xnd", year)
                        .add("xqd", team)
                        .add("__VIEWSTATE", viewstate)
                        .add("__EVENTARGUMENT", "")
                        .add("__EVENTTARGET", "xqd")
                        .build();
            }
            request = new Request.Builder()
                    .addHeader("contentType", "GB2312")
                    .addHeader("Referer", Constant.FORWARD_URL + xh)
                    .addHeader("Cookie", cookie)
                    .url("http://jwjx.njit.edu.cn/xskbcx.aspx?xh=" + xh + "&xm=" + xm + "&gnmkdm=N121603")
                    .post(builder)
                    .build();
        }
        client.newCall(request).enqueue(c);
    }


    /**
     * 获取bing每日一图
     *
     * @param c 回调接口
     */
    public static void loadBingPic(Callback c) {
        doAsynGetRequest(Constant.BING_PIC_API, null, null, c);
    }

    /**
     * 查询成绩第一步
     *
     * @param xh     学号
     * @param xm     姓名
     * @param cookie cookie
     * @param c      回调接口
     */
    public static void queryGradeInit(String xh, String xm, String cookie, Callback c) {
        doAsynGetRequest(Constant.QUERY_PREFIX_URL, new HashMap<String, String>(3) {{
            put("xh", xh);
            put("xm", xm);
            put("gnmkdm", "N121605");
        }}, new HashMap<String, String>(3) {{
            put("contentType", "GB2312");
            put("Referer", Constant.FORWARD_URL + xh);
            put("Cookie", cookie);
        }}, c);
    }

    /**
     * 查询成绩第二部
     *
     * @param viewstate 标志
     * @param xh        学号
     * @param xm        姓名
     * @param cookie    cookie
     * @param xn        学年
     * @param xq        学期
     * @param c         回调
     */
    public static void queryGrade(String viewstate, String xh, String xm, String cookie, String xn, String xq, Callback c) {
        FormBody formBody = generateFormBody(new HashMap<String, String>(6) {{
            put("__EVENTTARGET", "");
            put("__EVENTARGUMENT", "");
            put("__VIEWSTATE", viewstate);
            put("ddlxn", xq);
            put("ddlxq", xn);
            put("btnCx", " 查  询 ");
        }});
        doAsynPostRequest(Constant.QUERY_PREFIX_URL, new HashMap<String, String>(3) {{
            put("xh", xh);
            put("xm", xm);
            put("gnmkdm", "N121605");
        }}, new HashMap<String, String>(3) {{
            put("contentType", "GB2312");
            put("Referer", "http://jwjx.njit.edu.cn/xscjcx_dq.aspx?xh=" + xh + "&xm=" + xm + "&gnmkdm=N121605");
            put("Cookie", cookie);
        }}, formBody, c);
    }


    /**
     * 查询学分第一步
     *
     * @param xh     学号
     * @param xm     姓名
     * @param cookie cookie
     * @param c      回调接口
     */
    public static void queryCreditInit(String xh, String xm, String cookie, Callback c) {
        doAsynGetRequest(Constant.QUERY_PREFIX_URL, new HashMap<String, String>(3) {{
            put("xh", xh);
            put("xm", xm);
            put("gnmkdm", "N121617");
        }}, new HashMap<String, String>(3) {{
            put("contentType", "GB2312");
            put("Referer", Constant.FORWARD_URL + xh);
            put("Cookie", cookie);
        }}, c);
    }

    /**
     * 查询学分第二步
     *
     * @param xh        姓名
     * @param xm        学号
     * @param viewState 状态
     * @param cookie    cookie
     * @param c         回调
     */
    public static void queryCredit(String xh, String xm, String viewState, String cookie, Callback c) {
        RequestBody formBody = generateFormBody(new HashMap<String, String>(8) {{
            put("__EVENTTARGET", "");
            put("__EVENTARGUMENT", "");
            put("__VIEWSTATE", viewState);
            put("hidLanguage", "");
            put("ddlXN", "");
            put("ddlXQ", "");
            put("ddl_kcxz", "");
            put("Button1", "成绩统计");
        }});
        doAsynPostRequest(Constant.QUERY_PREFIX_URL, new HashMap<String, String>(3) {{
            put("xh", xh);
            put("xm", xm);
            put("gnmkdm", "N121617");
        }}, new HashMap<String, String>(7) {{
            put("contentType", "GB2312");
            put("Referer", "http://jwjx.njit.edu.cn/xscjcx.aspx?xh=" + xh + "&xm=" + xm + "&gnmkdm=N121617");
            put("Host", "jwjx.njit.edu.cn");
            put("Origin", "http://jwjx.njit.edu.cn");
            put("Cookie", cookie);
            put("Upgrade-Insecure-Requests", "1");
            put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.45 Safari/537.36");
        }}, formBody, c);
    }


    /**
     * 加载日历选择页
     *
     * @param c 回调接口
     */
    public static void loadCalendarChoosePage(Callback c) {
        doAsynGetRequest(Constant.SCHOOL_CALENDAR_CHOOSE_URL, null, null, c);
    }

    /**
     * 加载最新日历
     *
     * @param url 日历url
     * @param c   回调
     */
    public static void loadCalendarPage(String url, Callback c) {
        doAsynGetRequest(url, null, null, c);
    }


    /**
     * 显示用户好友
     *
     * @param un 用户名
     * @param c  回调
     */
    public static void showMyFriends(String un, Callback c) {
        doAsynGetRequest(Constant.USER_SHOW_MY_FRIEND_URL, new HashMap<String, String>(1) {{
            put("un", un);
        }}, null, c);
    }

    /**
     * 显示用户文件
     *
     * @param un 用户名
     * @param c  回调
     */
    public static void showMyFile(String un, Callback c) {

        doAsynGetRequest(Constant.FILE_SHOW_URL, new HashMap<String, String>(1) {{
            put("un", un);
        }}, null, c);
    }

    /**
     * 增加一条文件记录
     *
     * @param filePostAuthor 文件作者
     * @param fileNo         文件编号
     * @param fileName       文件名
     * @param attachment     附加信息
     * @param destination    目的地
     * @param fileSize       文件大小
     * @param c              回调
     */
    public static void insertFileRecord(String filePostAuthor, String fileNo, String fileName, String attachment, String destination, String fileSize, Callback c) {
        FormBody formBody = generateFormBody(new HashMap<String, String>(6) {{
            put("filePostAuthor", filePostAuthor);
            put("fileNo", fileNo);
            put("fileName", fileName);
            put("attachment", attachment);
            put("destination", destination);
            put("fileSize", fileSize);
        }});
        doAsynPostRequest(Constant.FILE_INSERT_RECORD_URL, null, null, formBody, c);
    }

    /**
     * 删除文件
     *
     * @param un  用户名
     * @param fid 文件编号
     * @param c   回调
     */
    public static void deleteOneFile(String un, String fid, Callback c) {
        doAsynPostRequest(Constant.FILE_DEL_URL, null, null, generateFormBody(un, new HashMap<String, String>(1) {{
            put("file_id", fid);
        }}), c);
    }


    /**
     * 生成darkme post表单，用户名和密码必选
     *
     * @param un    用户名
     * @param pwd   密码
     * @param extra 附加信息
     * @return 请求体
     */
    private static FormBody generateFormBody(String un, String pwd, Map<String, String> extra) {
        if (StringUtils.isEmpty(un) || StringUtils.isEmpty(pwd)) {
            return null;
        }
        if (extra == null) {
            extra = new HashMap<>(2);
        }
        extra.put("un", un);
        extra.put("pwd", pwd);
        return generateFormBody(extra);
    }

    /**
     * 生成darkme post表单，用户名必选
     *
     * @param un    用户名
     * @param extra 附加信息
     * @return 请求体
     */
    private static FormBody generateFormBody(String un, Map<String, String> extra) {
        if (StringUtils.isEmpty(un)) {
            return null;
        }
        if (extra == null) {
            extra = new HashMap<>(1);
        }
        extra.put("un", un);
        return generateFormBody(extra);
    }

    /**
     * 生成post表单
     *
     * @param info 表单内容
     * @return 请求体
     */
    private static FormBody generateFormBody(Map<String, String> info) {
        FormBody.Builder builder = new FormBody.Builder();
        if (info != null) {
            for (Map.Entry<String, String> entry : info.entrySet()) {
                builder.add(entry.getKey(), entry.getValue());
            }
        }
        return builder.build();
    }

    /**
     * 生成一个新的文件，获取编号
     *
     * @param filename 文件名
     * @param c        回调接口
     */
    public static void generateNewFile(String filename, Callback c) {
        doAsynGetRequest(Constant.FILE_GENERATE_URL, new HashMap<String, String>(1) {{
            put("filename", filename);
        }}, null, c);
    }

    /**
     * 文件搜索
     *
     * @param un 用户名
     * @param no 文件编号
     * @param c  回调
     */
    public static void searchFileByNo(String un, String no, Callback c) {
        doAsynPostRequest(Constant.FILE_SEARCH_URL, null, null, generateFormBody(un, new HashMap<String, String>(1) {{
            put("fileNo", no);
        }}), c);
    }

    /**
     * 查询是否有同名的用户
     *
     * @param username 用户名
     * @param c        回调
     */
    public static void hasMatcherUser(String username, Callback c) {
        client = getInstance();
        Request request = new Request.Builder()
                .url(Constant.USER_DO_EXIST_URL + "?name=" + username)
                .get()
                .build();
        client.newCall(request).enqueue(c);
    }


    /**
     * 登录darkme认证
     *
     * @param darkmeUn  账户名
     * @param darkmePwd 账户密码
     * @param c         回调
     */
    public static void userValidateDarkMe(String darkmeUn, String darkmePwd, Callback c) {
        doAsynPostRequest(Constant.USER_DO_LOGIN_URL, null, null, generateFormBody(darkmeUn, darkmePwd, null), c);
    }


    /**
     * 用户注册darkme
     *
     * @param t1    用户名
     * @param t2    密码
     * @param t3    邮箱
     * @param isMan 性别
     * @return okHttp响应
     * @throws IOException 异常
     */
    public static Response userRegisterDarkMe(String t1, String t2, String t3, String isMan) throws IOException {
        client = getInstance();
        Request request = new Request.Builder()
                .url(Constant.USER_DO_REGISTER_URL)
                .post(Objects.requireNonNull(generateFormBody(t1, t2, new HashMap<String, String>() {{
                    put("email", t3);
                    put("isMan", isMan);
                }})))
                .build();
        return client.newCall(request).execute();
    }


    /**
     * 发布新动态
     *
     * @param context  上下文
     * @param darkmeUn 用户名
     * @param content  内容
     * @param list     图片list
     * @param c        回调
     */
    public static void releaseNewTweet(Context context, String darkmeUn, String content, List<LocalMedia> list, Callback c) {
        FormBody formBody = generateFormBody(darkmeUn, new HashMap<String, String>(1 + list.size()) {{
            put("content", content);
            for (byte i = 0; i < list.size(); i++) {
                String path = getImageReadPath(list.get(i));
                Log.d(TAG, "image" + i + ": " + path);
                String result = uploadImage(context, path);
                if (!"-1".equals(result) && !"error".equals(result)) {
                    put("imgPath" + i, result);
                }
            }
        }});
        doAsynPostRequest(Constant.TWEET_POST_URL, null, null, formBody, c);
    }

    /**
     * 压缩并上传图像，返回服务器中的绝对路径；
     *
     * @param context   上下文
     * @param imagePath 本地图像路径
     * @return 图像在服务器中的路径
     */
    private static String uploadImage(Context context, String imagePath) {
        client = getInstance();
        final String[] result = {"-2"};
        Luban.with(context)
                .load(imagePath)
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onSuccess(File file) {
                        MultipartBody.Builder builder = new MultipartBody.Builder();
                        builder.addFormDataPart("file", imagePath,
                                RequestBody.create(MediaType.parse("application/octet-stream"), new File(file.getAbsolutePath())));
                        RequestBody requestBody = builder.build();
                        Request request = new Request.Builder()
                                .url(Constant.UPLOAD_IMAGE_URL)
                                .post(requestBody)
                                .build();
                        try {
                            ThreadPoolExecutorFactory.getThreadPoolExecutor().execute(() -> {
                                Response response = null;
                                try {
                                    response = client.newCall(request).execute();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                assert response != null;
                                Log.d(TAG, "响应码: " + response.code());
                                if (response.isSuccessful()) {
                                    String resultValue = null;
                                    try {
                                        assert response.body() != null;
                                        resultValue = response.body().string();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    Log.d(TAG, "响应体：" + resultValue);
                                    result[0] = resultValue;
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                }).launch();
        while ("-2".equals(result[0])) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return result[0];
    }

    /**
     * 获取图像裁剪后的路径
     * 例如 LocalMedia 里面返回三种path
     * 1.media.getPath(); 为原图path
     * 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
     * 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
     * 如果裁剪并压缩了，已取压缩路径为准，因为是先裁剪后压缩的
     *
     * @param media 裁剪后的对象
     * @return 裁剪后的路径
     */
    private static String getImageReadPath(LocalMedia media) {
        if (!media.isCut() && !media.isCompressed()) {
            return media.getPath();
        }
        if (media.isCompressed()) {
            return media.getCompressPath();
        }
        return media.getCutPath();
    }

    /**
     * 获取所有的动态
     *
     * @param startPosition 起始位置索引
     * @param c             回调
     */
    public static void getAllTweets(int startPosition, Callback c) {
        doAsynGetRequest(Constant.TWEET_GET_ALL_URL, new HashMap<String, String>(1) {{
            put("sp", String.valueOf(startPosition));

        }}, null, c);
    }

    /**
     * 删除动态
     *
     * @param id 动态id
     * @param c  回调
     */
    public static void deleteTweet(String id, Callback c) {
        doAsynGetRequest(Constant.TWEET_DEL_URL, new HashMap<String, String>(1) {{
            put("id", id);
        }}, null, c);
    }


    /**
     * 修改动态点赞数
     *
     * @param id  动态的id
     * @param add 点赞/取消
     */
    public static void modifyTweetGood(String id, boolean add) {
        doSyncGetRequest(Constant.TWEET_PRAISE_ADD_URL, new HashMap<String, String>(2) {{
            put("id", id);
            put("add", String.valueOf(add));
        }}, null);
    }

    /**
     * 展示用户&动态关系
     *
     * @param un      用户名
     * @param tweetId 动态id
     * @param c       回调
     */
    public static void showUserTweetInfo(String un, String tweetId, Callback c) {
        doAsynGetRequest(Constant.USER_TWEET_INFO_URL, new HashMap<String, String>(2) {{
            put("un", un);
            put("tweetId", tweetId);
        }}, null, c);
    }


    /**
     * 用户点赞
     *
     * @param un      用户名
     * @param tweetId 动态id
     * @param praise  点赞/取消点赞
     */
    public static void userPraiseTweet(String un, String tweetId, boolean praise) {
        doSyncGetRequest(Constant.USER_PRAISE_TWEET_URL, new HashMap<String, String>(3) {{
            put("un", un);
            put("tweetId", tweetId);
            put("praise", String.valueOf(praise));
        }}, null);
    }

    /**
     * 用户收藏
     *
     * @param un      用户名
     * @param tweetId 动态id
     * @param collect 收藏/取消
     * @return okHttp响应
     * @throws IOException 异常
     */
    public static Response userCollectTweet(String un, String tweetId, boolean collect) throws IOException {
        client = getInstance();
        Request request = new Request.Builder()
                .url(generateGetUrl(Constant.USER_COLLECT_TWEET_URL, new HashMap<String, String>(3) {{
                    put("un", un);
                    put("tweetId", tweetId);
                    put("collect", String.valueOf(collect));
                }}))
                .build();
        return client.newCall(request).execute();
    }

    /**
     * 设置用户头像
     *
     * @param darkmeUn   用户名
     * @param headImgBit 头像位图
     * @param c          回调
     */
    public static void setUserProfilePicture(String darkmeUn, Bitmap headImgBit, Callback c) {
        String picStr = Base64ImageUtils.bitmapToBase64Str(headImgBit);
        doAsynPostRequest(Constant.USER_SET_PROFILE_PICTURE_URL, null, null, generateFormBody(darkmeUn, new HashMap<String, String>(1) {{
            put("profilePicStr", picStr);
        }}), c);
    }

    /**
     * 获取用户头像
     *
     * @param darkmeUn 用户名
     * @param c        回调
     */
    public static void getUserProfilePicture(String darkmeUn, Callback c) {
        doAsynGetRequest(Constant.USER_GET_PROFILE_PICTURE_URL, new HashMap<String, String>(1) {{
            put("un", darkmeUn);
        }}, null, c);
    }

    /**
     * 获取handoff文本
     *
     * @param darkmeUn 用户名
     * @param c        回调
     */
    public static void getUserHandoffText(String darkmeUn, Callback c) {
        doAsynGetRequest(Constant.USER_GET_HANDOFF_TEXT_URL, new HashMap<String, String>(1) {{
            put("un", darkmeUn);
        }}, null, c);
    }


    /**
     * 推送handoff文本
     *
     * @param darkmeUn 用户名
     * @param text     文本内容
     * @param c        回调接口
     */
    public static void setUserHandoffText(String darkmeUn, String text, Callback c) {
        doAsynPostRequest(Constant.USER_POST_HANDOFF_TEXT_URL, null, null, generateFormBody(darkmeUn, new HashMap<String, String>(1) {{
            put("text", text);
        }}), c);
    }

    /**
     * 如果已经接收了文本，则跳过这个文本
     *
     * @param darkmeUn 用户名
     */
    public static void haveReceivedHandOffText(String darkmeUn) {
        doSyncGetRequest(Constant.USER_TURN_OFF_HANDOFF_TEXT_URL, new HashMap<String, String>(1) {{
            put("un", darkmeUn);
        }}, null);
    }


//    Memo

    /**
     * 根据便签状态获取便签
     *
     * @param darkmeUn   用户名
     * @param isFinished 是否完成
     * @param c          回调
     */
    public static void getUserMemoByState(String darkmeUn, boolean isFinished, Callback c) {
        doAsynPostRequest(Constant.USER_GET_MEMO_BY_STATE_URL, null, null, generateFormBody(darkmeUn, new HashMap<String, String>(1) {{
            put("isFinished", String.valueOf(isFinished));
        }}), c);
    }

    /**
     * 更改用户便签完成状态
     *
     * @param darkmeUn 用户名
     * @param id       便签id
     * @param toState  要修改的状态
     * @param isDel    是否删除此条便签
     * @param c        回调
     */
    public static void changeUserMemoState(String darkmeUn, int id, int toState, boolean isDel, Callback c) {
        doAsynGetRequest(Constant.USER_CHANGE_MEMO_STATE_URL, new HashMap<String, String>(4) {{
            put("un", darkmeUn);
            put("id", String.valueOf(id));
            put("toState", String.valueOf(toState));
            put("isDel", String.valueOf(isDel));
        }}, null, c);
    }

    /**
     * 新建便签
     *
     * @param darkmeUn 用户名
     * @param t        标题
     * @param content  内容
     * @param type     状态
     * @param c        回调
     */
    public static void newUserMemo(String darkmeUn, String t, String content, int type, Callback c) {
        doAsynPostRequest(Constant.USER_NEW_MEMO_URL, null, null, generateFormBody(darkmeUn, new HashMap<String, String>(3) {{
            put("title", t);
            put("content", content);
            put("type", String.valueOf(type));
        }}), c);
    }

    /**
     * 修改好友备注
     *
     * @param un       用户名
     * @param friendId 好友用户名
     * @param mark     新备注
     * @param c        回调
     */
    public static void changeFriendMark(String un, String friendId, String mark, Callback c) {
        doAsynGetRequest(Constant.USER_CHANGE_FRIEND_MARK, new HashMap<String, String>(3) {{
            put("un", un);
            put("friendId", friendId);
            put("mark", mark);
        }}, null, c);
    }

    /**
     * 删除好友
     *
     * @param id     用户编号
     * @param userId 用户id
     * @param friId  好友id
     * @param c      回调
     */
    public static void deleteUserFriend(String id, String userId, String friId, Callback c) {
        doAsynGetRequest(Constant.USER_DELETE_FRIEND_URL, new HashMap<String, String>(3) {{
            put("id", id);
            put("userId", userId);
            put("friendId", friId);
        }}, null, c);
    }


    /**
     * 发送get网络请求
     *
     * @param url     请求网址
     * @param args    url中携带的参数
     * @param headers 请求头
     * @param c       回调
     * @param asyn    同步/异步
     * @throws IOException 异常
     */
    private static void doGetRequest(String url, Map<String, String> args, Map<String, String> headers, Callback c, boolean asyn) throws IOException {
        client = getInstance();
        Request.Builder builder = new Request.Builder()
                .url(generateGetUrl(url, args));

        if (headers != null) {
            for (String key : headers.keySet()) {
                builder.addHeader(key, headers.get(key));
            }
        }
        Request request = builder.build();
        if (asyn) {
            client.newCall(request).enqueue(c);
        } else {
            client.newCall(request).execute();
        }
    }

    /**
     * 发送post网络请求
     *
     * @param url     请求网址
     * @param args    url中携带的参数
     * @param headers 请求头
     * @param c       回调
     * @param asyn    同步/异步
     * @throws IOException 异常
     */
    private static void doPostRequest(String url, Map<String, String> args, Map<String, String> headers, RequestBody requestBody, Callback c, boolean asyn) throws IOException {
        client = getInstance();
        Request.Builder builder = new Request.Builder()
                .post(requestBody)
                .url(generateGetUrl(url, args));
        if (headers != null) {
            for (String key : headers.keySet()) {
                builder.addHeader(key, headers.get(key));
            }
        }
        Request request = builder.build();
        if (asyn) {
            client.newCall(request).enqueue(c);
        } else {
            client.newCall(request).execute();
        }
    }

    /**
     * 发送异步get网络请求
     *
     * @param url     请求网址
     * @param args    url中携带的参数
     * @param headers 请求头
     * @param c       回调
     */
    private static void doAsynGetRequest(String url, Map<String, String> args, Map<String, String> headers, Callback c) {
        ThreadPoolExecutorFactory.getThreadPoolExecutor().execute(() -> {
            try {
                doGetRequest(url, args, headers, c, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 发送同步get网络请求
     *
     * @param url     请求网址
     * @param args    url中携带的参数
     * @param headers 请求头
     */
    private static void doSyncGetRequest(String url, Map<String, String> args, Map<String, String> headers) {
        ThreadPoolExecutorFactory.getThreadPoolExecutor().execute(() -> {
            try {
                doGetRequest(url, args, headers, null, false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


    /**
     * 发送异步post网络请求
     *
     * @param url         请求网址
     * @param args        url中携带的参数
     * @param headers     请求头
     * @param requestBody 表单请求体
     * @param c           回调
     */
    private static void doAsynPostRequest(String url, Map<String, String> args, Map<String, String> headers, RequestBody requestBody, Callback c) {
        ThreadPoolExecutorFactory.getThreadPoolExecutor().execute(() -> {
            try {
                doPostRequest(url, args, headers, requestBody, c, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    /**
     * 发送同步post网络请求
     *
     * @param url         请求网址
     * @param args        url中携带的参数
     * @param headers     请求头
     * @param requestBody 表单请求体
     */
    private static void doSyncPostRequest(String url, Map<String, String> args, Map<String, String> headers, RequestBody requestBody) {
        ThreadPoolExecutorFactory.getThreadPoolExecutor().execute(() -> {
            try {
                doPostRequest(url, args, headers, requestBody, null, false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 同意好友添加请求
     *
     * @param un        用户名
     * @param destFriId 好友id
     */
    public static void userSendAddFriendMessage(String un, String destFriId) {
        doSyncGetRequest(Constant.USER_SEND_MAKE_FRIEND_MSG, new HashMap<String, String>(2) {{
            put("un", un);
            put("friendId", destFriId);
        }}, null);
    }

    /**
     * 好友请求结果
     *
     * @param id        消息id
     * @param un        用户名
     * @param destFriId 好友id
     * @param agree     同意/拒绝
     * @param c         回调
     */
    public static void userSendAgreeFriendMessage(String id, String un, String destFriId, boolean agree, Callback c) {
        doAsynGetRequest(Constant.USER_SEND_AGREE_MAKE_FRIEND_MSG, new HashMap<String, String>(4) {{
            put("id", id);
            put("un", un);
            put("friendId", destFriId);
            put("agree", String.valueOf(agree));
        }}, null, c);
    }

    /**
     * 获取用户消息
     *
     * @param un 用户名
     * @param c  回调
     */
    public static void getUserMessage(String un, Callback c) {
        doAsynGetRequest(Constant.USER_GET_MSG, new HashMap<String, String>(1) {{
            put("un", un);
        }}, null, c);
    }

    /**
     * 用户删除消息
     *
     * @param msgId 消息id
     */
    public static void deleteUserMessage(String msgId) {
        doSyncGetRequest(Constant.USER_DEL_MSG, new HashMap<String, String>(1) {{
            put("id", msgId);
        }}, null);
    }
//    update

    /**
     * 检查更新
     *
     * @param c 回调
     */
    public static void checkUpdate(Callback c) {
        doAsynGetRequest(Constant.CHECK_UPDATE_URL, null, null, c);
    }

    /**
     * 拉取更新
     *
     * @return ok响应
     * @throws IOException 异常
     */
    public static Response pullUpdate() throws IOException {
        client = getInstance();
        Request request = new Request.Builder()
                .url(Constant.PUSH_UPDATE_URL)
                .build();
        return client.newCall(request).execute();
    }

//    notice

    /**
     * 拉取学校通知
     *
     * @param c 回调
     */
    public static void pullSchoolNotice(Callback c) {
        doAsynGetRequest(Constant.SCHOOL_NOTICE_URL, null, null, c);
    }

    /**
     * 拉取翻页通知
     *
     * @param position 第几页
     * @param c        回调
     */
    public static void pullSchoolNotice(int position, Callback c) {
        doAsynGetRequest(Constant.NOTICE_NEXT_BASE_URL + position + ".htm", null, null, c);
    }

    /**
     * 获取通知香型
     *
     * @param contentUrl 内容详情url
     * @param c          回调
     */
    public static void getNoticeDetail(String contentUrl, Callback c) {
        doAsynGetRequest(contentUrl, null, null, c);
    }


    /**
     * 根据关键字搜索图书
     *
     * @param key 关键字
     * @param c   回调
     */
    public static void searchBookByKey(String key, Callback c) {
        doAsynGetRequest(Constant.BOOK_URL_BEFORE + key + Constant.BOOK_URL_AFTER, null, null, c);
    }

    /**
     * 获取图书详情
     *
     * @param detailUrl 详情url
     * @param c         回调
     */
    public static void getBookDetail(String detailUrl, Callback c) {
        doAsynGetRequest(detailUrl, null, null, c);
    }

    /**
     * 拼接get请求中的参数
     *
     * @param url  请求url
     * @param args 参数
     * @return 拼接后的url
     */
    private static String generateGetUrl(String url, Map<String, String> args) {
        StringBuilder builder = new StringBuilder();
        builder.append(url).append("?");
        if (null != args) {
            for (String key : args.keySet()) {
                builder.append(key).append("=").append(args.get(key)).append("&");
            }
        } else {
            return url;
        }
        return builder.substring(0, builder.lastIndexOf("&"));
    }
}
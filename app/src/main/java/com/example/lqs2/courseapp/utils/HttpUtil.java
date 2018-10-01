package com.example.lqs2.courseapp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.luck.picture.lib.entity.LocalMedia;

import org.jsoup.Jsoup;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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


public class HttpUtil {

    private static OkHttpClient client = null;
    private static String TAG = "HttpUtil";

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


    private static String outputPostParams(Request request) {
        StringBuilder sb = new StringBuilder();
        String method = request.method();
        if ("POST".equals(method)) {
            if (request.body() instanceof FormBody) {
                FormBody body = (FormBody) request.body();
                for (int i = 0; i < body.size(); i++) {
                    sb.append(body.encodedName(i) + "=" + body.encodedValue(i) + "\n");
                }
                sb.delete(sb.length() - 1, sb.length());
            }
        }
        return sb.toString();
    }

    public static void getCheckCode(Callback c) {


        client = getInstance();
        Request request = new Request.Builder()
                .url("http://jwjx.njit.edu.cn/CheckCode.aspx")
                .get()
                .build();
        client.newCall(request).enqueue(c);
    }

    public static void get_login_VIEWSTATE(Callback c) {
        client = getInstance();
        Request request = new Request.Builder()
                .url(Constant.main_url)
                .build();
        client.newCall(request).enqueue(c);
    }

    public static void login(String xh, String xm, String code, String cookie, String __VIEWSTATE, Callback c) {
        client = getInstance();

        FormBody.Builder formBody = new FormBody.Builder();//创建表单请求体
        formBody.add("__VIEWSTATE", __VIEWSTATE);//传递键值对参数
        formBody.add("txtUserName", xh);
        formBody.add("TextBox2", xm);
        formBody.add("txtSecretCode", code);
        formBody.add("RadioButtonList1", "学生");
        formBody.add("Button1", "");
        formBody.add("lbLanguage", "");
        formBody.add("hidPdrs", "");
        formBody.add("hidsc", "");
        RequestBody requestBody = formBody.build();

        Request request = new Request.Builder()
                .post(requestBody)
                .addHeader("contentType", "GB2312")
                .addHeader("Referer", Constant.refer_url)
                .url(Constant.main_url)
                .addHeader("Cookie", cookie)
                .build();
        client.newCall(request).enqueue(c);
    }

    public static void redirect(String xh, String cookie, Callback c) {
        client = getInstance();
        Request request = new Request.Builder()
                .addHeader("Referer", Constant.refer_url)
                .addHeader("Cookie", cookie)
                .url(Constant.forward_url + xh)
                .build();
        client.newCall(request).enqueue(c);
    }


    public static void queryCourse(Context context, String xh, String xm, String year, String team, String cookie, boolean isXn, Callback c) throws IOException {
        client = getInstance();
        Request request;

        String xncr = (String) SharedPreferenceUtil.get(context, "xncr", "");
        String xqcr = (String) SharedPreferenceUtil.get(context, "xqcr", "");

        if ((xncr.equals(year) && xqcr.equals(team)) || ("".equals(year) && "".equals(team))) {
            request = new Request.Builder()
                    .addHeader("contentType", "GB2312")
                    .addHeader("Referer", Constant.forward_url + xh)
                    .addHeader("Cookie", cookie)
                    .url("http://jwjx.njit.edu.cn/xskbcx.aspx?xh=" + xh + "&xm=" + xm + "&gnmkdm=N121603")
                    .build();
        } else {
            request = new Request.Builder()
                    .addHeader("contentType", "GB2312")
                    .addHeader("Referer", Constant.forward_url + xh)
                    .addHeader("Cookie", cookie)
                    .url("http://jwjx.njit.edu.cn/xskbcx.aspx?xh=" + xh + "&xm=" + xm + "&gnmkdm=N121603")
                    .build();
            Response response = client.newCall(request).execute();
            String html = response.body().string();
            String __VIEWSTATE = Jsoup.parse(html).select("input[name=__VIEWSTATE]").get(0).attr("value");

            FormBody builder;
            if (isXn) {
                builder = new FormBody.Builder()
                        .add("xnd", year)
                        .add("xqd", team)
                        .add("__VIEWSTATE", __VIEWSTATE)
                        .add("__EVENTARGUMENT", "")
                        .add("__EVENTTARGET", "xnd")
                        .build();
            } else {
                builder = new FormBody.Builder()
                        .add("xnd", year)
                        .add("xqd", team)
                        .add("__VIEWSTATE", __VIEWSTATE)
                        .add("__EVENTARGUMENT", "")
                        .add("__EVENTTARGET", "xqd")
                        .build();
            }
            request = new Request.Builder()
                    .addHeader("contentType", "GB2312")
                    .addHeader("Referer", Constant.forward_url + xh)
                    .addHeader("Cookie", cookie)
                    .url("http://jwjx.njit.edu.cn/xskbcx.aspx?xh=" + xh + "&xm=" + xm + "&gnmkdm=N121603")
                    .post(builder)
                    .build();
        }
        client.newCall(request).enqueue(c);
    }


    public static void showTG(Callback c) {
        client = getInstance();
        Request request = new Request.Builder()
                .url(Constant.tzgg_url)
                .build();
        client.newCall(request).enqueue(c);
    }

    public static void loadBingPic(Callback c) {
        client = getInstance();
        Request request = new Request.Builder()
                .url(Constant.bing_pic_api)
                .build();
        client.newCall(request).enqueue(c);
    }

    public static void queryGradeInit(String xh, String xm, String cookie, Callback c) {
        client = getInstance();
        Request request = new Request.Builder()
                .addHeader("contentType", "GB2312")
                .addHeader("Referer", Constant.forward_url + xh)
                .addHeader("Cookie", cookie)
                .url("http://jwjx.njit.edu.cn/xscjcx_dq.aspx?xh=" + xh + "&xm=" + xm + "&gnmkdm=N121605")
                .build();
        client.newCall(request).enqueue(c);
    }

    public static void queryGrade(String __VIEWSTATE, String xh, String xm, String cookie, String xn, String xq, Callback c) {

        FormBody.Builder formBody = new FormBody.Builder();//创建表单请求体
        formBody.add("__EVENTTARGET", "");
        formBody.add("__EVENTARGUMENT", "");
        formBody.add("__VIEWSTATE", __VIEWSTATE);
        formBody.add("ddlxn", xq);
        formBody.add("ddlxq", xn);
        formBody.add("btnCx", " 查  询 ");
        RequestBody requestBody = formBody.build();
        System.out.println(formBody.toString());
        client = getInstance();
        Request request = new Request.Builder()
                .post(requestBody)
                .addHeader("contentType", "GB2312")
                .addHeader("Referer", "http://jwjx.njit.edu.cn/xscjcx_dq.aspx?xh=" + xh + "&xm=" + xm + "&gnmkdm=N121605")
                .addHeader("Cookie", cookie)
                .url("http://jwjx.njit.edu.cn/xscjcx_dq.aspx?xh=" + xh + "&xm=" + xm + "&gnmkdm=N121605")
                .build();
        client.newCall(request).enqueue(c);
    }


    public static void loadCalendarChoosePage(String url, Callback c) {
        client = getInstance();
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(c);
    }

    public static void loadCalendarPage(String url, Callback c) {
        client = getInstance();
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(c);
    }


    public static void getOneKeyComment_VIEWSTATE(String url, String cookie, String xh, Callback c) {
        client = getInstance();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("contentType", "GB2312")
                .addHeader("Cookie", cookie)
                .addHeader("Referer", Constant.forward_url + xh)
                .get()
                .build();
        client.newCall(request).enqueue(c);
//        }

    }

    public static String getOneKeyComment_VIEWSTATE_unRecommended(String url, String cookie, String xh) {

        StringBuilder stringBuilder = new StringBuilder();

        try {
            URL strURL = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) strURL.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Cookie", cookie);
            conn.setRequestProperty("contentType", "GB2312");
            conn.setRequestProperty("Referer", Constant.forward_url + xh);
            System.out.println("------------------");
            String line;
            BufferedReader bfr = new BufferedReader(new InputStreamReader(conn.getInputStream(), "GB2312"));
            while ((line = bfr.readLine()) != null) {
                stringBuilder.append(line);
                System.out.println(line);
            }
            System.out.println("------------------");


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }


    public static void OneKeyComment(String url, String cookie, String pjkc, Callback c) {

        FormBody.Builder formBody = new FormBody.Builder();

        StringBuilder paramsString = new StringBuilder();
        Map<String, String> params = new LinkedHashMap<>();

        params.put("__EVENTTARGET", "");
        params.put("__EVENTARGUMENT", "");
        params.put("__VIEWSTATE", Constant.__VIEWSTATE_COMMENT);
        params.put("pjkc", pjkc);

        formBody.add("__EVENTTARGET", "");
        formBody.add("__EVENTARGUMENT", "");
        formBody.add("__VIEWSTATE", Constant.__VIEWSTATE_COMMENT);
        formBody.add("pjkc", pjkc);
        for (int i = 2; i < 13; i++) {
            String str1 = "DataGrid1:_ctl" + i + ":JS1";
            String str2 = "DataGrid1:_ctl" + i + ":txtjs1";
            params.put(str1, "非常满意");
            params.put(str2, "");
            if (i == 12) {
                params.put(str1, "比较满意");
                params.put(str2, "");
                break;
            }
        }
        params.put("pjxx", "");
        params.put("txt1", "");
        params.put("TextBox1", "0");
        params.put("Button1", "保  存");

        for (Iterator<String> iterator = params.keySet().iterator(); iterator.hasNext(); ) {
            String name = iterator.next();
            String value = String.valueOf(params.get(name));
            paramsString.append(name + "=" + value);
            if (iterator.hasNext())
                paramsString.append("&");
        }


        formBody.add("DataGrid1:_ctl2:JS1", "非常满意");
        formBody.add("DataGrid1:_ctl2:txtjs1", "");

        formBody.add("DataGrid1:_ctl3:JS1", "非常满意");
        formBody.add("DataGrid1:_ctl3:txtjs1", "");

        formBody.add("DataGrid1:_ctl4:JS1", "非常满意");
        formBody.add("DataGrid1:_ctl4:txtjs1", "");

        formBody.add("DataGrid1:_ctl5:JS1", "非常满意");
        formBody.add("DataGrid1:_ctl5:txtjs1", "");

        formBody.add("DataGrid1:_ctl6:JS1", "非常满意");
        formBody.add("DataGrid1:_ctl6:txtjs1", "");

        formBody.add("DataGrid1:_ctl7:JS1", "非常满意");
        formBody.add("DataGrid1:_ctl7:txtjs1", "");

        formBody.add("DataGrid1:_ctl8:JS1", "非常满意");
        formBody.add("DataGrid1:_ctl8:txtjs1", "");

        formBody.add("DataGrid1:_ctl9:JS1", "非常满意");
        formBody.add("DataGrid1:_ctl9:txtjs1", "");

        formBody.add("DataGrid1:_ctl10:JS1", "非常满意");
        formBody.add("DataGrid1:_ctl10:txtjs1", "");

        formBody.add("DataGrid1:_ctl11:JS1", "非常满意");
        formBody.add("DataGrid1:_ctl11:txtjs1", "");

        formBody.add("DataGrid1:_ctl12:JS1", "比较满意");
        formBody.add("DataGrid1:_ctl12:txtjs1", "");

        formBody.add("pjxx", "");
        formBody.add("txt1", "");
        formBody.add("TextBox1", "0");
        formBody.add("Button1", "保  存");

        RequestBody requestBodyParams = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded;charset=GB2312"), paramsString.toString());

        RequestBody requestBodyForm = formBody.build();


        client = getInstance();
        Request request = new Request.Builder()
                .addHeader("contentType", "GB2312")
                .addHeader("Referer", url)
                .addHeader("Cookie", cookie)
//                RequestBody.create(MediaType.parse("application/x-www-form-urlencoded;charset=gb2312"),""
                .url(url)
                .post(requestBodyForm)
                .build();
        System.out.println("\"======================================================\"" + paramsString.toString());
        System.out.println(outputPostParams(request));
        client.newCall(request).enqueue(c);
    }


    public static String oneKeyComment_unRecommended(String url, String cookie, String pjkc) {


        StringBuilder stringBuilder = new StringBuilder();
        try {
            URL strUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) strUrl.openConnection();
            connection.setRequestMethod("POST");

            connection.setDoOutput(true);
            connection.setDoInput(true);

            Map<String, String> map = new LinkedHashMap<>();
            StringBuilder formParams = new StringBuilder();


            connection.setRequestProperty("Cookie", cookie);

            connection.setRequestProperty("Referer", url);
            connection.setRequestProperty("contentType", "GB2312");
//            connection.setInstanceFollowRedirects(false);

            map.put("__EVENTTARGET", "");
            map.put("__EVENTARGUMENT", "");
            map.put("__VIEWSTATE", Constant.__VIEWSTATE_COMMENT);
            map.put("pjkc", pjkc);
            for (int i = 2; i < 13; i++) {
                String str1 = "DataGrid1:_ctl" + i + ":JS1";
                String str2 = "DataGrid1:_ctl" + i + ":txtjs1";
                map.put(str1, URLEncoder.encode("非常满意", "GB2312"));
                map.put(str2, "");
                if (i == 12) {
                    map.put(str1, URLEncoder.encode("比较满意", "GB2312"));
                    map.put(str2, "");
                    break;
                }
            }
            map.put("pjxx", "");
            map.put("txt1", "");
            map.put("TextBox1", "0");
            map.put("Button1", URLEncoder.encode("保  存", "GB2312"));

            for (Iterator<String> iterator = map.keySet().iterator(); iterator
                    .hasNext(); ) {
                String name = iterator.next();
                String value = String.valueOf(map.get(name));
                formParams.append(name).append("=").append(value);
                if (iterator.hasNext())
                    formParams.append("&");
            }
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(new String(formParams).getBytes());
            outputStream.flush();
            String line;
            BufferedReader bfr = new BufferedReader(new InputStreamReader(connection.getInputStream(), "GB2312"));
            while ((line = bfr.readLine()) != null) {
                stringBuilder.append(line);
                System.out.println(line);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();

    }

//    public static void getAllNews(Callback c) {
//        client = getInstance();
//        Request request = new Request.Builder()
//                .url(Constant.get_news_url)
//                .addHeader("Content-type", "application/json;charset=UTF-8")
//                .get()
//                .build();
//        client.newCall(request).enqueue(c);
//    }
//
//    public static void addOneNews(Callback c, News news) {
//
//        MediaType JSON = MediaType.parse("application/json;charset=utf-8");
//        Gson gson = new Gson();
//        String json = gson.toJson(news);
//        try {
//            json = URLEncoder.encode(json, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        RequestBody requestBody = RequestBody.create(JSON, json);
//        client = getInstance();
//        Request request = new Request.Builder()
//                .url(Constant.add_news_url)
//                .addHeader("Content-type", "application/json;charset=UTF-8")
//                .post(requestBody)
//                .build();
//        client.newCall(request).enqueue(c);
//    }

//    public static void deleteOneNews(Callback c, int id) {
//        client = getInstance();
//        Request request = new Request.Builder()
//                .url(Constant.del_news_url + "?id=" + id)
//                .build();
//        client.newCall(request).enqueue(c);
//    }


    public static void showMyFriends(String un, Callback c) {
        client = getInstance();
        Request request = new Request.Builder()
                .url(generateGetUrl(Constant.user_show_my_friend_url, new HashMap<String, String>() {{
                    put("un", un);
                }}))
                .build();
        client.newCall(request).enqueue(c);
    }

    public static void showMyFile(String un, Callback c) {
        client = getInstance();
        Request request = new Request.Builder()
                .url(generateGetUrl(Constant.file_show_url, new HashMap<String, String>() {{
                    put("un", un);
                }}))
                .build();
        client.newCall(request).enqueue(c);
    }

    public static void insertFileRecord(String file_post_author, String fileNo, String fileName, String attachment, String destination, String fileSize, Callback c) {
        FormBody.Builder builder = new FormBody.Builder()
                .add("filePostAuthor", file_post_author)
                .add("fileNo", fileNo)
                .add("fileName", fileName)
                .add("attachment", attachment)
                .add("destination", destination)
                .add("fileSize", fileSize);

        client = getInstance();
        Request request = new Request.Builder()
                .url(Constant.file_insert_record_url)
                .post(builder.build())
                .build();
        client.newCall(request).enqueue(c);
    }


    public static void deleteOneFile(String un, String fid, Callback c) {
        client = getInstance();
        Request request = new Request.Builder()
                .url(Constant.file_del_url)
                .post(getValidationFormBody(un, new HashMap<String, String>() {{
                    put("file_id", fid);
                }}))
                .build();
        client.newCall(request).enqueue(c);
    }


    private static FormBody getValidationFormBody(String un, String pwd, Map<String, String> extra) {
        FormBody.Builder builder = new FormBody.Builder()
                .add("un", un)
                .add("pwd", pwd);
        if (extra != null) {
            for (Map.Entry<String, String> entry : extra.entrySet()) {
                builder.add(entry.getKey(), entry.getValue());
            }
        }
        return builder.build();
    }

    private static FormBody getValidationFormBody(String un, Map<String, String> extra) {
        FormBody.Builder builder = new FormBody.Builder()
                .add("un", un);
        if (extra != null) {
            for (Map.Entry<String, String> entry : extra.entrySet()) {
                builder.add(entry.getKey(), entry.getValue());
            }
        }
        return builder.build();
    }

    public static void generateNewFile(String filename, Callback c) {

        doAsynGetRequest(Constant.file_generate_url, new HashMap<String, String>() {{
            put("filename", filename);
        }}, c);
    }

    public static void searchFileByNo(String un, String no, Callback c) {
        client = getInstance();
        Request request = new Request.Builder()
                .url(Constant.file_sear_url)
                .post(getValidationFormBody(un, new HashMap<String, String>() {{
                    put("fileNo", no);
                }}))
                .build();
        client.newCall(request).enqueue(c);
    }

    public static void hasMatcherUser(String username, Callback c) {
        client = getInstance();
        Request request = new Request.Builder()
                .url(Constant.user_do_exist_url + "?name=" + username)
                .get()
                .build();
        client.newCall(request).enqueue(c);
    }


    public static void userValidateDarkMe(String darkme_un, String darkme_pwd, Callback c) throws IOException {
        client = getInstance();
        Request request = new Request.Builder()
                .url(Constant.user_do_login_url)
                .post(getValidationFormBody(darkme_un, darkme_pwd, null))
                .build();
        client.newCall(request).enqueue(c);
    }

    public static Response userRegisterDarkMe(String t1, String t2, String t3, String isMan) throws IOException {
        client = getInstance();
        Request request = new Request.Builder()
                .url(Constant.user_do_register_url)
                .post(getValidationFormBody(t1, t2, new HashMap<String, String>() {{
                    put("email", t3);
                    put("isMan", isMan);
                }}))
                .build();
        return client.newCall(request).execute();
    }


//    public static void releaseNewTweet(String darkme_un, String content, List<LocalMedia> list, Callback c) {
//        client = getInstance();
//        Request request = new Request.Builder()
//                .url(Constant.tweet_post_url)
//                .post(getValidationFormBody(darkme_un, new HashMap<String, String>() {{
//                    put("content", content);
//                    for (byte i = 0; i < list.size(); i++) {
//                        String picStr = Base64ImageUtils.bitmapToBase64Str(ImageTools.compressImage(BitmapFactory.decodeFile(list.get(i).getPath())));
//                        put("picStr" + i, picStr);
//                    }
//                    put("picNum", String.valueOf(list.size()));
//                }}))
//                .build();
//        client.newCall(request).enqueue(c);
//    }


    public static void releaseNewTweet(Context context, String darkme_un, String content, List<LocalMedia> list, Callback c) {
        client = getInstance();
        Request request = new Request.Builder()
                .url(Constant.tweet_post_url)
                .post(getValidationFormBody(darkme_un, new HashMap<String, String>() {{
                    put("content", content);
                    for (byte i = 0; i < list.size(); i++) {
                        String path = getImageReadPath(list.get(i));
                        Log.d(TAG, "image" + i + ": " + path);
                        String result = uploadImage(context, path);
                        if (!"-1".equals(result) && !"error".equals(result)) {
                            put("imgPath" + i, result);
                        }
                    }
                }}))
                .build();
        client.newCall(request).enqueue(c);
    }

    private static String uploadImage(Context context, String imagePath) {
        client = getInstance();
        final String[] result = {"-2"};
        Luban.with(context) // 初始化
                .load(imagePath) // 要压缩的图片
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onSuccess(File file) {
                        // 压缩成功后调用，返回压缩后的图片文件
                        MultipartBody.Builder builder = new MultipartBody.Builder();
                        builder.addFormDataPart("file", imagePath,
                                RequestBody.create(MediaType.parse("application/octet-stream"), new File(file.getAbsolutePath())));
                        RequestBody requestBody = builder.build();
                        Request request = new Request.Builder()
                                .url(Constant.upload_image_url)
                                .post(requestBody)
                                .build();
                        try {
                            new Thread(() -> {
                                Response response = null;
                                try {
                                    response = client.newCall(request).execute();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                Log.d(TAG, "响应码: " + response.code());
                                if (response.isSuccessful()) {
                                    String resultValue = null;
                                    try {
                                        resultValue = response.body().string();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    Log.d(TAG, "响应体：" + resultValue);
                                    result[0] = resultValue;
                                }
                            }).start();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                }).launch(); // 启动压缩
        while (result[0].equals("-2")) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return result[0];
    }

    private static String getImageReadPath(LocalMedia media) {

        // 例如 LocalMedia 里面返回三种path
        // 1.media.getPath(); 为原图path
        // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
        // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
        // 如果裁剪并压缩了，已取压缩路径为准，因为是先裁剪后压缩的

        if (!media.isCut() && !media.isCompressed()) {
            return media.getPath();
        }

        if (media.isCompressed()) {
            return media.getCompressPath();
        }
        return media.getCutPath();
    }

    public static void getAllTweets(int startPosition, Callback c) {
        client = getInstance();
        Request request = new Request.Builder()
                .url(generateGetUrl(Constant.tweet_get_all_url, new HashMap<String, String>() {{
                    put("sp", String.valueOf(startPosition));
                }}))
                .build();
        client.newCall(request).enqueue(c);
    }

    public static void deleteTweet(String id, Callback c) {

        client = getInstance();
        Request request = new Request.Builder()
                .url(generateGetUrl(Constant.tweet_del_url, new HashMap<String, String>() {{
                    put("id", id);
                }}))
                .build();
        client.newCall(request).enqueue(c);
    }

    public static void modifyTweetGood(String id, boolean add) {

        client = getInstance();
        Request request = new Request.Builder()
                .url(generateGetUrl(Constant.tweet_praise_add_url, new HashMap<String, String>() {{
                    put("id", id);
                    put("add", String.valueOf(add));
                }}))
                .build();
        try {
            client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void showUserTweetInfo(String un, String tweetId, Callback c) {
        client = getInstance();
        Request request = new Request.Builder()
                .url(generateGetUrl(Constant.user_tweet_info_url, new HashMap<String, String>() {{
                    put("un", un);
                    put("tweetId", tweetId);
                }}))
                .build();
        client.newCall(request).enqueue(c);
    }

    public static void userPraiseTweet(String un, String tweetId, boolean praise) {
        doSyncGetRequest(Constant.user_praise_tweet_url, new HashMap<String, String>() {{
            put("un", un);
            put("tweetId", tweetId);
            put("praise", String.valueOf(praise));
        }}, null);
    }

    public static Response userCollectTweet(String un, String tweetId, boolean collect) throws IOException {
        client = getInstance();
        Request request = new Request.Builder()
                .url(generateGetUrl(Constant.user_collect_tweet_url, new HashMap<String, String>() {{
                    put("un", un);
                    put("tweetId", tweetId);
                    put("collect", String.valueOf(collect));
                }}))
                .build();
        return client.newCall(request).execute();
    }


    public static void setUserProfilePicture(String darkme_un, Bitmap headImgBit, Callback c) {
        String picStr = Base64ImageUtils.bitmapToBase64Str(headImgBit);
        client = getInstance();
        Request request = new Request.Builder()
                .url(Constant.user_set_profile_picture_url)
                .post(getValidationFormBody(darkme_un, new HashMap<String, String>() {{
                    put("profilePicStr", picStr);
                }}))
                .build();
        client.newCall(request).enqueue(c);
    }

    public static void getUserProfilePicture(String darkme_un, Callback c) {
        doAsynGetRequest(Constant.user_get_profile_picture_url, new HashMap<String, String>() {{
            put("un", darkme_un);
        }}, c);
    }

    public static void getUserHandoffText(String darkme_un, Callback c) {
        doAsynGetRequest(Constant.user_get_handoff_text_url, new HashMap<String, String>() {{
            put("un", darkme_un);
        }}, c);
    }


    public static void setUserHandoffText(String darkme_un, String text, Callback c) {
        client = getInstance();
        Request request = new Request.Builder()
                .url(Constant.user_post_handoff_text_url)
                .post(getValidationFormBody(darkme_un, new HashMap<String, String>() {{
                    put("text", text);
                }}))
                .build();
        client.newCall(request).enqueue(c);
    }

    public static void haveReceivedHandOffText(String darkme_un) {
        doSyncGetRequest(Constant.user_turn_off_handoff_text_url, new HashMap<String, String>() {{
            put("un", darkme_un);
        }}, null);
    }


//    Memo

    public static void getUserMemoByState(String darkme_un, boolean isFinished, Callback c) {
        client = getInstance();
        Request request = new Request.Builder()
                .url(Constant.user_get_memo_by_state_url)
                .post(getValidationFormBody(darkme_un, new HashMap<String, String>() {{
                    put("isFinished", String.valueOf(isFinished));
                }}))
                .build();
        client.newCall(request).enqueue(c);
    }

    public static void changeUserMemoState(String darkme_un, int id, int toState, boolean isDel, Callback c) {
        doAsynGetRequest(Constant.user_change_memo_state_url, new HashMap<String, String>() {{
            put("un", darkme_un);
            put("id", String.valueOf(id));
            put("toState", String.valueOf(toState));
            put("isDel", String.valueOf(isDel));
        }}, c);
    }

    public static void newUserMemo(String darkme_un, String t, String content, int type, Callback c) {
        client = getInstance();
        Request request = new Request.Builder()
                .url(Constant.user_new_memo_url)
                .post(getValidationFormBody(darkme_un, new HashMap<String, String>() {
                    {
                        put("title", t);
                        put("content", content);
                        put("type", String.valueOf(type));
                    }
                }))
                .build();
        client.newCall(request).enqueue(c);
    }

    //    firend
    public static void changeFriendMark(String un, String friendId, String mark, Callback c) {

        doSyncGetRequest(Constant.user_change_friend_mark, new HashMap<String, String>() {{
            put("un", un);
            put("friendId", friendId);
            put("mark", mark);
        }}, c);
    }

    public static void deleteUserFriend(String id, String userId, String friId, Callback c) {
        doAsynGetRequest(Constant.user_delete_friend_url, new HashMap<String, String>() {{
            put("id", id);
            put("userId", userId);
            put("friendId", friId);
        }}, c);
    }


    private static void doGetRequest(String url, HashMap<String, String> args, Callback c, boolean asyn) throws IOException {

        client = getInstance();
        Request request = new Request.Builder()
                .url(generateGetUrl(url, args))
                .build();
        if (asyn) {
            client.newCall(request).enqueue(c);
        } else {
            client.newCall(request).execute();
        }
    }

    private static void doAsynGetRequest(String url, HashMap<String, String> args, Callback c) {
        new Thread(() -> {
            try {
                doGetRequest(url, args, c, true);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }).start();
    }

    private static void doSyncGetRequest(String url, HashMap<String, String> args, Callback c) {
        new Thread(() -> {
            try {
                doGetRequest(url, args, c, false);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }).start();
    }

    public static void userSendAddFriendMessage(String un, String destFriId) {
        doSyncGetRequest(Constant.user_send_make_friend_msg, new HashMap<String, String>() {{
            put("un", un);
            put("friendId", destFriId);
        }}, null);
    }

    public static void userSendAgreeFriendMessage(String id, String un, String destFriId, boolean agree, Callback c) {
        doSyncGetRequest(Constant.user_send_agree_make_friend_msg, new HashMap<String, String>() {{
            put("id", id);
            put("un", un);
            put("friendId", destFriId);
            put("agree", String.valueOf(agree));
        }}, c);
    }

    public static void getUserMessage(String un, Callback c) {
        doAsynGetRequest(Constant.user_get_msg, new HashMap<String, String>() {{
            put("un", un);
        }}, c);
    }

    public static void deleteUserMessage(String msgId) {
        doSyncGetRequest(Constant.user_del_msg, new HashMap<String, String>() {{
            put("id", msgId);
        }}, null);
    }

//    update

    public static void checkUpdate(Callback c) {
        doAsynGetRequest(Constant.check_update_url, null, c);
    }

    public static Response pushUpdate() throws IOException {

        client = getInstance();
        Request request = new Request.Builder()
                .url(Constant.push_update_url)
                .build();
        return client.newCall(request).execute();
    }

//    notice

    public static void pushSchoolNotice(Callback c) {
        doAsynGetRequest(Constant.school_notice_url, null, c);
    }

    public static void pushSchoolNotice(int position, Callback c) {
        doAsynGetRequest(Constant.notice_next_base_url + position + ".htm", null, c);
    }

    public static void getNoticeDetail(String contentUrl, Callback c) {
        doAsynGetRequest(contentUrl, null, c);
    }

    private static String generateGetUrl(String url, HashMap<String, String> args) {
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
package com.example.lqs2.courseapp.activities;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.lqs2.courseapp.R;
import com.example.lqs2.courseapp.utils.Constant;
import com.example.lqs2.courseapp.utils.HttpUtil;
import com.example.lqs2.courseapp.utils.ImageTools;
import com.example.lqs2.courseapp.utils.PermissionUtils;
import com.github.chrisbanes.photoview.PhotoView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SchoolCalendarActivity extends ActivityCollector {
    private PhotoView calendar_view;
    private ProgressBar progressBar;

    private TextView textView;
//    private Button download;
    private String picName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_calendar);

        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        calendar_view = findViewById(R.id.school_calendar_view);
        textView = findViewById(R.id.school_calendar_text_view);
        calendar_view.setOnLongClickListener(v -> {


            if (PermissionUtils.checkWriteExtraStoragePermission(this)) {
               download();
            } else {
                PermissionUtils.requestWritePermission(this, this, PermissionUtils.CODE_WRITE_EXTERNAL_STORAGE);
            }
            return true;
        });
        progressBar = findViewById(R.id.school_calendar_bar);
//        download = findViewById(R.id.download_calendar);
//        download.setEnabled(false);
//
//
//        download.setOnClickListener(v -> {
//
//
//        });

        progressBar.setVisibility(View.VISIBLE);
        loadSchoolCalendar();


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionUtils.CODE_WRITE_EXTERNAL_STORAGE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            download();
        }

    }

    private void download() {
        calendar_view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        calendar_view.layout(0, 0, calendar_view.getMeasuredWidth(), calendar_view.getMeasuredHeight());
        calendar_view.buildDrawingCache();
        Bitmap pic = calendar_view.getDrawingCache();
        if (picName != null) {
            ImageTools.saveBmp2Gallery(pic, picName, this);
        } else {
            Toast.makeText(this, "下载出错", Toast.LENGTH_LONG).show();
        }
    }


    private void loadSchoolCalendar() {
        HttpUtil.loadCalendarChoosePage(Constant.school_calendar_choose_url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String html1 = response.body().string();
                Document doc = Jsoup.parse(html1);
                Element ul_li_0 = doc.getElementById("line_u13_0");
                Element a = ul_li_0.selectFirst("a");
//                System.out.println(a.text());
                String link = a.attr("href");
                link = "http://jwc.njit.edu.cn/" + link;
                HttpUtil.loadCalendarPage(link, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Toast.makeText(SchoolCalendarActivity.this, "浏览出错", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        String html = response.body().string();
                        Document doc = Jsoup.parse(html);
                        Elements imgs = doc.getElementsByClass("img_vsb_content");
                        Element img = imgs.first();
//                        获取图片的绝对路径
                        String pic_url = img.attr("src");
                        pic_url = "http://jwc.njit.edu.cn" + pic_url;
                        String finalPic_url = pic_url;
                        runOnUiThread(() -> {
                            picName = a.text();
                            textView.setText(picName);
                            Glide.with(SchoolCalendarActivity.this).load(finalPic_url).into(calendar_view);
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(SchoolCalendarActivity.this, "喵～长按图片保存到本地", Toast.LENGTH_LONG).show();
                        });
                    }
                });
            }
        });
    }
}

package com.example.lqs2.courseapp.activities;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.lqs2.courseapp.R;
import com.example.lqs2.courseapp.utils.HttpUtil;
import com.example.lqs2.courseapp.utils.ImageTools;
import com.example.lqs2.courseapp.utils.PermissionUtils;
import com.example.lqs2.courseapp.utils.StatusBarUtils;
import com.github.chrisbanes.photoview.PhotoView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 加载学校最新校历
 *
 * @author lqs2
 */
public class SchoolCalendarActivity extends ActivityCollector {
    private PhotoView calendarView;
    private ProgressBar progressBar;
    private TextView textView;
    private String picName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_calendar);

        StatusBarUtils.setStatusTransparent(this);
        calendarView = findViewById(R.id.school_calendar_view);
        textView = findViewById(R.id.school_calendar_text_view);
        calendarView.setOnLongClickListener(v -> {
            if (PermissionUtils.checkWriteExtraStoragePermission(this)) {
                download();
            } else {
                PermissionUtils.requestWritePermission(this, this, PermissionUtils.CODE_WRITE_EXTERNAL_STORAGE);
            }
            return true;
        });
        progressBar = findViewById(R.id.school_calendar_bar);

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

    /**
     * 下载校历
     */
    private void download() {
        calendarView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        calendarView.layout(0, 0, calendarView.getMeasuredWidth(), calendarView.getMeasuredHeight());
        calendarView.buildDrawingCache();
        Bitmap pic = calendarView.getDrawingCache();
        if (picName != null) {
            ImageTools.saveBmp2Gallery(pic, picName, this);
        } else {
            Toast.makeText(this, "下载出错", Toast.LENGTH_LONG).show();
        }
    }


    /**
     * 加载校历
     */
    private void loadSchoolCalendar() {
        HttpUtil.loadCalendarChoosePage(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                String html1 = response.body().string();
                Document doc = Jsoup.parse(html1);
                Element ulLi0 = doc.getElementById("line_u13_0");
                Element a = ulLi0.selectFirst("a");
                String link = a.attr("href");
                link = "http://jwc.njit.edu.cn/" + link;
                HttpUtil.loadCalendarPage(link, new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        Toast.makeText(SchoolCalendarActivity.this, "浏览出错", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                        assert response.body() != null;
                        String html = response.body().string();
                        Document doc = Jsoup.parse(html);
                        Elements imgs = doc.getElementsByClass("img_vsb_content");
                        Element img = imgs.first();
//                        获取图片的绝对路径
                        String picUrl = img.attr("src");
                        picUrl = "http://jwc.njit.edu.cn" + picUrl;
                        String finalPicUrl = picUrl;
                        runOnUiThread(() -> {
                            picName = a.text();
                            textView.setText(picName);
                            Glide.with(SchoolCalendarActivity.this).load(finalPicUrl).into(calendarView);
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(SchoolCalendarActivity.this, "喵～长按图片保存到本地", Toast.LENGTH_LONG).show();
                        });
                    }
                });
            }
        });
    }
}

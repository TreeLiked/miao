package com.example.lqs2.courseapp.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.lqs2.courseapp.adapters.GradeAdapter;
import com.example.lqs2.courseapp.entity.Grade;
import com.example.lqs2.courseapp.utils.Constant;
import com.example.lqs2.courseapp.R;
import com.example.lqs2.courseapp.utils.HtmlCodeExtractUtil;
import com.example.lqs2.courseapp.utils.HttpUtil;
import com.example.lqs2.courseapp.utils.Tools;
import com.github.ybq.android.spinkit.style.Wave;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class GradeActivity extends ActivityCollector {
    private LinearLayout linearLayout;
    private Spinner xn_spinner;
    private Spinner xq_spinner;
    private Button query_btn;

    private List<Grade> mGradeList;
    private GradeAdapter gradeAdapter;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade);

        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.hide();

        linearLayout = findViewById(R.id.grade_linear_layout);
        swipeRefresh = findViewById(R.id.grade_swipe_refresh);
        progressBar = findViewById(R.id.grade_progress_bar);
        xn_spinner = findViewById(R.id.xn_spinner);
        xq_spinner = findViewById(R.id.xq_spinner);
        query_btn = findViewById(R.id.query_grade_btn);
        final RecyclerView grade_recycler_view = findViewById(R.id.grade_recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        grade_recycler_view.setLayoutManager(layoutManager);


        linearLayout.setPadding(0, Tools.getStatusBarHeight(GradeActivity.this), 0, 0);
        Wave wave = new Wave();
        progressBar.setIndeterminateDrawable(wave);
        progressBar.setVisibility(View.GONE);


        Intent intent = getIntent();
        final String __VIEWSTATE = intent.getStringExtra("__VIEWSTATE");
        final String cookie = intent.getStringExtra("cookie");
        final String xh = intent.getStringExtra("xh");
        final String xm = intent.getStringExtra("xm");
        ArrayList xnList = intent.getStringArrayListExtra("xnList");
        ArrayList xqList = intent.getStringArrayListExtra("xqList");
        System.out.println(xnList.toString());
        System.out.println(xqList.toString());

        ArrayAdapter xn_spinner_adapter = new ArrayAdapter<>(GradeActivity.this, R.layout.grade_xn_xq_item, xnList);
        ArrayAdapter xq_spinner_adapter = new ArrayAdapter<>(GradeActivity.this, R.layout.grade_xn_xq_item, xqList);


        xn_spinner.setAdapter(xn_spinner_adapter);
        xq_spinner.setAdapter(xq_spinner_adapter);


        xn_spinner.setSelection(1, true);
        xq_spinner.setSelection(1, true);

        query_btn.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);

            final String xn = (String) xn_spinner.getSelectedItem();
            final String xq = (String) xq_spinner.getSelectedItem();
            HttpUtil.queryGrade(__VIEWSTATE, xh, xm, cookie, xn, xq, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String html = response.body().string();
//                        System.out.println(html);
                    mGradeList = HtmlCodeExtractUtil.getGradeList(html);
                    System.out.println("-------------------------------\n" + html);
                    runOnUiThread(() -> {
                        if (gradeAdapter != null) {
                            gradeAdapter.clear();
                        }
                        gradeAdapter = new GradeAdapter(mGradeList, xn, xq);
                        grade_recycler_view.setAdapter(gradeAdapter);
                        progressBar.setVisibility(View.GONE);
                        if (gradeAdapter.getItemCount() == 0) {
                            Toast.makeText(GradeActivity.this, Constant.no_course_info, Toast.LENGTH_LONG).show();
                            GradeAdapter.isAllPassed = false;
                        }
                        if (GradeAdapter.isAllPassed) {
                            Toast.makeText(GradeActivity.this, Constant.course_all_passed_info, Toast.LENGTH_LONG).show();
                        }
                        GradeAdapter.isAllPassed = true;
                    });

                }
            });
        });

        swipeRefresh.setColorSchemeResources(R.color.r4, R.color.r3, R.color.r1, R.color.r5);
        swipeRefresh.setOnRefreshListener(() -> {
            Intent intent1 = new Intent(GradeActivity.this, MainActivity.class);
            startActivity(intent1);
            swipeRefresh.setRefreshing(false);
        });
    }
}

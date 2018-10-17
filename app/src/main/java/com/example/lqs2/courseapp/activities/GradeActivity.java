package com.example.lqs2.courseapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.lqs2.courseapp.R;
import com.example.lqs2.courseapp.adapters.GradeAdapter;
import com.example.lqs2.courseapp.entity.Grade;
import com.example.lqs2.courseapp.utils.Constant;
import com.example.lqs2.courseapp.utils.HtmlCodeExtractUtil;
import com.example.lqs2.courseapp.utils.HttpUtil;
import com.example.lqs2.courseapp.utils.StatusBarUtils;
import com.example.lqs2.courseapp.utils.Tools;
import com.github.ybq.android.spinkit.style.Wave;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 教务系统成绩查询活动
 *
 * @author lqs2
 */
public class GradeActivity extends ActivityCollector {
    private Spinner xnSpinner;
    private Spinner xqSpinner;

    private List<Grade> mGradeList;
    private GradeAdapter gradeAdapter;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade);
        StatusBarUtils.setStatusTransparent(this);


        LinearLayout linearLayout = findViewById(R.id.grade_linear_layout);
        swipeRefresh = findViewById(R.id.grade_swipe_refresh);
        progressBar = findViewById(R.id.grade_progress_bar);
        xnSpinner = findViewById(R.id.xn_spinner);
        xqSpinner = findViewById(R.id.xq_spinner);
        Button queryBtn = findViewById(R.id.query_grade_btn);
        final RecyclerView gradeRecyclerView = findViewById(R.id.grade_recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        gradeRecyclerView.setLayoutManager(layoutManager);


        linearLayout.setPadding(0, Tools.getStatusBarHeight(GradeActivity.this), 0, 0);
        Wave wave = new Wave();
        progressBar.setIndeterminateDrawable(wave);
        progressBar.setVisibility(View.GONE);


        Intent intent = getIntent();
        final String viewstate = intent.getStringExtra("__VIEWSTATE");
        final String cookie = intent.getStringExtra("cookie");
        final String xh = intent.getStringExtra("xh");
        final String xm = intent.getStringExtra("xm");
        ArrayList xnList = intent.getStringArrayListExtra("xnList");
        ArrayList xqList = intent.getStringArrayListExtra("xqList");
        System.out.println(xnList.toString());
        System.out.println(xqList.toString());

        ArrayAdapter xnSpinnerAdapter = new ArrayAdapter<>(GradeActivity.this, R.layout.grade_xn_xq_item, xnList);
        ArrayAdapter xqSpinnerAapter = new ArrayAdapter<>(GradeActivity.this, R.layout.grade_xn_xq_item, xqList);


        xnSpinner.setAdapter(xnSpinnerAdapter);
        xqSpinner.setAdapter(xqSpinnerAapter);


        xnSpinner.setSelection(1, true);
        xqSpinner.setSelection(1, true);

        queryBtn.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            final String xn = (String) xnSpinner.getSelectedItem();
            final String xq = (String) xqSpinner.getSelectedItem();
            HttpUtil.queryGrade(viewstate, xh, xm, cookie, xn, xq, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    assert response.body() != null;
                    String html = response.body().string();
                    mGradeList = HtmlCodeExtractUtil.getGradeList(html);
                    runOnUiThread(() -> {
                        if (gradeAdapter != null) {
                            gradeAdapter.clear();
                        }
                        gradeAdapter = new GradeAdapter(mGradeList, xn, xq);
                        gradeRecyclerView.setAdapter(gradeAdapter);
                        progressBar.setVisibility(View.GONE);
                        if (gradeAdapter.getItemCount() == 0) {
                            Toast.makeText(GradeActivity.this, Constant.NO_COURSE_INFO, Toast.LENGTH_LONG).show();
                            GradeAdapter.isAllPassed = false;
                        }
                        if (GradeAdapter.isAllPassed) {
                            Toast.makeText(GradeActivity.this, Constant.COURSE_ALL_PASSED_INFO, Toast.LENGTH_LONG).show();
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

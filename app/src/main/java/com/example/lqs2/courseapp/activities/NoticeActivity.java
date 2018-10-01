package com.example.lqs2.courseapp.activities;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;

import com.example.lqs2.courseapp.R;
import com.example.lqs2.courseapp.adapters.NoticeAdapter;
import com.example.lqs2.courseapp.utils.HtmlCodeExtractUtil;
import com.example.lqs2.courseapp.utils.HttpUtil;
import com.example.lqs2.courseapp.utils.StatusBarUtils;
import com.example.lqs2.courseapp.utils.ToastUtils;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class NoticeActivity extends AppCompatActivity {


    private SwipeRefreshLayout layout;
    private SwipeMenuRecyclerView recyclerView;
    private NoticeAdapter adapter;
    public static int anchorPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_notice);


        StatusBarUtils.setStatusBarTransparentAndTextColorBlack(this);

        bindViews();
        init();
        initRefresh();

        pushData();
    }

    private void initRefresh() {

        layout.setOnRefreshListener(this::pushData);
        recyclerView.useDefaultLoadMore();
//        recyclerView.setAutoLoadMore(true);
        recyclerView.setLoadMoreListener(this::loadMore);
        recyclerView.setAdapter(adapter);

    }

    private void init() {
        adapter = new NoticeAdapter(this, this);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    private void bindViews() {
        layout = findViewById(R.id.notice_recycle_view_refresh_layout);
        recyclerView = findViewById(R.id.notice_recycle_view);

    }

    private void pushData() {
        HttpUtil.pushSchoolNotice(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> layout.setRefreshing(false));
                ToastUtils.showConnectErrorOnMain(NoticeActivity.this, NoticeActivity.this);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                runOnUiThread(() -> {
                    layout.setRefreshing(false);
                    adapter.setData(HtmlCodeExtractUtil.parseHtmlForNotice(resp, true));
                });
            }
        });
    }

    private void loadMore() {
        HttpUtil.pushSchoolNotice(anchorPosition, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> layout.setRefreshing(false));
                ToastUtils.showConnectErrorOnMain(NoticeActivity.this, NoticeActivity.this);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                runOnUiThread(() -> {
                    anchorPosition--;
                    recyclerView.loadMoreFinish(false, true);
                    layout.setRefreshing(false);
                    adapter.setData(HtmlCodeExtractUtil.parseHtmlForNotice(resp, false));
                });
            }
        });
    }
}

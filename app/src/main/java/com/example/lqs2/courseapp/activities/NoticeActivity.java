package com.example.lqs2.courseapp.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.WindowManager;
import android.widget.EditText;

import com.example.lqs2.courseapp.R;
import com.example.lqs2.courseapp.adapters.NoticeAdapter;
import com.example.lqs2.courseapp.entity.Notice;
import com.example.lqs2.courseapp.utils.HtmlCodeExtractUtil;
import com.example.lqs2.courseapp.utils.HttpUtil;
import com.example.lqs2.courseapp.utils.StatusBarUtils;
import com.example.lqs2.courseapp.utils.ToastUtils;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 通知activity
 *
 * @author lqs2
 */
public class NoticeActivity extends ActivityCollector {
    private SwipeRefreshLayout layout;
    private SwipeMenuRecyclerView recyclerView;
    private EditText searchEdit;

    private NoticeAdapter adapter;

    public static int anchorPosition;
    private int mLastVisibleItemPosition;

    private List<Notice> noticeList = new ArrayList<>();
    private List<Notice> noticeSearchList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_notice);


        StatusBarUtils.setStatusBarTransparentAndTextColorBlack(this);

        bindViews();
        init();
        initRefresh();

        pullData();
    }

    /**
     * 初始化布局
     */
    private void initRefresh() {
        layout.setOnRefreshListener(this::pullData);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (layoutManager instanceof LinearLayoutManager) {
                    mLastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                }
                if (adapter != null) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE
                            && mLastVisibleItemPosition + 1 == adapter.getItemCount()) {
                        loadMore();
                    }
                }
            }
        });
        recyclerView.setAdapter(adapter);
    }

    /**
     * 初始化动作
     */
    private void init() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        adapter = new NoticeAdapter(this, this);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    if (null != noticeList && noticeList.size() > 0) {
                        noticeSearchList.clear();
                        for (int i = 0; i < noticeList.size(); i++) {
                            Notice notice = noticeList.get(i);
                            String str = s.toString().trim();
                            System.out.println(notice.toString());
                            if (notice.getTitle().contains(str) || notice.getTime().contains(str)) {
                                noticeSearchList.add(notice);
                            }
                        }
                        adapter.setData(noticeSearchList);
                    }
                } else {
                    adapter.setData(noticeList);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

        });
    }

    /**
     * 绑定视图
     */
    private void bindViews() {
        layout = findViewById(R.id.notice_recycle_view_refresh_layout);
        recyclerView = findViewById(R.id.notice_recycle_view);
        searchEdit = findViewById(R.id.notice_search_edit);

    }

    /**
     * 拉取数据
     */
    private void pullData() {
        HttpUtil.pullSchoolNotice(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> layout.setRefreshing(false));
                ToastUtils.showConnectErrorOnMain(NoticeActivity.this, NoticeActivity.this);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                String resp = response.body().string();
                List<Notice> notices = HtmlCodeExtractUtil.parseHtmlForNotice(resp, true);
                runOnUiThread(() -> {
                    layout.setRefreshing(false);
                    noticeList.addAll(notices);
                    adapter.setData(noticeList);
                });
            }
        });
    }

    /**
     * 向下拉加载更多数据
     */
    private void loadMore() {
        HttpUtil.pullSchoolNotice(anchorPosition, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> layout.setRefreshing(false));
                ToastUtils.showConnectErrorOnMain(NoticeActivity.this, NoticeActivity.this);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                String resp = response.body().string();
                runOnUiThread(() -> {
                    anchorPosition--;
                    List<Notice> notices = HtmlCodeExtractUtil.parseHtmlForNotice(resp, false);
                    if (notices != null && notices.size() > 0) {
                        noticeList.addAll(notices);
                        adapter.setData(noticeList);
                    } else {
                        recyclerView.loadMoreFinish(true, false);
                    }
                    recyclerView.loadMoreFinish(false, true);

                });
            }
        });
    }
}

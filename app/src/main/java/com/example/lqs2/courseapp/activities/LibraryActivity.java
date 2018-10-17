package com.example.lqs2.courseapp.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lqs2.courseapp.R;
import com.example.lqs2.courseapp.adapters.BookAdapter;
import com.example.lqs2.courseapp.common.StringUtils;
import com.example.lqs2.courseapp.entity.Book;
import com.example.lqs2.courseapp.utils.HtmlCodeExtractUtil;
import com.example.lqs2.courseapp.utils.HttpUtil;
import com.example.lqs2.courseapp.utils.StatusBarUtils;
import com.example.lqs2.courseapp.utils.ToastUtils;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 图书馆图书检索活动
 *
 * @author lqs2
 */
public class LibraryActivity extends ActivityCollector {


    private SwipeMenuRecyclerView recyclerView;
    private EditText searchEdit;

    private BookAdapter adapter;
    private List<Book> bookList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
        StatusBarUtils.setStatusBarTransparentAndTextColorBlack(this);
        bindViews();
        init();

    }

    /**
     * 初始化组件
     */
    private void init() {
        adapter = new BookAdapter(this, this);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);


        searchEdit.setOnEditorActionListener((v, actionId, event) -> {
            boolean done = actionId == EditorInfo.IME_ACTION_SEND
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction());
            if (done) {
                String key = searchEdit.getText().toString();
                if (!StringUtils.isEmpty(key)) {
                    searchBook(key);
                }
                ((InputMethodManager) Objects.requireNonNull(getSystemService(Context.INPUT_METHOD_SERVICE))).hideSoftInputFromWindow(Objects.requireNonNull(LibraryActivity.this.getCurrentFocus()).getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
            return true;
        });
    }

    /**
     * 绑定视图
     */
    private void bindViews() {
        recyclerView = findViewById(R.id.library_recycle_view);
        searchEdit = findViewById(R.id.library_search_edit);

    }

    /**
     * 查询图书
     *
     * @param key 图书关键字
     */
    private void searchBook(String key) {
        HttpUtil.searchBookByKey(key, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                ToastUtils.showConnectErrorOnMain(LibraryActivity.this, LibraryActivity.this);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                String resp = response.body().string();
                runOnUiThread(() -> {
                    if (!StringUtils.isEmpty(resp) && !resp.contains("本馆没有您检索的图书")) {
                        bookList.addAll(HtmlCodeExtractUtil.parseHtmlForBook(resp));
                        adapter.setData(bookList);
                    } else {
                        ToastUtils.showToast(LibraryActivity.this, "本馆没有您检索的图书", Toast.LENGTH_LONG);
                    }
                });
            }
        });
    }
}

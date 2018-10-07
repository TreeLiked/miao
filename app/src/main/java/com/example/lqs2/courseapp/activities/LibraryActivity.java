package com.example.lqs2.courseapp.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LibraryActivity extends AppCompatActivity {


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

    private void init() {

        adapter = new BookAdapter(this, this);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);


        searchEdit.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {

                String key = searchEdit.getText().toString();
                if (!StringUtils.isEmpty(key)) {
                    searchBook(key);
                }
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(LibraryActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
            return true;
        });
    }

    private void bindViews() {
        recyclerView = findViewById(R.id.library_recycle_view);
        searchEdit = findViewById(R.id.library_search_edit);

    }

    private void searchBook(String key) {
        HttpUtil.searchBookByKey(key, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ToastUtils.showConnectErrorOnMain(LibraryActivity.this, LibraryActivity.this);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
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
//        HttpUtil.pushSchoolNotice(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                runOnUiThread(() -> layout.setRefreshing(false));
//                ToastUtils.showConnectErrorOnMain(LibraryActivity.this, LibraryActivity.this);
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                String resp = response.body().string();
//
//                runOnUiThread(() -> {
//                    layout.setRefreshing(false);
//                    if (!StringUtils.isEmpty(resp) && !resp.contains("本馆没有您检索的图书")) {
//                        bookList.addAll(HtmlCodeExtractUtil.parseHtmlForBook(resp));
//                        adapter.setData(bookList);
//                    }
//                });
//            }
//        });
    }
}

package com.example.lqs2.courseapp.activities;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lqs2.courseapp.R;
import com.example.lqs2.courseapp.adapters.CardPagerAdapter;
import com.example.lqs2.courseapp.adapters.ShadowTransformer;
import com.example.lqs2.courseapp.entity.CardItem;
import com.example.lqs2.courseapp.entity.Memo;
import com.example.lqs2.courseapp.utils.Constant;
import com.example.lqs2.courseapp.utils.HttpUtil;
import com.example.lqs2.courseapp.utils.MaterialDialogUtils;
import com.example.lqs2.courseapp.utils.StatusBarUtils;
import com.example.lqs2.courseapp.utils.TimeUtils;
import com.example.lqs2.courseapp.utils.ToastUtils;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MemoActivity extends ActivityCollector implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {


    private List<Memo> memoList;
    private List<CardItem> cardItemList = new ArrayList<>();
    private Gson gson = new Gson();
    private String un;
    private ViewPager mViewPager;


    private CardPagerAdapter mCardAdapter;
    private ShadowTransformer mCardShadowTransformer;
    private ImageView bgView;

    private FloatingActionsMenu menu;
    private TextView title;

    public static TextView currentPositionView;

    private String darkme_un = "";
    public ProgressBar loadBar;
//    private CardFragmentPagerAdapter mFragmentCardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);

        darkme_un = getIntent().getStringExtra("darkme_un");

        mViewPager = findViewById(R.id.view_pager_memo);
        bgView = findViewById(R.id.memo_bg_view);
        menu = findViewById(R.id.memo_float_menu);
        title = findViewById(R.id.memo_page_title);
        currentPositionView = findViewById(R.id.memo_current_num);
        loadBar = findViewById(R.id.memo_load_progress_bar);
        loadBar.setVisibility(View.GONE);


        StatusBarUtils.setStatusTransparent(this);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        mCardAdapter = new CardPagerAdapter(this, this, 0);
        FloatingActionsMenu menu = findViewById(R.id.memo_float_menu);
        FloatingActionButton showFinishedTask = findViewById(R.id.memo_choice_finished);
        FloatingActionButton showUnfinishedTask = findViewById(R.id.memo_choice_unfinished);
        FloatingActionButton createTask = findViewById(R.id.memo_choice_create);
        FloatingActionButton getHelp = findViewById(R.id.memo_choice_help);
        showFinishedTask.setOnClickListener(this);
        showUnfinishedTask.setOnClickListener(this);
        createTask.setOnClickListener(this);
        getHelp.setOnClickListener(this);


        mCardShadowTransformer = new ShadowTransformer(mViewPager, mCardAdapter);
//        mCardShadowTransformer.enableScaling(true);
        mViewPager.setPageTransformer(false, mCardShadowTransformer);
        mViewPager.setOffscreenPageLimit(1);


        initMemo();
//        recyclerView = findViewById(R.id.month_record_recycler_view);
//
//
//        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
//        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
//        recordAdapter = new RecordAdapter(getApplicationContext(), this, mRecordList);
//        recyclerView.setAdapter(recordAdapter);
    }

//    private void testData() {
//        getUserMemoByState(false, false);
//    }


    private void initMemo() {
        getUserMemoByState(false, false);
    }

    private void displayBackground() {
//        new Thread(() -> {
//            Bitmap bitmap = ImageTools.compressImage(BitmapFactory.decodeResource(getResources(), R.drawable.mh6));
//            runOnUiThread(() -> {
//                Dali.create(MemoActivity.this).load(bitmap).blurRadius(1).into(bgView);
//            });
//        }).start();
//        Glide.with(MemoActivity.this).load(R.drawable.mh6).into(bgView);
    }


    @Override
    public void onClick(View v) {
        menu.collapseImmediately();
        switch (v.getId()) {
            case R.id.memo_choice_finished:
                getUserMemoByState(true, false);
                break;
            case R.id.memo_choice_unfinished:
                getUserMemoByState(false, false);
                break;
            case R.id.memo_choice_create:
                showNewMemoDialog();
                break;
            case R.id.memo_choice_help:
                MaterialDialogUtils.showSimpleConfirmDialog(this, new String[]{"备忘录帮助", Constant.MEMO_HELP_TEXT, "确认", ""});
                break;
            default:
                break;
        }
    }

    private void showNewMemoDialog() {
        View view = null;
        try {
            LayoutInflater inflater = LayoutInflater.from(this);
            view = inflater.inflate(R.layout.new_memo, null);
        } catch (InflateException e) {
            e.printStackTrace();
        }
        View finalView = view;
        int[] type = {0};
        RadioGroup sexGroup = finalView.findViewById(R.id.new_memo_type);
        sexGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton r = finalView.findViewById(checkedId);
            ToastUtils.showToast(MemoActivity.this, r.getText().toString(), Toast.LENGTH_SHORT);
            switch (checkedId) {
                case R.id.new_memo_type_1:
                    ToastUtils.showToast(MemoActivity.this, "紧急事件", Toast.LENGTH_SHORT);
                    type[0] = 1;
                    break;
                case R.id.new_memo_type_0:
                    ToastUtils.showToast(MemoActivity.this, "一般事件", Toast.LENGTH_SHORT);
                    type[0] = 1;
                    break;
                default:
                    break;
            }
        });
        MaterialDialogUtils.showYesOrNoDialogWithCustomView(this, new String[]{"创建新备忘录", "", "创建", "取消"}, view, new MaterialDialogUtils.DialogOnConfirmClickListener() {
            @Override
            public void onConfirmButtonClick() {
                EditText e0 = finalView.findViewById(R.id.new_memo_title);
                EditText e1 = finalView.findViewById(R.id.new_memo_content);
                String t = e0.getText().toString();
                String c = e1.getText().toString();
                String checkRes = checkIsIllegalMemo(t, c);
                if ("1".equals(checkRes)) {
                    HttpUtil.newUserMemo(darkme_un, t, c, type[0], new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            ToastUtils.showToastOnMain(MemoActivity.this, MemoActivity.this, "服务错误", Toast.LENGTH_SHORT);
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {

                            String resp = response.body().string();
                            if (!TextUtils.isEmpty(resp)) {
                                if ("1".equals(resp)) {
                                    ToastUtils.showToastOnMain(MemoActivity.this, MemoActivity.this, "添加待办事项成功", Toast.LENGTH_LONG);
                                    getUserMemoByState(false, true);
                                } else {
                                    ToastUtils.showToastOnMain(MemoActivity.this, MemoActivity.this, "添加错误，请稍后重试", Toast.LENGTH_SHORT);
                                }
                            }
                        }
                    });

                } else {
                    ToastUtils.showToast(MemoActivity.this, checkRes, Toast.LENGTH_LONG);
                }
            }
        }, true);
    }

    private String checkIsIllegalMemo(String title, String content) {
        if (TextUtils.isEmpty(content)) {
            return "内容请不要留空";
        }
        if (title.length() > 20) {
            return "标题长度不得超出20个字符";
        }
        if (content.length() > 100) {
            return "内容长度不得超出100个字符";
        }
        return "1";
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mCardShadowTransformer.enableScaling(isChecked);
    }

    public void getUserMemoByState(boolean isFinished, boolean onSubThread) {
        if (onSubThread) {
            runOnUiThread(() -> {
                loadBar.setVisibility(View.VISIBLE);
                changeTitle(isFinished);
            });
        } else {
            loadBar.setVisibility(View.VISIBLE);
            changeTitle(isFinished);
        }
        HttpUtil.getUserMemoByState(darkme_un, isFinished, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ToastUtils.showToastOnMain(MemoActivity.this, MemoActivity.this, "服务错误", Toast.LENGTH_SHORT);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String resp = response.body().string();
                if (!TextUtils.isEmpty(resp)) {
                    memoList = gson.fromJson(resp, new TypeToken<List<Memo>>() {
                    }.getType());
                    if (null != memoList) {
                        cardItemList.clear();

                        if (memoList.size() > 0) {
                            mCardAdapter = new CardPagerAdapter(MemoActivity.this, MemoActivity.this, memoList.size());
                            for (int i = 0; i < memoList.size(); i++) {
                                Memo m = memoList.get(i);
                                mCardAdapter.addCardItem(new CardItem(m.getId(), m.getMemo_title(), m.getMemo_content(), String.valueOf(m.getMemo_state()), String.valueOf(m.getMemo_type()), TimeUtils.tweetPostTimeConvert(m.getMemo_post_date())));
                            }
                            runOnUiThread(() -> {
                                mViewPager.setAdapter(mCardAdapter);
                                loadBar.setVisibility(View.GONE);
                            });
                        } else {
                            runOnUiThread(() -> {
                                cardItemList.clear();
                                mCardAdapter = new CardPagerAdapter(MemoActivity.this, MemoActivity.this, 0);
                                mViewPager.setAdapter(mCardAdapter);
                                mViewPager.setPageTransformer(false, mCardShadowTransformer);
                                mViewPager.setOffscreenPageLimit(3);
                                loadBar.setVisibility(View.GONE);
                                if (isFinished) {
                                    ToastUtils.showToast(MemoActivity.this, "没有已经完成的任务哦，快去添加一个吧", Toast.LENGTH_SHORT);
                                } else {
                                    ToastUtils.showToast(MemoActivity.this, "任务全部完成了，喵～", Toast.LENGTH_SHORT);
                                }
                            });
                        }
                    }
                } else {
                    ToastUtils.showToastOnMain(MemoActivity.this, MemoActivity.this, "服务异常", Toast.LENGTH_SHORT);
                }
            }
        });
    }


    private void changeTitle(boolean isFinished) {
        if (isFinished) {
            title.setText("已完成的事项");
            title.setTextColor(getResources().getColor(R.color.light_green));
        } else {
            title.setText("待完成的事项");
            title.setTextColor(getResources().getColor(R.color.r1));
        }
    }


//    private void testInsert() {
//        Calendar calendar = Calendar.getInstance();
//        int m = calendar.get(Calendar.DAY_OF_MONTH);
////        Calendar c = Calendar.getInstance();//
////        mYear = c.get(Calendar.YEAR); // 获取当前年份
////        mMonth = c.get(Calendar.MONTH) + 1;// 获取当前月份
////        mDay = c.get(Calendar.DAY_OF_MONTH);// 获取当日期
//        for (int i = m ; i >=1; i-- ){
//            mRecordList.add(i);
//        }
//    }


}

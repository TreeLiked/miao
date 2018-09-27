package com.example.lqs2.courseapp.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.lqs2.courseapp.R;
import com.example.lqs2.courseapp.adapters.ImageAdapter;
import com.example.lqs2.courseapp.entity.Tweet;
import com.example.lqs2.courseapp.utils.Base64ImageUtils;
import com.example.lqs2.courseapp.utils.Constant;
import com.example.lqs2.courseapp.utils.TimeUtils;

import java.util.Objects;

public class TweetDetailActivity extends AppCompatActivity {

    private EditText comment_detail_edit;

    private CollapsingToolbarLayout layout;
    private ImageView imageView;
    private TextView contentView;
    private TextView postTimeView;
    private RecyclerView recyclerView;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);

        layout = findViewById(R.id.tweet_detail_cover_layout);
        imageView = findViewById(R.id.tweet_detail_cover);
        contentView = findViewById(R.id.tweet_detail_content);
        postTimeView = findViewById(R.id.tweet_detail_postTime);


        ImageAdapter adapter = new ImageAdapter(this, TweetDetailActivity.this);
        recyclerView = findViewById(R.id.tweet_detail_img_view);
//        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.HORIZONTAL));
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        Intent intent = getIntent();
        Tweet tweet = (Tweet) Objects.requireNonNull(intent.getExtras()).getSerializable("tweet");

        assert tweet != null;
        layout.setTitle(tweet.getUserId());
        if (null != tweet.getContent()) {
            contentView.setText(tweet.getContent());
        }
        postTimeView.setText(TimeUtils.tweetPostTimeConvert(tweet.getPostTime()));
        if (Base64ImageUtils.isPicPath(tweet.getImgPath0())) {
            Glide.with(TweetDetailActivity.this).load(Constant.img_access_url + tweet.getImgPath0()).into(imageView);
            adapter.setDataC(ImageAdapter.getImagePathList(tweet));
        } else {
            Glide.with(this).load(R.drawable.girl).into(imageView);
        }


//        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
//        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
//
//        TextView news_detail_detail_view = findViewById(R.id.news_detail_detail);
//        TextView news_detail_from_name_view = findViewById(R.id.news_detail_from_name);
//        TextView news_detail_from_id_view = findViewById(R.id.news_detail_from_id);
//        TextView news_detail_date_view = findViewById(R.id.news_detail_date);
//
//        setSupportActionBar(toolbar);
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar!= null) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
//        }
//        collapsingToolbarLayout.setTitle(news_title);
//        news_detail_detail_view.setText(news_detail);
//        news_detail_from_name_view.setText("来自: "+news_from_name);
//        news_detail_from_id_view.setText("ID: "+news_from_id);
//        news_detail_date_view.setText(news_date);
//
//
//        FloatingActionButton comment_btn = findViewById(R.id.news_detail_comment);
//        comment_btn.setOnClickListener(v -> {
//            //弹窗隐藏时回调方法
//            //View控件点击事件回调
//            new TDialog.Builder(getSupportFragmentManager())
//                    .setLayoutRes(R.layout.t_dialog_evaluate)    //设置弹窗展示的xml布局
////                .setDialogView(view)  //设置弹窗布局,直接传入View
//                    .setWidth(600)  //设置弹窗宽度(px)
//                    .setHeight(getResources().getDisplayMetrics().widthPixels)  //设置弹窗高度(px)
//                    .setScreenWidthAspect(TweetDetailActivity.this, 0.8f)   //设置弹窗宽度(参数aspect为屏幕宽度比例 0 - 1f)
//                    .setScreenHeightAspect(TweetDetailActivity.this, 0.3f)  //设置弹窗高度(参数aspect为屏幕宽度比例 0 - 1f)
//                    .setGravity(Gravity.BOTTOM)     //设置弹窗展示位置
//                    .setTag("快来评论吧")   //设置Tag
//                    .setDimAmount(0.6f)     //设置弹窗背景透明度(0-1f)
//                    .setCancelableOutside(true)     //弹窗在界面外是否可以点击取消
//                    .setCancelable(true)    //弹窗是否可以取消
//                    .setOnDismissListener(dialog -> Toast.makeText(TweetDetailActivity.this, "弹窗消失回调", Toast.LENGTH_SHORT).show())
////                        .setOnBindViewListener(new OnBindViewListener() {   //通过BindViewHolder拿到控件对象,进行修改
////                            @Override
////                            public void bindView(BindViewHolder bindViewHolder) {
////                                bindViewHolder.setText(R.id.tv_content, "abcdef");
////                                bindViewHolder.setText(R.id.tv_title, "我是Title");
////                            }
////                        })
//                        .addOnClickListener(R.id.btn_evluate)   //添加进行点击控件的id
//                        .setOnViewClickListener((viewHolder, view, tDialog) -> {
//                            switch (view.getId()) {
//                                case R.id.btn_evluate:
//                                    comment_detail_edit = findViewById(R.id.comment_detail);
//                                    String comment = comment_detail_edit.getText().toString();
//                                    Toast.makeText(TweetDetailActivity.this, comment, Toast.LENGTH_SHORT).show();
//                                    tDialog.dismiss();
//
//                                    break;
//                                default:
//                                    break;
////                                    case R.id.btn_left:
////                                        Toast.makeText(DiffentDialogActivity.this, "left clicked", Toast.LENGTH_SHORT).show();
////                                        break;
////                                    case R.id.btn_right:
////                                        Toast.makeText(DiffentDialogActivity.this, "right clicked", Toast.LENGTH_SHORT).show();
////                                        tDialog.dismiss();
////                                        break;
////                                    case R.id.tv_title:
////                                        Toast.makeText(DiffentDialogActivity.this, "title clicked", Toast.LENGTH_SHORT).show();
////                                        break;
//                            }
//                        })
//                    .create()   //创建TDialog
//                    .show();    //展示
//        });


    }



}

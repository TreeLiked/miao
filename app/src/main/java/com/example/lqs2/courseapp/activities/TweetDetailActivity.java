package com.example.lqs2.courseapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lqs2.courseapp.R;
import com.example.lqs2.courseapp.adapters.ImageAdapter;
import com.example.lqs2.courseapp.entity.Notice;
import com.example.lqs2.courseapp.entity.Tweet;
import com.example.lqs2.courseapp.global.GlideApp;
import com.example.lqs2.courseapp.utils.Base64ImageUtils;
import com.example.lqs2.courseapp.utils.Constant;
import com.example.lqs2.courseapp.utils.StatusBarUtils;
import com.example.lqs2.courseapp.utils.TimeUtils;

import java.util.Objects;

/**
 * 动态的详情页面
 *
 * @author lqs2
 */
public class TweetDetailActivity extends ActivityCollector {
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);
        StatusBarUtils.setStatusTransparent(this);

        CollapsingToolbarLayout layout = findViewById(R.id.tweet_detail_cover_layout);
        ImageView imageView = findViewById(R.id.tweet_detail_cover);
        TextView contentView = findViewById(R.id.tweet_detail_content);
        TextView postTimeView = findViewById(R.id.tweet_detail_postTime);
        Intent intent = getIntent();
//        0 来自动态，1来自通知
        int type = intent.getIntExtra("FROM", 1);
        switch (type) {
            case 0:
                RecyclerView recyclerView = findViewById(R.id.tweet_detail_img_view);
                findViewById(R.id.notice_annex_layout).setVisibility(View.GONE);
                findViewById(R.id.notice_link_layout).setVisibility(View.GONE);
                ImageAdapter adapter = new ImageAdapter(this, TweetDetailActivity.this);
                GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(adapter);
                Tweet tweet = (Tweet) Objects.requireNonNull(intent.getExtras()).getSerializable("tweet");
                assert tweet != null;
                layout.setTitle(tweet.getUserId());
                if (null != tweet.getContent()) {
                    contentView.setText(tweet.getContent());
                }
                postTimeView.setText(TimeUtils.tweetPostTimeConvert(tweet.getPostTime()));
                if (Base64ImageUtils.isPicPath(tweet.getImgPath0())) {
                    GlideApp.with(TweetDetailActivity.this).load(Constant.IMG_ACCESS_URL + tweet.getImgPath0()).into(imageView);
                    adapter.setDataC(ImageAdapter.getImagePathList(tweet));
                } else {
                    GlideApp.with(this).load(R.drawable.tweet_no_img).into(imageView);
                }
                break;
            case 1:
                findViewById(R.id.tweet_detail_img_view).setVisibility(View.GONE);
                findViewById(R.id.news_detail_comment).setVisibility(View.GONE);
                GlideApp.with(this).load(Constant.CARD_COVER_URL).into(imageView);
                Notice notice = (Notice) Objects.requireNonNull(intent.getExtras()).getSerializable("notice");
                assert notice != null;
                layout.setTitle(notice.getTitle());
                layout.setExpandedTitleColor(ContextCompat.getColor(this, R.color.white));
                layout.setCollapsedTitleTextColor(ContextCompat.getColor(this, R.color.white));
                contentView.setText(notice.getContent());
                TextView linkView = findViewById(R.id.link_text_view);
                linkView.setOnClickListener(v -> {
                    Uri uri = Uri.parse(notice.getContentUrl());
                    Intent intent1 = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent1);
                });
                imageView.setOnClickListener(v -> {
                    Intent intent2 = new Intent(this, ImageBrowseActivity.class);
                    intent2.putExtra("type", 1);
                    intent2.putExtra("url", Constant.CARD_COVER_URL);
                    startActivity(intent2);
                });
                postTimeView.setText(notice.getTime());
                if (!TextUtils.isEmpty(notice.getAnnexUrl())) {
                    TextView annexView = findViewById(R.id.annex_text_view);
                    annexView.setText(notice.getAnnexText());
                    annexView.setOnClickListener(v -> {
                        Uri uri = Uri.parse(notice.getAnnexUrl());
                        Intent intent1 = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent1);
                    });
//                    annexView.setMovementMethod(LinkMovementMethod.getInstance());
//                    CharSequence c2 = Html.fromHtml("<a href=\"" + notice.getAnnexUrl() + "\">" + notice.getAnnexText() + "</a>");
//                    annexView.setText(c2);
//                    annexView.setTextColor(ContextCompat.getColor(this, R.color.fri_title_bg));
                }
                break;
            default:
                break;
        }
    }


}

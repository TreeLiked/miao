package com.example.lqs2.courseapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.lqs2.courseapp.R;
import com.example.lqs2.courseapp.activities.MainActivity;
import com.example.lqs2.courseapp.activities.TweetDetailActivity;
import com.example.lqs2.courseapp.entity.Tweet;
import com.example.lqs2.courseapp.entity.UserAndTweet;
import com.example.lqs2.courseapp.utils.Base64ImageUtils;
import com.example.lqs2.courseapp.utils.HttpUtil;
import com.example.lqs2.courseapp.utils.MaterialDialogUtils;
import com.example.lqs2.courseapp.utils.SharedPreferenceUtil;
import com.example.lqs2.courseapp.utils.TimeUtils;
import com.example.lqs2.courseapp.utils.ToastUtils;
import com.example.lqs2.courseapp.utils.UsualSharedPreferenceUtil;
import com.google.gson.Gson;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {


    private Context mContext;
    private List<Tweet> tweetList;
    private MainActivity activity;
    private String un;
    private Gson gson;
    private boolean darkMode;


    public void setData(List<Tweet> tweetList) {
        this.tweetList = tweetList;
        notifyDataSetChanged();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        LinearLayout bgLayout;
        CircleImageView circleImageView;
        TextView userIdView;
        TextView tweetContentView;
        TextView tweetPostTimeView;
        RecyclerView recyclerView;
        com.like.LikeButton praiseBtn;
        com.like.LikeButton collectBtn;
        TextView goodView;

//        ImageView commentView;

        ViewHolder(View view) {
            super(view);
            cardView = view.findViewById(R.id.tweet_item_layout);

            bgLayout = view.findViewById(R.id.tweet_item_layout_for_bg);
            circleImageView = view.findViewById(R.id.user_profile_pic);
            userIdView = view.findViewById(R.id.user_userId);
            tweetContentView = view.findViewById(R.id.tweet_content);
            tweetPostTimeView = view.findViewById(R.id.tweet_post_time);
            recyclerView = view.findViewById(R.id.tweet_imgs_recycler_view);
            praiseBtn = view.findViewById(R.id.tweet_praise_btn);
            collectBtn = view.findViewById(R.id.tweet_collect_btn);
            goodView = view.findViewById(R.id.tweet_good_view);

//            commentView = view.findViewById(R.id.tweet_comment);
        }
    }

    public TweetAdapter(Context context, MainActivity activity) {
        this.mContext = context;
        this.activity = activity;
        gson = new Gson();
        darkMode = UsualSharedPreferenceUtil.isDarkModeOn(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.tweet_item, parent, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tweet tweet = tweetList.get(position);
        String picStr = tweet.getUserProfilePicStr();
        if (!TextUtils.isEmpty(picStr)) {
            new Thread(() -> {
                Bitmap b = Base64ImageUtils.base64StrToBitmap(picStr);
                ((MainActivity) mContext).runOnUiThread(() -> Glide.with(mContext).load(b).thumbnail(0.2f).into(holder.circleImageView));
            }).start();
        } else {
            holder.circleImageView.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.default_head));
        }
        if (darkMode) {
            displayDarkMode(holder);
        }
        holder.userIdView.setText(tweet.getUserId());
        String postTime = TimeUtils.tweetPostTimeConvert(tweet.getPostTime());
        holder.tweetPostTimeView.setText(postTime);
        holder.tweetContentView.setText(tweet.getContent());
        holder.recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        holder.recyclerView.setItemAnimator(new DefaultItemAnimator());


        holder.goodView.setText(String.valueOf(tweet.getGood()));
        ImageAdapter adapter = new ImageAdapter(mContext, activity);
        adapter.setDataB(ImageAdapter.getImagePathList(tweet));

        holder.recyclerView.setAdapter(adapter);

        holder.cardView.setOnClickListener(v -> showTweetDetail(tweet));
        holder.tweetContentView.setOnClickListener(v1 -> showTweetDetail(tweet));
//        holder.recyclerView.setOnClickListener(v2 -> showTweetDetail(tweet));

        bindLongClickToDelete(holder, tweet, position);
        boolean hasLogin = activity.checkHasLoginDarkme();
        if (hasLogin) {
            getUserAccount();
        }

        bindUserAndTweet(holder, tweet.getId(), hasLogin);
        bindPraiseButton(holder.praiseBtn, holder.goodView, tweet.getId(), hasLogin);
        bindCollectButton(holder.collectBtn, tweet.getId(), hasLogin);


    }

    private void bindLongClickToDelete(ViewHolder holder, Tweet tweet, int position) {
        if (tweet.getUserId().equals(un)) {
            holder.cardView.setOnLongClickListener(v -> {
                MaterialDialogUtils.showYesOrNoDialogWithBothSthTodo(mContext, new String[]{"删除此条动态吗", "此操作不可恢复", "取消", "确认"}, new MaterialDialogUtils.DialogOnCancelClickListener() {
                    @Override
                    public void onCancelButtonClick() {
                        HttpUtil.deleteTweet(tweet.getId(), new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                ToastUtils.showToastOnMain(mContext, activity, "连接错误", Toast.LENGTH_SHORT);
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String resp = response.body().string();
                                if ("1".equals(resp)) {
                                    activity.runOnUiThread(() -> {
                                        deleteItem(position);
                                        ToastUtils.showToast(mContext, "删除成功", Toast.LENGTH_SHORT);
                                    });
                                } else {
                                    ToastUtils.showToastOnMain(mContext, activity, "Something went wrong...", Toast.LENGTH_SHORT);
                                }
                            }
                        });
                    }
                }, true);
                return false;
            });
        }
    }

    public void deleteItem(int position) {
        tweetList.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    public void showTweetDetail(Tweet tweet) {
        Intent intent = new Intent(activity, TweetDetailActivity.class);
        Bundle bundle = new Bundle();
        intent.putExtra("FROM", 0);
        bundle.putSerializable("tweet", tweet);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    private void bindUserAndTweet(ViewHolder holder, String id, boolean hasLogin) {
        if (hasLogin) {
            HttpUtil.showUserTweetInfo(un, id, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    String resp = response.body().string();
                    if (!"-1".equals(resp) && !"0".equals(resp)) {
                        UserAndTweet tweet = gson.fromJson(resp, UserAndTweet.class);
                        if (tweet.isPraise()) {
                            activity.runOnUiThread(() -> holder.praiseBtn.setLiked(true));
                        } else {
                            activity.runOnUiThread(() -> holder.praiseBtn.setLiked(false));
                        }
                        if (tweet.isCollect()) {
                            activity.runOnUiThread(() -> holder.collectBtn.setLiked(true));
                        } else {
                            activity.runOnUiThread(() -> holder.collectBtn.setLiked(false));
                        }
                    }

                }
            });
        }
    }

    private void bindCollectButton(LikeButton button, String id, boolean hasLogin) {
        if (hasLogin) {
            if (!TextUtils.isEmpty(un)) {
                button.setOnLikeListener(new OnLikeListener() {
                    @Override
                    public void liked(LikeButton likeButton) {
                        userCollectTweet(id, true);
                    }

                    @Override
                    public void unLiked(LikeButton likeButton) {
                        userCollectTweet(id, false);
                    }
                });
            }
        } else {
            button.setLiked(false);
            button.setEnabled(true);
            button.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    button.setLiked(false);
                    activity.showNoLoginDarkmeInfo(false, "，无法收藏");
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    button.setLiked(false);
                    activity.showNoLoginDarkmeInfo(false, "，无法收藏");
                }
            });
        }
    }


    private void bindPraiseButton(LikeButton button, TextView view, String id, boolean hasLogin) {
        if (hasLogin) {
            button.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    int good = Integer.parseInt(view.getText().toString());
                    good++;
                    view.setText(String.valueOf(good));
                    modifyPraiseCount(id, true);
                    userPraiseTweet(id, true);
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    int good = Integer.parseInt(view.getText().toString());
                    good--;
                    view.setText(String.valueOf(good));
                    modifyPraiseCount(id, false);
                    userPraiseTweet(id, false);
                }
            });
        } else {
            button.setLiked(false);
            button.setEnabled(true);
            button.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    button.setLiked(false);
                    activity.showNoLoginDarkmeInfo(false, "");
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    button.setLiked(false);
                    activity.showNoLoginDarkmeInfo(false, "");
                }
            });
        }
    }

    private void modifyPraiseCount(String id, boolean add) {
        new Thread(() -> HttpUtil.modifyTweetGood(id, add)).start();
    }


    private void userPraiseTweet(String tweetId, boolean praise) {
        new Thread(() -> HttpUtil.userPraiseTweet(un, tweetId, praise)).start();
    }

    private void userCollectTweet(String tweetId, boolean collect) {
        new Thread(() -> {
            try {
                Response resp = HttpUtil.userCollectTweet(un, tweetId, collect);
                String r = resp.body().string();
                if ("1".equals(r)) {

                    activity.showToastOnMainThread(collect ? "收藏成功" : "取消收藏成功", Toast.LENGTH_SHORT);
                } else {
                    activity.showToastOnMainThread(collect ? "收藏失败" : "取消收藏失败", Toast.LENGTH_SHORT);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }


    private void getUserAccount() {
        un = (String) SharedPreferenceUtil.get(mContext, "darkme_un", "");
    }

    @Override
    public int getItemCount() {
        if (null != tweetList) {
            return tweetList.size();
        } else {
            return -1;
        }
    }


    private void displayDarkMode(ViewHolder holder) {
        holder.tweetContentView.setTextColor(mContext.getResources().getColor(R.color.white));
        holder.goodView.setTextColor(mContext.getResources().getColor(R.color.white));
        holder.userIdView.setTextColor(mContext.getResources().getColor(R.color.white));
        holder.tweetPostTimeView.setTextColor(mContext.getResources().getColor(R.color.white));
        holder.bgLayout.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
    }


}

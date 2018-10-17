package com.example.lqs2.courseapp.adapters;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.lqs2.courseapp.R;
import com.example.lqs2.courseapp.activities.ImageBrowseActivity;
import com.example.lqs2.courseapp.activities.MainActivity;
import com.example.lqs2.courseapp.activities.NewTweetActivity;
import com.example.lqs2.courseapp.activities.TweetDetailActivity;
import com.example.lqs2.courseapp.entity.Tweet;
import com.example.lqs2.courseapp.utils.Base64ImageUtils;
import com.example.lqs2.courseapp.utils.Constant;
import com.example.lqs2.courseapp.utils.MaterialDialogUtils;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {


    private int type;
    /**
     * 实例方式1，本地选择图片用
     */
    private List<LocalMedia> mMedias;
    /**
     * 实例方式2，加载Drawable下的图片
     */
    private List<Drawable> drawableList;
    /**
     * 实例方式3，加载Base64
     */
    private List<String> pathList = new ArrayList<>();

    private List<String> stringListTweetDetail = new ArrayList<>();

    private int singleImgHeight;
    private int singleImgWidth;
    private Context mContext;
    private NewTweetActivity newTweetActivity;
    private MainActivity mainActivity;
    private TweetDetailActivity tweetDetailActivity;

    private RequestOptions options = new RequestOptions();

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        RelativeLayout layout;

        ImageViewHolder(View contentView) {
            super(contentView);
            layout = contentView.findViewById(R.id.image_item_layout);
            img = contentView.findViewById(R.id.img_thumbnails);
        }
    }

    public ImageAdapter(Context mContext, NewTweetActivity activity) {
        this.mContext = mContext;
        this.type = Constant.ADAPTER_FOR_NEW_TWEET_ACTIVITY;
        this.newTweetActivity = activity;
        initImageSize();

    }

    public ImageAdapter(Context context, MainActivity activity) {
        this.mContext = context;
        this.mainActivity = activity;
        this.type = Constant.ADAPTER_FOR_MAIN_ACTIVITY;
        initImageSize();
        initGlideOptions();
    }

    public ImageAdapter(Context context, TweetDetailActivity activity) {
        this.mContext = context;
        this.tweetDetailActivity = activity;
        this.type = Constant.ADAPTER_FOR_TWEET_DETAIL_ACTIVITY;
        initImageSize();
    }

    private void initGlideOptions() {
        options.placeholder(R.drawable.glide_placeholder_img);
        options.error(R.drawable.glide_error_img);
    }

    /**
     * 获取动态图片list
     *
     * @param tweet 动态
     * @return List<String>
     */
    public static List<String> getImagePathList(Tweet tweet) {
        List<String> imgPathList = new ArrayList();
        String imgPath = tweet.getImgPath0();
        if (Base64ImageUtils.isPicPath(imgPath)) {
            imgPathList.add(imgPath);
        }
        imgPath = tweet.getImgPath1();
        if (Base64ImageUtils.isPicPath(imgPath)) {
            imgPathList.add(imgPath);
        }
        imgPath = tweet.getImgPath2();
        if (Base64ImageUtils.isPicPath(imgPath)) {
            imgPathList.add(imgPath);
        }
        imgPath = tweet.getImgPath3();
        if (Base64ImageUtils.isPicPath(imgPath)) {
            imgPathList.add(imgPath);
        }
        imgPath = tweet.getImgPath4();
        if (Base64ImageUtils.isPicPath(imgPath)) {
            imgPathList.add(imgPath);
        }
        imgPath = tweet.getImgPath5();
        if (Base64ImageUtils.isPicPath(imgPath)) {
            imgPathList.add(imgPath);
        }
        imgPath = tweet.getImgPath6();
        if (Base64ImageUtils.isPicPath(imgPath)) {
            imgPathList.add(imgPath);
        }
        imgPath = tweet.getImgPath7();
        if (Base64ImageUtils.isPicPath(imgPath)) {
            imgPathList.add(imgPath);
        }
        imgPath = tweet.getImgPath8();
        if (Base64ImageUtils.isPicPath(imgPath)) {
            imgPathList.add(imgPath);
        }
        return imgPathList;
    }

    /**
     * 初始化文件的宽度
     */
    private void initImageSize() {
        int screenWidth = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
        switch (this.type) {
            case Constant.ADAPTER_FOR_MAIN_ACTIVITY:
                singleImgHeight = (screenWidth - 26) / 3;

                break;
            case Constant.ADAPTER_FOR_NEW_TWEET_ACTIVITY:
                singleImgHeight = (screenWidth - 60) / 3;
                break;
            case Constant.ADAPTER_FOR_TWEET_DETAIL_ACTIVITY:
                singleImgHeight = (screenWidth - 20) / 3;
                break;
            case Constant.ADAPTER_TO_DRAWABLE:
                singleImgHeight = (screenWidth - 60) / 3;
                break;
            default:
                singleImgHeight = (screenWidth - 60) / 3;
                break;
        }
        singleImgWidth = singleImgHeight;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void setDataB(List<String> pathList) {
        this.pathList.clear();
        for (int i = 0; i < pathList.size(); i++) {
            String path = pathList.get(i);
            if (Base64ImageUtils.isPicPath(path)) {
                this.pathList.add(path);
            }
        }
        notifyDataSetChanged();
    }

    /**
     * 设置数据的不同方式，以下不再注释
     *
     * @param mMedias
     */
    public void setDataA(List<LocalMedia> mMedias) {
        this.mMedias = mMedias;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ImageViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, parent, false));
    }

    public void setDataC(List<String> stringList) {
        this.stringListTweetDetail.clear();
        for (int i = 0; i < stringList.size(); i++) {
            String str = stringList.get(i);
            if (Base64ImageUtils.isPicPath(str)) {
                this.stringListTweetDetail.add(str);
            }
        }
        notifyDataSetChanged();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        ViewGroup.LayoutParams para;
        para = holder.img.getLayoutParams();
        para.height = singleImgHeight;
        para.width = singleImgWidth;

        switch (type) {
            case Constant.ADAPTER_FOR_NEW_TWEET_ACTIVITY:
                String path = mMedias.get(position).getPath();
                Glide.with(mContext).load(BitmapFactory.decodeFile(path)).thumbnail(0.05f).into(holder.img);
                clickToDoThing(holder, path, position);
                break;
            case Constant.ADAPTER_FOR_MAIN_ACTIVITY:
                String imgPath = pathList.get(position);
                String fullPath = Constant.IMG_ACCESS_URL + imgPath;
                holder.img.setOnClickListener(v -> {
                    Intent intent = new Intent(mainActivity, ImageBrowseActivity.class);
                    intent.putExtra("type", 1);
                    intent.putExtra("url", fullPath);
                    mContext.startActivity(intent);

                });
                Glide.with(mContext)
                        .load(fullPath)
                        .thumbnail(0.2f)
                        .apply(options)
                        .into(holder.img);
                break;
            case Constant.ADAPTER_FOR_TWEET_DETAIL_ACTIVITY:
                String absoluteImgPath = Constant.IMG_ACCESS_URL + stringListTweetDetail.get(position);
                Glide.with(mContext).load(absoluteImgPath).thumbnail(0.2f).into(holder.img);
                holder.img.setOnClickListener(v -> {
                    Intent intent = new Intent(tweetDetailActivity, ImageBrowseActivity.class);
                    intent.putExtra("type", 1);
                    intent.putExtra("url", absoluteImgPath);
                    mContext.startActivity(intent);
                });
                break;
            case Constant.ADAPTER_TO_DRAWABLE:
                break;
            default:
                break;
        }
        holder.img.setLayoutParams(para);
    }

    @Override
    public int getItemCount() {
        switch (type) {
            case Constant.ADAPTER_FOR_NEW_TWEET_ACTIVITY:
                return mMedias != null ? mMedias.size() : -1;
            case Constant.ADAPTER_FOR_MAIN_ACTIVITY:
                return pathList != null ? pathList.size() : -1;
            case Constant.ADAPTER_FOR_TWEET_DETAIL_ACTIVITY:
                return stringListTweetDetail != null ? stringListTweetDetail.size() : -1;
            case Constant.ADAPTER_TO_DRAWABLE:
                return drawableList != null ? drawableList.size() : -1;
            default:
                break;
        }
        return -1;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void clickToDoThing(ImageViewHolder holder, String path, int position) {
        switch (type) {
            case Constant.ADAPTER_FOR_NEW_TWEET_ACTIVITY:
                holder.img.setOnClickListener(v -> MaterialDialogUtils.showYesOrNoDialog(mContext, new String[]{"删除选项", "确认删除这张图片", "确认", "取消"}, new MaterialDialogUtils.AbstractDialogOnConfirmClickListener() {
                    @Override
                    public void onConfirmButtonClick() {
                        newTweetActivity.deleteSelectedImage(path, position);
                    }
                }, true));
                break;
            case Constant.ADAPTER_TO_DRAWABLE:
                break;
            case Constant.ADAPTER_FOR_MAIN_ACTIVITY:
                break;
            case Constant.ADAPTER_FOR_TWEET_DETAIL_ACTIVITY:
                break;
            default:
                break;
        }
    }
}
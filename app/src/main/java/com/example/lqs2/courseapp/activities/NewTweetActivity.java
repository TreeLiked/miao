package com.example.lqs2.courseapp.activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lqs2.courseapp.R;
import com.example.lqs2.courseapp.adapters.ImageAdapter;
import com.example.lqs2.courseapp.global.ThreadPoolExecutorFactory;
import com.example.lqs2.courseapp.utils.Constant;
import com.example.lqs2.courseapp.utils.HttpUtil;
import com.example.lqs2.courseapp.utils.StatusBarUtils;
import com.example.lqs2.courseapp.utils.ToastUtils;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.yalantis.ucrop.UCrop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 动态activity
 *
 * @author lqs2
 */
public class NewTweetActivity extends ActivityCollector implements View.OnClickListener {

    private List<LocalMedia> mediaList = new ArrayList<>();
    private EditText content;
    private Button releaseBtn;
    private ImageAdapter imageAdapter;
    private String darkmeUn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_tweet);

        StatusBarUtils.setStatusBarTransparentAndTextColorBlack(this);

        Intent intent = getIntent();
        darkmeUn = intent.getStringExtra("darkme_un");


        LinearLayout picChoose = findViewById(R.id.choose_pic);
        content = findViewById(R.id.tweets_add);
        releaseBtn = findViewById(R.id.release_tweets);
        TextView cancelView = findViewById(R.id.cancel_release_tweet);


        RecyclerView recyclerView = findViewById(R.id.img_thumbnails_recycler_view);
        imageAdapter = new ImageAdapter(this, this);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(imageAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        content.setOnFocusChangeListener((v, hasFocus) -> {
            InputMethodManager imm = (InputMethodManager) content.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (hasFocus) {
                assert imm != null;
                imm.toggleSoftInput(0,
                        InputMethodManager.HIDE_NOT_ALWAYS);
            } else {
                assert imm != null;
                imm.hideSoftInputFromWindow(
                        content.getWindowToken(), 0);
            }
        });
        content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void afterTextChanged(Editable s) {
                releaseOrNot();
            }
        });

        releaseBtn.setOnClickListener(this);
        picChoose.setOnClickListener(this);
        cancelView.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.choose_pic:
                choosePic();
                break;
            case R.id.release_tweets:
                releaseTweet(mediaList);
                break;
            case R.id.cancel_release_tweet:
                abortRelease();
                break;
            default:
                break;
        }
    }

    /**
     * 发布动态
     *
     * @param mediaList 发布动态
     */
    private void releaseTweet(List<LocalMedia> mediaList) {

        if (!TextUtils.isEmpty(darkmeUn)) {
            ToastUtils.showToast(this, "正在发布，请稍候", Toast.LENGTH_SHORT);
            String text = content.getText().toString();
            Intent intent = new Intent(NewTweetActivity.this, MainActivity.class);
            startActivity(intent);

            ThreadPoolExecutorFactory.getThreadPoolExecutor().execute(() -> HttpUtil.releaseNewTweet(NewTweetActivity.this, darkmeUn, text, mediaList, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    intent.putExtra("RESULT", "-1");
                    startActivity(intent);
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                    assert response.body() != null;
                    String resp = response.body().string();
                    if (!"0".equals(resp)) {
                        ToastUtils.showToastOnMain(MainActivity.context, MainActivity.activity, "发布成功", Toast.LENGTH_LONG);
                    } else {
                        ToastUtils.showToastOnMain(MainActivity.context, MainActivity.activity, "发布失败", Toast.LENGTH_LONG);
                    }
                }
            }));
        } else {
            Toast.makeText(this, "身份验证失败", Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    /**
     * 取消发布动态
     */
    private void abortRelease() {
        ActivityCollector.removeActivity(this);
    }


    /**
     * 从相册选择图片
     */
    public void choosePic() {
        PictureSelector.create(NewTweetActivity.this)
                //全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .openGallery(PictureMimeType.ofImage())
                //主题样式(不设置为默认样式) 也可参考demo values/styles下 例如：R.style.picture.white.style
                .theme(R.style.picture_QQ_style)
                // 最大图片选择数量 int
                .maxSelectNum(9)
                // 最小选择数量 int
                .minSelectNum(0)
                // 每行显示个数 int
                .imageSpanCount(4)
                .selectionMedia(mediaList)
                // 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                .selectionMode(PictureConfig.MULTIPLE)
                // 是否可预览图片 true or false
                .previewImage(true)
                // 是否显示拍照按钮 true or false
                .isCamera(true)
                // 拍照保存图片格式后缀,默认jpeg
                .imageFormat(PictureMimeType.PNG)
                // 图片列表点击 缩放效果 默认true
                .isZoomAnim(true)
                // 是否裁剪 true or false
                .enableCrop(false)
                // int glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                .glideOverride(160, 160)
                // 是否显示gif图片 true or false
                .isGif(false)
                // 是否开启点击声音 true or false
                .openClickSound(false)
                // 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
                .previewEggs(true)
                // 裁剪压缩质量 默认90 int
                .cropCompressQuality(90)
                .compress(true)
                // 小于100kb的图片不压缩
                .minimumCompressSize(100)
                //同步true或异步false 压缩 默认同步
                .synOrAsy(true)
                //结果回调onActivityResult code
                .forResult(PictureConfig.CHOOSE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == RESULT_OK) {
            switch (requestCode) {
                case Constant.IMAGE_REQUEST_CODE:
                    choosePic();
                    break;
                default:
                    break;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // LocalMedia 返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    // 如果裁剪并压缩了，已取压缩路径为准，因为先裁剪后压缩的
                    mediaList = PictureSelector.obtainMultipleResult(data);
                    if (mediaList.size() > 0) {
                        imageAdapter.setDataA(mediaList);
                        releaseOrNot();
                    }
                    break;
                case UCrop.REQUEST_CROP:
                    break;
                default:
                    break;
            }
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void deleteSelectedImage(String path, int position) {
        for (int i = 0; i < mediaList.size(); i++) {
            if (mediaList.get(i).getPath().equals(path)) {
                mediaList.remove(i);
                imageAdapter.notifyItemRemoved(position);
                //方法不会使position及其之后位置的item重新onBindViewHolder
                if (position != mediaList.size()) {
                    imageAdapter.notifyItemRangeChanged(position, mediaList.size() - position);
                }
                if (mediaList.size() <= 0) {
                    releaseOrNot();
                }
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void releaseOrNot() {
        if (mediaList.size() > 0 || content.getText().toString().length() > 0) {
            releaseBtn.setBackground(getDrawable(R.drawable.button_shape_normal2));
            releaseBtn.setTextColor(Color.BLACK);
        } else {
            releaseBtn.setBackground(getDrawable(R.drawable.button_shape_normal));
            releaseBtn.setTextColor(Color.parseColor("#A6A7A8"));
        }
    }
}

package com.example.lqs2.courseapp.activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class NewTweetActivity extends ActivityCollector implements View.OnClickListener {

    private static final String TAG = "NewTweetActivity";

//    private String mFilepath = Environment.getExternalStorageState() + "AndroidSamples";


    private LinearLayout pic_choose;
    private List<LocalMedia> mediaList = new ArrayList<>();
    private EditText content;
    private Button release_btn;
    private TextView cancel_view;
    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");


    private ImageAdapter imageAdapter;
    private StaggeredGridLayoutManager layoutManager;
    private RecyclerView recyclerView;
    private String darkme_un;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_tweet);

        StatusBarUtils.setStatusBarTransparentAndTextColorBlack(this);

        Intent intent = getIntent();
        darkme_un = intent.getStringExtra("darkme_un");


        pic_choose = findViewById(R.id.choose_pic);
        content = findViewById(R.id.tweets_add);
        release_btn = findViewById(R.id.release_tweets);
        cancel_view = findViewById(R.id.cancel_release_tweet);


        recyclerView = findViewById(R.id.img_thumbnails_recycler_view);
        imageAdapter = new ImageAdapter(this, this);
        layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(imageAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        content.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                InputMethodManager imm = (InputMethodManager) content.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (hasFocus) {
                    imm.toggleSoftInput(0,
                            InputMethodManager.HIDE_NOT_ALWAYS);
                } else {
                    imm.hideSoftInputFromWindow(
                            content.getWindowToken(), 0);
                }
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
//        Button send_news_btn = findViewById(R.id.confirm_send_news);
//        ImageButton add_pic_btn = findViewById(R.id.add_pic_btn);

        release_btn.setOnClickListener(this);
        pic_choose.setOnClickListener(this);
        cancel_view.setOnClickListener(this);

//        send_news_btn.setOnClickListener(this);
//        add_pic_btn.setOnClickListener(this);
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
//            case R.id.confirm_send_news:
//                String title = news_title_text.getText().toString();
//                String brief = news_brief_text.getText().toString();
//                String detail = news_detail_text.getText().toString();
//
//                News news = new News();
//                if ("".equals(title) || title.length() >= 20) {
//                    Toast.makeText(NewTweetActivity.this, "标题不能为空且长度限制在20字符以内", Toast.LENGTH_SHORT).show();
//                    break;
//                } else {
//                    news.setNews_title(title);
//                }
//                if ("".equals(detail)) {
//                    Toast.makeText(NewTweetActivity.this, "正文不能为空", Toast.LENGTH_SHORT).show();
//                    break;
//                } else {
//                    news.setNews_detail(detail);
//                }
//                CheckBox handOnSignature = findViewById(R.id.addSignature);
//                CheckBox handOnID = findViewById(R.id.addID);
//                if (handOnSignature.isChecked()) {
//                    news.setNews_from_name((String) SharedPreferenceUtil.get(NewTweetActivity.this, "signature", "匿名消息"));
//                }
//                if (handOnID.isChecked()) {
//                    news.setNews_from_id("0" + xh);
//                } else {
//                    news.setNews_from_id("1" + xh);
//                }
//                news.setNews_date(sdf.format(new Date()));
//
//                if ("000000".equals(brief)) {
//                    news.setNews_level(0);
//                } else {
//                    news.setNews_level(1);
//                }
//                HttpUtil.addOneNews(new Callback() {
//                    @Override
//                    public void onFailure(Call call, IOException e) {
//                        e.printStackTrace();
//                    }
//
//                    @Override
//                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                        String data = response.body().string();
//                        System.out.println(data);
//                        String showResult;
//                        if (data.substring(0, 1).equals("1")) {
//                            showResult = "发送成功";
//                        } else {
//                            showResult = "发送失败";
//                        }
//                        String finalShowResult = showResult;
//                        runOnUiThread(() -> {
//                            Toast.makeText(NewTweetActivity.this, finalShowResult, Toast.LENGTH_LONG).show();
//                            Intent intent = new Intent(NewTweetActivity.this, MainActivity.class);
//                            startActivity(intent);
//                        });
//                    }
//                }, news);
//                break;

            default:
                break;
        }
    }

    private void releaseTweet(List<LocalMedia> mediaList) {

        if (!TextUtils.isEmpty(darkme_un)) {
            ToastUtils.showToast(this, "正在发布，请稍候", Toast.LENGTH_SHORT);
            String text = content.getText().toString();
            Intent intent = new Intent(NewTweetActivity.this, MainActivity.class);
            startActivity(intent);
            new Thread(() -> HttpUtil.releaseNewTweet(NewTweetActivity.this, darkme_un, text, mediaList, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    intent.putExtra("RESULT", "-1");
                    startActivity(intent);
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String resp = response.body().string();
                    if (!"0".equals(resp)) {
                        ToastUtils.showToastOnMain(MainActivity.context, MainActivity.activity, "发布成功", Toast.LENGTH_LONG);
                    } else {
                        ToastUtils.showToastOnMain(MainActivity.context, MainActivity.activity, "发布失败", Toast.LENGTH_LONG);
                    }
                }
            })).start();
        } else {
            Toast.makeText(this, "身份验证失败", Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    private void abortRelease() {
        finish();
    }


    public void choosePic() {

        PictureSelector.create(NewTweetActivity.this)
                .openGallery(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .theme(R.style.picture_QQ_style)//主题样式(不设置为默认样式) 也可参考demo values/styles下 例如：R.style.picture.white.style
                .maxSelectNum(9)// 最大图片选择数量 int
                .minSelectNum(0)// 最小选择数量 int
                .imageSpanCount(4)// 每行显示个数 int
                .selectionMedia(mediaList)
                .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                .previewImage(true)// 是否可预览图片 true or false
                .isCamera(true)// 是否显示拍照按钮 true or false
                .imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .enableCrop(false)// 是否裁剪 true or false
                .glideOverride(160, 160)// int glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                .isGif(false)// 是否显示gif图片 true or false
                .openClickSound(false)// 是否开启点击声音 true or false
                .previewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
                .cropCompressQuality(90)// 裁剪压缩质量 默认90 int
                .compress(true)
                .minimumCompressSize(100)// 小于100kb的图片不压缩
                .synOrAsy(true)//同步true或异步false 压缩 默认同步
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
//          AddNewsActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);

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
                    //
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    // 如果裁剪并压缩了，已取压缩路径为准，因为是先裁剪后压缩的

                    mediaList = PictureSelector.obtainMultipleResult(data);

                    if (mediaList.size() > 0) {

                        imageAdapter.setDataA(mediaList);
                        releaseOrNot();
                    }
                    break;
                case UCrop.REQUEST_CROP:

//                    Glide.with(this)
//                            .load(UCrop.getOutput(data))
//                            .crossFade()
//                            .into(pic_1_view);
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
            release_btn.setBackground(getDrawable(R.drawable.button_shape_normal2));
            release_btn.setTextColor(Color.BLACK);
        } else {
            release_btn.setBackground(getDrawable(R.drawable.button_shape_normal));
            release_btn.setTextColor(Color.parseColor("#A6A7A8"));
        }
    }
}

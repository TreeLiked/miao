package com.example.lqs2.courseapp.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.lqs2.courseapp.R;
import com.example.lqs2.courseapp.utils.ImageTools;
import com.example.lqs2.courseapp.utils.PermissionUtils;
import com.example.lqs2.courseapp.utils.StatusBarUtils;
import com.example.lqs2.courseapp.utils.ToastUtils;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.github.chrisbanes.photoview.PhotoView;

/**
 * 图片浏览活动，可以复用
 *
 * @author lqs2
 */
public class ImageBrowseActivity extends ActivityCollector {


    private Bitmap imgBit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_browse);
        StatusBarUtils.setStatusTransparent(this);

        PhotoView photoView = findViewById(R.id.photo_view);

        FloatingActionsMenu menu = findViewById(R.id.image_browse_menu);
        FloatingActionButton downloadBtn = findViewById(R.id.image_browse_download);
        downloadBtn.setOnClickListener(v -> {
            menu.collapseImmediately();
            ImageTools.saveImgToGallery(this, this, imgBit);
        });
        Intent intent = getIntent();
        int type = intent.getIntExtra("type", -1);
        if (-1 != type) {
            switch (type) {
                case 1:
                    String url = intent.getStringExtra("url");
                    Glide.with(this)
                            .asBitmap()
                            .load(url)
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    imgBit = resource;
                                    photoView.setImageBitmap(resource);
                                }
                            });
                    break;
                case 2:
                    break;
                case 3:
                    break;
                case 4:
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionUtils.CODE_WRITE_EXTERNAL_STORAGE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ImageTools.saveImgToGallery(this, this, imgBit);
            } else {
                ToastUtils.showToast(this, "权限被拒绝，保存失败", Toast.LENGTH_LONG);
            }
        }
    }
}

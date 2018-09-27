package com.example.lqs2.courseapp.activities;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.example.lqs2.courseapp.R;
import com.example.lqs2.courseapp.adapters.PageViewAdapter;
import com.example.lqs2.courseapp.entity.File;
import com.example.lqs2.courseapp.fragment.DepthPageTransform;
import com.example.lqs2.courseapp.fragment.FileFragment;

import java.util.ArrayList;
import java.util.List;

public class FileActivity extends FragmentActivity implements View.OnClickListener {

    private static final String TAG = "FileActivity";
    private final List<File> fileList = new ArrayList<>();
    private ViewPager mViewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_personal_center);

        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        android.app.ActionBar actionBar = getActionBar();
        if (actionBar != null)
            actionBar.hide();
        init();



    }

    @Override
    public void onClick(View v) {

    }

    private void init() {
        mViewPager = findViewById(R.id.main_viewpager);
        tabLayout = findViewById(R.id.main_tab_layout);
        List<String> tabList = new ArrayList<>();
        tabList.add("文件");
//        tabList.add("便签");
//        tabList.add("好友");
//        tabList.add("消息");

        Fragment fragment1 = new FileFragment();
//        Fragment fragment2 = BlankFragment.newInstance("tab2");
//        Fragment fragment3 = BlankFragment.newInstance("tab3");
//        Fragment fragment4 = BlankFragment.newInstance("tab4");


        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(fragment1);
//        fragmentList.add(fragment2);
//        fragmentList.add(fragment3);
//        fragmentList.add(fragment4);

        mViewPager.setPageTransformer(true, new DepthPageTransform());
        mViewPager.setAdapter(new PageViewAdapter(getSupportFragmentManager(), fragmentList, tabList));
        tabLayout.setupWithViewPager(mViewPager);
    }



}

package com.example.lqs2.courseapp.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.example.lqs2.courseapp.R;
import com.example.lqs2.courseapp.adapters.PageViewAdapter;
import com.example.lqs2.courseapp.fragment.DepthPageTransform;
import com.example.lqs2.courseapp.fragment.FileFragment;
import com.example.lqs2.courseapp.utils.StatusBarUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件活动
 *
 * @author lqs2
 */
public class FileActivity extends FragmentActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_center);
        StatusBarUtils.setStatusTransparent(this);

        init();
    }

    @Override
    public void onClick(View v) {

    }

    /**
     * 加载标签页，现在只剩一个了
     */
    private void init() {
        ViewPager mViewPager = findViewById(R.id.main_viewpager);
        TabLayout tabLayout = findViewById(R.id.main_tab_layout);
        List<String> tabList = new ArrayList<>();
        tabList.add("文件");
        Fragment fragment1 = new FileFragment();
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(fragment1);
        mViewPager.setPageTransformer(true, new DepthPageTransform());
        mViewPager.setAdapter(new PageViewAdapter(getSupportFragmentManager(), fragmentList, tabList));
        tabLayout.setupWithViewPager(mViewPager);
    }


}

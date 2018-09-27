package com.example.lqs2.courseapp.activities;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.example.lqs2.courseapp.R;
import com.example.lqs2.courseapp.fragment.FriendFragment;
import com.example.lqs2.courseapp.fragment.MessageFragment;
import com.google.gson.Gson;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

public class FriendActivity extends ActivityCollector {


    public static Gson gson;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        gson = new Gson();
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

//        FragmentPageItemAdapter =
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(),
                FragmentPagerItems
                        .with(this)
                        .add("聊天", MessageFragment.class)
                        .add("好友", FriendFragment.class)
//                        .add("系统消息", FriendFragment.class)
                        .create());
        ViewPager viewPager = findViewById(R.id.viewpager_fri);
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = findViewById(R.id.viewpagertab_friend);
        viewPagerTab.setViewPager(viewPager);

    }
}

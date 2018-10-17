package com.example.lqs2.courseapp.activities;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.example.lqs2.courseapp.R;
import com.example.lqs2.courseapp.fragment.FriendFragment;
import com.example.lqs2.courseapp.fragment.MessageFragment;
import com.example.lqs2.courseapp.utils.StatusBarUtils;
import com.google.gson.Gson;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

/**
 * 好友活动
 *
 * @author lqs2
 */
public class FriendActivity extends ActivityCollector {


    public static Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        StatusBarUtils.setStatusTransparent(this);

        gson = new Gson();
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(),
                FragmentPagerItems
                        .with(this)
                        .add("聊天", MessageFragment.class)
                        .add("好友", FriendFragment.class)
                        .create());
        ViewPager viewPager = findViewById(R.id.viewpager_fri);
        viewPager.setAdapter(adapter);
        SmartTabLayout viewPagerTab = findViewById(R.id.viewpagertab_friend);
        viewPagerTab.setViewPager(viewPager);

    }
}

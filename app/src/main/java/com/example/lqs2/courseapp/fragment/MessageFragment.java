package com.example.lqs2.courseapp.fragment;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.lqs2.courseapp.R;
import com.example.lqs2.courseapp.activities.FriendActivity;
import com.example.lqs2.courseapp.adapters.FriMsgAdapter;
import com.example.lqs2.courseapp.entity.UserMessage;
import com.example.lqs2.courseapp.utils.HttpUtil;
import com.example.lqs2.courseapp.utils.ToastUtils;
import com.example.lqs2.courseapp.utils.UsualSharedPreferenceUtil;
import com.google.gson.reflect.TypeToken;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 *
 * @author lqs2
 */
public class MessageFragment extends Fragment {


    private com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView recyclerView;

    private SwipeRefreshLayout swipeRefreshLayout;
    private List<UserMessage> messages;

    private FriMsgAdapter adapter;

    private String un;


    private Context context;
    private Activity activity;

    public MessageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        recyclerView = view.findViewById(R.id.fri_msg_all);
        swipeRefreshLayout = view.findViewById(R.id.fri_msg_swipe_refresh_layout);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getContext();
        activity = getActivity();

        initSwipeRecyclerView();

        un = UsualSharedPreferenceUtil.getDarkmeAccount(context);
        loadUserMessage(true);
    }

    private void initSwipeRecyclerView() {
        adapter = new FriMsgAdapter(context, activity);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setSwipeMenuCreator((swipeLeftMenu, swipeRightMenu, viewType) -> {
            int width = getResources().getDimensionPixelSize(R.dimen.dp_70);
            int height = ViewGroup.LayoutParams.MATCH_PARENT;

            SwipeMenuItem item1 = new SwipeMenuItem(context)
                    .setText("  删除  ")
                    .setTextColor(context.getResources().getColor(R.color.white))
                    .setBackground(R.color.bsp_red)
                    .setWeight(width)
                    .setHeight(height);
            swipeRightMenu.addMenuItem(item1);
        });

        recyclerView.setSwipeItemClickListener((itemView, position) -> {
            UserMessage msg = messages.get(position);
            ToastUtils.showToast(context, msg.getMsgContent(), Toast.LENGTH_SHORT);
        });
        recyclerView.setSwipeMenuItemClickListener(menuBridge -> {
            menuBridge.closeMenu();
            // -1 为右侧菜单， 0为菜单的第一项
            int direction = menuBridge.getDirection();
            // RecyclerView的Item的position
            int adapterPosition = menuBridge.getAdapterPosition();
            // 菜单在RecyclerView的Item中的Position
            int menuPosition = menuBridge.getPosition();
            if (0 == menuPosition) {
                UserMessage msg = messages.get(adapterPosition);
                String msgId = msg.getId();
                int type = msg.getMsgType();
                if (1 != type) {
                    new Thread(() -> HttpUtil.deleteUserMessage(msgId)).start();
                }
            }
            adapter.deleteItem(adapterPosition);

        });
        swipeRefreshLayout.setOnRefreshListener(() -> loadUserMessage(false));
        recyclerView.setAdapter(adapter);
    }


    private void loadUserMessage(boolean init) {
        HttpUtil.getUserMessage(un, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                String str = "消息拉取失败";
                if (!init) {
                    str = "刷新错误";
                }
                ToastUtils.showToastOnMain(context, activity, str + "，请稍后重试", Toast.LENGTH_LONG);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                String resp = response.body().string();
                if (!TextUtils.isEmpty(resp)) {
                    if (!"-1".equals(resp)) {
                        messages = FriendActivity.gson.fromJson(resp, new TypeToken<List<UserMessage>>() {
                        }.getType());
                        activity.runOnUiThread(() -> adapter.setData(messages));
                    }
                }
                activity.runOnUiThread(() -> swipeRefreshLayout.setRefreshing(false));
            }
        });
    }
}

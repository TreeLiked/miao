package com.example.lqs2.courseapp.fragment;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lqs2.courseapp.R;
import com.example.lqs2.courseapp.adapters.FriendListAdapter;
import com.example.lqs2.courseapp.entity.User;
import com.example.lqs2.courseapp.entity.UserFriend;
import com.example.lqs2.courseapp.utils.Base64ImageUtils;
import com.example.lqs2.courseapp.utils.HttpUtil;
import com.example.lqs2.courseapp.utils.InputUtils;
import com.example.lqs2.courseapp.utils.MaterialDialogUtils;
import com.example.lqs2.courseapp.utils.ToastUtils;
import com.example.lqs2.courseapp.utils.UsualSharedPreferenceUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendFragment extends Fragment {


    private RecyclerView friListView;
    private EditText friOptEdit;

    private Context context;
    private Activity activity;

    private List<UserFriend> friendList;
    private List<UserFriend> specialFriendList = new ArrayList<>();
    private FriendListAdapter adapter;

    private SwipeRefreshLayout layout;

    private Gson gson;
    private String un;

    private long currentTime;
    private long lastTime;

    public FriendFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friend, container, false);

        gson = new Gson();
        context = getContext();
        activity = getActivity();

        friListView = view.findViewById(R.id.fri_list_recycler_view);
        friOptEdit = view.findViewById(R.id.fri_opt_edit);
        layout = view.findViewById(R.id.flush_friends_layout);
        layout.setOnRefreshListener(() -> displayMyFriend());
        friOptEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    if (null != friendList && friendList.size() > 0) {
                        specialFriendList.clear();
                        for (int i = 0; i < friendList.size(); i++) {
                            UserFriend friend = friendList.get(i);
                            String str = s.toString().trim();
                            if (friend.getFriendId().contains(str) || friend.getFriendMark().contains(str)) {
                                specialFriendList.add(friend);
                            }
                        }
                        adapter.setData(specialFriendList);
                    }
                } else {
                    adapter.setData(friendList);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                lastTime = System.currentTimeMillis();
                String text = s.toString().trim();
                if (specialFriendList.size() == 0 && !TextUtils.isEmpty(text)) {
                    InputUtils.closeSoftKeyboard(friOptEdit, context);
                    Handler handler = new Handler();
                    handler.postDelayed(new QueryUser(), 1500);
                }
            }
        });
        adapter = new FriendListAdapter(getActivity(), FriendFragment.this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        friListView.setLayoutManager(layoutManager);
        friListView.setAdapter(adapter);


        displayMyFriend();
        return view;
    }

    public void displayMyFriend() {
        layout.setRefreshing(true);
        un = UsualSharedPreferenceUtil.getDarkmeAccount(this.getContext());
        HttpUtil.showMyFriends(un, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                activity.runOnUiThread(() -> {
                    layout.setRefreshing(false);
                    ToastUtils.showToast(context, "服务错误", Toast.LENGTH_SHORT);
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                if (!"-1".equals(resp)) {
                    friendList = gson.fromJson(resp, new TypeToken<List<UserFriend>>() {
                    }.getType());
                    activity.runOnUiThread(() -> {
                        layout.setRefreshing(false);
                        adapter.setData(friendList);
                    });
                } else {
                    activity.runOnUiThread(() -> {
                        layout.setRefreshing(false);
                        ToastUtils.showToast(context, "服务异常", Toast.LENGTH_SHORT);

                    });
                }
            }
        });
    }

    class QueryUser implements Runnable {
        public void run() {
            currentTime = System.currentTimeMillis();
            if (currentTime - lastTime >= 1500) {
                String text = friOptEdit.getText().toString().trim();
                MaterialDialogUtils.showYesOrNoDialog(getActivity(), new String[]{"No User Matches", "是否查找ID为 [ " + text + " ] 的用户", "查找", "关闭"}, new MaterialDialogUtils.DialogOnConfirmClickListener() {
                    @Override
                    public void onConfirmButtonClick() {
                        HttpUtil.hasMatcherUser(text, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                ToastUtils.showToastOnMain(context, activity, "连接错误", Toast.LENGTH_SHORT);
                            }

                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onResponse(Call call, Response response) throws IOException {

                                String resp = response.body().string();
                                if (!"0".equals(resp) && !"error".equals(resp)) {
                                    User user = gson.fromJson(resp, User.class);
                                    if (null != user) {
                                        Bitmap bitmap = null;
                                        String picStr = user.getProfilePicStr();
                                        if (Base64ImageUtils.isPicPath(picStr)) {
                                            bitmap = Base64ImageUtils.base64StrToBitmap(picStr);
                                        }
                                        Bitmap finalBitmap = bitmap;
                                        activity.runOnUiThread(() -> {

                                            LayoutInflater inflater = LayoutInflater.from(context);
                                            View view = inflater.inflate(R.layout.user_detail_card, null, false);
                                            CircleImageView v0 = view.findViewById(R.id.user_detail_profile_pic);
                                            TextView v2 = view.findViewById(R.id.user_detail_userId);
                                            TextView v3 = view.findViewById(R.id.user_detail_userSex);
                                            TextView v4 = view.findViewById(R.id.user_detail_userMail);
                                            TextView v5 = view.findViewById(R.id.user_detail_userCreateTime);
                                            if (null == finalBitmap) {
                                                v0.setImageResource(R.drawable.default_head);
                                            } else {
                                                v0.setImageBitmap(finalBitmap);
                                            }
                                            v2.setText("用 户 名：" + user.getUsername());
                                            v3.setText("性     别：" + (user.isMan() ? "男" : "女"));
                                            v4.setText("邮     箱：" + user.getEmail());
                                            v5.setText("添加时间：" + user.getCreateTime());
                                            MaterialDialogUtils.showYesOrNoDialogWithCustomView(context, new String[]{"查找到用户", "", "添加", "关闭"}, view, new MaterialDialogUtils.DialogOnConfirmClickListener() {
                                                @Override
                                                public void onConfirmButtonClick() {
                                                    ToastUtils.showToast(context, "已发送好友添加请求", Toast.LENGTH_LONG);
                                                    friOptEdit.setText("");
                                                    new Thread(() -> HttpUtil.userSendAddFriendMessage(un, user.getUsername())).start();
                                                }
                                            }, true);
                                        });
                                    }
                                } else {
                                    ToastUtils.showToastOnMain(context, activity, "未查找到 [ 用户名：" + text + " ] 的用户", Toast.LENGTH_LONG);

                                }
                            }
                        });
                    }
                }, true);
            }
        }
    }
}

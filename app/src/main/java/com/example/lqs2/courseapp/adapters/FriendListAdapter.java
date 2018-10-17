package com.example.lqs2.courseapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.lqs2.courseapp.R;
import com.example.lqs2.courseapp.activities.FriendActivity;
import com.example.lqs2.courseapp.entity.UserFriend;
import com.example.lqs2.courseapp.fragment.FriendFragment;
import com.example.lqs2.courseapp.global.ThreadPoolExecutorFactory;
import com.example.lqs2.courseapp.utils.Base64ImageUtils;
import com.example.lqs2.courseapp.utils.HttpUtil;
import com.example.lqs2.courseapp.utils.MaterialDialogUtils;
import com.example.lqs2.courseapp.utils.SharedPreferenceUtil;
import com.example.lqs2.courseapp.utils.TimeUtils;
import com.example.lqs2.courseapp.utils.ToastUtils;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 好友适配器
 *
 * @author lqs2
 */
public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ViewHolder> {

    private Context mContext;
    private List<UserFriend> friends;
    private FriendFragment fragment;
    private String un;


    public FriendListAdapter(Context mContext, FriendFragment fragment) {
        this.mContext = mContext;
        this.fragment = fragment;
        un = (String) SharedPreferenceUtil.get(mContext, "darkme_un", "");
    }

    public void setData(List<UserFriend> friends) {
        this.friends = friends;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout layout;
        TextView friendIdView;
        TextView friendMarkView;
        TextView createTimeView;
        CircleImageView profilePicView;

        ViewHolder(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.user_friend_simple_layout);
            friendIdView = itemView.findViewById(R.id.user_friend_id);
            friendMarkView = itemView.findViewById(R.id.user_friend_mark);
            createTimeView = itemView.findViewById(R.id.user_friend_makeTime);
            profilePicView = itemView.findViewById(R.id.user_profile_pic3);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.fri_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        UserFriend simple = friends.get(position);
        holder.friendIdView.setText(simple.getFriendId());
        holder.friendMarkView.setText(simple.getFriendMark());
        if (TextUtils.isEmpty(simple.getFriendMark())) {
            holder.friendIdView.setText("");
            holder.friendMarkView.setText(simple.getFriendId());
        }
        holder.createTimeView.setText("添加时间: " + TimeUtils.tweetPostTimeConvert(simple.getCreateTime()));


        bindUserProfilePicAndDetailInfo(simple, holder);

//        bindDetailInfo(simple, holder);
//        bindClickToChat(simple, holder);
    }

//    private void bindClickToChat(UserFriend f, ViewHolder holder) {
//        holder.layout.setOnClickListener(v -> {
//            Intent i = new Intent(mContext, ChatActivity.class);
//            if (!TextUtils.isEmpty(f.getFriendMark())) {
//                i.putExtra("friendId", f.getFriendMark());
//            } else {
//                i.putExtra("friendId", f.getFriendId());
//            }
//            i.putExtra("un", un);
//            mContext.startActivity(i);
//        });
//    }


    private void bindUserProfilePicAndDetailInfo(UserFriend friend, ViewHolder holder) {

        ThreadPoolExecutorFactory.getThreadPoolExecutor().execute(() -> HttpUtil.getUserProfilePicture(friend.getFriendId(), new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                String resp = response.body().string();
                if (Base64ImageUtils.isPicPath(resp)) {
                    Bitmap pic = Base64ImageUtils.base64StrToBitmap(resp);
                    Objects.requireNonNull(fragment.getActivity()).runOnUiThread(() -> Glide.with(mContext).load(pic).into(holder.profilePicView));
                    bindDetailInfo(friend, holder, pic);
                } else {
                    Objects.requireNonNull(fragment.getActivity()).runOnUiThread(() -> Glide.with(mContext).load(R.drawable.default_head).into(holder.profilePicView));
                    bindDetailInfo(friend, holder, null);
                }
            }
        }));
    }

    /**
     * 绑定好友的详细信息
     *
     * @param simple 好友对象
     * @param holder 当前视图
     * @param bitmap 好友头像
     */
    @SuppressLint("SetTextI18n")
    private void bindDetailInfo(UserFriend simple, ViewHolder holder, Bitmap bitmap) {
        ((FriendActivity) mContext).runOnUiThread(() -> holder.layout.setOnClickListener(v -> {
                    LayoutInflater inflater = LayoutInflater.from(mContext);
                    View view = inflater.inflate(R.layout.friend_detail_card, null, false);
                    CircleImageView v0 = view.findViewById(R.id.friend_detail_profile_pic);
                    TextView v2 = view.findViewById(R.id.friend_detail_friendId);
                    TextView v3 = view.findViewById(R.id.friend_detail_friendMark);
                    TextView v4 = view.findViewById(R.id.friend_detail_friendSex);
                    TextView v5 = view.findViewById(R.id.friend_detail_friendMail);
                    TextView v6 = view.findViewById(R.id.friend_detail_friendMakeTime);
                    EditText e0 = view.findViewById(R.id.friend_detail_modify_friendMark);
                    if (null == bitmap) {
                        v0.setImageResource(R.drawable.default_head);
                    } else {
                        v0.setImageBitmap(bitmap);
                    }
                    v2.setText("用 户 名：" + simple.getFriendId());
                    v3.setText("备     注：" + simple.getFriendMark());
                    v4.setText("性     别：" + (simple.isMan() ? "男" : "女"));
                    v5.setText("邮     箱：" + simple.getEmail());
                    v6.setText("添加时间：" + simple.getCreateTime());
                    MaterialDialogUtils.showYesOrNoDialogWithAll(mContext, new String[]{"好友信息", "", "关闭", "删除好友"}, view, -1, new MaterialDialogUtils.AbstractDialogBothDoSthOnClickListener() {
                        @Override
                        public void onConfirmButtonClick() {
                            String mark = e0.getText().toString();
                            if (!TextUtils.isEmpty(mark) && !TextUtils.isEmpty(un) && !mark.equals(simple.getFriendMark())) {
                                HttpUtil.changeFriendMark(un, simple.getFriendId(), mark, new Callback() {
                                    @Override
                                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                        ToastUtils.showConnectErrorOnMain(mContext, fragment.getActivity());
                                    }

                                    @Override
                                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                        assert response.body() != null;
                                        String resp = response.body().string();
                                        if ("1".equals(resp)) {
                                            ToastUtils.showToastOnMain(mContext, Objects.requireNonNull(fragment.getActivity()), "备注修改成功", Toast.LENGTH_SHORT);
                                            fragment.displayMyFriend();
                                        } else {
                                            ToastUtils.showToastOnMain(mContext, Objects.requireNonNull(fragment.getActivity()), "备注修改失败", Toast.LENGTH_SHORT);
                                        }
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelButtonClick() {
                            MaterialDialogUtils.showYesOrNoDialog(mContext, new String[]{"删除好友", "确认删除 [ " + simple.getFriendId() + (!TextUtils.isEmpty(simple.getFriendMark()) ? "（" + simple.getFriendMark() + "）" : "") + " ]", "确认", "取消"}, new MaterialDialogUtils.AbstractDialogOnConfirmClickListener() {
                                @Override
                                public void onConfirmButtonClick() {

                                    HttpUtil.deleteUserFriend(simple.getId(), un, simple.getFriendId(), new Callback() {
                                        @Override
                                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                            ToastUtils.showConnectErrorOnMain(mContext, fragment.getActivity());
                                        }

                                        @Override
                                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                            assert response.body() != null;
                                            String resp = response.body().string();
                                            if ("1".equals(resp)) {
                                                ToastUtils.showToastOnMain(mContext, Objects.requireNonNull(fragment.getActivity()), "删除成功", Toast.LENGTH_SHORT);
                                                fragment.displayMyFriend();
                                            } else {
                                                ToastUtils.showToastOnMain(mContext, Objects.requireNonNull(fragment.getActivity()), "删除失败", Toast.LENGTH_SHORT);
                                            }
                                        }
                                    });
                                }
                            }, true);
                        }
                    }, true);
                })
        );
    }

    @Override
    public int getItemCount() {
        return friends != null ? friends.size() : -1;
    }

}

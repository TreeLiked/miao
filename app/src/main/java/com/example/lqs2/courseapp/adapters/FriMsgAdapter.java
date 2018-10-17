package com.example.lqs2.courseapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.lqs2.courseapp.R;
import com.example.lqs2.courseapp.activities.FileActivity;
import com.example.lqs2.courseapp.entity.UserMessage;
import com.example.lqs2.courseapp.global.ThreadPoolExecutorFactory;
import com.example.lqs2.courseapp.utils.Base64ImageUtils;
import com.example.lqs2.courseapp.utils.HttpUtil;
import com.example.lqs2.courseapp.utils.MaterialDialogUtils;
import com.example.lqs2.courseapp.utils.TimeUtils;
import com.example.lqs2.courseapp.utils.ToastUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 好友消息适配器
 *
 * @author lqs2
 */
public class FriMsgAdapter extends RecyclerView.Adapter<FriMsgAdapter.ViewHolder> {

    private Context mContext;
    private Activity activity;
    private List<UserMessage> messageList;

    /**
     * 用来保存不重复的好友
     */
    private List<String> friDistinctList = new ArrayList<>();

    public FriMsgAdapter(Context mContext, Activity activity) {
        this.mContext = mContext;
        this.activity = activity;
    }

    public void setData(List<UserMessage> messages) {

        friDistinctList.clear();
        this.messageList = messages;
        notifyDataSetChanged();
    }


    /**
     * 混合相同的好友
     */
    private void mergeIdenticalFriend() {
        for (int i = 0; i < messageList.size(); i++) {
            UserMessage msg = messageList.get(i);
            if (1 == msg.getMsgType()) {
                if (!friDistinctList.contains(msg.getMsgFrom())) {
                    friDistinctList.add(msg.getMsgFrom());
                } else {
                    messageList.remove(i);
                }
            }
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout layout;
        TextView userIdView;
        TextView chatTimeView;
        TextView contentView;
        CircleImageView profilePicView;

        ViewHolder(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.chat_simple_layout);
            userIdView = itemView.findViewById(R.id.user_userId1);
            chatTimeView = itemView.findViewById(R.id.chat_time1);
            contentView = itemView.findViewById(R.id.chat_content1);
            profilePicView = itemView.findViewById(R.id.user_profile_pic1);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.fri_msg_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        UserMessage msg = messageList.get(position);
        String msgContent = msg.getMsgContent();
        int msgType = msg.getMsgType();

        switch (msgType) {
            case 1:
                break;
            case 2:
                holder.userIdView.setText("好友消息");
                holder.contentView.setText(msgContent);
                holder.chatTimeView.setText(TimeUtils.tweetPostTimeConvert(msg.getPostTime()));
                Glide.with(mContext).load(R.drawable.fri_msg_fri_icon).into(holder.profilePicView);
                bindFriAction(holder, msgType, msg, position);
                break;
            case 3:
                holder.userIdView.setText("文件消息");
                holder.contentView.setText(msgContent);
                holder.chatTimeView.setText(TimeUtils.tweetPostTimeConvert(msg.getPostTime()));
                Glide.with(mContext).load(R.drawable.fri_msg_file_icon).into(holder.profilePicView);
                bindFileAction(holder, msgType, msg, position);
                break;
            case 0:
                holder.userIdView.setText("系统通知");
                holder.contentView.setText(msgContent);
                holder.chatTimeView.setText(TimeUtils.tweetPostTimeConvert(msg.getPostTime()));
                Glide.with(mContext).load(R.drawable.fri_msg_system_icon).into(holder.profilePicView);
                bindSysAction(holder, msgType, msg, position);
                break;
            default:
                break;
        }
    }

    /**
     * 删除消息的item
     *
     * @param position adapter 中的位置
     */
    public void deleteItem(int position) {
        messageList.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }


    /**
     * 绑定好友动作
     *
     * @param holder   当前视图
     * @param type     忘了
     * @param msg      消息内容
     * @param position 消息位置
     */
    private void bindFriAction(ViewHolder holder, int type, UserMessage msg, int position) {
        holder.layout.setOnClickListener(v -> bindOnClick(type, msg, position));
    }

    /**
     * 绑定文件动作
     *
     * @param holder   当前视图
     * @param type     忘了
     * @param msg      消息内容
     * @param position 消息位置
     */
    private void bindFileAction(ViewHolder holder, int type, UserMessage msg, int position) {
        holder.layout.setOnClickListener(v -> bindOnClick(type, msg, position));
    }

    /**
     * 绑定系统通知动作
     *
     * @param holder   当前视图
     * @param type     忘了
     * @param msg      消息内容
     * @param position 消息位置
     */
    private void bindSysAction(ViewHolder holder, int type, UserMessage msg, int position) {
        holder.layout.setOnClickListener(v -> bindOnClick(type, msg, position));
    }

    /**
     * 绑定单击时间
     *
     * @param type     消息的类型，想起来了
     * @param msg      消息内容
     * @param position 消息的位置
     */
    private void bindOnClick(int type, UserMessage msg, int position) {
        switch (type) {
            case 0:
                MaterialDialogUtils.showSimpleConfirmDialog(mContext, new String[]{"系统通知", msg.getMsgContent(), "我知道了", ""});
                break;
            case 2:
//                这种写法也是没谁了，，主要是不想写了
                if (msg.getMsgContent().contains("迫切")) {
                    MaterialDialogUtils.showYesOrNoDialog(mContext, new String[]{"好友通知", msg.getMsgContent(), "答应了", "当作没看见"}, new MaterialDialogUtils.AbstractDialogOnConfirmClickListener() {
                        @Override
                        public void onConfirmButtonClick() {
                            HttpUtil.userSendAgreeFriendMessage(msg.getId(), msg.getMsgTo(), msg.getMsgFrom(), true, new Callback() {
                                @Override
                                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                    ToastUtils.showConnectErrorOnMain(mContext, activity);
                                }

                                @Override
                                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                    assert response.body() != null;
                                    String resp = response.body().string();
                                    if ("1".equals(resp)) {
                                        ToastUtils.showToastOnMain(mContext, activity, "好友添加成功", Toast.LENGTH_SHORT);
                                        deleteItem(position);
                                        HttpUtil.deleteUserMessage(msg.getId());
                                    } else {
                                        ToastUtils.showToastOnMain(mContext, activity, "添加失败", Toast.LENGTH_SHORT);
                                    }
                                }
                            });
                        }
                    }, true);
                } else {
                    MaterialDialogUtils.showSimpleConfirmDialog(mContext, new String[]{"好友通知", msg.getMsgContent(), "好的", ""});
                }
                break;
            case 3:
                MaterialDialogUtils.showYesOrNoDialog(mContext, new String[]{"文件通知", msg.getMsgContent(), "前往下载", "以后再说"}, new MaterialDialogUtils.AbstractDialogOnConfirmClickListener() {
                    @Override
                    public void onConfirmButtonClick() {
                        Intent intent = new Intent(mContext, FileActivity.class);
                        mContext.startActivity(intent);
                    }
                }, true);
                break;
            default:
                break;

        }
    }

    /**
     * 加载好友头像
     *
     * @param friId 好友的id
     * @param view  当前视图
     * @param type  头像类型
     */
    private void loadFriendProfilePic(String friId, CircleImageView view, int type) {
        ThreadPoolExecutorFactory.getThreadPoolExecutor().execute(() -> HttpUtil.getUserProfilePicture(friId, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                String resp = response.body().string();
                if (!TextUtils.isEmpty(resp) && Base64ImageUtils.isPicPath(resp)) {
                    Bitmap bitmap = Base64ImageUtils.base64StrToBitmap(resp);
                    activity.runOnUiThread(() -> Glide.with(mContext).load(bitmap).into(view));
                } else {
                    switch (type) {
                        case 1:
                            activity.runOnUiThread(() -> Glide.with(mContext).load(R.drawable.default_head).into(view));
                            break;
                        case 3:
                            break;
                        case 0:
                            break;
                        default:
                            break;
                    }
                }
            }
        }));
    }

    @Override
    public int getItemCount() {
        return messageList != null ? messageList.size() : -1;
    }
}
package com.example.lqs2.courseapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.lqs2.courseapp.R;
import com.example.lqs2.courseapp.activities.ChatActivity;
import com.example.lqs2.courseapp.entity.ChatMsg;
import com.example.lqs2.courseapp.utils.Base64ImageUtils;
import com.example.lqs2.courseapp.utils.HttpUtil;

import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 聊天适配器
 *
 * @author lqs2
 */
public class ChatMsgAdapter extends RecyclerView.Adapter<ChatMsgAdapter.ViewHolder> {

    private List<ChatMsg> mMsgList;
    private Context mContext;

    private Bitmap userBit;
    private Bitmap myBit;

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout leftLayout;
        LinearLayout rightLayout;
        TextView leftMsg;
        TextView rightMsg;
        CircleImageView leftPic;
        CircleImageView rightPic;

        public ViewHolder(View view) {
            super(view);
            leftLayout = view.findViewById(R.id.left_layout);
            rightLayout = view.findViewById(R.id.right_layout);
            leftMsg = view.findViewById(R.id.left_msg);
            rightMsg = view.findViewById(R.id.right_msg);
            leftPic = view.findViewById(R.id.left_profile_pic);
            rightPic = view.findViewById(R.id.right_profile_pic);
        }
    }

    public ChatMsgAdapter(Context c) {
        this.mContext = c;
    }


    /**
     * 设置数据
     *
     * @param list 数据
     */
    public void setData(List<ChatMsg> list) {
        this.mMsgList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_msg_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatMsg msg = mMsgList.get(position);
        if (msg.getType() == ChatMsg.TYPE_RECEIVED) {
            if (null == userBit) {
                pullUserProfilePic(msg.getFromId(), holder.leftPic, false);
            } else {
                Glide.with(mContext).load(userBit).into(holder.leftPic);
            }
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftMsg.setText(msg.getContent());
        } else if (msg.getType() == ChatMsg.TYPE_SENT) {
            if (null == myBit) {
                pullUserProfilePic(msg.getFromId(), holder.rightPic, true);
            } else {
                Glide.with(mContext).load(myBit).into(holder.rightPic);
            }
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.leftLayout.setVisibility(View.GONE);
            holder.rightMsg.setText(msg.getContent());
        }
    }

    @Override
    public int getItemCount() {
        return mMsgList != null ? mMsgList.size() : -1;
    }


    /**
     * 拉取用户头像
     *
     * @param fromId    来源
     * @param imageView 头像layout
     * @param myself    my
     */
    private void pullUserProfilePic(String fromId, CircleImageView imageView, boolean myself) {
        HttpUtil.getUserProfilePicture(fromId, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                String resp = response.body().string();
                if (!TextUtils.isEmpty(resp) && Base64ImageUtils.isPicPath(resp)) {
                    if (myself) {
                        myBit = Base64ImageUtils.base64StrToBitmap(resp);
                    } else {
                        userBit = Base64ImageUtils.base64StrToBitmap(resp);
                    }
                } else {
                    if (myself) {
                        myBit = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.default_head);
                    } else {
                        userBit = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.default_head);
                    }
                }
                if (myself) {
                    ((ChatActivity) mContext).runOnUiThread(() -> Glide.with(mContext).load(myBit).into(imageView));
                } else {
                    ((ChatActivity) mContext).runOnUiThread(() -> Glide.with(mContext).load(userBit).into(imageView));
                }
            }
        });
    }
}

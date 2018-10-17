package com.example.lqs2.courseapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lqs2.courseapp.R;
import com.example.lqs2.courseapp.activities.NoticeActivity;
import com.example.lqs2.courseapp.activities.TweetDetailActivity;
import com.example.lqs2.courseapp.entity.Notice;
import com.example.lqs2.courseapp.utils.HtmlCodeExtractUtil;
import com.example.lqs2.courseapp.utils.HttpUtil;
import com.example.lqs2.courseapp.utils.ToastUtils;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 通知适配器
 *
 * @author lqs2
 */
public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.NoticeHolder> {

    private Context context;
    private Activity activity;

    private List<Notice> notices;

    public NoticeAdapter(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public void setData(List<Notice> notices) {
        this.notices = notices;
        notifyDataSetChanged();
    }

    /**
     * 绑定单击时间
     *
     * @param holder 当前视图
     * @param notice 当前通知对象
     */
    private void bindOneClick(NoticeHolder holder, Notice notice) {

        String contentUrl = notice.getContentUrl();
        holder.layout.setOnClickListener(v -> HttpUtil.getNoticeDetail(contentUrl, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                ToastUtils.showConnectErrorOnMain(context, (NoticeActivity) context);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                String resp = response.body().string();
                Notice notice = HtmlCodeExtractUtil.getSingleNoticeDetail(resp, contentUrl);
                Intent intent = new Intent(activity, TweetDetailActivity.class);
                Bundle bundle = new Bundle();
                intent.putExtra("FROM", 1);
                bundle.putSerializable("notice", notice);
                intent.putExtras(bundle);
                activity.startActivity(intent);

            }
        }));
    }

    @NonNull
    @Override
    public NoticeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (context == null) {
            context = parent.getContext();
        }
        return new NoticeHolder(LayoutInflater.from(context).inflate(R.layout.notice_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeHolder holder, int position) {
        Notice notice = notices.get(position);

        holder.titleView.setText(notice.getTitle());
        holder.timeView.setText(notice.getTime());

        bindOneClick(holder, notice);

    }

    static class NoticeHolder extends RecyclerView.ViewHolder {

        LinearLayout layout;
        TextView titleView;
        TextView timeView;

        NoticeHolder(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.notice_layout);
            titleView = itemView.findViewById(R.id.notice_title_view);
            timeView = itemView.findViewById(R.id.notice_time_view);
        }
    }

    @Override
    public int getItemCount() {
        return notices != null ? notices.size() : -1;
    }


}

package com.example.lqs2.courseapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lqs2.courseapp.R;
import com.example.lqs2.courseapp.common.StringUtils;
import com.example.lqs2.courseapp.entity.Book;
import com.example.lqs2.courseapp.entity.BookLoc;
import com.example.lqs2.courseapp.utils.HtmlCodeExtractUtil;
import com.example.lqs2.courseapp.utils.HttpUtil;
import com.example.lqs2.courseapp.utils.MaterialDialogUtils;
import com.example.lqs2.courseapp.utils.ToastUtils;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.NoticeHolder> {

    private Context context;
    private Activity activity;

    private List<Book> books;


    public BookAdapter(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public void setData(List<Book> books) {
        this.books = books;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NoticeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (context == null) {
            context = parent.getContext();
        }
        return new NoticeHolder(LayoutInflater.from(context).inflate(R.layout.book_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeHolder holder, int position) {
        Book book = books.get(position);
        holder.nameView.setText(book.getBookNameWithNo());
        holder.authorView.setText(book.getAuthor());
        holder.publisherView.setText(book.getPublisher());
        holder.blInfoView.setText(book.getBlInfo());
        bindOneClick(holder, book);

    }

    private void bindOneClick(NoticeHolder holder, Book book) {

        holder.layout.setOnClickListener(v -> HttpUtil.getBookDeatil(book.getDetailUrl(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ToastUtils.showConnectErrorOnMain(context, activity);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                if (!StringUtils.isEmpty(resp)) {
                    HtmlCodeExtractUtil.parseHtmlForBookDetail(book, resp);
                    StringBuilder builder = new StringBuilder();
                    List<BookLoc> locs = book.getLocs();
                    for (int i = 0; i < locs.size(); i++) {
                        builder.append("馆藏信息 [ ").append(i + 1).append(" ]").append("\n");
                        BookLoc loc = locs.get(i);
                        builder.append("索书号：\t").append(loc.getSearchNo()).append("\n");
                        builder.append("条码号：\t").append(loc.getIdNo()).append("\n");
                        builder.append("年卷期：\t").append(loc.getYearNo()).append("\n");
                        builder.append("馆藏地：\t").append(loc.getRoomNo()).append("\n");
                        builder.append("书刊状态：\t").append(loc.getBlState()).append("\n\n");
                    }
                    activity.runOnUiThread(() -> MaterialDialogUtils.showYesOrNoDialogWithBothSthTodo(context, new String[]{"馆藏", builder.toString(), "确认", "查看图书详细信息"}, new MaterialDialogUtils.DialogBothDoSthOnClickListener() {
                        @Override
                        public void onConfirmButtonClick() {

                        }

                        @Override
                        public void onCancelButtonClick() {
                            MaterialDialogUtils.showSimpleConfirmDialog(context, new String[]{"Detail", book.getDetailInfo(), "确认", ""});
                        }
                    }, true));
                } else {
                    ToastUtils.showToastOnMain(context, activity, "图书信息拉取失败", Toast.LENGTH_SHORT);
                }
            }
        }));
    }

    @Override
    public int getItemCount() {
        return books != null ? books.size() : -1;
    }

    static class NoticeHolder extends RecyclerView.ViewHolder {

        CardView layout;
        TextView nameView;
        TextView authorView;
        TextView publisherView;
        TextView blInfoView;

        NoticeHolder(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.book_item_layout);
            nameView = itemView.findViewById(R.id.book_item_name);
            authorView = itemView.findViewById(R.id.book_item_author);
            publisherView = itemView.findViewById(R.id.book_item_publisher);
            blInfoView = itemView.findViewById(R.id.book_item_blInfo);
        }
    }


}

package com.example.lqs2.courseapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lqs2.courseapp.R;
import com.example.lqs2.courseapp.activities.MemoActivity;
import com.example.lqs2.courseapp.entity.CardItem;
import com.example.lqs2.courseapp.utils.HttpUtil;
import com.example.lqs2.courseapp.utils.MaterialDialogUtils;
import com.example.lqs2.courseapp.utils.ToastUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CardPagerAdapter extends PagerAdapter implements CardAdapter {

    private List<CardView> mViews;
    private List<CardItem> mData;
    private Context mContext;
    private MemoActivity activity;
    private float mBaseElevation;

    private int memoCount;

    public CardPagerAdapter(Context context, MemoActivity activity, int memoCount) {
        this.activity = activity;
        this.mContext = context;
        this.memoCount = memoCount;
        mData = new ArrayList<>();
        mViews = new ArrayList<>();
    }

    public void addCardItem(CardItem item) {
        mViews.add(null);
        mData.add(item);
    }


    public void resetData(List<CardItem> cardItems) {
        mData.clear();
        mViews.add(null);
        mData.addAll(cardItems);
        notifyDataSetChanged();
    }

    public void resetData() {
        mViews.clear();
        mData.clear();
    }

    public float getBaseElevation() {
        return mBaseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        return mViews.get(position);
    }

    @Override
    public int getCount() {

        return mData.size();
    }

//    @Override
//    public float getPageWidth(int position) {
//        return (float) 0.95;
//    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.memo_card_page_adapter, container, false);
        container.addView(view);

        CardItem c = mData.get(position);
        bind(c, view);
        view.setOnLongClickListener(v -> {
            MaterialDialogUtils.showYesOrNoDialog(mContext, new String[]{"确认删除此备忘录吗", "此操作不可恢复", "确认", "取消"}, new MaterialDialogUtils.DialogOnConfirmClickListener() {
                @Override
                public void onConfirmButtonClick() {
                    changeMemoState(c.getId(), "1".equals(c.getType()) ? -1 : -2, true);
                }
            }, true);
            return true;
        });
        CardView cardView = view.findViewById(R.id.cardView);
        if (mBaseElevation == 0) {
            mBaseElevation = cardView.getCardElevation();
        }
        cardView.setMaxCardElevation(mBaseElevation * MAX_ELEVATION_FACTOR);
        mViews.set(position, cardView);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mViews.set(position, null);
    }


    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.setPrimaryItem(container, position, object);
        MemoActivity.currentPositionView.setText("第 " + (position + 1) + " 条，共 " + memoCount + " 条记录");

    }

    private void bind(CardItem item, View view) {

        TextView titleTextView = view.findViewById(R.id.titleTextView);
        TextView contentTextView = view.findViewById(R.id.contentTextView);
        TextView timeView = view.findViewById(R.id.memo_time_view);


        ImageView markView = view.findViewById(R.id.memo_mark_bg);
        Button btn = view.findViewById(R.id.memo_btn_text);
        String state = item.getState();


        if ("1".equals(state)) {
            markView.setImageResource(R.drawable.ic_bookmark_24dp_green);
        } else if ("0".equals(state)) {
            String type = item.getType();
            if ("1".equals(type)) {
                markView.setImageResource(R.drawable.ic_bookmark_24dp_yellow);
            } else if ("0".equals(type)) {
                markView.setImageResource(R.drawable.ic_bookmark_24dp_blue);
            }
        }
        btn.setOnClickListener(v -> {
            if ("1".equals(state)) {
                changeMemoState(item.getId(), 0, false);
            } else if ("0".equals(state)) {
                changeMemoState(item.getId(), 1, false);
            }
        });
        titleTextView.setText(item.getTitle());
        contentTextView.setText(item.getText());
        timeView.setText(item.getCreateTime());
        btn.setText("1".equals(state) ? "取消完成" : "标记为已完成");
    }


    private void changeMemoState(int id, int toState, boolean isDel) {

        HttpUtil.changeUserMemoState("admin", id, toState, isDel, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                showSpecialToast("服务器异常", Toast.LENGTH_SHORT);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                assert response.body() != null;
                String resp = response.body().string();
                if ("1".equals(resp)) {
                    if (isDel) {
                        showSpecialToast("删除成功", Toast.LENGTH_SHORT);
                        if (-1 == toState) {
                            activity.getUserMemoByState(true, true);
                        } else if (-2 == toState) {
                            activity.getUserMemoByState(false, true);
                        }
                    } else {
                        if (1 == toState) {
                            showSpecialToast("任务已完成，喵～", Toast.LENGTH_LONG);
                            activity.getUserMemoByState(true, true);
                        } else if (0 == toState) {
                            showSpecialToast("已恢复到待完成事项", Toast.LENGTH_LONG);
                            activity.getUserMemoByState(false, true);
                        }
                    }
                } else if ("0".equals(resp)) {
                    showSpecialToast("备忘录状态异常", Toast.LENGTH_SHORT);
                } else {
                    showSpecialToast("服务异常", Toast.LENGTH_SHORT);
                }
            }
        });
    }

    private void showSpecialToast(String msg, int t) {
        ToastUtils.showToastOnMain(mContext, activity, msg, t);
    }

    private void flushMemo(boolean isFinished) {


    }
}

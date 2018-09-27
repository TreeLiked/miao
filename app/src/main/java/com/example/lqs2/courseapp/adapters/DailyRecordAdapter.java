package com.example.lqs2.courseapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lqs2.courseapp.entity.DailyRecord;
import com.example.lqs2.courseapp.R;

import java.util.List;

public class DailyRecordAdapter extends RecyclerView.Adapter<DailyRecordAdapter.ViewHolder> {

    private List<DailyRecord> mDailyRecordList;
    private Context mContext;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView record_date;
        TextView record_info;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            record_date = view.findViewById(R.id.record_date);
            record_info = view.findViewById(R.id.record_info);
        }
    }


    public DailyRecordAdapter(List<DailyRecord> mDailyRecordList) {
        this.mDailyRecordList = mDailyRecordList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.daily_record_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DailyRecord record = mDailyRecordList.get(position);
        holder.record_date.setText(record.getDate());
        holder.record_info.setText(record.getRecord());

    }

    @Override
    public int getItemCount() {
        return mDailyRecordList.size();
    }
}

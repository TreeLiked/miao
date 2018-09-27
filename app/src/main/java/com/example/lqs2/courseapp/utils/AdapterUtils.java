package com.example.lqs2.courseapp.utils;

import android.support.v7.widget.RecyclerView;

public class AdapterUtils {


    public static void removeItemAtPosition(RecyclerView.Adapter adapter, int position) {

        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeChanged(0, adapter.getItemCount());

    }
}

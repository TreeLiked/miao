package com.example.lqs2.courseapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.lqs2.courseapp.R;

import java.util.List;

/**
 * 下拉框适配器
 *
 * @author lqs2
 */
public class SpinnerAdapter extends ArrayAdapter<String> {

    private int resourceId;

    public SpinnerAdapter(@NonNull Context context, int resource, @NonNull List objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String str = String.valueOf(getItem(position));
        View view = LayoutInflater.from(getContext()).inflate(R.layout.spinner_item, parent, false);
        TextView label = view.findViewById(R.id.spinner_item_text_view);
        label.setText(str);
        return view;
    }
}

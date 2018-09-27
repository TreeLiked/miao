package com.example.lqs2.courseapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.lqs2.courseapp.entity.Course;
import com.example.lqs2.courseapp.R;

import java.util.List;

public class CourseAdapter extends BaseAdapter {

    private List<Course> mCourseList;
    private int resourceId;
    private Context mContext;


    public CourseAdapter(List<Course> mCourseList, int resourceId, Context mContext) {
        this.mCourseList = mCourseList;
        this.resourceId = resourceId;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mCourseList.size();
    }

    @Override
    public Object getItem(int position) {
        return mCourseList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Course course = (Course) getItem(position);
        View view;
        ViewHolder viewHolder;

        if (convertView == null) {

            view = LayoutInflater.from(mContext).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.textView = view.findViewById(R.id.course_info);
            view.setTag(viewHolder);

        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.textView.setText(course.getClsName());
        return view;
    }

    class ViewHolder {
        TextView textView;
    }
}

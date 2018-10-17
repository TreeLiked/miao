package com.example.lqs2.courseapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lqs2.courseapp.R;
import com.example.lqs2.courseapp.entity.Grade;

import java.util.ArrayList;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * 教务系统成绩适配器
 *
 * @author lqs2
 */
public class GradeAdapter extends RecyclerView.Adapter<GradeAdapter.ViewHolder> {

    private Context mContext;
    private List<Grade> mGradeList = new ArrayList<>();
    public static boolean isAllPassed;


    public GradeAdapter(List<Grade> mGradeList, String schoolYear, String team) {
        isAllPassed = true;
        if ("全部".equals(team)) {
            for (int i = 0; i < mGradeList.size(); i++) {
                Grade grade = mGradeList.get(i);
                String text = grade.getCourseGrade();
                try {
                    if (Float.parseFloat(text) < 60) {
                        isAllPassed = false;
                    }
                } catch (NumberFormatException e) {
                    if ("不合格".equals(text)) {
                        isAllPassed = false;
                    }
                }
                if (grade.getSchoolYear().equals(schoolYear)) {
                    this.mGradeList.add(grade);
                }
            }
        } else {
            int which = Integer.parseInt(team);
            for (int i = 0; i < mGradeList.size(); i++) {
                Grade grade = mGradeList.get(i);
                String text = grade.getCourseGrade();
                try {
                    if (Float.parseFloat(text) < 60) {
                        isAllPassed = false;
                    }
                } catch (NumberFormatException e) {
                    if ("不合格".equals(text)) {
                        isAllPassed = false;
                    }
                }
                if (grade.getSchoolYear().equals(schoolYear) && grade.getTeam() == which) {
                    this.mGradeList.add(grade);
                }
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        System.out.println("onCreateView");
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.grade_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(v -> {
            int position = holder.getAdapterPosition();
            Grade grade = mGradeList.get(position);
            final MaterialDialog mMaterialDialog = new MaterialDialog(mContext);
            mMaterialDialog
                    .setTitle(grade.getCourseName())
                    .setMessage("学       年:  " + grade.getSchoolYear()
                            + "\n学       期:  " + grade.getTeam()
                            + "\n代       码:  " + grade.getCourseNo()
                            + "\n课程性质:  " + grade.getCourseNature()
                            + "\n课程归属:  " + grade.getCourseAttr()
                            + "\n学       分:  " + grade.getCourseCredit()
                            + "\n期中成绩:  " + grade.getCourseMiddleGarde()
                            + "\n期末成绩:  " + grade.getCourseFinalGrade()
                            + "\n实验成绩:  " + grade.getCourseExpGrade()
                            + "\n成       绩:  " + grade.getCourseGrade()
                            + "\n补考成绩:  " + grade.getRetestGrade()
                            + "\n是否重修:  " + grade.getIsReconstruct()
                            + "\n开课学院:  " + grade.getCourseBelong()
                            + "\n备       注:  " + grade.getMore()
                            + "\n补考备注:  " + grade.getRetestMore())
                    .setPositiveButton("确认", v1 -> mMaterialDialog.dismiss())
                    .setCanceledOnTouchOutside(true);
            mMaterialDialog.show();

        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        System.out.println("onBIndView");
        Grade grade = mGradeList.get(position);
        holder.gradeName.setText(String.format("课程名称: %s", grade.getCourseName()));
        holder.gradeNo.setText(String.format("课程代码: %s", grade.getCourseNo()));
        holder.gradeCredit.setText(String.format("学  分: %s", grade.getCourseCredit()));
        holder.gradeGrade.setText(String.format("成  绩: %s", grade.getCourseGrade()));

    }

    /**
     * 清楚成绩list
     */
    public void clear() {
        if (mGradeList != null) {
            mGradeList.clear();
            notifyDataSetChanged();
        }
    }


    @Override
    public int getItemCount() {
        return mGradeList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView gradeName;
        TextView gradeNo;
        TextView gradeCredit;
        TextView gradeGrade;

        ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            gradeName = view.findViewById(R.id.grade_name);
            gradeNo = view.findViewById(R.id.grade_no);
            gradeCredit = view.findViewById(R.id.grade_credit);
            gradeGrade = view.findViewById(R.id.grade_grade);
        }
    }
}

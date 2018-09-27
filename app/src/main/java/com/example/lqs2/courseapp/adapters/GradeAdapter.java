package com.example.lqs2.courseapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lqs2.courseapp.entity.Grade;
import com.example.lqs2.courseapp.R;

import java.util.ArrayList;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;

public class GradeAdapter extends RecyclerView.Adapter<GradeAdapter.ViewHolder> {

    private Context mContext;
    private List<Grade> mGradeList = new ArrayList<>();
    public static boolean isAllPassed;


    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView grade_name;
        TextView grade_no;
        TextView grade_credit;
        TextView grade_grade;

        ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            grade_name = view.findViewById(R.id.grade_name);
            grade_no = view.findViewById(R.id.grade_no);
            grade_credit = view.findViewById(R.id.grade_credit);
            grade_grade = view.findViewById(R.id.grade_grade);
        }
    }


    public GradeAdapter(List<Grade> mGradeList, String school_year, String team) {
        isAllPassed = true;
        if (team.equals("全部")) {
            for (int i = 0; i < mGradeList.size(); i++) {
                Grade grade = mGradeList.get(i);
                String text = grade.getCourse_grade();
                try{
                    if (Float.parseFloat(text) < 60) {
                        isAllPassed = false;
                    }
                } catch (NumberFormatException e) {
                    if (text.equals("不合格")){
                        isAllPassed = false;
                    }
                }
                if (grade.getSchool_year().equals(school_year)) {
                    this.mGradeList.add(grade);
                }
            }
        } else {
            int which = Integer.parseInt(team);
            for (int i = 0; i < mGradeList.size(); i++) {
                Grade grade = mGradeList.get(i);
                String text = grade.getCourse_grade();
                try{
                    if (Float.parseFloat(text) < 60) {
                        isAllPassed = false;
                    }
                } catch (NumberFormatException e) {
                    if (text.equals("不合格")){
                        isAllPassed = false;
                    }
                }
                if (grade.getSchool_year().equals(school_year) && grade.getTeam() == which) {
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
                    .setTitle(grade.getCourse_name())
                    .setMessage("学       年:  " + grade.getSchool_year()
                            + "\n学       期:  " + grade.getTeam()
                            + "\n代       码:  " + grade.getCourse_no()
                            + "\n课程性质:  " + grade.getCourse_nature()
                            + "\n课程归属:  " + grade.getCourse_attr()
                            + "\n学       分:  " + grade.getCourse_credit()
//                            + "\n平时成绩:  " + grade.getCourse_usual_grade()
                            + "\n期中成绩:  " + grade.getCourse_middle_garde()
                            + "\n期末成绩:  " + grade.getCourse_final_grade()
                            + "\n实验成绩:  " + grade.getCourse_exp_grade()
                            + "\n成       绩:  " + grade.getCourse_grade()
                            + "\n补考成绩:  " + grade.getRetest_grade()
                            + "\n是否重修:  " + grade.getIsReconstruct()
                            + "\n开课学院:  " + grade.getCourse_belong()
                            + "\n备       注:  " + grade.getMore()
                            + "\n补考备注:  " + grade.getRetest_more())
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
        holder.grade_name.setText(String.format("课程名称: %s", grade.getCourse_name()));
        holder.grade_no.setText(String.format("课程代码: %s", grade.getCourse_no()));
        holder.grade_credit.setText(String.format("学  分: %s", grade.getCourse_credit()));
        holder.grade_grade.setText(String.format("成  绩: %s", grade.getCourse_grade()));

    }

    
    @Override
    public int getItemCount() {
        return mGradeList.size();
    }

    public void clear() {
        if (mGradeList != null) {
            mGradeList.clear();
            notifyDataSetChanged();
        }
    }
}

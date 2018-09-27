package com.example.lqs2.courseapp.entity;

public class Course {
//    星期几:周一到周日
    private int day;
//    第几节课：总共12节
    private int clsNum;
//    每节课的长度
    private int clsCount;
//    随机的颜色
    private int color;
//    课程名
    private String clsName;
//    显示在dialog框中的信息
    private String dialog_name;
    private String dialog_location;
    private String dialog_teacher;
    private String dialog_weeks;
    private String dialog_times;

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getClsNum() {
        return clsNum;
    }

    public void setClsNum(int clsNum) {
        this.clsNum = clsNum;
    }

    public int getClsCount() {
        return clsCount;
    }

    public void setClsCount(int clsCount) {
        this.clsCount = clsCount;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getClsName() {
        return clsName;
    }

    public void setClsName(String clsName) {
        this.clsName = clsName;
    }

    public String getDialog_name() {
        return dialog_name;
    }

    public void setDialog_name(String dialog_name) {
        this.dialog_name = dialog_name;
    }

    public String getDialog_location() {
        return dialog_location;
    }

    public void setDialog_location(String dialog_location) {
        this.dialog_location = dialog_location;
    }

    public String getDialog_teacher() {
        return dialog_teacher;
    }

    public void setDialog_teacher(String dialog_teacher) {
        this.dialog_teacher = dialog_teacher;
    }

    public String getDialog_weeks() {
        return dialog_weeks;
    }

    public void setDialog_weeks(String dialog_weeks) {
        this.dialog_weeks = dialog_weeks;
    }

    public String getDialog_times() {
        return dialog_times;
    }

    public void setDialog_times(String dialog_times) {
        this.dialog_times = dialog_times;
    }


    @Override
    public String toString() {
        return "Course{" +
                "day=" + day +
                ", clsNum=" + clsNum +
                ", clsCount=" + clsCount +
                ", color=" + color +
                ", clsName='" + clsName + '\'' +
                ", dialog_name='" + dialog_name + '\'' +
                ", dialog_location='" + dialog_location + '\'' +
                ", dialog_teacher='" + dialog_teacher + '\'' +
                ", dialog_weeks='" + dialog_weeks + '\'' +
                ", dialog_times='" + dialog_times + '\'' +
                '}';
    }
}

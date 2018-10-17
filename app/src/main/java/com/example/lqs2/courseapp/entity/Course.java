package com.example.lqs2.courseapp.entity;


/**
 * course model
 *
 * @author lqs2
 */
public class Course {


    /**
     * 星期几:周一到周日
     */
    private int day;

    /**
     * 第几节课：总共12节
     */
    private int clsNum;

    /**
     * 每节课的长度
     */
    private int clsCount;

    /**
     * 随机的颜色
     */
    private int color;

    /**
     * 课程名
     */
    private String clsName;

    /**
     * 显示在dialog框中的信息
     */
    private String dialogName;
    private String dialogLocation;
    private String dialogTeacher;
    private String dialogWeeks;
    private String dialogTimes;

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

    public String getDialogName() {
        return dialogName;
    }

    public void setDialogName(String dialogName) {
        this.dialogName = dialogName;
    }

    public String getDialogLocation() {
        return dialogLocation;
    }

    public void setDialogLocation(String dialogLocation) {
        this.dialogLocation = dialogLocation;
    }

    public String getDialogTeacher() {
        return dialogTeacher;
    }

    public void setDialogTeacher(String dialogTeacher) {
        this.dialogTeacher = dialogTeacher;
    }

    public String getDialogWeeks() {
        return dialogWeeks;
    }

    public void setDialogWeeks(String dialogWeeks) {
        this.dialogWeeks = dialogWeeks;
    }

    public String getDialogTimes() {
        return dialogTimes;
    }

    public void setDialogTimes(String dialogTimes) {
        this.dialogTimes = dialogTimes;
    }


    @Override
    public String toString() {
        return "Course{" +
                "day=" + day +
                ", clsNum=" + clsNum +
                ", clsCount=" + clsCount +
                ", color=" + color +
                ", clsName='" + clsName + '\'' +
                ", dialogName='" + dialogName + '\'' +
                ", dialogLocation='" + dialogLocation + '\'' +
                ", dialogTeacher='" + dialogTeacher + '\'' +
                ", dialogWeeks='" + dialogWeeks + '\'' +
                ", dialogTimes='" + dialogTimes + '\'' +
                '}';
    }
}

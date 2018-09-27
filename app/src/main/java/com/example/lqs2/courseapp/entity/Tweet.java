package com.example.lqs2.courseapp.entity;


import java.io.Serializable;

public class Tweet implements Serializable{

    private static final long serialVersionUID = 1L;

    private String id;
    private String userId;
    private String userProfilePicStr;
    private String content;
    private int good;
    private String commentId;
    private String imgPath0;
    private String imgPath1;
    private String imgPath2;
    private String imgPath3;
    private String imgPath4;
    private String imgPath5;
    private String imgPath6;
    private String imgPath7;
    private String imgPath8;
    private String postTime;


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserProfilePicStr() {
        return userProfilePicStr;
    }

    public void setUserProfilePicStr(String userProfilePicStr) {
        this.userProfilePicStr = userProfilePicStr;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getGood() {
        return good;
    }

    public void setGood(int good) {
        this.good = good;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getImgPath0() {
        return imgPath0;
    }

    public void setImgPath0(String imgPath0) {
        this.imgPath0 = imgPath0;
    }

    public String getImgPath1() {
        return imgPath1;
    }

    public void setImgPath1(String imgPath1) {
        this.imgPath1 = imgPath1;
    }

    public String getImgPath2() {
        return imgPath2;
    }

    public void setImgPath2(String imgPath2) {
        this.imgPath2 = imgPath2;
    }

    public String getImgPath3() {
        return imgPath3;
    }

    public void setImgPath3(String imgPath3) {
        this.imgPath3 = imgPath3;
    }

    public String getImgPath4() {
        return imgPath4;
    }

    public void setImgPath4(String imgPath4) {
        this.imgPath4 = imgPath4;
    }

    public String getImgPath5() {
        return imgPath5;
    }

    public void setImgPath5(String imgPath5) {
        this.imgPath5 = imgPath5;
    }

    public String getImgPath6() {
        return imgPath6;
    }

    public void setImgPath6(String imgPath6) {
        this.imgPath6 = imgPath6;
    }

    public String getImgPath7() {
        return imgPath7;
    }

    public void setImgPath7(String imgPath7) {
        this.imgPath7 = imgPath7;
    }

    public String getImgPath8() {
        return imgPath8;
    }

    public void setImgPath8(String imgPath8) {
        this.imgPath8 = imgPath8;
    }

    public String getPostTime() {
        return postTime;
    }

    public void setPostTime(String postTime) {
        this.postTime = postTime;
    }

    @Override
    public String toString() {
        return "Tweet{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", userProfilePicStr='" + userProfilePicStr + '\'' +
                ", content='" + content + '\'' +
                ", good=" + good +
                ", commentId='" + commentId + '\'' +
                ", imgPath0='" + imgPath0 + '\'' +
                ", imgPath1='" + imgPath1 + '\'' +
                ", imgPath2='" + imgPath2 + '\'' +
                ", imgPath3='" + imgPath3 + '\'' +
                ", imgPath4='" + imgPath4 + '\'' +
                ", imgPath5='" + imgPath5 + '\'' +
                ", imgPath6='" + imgPath6 + '\'' +
                ", imgPath7='" + imgPath7 + '\'' +
                ", imgPath8='" + imgPath8 + '\'' +
                ", postTime='" + postTime + '\'' +
                '}';
    }
}

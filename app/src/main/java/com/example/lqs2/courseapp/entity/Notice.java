package com.example.lqs2.courseapp.entity;

import java.io.Serializable;

public class Notice implements Serializable {

    private static final long serialVersionUID = 2L;


    private String title;
    private String time;

    private String contentUrl;
    private String content;

    private String annexUrl;
    private String annexText;

    public Notice() {
    }


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Notice(String title, String time, String contentUrl) {
        this.title = title;
        this.time = time;
        this.contentUrl = contentUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAnnexUrl() {
        return annexUrl;
    }

    public void setAnnexUrl(String annexUrl) {
        this.annexUrl = annexUrl;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }

    public void setAnnexText(String annexText) {
        this.annexText = annexText;
    }

    public String getAnnexText() {
        return annexText;
    }

    @Override
    public String toString() {
        return "Notice{" +
                "title='" + title + '\'' +
                ", time='" + time + '\'' +
                ", contentUrl='" + contentUrl + '\'' +
                ", content='" + content + '\'' +
                ", annexUrl='" + annexUrl + '\'' +
                ", annexText='" + annexText + '\'' +
                '}';
    }
}

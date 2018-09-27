package com.example.lqs2.courseapp.entity;


public class CardItem {

    private int id;
    private String title;
    private String text;
    private String state;
    private String type;
    private String createTime;

    public CardItem(int id, String title, String text, String state, String type, String createTime) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.state = state;
        this.type = type;
        this.createTime = createTime;
    }


    public String getState() {
        return state;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }


    public String getCreateTime() {
        return createTime;
    }

}
